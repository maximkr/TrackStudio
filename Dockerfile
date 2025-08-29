# =========================
# Stage 1: Build with Gradle
# =========================
FROM ubuntu:24.04 AS builder

ENV DEBIAN_FRONTEND=noninteractive

# Create non-root user for security
RUN groupadd -r gradle && useradd -r -g gradle gradle

# Install base utilities
RUN apt-get update && apt-get install -y \
    wget curl tar unzip zip ca-certificates bash coreutils \
    && rm -rf /var/lib/apt/lists/*

# --- Install OpenJDK 21.0.7 ---
RUN wget https://download.oracle.com/java/21/archive/jdk-21.0.7_linux-x64_bin.tar.gz -O /tmp/openjdk.tar.gz \
    && mkdir -p /usr/local/java \
    && tar -xzf /tmp/openjdk.tar.gz -C /usr/local/java \
    && rm /tmp/openjdk.tar.gz \
    && chown -R gradle:gradle /usr/local/java
ENV JAVA_HOME=/usr/local/java/jdk-21.0.7
ENV PATH="$JAVA_HOME/bin:${PATH}"

# --- Install Gradle 9.0.0 ---
ENV GRADLE_VERSION=9.0.0
RUN wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -O /tmp/gradle.zip \
    && mkdir -p /opt/gradle \
    && unzip -q /tmp/gradle.zip -d /opt/gradle \
    && rm /tmp/gradle.zip \
    && chown -R gradle:gradle /opt/gradle
ENV GRADLE_HOME=/opt/gradle/gradle-${GRADLE_VERSION}
ENV PATH="${GRADLE_HOME}/bin:${PATH}"

# Gradle local cache
ENV GRADLE_USER_HOME=/home/gradle/.gradle
RUN mkdir -p ${GRADLE_USER_HOME} && chown -R gradle:gradle /home/gradle

WORKDIR /app
RUN chown gradle:gradle /app

# Switch to non-root user
USER gradle

# Copy only build scripts first (to leverage Docker layer caching)
COPY --chown=gradle:gradle build.gradle.kts settings.gradle.kts gradle.properties ./

# Pre-download dependencies
RUN gradle --no-daemon --stacktrace dependencies

# Copy application sources
COPY --chown=gradle:gradle src ./src

# Build WAR file (skip tests for faster build)
RUN gradle --no-daemon clean war -x test

# =======================
# Stage 2: Runtime (Tomcat)
# =======================
FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive

# Create tomcat user for security
RUN groupadd -r tomcat && useradd -r -g tomcat tomcat

# Install basic tools
RUN apt-get update && apt-get install -y \
    wget tar ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# --- Install OpenJDK 21.0.7 ---
RUN wget https://download.oracle.com/java/21/archive/jdk-21.0.7_linux-x64_bin.tar.gz -O /tmp/openjdk.tar.gz \
    && mkdir -p /usr/local/java \
    && tar -xzf /tmp/openjdk.tar.gz -C /usr/local/java \
    && rm /tmp/openjdk.tar.gz \
    && chown -R tomcat:tomcat /usr/local/java
ENV JAVA_HOME=/usr/local/java/jdk-21.0.7
ENV PATH="$JAVA_HOME/bin:${PATH}"

# --- Install Tomcat 9.0.108 ---
ENV TOMCAT_VERSION=9.0.108
RUN wget https://dlcdn.apache.org/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz -O /tmp/tomcat.tar.gz \
    && mkdir -p /usr/local/tomcat \
    && tar -xzf /tmp/tomcat.tar.gz -C /usr/local/tomcat --strip-components=1 \
    && rm /tmp/tomcat.tar.gz \
    && chown -R tomcat:tomcat /usr/local/tomcat
ENV CATALINA_HOME=/usr/local/tomcat
ENV PATH="$CATALINA_HOME/bin:${PATH}"

# Create TrackStudio data directories with proper permissions
RUN mkdir -p /data/trackstudio/upload /data/trackstudio/index \
    && chown -R tomcat:tomcat /data/trackstudio

# Copy WAR built in the builder stage
COPY --from=builder --chown=tomcat:tomcat /app/build/libs/TrackStudio.war $CATALINA_HOME/webapps/

# Set JVM encoding for UTF-8 support
ENV CATALINA_OPTS="-Dfile.encoding=UTF-8"

# Switch to non-root user
USER tomcat

EXPOSE 8080
CMD ["catalina.sh", "run"]
