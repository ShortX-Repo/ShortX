import tornaco.project.android.shortx.Compose
import tornaco.project.android.shortx.Configs.resPrefix

plugins {
    alias(libs.plugins.agp.lib)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "github.tornaco.shortx.android.third.party.recorderable"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        compose = true
        buildConfig = false
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
        viewBinding = false
        dataBinding = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Compose.composeVersion
    }

    resourcePrefix = resPrefix
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.util)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)
}