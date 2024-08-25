# Scala Native Ember Example
This is a simple example project demonstrating how to build a Scala Native application using http4s.
Example based on example: https://github.com/ChristopherDavenport/scala-native-ember-example

### Build and run binaries
```
sbt nativeLink
SCALA_VERSION=$(sbt "scalaVersion" | tail -1 | awk '{print $NF}')
./target/scala-$SCALA_VERSION/scala-native-ember-example-out
```

### Run docker image
```
docker run -p 8080:8080 ghcr.io/biandratti/http4s-native-example/http4s-example:latest
```
