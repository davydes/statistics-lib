plugins {
    kotlin("jvm") version "1.3.61"
}

group = "ru.liptsoft"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit:junit:4.12")
    testImplementation("org.assertj:assertj-core:3.14.0")
    testImplementation("com.nhaarman:mockito-kotlin:1.6.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}