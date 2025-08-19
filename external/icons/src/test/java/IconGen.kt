import com.android.ide.common.vectordrawable.Svg2Vector
import org.junit.Test
import java.io.File

class IconGen {
    private val sourceFile = File("../../core/src/main/java/tornaco/apps/shortx/core/res/Remix.kt")

    @Test
    fun gen() {
        exportIcons()
    }

    private fun exportIcons() {
        val remixIconPath = "../../../RemixIcon/icons"
        val fixedIconPath = "../../../RemixIcon/fixed-icons"
        val remixIconDir = File(remixIconPath)
        println(remixIconDir.canonicalPath)

        val iconMap = mutableMapOf<String, MutableList<String>>()

        remixIconDir.listFiles()?.forEach { file ->
            val categoryName = file.name.replace("&", "And").replace(" ", "")
            file.listFiles()?.forEach { iconFile ->
                val fixedIconFile = File(fixedIconPath, iconFile.name)
                println("fixedIconFile: ${fixedIconFile.canonicalPath}")

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
                    val preferredFile = if (fixedIconFile.exists()) fixedIconFile else iconFile
                    Svg2Vector.parseSvgToXml(preferredFile.toPath(), it)
                }
            }
        }


        // Write entry kt.
        iconMap.forEach { entry ->
            val code = """            
            package tornaco.apps.shortx.core.res
                
            object Remix_${entry.key} {
                
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
                    "Remix_${entry.key}.${
                        it.replace("ic_remix_", "").addPrefixIfStartWithDigit()
                    } to \"$it\""
                }
            })
                
                
            """.trimIndent()

            File(sourceFile.parentFile, "${entry.key}.kt").writeText(
                code
            )
        }

        // Write Remix.kt
        val kotlinFileCode: String = """
        package tornaco.apps.shortx.core.res
        
       
        object Remix {
        
         ${
            iconMap.map { entry ->
                "val ${entry.key} get() = Remix_${entry.key}"
            }.joinToString(System.lineSeparator())
        }
        
        
        val all = mapOf(
                ${iconMap.map { "\"${it.key}\" to remixIcons${it.key}" }.joinToString(",\n")}
            )
            
            
        val allFlat = Remix.all.values.flatMap { it.entries }.associate { it.key to it.value }


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