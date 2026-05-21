plugins {
    alias(libs.plugins.kotlin.jvm)
}

val libxposedApiAar by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val extractLibxposedApi by tasks.registering(Copy::class) {
    from({ zipTree(libxposedApiAar.singleFile) }) {
        include("classes.jar")
    }
    into(layout.buildDirectory.dir("generated/libxposed-api"))
    rename("classes.jar", "libxposed-api.jar")
}

dependencies {
    libxposedApiAar(libs.libxposed.api)

    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    compileOnly(files("${rootProject.rootDir.path}/services-ui/jar/classes.jar"))
    compileOnly(files("${rootProject.rootDir.path}/services-jni/jar/classes.jar"))
    compileOnly(files(layout.buildDirectory.file("generated/libxposed-api/libxposed-api.jar")))

    implementation(project(":core"))
    implementation(project(":core-api"))
    implementation(project(":patch-xposed:patch-common"))

    implementation(project(":external:dex-maker"))
    implementation(project(":external:utils"))

    testImplementation(project(":services"))

    implementation(libs.mvel2)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.reflect)
}

tasks.named("compileKotlin") {
    dependsOn(extractLibxposedApi)
}
