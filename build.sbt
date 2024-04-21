import scala.collection.mutable.ListBuffer

Global / dependencyCheckFormats := Seq("HTML", "JSON")

val Http4sVersion = "1.0.0-M41"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.5.6"
val MunitCatsEffectVersion = "1.0.6"
val EpollcatVersion = "0.1.6"

val isLinux = Option(System.getProperty("os.name"))
  .exists(_.toLowerCase().contains("linux"))
val isMacOs =
  Option(System.getProperty("os.name")).exists(_.toLowerCase().contains("mac"))
val isArm = Option(System.getProperty("os.arch"))
  .exists(_.toLowerCase().contains("aarch64"))
val s2nLibPath = sys.env.get("S2N_LIBRARY_PATH")

lazy val root = (project in file("."))
  .enablePlugins(ScalaNativePlugin)
  .settings(
    organization := "com.native",
    name := "Scala Native Ember Example",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.4.1",
    libraryDependencies ++= Seq(
      "com.armanbilge" %%% "epollcat" % EpollcatVersion, // Runtime
      "org.http4s" %%% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %%% "http4s-ember-client" % Http4sVersion,
      "org.http4s" %%% "http4s-circe" % Http4sVersion,
      "org.http4s" %%% "http4s-dsl" % Http4sVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "org.typelevel" %% "log4cats-slf4j" % "2.6.0",
      "org.scalameta" %% "munit" % MunitVersion % Test,
      "org.typelevel" %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    nativeConfig ~= { c =>
      val linkOpts = ListBuffer.empty[String]
      linkOpts.append("-L/home/linuxbrew/.linuxbrew/lib")
      if (isLinux) // brew-installed s2n
        linkOpts.append("-L/home/linuxbrew/.linuxbrew/lib")
      else if (isMacOs) // brew-installed OpenSSL
        if (isArm) linkOpts.append("-L/opt/homebrew/opt/openssl@3/lib")
        else linkOpts.append("-L/usr/local/opt/openssl@3/lib")
      s2nLibPath match {
        case None =>
        case Some(path) => linkOpts.append(s"-L$path")
      }
      c.withLinkingOptions(c.linkingOptions ++ linkOpts.toSeq)
    },
    envVars ++= {
      val ldLibPath =
        if (isLinux)
          Map("LD_LIBRARY_PATH" -> "/home/linuxbrew/.linuxbrew/lib")
        else Map("LD_LIBRARY_PATH" -> "/usr/local/opt/openssl@1.1/lib")
      Map("S2N_DONT_MLOCK" -> "1") ++ ldLibPath
    }
  )

addCommandAlias("checkFormat", ";scalafmtSbtCheck ;scalafmtCheckAll")
addCommandAlias("scalafixLint", ";compile ;scalafix")
addCommandAlias(
  "testCoverage",
  ";coverage ;test ;coverageAggregate; coverageReport"
)
addCommandAlias(
  "verify",
  ";checkFormat ;scalafixLint ;testCoverage; dependencyCheck"
)
