package redis.bench.client

import java.util.concurrent.Executors

import cats.effect.{ContextShift, Resource, Sync}
import cats.syntax.functor._
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.{Jedis, JedisPool}

import scala.concurrent.ExecutionContext

object JedisRedis {

  private class JedisClient[F[_]: Sync: ContextShift](jedisPool: JedisPool) extends RedisClient[F] {

    private val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(40))

    override def set(key: String, value: String): F[Unit] =
      withJedis { redis =>
        Sync[F].delay(redis.set(key, value)).void
      }

    def withJedis[T](f: Jedis => F[T]): F[T] = {
      ContextShift[F].evalOn(ec) {
        Resource.fromAutoCloseable(Sync[F].delay(jedisPool.getResource)).use(jedis => Sync[F].suspend(f(jedis)))
      }
    }
  }

  def simpleClient[F[_]: Sync: ContextShift](): Resource[F, RedisClient[F]] = {
    val conf = new GenericObjectPoolConfig()
    conf.setMaxTotal(32)
    Resource
      .fromAutoCloseable(Sync[F].delay(new JedisPool(conf, "localhost", 6379)))
      .map(new JedisClient(_))
  }
}
