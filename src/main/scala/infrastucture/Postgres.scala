package infrastucture

import cats.effect._
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import fs2._
import infrastucture.Configuration.DbConfig
import org.flywaydb.core.Flyway

object Postgres {
  def runMigrations[F[_]: Sync](dbConfig: DbConfig): Stream[F, Int] =
    Stream.eval {
      Sync[F].delay {
        Flyway
          .configure()
          .locations(s"classpath:sql")
          .dataSource(dbConfig.url, dbConfig.username, dbConfig.password)
          .load()
          .migrate()
      }
    }

  def transactor[F[_]: Async](
      dbConfig: DbConfig
  ): Resource[F, HikariTransactor[F]] =
    for {
      connectEc  <- ExecutionContexts.fixedThreadPool[F](dbConfig.threadPoolSize)
      transactor <- HikariTransactor.newHikariTransactor[F](
                      driverClassName = "org.postgresql.Driver",
                      url = dbConfig.url,
                      user = dbConfig.username,
                      pass = dbConfig.password,
                      connectEc
                    )
      _          <- Resource.eval(transactor.configure(ds => Async[F].delay(ds.setAutoCommit(false))))
    } yield transactor
}
