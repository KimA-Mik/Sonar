import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    implementation(libs.clikt)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.websockets)
    //TODO: Remove logback and use ktor's logging with slf4j and log4j
    implementation(libs.logback.classic)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.koin.ktor)

    implementation(project(":common:server-api"))
    implementation(project(":server:data:market"))
    implementation(project(":server:data:user"))
    implementation(project(":server:feature:auth"))
    implementation(project(":server:feature:securities"))
}
