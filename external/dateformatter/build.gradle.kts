plugins {
    id("com.android.library")
}

android {
    defaultConfig {
        minSdk = tornaco.project.android.shortx.Configs.minSdkVersion
        compileSdk = tornaco.project.android.shortx.Configs.compileSdkVersion
        targetSdk = tornaco.project.android.shortx.Configs.targetSdkVersion
        testInstrumentationRunner = tornaco.project.android.shortx.Configs.testInstrumentationRunner

        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = false
        buildConfig = false
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
        viewBinding = false
        dataBinding = false
    }
}

dependencies {
}


