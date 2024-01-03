package tornaco.apps.shortx.ui.addrule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.code.CodeEditorDialog
import tornaco.apps.shortx.ui.base.code.rememberCodeEditorState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import kotlin.reflect.KClass

sealed interface RuleInstanceIdGen {
    data class RuleInstanceIdGenStatic(val insId: String) : RuleInstanceIdGen
    data class RuleInstanceIdGenString(val insId: String) : RuleInstanceIdGen
    data class RuleInstanceIdGenMVEL(val expression: String) : RuleInstanceIdGen
    data class RuleInstanceIdGenJS(val expression: String) : RuleInstanceIdGen
}

@Composable
private fun RuleInstanceIdGenSelector(
    gen: RuleInstanceIdGen,
    readOnly: Boolean,
    setGen: (RuleInstanceIdGen) -> Unit
) {
    val i18N = LocalI18N.current

    val opSelectorState = rememberDropdownSelectorState()
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SmallTitle(
            modifier = Modifier.padding(vertical = 12.dp),
            i18N["ui.rule.ins.id.gen"]
        )
        ListItem(
            modifier = Modifier,
            titleLines = listOf(i18N[gen::class.labelKey()]),
            icon = {
                ColoredIcon(size = 28.dp) {
                    RemixIcon(modifier = Modifier.size(12.dp), remixName = gen::class.iconName())
                }
            })

        Box {
            if (!readOnly) {
                FilledTonalButton(onClick = {
                    opSelectorState.open()
                }) {
                    RemixIcon(
                        modifier = Modifier.size(16.dp),
                        remixName = Remix.Design.edit_circle_line
                    )
                    TinySpacer()
                    Text(text = i18N["ui.rule.ins.id.gen.edit"])
                }
            }

            val mvelSelectorState = rememberCodeEditorState(
                title = i18N["ui.title.mvel"],
                execute = {
                    shortXManager.executeMVEL(it)
                },
                onSelected = {
                    setGen(RuleInstanceIdGen.RuleInstanceIdGenMVEL(it.src))
                })
            CodeEditorDialog(state = mvelSelectorState)

            DropdownSelector(
                state = opSelectorState,
                items = listOf(
                    DropdownItem(
                        labelLines = listOf(
                            i18N[RuleInstanceIdGen.RuleInstanceIdGenStatic::class.labelKey()],
                            i18N[RuleInstanceIdGen.RuleInstanceIdGenStatic::class.descriptionKey()],
                        ), data = "Static"
                    ),
                    DropdownItem(
                        labelLines = listOf(
                            i18N[RuleInstanceIdGen.RuleInstanceIdGenString::class.labelKey()],
                            i18N[RuleInstanceIdGen.RuleInstanceIdGenString::class.descriptionKey()],
                        ), data = "String"
                    ),
                    DropdownItem(
                        labelLines = listOf(
                            i18N[RuleInstanceIdGen.RuleInstanceIdGenMVEL::class.labelKey()],
                            i18N[RuleInstanceIdGen.RuleInstanceIdGenMVEL::class.descriptionKey()],
                        ), data = "MVEL"
                    ),
                    DropdownItem(
                        labelLines = listOf(
                            i18N[RuleInstanceIdGen.RuleInstanceIdGenJS::class.labelKey()],
                            i18N[RuleInstanceIdGen.RuleInstanceIdGenJS::class.descriptionKey()],
                        ), data = "JS"
                    ),
                )
            ) {

            }
        }
    }
}


fun KClass<*>.labelKey(): String {
    return when (this) {
        RuleInstanceIdGen.RuleInstanceIdGenJS::class -> {
            "ui.rule.ins.id.gen.js"
        }

        RuleInstanceIdGen.RuleInstanceIdGenMVEL::class -> {
            "ui.rule.ins.id.gen.mvel"
        }

        RuleInstanceIdGen.RuleInstanceIdGenStatic::class -> {
            "ui.rule.ins.id.gen.static"
        }

        RuleInstanceIdGen.RuleInstanceIdGenString::class -> {
            "ui.rule.ins.id.gen.string"
        }

        else -> {
            "N/A"
        }
    }
}


fun KClass<*>.descriptionKey(): String {
    return when (this) {
        RuleInstanceIdGen.RuleInstanceIdGenJS::class -> {
            "ui.rule.ins.id.gen.js.description"
        }

        RuleInstanceIdGen.RuleInstanceIdGenMVEL::class -> {
            "ui.rule.ins.id.gen.mvel.description"
        }

        RuleInstanceIdGen.RuleInstanceIdGenStatic::class -> {
            "ui.rule.ins.id.gen.static.description"
        }

        RuleInstanceIdGen.RuleInstanceIdGenString::class -> {
            "ui.rule.ins.id.gen.string.description"
        }

        else -> {
            "N/A"
        }
    }
}


fun KClass<*>.iconName(): String {
    return when (this) {
        RuleInstanceIdGen.RuleInstanceIdGenJS::class -> {
            Remix.Development.javascript_fill
        }

        RuleInstanceIdGen.RuleInstanceIdGenMVEL::class -> {
            Remix.Development.code_s_slash_fill
        }

        RuleInstanceIdGen.RuleInstanceIdGenStatic::class -> {
            Remix.Development.parentheses_fill
        }

        RuleInstanceIdGen.RuleInstanceIdGenString::class -> {
            Remix.Development.braces_fill
        }

        else -> {
            Remix.Document.file_unknow_fill
        }
    }
}