import cats.effect._
import infrastucture.http.BookRoutes
import infrastucture.{Configuration, Postgres}
import org.http4s.HttpApp
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import repositories.interpreters.PostgresBookRepository

import scala.concurrent.ExecutionContext.global

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val program: Resource[IO, HttpApp[IO]] = for {
      config        <- Configuration.resource[IO]
      transactor    <- Postgres.transactor[IO](config.postgres)
      _             <- Resource.eval(Postgres.runMigrations[IO](config.postgres).compile.drain)
      bookRepository = PostgresBookRepository.make[IO](transactor)
      routes         = BookRoutes.make(bookRepository)
    } yield routes.orNotFound

    program.use(app =>
      BlazeServerBuilder[IO](global)
        .withHttpApp(app)
        .bindHttp(8080, "localhost")
        .serve
        .compile
        .lastOrError
    )
  }
}
