package redis.bench.client

trait RedisClient[F[_]] {

  def set(key: String, value: String): F[Unit]
}
