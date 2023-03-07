import tornaco.project.android.shortx.Configs.sxVersionCode
import tornaco.project.android.shortx.Configs.sxVersionName
import tornaco.project.android.shortx.Configs.outDir
import tornaco.project.android.shortx.log

buildscript {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        classpath(tornaco.project.android.shortx.ClassPaths.gradlePlugin)
        classpath(tornaco.project.android.shortx.ClassPaths.kotlinPlugin)
        classpath(tornaco.project.android.shortx.Hilt.gradlePlugin)
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

sxVersionCode = androidGitVersion.code()
sxVersionName = androidGitVersion.name()

subprojects {
    log("subprojects: ${this.name}")

    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(
            arrayOf(
                "-Xmaxerrs",
                "1000"))
        options.encoding = "UTF-8"
    }

    buildDir = file("${outDir}${path.replace(":", File.separator)}")
}

rootProject.buildDir = outDir
tasks["clean"].doLast {
    delete(outDir)
}
