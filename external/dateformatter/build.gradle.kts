plugins {
    alias(libs.plugins.agp.lib)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "github.tornaco.shortx.android.third.party.date.formatter"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
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