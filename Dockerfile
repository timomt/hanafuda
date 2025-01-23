# Stage 1 - Big JDK to build small custom JRE including openjfx
FROM azul/zulu-openjdk-alpine:21 AS builder

ENV SBT_VERSION=1.10.7 \
    SCALA_VERSION=3.5.1

RUN echo "http://dl-cdn.alpinelinux.org/alpine/edge/testing" >> /etc/apk/repositories

RUN apk update && \
    apk add --no-cache bash curl unzip openjfx && \
    rm -rf /tmp/* /var/cache/apk/*

RUN curl -L -o sbt.zip https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.zip && \
    unzip sbt.zip -d /usr/local && \
    rm sbt.zip && \
    ln -s /usr/local/sbt/bin/sbt /usr/local/bin/sbt

RUN $JAVA_HOME/bin/jlink \
    --module-path /usr/lib/openjfx:/opt/java/openjdk/jmods \
    --add-modules java.base,java.logging,java.management,jdk.compiler,java.sql,javafx.base,javafx.controls,javafx.fxml,jdk.jartool,jdk.zipfs \
    --output /custom-jre

RUN cp /usr/lib/openjfx/*.so /custom-jre/lib

WORKDIR /hanafuda
ADD . /hanafuda

RUN printf "compile\nexit\n" | sbt

# Stage 2 - Small custom JRE
FROM azul/zulu-openjdk-alpine:21-jre

RUN apk add --no-cache bash vim libc6-compat libxxf86vm-dev mesa-gl apk-gtk3 ttf-dejavu

COPY --from=builder /custom-jre /custom-jre
COPY --from=builder /hanafuda /hanafuda
COPY --from=builder /usr/local/sbt /usr/local/sbt
COPY --from=builder /usr/local/bin/sbt /usr/local/bin/sbt

ENV PATH="/custom-jre/bin:$PATH"

WORKDIR /hanafuda

CMD ["sbt", "run"]

# run:  docker run --rm -it --net=host --env DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix -v /dev/dri:/dev/dri --device /dev/kfd:/dev/kfd hanafuda:v1 /bin/bash