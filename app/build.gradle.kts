import tornaco.project.android.shortx.Configs
import tornaco.project.android.shortx.Configs.keyStoreAlias
import tornaco.project.android.shortx.Configs.keyStorePassword
import tornaco.project.android.shortx.Flavor
import tornaco.project.android.shortx.log
import kotlin.random.Random

plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.dagger.hilt.android)
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "21"
    }

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
                "proguard-rules.pro",
                "proguard-rules-dynamic.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            ndk {
                abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86_64"))
                abiFilters.addAll(extraAbiFilter)
            }
        }

        getByName("debug") {
            isMinifyEnabled = !noMinify
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "proguard-rules-dynamic.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            // Also support x86 for emulator test
            ndk {
                abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86_64"))
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
    ksp(libs.dagger.hilt.android.compiler)

    implementation(libs.appcompat)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)

    implementation(libs.hiddenapibypass)
    compileOnly(libs.libxposed.api)

    implementation(project(":ui"))
    implementation(project(":services"))
    implementation(project(":patch-xposed:patch-main"))
    implementation(project(":core"))
    implementation(project(":core-api"))

    implementation(project(":external:codeditor"))
    implementation(project(":external:utils"))
    implementation(project(":ui-base"))

    debugImplementation(project(":services-ui"))
    // Demo to test
    debugImplementation(libs.luckycatlabs.sunrisesunsetcalculator)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotest.assertions.core)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.uiautomator)
    androidTestImplementation(libs.kotest.assertions.core)

    coreLibraryDesugaring(libs.desugar.jdk.libs)
}


val generateProguardRules by tasks.registering {
    doLast {
        val ruleFile = file("proguard-rules-dynamic.pro")
        ruleFile.writeText("")

        ruleFile.apply {
            appendText("-repackageclasses ${generateRandomPackageName()}")
            appendText(System.lineSeparator())
        }

        ruleFile.appendText(System.lineSeparator())
        ruleFile.appendText("""
            -obfuscationdictionary proguard_dict.txt
            -classobfuscationdictionary proguard_dict.txt
            -packageobfuscationdictionary proguard_dict.txt
        """.trimIndent())
    }
}

fun generateRandomPackageName(): String {
    val packageParts = mutableListOf<String>()
    val partCount = Random.nextInt(2, 5)

    for (i in 1..partCount) {
        val partLength = Random.nextInt(3, 8)
        val part = (1..partLength)
            .map { ('a'..'z').random() } // 随机生成小写字母
            .joinToString("")
        packageParts.add(part)
    }

    return packageParts.joinToString(".")
}

tasks.named("preBuild") {
    dependsOn(generateProguardRules)
}






