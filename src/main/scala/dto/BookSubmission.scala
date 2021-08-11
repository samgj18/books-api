package dto

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import domain.Book.{AuthorParam, NameParam, YearParam}

@derive(decoder, encoder)
case class BookSubmission(name: NameParam, author: AuthorParam, year: YearParam)
