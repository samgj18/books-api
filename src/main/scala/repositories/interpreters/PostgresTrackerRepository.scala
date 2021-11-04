package repositories.interpreters

import repositories.algebras.TrackerRepository._
import repositories.algebras.TrackerRepository
import java.time.LocalDateTime
import cats.effect._
import cats.implicits._
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.util.transactor.Transactor
import java.time.ZoneOffset

object PostgresTrackerRepository {
  def make[F[_]: MonadCancelThrow](xa: Transactor[F]): TrackerRepository[F] =
    new PostgresTrackerRepository[F](xa)
}

class PostgresTrackerRepository[F[_]: MonadCancelThrow](xa: Transactor[F])
    extends TrackerRepository[F] {

  override def resume(a: Application): F[LocalDateTime] =
    sql"""
    SELECT last_pushed_record 
    FROM progress_tracker 
    WHERE table_name = ${Application.appName(a)}
    """
      .query[LocalDateTime]
      .option
      .transact(xa)
      .map(_.fold(LocalDateTime.ofEpochSecond(0L, 0, ZoneOffset.UTC))(identity))

  override def saveProgress(a: Application, s: LocalDateTime): F[Unit] =
    sql"""
    INSERT INTO progress_tracker AS existing (table_name, last_pushed_record)
    VALUES (${Application.appName(a)}, $s)
    ON CONFLICT (table_name)
    DO UPDATE SET last_pushed_record = excluded.last_pushed_record
    WHERE excluded.last_pushed_record > existing.last_pushed_record
    """.update.run
      .transact(xa)
      .void

}
