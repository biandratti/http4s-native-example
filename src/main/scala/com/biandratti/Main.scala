package com.biandratti

import cats.effect.{IO, IOApp}
import com.biandratti.server.Http4sServer
import epollcat.EpollApp

object Main  extends EpollApp:
  override def run(args: List[String]) = Http4sServer.run[IO]
