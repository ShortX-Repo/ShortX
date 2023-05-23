package tornaco.apps.shortx.core

import android.content.Intent
import android.net.Uri
import tornaco.apps.shortx.core.proto.common.AndroidIntent
import tornaco.apps.shortx.core.proto.common.AndroidIntentExtraType

fun AndroidIntent.toIntent() = Intent().apply {
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this@toIntent.action?.let { action = it }
    this@toIntent.pkgName?.let { pkgName ->
        `package` = pkgName
        this@toIntent.className?.let { setClassName(pkgName, it) }
    }
    this@toIntent.extrasList?.let { extras ->
        extras.forEach {
            @Suppress("MoveVariableDeclarationIntoWhen")
            val type: AndroidIntentExtraType = it.type
            when (type) {
                AndroidIntentExtraType.Int -> {
                    putExtra(it.key, it.value.toInt())
                }

                AndroidIntentExtraType.Long -> {
                    putExtra(it.key, it.value.toLong())
                }

                AndroidIntentExtraType.String -> {
                    putExtra(it.key, it.value)
                }

                AndroidIntentExtraType.Bool -> {
                    putExtra(it.key, it.value.toBoolean())
                }

                AndroidIntentExtraType.Float -> {
                    putExtra(it.key, it.value.toFloat())
                }

                AndroidIntentExtraType.Double -> {
                    putExtra(it.key, it.value.toDouble())
                }

                AndroidIntentExtraType.UNRECOGNIZED -> {
                    putExtra(it.key, it.value)
                }
            }
        }
    }
    this@toIntent.data?.let { data = Uri.parse(it) }

}