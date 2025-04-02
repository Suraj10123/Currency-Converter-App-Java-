# Use lightweight JRE base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY CurrencyConverterApp.jar CurrencyConverterApp.jar

# Run the app
ENTRYPOINT ["java", "-jar", "CurrencyConverterApp.jar"]
