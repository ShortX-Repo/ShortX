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
include(":ui-base")
include(":ui")
include(":binder-retriever")
include(":core")
include(":services")
include(":services-33")
include(":services-ui")
include(":patch")
include(":patch-magisk:module")
include(":patch-magisk:bridge")
include(":patch-magisk:bridge-dex-app")
include(":patch-magisk:patch-framework")
include(":external:utils")
include(":external:dateformatter")
include(":external:reorderable")
include(":external:picasso")
include(":external:compose-color-picker")
include(":external:icons")
include(":external:sec_net")
include(":prebuilts-gen")
