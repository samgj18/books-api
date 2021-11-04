package repositories.algebras

import java.time.LocalDateTime

import repositories.algebras.TrackerRepository._

object TrackerRepository      {
  sealed trait Application
  object Application {
    case object Book extends Application

    def appName(a: Application): String =
      a match {
        case Book => "books"
      }
  }
}

trait TrackerRepository[F[_]] {
  def resume(a: Application): F[LocalDateTime]

  def saveProgress(a: Application, s: LocalDateTime): F[Unit]
}
