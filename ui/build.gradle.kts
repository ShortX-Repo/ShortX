import tornaco.project.android.shortx.Configs
import tornaco.project.android.shortx.Configs.resPrefix
import tornaco.project.android.shortx.addI18NTask

plugins {
    alias(libs.plugins.agp.lib)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.compose.compiler)
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

    resourcePrefix = resPrefix
}

dependencies {
    implementation(files("lib/apkzlib.jar"))
    implementation(files("lib/ManifestEditor-1.0.2.jar"))

    implementation(libs.dagger.hilt.android)
    implementation(libs.dagger.hilt.navigation.compose)
    kapt(libs.dagger.hilt.android.compiler)

    implementation(libs.glide.landscapist)
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    implementation(libs.kotlinx.serialization.json.jvm)
    implementation(libs.kotlin.reflect)

    implementation(libs.navigation.compose)
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
    implementation(libs.androidx.biometric)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.viewbinding)
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

    implementation(libs.orbit.compose)
    implementation(libs.orbit.viewmodel)

    implementation(libs.tinypinyin)

    implementation(project(":binder-retriever"))
    implementation(project(":core"))
    implementation(project(":services-ui"))
    implementation(project(":external:sec_net"))

    implementation(libs.shortx.ui.base)
    implementation(libs.shortx.ext.compose.color.picker)
    implementation(libs.shortx.ext.reorderable)
    implementation(libs.shortx.ext.dateformatter)
    implementation(libs.shortx.ext.utils)
    implementation(libs.shortx.ext.codeditor)
    implementation(libs.shortx.ext.icons)
    implementation(libs.shortx.ext.dex.maker)

    implementation(libs.mmkv)
    implementation(libs.jotp)

    implementation(libs.sardine.android)

    implementation(libs.apksig)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.orbit.test)
    testImplementation(libs.androidx.test.runner)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
}

configurations.all {
    exclude(group = "javax.annotation", module = "javax.annotation-api")
}

addI18NTask()