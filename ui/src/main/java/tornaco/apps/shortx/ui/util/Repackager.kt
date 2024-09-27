package tornaco.apps.shortx.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.LayerDrawable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat
import com.android.tools.build.apkzlib.zip.AlignmentRules
import com.android.tools.build.apkzlib.zip.ZFile
import com.android.tools.build.apkzlib.zip.ZFileOptions
import com.wind.meditor.core.ManifestEditor
import com.wind.meditor.property.AttributeItem
import com.wind.meditor.property.ModificationProperty
import com.wind.meditor.utils.NodeValue
import tornaco.apps.shortx.core.res.remixResId
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.ui.R
import tornaco.apps.shortx.ui.shortcut.ShortcutStubInfo
import tornaco.apps.shortx.ui.util.ApkSignatureHelper.provideSigningExtension
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File


class Repackager(private val context: Context) {
    private val logger = Logger("Repackager")

    companion object {
        val ICONS = listOf(
            "res/o-.png",
            "res/RJ.png",
            "res/FS.png",
            "res/yn.png",
            "res/9w.png",
        )
    }

    fun patch(apkFile: File, keystoreFile: File, info: ShortcutStubInfo): File {
        val outputFile = File(apkFile.parentFile, "patched-${apkFile.name}")
        runCatching {
            patch(apkFile, outputFile, keystoreFile, info)
        }.onFailure {
            outputFile.delete()
            throw it
        }
        return outputFile
    }

    private fun patch(apkFile: File, outputFile: File, keystoreFile: File, info: ShortcutStubInfo) {
        val dstZFile = ZFile.openReadWrite(
            outputFile, ZFileOptions().setAlignmentRule(
                AlignmentRules.compose(AlignmentRules.constantForSuffix(".so", 4096))
            )
        )

        provideSigningExtension(keystoreFile.inputStream()).register(dstZFile)
        val srcZFile = ZFile.openReadOnly(apkFile)

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

            if (ICONS.contains(name)) {
                val patchedIcon = patchIcon(info)
                if (patchedIcon != null) {
                    logger.d("replace icon: $name")
                    dstZFile.add(name, patchedIcon, false)
                    continue
                }
            }

            dstZFile.add(name, entry.open(), false)
        }
        dstZFile.realign()
        dstZFile.close()
        srcZFile.close()
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
        ).addManifestAttribute(
            AttributeItem(
                NodeValue.UsesSDK.TARGET_SDK_VERSION,
                // Failed parse during installPackageLI: Targeting R+ (version 30 and above) requires the resources.arsc
                // of installed APKs to be stored uncompressed and aligned on a 4-byte boundary

                // WTF? still got this err.
                // Maybe the resource.arsc need to be change.
                29
            )
        )

        info.daId?.let {
            property.addMetaData(ModificationProperty.MetaData("target-da-id", it))
        }

        return ByteArrayOutputStream().apply {
            ManifestEditor(ByteArrayInputStream(data), this, property).processManifest()
            flush()
            close()
        }.toByteArray()
    }

    private fun patchIcon(info: ShortcutStubInfo): ByteArrayInputStream? {
        val actionIconDrawable = ResourcesCompat.getDrawable(
            context.resources,
            context.remixResId(info.appIcon),
            null
        )?.apply {
            setTint(Color(info.appIconTintColor).toArgb())
        }
        val templateDrawable =
            ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ui_ic_shortcut_template,
                null
            )
        val ld = templateDrawable as LayerDrawable
        ld.setTint(Color(info.appIconBgColor).toArgb())
        ld.setDrawableByLayerId(R.id.settings_ic_foreground, actionIconDrawable)

        val bitmap: Bitmap = BitmapUtils.getBitmap(context, templateDrawable) ?: return null
        val bos = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
        return ByteArrayInputStream(bos.toByteArray())
    }
}