package infrastucture

import cats.effect._
import fs2.Stream
import infrastucture.Configuration._
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig.module.catseffect.loadConfigF
import pureconfig.module.catseffect.syntax._

object Configuration {
  def loadConfiguration[F[_]: Async]: Stream[F, Configuration] =
    Stream.eval(ConfigSource.default.loadF[F, Configuration])

  def resource[F[_]: Sync]: Resource[F, Configuration] =
    Resource.eval(loadConfigF[F, Configuration])

  case class DbConfig(
      url: String,
      username: String,
      password: String,
      threadPoolSize: Int
  )

  case class KafkaConfig(
      bootstrapServers: String,
      topic: String
  )

  case class HttpConfig(
      port: Int
  )
}

case class Configuration(
    http: HttpConfig,
    postgres: DbConfig,
    kafka: KafkaConfig
)
