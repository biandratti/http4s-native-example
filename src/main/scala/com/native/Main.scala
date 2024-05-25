package com.native

import cats.effect.{ExitCode, IO}
import epollcat.EpollApp

object Main extends EpollApp:
  def run(args: List[String]): IO[ExitCode] =
    Http4sServer.run[IO].useForever.as(ExitCode.Success)
