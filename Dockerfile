FROM openjdk:21-slim
WORKDIR /app
COPY . .
RUN ./gradlew build
CMD ["./gradlew", "bootRun"]
