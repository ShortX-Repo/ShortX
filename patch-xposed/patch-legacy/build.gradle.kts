plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    compileOnly(files("${rootProject.rootDir.path}/patch-xposed/patch-legacy/libs/api-82.jar"))

    implementation(project(":core"))
    implementation(project(":core-api"))
    implementation(project(":patch-xposed:patch-main"))
    implementation(project(":patch-xposed:patch-systemui"))
    implementation(project(":patch-xposed:patch-common"))
}