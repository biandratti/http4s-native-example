import sbt._

object Dependencies {

  lazy val logbackDependencies: Seq[ModuleID] = {
    Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.12"
    )
  }

  lazy val http4sDependencies: Seq[ModuleID] = {
    val Http4sVersion = "1.0.0-M29"
    Seq(
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-ember-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion
    )
  }

  lazy val munitDependencies: Seq[ModuleID] = {
    Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test
    )
  }

  lazy val kafkaDependencies: Seq[ModuleID] = {
    Seq(
      "com.github.fd4s" %% "fs2-kafka" % "3.1.0"
    )
  }

}
