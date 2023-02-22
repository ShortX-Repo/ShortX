import tornaco.project.android.shortx.Coroutines

plugins {
    id("java")
    id("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/33/android-33.jar"))

    implementation(project(":core"))
    implementation(project(":external:utils"))

    implementation(Coroutines.android)
    implementation(Coroutines.core)
    implementation(Coroutines.stdlib)
}
