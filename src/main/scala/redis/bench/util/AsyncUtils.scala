package redis.bench.util

import java.util.concurrent.CompletionStage

import cats.effect.Async
import com.twitter.util.{Future, Return, Throw}

object AsyncUtils {

  def toF[F[_]: Async, A](future: => Future[A]): F[A] =
    Async[F].async[A] { cb =>
      future.respond {
        case Return(v) => cb(Right(v))
        case Throw(e)  => cb(Left(e))
      }
    }

  def csToF[F[_]: Async, A](future: => CompletionStage[A]): F[A] = {
    Async[F].async[A] { cb =>
      future.whenComplete((value, t) => {
        if (t == null) {
          cb(Right(value))
        } else {
          cb(Left(t))
        }
      })
      ()
    }
  }
}
