package infrastucture.kafka

import cats.effect._
import cats.implicits._
import fs2.kafka._
import fs2.{Pipe, Stream}
import io.circe.Encoder
import io.circe.syntax._
import infrastucture.Configuration.KafkaConfig
import fs2.kafka._
import scala.concurrent.duration._
import java.nio.charset.StandardCharsets.UTF_8

object Kafka {
  def producerPipe[F[_]: Async, K: Encoder, V: Encoder](key: V => K)(
      kafkaProducer: KafkaProducer[F, K, V],
      config: ProducerSettings[F, K, V],
      topic: String
  ): Pipe[F, V, V] =
    in =>
      in.map(v => ProducerRecords.one(ProducerRecord(topic, key(v), v), v))
        .through(
          KafkaProducer.pipe(config, kafkaProducer)
        )
        .map(_.passthrough)

  // TODO: Implement producerSettings and this method will automatically work
  def make[F[_]: Async, K: Encoder, V: Encoder](
      config: KafkaConfig
  ): Stream[F, KafkaProducer[F, K, V]] =
    KafkaProducer.stream(producerSettings(config))

  // TODO: Use your KafkaConfig to obtain the broker URL and use the simplest possible configuration to connect to Kafka
  def producerSettings[F[_], K, V](
      config: KafkaConfig
  )(implicit
      F: Sync[F],
      keySerializer: Serializer[F, K],
      valueSerializer: Serializer[F, V]
  ): ProducerSettings[F, K, V] = {
    ProducerSettings[F, K, V](keySerializer, valueSerializer)
      .withAcks(Acks.All)
      .withBootstrapServers(config.bootstrapServers)
  }

  // NOTE: This allows you to implement a Circe Encoder typeclass and use automatic derivation to get a Kafka Serializer
  implicit def circeEncoderDerivesKafkaSerializer[F[_]: Sync, A: Encoder]: Serializer[F, A] =
    Serializer.instance[F, A]((topic, headers, a) => a.asJson.noSpaces.getBytes(UTF_8).pure[F])
}
