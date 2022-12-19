import tornaco.project.android.shortx.Others
import tornaco.project.android.shortx.Compose
import tornaco.project.android.shortx.Configs
import tornaco.project.android.shortx.Configs.resPrefix
import tornaco.project.android.shortx.Hilt
import tornaco.project.android.shortx.log

plugins {
    id("com.android.library")
    id("kotlin-android")
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
            version = "3.18.1"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

}