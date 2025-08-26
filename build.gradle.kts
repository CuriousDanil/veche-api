plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.1.20"
    kotlin("kapt") version "2.1.20"
    id("com.appland.appmap") version "1.2.0"
}

group = "com.veche"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

springBoot {
    mainClass.set("com.veche.api.ApiApplicationKt")
}

extra["springAiVersion"] = "1.0.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.ai:spring-ai-starter-model-openai")
    implementation("org.springframework.boot:spring-boot-starter-actuator") // debug
    implementation("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("com.anthropic:anthropic-java:2.5.1")

    implementation("org.liquibase:liquibase-core")

    implementation("com.vladmihalcea:hibernate-types-60:2.21.1")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // JPABuddy
    implementation("org.mapstruct:mapstruct:1.6.3")
    implementation("org.mapstruct:mapstruct-processor:1.6.3")
    kapt("org.mapstruct:mapstruct-processor:1.6.3")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.20.3"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.redis:testcontainers-redis:2.2.4")
}
tasks.test { useJUnitPlatform() }

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
    dependsOn("update")
}

tasks.bootJar {
    archiveFileName.set("veche-api.jar")
}
