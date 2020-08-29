package redis.bench.main

import cats.effect.ExitCode
import monix.eval._
import redis.bench.client.{FinagleRedis, LettuceRedis}

object MainMonix extends TaskApp {

  override def run(args: List[String]): Task[ExitCode] = {
    val redisR = LettuceRedis.simpleClient[Task]()
    Benchmark.run[Task](redisR, number = 300_000, iterations = 2).map(_ => ExitCode.Success)
  }
}
