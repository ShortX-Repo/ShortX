plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    compileOnly(files("${rootProject.rootDir.path}/patch-xposed/patch-main/libs/api-82.jar"))

    implementation(project(":core"))
    implementation(libs.mvel2)
}