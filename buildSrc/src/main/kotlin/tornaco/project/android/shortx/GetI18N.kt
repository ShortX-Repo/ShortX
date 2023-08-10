package tornaco.project.android.shortx

import org.gradle.api.Project
import java.io.File
import java.net.URL


private const val taskName = "getI18nFromGithub"
private const val i18NBaseUrl = "https://raw.githubusercontent.com/ShortX-Repo/ShortX-i18n/main"
private val allFiles = arrayOf(
    "i18n-en-US.json",
    "i18n-zh-CN.json",
    "i18n-zh-TW.json",
)

fun Project.addI18NTask() {
    tasks.register(taskName) {
        group = "i18n"
        doLast {
            allFiles.forEach {
                val url = "$i18NBaseUrl/$it"
                val file = File("${project.rootDir}", "ui/src/main/assets/$it")
                log("Download from: $url")
                file.delete()
                file.writeText(URL(url).readText())
                log("Write to: $file")
            }
        }
    }

    tasks.named("build") {
        dependsOn(taskName)
    }
}