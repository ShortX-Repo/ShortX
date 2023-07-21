import tornaco.project.android.shortx.Compose
import tornaco.project.android.shortx.Configs
import tornaco.project.android.shortx.Configs.resPrefix

plugins {
    alias(libs.plugins.agp.lib)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.dagger.hilt.android)
}

android {
    namespace = "${Configs.sxPackageName}.ui"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    setPublishNonDefault(true)
    flavorDimensions.add("market")
    productFlavors {
        create("row") {
            dimension = "market"
        }
        create("prc") {
            dimension = "market"
        }
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
    implementation(libs.dagger.hilt.android)
    implementation(libs.dagger.hilt.navigation.compose)
    kapt(libs.dagger.hilt.android.compiler)

    implementation(libs.glide.landscapist)
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    implementation(libs.kotlinx.serialization.json.jvm)
    implementation(libs.kotlin.reflect)

    implementation(libs.navigation.compose)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.accompanist.placeholder.material)

    implementation(libs.core.splashscreen)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.core.ktx)
    // Note: Do not upgrade this lib, ComposeOverlay.
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.swiperefreshlayout)
    implementation(libs.datastore.preferences)
    implementation(libs.browser)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.gson)

    implementation(libs.chucker)

    implementation(libs.codeview)

    // https://github.com/LibChecker/LibChecker-Rules-Bundle
    implementation(libs.libchecker.rules.bundle)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    implementation(libs.lottie.compose)
    implementation(libs.lottie)

    implementation(project(":binder-retriever"))
    implementation(project(":ui-base"))
    implementation(project(":core"))
    implementation(project(":external:dateformatter"))
    implementation(project(":external:utils"))
    implementation(project(":external:reorderable"))
    implementation(project(":external:compose-color-picker"))
    implementation(project(":external:icons"))
    implementation(project(":external:sec_net"))

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}

configurations.all {
    exclude(group = "javax.annotation", module = "javax.annotation-api")
}
