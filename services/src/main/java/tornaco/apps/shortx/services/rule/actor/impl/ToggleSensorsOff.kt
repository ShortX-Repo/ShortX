package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.SensorPrivacyManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.keyguardManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object ToggleSensorsOff : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ToggleSensorsOff::class.java
    }

    @SuppressLint("MissingPermission", "NewApi")
    override suspend fun handle(params: HandleParams) = with(params) {
        require(OsUtils.isSOrAbove()) {
            "Sensor off feature is not supported on lower Android version. (require 12+)"
        }
        val sensorPriv = androidContext.getSystemService(Context.SENSOR_PRIVACY_SERVICE) as SensorPrivacyManager
        val currentEnabled = sensorPriv.isAllSensorPrivacyEnabled
        val targetEnabled = !currentEnabled
        if (targetEnabled && androidContext.keyguardManager.isKeyguardLocked) {
            throw IllegalStateException("Keyguard is locked.")
        }
        sensorPriv.setAllSensorPrivacy(targetEnabled)
        Actor.Res.EMPTY
    }
}
