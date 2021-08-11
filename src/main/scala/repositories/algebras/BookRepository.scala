package repositories.algebras

import domain.Book._

trait BookRepository[F[_]] {
  def createBook(name: Name, author: Author, year: Year): F[Book]
  def updateBook(id: String, book: Book): F[Option[Book]]
  def removeBook(id: String): F[Boolean]
  def getBook(id: String): F[Option[Book]]
  def retrieveAll(): F[List[Book]]
}
