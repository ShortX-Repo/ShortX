package tornaco.apps.shortx.services.rule.actor.impl

import com.android.internal.annotations.VisibleForTesting
import com.google.protobuf.Message
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.action.WriteGlobalVar
import tornaco.apps.shortx.core.proto.action.WriteGlobalVarOp
import tornaco.apps.shortx.core.proto.gv.BoolVar
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.gv.GlobalVarCreatedBy_Rule
import tornaco.apps.shortx.core.proto.gv.Int64Var
import tornaco.apps.shortx.core.proto.gv.StringListVar
import tornaco.apps.shortx.core.proto.gv.StringVar
import tornaco.apps.shortx.core.rule.addOrUpdateValue
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.isBool
import tornaco.apps.shortx.core.rule.isInt
import tornaco.apps.shortx.core.rule.isList
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.setValue
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.rule.valueList
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object WriteGlobalVar : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WriteGlobalVar::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ WriteGlobalVar::class.java
        val globalVar = shortXContext.rule.getGlobalVarByName(d.varName)
            ?: require(d.autoCreateIfMissing) {
                "Global var: ${d.varName} not exists. pls create first."
            }.let {
                createDefaultGlobalVar(
                    name = d.varName,
                    op = d.op,
                    srcId = params.evaluateContext.srcId
                )
            }
        val op: WriteGlobalVarOp = d.op
        logger.d("$op -> ${globalVar.name}")

        val cv = RuleContextAndVars(params)

        val stringValueToWrite =
            d.valueAsString.compileContextAndVars(cv)

        val newGlobalVar = handleGVOp(globalVar, op, stringValueToWrite)

        shortXContext.rule.addGlobalVar(newGlobalVar)
        Actor.Res.EMPTY
    }

    private fun createDefaultGlobalVar(
        name: String,
        op: WriteGlobalVarOp,
        srcId: String
    ): GlobalVar {
        val type = when (op) {
            WriteGlobalVarOp.WriteGlobalVarOp_Plus1,
            WriteGlobalVarOp.WriteGlobalVarOp_Minus1,
            WriteGlobalVarOp.WriteGlobalVarOp_PlusDelta,
            WriteGlobalVarOp.WriteGlobalVarOp_MinusDelta -> Int64Var.getDefaultInstance().pack_()

            WriteGlobalVarOp.WriteGlobalVarOp_DeleteFirst,
            WriteGlobalVarOp.WriteGlobalVarOp_DeleteLast,
            WriteGlobalVarOp.WriteGlobalVarOp_RemoveAtIndex,
            WriteGlobalVarOp.WriteGlobalVarOp_Reverse,
            WriteGlobalVarOp.WriteGlobalVarOp_Shuffle -> StringListVar.getDefaultInstance().pack_()

            WriteGlobalVarOp.WriteGlobalVarOp_Invert -> BoolVar.getDefaultInstance().pack_()

            else -> StringVar.getDefaultInstance().pack_()
        }

        return GlobalVar.newBuilder()
            .setName(name)
            .setType(type)
            .setCreatedBy(
                GlobalVarCreatedBy_Rule.newBuilder()
                    .setSrcId(srcId)
                    .build()
                    .pack_()
            )
            .build()
    }

    @VisibleForTesting
    fun handleGVOp(
        globalVar: GlobalVar,
        op: WriteGlobalVarOp,
        stringValueToWrite: String
    ): GlobalVar {
        val newGlobalVar: GlobalVar = when (op) {
            WriteGlobalVarOp.WriteGlobalVarOp_Auto -> {
                if (globalVar.isList()) {
                    globalVar.addOrUpdateValue(listOf(stringValueToWrite))
                } else {
                    globalVar.setValue(listOf(stringValueToWrite))
                }
            }

            WriteGlobalVarOp.WriteGlobalVarOp_AppendToLast -> {
                if (globalVar.isList()) {
                    globalVar.addOrUpdateValue(listOf(stringValueToWrite))
                } else {
                    val currentValue = globalVar.valueList()
                    val newValueString = if (currentValue.isEmpty()) {
                        stringValueToWrite
                    } else {
                        currentValue[0] + stringValueToWrite
                    }
                    globalVar.setValue(listOf(newValueString))
                }
            }

            WriteGlobalVarOp.WriteGlobalVarOp_AppendToFirst -> {
                if (globalVar.isList()) {
                    globalVar.throwIfNotList(op)
                    val currentValue = globalVar.valueList()
                    globalVar.setValue(currentValue.toMutableList().apply {
                        add(0, stringValueToWrite)
                    })
                } else {
                    val currentValue = globalVar.valueList()
                    val newValueString = if (currentValue.isEmpty()) {
                        stringValueToWrite
                    } else {
                        stringValueToWrite + currentValue[0]
                    }
                    globalVar.setValue(listOf(newValueString))
                }
            }

            WriteGlobalVarOp.WriteGlobalVarOp_Override -> {
                globalVar.setValue(listOf(stringValueToWrite))
            }

            WriteGlobalVarOp.WriteGlobalVarOp_DeleteValue -> {
                val currentValue = globalVar.valueList()
                if (globalVar.isList()) {
                    globalVar.setValue(currentValue.toMutableList().apply {
                        remove(stringValueToWrite)
                    })
                } else {
                    val newValueString = if (currentValue.isEmpty()) {
                        ""
                    } else {
                        currentValue[0].replace(stringValueToWrite, "")
                    }
                    globalVar.setValue(listOf(newValueString))
                }
            }

            WriteGlobalVarOp.WriteGlobalVarOp_DeleteLast -> {
                globalVar.throwIfNotList(op)
                val currentValue = globalVar.valueList()
                globalVar.setValue(currentValue.toMutableList().apply {
                    if (this.isNotEmpty()) removeAt(lastIndex)
                })
            }

            WriteGlobalVarOp.WriteGlobalVarOp_DeleteFirst -> {
                globalVar.throwIfNotList(op)
                val currentValue = globalVar.valueList()
                globalVar.setValue(currentValue.toMutableList().apply {
                    if (this.isNotEmpty()) removeAt(0)
                })
            }

            WriteGlobalVarOp.WriteGlobalVarOp_RemoveAtIndex -> {
                globalVar.throwIfNotList(op)
                val indexToRemove = stringValueToWrite.toInt()
                val currentValue = globalVar.valueList()
                globalVar.setValue(currentValue.toMutableList().apply {
                    removeAt(indexToRemove)
                })
            }

            WriteGlobalVarOp.WriteGlobalVarOp_Clear -> {
                globalVar.setValue(emptyList())
            }

            WriteGlobalVarOp.WriteGlobalVarOp_Reverse -> {
                globalVar.throwIfNotList(op)
                val currentValue = globalVar.valueList()
                globalVar.setValue(currentValue.reversed())
            }

            WriteGlobalVarOp.WriteGlobalVarOp_Shuffle -> {
                globalVar.throwIfNotList(op)
                val currentValue = globalVar.valueList()
                globalVar.setValue(currentValue.shuffled())
            }

            WriteGlobalVarOp.UNRECOGNIZED -> {
                throw UnsupportedOperationException("WriteGlobalVar: UNRECOGNIZED op")
            }

            WriteGlobalVarOp.WriteGlobalVarOp_Plus1 -> {
                globalVar.throwIfNotGenericType(op)
                val currentValue = globalVar.valueList()
                val newValueInt: Long = if (currentValue.isEmpty()) {
                    1
                } else {
                    currentValue[0].toLong() + 1
                }
                globalVar.setValue(listOf(newValueInt.toString()))
            }

            WriteGlobalVarOp.WriteGlobalVarOp_Minus1 -> {
                globalVar.throwIfNotGenericType(op)
                val currentValue = globalVar.valueList()
                val newValueInt: Long = if (currentValue.isEmpty()) {
                    0
                } else {
                    currentValue[0].toLong() - 1
                }
                globalVar.setValue(listOf(newValueInt.toString()))
            }

            WriteGlobalVarOp.WriteGlobalVarOp_Invert -> {
                if (globalVar.isList()) {
                    val currentValue = globalVar.valueList()
                    globalVar.setValue(currentValue.reversed())
                } else if (globalVar.isBool()) {
                    val currentValue = globalVar.valueList()
                    val newValueBool = if (currentValue.isEmpty()) {
                        false
                    } else {
                        !currentValue[0].toBoolean()
                    }
                    globalVar.setValue(listOf(newValueBool.toString()))
                } else {
                    globalVar
                }
            }

            WriteGlobalVarOp.WriteGlobalVarOp_PlusDelta -> {
                globalVar.throwIfNotIntType(op)
                val currentValue = globalVar.valueList()
                val intDelta = stringValueToWrite.trim().toLong()
                val newValueInt: Long = if (currentValue.isEmpty()) {
                    intDelta
                } else {
                    currentValue[0].toLong() + intDelta
                }
                globalVar.setValue(listOf(newValueInt.toString()))
            }

            WriteGlobalVarOp.WriteGlobalVarOp_MinusDelta -> {
                globalVar.throwIfNotIntType(op)
                val currentValue = globalVar.valueList()
                val intDelta = stringValueToWrite.trim().toLong()
                val newValueInt: Long = if (currentValue.isEmpty()) {
                    -intDelta
                } else {
                    currentValue[0].toLong() - intDelta
                }
                globalVar.setValue(listOf(newValueInt.toString()))
            }
        }

        logger.logOnDebug("Write global var, before: ${globalVar.valueList()}")
        logger.logOnDebug("Write global var, after: ${newGlobalVar.valueList()}")

        return newGlobalVar
    }

    private fun GlobalVar.throwIfNotList(op: WriteGlobalVarOp) {
        require(isList()) {
            "$op can only be applied to List type."
        }
    }

    private fun GlobalVar.throwIfNotGenericType(op: WriteGlobalVarOp) {
        require(!isList()) {
            "$op can only be applied to generic type."
        }
    }

    private fun GlobalVar.throwIfNotIntType(op: WriteGlobalVarOp) {
        require(isInt()) {
            "$op can only be applied to Int type."
        }
    }
}