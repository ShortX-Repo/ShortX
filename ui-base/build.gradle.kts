plugins {
    alias(libs.plugins.agp.lib)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.compose.compiler)
    id("shortxmvnpub")
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

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(libs.dagger.hilt.android)
    implementation(libs.dagger.hilt.navigation.compose)
    kapt(libs.dagger.hilt.android.compiler)

    implementation(libs.glide.landscapist)
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    implementation(libs.okhttp3.integration)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons)

    implementation(libs.navigation.compose)
    implementation(libs.accompanist.placeholder.material)
    implementation(libs.accompanist.systemuicontroller)

    implementation(libs.datastore.preferences)

    implementation(libs.swiperefreshlayout)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.core.ktx)
    // Note: Do not upgrade this lib, ComposeOverlay.
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)

    implementation(libs.browser)

    implementation(libs.codeview)
    implementation(libs.tinypinyin)

    implementation(libs.compose.markdown)

    implementation(project(":core"))
    implementation(project(":core-api"))
    implementation(project(":external:icons"))
    implementation(project(":external:dateformatter"))
    implementation(project(":external:picasso"))
    implementation(project(":external:reorderable"))
}
