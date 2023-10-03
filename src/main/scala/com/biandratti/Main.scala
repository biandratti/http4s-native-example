package com.biandratti

import cats.effect.{IO, IOApp}
import com.biandratti.server.Http4sServer

object Main extends IOApp.Simple:
  val run = Http4sServer.run[IO]
