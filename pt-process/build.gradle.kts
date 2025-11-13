import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.pt"
// TODO версию родителя взять
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":pt-api"))
    implementation(project(":pt-db"))
    implementation(project(":pt-numbers"))
    implementation(project(":pt-product"))
    implementation("com.jayway.jsonpath:json-path:2.9.0")
    implementation("org.springframework:spring-beans")
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web"){
        exclude("org.springframework.boot", "spring-boot-starter-tomcat")
    }
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