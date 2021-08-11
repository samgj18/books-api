package domain

import derevo.cats._
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe._
import io.circe.refined._
import io.estatico.newtype.macros.newtype

object Book {

  @derive(decoder, encoder, eqv, show)
  @newtype case class Author(value: String)

  @derive(decoder, encoder, eqv, show)
  @newtype case class Name(value: String)

  @derive(decoder, encoder, eqv, show)
  @newtype case class Year(value: Int)

  /** Parameters */

  @derive(decoder, encoder)
  @newtype
  case class AuthorParam(value: NonEmptyString) {
    def toDomain: Author = Author(value.toLowerCase)
  }

  @derive(decoder, encoder)
  @newtype
  case class YearParam(value: Int)              {
    def toDomain: Year = Year(value.intValue())
  }

  @derive(decoder, encoder)
  @newtype
  case class NameParam(value: NonEmptyString)   {
    def toDomain: Name = Name(value.toLowerCase)
  }

  @derive(decoder, encoder)
  case class Book(id: Int, name: String, author: String, year: Int)
}
