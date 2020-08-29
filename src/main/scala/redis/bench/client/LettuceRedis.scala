package redis.bench.client

import cats.FlatMap
import cats.effect.{Async, Resource, Sync}
import cats.syntax.functor._
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.resource.ClientResources
import io.lettuce.core.tracing.Tracing
import io.lettuce.core.{RedisClient => LettuceClient}
import redis.bench.util.AsyncUtils.csToF

object LettuceRedis {

  private class LettuceRedisClient[F[_]: Async](redis: RedisAsyncCommands[String, String]) extends RedisClient[F] {
    override def set(key: String, value: String): F[Unit] = {
      csToF(redis.set(key, value)).void
    }
  }

  def simpleClient[F[_]: Async: FlatMap](): Resource[F, RedisClient[F]] = {
    Resource
      .make(
        Sync[F].delay(
          LettuceClient
            .create(ClientResources.builder().tracing(Tracing.disabled()).build(), "redis://localhost")
            .connect()
        )
      )(conn => csToF(conn.closeAsync()).void)
      .map(conn => new LettuceRedisClient(conn.async()))
  }
}
