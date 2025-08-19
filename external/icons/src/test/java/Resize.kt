import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class Resize {
    @Test
    fun main() {
        File("../../external/icons/src/main/res/drawable").listFiles().forEach {
            println("$it")
            convertVectorDrawableSize(it.absolutePath, "16dp")
        }
    }

    fun convertVectorDrawableSize(filePath: String, newSize: String) {
        val file = File(filePath)

        // 读取XML文件
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = documentBuilder.parse(file)

        // 获取根元素 (vector 标签)
        val root = document.documentElement

        // 修改 width 和 height 属性
        root.setAttribute("android:width", newSize)
        root.setAttribute("android:height", newSize)

        // 将修改后的内容写回XML文件
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

        val source = DOMSource(document)
        val result =
            StreamResult(File(file.parentFile, file.nameWithoutExtension + "_$newSize.xml"))
        transformer.transform(source, result)

        println("Successfully updated vector drawable size to $newSize $filePath")
    }
}