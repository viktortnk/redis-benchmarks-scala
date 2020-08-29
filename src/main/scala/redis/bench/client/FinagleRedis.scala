package redis.bench.client

import cats.FlatMap
import cats.effect.{Async, Resource, Sync}
import cats.syntax.flatMap._
import com.twitter.finagle.Redis
import com.twitter.finagle.redis.Client
import com.twitter.finagle.stats.NullStatsReceiver
import com.twitter.finagle.tracing.NullTracer
import com.twitter.io.Buf
import redis.bench.util.AsyncUtils.toF

object FinagleRedis {

  private class FinagleRedisClient[F[_]: Async](redis: Client) extends RedisClient[F] {

    override def set(key: String, value: String): F[Unit] = {
      toF(redis.set(Buf.Utf8(key), Buf.Utf8(value)))
    }
  }

  def simpleClient[F[_]: Async: FlatMap](): Resource[F, RedisClient[F]] = {
    Resource
      .make {
        Sync[F]
          .delay {
            Redis.client
              .withStatsReceiver(NullStatsReceiver)
              .withTracer(NullTracer)
              .newRichClient("localhost:6379")
          }
          .flatTap(c => toF(c.ping()))
      }(c => toF(c.close()))
      .map(new FinagleRedisClient[F](_))
  }
}
