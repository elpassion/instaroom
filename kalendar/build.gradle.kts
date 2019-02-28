plugins {
    `maven-publish`
    kotlin("jvm")
}

val kotlinVersion: String by project

group = "com.github.elpassion.instaroom"
version = "0.0.2"

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    compile("com.google.apis:google-api-services-calendar:v3-rev305-1.23.0")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.1")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.0.1")
}

// Create sources Jar from main kotlin sources
val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    classifier = "sources"
    from(sourceSets.getByName("main").allSource)
}

publishing {
    publications {
        create("default", MavenPublication::class.java) {
            from(components.getByName("java"))
            artifact(sourcesJar)
        }
    }
    repositories {
        maven {
            url = uri("$buildDir/repository")
        }
    }
}
