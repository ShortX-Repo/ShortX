package tornaco.apps.shortx.services.rule.cond.impl

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.SensorPrivacyManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.rule.cond.Cond

object RequireSensorOff : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireSensorOff::class.java

    @SuppressLint("NewApi", "MissingPermission")
    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        val data =
            param.condition.unpack_(tornaco.apps.shortx.core.proto.condition.RequireSensorOff::class.java)
        val requiredOn = data.isRequireOn

        require(OsUtils.isSOrAbove()) {
            "Sensor off feature is not supported on lower Android version. (require 12+)"
        }

        val sensorPriv: SensorPrivacyManager =
            param.context.getSystemService(Context.SENSOR_PRIVACY_SERVICE) as SensorPrivacyManager
        val currentEnabled = sensorPriv.isAllSensorPrivacyEnabled
        logger.d("isAllSensorPrivacyEnabled: $currentEnabled")

        val res = currentEnabled == requiredOn

        return if (data.isInvert) {
            !res
        } else {
            res
        }.toResult()
    }
}