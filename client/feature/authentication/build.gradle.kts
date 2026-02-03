import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.kotlin.compose)
}

val localProperties = loadProperties(rootProject.file("local.properties").toString())

android {
    namespace = "ru.kima.sonar.feature.authentication"
    compileSdk {
        version = release(libs.versions.android.compileSdk.get().toInt())
    }

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        val apiEndpointName = "API_ENDPOINT"
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("String", apiEndpointName, localProperties.getProperty("api.url"))
        }

        debug {
            buildConfigField(
                "String",
                apiEndpointName,
                localProperties.getProperty("api.url.debug")
            )
        }

        buildFeatures {
            buildConfig = true
            compose = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(project(":client:data:application-config"))
    implementation(project(":client:data:home-api"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}