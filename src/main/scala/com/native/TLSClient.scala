package com.native

import cats.effect._
import cats.effect.Async
import org.http4s.ember.client.EmberClientBuilder
import fs2.io.net.Network
import fs2.io.net.tls.{S2nConfig, TLSContext}
import org.http4s.client.Client

object TLSClient:

  def customTLS[F[_]: Async: Network]: Resource[F, TLSContext[F]] =
    S2nConfig.builder
      .withCipherPreferences("default_tls13")
      .build[F]
      .map(Network[F].tlsContext.fromS2nConfig(_))

  def build[F[_]: Async: Network](
      tlsContext: TLSContext[F]
  ): Resource[F, Client[F]] = {
    EmberClientBuilder
      .default[F]
      .withTLSContext(tlsContext)
      .withHttp2
      .build
  }
