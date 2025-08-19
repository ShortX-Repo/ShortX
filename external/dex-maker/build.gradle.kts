plugins {
    id("java")
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/34/android-34.jar"))
    implementation(project(":core-api"))
    implementation(libs.dalvik.dx)
}