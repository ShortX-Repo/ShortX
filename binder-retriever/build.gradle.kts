import tornaco.project.android.shortx.Configs

plugins {
    alias(libs.plugins.agp.lib)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "tornaco.apps.shortx.app.binder.retriever"

    defaultConfig {
        minSdk = Configs.minSdkVersion
        compileSdk = Configs.compileSdkVersion
        targetSdk = Configs.targetSdkVersion
        testInstrumentationRunner = Configs.testInstrumentationRunner

        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cppFlags("")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = Configs.cmakeVersion
        }
    }
}

dependencies {

}