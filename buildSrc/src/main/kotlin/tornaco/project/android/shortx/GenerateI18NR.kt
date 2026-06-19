package tornaco.project.android.shortx

import org.gradle.api.Project
import java.io.File

private const val taskName = "generateI18NR"

private fun String.toConstantName(): String {
    return replace('.', '_').replace('-', '_').replace(':', '_').uppercase()
}

fun Project.addGenerateI18NRTask() {
    val i18nDir = File(rootDir, "ui/src/main/assets/i18n/en-US")
    val outputDir = File(project.buildDir, "generated/i18n")
    val outputFile = File(outputDir, "tornaco/apps/shortx/core/i18n/I18N_R.kt")

    tasks.register(taskName) {
        group = "i18n"
        inputs.dir(i18nDir)
        outputs.file(outputFile)

        doLast {
            val keys = mutableSetOf<String>()
            i18nDir.listFiles { f -> f.extension == "json" }
                ?.sorted()
                ?.forEach { file ->
                    log("Reading i18n keys from: ${file.name}")
                    val content = file.readText()
                    val regex = Regex(""""([^"]+)"\s*:""")
                    regex.findAll(content).forEach { match ->
                        keys.add(match.groupValues[1])
                    }
                }

            log("Total unique keys: ${keys.size}")

            val sortedKeys = keys.sorted()
            val sb = StringBuilder()
            sb.appendLine("@file:Suppress(\"unused\", \"ConstPropertyName\")")
            sb.appendLine()
            sb.appendLine("package tornaco.apps.shortx.core.i18n")
            sb.appendLine()
            sb.appendLine("object I18N_R {")
            sortedKeys.forEach { key ->
                val constName = key.toConstantName()
                sb.appendLine("    const val $constName = \"$key\"")
            }
            sb.appendLine("}")
            sb.appendLine()

            outputFile.parentFile.mkdirs()
            outputFile.writeText(sb.toString())
            log("Generated I18N_R.kt with ${sortedKeys.size} constants")
        }
    }

    sourceSets.named("main") {
        java {
            srcDir(outputDir)
        }
    }

    tasks.named("compileKotlin") {
        dependsOn(taskName)
    }
}

private val Project.sourceSets: org.gradle.api.tasks.SourceSetContainer
    get() = extensions.getByType(org.gradle.api.tasks.SourceSetContainer::class.java)
