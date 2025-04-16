package tornaco.apps.shortx.services.xposed.hooks.hook

import tornaco.apps.shortx.core.util.Logger

private val logger = Logger("Alt")

fun altFuncs(vararg funs: Func) {
    run loop@{
        funs.forEach { func ->
            if (func.condition()) {
                logger.d("Trying: ${func.logTag}")
                runCatching {
                    func.action()
                    return@loop
                }.onFailure {
                    logger.d("${func.logTag} fails.")
                }
            }
        }
    }
}

data class Func(
    val logTag: String,
    val condition: () -> Boolean,
    val action: () -> Unit
)