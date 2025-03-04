package tornaco.apps.shortx.services.abtesting

import android.os.Build
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.util.Logger

object ABTesting {
    private val logger = Logger("ABTesting")

    val test = Test(
        isOCRDetectPFDEnabled = BuildPropInternal.APP_IS_DEBUG || Build.MODEL == "23127PN0CC" || Build.MODEL == "Android SDK built for arm64"
    )

    fun dump() {
        logger.w("Test: $test")
    }
}

data class Test(
    val isOCRDetectPFDEnabled: Boolean
)