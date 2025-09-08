#############################
# Stage 1: Build with Gradle
#############################
FROM gradle:9.0.0-jdk21 AS builder
WORKDIR /src

COPY .git ./.git

# Копируем только файлы сборки для кеша
COPY settings.gradle.kts build.gradle.kts gradle.properties ./

# Прогрев зависимостей
RUN --mount=type=cache,target=/home/gradle/.gradle \
    gradle --no-daemon --stacktrace dependencies

# Исходники и сборка WAR (без тестов)
COPY src ./src
RUN --mount=type=cache,target=/home/gradle/.gradle \
    gradle --no-daemon clean war -x test

#############################
# Stage 2: Runtime (Tomcat+JDK Temurin)
#############################
FROM tomcat:9.0-jdk21-temurin

# Очистить дефолтные webapps
RUN rm -rf "$CATALINA_HOME/webapps/*"

# Разворачиваем наш WAR как ROOT
COPY --from=builder /src/build/libs/TrackStudio.war "$CATALINA_HOME/webapps/ROOT.war"

# Директории данных TrackStudio
RUN mkdir -p /data/trackstudio/upload /data/trackstudio/index

# --- создать пользователя и выдать права на каталоги, где Tomcat пишет ---
RUN set -eux; \
  groupadd -r tomcat || true; \
  useradd  -r -g tomcat -d "$CATALINA_HOME" -s /usr/sbin/nologin tomcat || true; \
  mkdir -p /logs "$CATALINA_HOME/logs" "$CATALINA_HOME/work" "$CATALINA_HOME/temp"; \
  chown -R tomcat:tomcat \
    "$CATALINA_HOME/webapps" \
    "$CATALINA_HOME/logs" \
    "$CATALINA_HOME/work" \
    "$CATALINA_HOME/temp" \
    /data/trackstudio /logs

# JVM/Tomcat настройки
ENV CATALINA_OPTS="\
 -Dfile.encoding=UTF-8 \
 -Dtrackstudio.upload.dir=/data/trackstudio/upload \
 -Dtrackstudio.index.dir=/data/trackstudio/index"

WORKDIR $CATALINA_HOME
USER tomcat
EXPOSE 8080
CMD ["catalina.sh", "run"]
