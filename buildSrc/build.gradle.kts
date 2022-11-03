repositories {
    mavenCentral()
    google()
}

plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("shortx") {
            id = "shortx"
            implementationClass = "tornaco.project.android.shortx.ShortXBuildPlugin"
        }
    }
}
