package tornaco.apps.shortx.services.rule.cond.impl

import android.content.Context
import android.hardware.camera2.CameraManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.cameraManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.cond.Cond

object FlashlightIsOn : Cond {
    @Volatile
    private var flashLightEnabled = false

    private val torchCallback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            super.onTorchModeChanged(cameraId, enabled)
            flashLightEnabled = enabled
        }
    }

    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.FlashlightIsOn::class.java

    override fun init(androidContext: Context, shortXContext: ShortXContext) {
        super.init(androidContext, shortXContext)
        androidContext.cameraManager.registerTorchCallback(torchCallback, null)
    }

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.FlashlightIsOn::class.java

            if (data.isInvert) {
                !flashLightEnabled
            } else {
                flashLightEnabled
            }.toResult()
        }
    }
}
