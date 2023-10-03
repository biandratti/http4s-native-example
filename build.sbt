val Http4sVersion = "1.0.0-M29"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.2.6"
val MunitCatsEffectVersion = "1.0.7"

lazy val root = (project in file("."))
  .settings(
    organization := "com.biandratti",
    name := "http4s-example",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.1.3",
    inThisBuild(
      List(
        scalaVersion := "3.1.3",
        semanticdbEnabled := true,
        semanticdbVersion := scalafixSemanticdb.revision
      )
    ),
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-ember-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.scalameta" %% "munit" % MunitVersion % Test,
      "org.typelevel" %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback" % "logback-classic" % LogbackVersion
    ),
    testFrameworks += new TestFramework("munit.Framework")
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
