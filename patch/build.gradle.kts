plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    compileOnly(libs.xposed.api)

    implementation(project(":services"))
    implementation(project(":core"))
    implementation(libs.shortx.ext.dex.maker)

    implementation(libs.mvel2)
}