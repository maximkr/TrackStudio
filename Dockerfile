# =========================
# Stage 1: Build with Gradle
# =========================
FROM ubuntu:24.04 AS builder

ENV DEBIAN_FRONTEND=noninteractive

# Install base utilities
RUN apt-get update && apt-get install -y \
    wget curl tar unzip zip ca-certificates bash coreutils \
    && rm -rf /var/lib/apt/lists/*

# --- Install OpenJDK 17.0.2 ---
RUN wget https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz -O /tmp/openjdk.tar.gz \
    && mkdir -p /usr/local/java \
    && tar -xzf /tmp/openjdk.tar.gz -C /usr/local/java \
    && rm /tmp/openjdk.tar.gz
ENV JAVA_HOME=/usr/local/java/jdk-17.0.2
ENV PATH="$JAVA_HOME/bin:${PATH}"

# --- Install Gradle 8.14.3 ---
ENV GRADLE_VERSION=9.0.0
RUN wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -O /tmp/gradle.zip \
    && mkdir -p /opt/gradle \
    && unzip -q /tmp/gradle.zip -d /opt/gradle \
    && rm /tmp/gradle.zip
ENV GRADLE_HOME=/opt/gradle/gradle-${GRADLE_VERSION}
ENV PATH="${GRADLE_HOME}/bin:${PATH}"

# Gradle local cache
ENV GRADLE_USER_HOME=/home/gradle/.gradle
RUN mkdir -p ${GRADLE_USER_HOME}

WORKDIR /app

# Copy only build scripts first (to leverage Docker layer caching)
COPY build.gradle.kts settings.gradle.kts gradle.properties ./

# Pre-download dependencies
RUN gradle --no-daemon --stacktrace dependencies

# Copy application sources
COPY src ./src

# Build WAR file (skip tests for faster build)
RUN gradle --no-daemon clean war -x test

# =======================
# Stage 2: Runtime (Tomcat)
# =======================
FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive

# Install basic tools
RUN apt-get update && apt-get install -y \
    wget tar mc ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# --- Install OpenJDK 17.0.2 ---
RUN wget https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz -O /tmp/openjdk.tar.gz \
    && mkdir -p /usr/local/java \
    && tar -xzf /tmp/openjdk.tar.gz -C /usr/local/java \
    && rm /tmp/openjdk.tar.gz
ENV JAVA_HOME=/usr/local/java/jdk-17.0.2
ENV PATH="$JAVA_HOME/bin:${PATH}"

# --- Install Tomcat 9.0.108 ---
ENV TOMCAT_VERSION=9.0.108
RUN wget https://dlcdn.apache.org/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz -O /tmp/tomcat.tar.gz \
    && mkdir -p /usr/local/tomcat \
    && tar -xzf /tmp/tomcat.tar.gz -C /usr/local/tomcat --strip-components=1 \
    && rm /tmp/tomcat.tar.gz
ENV CATALINA_HOME=/usr/local/tomcat
ENV PATH="$CATALINA_HOME/bin:${PATH}"

# Copy WAR built in the builder stage
COPY --from=builder /app/build/libs/TrackStudio.war $CATALINA_HOME/webapps/

EXPOSE 8080
CMD ["catalina.sh", "run"]
