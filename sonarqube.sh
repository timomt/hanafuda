#!/bin/bash

sbt jacoco

bash -c 'cd ~/Development/se/Hanafuda && ~/Downloads/sonar-scanner-6.2.1.4610-linux-x64/bin/sonar-scanner \
  -Dsonar.projectKey=koi-koi \
  -Dsonar.sources=. \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqp_c4d26970ed709cefd12a9f983035b133c5bf002a'

firefox http://localhost:9000