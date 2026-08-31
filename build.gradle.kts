import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "2.2.0"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.mapcrawler"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

val profile = if (project.hasProperty("profile"))
    project.property("profile").toString() else "local"


sourceSets.getByName("main").resources {
    srcDirs(
        listOf(
            "src/main/resources",
            "src/main/resources-env/$profile"
        )
    )
}


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Headless browser automation
    implementation("com.microsoft.playwright:playwright:1.48.0")

    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.jsoup:jsoup:1.18.1")
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("org.json:json:20250107")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

// resources 와 resources-env/$profile 을 함께 넣을 때 발생하는 중복 파일 충돌 방지
gradle.taskGraph.whenReady {
    allTasks
        .filter { it.hasProperty("duplicatesStrategy") }
        .forEach {
            it.setProperty("duplicatesStrategy", "exclude")
        }
}

tasks.withType<BootJar> {
    enabled = true
    archiveClassifier.set("")
    archiveFileName.set("map-place-crawler.jar")
}

// 배포 산출물을 실행 가능한 fat jar 하나로 고정한다. (plain jar 미생성)
tasks.named<Jar>("jar") {
    enabled = false
}

tasks.withType<Test> {
    useJUnitPlatform()
}
