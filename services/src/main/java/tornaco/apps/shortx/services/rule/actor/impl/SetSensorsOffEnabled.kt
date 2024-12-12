package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.SensorPrivacyManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.keyguardManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object SetSensorsOffEnabled : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetSensorsOffEnabled::class.java
    }

    // RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.SetSensorsOffEnabled::class.java
        val enable = data.enable
        require(OsUtils.isSOrAbove()) {
            "Sensor off feature is not supported on lower Android version. (require 12+)"
        }
        if (OsUtils.isSOrAbove()) {
            if (enable && androidContext.keyguardManager.isKeyguardLocked) {
                throw IllegalStateException("Keyguard is locked.")
            }
            logger.i("setAllSensorPrivacy: $enable")
            val sensorPriv: SensorPrivacyManager =
                androidContext.getSystemService(Context.SENSOR_PRIVACY_SERVICE) as SensorPrivacyManager
            sensorPriv.setAllSensorPrivacy(enable)
        }
        Actor.Res.EMPTY
    }
}