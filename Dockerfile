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
FROM gradle:8.5-jdk21

WORKDIR /minecraft

COPY --from=builder /build/ /minecraft/

# Copy world data and configuration from run directory (not as volume)
COPY run/ /minecraft/run/

# Accept EULA
RUN echo "eula=true" > eula.txt

EXPOSE 8081

# Set JVM options
ENV JAVA_OPTS="-Xmx2G -Xms2G"

# headless may be needed
# -Djava.awt.headless=true

# Run using gradle's runClient task which handles all Fabric setup
#COPY --from=builder /build/gradlew /minecraft/
#COPY --from=builder /build/gradle /minecraft/gradle
CMD ["./gradlew", "runClient", "--no-daemon"]
