FROM gradle:5.6.2-jdk8 as builder

WORKDIR /home/app
COPY . .
RUN ./gradlew build --no-daemon

FROM openjdk:8-jre-alpine

WORKDIR /home/app
COPY --from=builder /home/app/build/libs/app.jar ./app.jar
CMD java -jar app.jar