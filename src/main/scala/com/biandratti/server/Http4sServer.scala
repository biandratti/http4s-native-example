package com.biandratti.server

import cats.effect.*
import cats.effect.std.Console
import cats.syntax.all.*
import com.biandratti.service.HelloWorld
import com.comcast.ip4s.*
import epollcat.EpollApp
import fs2.io.net.Network
import fs2.io.net.tls.{S2nConfig, TLSContext}
import io.circe.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.client.Client
import org.http4s.dsl.request.*
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger
import org.http4s.syntax.all.*
object Http4sServer {

  def run[F[_] : Async]: F[ExitCode] = customTLS
    .flatMap(createClient)
    .use { client =>
      createServer(client).useForever
    }
    .as(ExitCode.Success)

  private def customTLS[F[_] : Async] =
    S2nConfig.builder
      .withCipherPreferences("default_tls13")
      .build[F]
      .map(Network[F].tlsContext.fromS2nConfig(_))

  private def createClient[F[_] : Async](
                                          tlsContext: TLSContext[F]
                                        ): Resource[F, Client[F]] = {
    EmberClientBuilder
      .default[F]
      .withTLSContext(tlsContext)
      .withHttp2
      .build
  }

  private def app[F[_] : Async](client: Client[F]) = HttpRoutes.of[F] {
    case GET -> Root =>
      Response[F](Status.Ok).withEntity("Hey There!").pure[F]
    case GET -> Root / "hello" / person =>
      Response[F](Status.Ok).withEntity(s"Hello, $person").pure[F]
  }

  private def createServer[F[_] : Async](client: Client[F]): Resource[F, Unit] =
    EmberServerBuilder
      .default[F]
      .withHttp2
      .withHttpApp(app(client).orNotFound)
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .build
      .void

}