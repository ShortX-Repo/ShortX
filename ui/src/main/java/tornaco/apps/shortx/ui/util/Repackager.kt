package tornaco.apps.shortx.ui.util

import android.content.Context
import com.android.tools.build.apkzlib.zip.AlignmentRules
import com.android.tools.build.apkzlib.zip.ZFile
import com.android.tools.build.apkzlib.zip.ZFileOptions
import com.wind.meditor.core.ManifestEditor
import com.wind.meditor.property.AttributeItem
import com.wind.meditor.property.ModificationProperty
import com.wind.meditor.utils.NodeValue
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.shortcut.ShortcutStubInfo
import tornaco.apps.shortx.ui.util.ApkSignatureHelper.provideSigningExtension
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

class Repackager(private val context: Context) {
    private val logger = Logger("Repackager")

    fun patch(apkFile: File, info: ShortcutStubInfo): File {
        val outputFile = File(apkFile.parentFile, "patched-${apkFile.name}")
        runCatching {
            patch(apkFile, outputFile, info)
        }.onFailure {
            outputFile.delete()
            throw it
        }
        return outputFile
    }

    fun patch(apkFile: File, outputFile: File, info: ShortcutStubInfo) {
        val dstZFile = ZFile.openReadWrite(
            outputFile, ZFileOptions().setAlignmentRule(
                AlignmentRules.compose(AlignmentRules.constantForSuffix(".so", 4096))
            )
        )
        provideSigningExtension(context.assets.open("apk/keystore")).register(dstZFile)
        val srcZFile = ZFile.openReadOnly(apkFile)
        val dexFiles = mutableListOf<File>()

        for (entry in srcZFile.entries()) {
            val name = entry.centralDirectoryHeader.name
            logger.d("add entry: $name")

            if (name.startsWith("AndroidManifest.xml")) {
                dstZFile.add(
                    name, ByteArrayInputStream(
                        patchManifest(entry.read(), info)
                    ), false
                )
                continue
            }

            dstZFile.add(name, entry.open(), false)
        }
        dstZFile.realign()
        dstZFile.close()
        srcZFile.close()
        dexFiles.forEach { it.delete() }
    }

    private fun patchManifest(data: ByteArray, info: ShortcutStubInfo): ByteArray {
        val property = ModificationProperty()

        property.addManifestAttribute(
            AttributeItem(
                NodeValue.Manifest.PACKAGE,
                info.appPkgName
            ).apply {
                type = 3
                namespace = null
            }).addApplicationAttribute(
            AttributeItem(
                NodeValue.Application.LABEL,
                info.appLabel
            )
        )

        return ByteArrayOutputStream().apply {
            ManifestEditor(ByteArrayInputStream(data), this, property).processManifest()
            flush()
            close()
        }.toByteArray()
    }
}