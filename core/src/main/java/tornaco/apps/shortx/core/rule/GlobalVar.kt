package tornaco.apps.shortx.core.rule

import tornaco.apps.shortx.core.proto.action.WriteGlobalVarOp
import tornaco.apps.shortx.core.proto.condition.GlobalVarOp
import tornaco.apps.shortx.core.proto.gv.BoolListVar
import tornaco.apps.shortx.core.proto.gv.BoolVar
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.gv.Int64ListVar
import tornaco.apps.shortx.core.proto.gv.Int64Var
import tornaco.apps.shortx.core.proto.gv.StringListVar
import tornaco.apps.shortx.core.proto.gv.StringVar
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.replaceLazy

private val logger = Logger("GlobalVar")

enum class GlobalVarType {
    Int,
    IntList,
    Bool,
    BoolList,
    String,
    StringList,
}

fun GlobalVarType.label(): String {
    return when (this) {
        GlobalVarType.Int -> "Int"
        GlobalVarType.IntList -> "Int list"
        GlobalVarType.Bool -> "Boolean"
        GlobalVarType.BoolList -> "Boolean list"
        GlobalVarType.String -> "String"
        GlobalVarType.StringList -> "String list"
    }
}

fun GlobalVarType.toProtoType(): ProtoAny = when (this) {
    GlobalVarType.Int -> Int64Var.getDefaultInstance().pack_()
    GlobalVarType.IntList -> Int64ListVar.getDefaultInstance().pack_()
    GlobalVarType.Bool -> BoolVar.getDefaultInstance().pack_()
    GlobalVarType.BoolList -> BoolListVar.getDefaultInstance().pack_()
    GlobalVarType.String -> StringVar.getDefaultInstance().pack_()
    GlobalVarType.StringList -> StringListVar.getDefaultInstance().pack_()
}

fun ProtoAny.toType(): GlobalVarType? {
    return when {
        this is_ (Int64Var::class.java) -> {
            GlobalVarType.Int
        }

        this is_ (Int64ListVar::class.java) -> {
            GlobalVarType.IntList
        }

        this is_ (BoolVar::class.java) -> {
            GlobalVarType.Bool
        }

        this is_ (BoolListVar::class.java) -> {
            GlobalVarType.BoolList
        }

        this is_ (StringVar::class.java) -> {
            GlobalVarType.String
        }

        this is_ (StringListVar::class.java) -> {
            GlobalVarType.StringList
        }

        else -> {
            null
        }
    }
}

fun GlobalVar.valueString(): String = valueList().joinToString()

fun GlobalVar.valueList(
    noValueReturn: () -> List<String> = {
        listOf("NO VALUE")
    }
): List<String> = when (this.`var`.toType()) {
    GlobalVarType.Int -> {
        val v = this.`var`.unpack(Int64Var::class.java)
        listOf(v.value.toString())
    }

    GlobalVarType.IntList -> {
        val v = this.`var`.unpack(Int64ListVar::class.java)
        v.valuesList.map { it.toString() }
    }

    GlobalVarType.Bool -> {
        val v = this.`var`.unpack(BoolVar::class.java)
        listOf(v.value.toString())
    }

    GlobalVarType.BoolList -> {
        val v = this.`var`.unpack(BoolListVar::class.java)
        v.valuesList.map { it.toString() }
    }

    GlobalVarType.String -> {
        val v = this.`var`.unpack(StringVar::class.java)
        listOf(v.value)
    }

    GlobalVarType.StringList -> {
        val v = this.`var`.unpack(StringListVar::class.java)
        v.valuesList
    }

    null -> {
        noValueReturn()
    }
}

fun GlobalVar.isList(): Boolean = listOf(
    GlobalVarType.StringList,
    GlobalVarType.IntList,
    GlobalVarType.BoolList
).contains(this.type.toType())

