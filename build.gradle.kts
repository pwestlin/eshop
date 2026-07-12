plugins {
    kotlin("jvm") version "2.4.0"
    kotlin("plugin.spring") version "2.4.0"
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("dev.detekt") version "2.0.0-alpha.5"
}

group = "nu.westlin"
version = "0.0.1-SNAPSHOT"
description = "E-Shop"

repositories {
    mavenCentral()
}

extra["springModulithVersion"] = "2.1.0"

dependencies {
    detektPlugins("io.github.pwestlin:detekt-rules:1.0")
    detektPlugins("dev.detekt:detekt-rules-ktlint-wrapper:2.0.0-alpha.5")

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("org.springframework.modulith:spring-modulith-starter-jdbc")
    implementation("tools.jackson.module:jackson-module-kotlin")

    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-data-jdbc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation("com.ninja-squad:springmockk:5.0.1")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
    }
}

kotlin {
    jvmToolchain(25)

    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

detekt {
    // Gör att din detekt.yml ärver alla standardregler istället för att skriva över dem helt
    buildUponDefaultConfig = true
    autoCorrect = true

    config.setFrom(files("src/main/detekt/detekt.yml"))
}

// Tasket "check" är kopplat till det gamla tasket "detekt" som inte hittar reglerna i "io.github.pwestlin:detekt-rules".

// 1. Tvinga 'check' (och därmed CI) att köra de typ-säkra analyserna
tasks.named("check") {
    dependsOn("detektMain", "detektTest")
}

// 2. Konfigurera om det korta bekvämlighetskommandot './gradlew detekt'
tasks.named("detekt") {
    // Låt kommandot delegera direkt vidare till de typ-säkra taskerna...
    dependsOn("detektMain", "detektTest")

    // ...men stäng av själva exekveringen av den generiska scannern.
    // Det gör att den bara markeras som SKIPPED och inte gör något dubbelarbete utan typanalys.
    enabled = false
}