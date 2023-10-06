package tornaco.apps.shortx.ui.biometric

import android.content.Context
import android.content.Intent
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.action.BiometricVerify

object BiometricVerifyActivityStarter {
    const val EXTRA_REQUEST_CODE = "extra.request.code"
    const val EXTRA_TITLE = "extra.title"
    const val EXTRA_SUBTITLE = "extra.subtitle"
    const val RESULT_CASE = "result.case"

    @JvmStatic
    fun start(context: Context, requestCode: Int, action: BiometricVerify) {
        val starter = Intent("")
            .setPackage(BuildProp.APP_PACKAGE_NAME)
            .setClassName(
                BuildProp.APP_PACKAGE_NAME,
                "tornaco.apps.shortx.ui.biometric.BiometricVerifyActivity"
            )
            .putExtra(EXTRA_REQUEST_CODE, requestCode)
            .putExtra(EXTRA_TITLE, action.title)
            .putExtra(EXTRA_SUBTITLE, action.subTitle)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(starter)
    }
}