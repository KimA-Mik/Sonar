pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Sonar"
include(":client:app")
include(":client:data:application-config")
include(":client:data:home-api")
include(":client:common:ui")
include(":client:feature:authentication")
include(":client:feature:securities")
include(":common:server-api")
include(":server:app")
include(":server:data:market")
