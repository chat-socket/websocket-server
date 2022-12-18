FROM openjdk:18-alpine AS jre-build
WORKDIR /app

# copy the dependencies into the docker image
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN sh gradlew clean deps build

# copy the executable jar into the docker image
RUN cp build/libs/*-SNAPSHOT.jar build/app.jar


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
--class-path="./build/lib/*" \
--module-path="./build/lib/*" \
# pipe the result of running jdeps on the app jar to file
build/app.jar > jre-deps.info

# new since last time!
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
COPY --from=jre-build /app/build/lib/* lib/

# copy the app
COPY --from=jre-build /app/build/app.jar app.jar

# run the app on startup
ENTRYPOINT jre/bin/java -jar app.jar
