package com.native

import cats.effect._
import cats.effect.Async
import cats.syntax.all._
import com.comcast.ip4s._
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import fs2.io.net.Network
import fs2.io.net.tls.{S2nConfig, TLSContext}
import org.http4s.client.Client

object Http4sServer:

  private def customTLS[F[_]: Async] =
    S2nConfig.builder
      .withCipherPreferences("default_tls13")
      .build[F]
      .map(Network[F].tlsContext.fromS2nConfig(_))

  private def createClient[F[_]: Async](
      tlsContext: TLSContext[F]
  ): Resource[F, Client[F]] = {
    EmberClientBuilder
      .default[F]
      .withTLSContext(tlsContext)
      .withHttp2
      .build
  }
  def run[F[_]: Async]: F[Nothing] = {
    for {
      client <- customTLS.flatMap(createClient)
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
  }.useForever
