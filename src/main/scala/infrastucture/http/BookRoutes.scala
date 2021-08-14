package infrastucture.http

import cats.{Monad, MonadThrow}
import dto.BookSubmission
import infrastucture.utils.TypeRefinedOps.RefinedRequestDecoder
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import repositories.algebras.BookRepository
import cats.implicits._

object BookRoutes        {
  def make[F[_]: Monad: JsonDecoder: MonadThrow](
      bookRepository: BookRepository[F]
  ): HttpRoutes[F] = {
    new BookRoutes[F](bookRepository).routes
  }
}

final case class BookRoutes[F[_]: Monad: JsonDecoder: MonadThrow](
    books: BookRepository[F]
) extends Http4sDsl[F] {

  private[infrastucture] val prefixPath = "/books"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      Ok(books.retrieveAll())

    case _ @GET -> Root / IntVar(id)    =>
      books.getBook(id).flatMap {
        case Some(book) => Ok(book)
        case None       => NotFound()
      }

    case req @ POST -> Root             =>
      req.decodeR[BookSubmission] { book =>
        books
          .createBook(
            book.name.toDomain,
            book.author.toDomain,
            book.year.toDomain
          )
          .flatMap(Created(_))
          .recoverWith {
            case e => Conflict(e.getMessage)
          }
      }

    case req @ PUT -> Root / IntVar(id) =>
      req
        .decodeR[BookSubmission] { book =>
          books.updateBook(id, book.name.toDomain, book.author.toDomain, book.year.toDomain) >> Ok()
        }
        .recoverWith {
          case e => NotFound(e.getMessage)
        }

    case _ @DELETE -> Root / IntVar(id) =>
      books.removeBook(id) >> NoContent()
  }

  val routes: HttpRoutes[F]             = Router(
    prefixPath -> httpRoutes
  )

}
