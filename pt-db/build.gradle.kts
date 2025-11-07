import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.7"
}
// TODO взять у parent'a
group = "ru.pt"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.springframework.security:spring-security-core")
    runtimeOnly("org.postgresql:postgresql")
    implementation(project(":pt-api"))
    implementation("org.springframework:spring-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // TODO version remove/const
    implementation("com.jayway.jsonpath:json-path:2.9.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}