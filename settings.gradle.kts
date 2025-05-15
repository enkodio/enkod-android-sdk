pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven ("https://developer.huawei.com/repo/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven ("https://jitpack.io")
        maven ("https://developer.huawei.com/repo/")
    }
}

rootProject.name = "androidsdk"
include(":app")
include(":androidsdk")