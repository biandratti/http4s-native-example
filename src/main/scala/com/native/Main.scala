package com.native

import cats.effect.{IO, IOApp}
import org.typelevel.log4cats.*
import org.typelevel.log4cats.slf4j.Slf4jFactory


object Main extends IOApp.Simple:
  val run = {
    implicit val logging: LoggerFactory[IO] = Slf4jFactory.create[IO]
    MyServer.run[IO]
  }
