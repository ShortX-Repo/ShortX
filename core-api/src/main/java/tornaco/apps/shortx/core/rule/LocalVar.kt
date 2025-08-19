package tornaco.apps.shortx.core.rule

import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.gv.BoolListVar
import tornaco.apps.shortx.core.proto.gv.BoolVar
import tornaco.apps.shortx.core.proto.gv.Int64ListVar
import tornaco.apps.shortx.core.proto.gv.Int64Var
import tornaco.apps.shortx.core.proto.gv.LocalVar
import tornaco.apps.shortx.core.proto.gv.StringListVar
import tornaco.apps.shortx.core.proto.gv.StringVar
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.replaceLazy

const val LOCAL_VAR_REF_PREFIX = "localVarOf$"
private val logger = Logger("LocalVar")

fun String.replaceLocalVarValues(localVars: LocalVars): String {
    // FAST Check if contains LOCAL_VAR_REF_PREFIX
    if (!this.contains(LOCAL_VAR_REF_PREFIX)) return this

    // FAST Check if vars it empty
    if (localVars.isEmpty) return this

    logger.d("localVars: ${localVars.all.map { it.name + "-" + it.valueList() }}")

    var strWithLocalVarValue = this
    localVars.all
        // In case name override: globalVarOf$applist and globalVarOf$app
        .sortedByDescending { it.name.length }
        .forEach { entry ->
            strWithLocalVarValue =
                strWithLocalVarValue.replaceLazy(LOCAL_VAR_REF_PREFIX + entry.name, {
                    val valueItems = entry.valueList(noValueReturn = { emptyList() })
                    if (entry.isList()) {
                        valueItems.joinToString()
                    } else {
                        if (valueItems.isEmpty()) "" else valueItems[0]
                    }
                })
        }
    return strWithLocalVarValue
}

fun LocalVar.isList(): Boolean = listOf(
    GlobalVarType.StringList,
    GlobalVarType.IntList,
    GlobalVarType.BoolList
).contains(this.type.toType())

fun LocalVar.valueString(): String = valueList().joinToString()

fun LocalVar.valueList(
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


fun LocalVar.addOrUpdateValue(value: List<String>): LocalVar {
    logger.d("addOrUpdateValue: $value")
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

        LocalVar.newBuilder(this).setVar(newVarProtoAny).build()


    } else {
        LocalVar.newBuilder(this).setVar(value.first().toVarValueNotList(this.type.toType()))
            .build()
    }
}
