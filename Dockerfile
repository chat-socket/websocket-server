FROM openjdk:18-slim AS deps-build
WORKDIR /app

# copy the dependencies into the docker image
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN sh gradlew clean installDist

# copy the executable jar into the docker image
RUN mv build/install/* build/dist
RUN mv build/libs/*-plain.jar build/libs/app.jar


# find JDK dependencies dynamically from jar
RUN jdeps \
--ignore-missing-deps \
# suppress any warnings printed to console
-q \
# java release version targeting
--multi-release 17 \
# output the dependencies at end of run
--print-module-deps \
# specify the the dependencies for the jar
--class-path="./build/dist/lib/*" \
# pipe the result of running jdeps on the app jar to file
build/libs/app.jar > jre-deps.info

FROM openjdk:17-alpine AS jre-build
WORKDIR /app

COPY --from=deps-build /app/jre-deps.info jre-deps.info

RUN jlink --verbose \
--compress 2 \
--strip-java-debug-attributes \
--no-header-files \
--no-man-pages \
--output jre \
--add-modules $(cat jre-deps.info)


# take a smaller runtime image for the final output
FROM alpine:3.16.2
WORKDIR /deployment

# copy the custom JRE produced from jlink
COPY --from=jre-build /app/jre jre

# copy the app dependencies
COPY --from=deps-build /app/build/dist dist

ENV JAVA_HOME=/deployment/jre

EXPOSE 8080

# run the app on startup
ENTRYPOINT /bin/sh dist/bin/application
