FROM gradle:7.6.0-jdk19-alpine AS builder
USER root
WORKDIR /builder
ADD . /builder
RUN gradle build --stacktrace

FROM openjdk:19-jdk
WORKDIR /app
EXPOSE 8080
COPY --from=builder /builder/build/libs/blog-0.0.1.jar .
CMD ["java", "-jar", "blog-0.0.1.jar"]