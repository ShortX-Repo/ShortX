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
    compileOnly(files(layout.buildDirectory.file("generated/libxposed-api/libxposed-api.jar")))

    implementation(project(":core-api"))
    implementation(project(":core"))
    implementation(project(":external:utils"))
    implementation(libs.mvel2)
}

tasks.named("compileKotlin") {
    dependsOn(extractLibxposedApi)
}
