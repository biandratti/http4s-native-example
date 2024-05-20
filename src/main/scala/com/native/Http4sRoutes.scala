package com.native

import cats.effect.Sync
import cats.implicits.*
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object Http4sRoutes:

  def helloWorldRoutes[F[_]: Sync](H: HelloWorld[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F] { case GET -> Root / "hello" / name =>
      for {
        greeting <- H.hello(HelloWorld.Name(name))
        resp <- Ok(greeting)
      } yield resp
    }
