import tornaco.project.android.shortx.*
import tornaco.project.android.shortx.Compose.bomVersion
import tornaco.project.android.shortx.Configs.resPrefix

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "${Configs.sxPackageName}.ui"

    defaultConfig {
        minSdk = Configs.minSdkVersion
        compileSdk = Configs.compileSdkVersion
        targetSdk = Configs.targetSdkVersion
        testInstrumentationRunner = Configs.testInstrumentationRunner

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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Compose.composeVersion
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    resourcePrefix = resPrefix
}

dependencies {
    implementation(Hilt.library)
    implementation(Hilt.hiltNavigationCompose)
    kapt(Hilt.googleAndroidCompiler)

    implementation(Others.landscapistGlide)
    implementation(Others.glide)
    kapt(Others.glideCompiler)
    annotationProcessor(Others.glideCompiler)

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json-jvm
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.4.1")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")

    implementation("androidx.navigation:navigation-compose:2.5.3")

    implementation("com.google.accompanist:accompanist-navigation-animation:0.28.0")
    implementation("com.google.accompanist:accompanist-placeholder-material:0.28.0")

    implementation("androidx.core:core-splashscreen:1.0.0")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0-alpha02")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Import the Compose BOM
    implementation(platform("androidx.compose:compose-bom:$bomVersion"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.7.1")

    debugImplementation("com.github.chuckerteam.chucker:library:3.5.2")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:3.5.2")

    implementation(project(":binder-retriever"))
    implementation(project(":core"))
    implementation(project(":external:shell"))
    implementation(project(":external:dateformatter"))
    implementation(project(":external:utils"))
    implementation(project(":external:reorderable"))
    implementation(project(":external:compose-color-picker"))

    val rowImplementation by configurations
    rowImplementation(Lottie.composeLottie)
    rowImplementation(Lottie.lottie)

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.3.3")

    debugImplementation("androidx.compose.ui:ui-tooling:1.3.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.3.3")
}