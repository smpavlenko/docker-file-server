#!/usr/bin/env bash

mvn clean install

PORT=8080
CONTAINER_NAME=docker-file-server

docker build -t docker-file-server .
docker run -it --rm -p $PORT:$PORT -v $(pwd):/root_dir --name $CONTAINER_NAME $CONTAINER_NAME /root_dir