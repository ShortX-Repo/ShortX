package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.view.View
import tornaco.apps.shortx.core.IShortX
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.QSTile
import tornaco.apps.shortx.core.proto.common.QSTileList
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.systemui.IQSTileHost
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.xposed.hooks.hook.Func
import tornaco.apps.shortx.services.xposed.hooks.hook.altFuncs
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.QSHostAdapter.getTiles
import util.Reflections

class QSTileHostImpl(private val shortx: IShortX) : IQSTileHost.Stub() {
    private val logger = Logger("ShortXHook.SystemUIHook.QSTileHostImpl")

    override fun getTiles(receiver: SynchronousResultReceiver) {
        receiver.send(ByteArrayWrapper(getTilesInternal().toByteArray()))
    }

    override fun click(tileSpec: String) {
        runCatching {
            onEachTiles { spec, tile ->
                if (spec == tileSpec) {
                    logger.d("click: $spec")
                    val isCustomTile = tile.javaClass.name.endsWith("CustomTile")
                    logger.d("Tile class: ${tile.javaClass.name}")
                    if (isCustomTile) {
                        clickCustomTile(tile)
                    } else {
                        clickStockTile(tile)
                    }
                }
            }
        }.onFailure {
            shortx.reportFATALError(it.stackTraceToString())
        }
    }

    private fun clickStockTile(tile: Any) {
        altFuncs(
            // Android 15.
            Func("Android15 - handleClick(Expandable)",
                { OsUtils.isVOrAbove() }) {
                val expandableClazz = Reflections.findClass(
                    "com.android.systemui.animation.Expandable",
                    tile.javaClass.classLoader
                )
                Reflections.callMethod(
                    tile,
                    "handleClick",
                    arrayOf(expandableClazz),
                    null
                )
            },

            // Normal android
            // click(View v);
            Func("Android - click(View v)", { true }) {
                Reflections.callMethod(tile, "click", arrayOf(View::class.java), null)
            },

            // Flyme
            //   @Override // com.android.systemui.plugins.qs.QSTile
            //    public void click() {
            //        this.mMetricsLogger.write(populate(new LogMaker(925).setType(4).addTaggedData(1592, Integer.valueOf(this.mStatusBarStateController.getState()))));
            //        this.mUiEventLogger.logWithInstanceId(QSEvent.QS_ACTION_CLICK, 0, getMetricsSpec(), getInstanceId());
            //        this.mQSLogger.logTileClick(this.mTileSpec, this.mStatusBarStateController.getState(), this.mState.state);
            //        this.mHandler.sendEmptyMessage(2);
            //        SystemUIMobEventUtils.collectClickNumberOfShortCut(this.mContext, this.mTileSpec);
            //    }
            Func("Flyme - click()", { true }) {
                Reflections.callMethod(tile, "click")
            },

            // Some MIUI
            // // com.android.systemui.qs.external.CustomTile.handleClick():void
            Func("MIUI - handleClick", { true }) {
                Reflections.callMethod(tile, "handleClick")
            }
        )
    }

    private fun clickCustomTile(tile: Any) {
        altFuncs(
            Func("handleClick(@Nullable Expandable)", { OsUtils.isVOrAbove() }) {
                val expandableClazz = Reflections.findClass(
                    "com.android.systemui.animation.Expandable",
                    tile.javaClass.classLoader
                )
                Reflections.callMethod(
                    tile,
                    "handleClick",
                    arrayOf(expandableClazz),
                    null
                )
            },
            Func("handleClick(@Nullable View view)", { true }) {
                Reflections.callMethod(
                    tile,
                    "handleClick",
                    arrayOf(View::class.java),
                    null
                )
            },
            Func("handleClick()", { true }) {
                // Some MIUI systemui
                // com.android.systemui.qs.external.CustomTile.handleClick():void
                Reflections.callMethod(tile, "handleClick")
            },
        )
    }

    override fun longClick(tileSpec: String) {
        runCatching {
            onEachTiles { spec, tile ->
                if (spec == tileSpec) {
                    logger.d("long click: $spec")
                    altFuncs(
                        Func("longClick(view)", { true }) {
                            Reflections.callMethod(
                                tile,
                                "longClick",
                                arrayOf(View::class.java),
                                null
                            )
                        },
                        Func("longClick()", { true }) {
                            Reflections.callMethod(tile, "longClick")
                        },
                    )
                }
            }
        }.onFailure {
            shortx.reportFATALError(it.stackTraceToString())
        }
    }

    override fun getState(tileSpec: String): Int {
        return runCatching {
            var state = -1
            onEachTiles { spec, tile ->
                if (spec == tileSpec) {
                    logger.d("getState found spec: $spec")
                    val stateObj = Reflections.callMethod(tile, "getState")
                    logger.d("getState stateObj: $stateObj")
                    if (stateObj != null) {
                        val stateInt = Reflections.getIntField(stateObj, "state")
                        logger.d("getState stateInt: $stateInt")
                        state = stateInt
                    }
                }
            }
            state
        }.getOrElse {
            shortx.reportFATALError(it.stackTraceToString())
            -1
        }
    }

    private fun onEachTiles(action: (spec: String, Any) -> Unit) {
        val tiles = getTiles().filterNotNull()
        logger.d("onEachTiles, tiles: ${tiles.map { it }}")
        tiles.forEach {
            val spec = Reflections.callMethod(it, "getTileSpec") ?: it.toString()
            action(spec.toString(), it)
        }
    }

    private fun getTilesInternal(): QSTileList = runCatching {
        val tiles = getTiles()
        logger.d("getTilesInternal, tiles: ${tiles.map { it }}")
        val qsTiles = tiles.mapNotNull {
            runCatching {
                val spec = Reflections.callMethod(it, "getTileSpec") ?: it.toString()
                val label = Reflections.callMethod(it, "getTileLabel") ?: it.toString()
                logger.d("Spec: $spec - $label")
                QSTile.newBuilder().setTileSpec(spec.toString()).setLabel(label.toString()).build()
            }.getOrElse {
                logger.e(it, "getTilesInternal")
                null
            }
        }
        QSTileList.newBuilder().addAllTile(qsTiles).build()
    }.getOrElse {
        logger.e(it, "getTilesInternal")
        QSTileList.getDefaultInstance()
    }
}


