package tornaco.apps.shortx.core

import android.content.Intent
import android.net.Uri
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.common.AndroidIntent
import tornaco.apps.shortx.core.proto.common.AndroidIntentExtraType
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues

fun AndroidIntent.toIntent(
    contextData: ContextData,
    localVars: LocalVars,
    globalVarsLazy: () -> List<GlobalVar>,
) =
    Intent().apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this@toIntent.action.ifEmpty { Intent.ACTION_VIEW }.let { action = it }

        val pkgName = this@toIntent.pkgName.inflateContextMap(contextData)
            .replaceLocalVarValues(localVars)
            .replaceGlobalVarValues(globalVarsLazy)
        val className = this@toIntent.className.inflateContextMap(contextData)
            .replaceLocalVarValues(localVars)
            .replaceGlobalVarValues(globalVarsLazy)
        if (pkgName.isNotBlank()) {
            setPackage(pkgName)
            if (className.isNotBlank()) {
                setClassName(pkgName, className)
            }
        }
        val data = this@toIntent.data
        if (data.isNotBlank()) {
            setData(
                Uri.parse(
                    data.inflateContextMap(contextData)
                        .replaceLocalVarValues(localVars)
                        .replaceGlobalVarValues(globalVarsLazy)
                )
            )
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

    }