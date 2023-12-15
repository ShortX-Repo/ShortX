package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.ComponentName
import android.content.Context
import android.view.View
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.IShortX
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.notification.INotificationPoster
import tornaco.apps.shortx.core.notification.NotificationPoster
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.platform.ClassNames.SYSTEM_UI_APP
import tornaco.apps.shortx.core.platform.ClassNames.SYSTEM_UI_CUSTOM_TILE
import tornaco.apps.shortx.core.platform.Methods.onCreate
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.common.QSTile
import tornaco.apps.shortx.core.proto.common.QSTileList
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.su.LibShKtShellImpl
import tornaco.apps.shortx.core.systemui.IQSTileHost
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.logAdapter
import util.Reflections
import java.util.Collection

object SystemUIHook {
    private val logger = Logger("ShortXHook.SystemUIHook")

    fun hookTile(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        if (PackageNames.systemUI == lpparam.packageName) {
            afterMethod(
                Reflections.findClass(
                    SYSTEM_UI_CUSTOM_TILE,
                    lpparam.classLoader
                ),
                "handleClick"
            ) { param ->
                logger.d("handleClick: ${param.thisObject}")
                ShortXManagerNative.shortX?.let {
                    if (it.isQSTileOptEnabled) {
                        val name: ComponentName =
                            Reflections.getObjectField(
                                param.thisObject,
                                "mComponent"
                            ) as ComponentName
                        if (name.packageName.contains(BuildProp.APP_PACKAGE_NAME)) {
                            // tornaco.apps.shortx/tornaco.apps.shortx.ui.service.tile.Tile7
                            val tileName = name.className.substringAfterLast(".")
                            val tile = StatusBarTile.valueOf(tileName)
                            logger.d("handleClick: $name, tile: $tile")
                            it.onTileClick(tile.number)
                            param.result = null
                        }
                    }
                }
            }
        }
    }.onFailure {
        logger.e(it, "hookTile")
    }

    fun hookQS(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        if (PackageNames.systemUI == lpparam.packageName) {
            afterMethod(
                clazz = Reflections.findClass(
                    "com.android.systemui.qs.QSTileHost",
                    lpparam.classLoader
                ),
                methodName = "onTuningChanged"
            ) { param ->
                logger.d("onTuningChanged: ${param.args.map { it }}")
                val shortx = ShortXManagerNative.shortX
                logger.i("ShortX: $shortx")
                shortx?.attachQSTileHost(QSTileHostImpl(param.thisObject, shortx))
            }
        }
    }.onFailure {
        logger.e(it, "hookTile")
    }

    fun hookApp(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        if (PackageNames.systemUI == lpparam.packageName) {
            afterMethod(
                Reflections.findClass(SYSTEM_UI_APP, lpparam.classLoader),
                onCreate
            ) { param ->
                val shortx = ShortXManagerNative.shortX
                if (shortx == null) {
                    XposedBridge.log("systemUI hook. ERROR, shortX is null")
                    return@afterMethod
                }
                logger.i("ShortX: $shortx")

                // Install log
                logAdapter = { _: Int, tag: String, msg: String ->
                    shortx.log("$tag - $msg")
                }

                val app: Context = param.thisObject as Context
                logger.d("After onCreate: $app")
                installShellImpl(shortx)
                installNotificationPoster(shortx, app)
            }
        }
    }.onFailure {
        logger.e(it, "hookApp")
    }

    private fun installShellImpl(shortx: IShortX) {
        logger.d("installShellImpl")
        val shell = LibShKtShellImpl
        shortx.attachSUImpl("SystemUI", shell)
        logger.i("installShellImpl success.")
    }

