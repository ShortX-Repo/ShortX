import tornaco.project.android.shortx.Configs.sxPackageName
import tornaco.project.android.shortx.Configs.sxVersionCode
import tornaco.project.android.shortx.Configs.sxVersionName
import tornaco.project.android.shortx.Configs.sxIsDebugBuild
import tornaco.project.android.shortx.addAidlTask
import java.util.*

plugins {
    id("java")
    id("kotlin")
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
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

addAidlTask()