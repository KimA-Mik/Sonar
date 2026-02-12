plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.koin.compiler)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.spring.security.crypto)
    // Source: https://mvnrepository.com/artifact/commons-logging/commons-logging
    implementation(libs.commons.logging)
    // Source: https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk18on
    implementation(libs.bcprov.jdk18on)
    implementation(libs.koin.ktor)

    implementation(project(":common:server-api"))
    implementation(project(":common:util"))
    implementation(project(":server:data:user"))
}