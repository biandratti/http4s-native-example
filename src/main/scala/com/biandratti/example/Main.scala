package com.biandratti.example

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple:
  val run = Http4sexampleServer.run[IO]
