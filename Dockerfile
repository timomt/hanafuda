FROM sbtscala/scala-sbt:eclipse-temurin-23.0.1_11_1.10.7_3.6.2

RUN apt-get update && apt-get install -y \
    libx11-6 libxext6 libxrender1 libxtst6 libxi6 libfreetype6 libxft2 \
    libfontconfig1 libxinerama1 libxcursor1 libxrandr2 libxcomposite1 \
    libgl1-mesa-dri mesa-utils libglu1-mesa xvfb x11-xkb-utils xkb-data \
    libcanberra-gtk3-module alsa-utils unzip wget at-spi2-core

RUN wget https://download2.gluonhq.com/openjfx/23.0.1/openjfx-23.0.1_linux-aarch64_bin-sdk.zip \
    && unzip openjfx-23.0.1_linux-aarch64_bin-sdk.zip -d /opt \
    && rm openjfx-23.0.1_linux-aarch64_bin-sdk.zip

# Set the JavaFX environment variables
ENV PATH=$PATH:/opt/openjfx-23.0.1/lib
ENV JAVA_MODULE_PATH=/opt/openjfx-23.0.1/lib

WORKDIR /hanafuda
ADD . /hanafuda

# Resolve SBT dependencies (cache optimization step)
RUN sbt update

# Run SBT
CMD ["sbt", "run"]

# run:  docker run --rm -it --net=host --env DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix -v /dev/dri:/dev/dri --device /dev/kfd:/dev/kfd hanafuda:v1 /bin/bash