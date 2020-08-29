package redis.bench.main

import cats.effect.{Clock, Concurrent, Resource, Sync}
import cats.instances.list._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.traverse._
import redis.bench.client.RedisClient
import redis.bench.util.RandomStringGen

import scala.concurrent.duration.NANOSECONDS

object Benchmark {

  def measure[F[_], A](fa: F[A])(implicit F: Sync[F], clock: Clock[F]): F[A] =
    (for {
      start  <- clock.monotonic(NANOSECONDS)
      result <- fa
      finish <- clock.monotonic(NANOSECONDS)
    } yield (result, finish - start)).flatMap {
      case (result, time) =>
        F.delay(println(s"time taken: ${NANOSECONDS.toMillis(time) / 1000.0}s")).as(result)
    }

  def run[F[_]: Concurrent: Clock](redisR: Resource[F, RedisClient[F]], number: Int, iterations: Int = 2): F[Unit] = {
    def evalStream(r: RedisClient[F], seed: Int): F[Unit] =
      RandomStringGen
        .create[F](seed)
        .take(number)
        .mapAsyncUnordered(16)(str => r.set(str, str))
        //        .take(10)
        //        .evalTap(s => IO(println(s)))
        .compile
        .drain

    redisR.use { redis =>
      (1 to iterations).toList.traverse(v => measure(evalStream(redis, v))).void
    }
  }
}
