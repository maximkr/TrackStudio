plugins {
    `java`
    war
}

group = "com.trackstudio"
version = "6.0-SNAPSHOT"
description = "TrackStudio Webapp"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.compileJava {
    options.compilerArgs.add("-XDignore.symbol.file=true")
    options.isVerbose = false
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
}

dependencies {
    // Тесты
    testImplementation("junit:junit:4.12")
    testImplementation("org.hamcrest:hamcrest-all:1.3")

    // Provided (только на контейнере приложений)
    compileOnly("javax.servlet:javax.servlet-api:4.0.1")
    compileOnly("javax.servlet.jsp:javax.servlet.jsp-api:2.3.3")

    // Остальное как implementation
    implementation("commons-logging:commons-logging:1.1.1")
    implementation("org.json:json:20180813")
    implementation("com.github.stephenc.jcip:jcip-annotations:1.0-1")

    implementation("org.apache.struts:struts-core:1.3.10")
    implementation("org.apache.struts:struts-tiles:1.3.10")
    implementation("org.apache.struts:struts-taglib:1.3.10")
    implementation("org.apache.struts:struts-el:1.3.10")
    implementation("org.apache.struts:struts-extras:1.3.10")

    implementation("org.freemarker:freemarker:2.3.28")

    implementation("org.apache.lucene:lucene-core:7.6.0")
    implementation("org.apache.lucene:lucene-analyzers-common:7.6.0")
    implementation("org.apache.lucene:lucene-highlighter:7.6.0")
    implementation("org.apache.lucene:lucene-queryparser:7.6.0")

    implementation("org.hibernate:hibernate-core:5.4.1.Final")
    implementation("antlr:antlr:2.7.6")
    implementation("org.hibernate:hibernate-c3p0:5.4.1.Final")

    implementation("javax.servlet:jstl:1.2")
    implementation("log4j:log4j:1.2.17")
    implementation("com.sun.mail:javax.mail:1.6.2")
    implementation("bsh:bsh:2.0b1")
    implementation("org.quartz-scheduler:quartz:2.3.0")
    implementation("xerces:xercesImpl:2.12.0")
    implementation("org.htmlparser:htmlparser:2.1")
    implementation("com.github.kevinsawicki:http-request:6.0")
    implementation("net.iharder:base64:2.3.9")
    implementation("net.sf.ehcache:ehcache-core:2.4.8")
    implementation("org.codehaus.jackson:jackson-mapper-asl:1.9.13")
    implementation("org.codehaus.jackson:jackson-core-asl:1.9.13")
    implementation("org.codehaus.jackson:jackson-xc:1.9.13")
    implementation("org.codehaus.jackson:jackson-jaxrs:1.9.13")
    implementation("net.htmlparser.jericho:jericho-html:2.3")
    implementation("com.mchange:c3p0:0.9.5.2")
    implementation("org.postgresql:postgresql:42.2.5")
    implementation("commons-codec:commons-codec:1.11")
    implementation("commons-fileupload:commons-fileupload:1.4")
}

tasks.war {
    // Аналог <finalName>TrackStudio</finalName>
    archiveBaseName.set("TrackStudio")
    archiveFileName.set("TrackStudio.war")
}


// Юнит-тесты JUnit4
tasks.test {
    useJUnit()
}
