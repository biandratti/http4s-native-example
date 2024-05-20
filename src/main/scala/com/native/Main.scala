package com.native

import cats.effect.{IO, IOApp}
import epollcat.EpollApp

object Main extends EpollApp {

  override def run(args: List[String]) = Http4sServer.run[IO]
}
