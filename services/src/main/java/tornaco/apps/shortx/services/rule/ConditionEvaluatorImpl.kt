package tornaco.apps.shortx.services.rule

import android.content.Context
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.proto.condition.*
import tornaco.apps.shortx.core.rule.*
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.MVELExecutor

class ConditionEvaluatorImpl(
    private val context: Context,
    private val shortXContext: ShortXContext
) : ConditionEvaluator {
    private val logger = Logger("ConditionEvaluatorImpl")

    private fun evaluate(condition: ProtoAny, contextData: ContextData): Boolean {
        return when {
            condition is_ CurrentPkgList::class.java -> {
                val currentPkg = condition unpack_ CurrentPkgList::class.java
                val state = shortXContext.appState.currentFgComponentName.value
                val requiredPkgList = if (currentPkg.pkgSetsList.isNotEmpty()) {
                    currentPkg.pkgSetsList.flatMap {
                        shortXContext.pkgSet.getPkgSetById(it)?.appPkgsList ?: emptyList()
                    }
                } else {
                    currentPkg.pkgsList
                }
                requiredPkgList.any {
                    it.pkgName == state.pkg.pkgName && it.userId == state.pkg.userId
                }
            }
            condition is_ True::class.java -> {
                true
            }
            condition is_ False::class.java -> {
                false
            }
            condition is_ MatchMVEL::class.java -> {
                val mvel = condition unpack_ MatchMVEL::class.java
                val res = MVELExecutor(context, shortXContext, mvel.expression).invoke()
                kotlin.runCatching { res as Boolean }.getOrElse {
                    logger.e(it, "MatchMVEL check error")
                    false
                }
            }
            condition is_ EvaluateGlobalVar::class.java -> {
                val data = condition unpack_ EvaluateGlobalVar::class.java
                val globalVar = shortXContext.rule.getGlobalVarById(data.varId)
                if (globalVar == null) {
                    logger.w("EvaluateGlobalVar, var with id: ${data.varId} not found.")
                    return false
                }
                val type: GlobalVarType? = globalVar.type.toType()
                if (type == null) {
                    logger.w("EvaluateGlobalVar, var with id: ${data.varId} unknown type.")
                    return false
                }

                val emptyValue = emptyList<String>()
                val currentValue = globalVar.valueList(noValueReturn = { emptyValue })
                val currentValueHasBeenSet = currentValue.isNotEmpty()
                logger.d("EvaluateGlobalVar, var: ${globalVar.name}, currentValueHasBeenSet: $currentValueHasBeenSet, currentValue: $currentValue")

                return when (data.op as GlobalVarOp) {
                    GlobalVarOp.GreaterThan -> {
                        if (!currentValueHasBeenSet) {
                            false
                        } else if (type == GlobalVarType.Int) {
                            kotlin.runCatching {
                                currentValue.first()
                                    .toLong() > data.payload.value.inflateContextMap(contextData)
                                    .toLong()
                            }.getOrElse {
                                logger.e(it, "GreaterThan err")
                                false
                            }
                        } else {
                            false
                        }
                    }
                    GlobalVarOp.LessThan -> {
                        if (!currentValueHasBeenSet) {
                            false
                        } else if (type == GlobalVarType.Int) {
                            kotlin.runCatching {
                                currentValue.first()
                                    .toLong() < data.payload.value.inflateContextMap(contextData)
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
                                )
                            }.getOrElse {
                                logger.e(it, "EqualTo err")
                                false
                            }
                        } else {
                            kotlin.runCatching {
                                currentValue == data.payload.value.splitAsGlobalVarOpPayloadStringList()
                                    .map {
                                        it.inflateContextMap(contextData)
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
                                    data.payload.value.inflateContextMap(contextData),
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
                                        it.inflateContextMap(contextData)
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
                                        data.payload.value.inflateContextMap(contextData),
                                        ignoreCase = true
                                    )
                            }.getOrElse {
                                logger.e(it, "DoesNotContain-String err")
                                false
                            }
                        } else if (globalVar.isList()) {
                            kotlin.runCatching {
                                data.payload.value.splitAsGlobalVarOpPayloadStringList().map {
                                    it.inflateContextMap(contextData)
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
                    GlobalVarOp.UNRECOGNIZED -> {
                        false
                    }
                }
            }
            else -> false
        }
    }

    fun tryEvaluate(condition: ProtoAny, contextData: ContextData): Boolean {
        return kotlin.runCatching { evaluate(condition, contextData) }.getOrElse {
            logger.e(it, "tryEvaluate condition error")
            false
        }
    }

    override fun evaluate(
        logTag: String,
        condOp: ConditionOperator,
        condOpPayload: ConditionOperatorPayload,
        conditions: List<ProtoAny>,
        contextData: ContextData
    ): Boolean {
        logger.d("evaluate rule condition: $logTag $condOp condition size: ${conditions.size}, context: $contextData")
        return when (condOp) {
            ConditionOperator.ALL -> {
                conditions.all { tryEvaluate(it, contextData) }
            }
            ConditionOperator.ANY -> {
                conditions.any { tryEvaluate(it, contextData) }
            }
            ConditionOperator.NONE -> {
                conditions.none { tryEvaluate(it, contextData) }
            }
            ConditionOperator.MVEL -> {
                val expression = condOpPayload.expression
                if (expression.isNullOrBlank()) return false

                val varMap = mutableMapOf<String, Any>()
                var expressionTranslation = expression
                repeat(conditions.size) { index ->
                    val codeName = "c${index + 1}"
                    varMap[codeName] = ConditionCallable(
                        eva = this,
                        condition = conditions[index],
                        contextData = contextData
                    )
                    expressionTranslation =
                        expressionTranslation.replace(codeName, "$codeName.call()")
                    logger.d("replace codeName: $codeName, after replace: $expressionTranslation")
                }
                val res = MVELExecutor(context, shortXContext, expressionTranslation).invoke(varMap)
                logger.d("evaluate rule condition MVEL: $expressionTranslation, result: $res")
                return res.toString().toBooleanStrictOrNull() ?: false
            }
            else -> {
                conditions.all { tryEvaluate(it, contextData) }
            }
        }
    }
}