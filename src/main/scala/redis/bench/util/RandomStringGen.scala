package redis.bench.util

import java.util.Random

import org.apache.commons.lang3.RandomStringUtils

object RandomStringGen {

  def create[F[_]](seed: Int): fs2.Stream[F, String] =
    fs2.Stream
      .emit(new Random(seed))
      .repeat
      .map(rnd => RandomStringUtils.random(10, 0, 0, true, true, null, rnd))
}
