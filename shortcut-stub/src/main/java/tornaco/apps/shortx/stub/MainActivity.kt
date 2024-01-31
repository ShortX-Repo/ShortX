package tornaco.apps.shortx.stub

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.util.ApkUtils
import tornaco.apps.shortx.core.util.PkgUtils

class MainActivity : Activity() {
    companion object {
        const val META_DATA_TARGET_DA_ID = "target-da-id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching {
            val targetDAId: String? =
                PkgUtils.getApplicationInfo(this, packageName)?.metaData?.getString(
                    META_DATA_TARGET_DA_ID
                )
            val da = targetDAId?.let {
                ShortXManagerNative.shortX?.getDirectActionById(targetDAId)
            }
            if (da != null) {
                val appLabel = ApkUtils.loadNameByPkgName(this, packageName) ?: packageName
                val evaluateContext = EvaluateContext.newBuilder()
                    .setSrcId(targetDAId)
                    .setSrcEvent(ContextSrcEvent.ContextSrc_DirectAction)
                    .setSrcTitle(appLabel.toString())
                    .build()
                ShortXManagerNative.shortX?.executeDirectionActionById(
                    ByteArrayWrapper(
                        evaluateContext.toByteArray()
                    ), targetDAId
                )
            } else {
                ShortXManagerNative.shortX?.shortcutAppLaunched(packageName)
            }

        }.onFailure {
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
            ShortXManagerNative.shortX?.log("shortcut.stub ${it.stackTraceToString()}")
        }
        finish()
    }
}