package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.proto.condition.EvaluateGlobalVar
import tornaco.apps.shortx.core.proto.condition.GlobalVarOp
import tornaco.apps.shortx.core.rule.GlobalVarType
import tornaco.apps.shortx.core.rule.isList
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.splitAsGlobalVarOpPayloadStringList
import tornaco.apps.shortx.core.rule.toType
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.rule.valueList
import tornaco.apps.shortx.services.rule.cond.Cond

object EvaluateGlobalVar : Cond {
    override fun condClass(): Class<out Message> = EvaluateGlobalVar::class.java

    override suspend fun evaluate(param: Cond.CondParam): Boolean {
        return with(param) {
            val data = condition unpack_ EvaluateGlobalVar::class.java
            val globalVar = shortXContext.rule.getGlobalVarByName(data.varName)

            if (globalVar == null) {
                logger.i("EvaluateGlobalVar, var with varName: ${data.varName} not found, op is: ${data.op}")
                return data.op == GlobalVarOp.IsNotExists
            }

            val type: GlobalVarType? = globalVar.type.toType()
            if (type == null) {
                logger.w("EvaluateGlobalVar, var with varName: ${data.varName} unknown type.")
                return false
            }

            val emptyValue = emptyList<String>()
            val currentValue = globalVar.valueList(noValueReturn = { emptyValue })
            contextData.add(
                ContextDataMapping.Condition.conditionMatchReason.name,
                "Current value: ${currentValue.joinToString()}"
            )
            val currentValueHasBeenSet = currentValue.isNotEmpty()
            logger.d("EvaluateGlobalVar, var: ${globalVar.name}, currentValueHasBeenSet: $currentValueHasBeenSet, currentValue: $currentValue")

            val res = when (data.op as GlobalVarOp) {
                GlobalVarOp.GreaterThan -> {
                    if (!currentValueHasBeenSet) {
                        false
                    } else if (type == GlobalVarType.Int) {
                        kotlin.runCatching {
                            currentValue.first()
                                .toLong() > data.payload.value.inflateContextMap(contextData)
                                .replaceGlobalVarValues {
                                    shortXContext.rule.getAllGlobalVars()
                                }
                                .toLong()
                        }.getOrElse {
                            logger.e(it, "GreaterThan err")
                            false
                        }
                    } else {
                        false
                    }
                }

                GlobalVarOp.GreaterThanOrEQ -> {
                    if (!currentValueHasBeenSet) {
                        false
                    } else if (type == GlobalVarType.Int) {
                        kotlin.runCatching {
                            currentValue.first()
                                .toLong() >= data.payload.value.inflateContextMap(contextData)
                                .replaceGlobalVarValues {
                                    shortXContext.rule.getAllGlobalVars()
                                }
                                .toLong()
                        }.getOrElse {
                            logger.e(it, "GreaterThanOrEQ err")
                            false
                        }
                    } else {
                        false
                    }
                }

                GlobalVarOp.LessThanOrEQ -> {
                    if (!currentValueHasBeenSet) {
                        false
                    } else if (type == GlobalVarType.Int) {
                        kotlin.runCatching {
                            currentValue.first()
                                .toLong() <= data.payload.value.inflateContextMap(contextData)
                                .replaceGlobalVarValues {
                                    shortXContext.rule.getAllGlobalVars()
                                }
                                .toLong()
                        }.getOrElse {
                            logger.e(it, "LessThanOrEQ err")
                            false
                        }
                    } else false
                }

                GlobalVarOp.LessThan -> {
                    if (!currentValueHasBeenSet) {
                        false
                    } else if (type == GlobalVarType.Int) {
                        kotlin.runCatching {
                            currentValue.first()
                                .toLong() < data.payload.value.inflateContextMap(contextData)
                                .replaceGlobalVarValues {
                                    shortXContext.rule.getAllGlobalVars()
                                }
                                .toLong()
                        }.getOrElse {
                            logger.e(it, "LessThan err")
                            false
                        }
                    } else false
                }

                GlobalVarOp.EqualTo -> {
                    if (!currentValueHasBeenSet) {
                        false
                    } else if (!globalVar.isList()) {
                        kotlin.runCatching {
                            currentValue.first() == data.payload.value.inflateContextMap(
                                contextData
                            ).replaceGlobalVarValues {
                                shortXContext.rule.getAllGlobalVars()
                            }
                        }.getOrElse {
                            logger.e(it, "EqualTo err")
                            false
                        }
                    } else {
                        kotlin.runCatching {
                            currentValue == data.payload.value.splitAsGlobalVarOpPayloadStringList()
                                .map {
                                    it.inflateContextMap(contextData).replaceGlobalVarValues {
                                        shortXContext.rule.getAllGlobalVars()
                                    }
                                }
                        }.getOrElse {
                            logger.e(it, "EqualTo-List err")
                            false
                        }
                    }
                }

                GlobalVarOp.Contains -> {
                    if (!currentValueHasBeenSet) {
                        false
                    } else if (type == GlobalVarType.String) {
                        kotlin.runCatching {
                            currentValue.first().contains(
                                data.payload.value.inflateContextMap(contextData)
                                    .replaceGlobalVarValues {
                                        shortXContext.rule.getAllGlobalVars()
                                    },
                                ignoreCase = true
                            )
                        }.getOrElse {
                            logger.e(it, "Contains-String err")
                            false
                        }
                    } else if (globalVar.isList()) {
                        kotlin.runCatching {
                            currentValue.containsAll(
                                data.payload.value.splitAsGlobalVarOpPayloadStringList().map {
                                    it.inflateContextMap(contextData).replaceGlobalVarValues {
                                        shortXContext.rule.getAllGlobalVars()
                                    }
                                })
                        }.getOrElse {
                            logger.e(it, "Contains-List err")
                            false
                        }
                    } else false
                }

                GlobalVarOp.DoesNotContain -> {
                    if (!currentValueHasBeenSet) {
                        false
                    } else if (type == GlobalVarType.String) {
                        kotlin.runCatching {
                            !currentValue.first()
                                .contains(
                                    data.payload.value.inflateContextMap(contextData)
                                        .replaceGlobalVarValues {
                                            shortXContext.rule.getAllGlobalVars()
                                        },
                                    ignoreCase = true
                                )
                        }.getOrElse {
                            logger.e(it, "DoesNotContain-String err")
                            false
                        }
                    } else if (globalVar.isList()) {
                        kotlin.runCatching {
                            data.payload.value.splitAsGlobalVarOpPayloadStringList().map {
                                it.inflateContextMap(contextData).replaceGlobalVarValues {
                                    shortXContext.rule.getAllGlobalVars()
                                }
                            }.none {
                                currentValue.contains(it)
                            }
                        }.getOrElse {
                            logger.e(it, "DoesNotContain-List err")
                            false
                        }
                    } else false
                }

                GlobalVarOp.NotSet -> {
                    !currentValueHasBeenSet
                }

                GlobalVarOp.HasBeenSet -> {
                    currentValueHasBeenSet
                }

                GlobalVarOp.IsEmpty -> {
                    if (!currentValueHasBeenSet) {
                        false
                    } else if (type == GlobalVarType.String) {
                        kotlin.runCatching {
                            currentValue.first().isEmpty()
                        }.getOrElse {
                            logger.e(it, "IsEmpty-String err")
                            false
                        }
                    } else if (globalVar.isList()) {
                        currentValue.isEmpty()
                    } else false
                }

                GlobalVarOp.IsNotEmpty -> {
                    if (!currentValueHasBeenSet) {
                        false
                    } else if (type == GlobalVarType.String) {
                        kotlin.runCatching {
                            currentValue.first().isNotEmpty()
                        }.getOrElse {
                            logger.e(it, "IsNotEmpty-String err")
                            false
                        }
                    } else if (globalVar.isList()) {
                        currentValue.isNotEmpty()
                    } else false
                }

                GlobalVarOp.IsExists -> {
                    true
                }

                GlobalVarOp.IsNotExists -> {
                    false
                }

                GlobalVarOp.UNRECOGNIZED -> {
                    false
                }
            }

            if (data.isInvert) {
                !res
            } else {
                res
            }
        }
    }
}