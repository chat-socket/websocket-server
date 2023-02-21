#!/bin/bash

VERSION="1.0.0-dev"
POSITIONAL=()
while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    -v|--version)
    VERSION="$2"
    shift # past argument
    shift # past value
    ;;
    *)    # unknown option
    POSITIONAL+=("$1") # save it in an array for later
    shift # past argument
    ;;
esac
done
set -- "${POSITIONAL[@]}" # restore positional parameters

/bin/bash gradlew build -Dquarkus.package.type=native

docker build -f src/main/docker/Dockerfile.native-micro -t "chatsocket/websocket-server:${VERSION}" "${POSITIONAL[@]}" .
