import tornaco.project.android.shortx.Configs.sxPackageName
import tornaco.project.android.shortx.Configs.sxVersionCode
import tornaco.project.android.shortx.Configs.sxVersionName
import tornaco.project.android.shortx.Configs.sxIsDebugBuild
import tornaco.project.android.shortx.addAidlTask
import java.util.*

plugins {
    id("java")
    id("kotlin")
    id("com.google.protobuf").version("0.9.1")
    // https://github.com/gmazzo/gradle-buildconfig-plugin
    id("com.github.gmazzo.buildconfig") version "3.1.0"
}

buildConfig {
    packageName("${sxPackageName}.core")
    className("BuildProp")
    buildConfigField("String", "APP_PACKAGE_NAME", provider { "\"${sxPackageName}\"" })
    buildConfigField("String", "APP_VERSION_NAME", provider { "\"${sxVersionName}\"" })
    buildConfigField("int", "APP_VERSION_CODE", provider { "$sxVersionCode" })
    buildConfigField("boolean", "APP_IS_DEBUG", provider { "$sxIsDebugBuild" })
    buildConfigField("long", "BUILD_TIME", "${System.currentTimeMillis()}L")
    buildConfigField("String", "BUILD_FINGERPRINT", "\"${UUID.randomUUID()}\"")

    buildConfigField("String", "APP_SHELL_SERVICE_NAME", "\"tornaco.apps.shortx.ui.service.ShellService\"")
    buildConfigField("String", "DEFAULT_OVERLAY_BUTTON_ICON", "\"Settings\"")
    buildConfigField("String", "PLAY_PUBLIC_KEY", "\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmbQreguSE0X2zYOGeKzeNutbc7J5oYNFoOf3EKpYyli11M4/DhZ7+XJeYTMCKfskyF9va3cus2CrNNdgnoj074sVqE1MAPhPrH2t57eblJecb99xTwB071lQTkuj3HLteToCPjZbLN/PZMJ8LBCFraEyXZ8v/tH4yClf9XlTdAGPOU9xCsz9J1Jx/OMgcqBhvXDdKgtTjJ9Q5nvnQWX5Hcf+++LsZdC7xgnsKeEaa+TK39+2oA4659kSVcXptFfoCa/FOp7YF5cWDjazUTdA2LmO0bgnlUweOP0y10E23FcdkBvI+YsqsraxF1EZ+eu0BnnUkUp3a1TmfBDzAswFMQIDAQAB\"")
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    api("com.google.protobuf:protobuf-java:3.17.2")

    implementation(tornaco.project.android.shortx.Coroutines.android)
    implementation(tornaco.project.android.shortx.Coroutines.core)
    implementation(tornaco.project.android.shortx.Coroutines.stdlib)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

addAidlTask()

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.6.1"
    }
}
