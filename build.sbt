Global / dependencyCheckFormats := Seq("HTML", "JSON")

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
    libraryDependencies ++= Dependencies.http4sDependencies
      ++ Dependencies.logbackDependencies
      ++ Dependencies.munitDependencies,
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
