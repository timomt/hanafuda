FROM azul/zulu-openjdk-alpine:21 as builder

ENV SBT_VERSION=1.10.7 \
    SCALA_VERSION=3.5.1

RUN echo "http://dl-cdn.alpinelinux.org/alpine/edge/testing" >> /etc/apk/repositories

RUN apk add --no-cache bash curl unzip openjfx

RUN curl -L -o sbt.zip https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.zip && \
    unzip sbt.zip -d /usr/local && \
    rm sbt.zip && \
    ln -s /usr/local/sbt/bin/sbt /usr/local/bin/sbt

RUN rm -rf /tmp/* /var/cache/apk/*

WORKDIR /hanafuda
ADD . /hanafuda

RUN $JAVA_HOME/bin/jlink \
    --module-path /usr/lib/openjfx:/opt/java/openjdk/jmods \
    --add-modules java.base,javafx.controls,javafx.fxml,jdk.jartool \
    --output /custom-jre


FROM azul/zulu-openjdk-alpine:21-jre

RUN apk add --no-cache bash vim libc6-compat libxxf86vm-dev mesa-gl apk-gtk3 ttf-dejavu

COPY --from=builder /custom-jre /custom-jre
COPY --from=builder /hanafuda /hanafuda
COPY --from=builder /usr/local/sbt /usr/local/sbt
COPY --from=builder /usr/local/bin/sbt /usr/local/bin/sbt

WORKDIR /hanafuda

ENV PATH="/custom-jre/bin:$PATH"

CMD ["sbt", "run"]

# run:  docker run --rm -it --net=host --env DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix -v /dev/dri:/dev/dri --device /dev/kfd:/dev/kfd hanafuda:v1 /bin/bash