package tornaco.apps.shortx.services

import android.annotation.SuppressLint
import android.content.Context
import android.os.Binder
import android.os.Parcel
import android.os.ServiceManager
import android.view.InputEvent
import android.view.MotionEvent
import tornaco.apps.shortx.GuideLineView
import tornaco.apps.shortx.core.IPC_CODE
import tornaco.apps.shortx.core.IShortX
import tornaco.apps.shortx.core.PROXY_SERVICE_NAME
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.core.util.statusBarManager

@SuppressLint("StaticFieldLeak")
object ShortXService : SystemService() {

    private val serviceBinder = ShortXBinderService()

    private val handler = newHandlerOfNewThread("ShortXService")
    private var listener: SystemGesturesPointerEventListener? = null

    private var firingArea: Area? = null

    private val removeFiring = Runnable {
        firingArea = null
    }

    override fun start(context: Context) {
        super.start(context)
    }

    override fun systemReady() {
        super.systemReady()
        publishBinderService()

        settings.apply {
            readBlocking()
            logger.d("data-id before = ${getString("data-id")}")
            putString("data-id", System.currentTimeMillis().toString())
            logger.d("data-id = ${getString("data-id")}")
        }

        listener = SystemGesturesPointerEventListener(context,
            handler,
            object : SystemGesturesPointerEventListener.CallbacksAdapter() {
                override fun onUpOrCancel() {
                    super.onUpOrCancel()
                    handler.removeCallbacks(removeFiring)
                    handler.postDelayed(removeFiring, 200)
                }

                override fun onShowPress(x: Int, y: Int) {
                    super.onShowPress(x, y)
                    logger.d("onShowPress: $x $y")
                    val screenSize = context.screenSize()
                    val matching = allHotSeats.firstOrNull {
                        it.getRect(screenSize.x.toFloat(), screenSize.y.toFloat())
                            .contains(x.toFloat(), y.toFloat())
                    }
                    if (matching != null && matching.area == Area.RightBottom) {
                        handler.removeCallbacks(removeFiring)
                        firingArea = matching.area
                    }
                    logger.d("onShowPress: $firingArea")
                }

                override fun onDoubleTap(x: Int, y: Int) {
                    super.onDoubleTap(x, y)
                    logger.d("onDoubleTap: $x $y")
                    val screenSize = context.screenSize()
                    val matching = allHotSeats.firstOrNull {
                        it.getRect(screenSize.x.toFloat(), screenSize.y.toFloat())
                            .contains(x.toFloat(), y.toFloat())
                    }
                    if (matching != null && matching.area == Area.RightBottom) {
                        handler.removeCallbacks(removeFiring)
                        firingArea = matching.area
                    }
                    logger.d("onDoubleTap: $firingArea")
                }

                override fun onSwipeFromTop() {
                    logger.d("onSwipeFromTop")
                }

                override fun onSwipeFromBottom(x: Int, y: Int) {
                    logger.d("onSwipeFromBottom: $x $y")
                }

                override fun onSwipeFromRight(x: Int, y: Int) {
                    val screenSize = context.screenSize()
                    val matching = allHotSeats.firstOrNull {
                        it.getRect(screenSize.x.toFloat(), screenSize.y.toFloat())
                            .contains(x.toFloat(), y.toFloat())
                    }
                    if (matching != null && matching.area == Area.RightBottom) {
                        handler.removeCallbacks(removeFiring)
                        firingArea = matching.area
                    }
                    firingArea?.let { fireSwipeOnArea(it) }
                    logger.d("onSwipeFromRight: $x $y  --> $matching")
                }

                override fun onSwipeFromLeft(x: Int, y: Int) {
                    logger.d("onSwipeFromLeft: $x $y")
                }
            })

        listener?.systemReady()
        GuideLineView(context).attach()
    }

    fun filterInput(event: InputEvent): Boolean? {
        logger.d("filterInput: $event")
        var res: Boolean? = null
        if (event is MotionEvent) {
            if (firingArea != null) {
                res = false
            }
            listener?.onPointerEvent(event)
        }
        logger.d("return: $res ")
        return res
    }

    fun fireSwipeOnArea(area: Area) {
        when (area) {
            Area.LeftTop -> {}
            Area.LeftCenter -> {}
            Area.LeftBottom -> {}
            Area.LeftAll -> {}
            Area.RightTop -> {}
            Area.RightCenter -> {}
            Area.RightBottom -> {
                handler.post {
                    context.statusBarManager.expandNotificationsPanel()
                }
            }
            Area.RightAll -> {}
            Area.TopLeft -> {}
            Area.TopCenter -> {}
            Area.TopRight -> {}
            Area.TopAll -> {}
            Area.BottomLeft -> {}
            Area.BottomCenter -> {}
            Area.BottomRight -> {}
            Area.BottomAll -> {}
        }
    }

    private fun publishBinderService() {
        ServiceManager.waitForService(PROXY_SERVICE_NAME)?.let { orig ->
            logger.w("publishBinderService, originService: $orig")
            kotlin.runCatching {
                ServiceManager.addService(PROXY_SERVICE_NAME, newBinderProxy(orig as Binder))
                logger.w("publishBinderService, add success.")
            }.onFailure { error ->
                logger.e(error, "Fail to addService")
            }
        }
    }

    private fun newBinderProxy(orig: Binder): BinderProxy {
        return BinderProxy(orig, object : BinderProxy.InvocationHandler {
            override fun onTransact(
                code: Int,
                data: Parcel?,
                reply: Parcel,
                flags: Int,
            ): Boolean? {
                logger.d("BinderProxy, onTransact: $code")
                if (code == IPC_CODE) {
                    data!!.enforceInterface(IShortX::class.java.name)
                    val service = serviceBinder
                    reply.writeStrongBinder(service.asBinder())
                    return true
                }
                return null
            }
        })
    }
}