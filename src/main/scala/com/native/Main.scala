package com.native.quickstart2

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple:
  val run = Http4sServer.run[IO]
