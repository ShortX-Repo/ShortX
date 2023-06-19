package tornaco.project.android.shortx

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.the
import java.io.File

private const val taskName = "generateAidlFiles"

fun Project.addAidlTask() {
    tasks.register(taskName) {
        group = "idl"
        doLast {
            val projectPrebuiltAndroidSdkDir = File(rootProject.projectDir, "android-sdk")
            val javaSrcDirs = project.the<SourceSetContainer>()["main"].java.srcDirs
            println("aidlTask, javaSrcDirs: $javaSrcDirs")

            println("aidl executable file: ${aidl()}, is it exists? ${File(aidl()).exists()}")

            javaSrcDirs.forEach { srcDir ->
                val tree = fileTree(srcDir)
                tree.forEach { file ->
                    if (file.name.endsWith(".aidl")) {
                        val aidlPath = file.path
                        val commands = arrayOf(
                            aidl(),
                            "-I$srcDir",
                            "-p$projectPrebuiltAndroidSdkDir${File.separator}framework.aidl",
                            "-p$projectPrebuiltAndroidSdkDir${File.separator}shortx.aidl",
                            "-p$projectPrebuiltAndroidSdkDir${File.separator}thanos.aidl",
                            aidlPath
                        )
                        println("command: ${commands.map { it }}")
                        exec {
                            commandLine(
                                aidl(),
                                "-I$srcDir",
                                "-p$projectPrebuiltAndroidSdkDir${File.separator}framework.aidl",
                                "-p$projectPrebuiltAndroidSdkDir${File.separator}shortx.aidl",
                                "-p$projectPrebuiltAndroidSdkDir${File.separator}thanos.aidl",
                                aidlPath
                            )
                        }.apply {
                            println("Result: ${this}")
                        }
                    }
                }
            }
        }
    }

    tasks.named("compileJava") {
        dependsOn(taskName)
    }
    tasks.named("build") {
        dependsOn(taskName)
    }
}

private fun Project.aidl() = buildTools("aidl")