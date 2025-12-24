FROM openjdk:21-jdk-slim

# Install required dependencies for ND4J native backend
RUN apt-get update && apt-get install -y \
    libopenblas-dev \
    liblapack-dev \
    libgomp1 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy project files
COPY . .

# Build the project
RUN ./gradlew build -x test --no-daemon

# Expose port for application
EXPOSE 8080

# Set entry point
ENTRYPOINT ["java", "-jar", "build/libs/text-qa-system-1.0-SNAPSHOT.jar"]

# Or use the runApp entry point with file path
# ENTRYPOINT ["java", "-cp", "build/libs/text-qa-system-1.0-SNAPSHOT.jar:build/resources/main", \
#             "com.medhavi.qa.TextQAApplicationRunner"]
