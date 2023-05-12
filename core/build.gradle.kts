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
    buildConfigField("String", "OPEN_SOURCE_LICENSE_URL", "\"https://github.com/Tornaco/ShortX-Release/blob/main/open_source_license.txt\"")
    buildConfigField("String", "PLAY_PUBLIC_KEY", "\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmqDRzFFEKnhoclz6m0+9pkN8M/tokdZ6adHQhN9QXEwSy+w0OM4UZQwUOmmn9JvQd9/ibiGZbPK0Phmazvt3dVqgLYgOm0Z+qQFc2TnJ2SsxnjxilEt9HQvGLACH1OpNVxB3GgsXRyLPc4SqfUGtb9Wlh+qz4L01yLFhsq/AnSHQlQj37I+xYHUv0pZOSlkx7ORXJsTufXcEsL4Yc9dLHh7wEBWJqwuhQVT/BOIr1yipK1a3adzEnX6Iv1+yDmQy/EIfIt6jOJowOHMHW8MvtQ71c1WvuYfE4biS3xr2pybRxfipBEJBcdMdU+yMrsBzhp++QpGOxrfQitasmRB4twIDAQAB\"")

    buildConfigField("String", "API_BASE_URL", "\"http://shortx.emui.tech/api/\"")
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    api("com.google.protobuf:protobuf-java:3.17.2")
    // https://mvnrepository.com/artifact/com.google.protobuf/protobuf-java-util
    api("com.google.protobuf:protobuf-java-util:3.22.3")

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
