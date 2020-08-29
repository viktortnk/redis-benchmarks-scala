package redis.bench.main

import cats.effect.{ExitCode, IO, IOApp}
import redis.bench.client.{FinagleRedis, JedisRedis, LettuceRedis}

object MainCE extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val redisR = LettuceRedis.simpleClient[IO]()
    Benchmark.run[IO](redisR, 300_000, iterations = 2).as(ExitCode.Success)
  }

}
