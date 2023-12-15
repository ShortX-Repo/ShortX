import tornaco.project.android.shortx.Configs
import tornaco.project.android.shortx.Configs.keyStoreAlias
import tornaco.project.android.shortx.Configs.keyStorePassword
import tornaco.project.android.shortx.log

plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.kover)
}

android {
    namespace = Configs.sxPackageName

    signingConfigs {
        create("release") {
            Configs.KeyStorePath.also {
                storeFile = rootProject.file(it)
                storePassword = keyStorePassword()
                keyAlias = keyStoreAlias()
                keyPassword = keyStorePassword()
            }
        }
    }

    buildTypes {
        val noMinify = project.findProperty("no-minify")?.toString().toBoolean()
        log("noMinify: $noMinify")

        getByName("release") {
            isMinifyEnabled = !noMinify
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            isMinifyEnabled = !noMinify
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    flavorDimensions.add("market")
    productFlavors {
        create("row") {
            dimension = "market"
            versionNameSuffix = "-row"
        }
        create("prc") {
            dimension = "market"
            versionNameSuffix = "-prc"
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
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }

    applicationVariants.all {
        outputs.all {
            val impl =
                this as com.android.build.gradle.internal.api.ApkVariantOutputImpl
            log("impl.outputFileName changed to:${impl.outputFileName}")
        }
    }

    defaultConfig {
        setProperty("archivesBaseName", "shortx-${Configs.sxVersionName}(${Configs.sxVersionCode})")
    }
}

dependencies {
    implementation(libs.dagger.hilt.android)
    implementation(libs.dagger.hilt.navigation.compose)
    kapt(libs.dagger.hilt.android.compiler)

    implementation(libs.appcompat)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)

    implementation(libs.hiddenapibypass)

    implementation(project(":ui"))
    implementation(project(":ui-base"))
    implementation(project(":services"))
    implementation(project(":patch"))
    implementation(project(":core"))
    implementation(project(":external:utils"))
    implementation(project(":external:codeditor"))

    debugImplementation(project(":services-ui"))

    kover(project(":core"))
    kover(project(":services"))

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotest.assertions.core)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.uiautomator)
    androidTestImplementation(libs.kotest.assertions.core)
}







