// settings.gradle.kts
// Islamic Hub Native Android — project-level Gradle settings
// প্রোজেক্ট-লেভেল Gradle কনফিগ

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "IslamicHubNative"
include(":app")
