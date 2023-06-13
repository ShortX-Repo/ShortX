import tornaco.project.android.shortx.Compose

plugins {
    alias(libs.plugins.agp.lib)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.dagger.hilt.android)
}

android {
    namespace = "github.tornaco.shortx.ui.base"

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
}
dependencies {
    implementation(libs.dagger.hilt.android)
    implementation(libs.dagger.hilt.navigation.compose)
    kapt(libs.dagger.hilt.android.compiler)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)

    implementation(libs.navigation.compose)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.accompanist.placeholder.material)
    implementation(libs.swiperefreshlayout)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.core.ktx)
    // Note: Do not upgrade this lib, ComposeOverlay.
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)

    implementation(libs.browser)


    implementation(libs.io.noties.markwo.core)
    implementation(libs.io.noties.markwo.ext.strikethrough)
    implementation(libs.io.noties.markwo.ext.tables)
    implementation(libs.io.noties.markwo.html)
    implementation(libs.io.noties.markwo.linkify)

    implementation(project(":core"))
    implementation(project(":external:icons"))
}
