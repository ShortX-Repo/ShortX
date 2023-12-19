plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    implementation(libs.okhttp)
    implementation("com.squareup.okio:okio:3.7.0")

    implementation(project(":core"))
}