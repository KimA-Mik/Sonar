package ru.kima.sonar.server.feature.portfolios.service.core

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.Application
import java.io.FileInputStream


fun Application.initializeFirebase(projectId: String, credentialsPath: String) {
    val serviceAccount = FileInputStream(credentialsPath)
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setProjectId(projectId)
        .build()

    FirebaseApp.initializeApp(options)
}