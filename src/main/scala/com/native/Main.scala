package com.native

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple:
  val run = MyServer.run[IO]
