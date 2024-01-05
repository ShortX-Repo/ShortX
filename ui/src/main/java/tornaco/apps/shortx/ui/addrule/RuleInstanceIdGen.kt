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
import tornaco.apps.shortx.core.proto.rule.RuleInstanceIdGenerator_JS
import tornaco.apps.shortx.core.proto.rule.RuleInstanceIdGenerator_MVEL
import tornaco.apps.shortx.core.proto.rule.RuleInstanceIdGenerator_Static
import tornaco.apps.shortx.core.proto.rule.RuleInstanceIdGenerator_String
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitleWithTip
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.code.CodeEditorDialog
import tornaco.apps.shortx.ui.base.code.rememberCodeEditorState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import kotlin.reflect.KClass

sealed interface RuleInstanceIdGen {
    val displayValue: String?

    data object RuleInstanceIdGenDefault : RuleInstanceIdGen {
        override val displayValue: String? = null
    }

    data class RuleInstanceIdGenStatic(val insId: String) : RuleInstanceIdGen {
        override val displayValue: String
            get() = insId
    }

    data class RuleInstanceIdGenString(val insId: String) : RuleInstanceIdGen {
        override val displayValue: String
            get() = insId
    }

    data class RuleInstanceIdGenMVEL(val expression: String) : RuleInstanceIdGen {
        override val displayValue: String
            get() = expression
    }

    data class RuleInstanceIdGenJS(val expression: String) : RuleInstanceIdGen {
        override val displayValue: String
            get() = expression
    }
}

fun ProtoAny.toRuleInstanceIdGen(): RuleInstanceIdGen {
    return if (this is_ RuleInstanceIdGenerator_Static::class.java) {
        val gen = this unpack_ RuleInstanceIdGenerator_Static::class.java
        RuleInstanceIdGen.RuleInstanceIdGenStatic(gen.insId)
    } else if (this is_ RuleInstanceIdGenerator_String::class.java) {
        val gen = this unpack_ RuleInstanceIdGenerator_String::class.java
        RuleInstanceIdGen.RuleInstanceIdGenString(gen.insId)
    } else if (this is_ RuleInstanceIdGenerator_JS::class.java) {
        val gen = this unpack_ RuleInstanceIdGenerator_JS::class.java
        RuleInstanceIdGen.RuleInstanceIdGenJS(gen.expression)
    } else if (this is_ RuleInstanceIdGenerator_MVEL::class.java) {
        val gen = this unpack_ RuleInstanceIdGenerator_MVEL::class.java
        RuleInstanceIdGen.RuleInstanceIdGenMVEL(gen.expression)
    } else {
        RuleInstanceIdGen.RuleInstanceIdGenDefault
    }
}

fun RuleInstanceIdGen.toProto(): ProtoAny? {
    return when (this) {
        RuleInstanceIdGen.RuleInstanceIdGenDefault -> {
            null
        }

        is RuleInstanceIdGen.RuleInstanceIdGenJS -> {
            RuleInstanceIdGenerator_JS.newBuilder().setExpression(this.expression).build().pack_()
        }

        is RuleInstanceIdGen.RuleInstanceIdGenMVEL -> {
            RuleInstanceIdGenerator_MVEL.newBuilder().setExpression(this.expression).build().pack_()
        }

        is RuleInstanceIdGen.RuleInstanceIdGenStatic -> {
            RuleInstanceIdGenerator_Static.newBuilder().setInsId(this.insId).build().pack_()
        }

        is RuleInstanceIdGen.RuleInstanceIdGenString -> {
            RuleInstanceIdGenerator_String.newBuilder().setInsId(this.insId).build().pack_()
        }
    }
}

