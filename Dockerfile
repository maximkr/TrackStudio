FROM ubuntu:24.04 AS builder
RUN apt-get update

RUN apt-get update && apt-get install -y wget tar mc \
    && rm -rf /var/lib/apt/lists/*

# Download and unpack OpenJDK 11.0.2
RUN wget https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_linux-x64_bin.tar.gz -O /tmp/openjdk.tar.gz \
    && mkdir -p /usr/local/java \
    && tar -xzf /tmp/openjdk.tar.gz -C /usr/local/java \
    && rm /tmp/openjdk.tar.gz

# Set env
ENV JAVA_HOME=/usr/local/java/jdk-11.0.2
ENV PATH="$JAVA_HOME/bin:${PATH}"

# Maven
ENV MAVEN_VERSION=3.9.11
RUN wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz -O /tmp/maven.tar.gz \
    && mkdir -p /usr/local/maven \
    && tar -xzf /tmp/maven.tar.gz -C /usr/local/maven \
    && rm /tmp/maven.tar.gz

ENV MAVEN_HOME=/usr/local/maven/apache-maven-${MAVEN_VERSION}
ENV PATH="$MAVEN_HOME/bin:${PATH}"

# copy source and build
WORKDIR /app

# Copy pom.xml only to prepare docker build cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy other files and build
COPY src src
RUN mvn -B package --file pom.xml

# ===== Stage 2: Runtime =====
FROM ubuntu:24.04

RUN apt-get update && apt-get install -y wget tar mc \
    && rm -rf /var/lib/apt/lists/*

# === Java 11 ===
RUN wget https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_linux-x64_bin.tar.gz -O /tmp/openjdk.tar.gz \
    && mkdir -p /usr/local/java \
    && tar -xzf /tmp/openjdk.tar.gz -C /usr/local/java \
    && rm /tmp/openjdk.tar.gz

ENV JAVA_HOME=/usr/local/java/jdk-11.0.2
ENV PATH="$JAVA_HOME/bin:${PATH}"

# === Tomcat ===
ENV TOMCAT_VERSION=9.0.108
RUN wget https://dlcdn.apache.org/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz -O /tmp/tomcat.tar.gz \
    && mkdir -p /usr/local/tomcat \
    && tar -xzf /tmp/tomcat.tar.gz -C /usr/local/tomcat --strip-components=1 \
    && rm /tmp/tomcat.tar.gz

ENV CATALINA_HOME=/usr/local/tomcat
ENV PATH="$CATALINA_HOME/bin:${PATH}"

# Копируем только .war из builder
COPY --from=builder /app/target/*.war $CATALINA_HOME/webapps/

EXPOSE 8080
CMD ["catalina.sh", "run"]
