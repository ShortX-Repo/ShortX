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
include(":services-29")
include(":services-28")
include(":services-30")
include(":services-31")
include(":services-32")
include(":services-33")
include(":services-34")
include(":services-ui")
include(":shortcut-stub")
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
include(":external:dex-maker")
include(":external:rhino-android")
include(":external:drawbox")
include(":external:codeditor")
include(":prebuilts-gen")
include(":flutter:shortx_f")
