import cats.effect._
import cats.syntax.all._
import domain.Book._
import fs2.Stream
import fs2.kafka._
import infrastucture.Configuration.KafkaConfig
import infrastucture._
import infrastucture.http.BookRoutes
import infrastucture.kafka.Kafka._
import infrastucture.kafka.StreamToKafka
import infrastucture.kafka._
import io.circe.Encoder
import org.http4s._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import repositories.algebras.TrackerRepository._
import repositories.algebras._
import repositories.interpreters._

import scala.concurrent.ExecutionContext.global
import scala.concurrent.ExecutionContext

object Main extends IOApp {

  def makeHttpServer[F[_]: Async](routes: HttpRoutes[F], context: ExecutionContext) =
    BlazeServerBuilder[F](context)
      .bindHttp(8080, "localhost")
      .withHttpApp(routes.orNotFound)
      .withExecutionContext(global)
      .serve

  def makeKafkaProgram[F[_]: Async](
      kafkaProducer: KafkaProducer[F, String, Book],
      bookRepository: BookRepository[F],
      progressTracker: TrackerRepository[F],
      config: ProducerSettings[F, String, Book],
      topic: String
  ): Stream[F, Book] =
    StreamToKafka
      .program[F](
        kafkaProducer,
        bookRepository,
        progressTracker,
        config,
        topic
      )

  override def run(args: List[String]): IO[ExitCode] = {
    val program = for {
      config                   <- Stream.resource(Configuration.resource[IO])
      transactor               <- Stream.resource(Postgres.transactor[IO](config.postgres))
      producer                 <- Kafka.make[IO, String, Book](config.kafka)
      producerSettings          = Kafka.producerSettings[IO, String, Book](config.kafka)
      bookRepository            = PostgresBookRepository.make[IO](transactor)
      postgresTrackerRepository = PostgresTrackerRepository.make[IO](transactor)
      routes                    = BookRoutes.make(bookRepository)
      kafkaStream               = makeKafkaProgram[IO](
                      producer,
                      bookRepository,
                      postgresTrackerRepository,
                      producerSettings,
                      config.kafka.topic
                    )
      httpStream                = makeHttpServer[IO](routes, global)
      _                        <- kafkaStream concurrently httpStream

    } yield ()

    program.compile.drain
      .as(ExitCode.Success)

  }
}
