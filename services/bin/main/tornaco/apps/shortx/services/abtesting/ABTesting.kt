package tornaco.apps.shortx.services.abtesting

import tornaco.apps.shortx.core.util.Logger

object ABTesting {
    private val logger = Logger("ABTesting")

    val test = Test(false)

    fun dump() {
        logger.w("Test: $test")
    }
}

data class Test(
    val dummy: Boolean
)