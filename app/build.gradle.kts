import tornaco.project.android.shortx.Configs
import tornaco.project.android.shortx.Configs.keyStoreAlias
import tornaco.project.android.shortx.Configs.keyStorePassword
import tornaco.project.android.shortx.Flavor
import tornaco.project.android.shortx.log

plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.kover)
    alias(libs.plugins.compose.compiler)
}

log("sxFlavor: ${Configs.sxFlavor}")

if (Configs.sxFlavor == Flavor.ROW) {
    log("Apply Google Services plugin")
    // Add the Google services Gradle plugin
    // The google-services Gradle plugin needs to be applied on a project with com.android.application or com.android.dynamic-feature.
    plugins.apply("com.google.gms.google-services")
    plugins.apply("com.google.firebase.crashlytics")
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
        val noMinify = project.findProperty("noMinify")?.toString().toBoolean()
        log("noMinify: $noMinify")

        val extraAbiFilter =
            project.findProperty("extraAbiFilter")?.toString()?.split(";")?.map { it.trim() }
                ?: emptyList()
        log("extraAbiFilter: $extraAbiFilter")

        getByName("release") {
            isMinifyEnabled = !noMinify
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            ndk {
                abiFilters.addAll(listOf("arm64-v8a"))
                abiFilters.addAll(extraAbiFilter)
            }
        }

        getByName("debug") {
            isMinifyEnabled = !noMinify
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            // Also support x86 for emulator test
            ndk {
                abiFilters.addAll(listOf("arm64-v8a", "x86_64"))
                abiFilters.addAll(extraAbiFilter)
            }
        }
    }

    flavorDimensions.add("market")
    productFlavors {
        create("row") {
            dimension = "market"
            versionNameSuffix = "-row"

            // Support all abi for row.
            ndk {
                abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86_64"))
            }
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

    packaging {
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        resources.pickFirsts.add("google/protobuf/*.proto")
        resources.pickFirsts.add("tornaco.apps.shortx.proto/*.proto")
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
    implementation(libs.material)
    kapt(libs.dagger.hilt.android.compiler)

    implementation(libs.appcompat)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)

    implementation(libs.hiddenapibypass)

    implementation(project(":ui"))
    implementation(project(":services"))
    implementation(project(":patch"))
    implementation(project(":core"))

    implementation(libs.shortx.ext.codeditor)
    implementation(libs.shortx.ui.base)

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







