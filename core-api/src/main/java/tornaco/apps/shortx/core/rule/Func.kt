package tornaco.apps.shortx.core.rule

import tornaco.apps.shortx.core.proto.func.FuncParameter
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.replaceLazy

const val FUNC_PARAM_REF_PREFIX = "argOf$"
private val logger = Logger("Func")

fun String.replaceFuncParameterInputs(funcParameters: List<FuncParameterInput>): String {
    // FAST Check if contains FUNC_PARAM_REF_PREFIX
    if (!this.contains(FUNC_PARAM_REF_PREFIX)) return this

    // FAST Check if it is empty
    if (funcParameters.isEmpty()) return this

    logger.v("funcParameters: ${funcParameters.map { it.name + "-" + it.value }}")

    var strWithParamValue = this
    funcParameters
        // In case name override: argOf$applist and argOf$app
        .sortedByDescending { it.name.length }
        .forEach { entry ->
            strWithParamValue =
                strWithParamValue.replaceLazy(FUNC_PARAM_REF_PREFIX + entry.name, {
                    entry.value
                })
        }
    return strWithParamValue
}

fun FuncParameter.asDefaultInput() = FuncParameterInput.newBuilder()
    .setName(this.name)
    .setValue(this.defaultValue)
    .build()

fun FuncParameterInput.toLogString() = "${name}-${value}"