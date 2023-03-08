plugins {
    id("java")
    id("kotlin")
    id("application")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))

    implementation(project(":core"))
    implementation("com.google.protobuf:protobuf-java:3.17.2")

    implementation(tornaco.project.android.shortx.Coroutines.core)
    implementation(tornaco.project.android.shortx.Coroutines.stdlib)
}

application {
    mainClass.set("app.shortx.prebuilts_gen.AppKt")
}