# Multi-stage build for Minecraft Fabric Mod
# Stage 1: Build and prepare the server
FROM gradle:8.5-jdk21 AS builder

WORKDIR /build

# Copy gradle files first for better caching
COPY gradle/ gradle/
COPY gradlew gradlew.bat gradle.properties settings.gradle build.gradle ./

# Download dependencies (cached layer)
RUN ./gradlew --no-daemon dependencies

# Copy source code
COPY src/ src/

# Build the mod
RUN ./gradlew --no-daemon build -x test

# Prepare the server using Fabric Loom's server preparation
# This downloads and sets up everything needed for a Fabric server
RUN ./gradlew --no-daemon downloadAssets

# Stage 2: Runtime image
FROM openjdk:21-jdk-slim

LABEL authors="axherrm"
LABEL description="MC Maze Agent State Extractor Mod - Minecraft with Fabric"

WORKDIR /minecraft

# Copy Minecraft and Fabric runtime from builder
COPY --from=builder /build/.gradle /minecraft/.gradle
COPY --from=builder /build/build/libs/*.jar /minecraft/mods/

# Copy world data and configuration from run directory (not as volume)
COPY run/saves /minecraft/saves
COPY run/config /minecraft/config
COPY run/options.txt /minecraft/options.txt

# Accept EULA
RUN echo "eula=true" > eula.txt

# Server configuration
RUN echo "server-port=25565" > server.properties && \
    echo "online-mode=false" >> server.properties && \
    echo "gamemode=survival" >> server.properties && \
    echo "difficulty=normal" >> server.properties

# Expose Minecraft server port and WebSocket port
EXPOSE 25565
EXPOSE 8887

# Set JVM options
ENV JAVA_OPTS="-Xmx2G -Xms2G"

# Run using gradle's runServer task which handles all Fabric setup
# Note: This assumes gradlew is available
COPY --from=builder /build/gradlew /minecraft/
COPY --from=builder /build/gradle /minecraft/gradle
CMD ["./gradlew", "runServer", "--no-daemon"]
