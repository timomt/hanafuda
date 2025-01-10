#FROM bellsoft/liberica-runtime-container:jdk-23-cds-slim-musl
#
#ENV SCALA_VERSION=3.5.1
#ENV SBT_VERSION=1.5.5
#
#
#
#RUN \
#   curl -fsL https://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz | tar xfz - -C /root/ && \
#    echo >> /root/.bashrc && \
#    echo 'export PATH=~/scala-$SCALA_VERSION/bin:$PATH' >> /root/.bashrc
#
#RUN \
#   curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
#    dpkg -i sbt-$SBT_VERSION.deb && \
#    rm sbt-$SBT_VERSION.deb && \
#    apt-get update && \
#    apt-get install sbt && \
#    sbt sbtVersion

FROM sbtscala/scala-sbt:amazoncorretto-al2023-21.0.5_1.10.6_3.6.2
RUN yum update -y && \
    yum install -y libXxf86vm gtk3 libXtst mesa-libGL alsa-lib alsa-utils
#RUN yum update -y

WORKDIR /hanafuda
ADD . /hanafuda

RUN export DISPLAY=:0

# Resolve SBT dependencies (cache optimization step)
RUN sbt update

# Run SBT
CMD ["sbt", "run"]