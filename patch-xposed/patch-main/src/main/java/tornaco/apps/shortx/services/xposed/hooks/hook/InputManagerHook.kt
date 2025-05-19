package tornaco.apps.shortx.services.xposed.hooks.hook

import android.annotation.SuppressLint
import android.view.KeyEvent
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.platform.Classes.inputManagerService
import tornaco.apps.shortx.core.platform.Classes.keyCombinationManager
import tornaco.apps.shortx.core.platform.Classes.singleKeyGestureDetector
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.android.ServiceHolder
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded
import util.ReflectionsExt
import java.lang.reflect.Method


object InputManagerHook {
    private val logger = Logger("ShortXHook.InputManagerHook")

    private var interceptKeyBeforeDispatching_KeyEvent_Index: Int? = null

    @SuppressLint("PrivateApi")
    fun hookInputManager(lpparam: ISystemServerLoaded.Param) {
        kotlin.runCatching {
            afterMethod(lpparam.classLoader.inputManagerService(), Methods.start) { param ->
                logger.d("inputManagerService start.")
                ServiceHolder.inputManagerService = param.thisObject
            }
        }.onFailure {
            logger.e(it, "inputManagerService")
        }

        kotlin.runCatching {
            afterConstruct(lpparam.classLoader.singleKeyGestureDetector()) {
                logger.logOnDebug("SingleKeyGestureDetector constructed. ${Throwable().stackTraceToString()}")
                val det = it.thisObject
                logger.logOnDebug("det- $det")
                ServiceHolder.singleKeyGestureDetector = det
            }
        }.onFailure {
            logger.e(it)
        }

        kotlin.runCatching {
            afterConstruct(lpparam.classLoader.keyCombinationManager()) {
                logger.logOnDebug("KeyCombinationManager constructed. ${Throwable().stackTraceToString()}")
                val manager = it.thisObject
                logger.logOnDebug("manager- $manager")
                ServiceHolder.keyCombinationManager = manager
            }
        }.onFailure {
            logger.e(it)
        }

        kotlin.runCatching {
            // public int interceptKeyBeforeQueueing(KeyEvent event, int policyFlags)
            beforeMethod(
                lpparam.classLoader.inputManagerService(),
                Methods.interceptKeyBeforeQueueing
            ) { param ->
                param.logParamsOnDebug()
                val keyEvent = param.args[0] as KeyEvent
                val policyFlags = param.args[1] as Int
                val res =
                    ShortXService.shortXContext.input.interceptKeyBeforeQueueing(
                        keyEvent,
                        policyFlags
                    )
                if (res != null) {
                    logger.logOnDebug { "Set interceptKeyBeforeQueueing res: $res" }
                    param.result = res
                }
            }
        }.onFailure {
            logger.e(it, "inputManagerService")
        }

        // public int interceptKeyBeforeDispatching((IBinder focus, KeyEvent event, int policyFlags)
        beforeMethod(
            lpparam.classLoader.inputManagerService(),
            Methods.interceptKeyBeforeDispatching
        ) { param ->
            param.logParamsOnDebug()

            if (interceptKeyBeforeDispatching_KeyEvent_Index == null) {
                interceptKeyBeforeDispatching_KeyEvent_Index =
                    ReflectionsExt.getFirstArgIndexLikeTypeForMethod(
                        param.method as Method,
                        KeyEvent::class.java.name
                    )
                logger.w("interceptKeyBeforeDispatching_KeyEvent_Index: $interceptKeyBeforeDispatching_KeyEvent_Index")
            }

            interceptKeyBeforeDispatching_KeyEvent_Index?.let {
                val keyEvent = param.args[it] as KeyEvent
                val res =
                    ShortXService.shortXContext.input.interceptKeyBeforeDispatching(keyEvent)
                if (res != null) {
                    logger.logOnDebug { "Set interceptKeyBeforeDispatching res: $res" }
                    param.result = res
                }
            }
        }
    }
}
