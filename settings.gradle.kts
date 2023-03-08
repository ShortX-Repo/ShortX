@file:Suppress("UnstableApiUsage")


rootProject.name = "ShortX"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
@Suppress("JcenterRepositoryObsolete") dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        jcenter()
        maven(url = "https://jitpack.io")
    }
}

include(":app")
include(":ui")
include(":binder-retriever")
include(":core")
include(":services")
include(":services-33")
include(":patch")
include(":patch-magisk:module")
include(":patch-magisk:bridge")
include(":patch-magisk:bridge-dex-app")
include(":patch-magisk:patch-framework")
include(":external:utils")
include(":external:shell")
include(":external:dateformatter")
include(":external:reorderable")
include(":prebuilts-gen")
