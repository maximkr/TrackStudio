plugins {
    `java`
    war
    id("com.palantir.git-version") version "4.0.0"
}

// Автоматическое версионирование на основе git тегов
val gitVersion: groovy.lang.Closure<String> by extra

group = "com.trackstudio"
version = gitVersion()
description = "TrackStudio Webapp"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.compileJava {
    options.isVerbose = false
    options.encoding = "UTF-8"
}

tasks.compileTestJava {
    options.encoding = "UTF-8"
}

tasks.processResources {
    filteringCharset = "UTF-8"
    
    // Генерируем файл с версией для Java кода
    doFirst {
        val versionPropsFile = File(sourceSets.main.get().output.resourcesDir, "version.properties")
        versionPropsFile.parentFile.mkdirs()
        versionPropsFile.writeText("trackstudio.version=${version}\n")
    }
    
    // JSTL читает .properties как ISO-8859-1. Конвертируем language_*.properties
    // из UTF-8 в ASCII с \uXXXX, как делал Maven (native2ascii),
    // чтобы на рантайме текст отображался корректно.
    filesMatching("**/language_*.properties") {
        filter(org.apache.tools.ant.filters.EscapeUnicode::class.java)
    }
}

tasks.processTestResources {
    filteringCharset = "UTF-8"
}

repositories {
    mavenCentral()
}

dependencies {
    // Тесты
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest:2.2")

    // Provided (только на контейнере приложений)
    compileOnly("javax.servlet:javax.servlet-api:4.0.1")
    compileOnly("javax.servlet.jsp:javax.servlet.jsp-api:2.3.3")



    // Остальное как implementation
    // Logging - миграция с Log4j 1.2 на SLF4J + Logback
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("ch.qos.logback:logback-core:1.4.14")
    implementation("org.slf4j:jcl-over-slf4j:2.0.12") // Commons Logging через SLF4J
    implementation("org.slf4j:log4j-over-slf4j:2.0.12") // Log4j 1.2 через SLF4J (для legacy кода)
    
    implementation("org.json:json:20240303")
    implementation("com.github.stephenc.jcip:jcip-annotations:1.0-1")

    implementation("org.apache.struts:struts-core:1.3.10")
    implementation("org.apache.struts:struts-tiles:1.3.10")
    implementation("org.apache.struts:struts-taglib:1.3.10")
    implementation("org.apache.struts:struts-el:1.3.10")
    implementation("org.apache.struts:struts-extras:1.3.10")

    implementation("org.freemarker:freemarker:2.3.33")

    implementation("org.apache.lucene:lucene-core:7.6.0")
    implementation("org.apache.lucene:lucene-analyzers-common:7.6.0")
    implementation("org.apache.lucene:lucene-highlighter:7.6.0")
    implementation("org.apache.lucene:lucene-queryparser:7.6.0")

    implementation("org.hibernate:hibernate-core:5.6.15.Final")
    implementation("org.hibernate:hibernate-c3p0:5.6.15.Final")
    implementation("antlr:antlr:2.7.6")

    implementation("javax.servlet:jstl:1.2")
    // Удаляем log4j:log4j:1.2.17 - заменяем на SLF4J + Logback выше
    implementation("com.sun.mail:javax.mail:1.6.2")
    implementation("org.apache-extras.beanshell:bsh:2.0b6")
    implementation("org.quartz-scheduler:quartz:2.3.2")
    implementation("xerces:xercesImpl:2.12.2")
    implementation("org.htmlparser:htmlparser:2.1")
    implementation("com.github.kevinsawicki:http-request:6.0")
    implementation("net.iharder:base64:2.3.9")
    implementation("net.sf.ehcache:ehcache-core:2.6.11")
    implementation("org.codehaus.jackson:jackson-mapper-asl:1.9.13")
    implementation("org.codehaus.jackson:jackson-core-asl:1.9.13")
    implementation("org.codehaus.jackson:jackson-xc:1.9.13")
    implementation("org.codehaus.jackson:jackson-jaxrs:1.9.13")
    implementation("net.htmlparser.jericho:jericho-html:3.4")
    implementation("com.mchange:c3p0:0.9.5.5")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("commons-codec:commons-codec:1.17.0")
    implementation("commons-fileupload:commons-fileupload:1.6.0")
}

// Исключаем старые логирование библиотеки из всех зависимостей
configurations.all {
    exclude(group = "commons-logging", module = "commons-logging")
    exclude(group = "log4j", module = "log4j")
    exclude(group = "org.slf4j", module = "slf4j-log4j12")
    exclude(group = "org.slf4j", module = "slf4j-jdk14")
}

tasks.war {
    // Аналог <finalName>TrackStudio</finalName>
    archiveBaseName.set("TrackStudio")
    archiveFileName.set("TrackStudio.war")
    // Сохраняем реальные timestamp файлов — иначе Tomcat не перекомпилирует JSP
    // при редеплое (Gradle reproducible builds ставят дату 1980)
    isPreserveFileTimestamps = true
}


// Юнит-тесты JUnit4
tasks.test {
    useJUnit()
    systemProperty("file.encoding", "UTF-8")
}

// Настройка Javadoc для UTF-8 (на случай генерации документации)
tasks.javadoc {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).charSet = "UTF-8"
}
