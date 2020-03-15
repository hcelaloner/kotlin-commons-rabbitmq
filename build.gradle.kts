buildscript {
    repositories {
        jcenter()
        mavenCentral()
    }
}

plugins {
    java
    kotlin("jvm") version "1.3.72"
    id("org.jetbrains.dokka") version "0.10.1"
}

// Logging
val slf4jVersion by extra { "1.7.30" }

// Misc
val jsr305Version by extra { "3.0.2" }

// Blockhound
val blockhoundVersion by extra { "1.0.3.RELEASE" }

// Testing
val jUnitJupiterVersion by extra { "5.6.2" }
val assertJVersion by extra { "3.16.1" }
val mockitoVersion by extra { "3.3.3" }


allprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.dokka")

    group = "com.hcelaloner"

    repositories {
        jcenter()
        mavenCentral()
    }

    dependencies {
        // Kotlin STD
        implementation(kotlin("stdlib-jdk8"))

        // Misc
        implementation("org.apache.commons:commons-lang3:+")

        // SL4J Logging
        compileOnly("org.slf4j:slf4j-api:$slf4jVersion")

        // Junit 5
        testImplementation("org.junit.jupiter:junit-jupiter-api:${jUnitJupiterVersion}")
        testImplementation("org.junit.jupiter:junit-jupiter-params:${jUnitJupiterVersion}")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${jUnitJupiterVersion}")
        testImplementation("org.assertj:assertj-core:${assertJVersion}")
        testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=compatibility")
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=compatibility")
        }
        dokka {
            outputFormat = "html"
            outputDirectory = "$buildDir/javadoc"
        }
        test {
            useJUnitPlatform()
        }
    }
}