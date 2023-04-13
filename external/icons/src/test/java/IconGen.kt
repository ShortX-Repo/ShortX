import com.android.ide.common.vectordrawable.Svg2Vector
import org.junit.Test
import java.io.File

class IconGen {
    private val sourceFile = File("src/main/java/github/tornaco/shortx/android/icon/Remix.kt")

    @Test
    fun gen() {
        exportIcons()
    }

    private fun exportIcons() {
        val remixIconPath = "../../RemixIcon/icons"
        val remixIconDir = File(remixIconPath)

        val iconMap = mutableMapOf<String, MutableList<String>>()

        remixIconDir.listFiles()?.forEach { file ->
            println(file)
            val categoryName = file.name.replace("&", "And").replace(" ", "")
            file.listFiles()?.forEach { iconFile ->
                val androidResFileName =
                    "ic_remix_" + iconFile.nameWithoutExtension.replace("-", "_")
                println(categoryName to androidResFileName)

                if (iconMap[categoryName] == null) {
                    iconMap[categoryName] = mutableListOf()
                }
                iconMap[categoryName]?.add(androidResFileName)

                File(
                    "src/main/res/drawable/$androidResFileName.xml"
                ).outputStream().use {
                    Svg2Vector.parseSvgToXml(iconFile, it)
                }
            }
        }

        val kotlinFileCode: String = """
        package github.tornaco.shortx.android.icon
        
        import github.tornaco.shortx.android.third.party.icons.R

        object Remix {
        
           
            ${
            iconMap.map { entry ->
                """
                    
            object ${entry.key} {
                
                 ${
                    entry.value.joinToString("\n") {
                        "val ${
                            it.replace("ic_remix_", "").addPrefixIfStartWithDigit()
                        } =  \"${it.replace("ic_remix_", "").replace("_", "-")}\""
                    }
                }
            }
                    
                    
                    
                    
            val remixIcons${entry.key} = mapOf(
            ${
                    entry.value.joinToString(",\n") {
                        "${entry.key}.${
                            it.replace("ic_remix_", "").addPrefixIfStartWithDigit()
                        } to R.drawable.$it"
                    }
                })
            """.trimIndent()
            }.joinToString("\n\n") { it }
        }
        
        
        val all = mapOf(
                ${iconMap.map { "\"${it.key}\" to remixIcons${it.key}" }.joinToString(",\n")}
            )
        }
    """.trimIndent()

        println(kotlinFileCode)

        sourceFile
            .apply {
                parentFile?.mkdirs()
            }
            .writeText(
                kotlinFileCode
            )
    }

}

private fun String.addPrefixIfStartWithDigit(): String {
    return if (get(0).isDigit()) return "_$this" else this
}