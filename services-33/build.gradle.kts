plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/33/android-33.jar"))

    implementation(project(":core"))
    implementation(project(":external:utils"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.stdlib.jdk8)
}
