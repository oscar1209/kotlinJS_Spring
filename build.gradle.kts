
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


val ktorVersion: String = "2.0.0"



plugins {
    //id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.springframework.boot") version "3.2.0" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false

    kotlin("plugin.spring") version "1.9.10" apply false

    kotlin("multiplatform") version "1.9.10"
    kotlin("plugin.serialization").version("1.9.10") apply false
}

group = "dev.kmandalas"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven("com.github.johnrengelman.shadow")
}


kotlin {
    jvm("spring") {
        apply(plugin = "org.springframework.boot")
        apply(plugin = "io.spring.dependency-management")
        apply(plugin = "org.jetbrains.kotlin.plugin.spring")
        apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
        //apply(plugin = "com.github.johnrengelman.shadow")


        withJava()

        tasks.withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs += "-Xjsr305=strict"
                jvmTarget = "17"
            }
        }
        /*
        tasks.withType<ShadowJar> {
            archiveFileName.set("app.jar")

        }

         */
        tasks.withType<Test> {
            useJUnitPlatform()
        }


    }
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("app.softwork:kotlinx-uuid-core:0.0.25")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val springMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation("org.springframework.boot:spring-boot-starter-security")
                implementation("org.springframework.boot:spring-boot-starter-web")
                implementation("org.springframework.boot:spring-boot-starter-aop")
                implementation("org.springframework.boot:spring-boot-starter-jdbc")
                implementation("com.mysql:mysql-connector-j:8.3.0")
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
                implementation("org.jetbrains.kotlin:kotlin-reflect")
                implementation("io.github.serpro69:kotlin-faker:1.7.0")
            }
        }
        val springTest by getting
        val jsMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

                implementation(npm("navigo", "8.11.1"))
            }
        }
        val jsTest by getting
    }
}

