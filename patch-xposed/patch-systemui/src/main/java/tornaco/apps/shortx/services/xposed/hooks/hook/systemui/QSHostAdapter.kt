package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.xposed.hooks.hook.ClazzDumper
import util.Reflections

object QSHostAdapter {
    private val logger = Logger("QSHostAdapter")

    // Android 15+
    var _QSHostAdapter: Any? = null
        set(value) {
            field = value
            logger.w("_QSHostAdapter -> $value")
        }

    var qsTileHost: Any? = null
        set(value) {
            field = value
            logger.w("qsTileHost -> $value")
        }

    fun getTiles(): List<*> {
        return if (OsUtils.isVOrAbove()) {
            getTilesAndroid15() ?: getTilesAndroid14()
        } else {
            getTilesAndroid14()
        }
    }

    private fun getTilesAndroid14(): List<*> {
        return runCatching {
            Reflections.callMethod(
                requireNotNull(qsTileHost) { "qsTileHost is null." },
                "getTiles"
            ) as Collection<*>
        }.getOrElse {
            if (it is NoSuchMethodError) {
                logger.w("NoSuchMethodError when call getTiles(), try to get field: mTiles")
                // LinkedHashMap<String, QSTile> mTiles = new LinkedHashMap<>()
                val map = Reflections.getObjectField(qsTileHost, "mTiles") as Map<*, *>
                map.values
            } else {
                throw Error(it)
            }
        }.filterNotNull()
    }

    private fun getTilesAndroid15(): List<*>? {
        if (_QSHostAdapter == null) {
            logger.e("getTilesAndroidV, _QSHostAdapter == null")
            return null
        }
        return runCatching {
            (Reflections.callMethod(_QSHostAdapter, "getTiles") as Collection<*>).filterNotNull()
        }.getOrElse {
            ClazzDumper.dump(_QSHostAdapter?.javaClass)
            logger.e(it, "getTilesAndroidV")
            null
        }
    }
}