package tornaco.apps.shortx.services.app

import android.content.ComponentName
import android.util.ArrayMap
import tornaco.apps.shortx.core.util.Logger
import util.ReflectionUtils
import util.Reflections
import java.lang.reflect.Method

object ActiveServiceFunc {
    private val logger = Logger("ActiveServiceFunc")
    private const val stopServiceLockMethodName = "stopServiceLocked"
    private var stopServiceMethod: StopServiceMethod? = null

    // ArrayMap<ComponentName, ServiceRecord> getServicesLocked(int callingUser) {
    //     return getServiceMapLocked(callingUser).mServicesByName;
    // }
    //

    fun getServicesLocked(asIns: Any, userId: Int): ArrayMap<ComponentName, Any> {
        val arrayMap = Reflections.callMethod(asIns, "getServicesLocked", userId)
        @Suppress("UNCHECKED_CAST")
        return arrayMap as ArrayMap<ComponentName, Any>
    }

    fun stopService(asIns: Any, serviceRecordObj: Any) {
        if (stopServiceMethod == null) {
            stopServiceMethod = findStopServiceMethod(asIns, serviceRecordObj)
            logger.d("stopServiceMethod: $stopServiceMethod")
        }
        requireNotNull(stopServiceMethod) { "Can not find stopServiceMethod." }.call(
            asIns,
            serviceRecordObj
        )
        logger.d("stopService: $serviceRecordObj")
    }

    private fun findStopServiceMethod(asIns: Any, serviceRecordObj: Any): StopServiceMethod? {
        val serviceRecordObjClass: Class<*> = serviceRecordObj.javaClass
        var stopMethod = ReflectionUtils.findMethod(
            asIns.javaClass,
            stopServiceLockMethodName,
            serviceRecordObjClass
        )
        if (stopMethod != null) {
            return StopServiceMethod(stopMethod)
        }

        // Adopt for Android 12.
        stopMethod = ReflectionUtils.findMethod(
            asIns.javaClass,
            stopServiceLockMethodName,
            serviceRecordObjClass,
            Boolean::class.javaPrimitiveType
        )
        return stopMethod?.let { StopServiceMethodS(it) }
    }

    private open class StopServiceMethod(val stopMethod: Method) {
        private val logger = Logger("StopServiceMethod")

        open fun call(asIns: Any, serviceRecordObj: Any) {
            ReflectionUtils.makeAccessible(stopMethod)
            ReflectionUtils.invokeMethod(stopMethod, asIns, serviceRecordObj)
            logger.i("Call success: $serviceRecordObj")
        }
    }

    private class StopServiceMethodS(method: Method) :
        StopServiceMethod(method) {
        private val logger = Logger("StopServiceMethodS")

        override fun call(asIns: Any, serviceRecordObj: Any) {
            ReflectionUtils.makeAccessible(stopMethod)
            ReflectionUtils.invokeMethod(stopMethod, asIns, serviceRecordObj, true)
            logger.i("Call success: $serviceRecordObj")
        }
    }
}