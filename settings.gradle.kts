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
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // Или FAIL_ON_PROJECT_REPOS, если строго запрещаете репозитории в модулях
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // Добавлено для поддержки возможных внешних библиотек
    }
}

rootProject.name = "studycard"
include(":app")
