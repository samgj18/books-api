package infrastucture.kafka

import cats.effect._
import fs2.Stream
import domain.Book._
import repositories.algebras.BookRepository
import fs2.kafka.KafkaProducer
import io.circe.Encoder
import repositories.algebras.TrackerRepository
import cats.syntax.all._
import repositories.algebras.TrackerRepository._
import infrastucture.Configuration.KafkaConfig
import fs2.kafka.ProducerSettings

object StreamToKafka {
  // TODO: Stream data from the database to Kafka and make sure to track progress so you can resume from where you left off
  // Hint: You will need to feed in at least the repository, tracker and kafka producer as function arguments and you
  // will need to make use of producerPipe
  // Hint: Instantiate the Kafka Producer in the main application so it will only be created and reused once
  def program[F[_]: Async](
      kafkaProducer: KafkaProducer[F, String, Book],
      bookRepository: BookRepository[F],
      progressTracker: TrackerRepository[F],
      config: ProducerSettings[F, String, Book],
      topic: String
  ): Stream[F, Book] =
    Stream
      .eval(progressTracker.resume(Application.Book))
      .flatMap(resumeDate => {
        val kafkaPipe =
          Kafka.producerPipe[F, String, Book](book => book.author)(kafkaProducer, config, topic)

        val stream = bookRepository.retrieveByDate(resumeDate)

        stream
          .through(kafkaPipe)
          .evalTap(book => progressTracker.saveProgress(Application.Book, book.updatedAt))
      })
      .repeat
}
