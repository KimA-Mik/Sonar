import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.serialization)
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
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.websockets)
    implementation(libs.koin.ktor)

    implementation(project(":common:server-api"))
    implementation(project(":common:util"))
    implementation(project(":server:data:user"))
    implementation(project(":server:data:market"))
    implementation(project(":server:feature:auth"))
}