fun GlobalVar.isBool(): Boolean = listOf(
    GlobalVarType.Bool
).contains(this.type.toType())

fun GlobalVar.isInt(): Boolean = listOf(
    GlobalVarType.Int
).contains(this.type.toType())

fun String.toVarValueNotList(type: GlobalVarType?): ProtoAny? {
    return when (type) {
        GlobalVarType.Int -> {
            kotlin.runCatching { this.toLong() }.getOrNull()?.let {
                Int64Var.newBuilder().setValue(it)
                    .build().pack_()
            }
        }

        GlobalVarType.Bool -> {
            kotlin.runCatching { this.toBoolean() }.getOrNull()?.let {
                BoolVar.newBuilder()
                    .setValue(it).build()
                    .pack_()
            }
        }

        GlobalVarType.String -> {
            StringVar.newBuilder().setValue(this).build().pack_()
        }

        else -> {
            null
        }
    }
}

fun String.splitAsGlobalVarOpPayloadStringList(): List<String> {
    return split(System.lineSeparator()).map { it.trim() }
}

fun GlobalVarOp.toDisplayName(): String {
    return when (this) {
        GlobalVarOp.GreaterThan -> "$name (>)"
        GlobalVarOp.GreaterThanOrEQ -> "$name (>=)"
        GlobalVarOp.LessThan -> "$name (<)"
        GlobalVarOp.LessThanOrEQ -> "$name (<=)"
        GlobalVarOp.EqualTo -> "$name (==)"
        GlobalVarOp.Contains -> "$name (contains)"
        GlobalVarOp.DoesNotContain -> "$name (!contains)"
        GlobalVarOp.NotSet -> name
        GlobalVarOp.HasBeenSet -> name
        GlobalVarOp.IsEmpty -> name
        GlobalVarOp.IsNotEmpty -> name
        GlobalVarOp.IsExists -> name
        GlobalVarOp.IsNotExists -> name
        GlobalVarOp.UNRECOGNIZED -> "N/A"
    }
}

const val GLOBAL_VAR_REF_PREFIX = "globalVarOf$"

fun String.replaceGlobalVarValues(globalVarsLazy: () -> List<GlobalVar>): String {
    // FAST Check if contains GLOBAL_VAR_REF_PREFIX
    if (!this.contains(GLOBAL_VAR_REF_PREFIX)) return this

    val vars = globalVarsLazy()
    // FAST Check if vars it empty
    if (vars.isEmpty()) return this

    var strWithGValue = this
    vars
        // In case name override: globalVarOf$applist and globalVarOf$app
        .sortedByDescending { it.name.length }
        .forEach { entry ->
            if (entry.isList()) {
                val listValue = entry.valueList(noValueReturn = { emptyList() })
                listValue.forEachIndexed { index, value ->
                    strWithGValue =
                        strWithGValue.replaceLazy(GLOBAL_VAR_REF_PREFIX + entry.name + "[$index]", {
                            value
                        })
                }
            }

            strWithGValue =
                strWithGValue.replaceLazy(GLOBAL_VAR_REF_PREFIX + entry.name, {
                    val valueItems = entry.valueList(noValueReturn = { emptyList() })
                    if (entry.isList()) {
                        valueItems.joinToString()
                    } else {
                        if (valueItems.isEmpty()) "" else valueItems[0]
                    }
                })
        }
    return strWithGValue
}


