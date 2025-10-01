package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
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
        logger.w("getTiles isVOrAbove? ${OsUtils.isVOrAbove()}")
        return if (OsUtils.isVOrAbove()) {
            val tiles = getTilesAndroid15()
            tiles ?: getTilesAndroid14()
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
        logger.w("getTilesAndroid15, _QSHostAdapter: $_QSHostAdapter")
        if (_QSHostAdapter == null) {
            logger.e("getTilesAndroid15, _QSHostAdapter == null")
            return null
        }
        return runCatching {
            // public final CurrentTilesInteractor interactor;
            val interactor = runCatching {
                Reflections.getObjectField(_QSHostAdapter, "interactor")
            }.getOrNull()
            logger.w("getTilesAndroid15, interactor: $interactor")
            // 两种都尝试一下，防止有的rom，有interactor但是又没用?
            val adapterTiles: List<Any> = runCatching {
                (Reflections.callMethod(
                    _QSHostAdapter,
                    "getTiles"
                ) as Collection<*>).filterNotNull()
            }.getOrElse {
                logger.w("_QSHostAdapter-getTiles error ${it.message}")
                emptyList()
            }
            logger.d("getTilesAndroid15-adapterTiles $adapterTiles")

            val interactorTiles: List<Any> = interactor?.let { ir ->
                runCatching {
                    (KotlinTileAccessor.getTiles(ir) as Collection<*>).filterNotNull()
                }.getOrElse {
                    logger.w("interactor-getTiles error ${it.message}")
                    emptyList()
                }
            } ?: emptyList()
            logger.d("getTilesAndroid15-interactorTiles $adapterTiles")
            (adapterTiles.toMutableList().apply {
                addAll(interactorTiles)
            }).distinct()
        }.getOrElse {
            logger.e(it, "getTilesAndroid15")
            null
        }
    }
}