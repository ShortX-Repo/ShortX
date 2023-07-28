package tornaco.apps.shortx.services.util

import android.view.MotionEvent

suspend fun MotionEvent.copyUse(
    downTime: Long,
    eventTime: Long,
    action: suspend (MotionEvent) -> Unit
) {
    val pointerProps = arrayOfNulls<MotionEvent.PointerProperties>(this.pointerCount)
    val pointerCoords = arrayOfNulls<MotionEvent.PointerCoords>(this.pointerCount)
    for (i in 0 until this.pointerCount) {
        val prop = MotionEvent.PointerProperties()
        val coords = MotionEvent.PointerCoords()

        this.getPointerProperties(i, prop)
        pointerProps[i] = prop

        this.getPointerCoords(i, coords)
        pointerCoords[i] = coords
    }

    val newEvent = MotionEvent.obtain(
        downTime,
        eventTime,
        this.action,
        this.pointerCount,
        pointerProps,
        pointerCoords,
        this.metaState,
        this.buttonState,
        this.xPrecision,
        this.yPrecision,
        this.deviceId,
        this.edgeFlags,
        this.source,
        this.displayId,
        this.flags
    )

    action(newEvent)
    newEvent.recycle()
}