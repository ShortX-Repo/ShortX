buildscript {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        classpath(tornaco.project.android.shortx.ClassPaths.gradlePlugin)
        classpath(tornaco.project.android.shortx.ClassPaths.kotlinPlugin)
    }
}

plugins {
    id("com.diffplug.spotless").version("5.7.0")
    id("com.gladed.androidgitversion").version("0.4.10")
    id("shortx")
}

androidGitVersion {
    prefix = "v"
    codeFormat = "MNPBB"
    baseCode = 1
}