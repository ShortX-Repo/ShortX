package tornaco.apps.shortx.core

import android.content.Intent
import android.net.Uri
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.common.AndroidIntent
import tornaco.apps.shortx.core.proto.common.AndroidIntentExtraType
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.rule.RuleContextAndVars
import tornaco.apps.shortx.core.rule.compileContextAndVars

fun AndroidIntent.toIntent(
    contextData: ContextData,
    localVars: LocalVars,
    parameterInputs: List<FuncParameterInput>,
    globalVarsLazy: () -> List<GlobalVar>,
) =
    Intent().apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this@toIntent.action.ifEmpty { Intent.ACTION_VIEW }.let { action = it }
        val ruleContextAndVars =
            RuleContextAndVars(
                localVars = localVars,
                contextData = contextData,
                funcParameters = parameterInputs,
                globalVarsLazy = globalVarsLazy
            )
        val pkgName = this@toIntent.pkgName.compileContextAndVars(ruleContextAndVars)
        val className = this@toIntent.className.compileContextAndVars(ruleContextAndVars)
        if (pkgName.isNotBlank()) {
            setPackage(pkgName)
            if (className.isNotBlank()) {
                setClassName(pkgName, className)
            }
        }
        val data = this@toIntent.data
        if (data.isNotBlank()) {
            setData(
                Uri.parse(data.compileContextAndVars(ruleContextAndVars))
            )
        }

        this@toIntent.extrasList?.let { extras ->
            extras.forEach {
                val type: AndroidIntentExtraType = it.type
                val realKey = it.key.compileContextAndVars(ruleContextAndVars)
                val realValue = it.value.compileContextAndVars(ruleContextAndVars)

                when (type) {
                    AndroidIntentExtraType.Int -> {
                        putExtra(realKey, realValue.toInt())
                    }

                    AndroidIntentExtraType.Long -> {
                        putExtra(realKey, realValue.toLong())
                    }

                    AndroidIntentExtraType.String -> {
                        putExtra(realKey, realValue)
                    }

                    AndroidIntentExtraType.Bool -> {
                        putExtra(realKey, realValue.toBoolean())
                    }

                    AndroidIntentExtraType.Float -> {
                        putExtra(realKey, realValue.toFloat())
                    }

                    AndroidIntentExtraType.Double -> {
                        putExtra(realKey, realValue.toDouble())
                    }

                    AndroidIntentExtraType.UNRECOGNIZED -> {
                        putExtra(realKey, realValue)
                    }
                }
            }
        }

    }