fun GlobalVar.addOrUpdateValue(value: List<String>): GlobalVar {
    return if (this.isList()) {
        val newVarProtoAny: ProtoAny? = when (this.type.toType()) {
            GlobalVarType.IntList -> {
                value.mapNotNull { kotlin.runCatching { it.toLong() }.getOrNull() }
                    .let { longV ->
                        val data = if (this.`var`.`is`(Int64ListVar::class.java)) {
                            this.`var` unpack_ Int64ListVar::class.java
                        } else {
                            Int64ListVar.newBuilder().build()
                        }
                        Int64ListVar.newBuilder(data).addAllValues(longV).build().pack_()
                    }
            }

            GlobalVarType.BoolList -> {
                value.mapNotNull { kotlin.runCatching { it.toBoolean() }.getOrNull() }
                    .let { boolV ->
                        val data = if (this.`var`.`is`(BoolListVar::class.java)) {
                            this.`var` unpack_ BoolListVar::class.java
                        } else {
                            BoolListVar.newBuilder().build()
                        }
                        BoolListVar.newBuilder(data).addAllValues(boolV).build().pack_()
                    }
            }

            GlobalVarType.StringList -> {
                val data = if (this.`var`.`is`(StringListVar::class.java)) {
                    this.`var` unpack_ StringListVar::class.java
                } else {
                    StringListVar.newBuilder().build()
                }
                logger.d("before add to StringList: $value --> current: $data")
                StringListVar.newBuilder(data).addAllValues(value).build().pack_()
            }

            else -> {
                null
            }
        }

        GlobalVar.newBuilder(this).setVar(newVarProtoAny).build()
    } else {
        GlobalVar.newBuilder(this).setVar(
            value.first().toVarValueNotList(this.type.toType())
        ).build()
    }
}


fun GlobalVar.setValue(value: List<String>): GlobalVar {
    return if (this.isList()) {
        val newVarProtoAny: ProtoAny? = when (this.type.toType()) {
            GlobalVarType.IntList -> {
                value.mapNotNull { kotlin.runCatching { it.toLong() }.getOrNull() }
                    .let { longV ->
                        Int64ListVar.newBuilder().addAllValues(longV).build().pack_()
                    }
            }

            GlobalVarType.BoolList -> {
                value.mapNotNull { kotlin.runCatching { it.toBoolean() }.getOrNull() }
                    .let { boolV ->
                        BoolListVar.newBuilder().addAllValues(boolV).build().pack_()
                    }
            }

            GlobalVarType.StringList -> {
                StringListVar.newBuilder().addAllValues(value).build().pack_()
            }

            else -> {
                null
            }
        }

        GlobalVar.newBuilder(this).setVar(newVarProtoAny).build()
    } else {
        if (value.isEmpty()) {
            GlobalVar.newBuilder(this).clearVar().build()
        } else {
            GlobalVar.newBuilder(this).setVar(
                value.first().toVarValueNotList(this.type.toType())
            ).build()
        }
    }
}

fun WriteGlobalVarOp.displayLabelKey(): String {
    return "ui.action.write.global.var.op.${name.lowercase()}"
}

fun WriteGlobalVarOp.isForListOnly(): Boolean {
    return listOnlyOps.contains(this)
}

fun WriteGlobalVarOp.needUserInputValue(): Boolean {
    return needInputValueOps.contains(this)
}

private val listOnlyOps = listOf(
    WriteGlobalVarOp.WriteGlobalVarOp_DeleteLast,
    WriteGlobalVarOp.WriteGlobalVarOp_DeleteFirst,
    WriteGlobalVarOp.WriteGlobalVarOp_Reverse,
    WriteGlobalVarOp.WriteGlobalVarOp_Shuffle,
    WriteGlobalVarOp.WriteGlobalVarOp_RemoveAtIndex,
)
private val needInputValueOps = listOf(
    WriteGlobalVarOp.WriteGlobalVarOp_Auto,
    WriteGlobalVarOp.WriteGlobalVarOp_AppendToFirst,
    WriteGlobalVarOp.WriteGlobalVarOp_AppendToLast,
    WriteGlobalVarOp.WriteGlobalVarOp_Override,
    WriteGlobalVarOp.WriteGlobalVarOp_DeleteValue,
    WriteGlobalVarOp.WriteGlobalVarOp_PlusDelta,
    WriteGlobalVarOp.WriteGlobalVarOp_MinusDelta,
)