package com.native

import cats.effect.*
import cats.syntax.all.*
import com.comcast.ip4s.*
import fs2.io.net.Network
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger

object Http4sServer:

  def run[F[_]: Async: Network]: Resource[F, Unit] = {
    for {
      tlsContext <- TLSClient.customTLS
      client <- TLSClient.build(tlsContext)
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)
      httpApp = (
        Http4sRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
          Http4sRoutes.jokeRoutes[F](jokeAlg)
      ).orNotFound
      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      _ <-
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }
