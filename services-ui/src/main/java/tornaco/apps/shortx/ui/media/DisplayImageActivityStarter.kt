package tornaco.apps.shortx.ui.media

import android.content.Context
import android.content.Intent
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.proto.action.DisplayImage

object DisplayImageActivityStarter {
    const val EXTRA_REQUEST_CODE = "extra.request.code"
    const val EXTRA_IMAGE_URL = "extra.url"
    const val EXTRA_IMAGE_SCALE_TYPE = "extra.scale.type"

    @JvmStatic
    fun start(context: Context, requestCode: Int, action: DisplayImage) {
        val starter = Intent("")
            .setPackage(BuildPropInternal.APP_PACKAGE_NAME)
            .setClassName(
                BuildPropInternal.APP_PACKAGE_NAME,
                "tornaco.apps.shortx.ui.media.DisplayImageActivity"
            )
            .putExtra(EXTRA_REQUEST_CODE, requestCode)
            .putExtra(EXTRA_IMAGE_URL, action.imageUrl)
            .putExtra(EXTRA_IMAGE_SCALE_TYPE, action.scaleType.name)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(starter)
    }
}