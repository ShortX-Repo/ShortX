package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.hardware.camera2.CameraManager
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.FlashLightStatusChanged
import tornaco.apps.shortx.core.util.cameraManager
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ShortXContext

class TorchStateTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) : Trigger {
    private val torchCallback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            super.onTorchModeChanged(cameraId, enabled)
            shortXContext.rule.publishFact(
                "onTorchModeChanged",
                FlashLightStatusChanged.newBuilder()
                    .setOoa(enabled.select(OnOffAny.On, OnOffAny.Off))
                    .build()
            )
        }
    }

    override fun register() {
        context.cameraManager.registerTorchCallback(torchCallback, shortXContext.rule.handler)
    }

    override fun unRegister() {
        context.cameraManager.unregisterTorchCallback(torchCallback)
    }
}