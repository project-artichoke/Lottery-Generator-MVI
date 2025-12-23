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

rootProject.name = "Lottery Generator"

// App module
include(":app")

// Core modules
include(":core:core-common")
include(":core:core-database")
include(":core:core-data")
include(":core:core-domain")
include(":core:core-ui")
include(":core:core-navigation")

// Feature modules
include(":feature:feature-generator")
include(":feature:feature-history")
include(":feature:feature-settings")
