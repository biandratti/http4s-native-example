package com.native

import cats.data.Kleisli
import cats.effect.Async
import cats.effect.kernel.Resource
import cats.syntax.all.*
import com.comcast.ip4s.*
import fs2.io.net.Network
import fs2.io.net.tls.{S2nConfig, TLSContext}
import org.http4s.{HttpApp, Request, Response}
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object MyServer:

  given loggerFactory[F[_]: Async]: LoggerFactory[F] = Slf4jFactory.create[F]
  def run[F[_]: Async: Network]: F[Nothing] = {
    for {
      //client <- EmberClientBuilder.default[F].build
      client <- customTLS.flatMap(createClient)
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)

      httpApp: Kleisli[F, Request[F], Response[F]] = (
        MyRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
        MyRoutes.jokeRoutes[F](jokeAlg)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp: HttpApp[F] = Logger.httpApp(true, true)(httpApp)

      _ <- 
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }.useForever

  private def customTLS[F[_] : Async: Network]: Resource[F, TLSContext[F]] = {
    S2nConfig.builder
      .withCipherPreferences("default_tls13")
      .build[F]
      .map(Network[F].tlsContext.fromS2nConfig(_))
  }

  private def createClient[F[_] : Async: Network](
                                          tlsContext: TLSContext[F]
                                        ): Resource[F, Client[F]] = {
    EmberClientBuilder
      .default[F]
      .withTLSContext(tlsContext)
      .withHttp2
      .build
  }