@Composable
fun RuleInstanceIdGenSelector(
    gen: RuleInstanceIdGen,
    readOnly: Boolean,
    setGen: (RuleInstanceIdGen) -> Unit
) {
    val i18N = LocalI18N.current

    val opSelectorState = rememberDropdownSelectorState()
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        FieldTitleWithTip(
            i18N["ui.rule.ins.id.gen"],
            i18N["ui.rule.ins.id.gen.description"]
        )
        ListItem(
            modifier = Modifier,
            titleLines = listOfNotNull(i18N[gen::class.labelKey()], gen.displayValue),
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

            val ruleInsGenStaticDialogState = rememberTextInputState(
                title = i18N[RuleInstanceIdGen.RuleInstanceIdGenStatic::class.labelKey()],
                message = i18N[RuleInstanceIdGen.RuleInstanceIdGenStatic::class.descriptionKey()]
            ) {
                setGen(RuleInstanceIdGen.RuleInstanceIdGenStatic(it))
            }
            TextInputDialog(state = ruleInsGenStaticDialogState)

            val ruleInsGenStringDialogState = rememberTextInputState(
                title = i18N[RuleInstanceIdGen.RuleInstanceIdGenString::class.labelKey()],
                message = i18N[RuleInstanceIdGen.RuleInstanceIdGenString::class.descriptionKey()]
            ) {
                setGen(RuleInstanceIdGen.RuleInstanceIdGenString(it))
            }
            TextInputDialog(state = ruleInsGenStringDialogState)

            val ruleInsGenJSDialogState = rememberTextInputState(
                title = i18N[RuleInstanceIdGen.RuleInstanceIdGenJS::class.labelKey()],
                message = i18N[RuleInstanceIdGen.RuleInstanceIdGenJS::class.descriptionKey()]
            ) {
                setGen(RuleInstanceIdGen.RuleInstanceIdGenJS(it))
            }
            TextInputDialog(state = ruleInsGenJSDialogState)

            val ruleInsGenMVELDialogState = rememberTextInputState(
                title = i18N[RuleInstanceIdGen.RuleInstanceIdGenMVEL::class.labelKey()],
                message = i18N[RuleInstanceIdGen.RuleInstanceIdGenMVEL::class.descriptionKey()]
            ) {
                setGen(RuleInstanceIdGen.RuleInstanceIdGenMVEL(it))
            }
            TextInputDialog(state = ruleInsGenMVELDialogState)

            DropdownSelector(
                state = opSelectorState,
                items = listOf(
                    DropdownItem(
                        labelLines = listOf(
                            i18N[RuleInstanceIdGen.RuleInstanceIdGenDefault::class.labelKey()],
                            i18N[RuleInstanceIdGen.RuleInstanceIdGenDefault::class.descriptionKey()],
                        ), data = "Default"
                    ),
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
                when (it.data) {
                    "Default" -> {
                        setGen(RuleInstanceIdGen.RuleInstanceIdGenDefault)
                    }

                    "Static" -> {
                        ruleInsGenStaticDialogState.show(
                            if (gen is RuleInstanceIdGen.RuleInstanceIdGenStatic) {
                                gen.insId
                            } else null
                        )
                    }

                    "String" -> {
                        ruleInsGenStringDialogState.show(
                            if (gen is RuleInstanceIdGen.RuleInstanceIdGenString) {
                                gen.insId
                            } else null
                        )
                    }

                    "MVEL" -> {
                        ruleInsGenMVELDialogState.show(
                            if (gen is RuleInstanceIdGen.RuleInstanceIdGenMVEL) {
                                gen.expression
                            } else null
                        )
                    }

                    "JS" -> {
                        ruleInsGenJSDialogState.show(
                            if (gen is RuleInstanceIdGen.RuleInstanceIdGenJS) {
                                gen.expression
                            } else null
                        )
                    }
                }
            }
        }
    }
}


fun KClass<*>.labelKey(): String {
    return when (this) {
        RuleInstanceIdGen.RuleInstanceIdGenDefault::class -> {
            "ui.rule.ins.id.gen.default"
        }

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
        RuleInstanceIdGen.RuleInstanceIdGenDefault::class -> {
            "ui.rule.ins.id.gen.default.description"
        }

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
        RuleInstanceIdGen.RuleInstanceIdGenDefault::class -> {
            Remix.System.refresh_fill
        }

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