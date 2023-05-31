plugins {
    alias(libs.plugins.kotlin.jvm)
    id("application")
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))

    implementation(project(":core"))
    api(libs.protobuf.java)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.stdlib.jdk8)
}

application {
    mainClass.set("app.shortx.prebuilts_gen.AppKt")
}