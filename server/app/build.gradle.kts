import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
}

val file: File = rootProject.file("local.properties")
val localProperties = if (file.exists()) loadProperties(file.toString())
else null

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

tasks.shadowJar {
    enabled = localProperties?.getProperty("shadow.disable")?.toBooleanStrictOrNull() ?: true
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    mergeServiceFiles() // https://github.com/grpc/grpc-java/issues/10853
    manifest {
        attributes["Main-Class"] = "ru.kima.sonar.server.MainKt"
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
    implementation(libs.ktor.network.tls.certificates)
    //TODO: Remove logback and use ktor's logging with slf4j and log4j
    implementation(libs.logback.classic)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.koin.ktor)

    implementation(project(":common:server-api"))
    implementation(project(":server:data:market"))
    implementation(project(":server:data:user"))
    implementation(project(":server:feature:auth"))
    implementation(project(":server:feature:portfolios"))
    implementation(project(":server:feature:securities"))
}
