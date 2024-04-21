package com.native

import cats.Applicative
import cats.implicits.*
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe.*

trait HelloWorld[F[_]]:
  def hello(n: HelloWorld.Name): F[HelloWorld.Greeting]

object HelloWorld:
  final case class Name(name: String) extends AnyVal
  /**
    * More generally you will want to decouple your edge representations from
    * your internal data structures, however this shows how you can
    * create encoders for your data.
    **/
  final case class Greeting(greeting: String) extends AnyVal
  object Greeting:
    given Encoder[Greeting] = (a: Greeting) => Json.obj(
      ("message", Json.fromString(a.greeting)),
    )

    given [F[_]]: EntityEncoder[F, Greeting] =
      jsonEncoderOf[Greeting]

  def impl[F[_]: Applicative]: HelloWorld[F] = (n: HelloWorld.Name) => Greeting("Hello, " + n.name).pure[F]