    private fun installNotificationPoster(shortx: IShortX, context: Context) {
        logger.d("installNotificationPoster")
        val appRes = AppResources(
            context,
            BuildProp.APP_PACKAGE_NAME
        )
        val notificationPoster by lazy {
            NotificationPoster(
                context,
                "ShortX silence",
                "ShortX alert",
                "ShortX",
                getIcon = { remixNam ->
                    val resName = Remix.allFlat[remixNam]
                    resName?.let {
                        appRes.getIcon(it)
                    }
                },
                getBitmap = {
                    appRes.getBitmap(it)
                },
                onButtonClick = { button ->
                    button.actionList.forEach {
                        val evaluateContext = EvaluateContext.newBuilder()
                            .setSrcId("Notification button")
                            .setSrcEvent(ContextSrcEvent.ContextSrc_Rule)
                            .setSrcTitle(button.label)
                            .build()
                        shortx.executeAction(
                            ByteArrayWrapper(evaluateContext.toByteArray()),
                            ByteArrayWrapper(it.toByteArray())
                        )
                    }
                },
                onPostNotificationClick = { post ->
                    post.clickActionList.forEach {
                        val evaluateContext = EvaluateContext.newBuilder()
                            .setSrcId("Notification")
                            .setSrcEvent(ContextSrcEvent.ContextSrc_Rule)
                            .setSrcTitle(post.title)
                            .build()
                        shortx.executeAction(
                            ByteArrayWrapper(evaluateContext.toByteArray()),
                            ByteArrayWrapper(it.toByteArray())
                        )
                    }
                }
            )
        }

        val stub = object : INotificationPoster.Stub() {
            override fun post(data: ByteArrayWrapper) {
                runCatching {
                    val n = PostNotification.parseFrom(data.byteData)
                    notificationPoster.invoke(n)
                }
            }
        }

        shortx.attachNotificationPoster("SystemUI", stub)
        logger.i("installNotificationPoster success.")
    }
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "UNCHECKED_CAST")
class QSTileHostImpl(private val hostObj: Any, private val shortx: IShortX) : IQSTileHost.Stub() {
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
                        // void handleClick(@Nullable View view)
                        Reflections.callMethod(tile, "handleClick", arrayOf(View::class.java), null)
                    } else {
                        // Normal android
                        // click(View v);

                        // Flyme:
                        //   @Override // com.android.systemui.plugins.qs.QSTile
                        //    public void click() {
                        //        this.mMetricsLogger.write(populate(new LogMaker(925).setType(4).addTaggedData(1592, Integer.valueOf(this.mStatusBarStateController.getState()))));
                        //        this.mUiEventLogger.logWithInstanceId(QSEvent.QS_ACTION_CLICK, 0, getMetricsSpec(), getInstanceId());
                        //        this.mQSLogger.logTileClick(this.mTileSpec, this.mStatusBarStateController.getState(), this.mState.state);
                        //        this.mHandler.sendEmptyMessage(2);
                        //        SystemUIMobEventUtils.collectClickNumberOfShortCut(this.mContext, this.mTileSpec);
                        //    }
                        runCatching {
                            Reflections.callMethod(tile, "click", arrayOf(View::class.java), null)
                        }.onFailure {
                            logger.w("Call click(view) fail, try click()")
                            Reflections.callMethod(tile, "click")
                        }
                    }
                }
            }
        }.onFailure {
            shortx.reportFATALError(it.stackTraceToString())
        }
    }

    override fun longClick(tileSpec: String) {
        runCatching {
            onEachTiles { spec, tile ->
                if (spec == tileSpec) {
                    logger.d("long click: $spec")
                    runCatching {
                        Reflections.callMethod(tile, "longClick", arrayOf(View::class.java), null)
                    }.onFailure {
                        logger.w("Call longClick(view) fail, try longClick()")
                        Reflections.callMethod(tile, "longClick")
                    }
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
        val tiles = invokeGetTiles()
        logger.d("onEachTiles, tiles: ${tiles.map { it }}")
        tiles.forEach {
            val spec = Reflections.callMethod(it, "getTileSpec") ?: it.toString()
            action(spec.toString(), it)
        }
    }

    private fun getTilesInternal(): QSTileList = runCatching {
        val tiles = invokeGetTiles()
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

    private fun invokeGetTiles() = runCatching {
        Reflections.callMethod(hostObj, "getTiles") as Collection<*>
    }.getOrElse {
        if (it is NoSuchMethodError) {
            logger.w("NoSuchMethodError when call getTiles(), try to get field: mTiles")
            // LinkedHashMap<String, QSTile> mTiles = new LinkedHashMap<>()
            val map = Reflections.getObjectField(hostObj, "mTiles") as Map<*, *>
            map.values
        } else {
            throw Error(it)
        }
    }.filterNotNull()

}


