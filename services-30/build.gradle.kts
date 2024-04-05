plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/30/android-30.jar"))

    implementation(project(":core"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.stdlib.jdk8)
}
