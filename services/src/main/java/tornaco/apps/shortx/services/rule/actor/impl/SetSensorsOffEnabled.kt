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
    ): Unit = with(params) {
        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.SetSensorsOffEnabled::class.java
        val enable = data.enable
        if (OsUtils.isSOrAbove()) {
            if (enable && context.keyguardManager.isKeyguardLocked) {
                logger.w("Keyguard is locked, skip.")
                return@with
            }
            logger.i("setAllSensorPrivacy: $enable")
            val sensorPriv: SensorPrivacyManager =
                context.getSystemService(Context.SENSOR_PRIVACY_SERVICE) as SensorPrivacyManager
            sensorPriv.setAllSensorPrivacy(enable)
        }
    }
}