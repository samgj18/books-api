package repositories.algebras

import domain.Book._
import fs2.Stream
import java.time.LocalDateTime

trait BookRepository[F[_]] {
  def createBook(name: Name, author: Author, year: Year): F[Book]
  def updateBook(id: Int, name: Name, author: Author, year: Year): F[Book]
  def removeBook(id: Int): F[Boolean]
  def getBook(id: Int): F[Option[Book]]
  def retrieveAll(): F[List[Book]]
  def retrieveByDate(date: LocalDateTime): Stream[F, Book]
}
