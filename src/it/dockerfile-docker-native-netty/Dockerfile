FROM ghcr.io/graalvm/native-image:java11-21.3.0 AS builder
WORKDIR /home/app

COPY classes /home/app/classes
COPY dependency/* /home/app/libs/
RUN native-image -H:Class=io.micronaut.build.examples.Application -H:Name=application --no-fallback -cp "/home/app/libs/*:/home/app/classes/"

FROM frolvlad/alpine-glibc:alpine-3.12
RUN apk update && apk add libstdc++
COPY --from=builder /home/app/application /app/application

EXPOSE 8080
ENTRYPOINT ["/app/application"]
