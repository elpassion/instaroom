import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by project
val ktorVersion: String by project
val logbackVersion: String by project

plugins {
    application
    kotlin("jvm")
}

apply {
    from("jarsetup.gradle")
}

group = "instaroom"
version = "0.0.1"

application {
    mainClassName = "io.ktor.server.netty.DevelopmentEngine"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://kotlin.bintray.com/kotlin-js-wrappers") }
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    compile("io.ktor:ktor-server-netty:$ktorVersion")
    compile("ch.qos.logback:logback-classic:$logbackVersion")
    compile("io.ktor:ktor-server-core:$ktorVersion")
    compile("io.ktor:ktor-html-builder:$ktorVersion")
    compile("org.jetbrains:kotlin-css-jvm:1.0.0-pre.31-kotlin-1.2.41")
    compile("io.ktor:ktor-server-host-common:$ktorVersion")
    compile("io.ktor:ktor-server-sessions:$ktorVersion")
    compile("io.ktor:ktor-auth:$ktorVersion")
    compile("io.ktor:ktor-client-core:$ktorVersion")
    compile("io.ktor:ktor-client-core-jvm:$ktorVersion")
    compile("io.ktor:ktor-client-apache:$ktorVersion")
    compile("io.ktor:ktor-jackson:$ktorVersion")
    compile("io.ktor:ktor-freemarker:$ktorVersion")
    compile("com.google.apis:google-api-services-calendar:v3-rev305-1.23.0")
    testCompile("io.ktor:ktor-server-tests:$ktorVersion")
    testCompile("io.ktor:ktor-client-mock:$ktorVersion")
}

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

tasks.register("createConfig") {
    val hash = getHeadCommitHash()
    file("src/Config.kt").writeText("val configHeadCommitHash = \"$hash\"")
}

tasks.named<Task>("assemble") { dependsOn("createConfig") }
