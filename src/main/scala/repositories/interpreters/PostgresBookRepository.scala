package repositories.interpreters

import cats.effect.Sync
import domain.Book._
import doobie.implicits._
import doobie.implicits.legacy.instant._
import doobie.util.Get
import doobie.{Put, Query0, Transactor}
import repositories.algebras.BookRepository

import java.time.{Instant, ZoneId, ZonedDateTime}

object PostgresBookRepository {
  def make[F[_]: Sync](xa: Transactor[F]): BookRepository[F] =
    new PostgresBookRepository[F](xa)
}

class PostgresBookRepository[F[_]: Sync](private val xa: Transactor[F]) extends BookRepository[F] {
  val UTC: ZoneId                         = ZoneId.of("Z")
  implicit val zdtGet: Get[ZonedDateTime] = Get[Instant].map(i => ZonedDateTime.ofInstant(i, UTC))
  implicit val zdtPut: Put[ZonedDateTime] =
    Put[Instant].contramap(_.withZoneSameInstant(UTC).toInstant)

  def createBook(name: Name, author: Author, year: Year): F[Book] = {
    val insert = sql"""
    INSERT INTO books (name, author, year)
    VALUES (${name.value}, ${author.value}, ${year.value})
    """.update.withUniqueGeneratedKeys[Int]("book_id")

    val program = for {
      id   <- insert
      item <- findQ(id).unique
    } yield item
    program.transact(xa)
  }

  def retrieveAll(): F[List[Book]]                                =
    sql"""
    SELECT * FROM books"""
      .query[Book]
      .to[List]
      .transact(xa)

  def updateBook(id: String, book: Book): F[Option[Book]] = ???

  def removeBook(id: String): F[Boolean] = ???

  def getBook(id: String): F[Option[Book]] = ???

  private def findQ(id: Int): Query0[Book] =
    sql"""
    SELECT *
    FROM books
    WHERE book_id = $id
    """
      .query[Book]

}