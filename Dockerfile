# Use Ubuntu 23.04 as the base image https://github.com/aws/s2n-tls
FROM ubuntu:22.04 as build-env

ARG SBT_VERSION=1.9.6
ARG S2N_VERSION=1.3.55

ENV JAVA_HOME=/opt/java/openjdk
COPY --from=eclipse-temurin:17 $JAVA_HOME $JAVA_HOME
ENV PATH="${JAVA_HOME}/bin:${PATH}"

WORKDIR /build

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl ca-certificates unzip git \
    clang \
    libssl-dev cmake build-essential

# Install sbt
RUN \
    curl -sL "https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.zip" > /tmp/sbt.zip && \
    unzip /tmp/sbt.zip -d /opt
ENV PATH="/opt/sbt/bin:${PATH}"

# Install s2n.
RUN git clone https://github.com/aws/s2n-tls.git --branch "v$S2N_VERSION"
RUN cd s2n-tls && \
    cmake . -Bbuild \
        -DCMAKE_BUILD_TYPE=Release \
        -DCMAKE_INSTALL_PREFIX=./s2n-tls-install && \
    cmake --build build -j $(nproc) && \
    cmake --install build

# Build the native image.
COPY . /build
RUN SCALA_VERSION=$(sbt "show scalaVersion" | grep '\[info\]' | tail -1 | awk '{print $NF}') && \
    S2N_LIBRARY_PATH=/build/s2n-tls/s2n-tls-install/lib sbt nativeLink && \
    mv /build/target/scala-${SCALA_VERSION}/scala-native-ember-example-out /build/app

FROM gcr.io/distroless/cc
WORKDIR /app
COPY --from=build-env /build/app ./app
EXPOSE 8080
ENV S2N_DONT_MLOCK=1
ENTRYPOINT ["./app"]
