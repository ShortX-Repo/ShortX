@file:Suppress("UnstableApiUsage")

import java.io.FileInputStream
import java.util.Properties


rootProject.name = "ShortX"

val githubProperties = Properties()
val githubPropFile = File(rootProject.projectDir, "github.properties")
println("githubPropFile: $githubPropFile")
if (githubPropFile.exists()) {
    githubProperties.load(FileInputStream(githubPropFile))
}

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
        maven(url = "https://maven.aliyun.com/repository/public")
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ShortX-Repo/ShortX-Core")
            credentials {
                username = (githubProperties["gpr.usr"] ?: System.getenv("GPR_USER")).toString()
                password = (githubProperties["gpr.key"] ?: System.getenv("GPR_API_KEY")).toString()
            }
        }
    }
}

include(":app")
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
include(":services-35")
include(":services-ui")
include(":patch")
include(":patch-magisk:module")
include(":patch-magisk:bridge")
include(":patch-magisk:bridge-dex-app")
include(":patch-magisk:patch-framework")
include(":external:sec_net")
include(":prebuilts-gen")