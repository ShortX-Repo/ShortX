package tornaco.apps.shortx.services.ui

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.ArrayMap
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.Pools
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.services.ui.LaneView.Speed.Async
import tornaco.apps.shortx.services.ui.LaneView.Speed.Sync
import java.util.LinkedList

/**
 * [LaneView] is used to show live comments all over the screen
 */
class LaneView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val logger = Logger("LaneView")

    /**
     * whether live comments overlap with each other
     */
    var overlap = 0f

    /**
     * the gap between different lane
     */
    var verticalGap: Int = 5
        set(value) {
            field = value._dp
        }

    /**
     * the gap between different live comments in one lane
     */
    var horizontalGap: Int = 5
        set(value) {
            field = value._dp
        }

    /**
     * how one live comment should sync with others
     */
    var speedMode: Speed = Async

    /**
     * how long a live comment will show in screen
     */
    var duration = 4000L

    /**
     * a lambda will be invoked when all lanes have nothing to show
     */
    var onEmpty: (() -> Unit)? = null

    /**
     * define how to create live comment view
     */
    lateinit var createView: () -> View

    /**
     * define how to bind data with live comment view
     */
    lateinit var bindView: (Any, View) -> Unit

    /**
     * a pool which holds several live comments view to be reuse
     */
    private lateinit var pool: Pools.SimplePool<View>

    /**
     * the capacity of [pool]
     */
    var poolCapacity: Int = 20
        set(value) {
            field = value
            pool = Pools.SimplePool(value)
        }

    /**
     * [Lane] is sorted by it's top coordinate in [LaneView]
     */
    private var laneMap = ArrayMap<Int, Lane>()

    init {
        verticalGap = 5
        horizontalGap = 5
        poolCapacity = 20
        isClickable = false
        isFocusable = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    }

    fun show(datas: List<Any>) {
        datas.forEach { show(it) }
    }

    fun pause() {
        laneMap.values.forEach { lane ->
            lane.pause()
        }
    }

    fun resume() {
        laneMap.values.forEach { lane ->
            lane.resume()
        }
    }

    fun cancel(id: String) {
        // TODO.
    }

    fun show(data: Any) {
        post {
            kotlin.runCatching {
                if (measuredWidth == 0 || measuredHeight == 0) return@post
                val child = obtain()

                bindView(data, child)

                /**
                 * measure child view
                 */
                measureChild(
                    child,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                )
                /**
                 * add child view
                 */
                addView(child)
                val left = measuredWidth
                val top = getRandomTop(child.measuredHeight)
                /**
                 * layout child view just behind the most right of [LaneView]
                 */
                child.layout(left, top, left + child.measuredWidth, top + child.measuredHeight)
                /**
                 * put child view into [Lane]
                 */
                laneMap[top]?.add(child, data) ?: run {
                    Lane(measuredWidth).also {
                        it.add(child, data)
                        laneMap[top] = it
                        it.showNext()
                    }
                }
            }.onFailure {
                logger.e(it, "show error")
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    /**
     * obtain a live comment view whether in [pool] or creating a new one
     */
    private fun obtain(): View = pool.acquire() ?: createView()

    /**
     * recycle a live comment view after it gets across the screen
     */
    private fun recycle(view: View) {
        view.detach()
        pool.release(view)
    }

    /**
     * check whether all [Lane] have nothing to show
     */
    private fun checkLanes() {
        val isAllEmpty =
            laneMap.values.fold(true) { acc, lane -> acc.and(lane.viewDataMap.isEmpty()) }
        if (isAllEmpty) {
            onEmpty?.invoke()
        }
    }

    /**
     * get the lane top by random
     */
    private fun getRandomTop(commentHeight: Int): Int {
        return if (overlap == 0f) {
            val lanesHeight =
                measuredHeight - paddingTop - paddingBottom
            val lanesCapacity = (lanesHeight + verticalGap) / (commentHeight + verticalGap)
            val extraPadding =
                lanesHeight - commentHeight * lanesCapacity - verticalGap * (lanesCapacity - 1)
            val firstLaneTop = paddingTop + extraPadding / 2
            val randomOffset = (0 until lanesCapacity).random() * (commentHeight + verticalGap)
            firstLaneTop + randomOffset
        } else {
            0
        }
    }

    /**
     * a [Lane] is used to show live comments in sequence without overlapping
     */
    inner class Lane(var laneWidth: Int) {
        private var viewQueue = LinkedList<View>()
        private val dataQueue = LinkedList<Any>()
        private var currentView: View? = null

        /**
         * all showing view and it's data are here
         */
        val viewDataMap = ArrayMap<View, ViewData>()

        //a valve to control live comments showing without overlapping
        private var isBlockShowNext = false
        private val onLayoutChangeListener =
            OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                if (laneWidth - left > v.measuredWidth + horizontalGap) {
                    isBlockShowNext = false
                    showNext()
                }
            }

        fun showNext() {
            if (isBlockShowNext) return
            currentView?.removeOnLayoutChangeListener(onLayoutChangeListener)
            currentView = viewQueue.poll()
            currentView?.let { view ->
                view.addOnLayoutChangeListener(onLayoutChangeListener)
                val duration = when (speedMode) {
                    is Sync -> {
                        val distance = laneWidth + view.measuredWidth
                        val speed = laneWidth.toFloat() / duration
                        (distance / speed).toLong()
                    }

                    is Async -> {
                        duration
                    }
                }
                val valueAnimator = ValueAnimator.ofFloat(1.0f).apply {
                    setDuration(duration)
                    interpolator = LinearInterpolator()
                    addUpdateListener {
                        val value = it.animatedFraction
                        val left = (laneWidth - value * (laneWidth + view.measuredWidth)).toInt()
                        view.layout(
                            left,
                            view.top,
                            left + view.measuredWidth,
                            view.top + view.measuredHeight
                        )
                    }
                    addListener {
                        onEnd = {
                            recycle(view)
                            viewDataMap.remove(view)

                            postDelayed({
                                checkLanes()
                            }, 800)
                        }
                    }
                }
                valueAnimator.start()
                dataQueue.poll()?.let { viewDataMap[view] = ViewData(it, valueAnimator) }
                isBlockShowNext = true
            }
        }

        fun forEachView(each: (View, Any) -> Any) {
            viewDataMap.forEach { entry -> each(entry.key, entry.value.data) }
        }

        fun add(view: View, data: Any) {
            dataQueue.addLast(data)
            viewQueue.addLast(view)
            showNext()
        }

        fun pause() {
            viewDataMap.values.forEach { viewData ->
                viewData.animator.pause()
            }
        }

        fun resume() {
            viewDataMap.values.forEach { viewData ->
                viewData.animator.resume()
            }
        }

        fun snapshot(): List<ViewPosition> {
            val viewPositions = mutableListOf<ViewPosition>()
            viewDataMap.forEach { entry ->
                viewPositions.add(ViewPosition(entry.key.left, entry.key.top, entry.value.data))
            }
            return viewPositions
        }

        /**
         * keep view's data and animator here
         */
        inner class ViewData(var data: Any, var animator: ValueAnimator)

    }

    //<editor-fold desc="helper function">
    private fun View?.detach() =
        this?.parent?.let { it as? ViewGroup }?.also { it.removeView(this) }

    fun ValueAnimator.addListener(action: AnimatorListenerBuilder.() -> Unit): ValueAnimator {
        AnimatorListenerBuilder().apply(action).let { builder ->
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                    animation?.let { builder.onRepeat?.invoke(animation) }
                }

                override fun onAnimationEnd(animation: Animator?) {
                    animation?.let { builder.onEnd?.invoke(animation) }
                }

                override fun onAnimationCancel(animation: Animator?) {
                    animation?.let { builder.onCancel?.invoke(animation) }
                }

                override fun onAnimationStart(animation: Animator?) {
                    animation?.let { builder.onStart?.invoke(animation) }
                }

            })
        }
        return this
    }

    class AnimatorListenerBuilder {
        var onRepeat: ((Animator) -> Unit)? = null
        var onEnd: ((Animator) -> Unit)? = null
        var onCancel: ((Animator) -> Unit)? = null
        var onStart: ((Animator) -> Unit)? = null
    }

    private fun Rect.relativeTo(otherRect: Rect): Rect {
        val relativeLeft = left - otherRect.left
        val relativeTop = top - otherRect.top
        val relativeRight = relativeLeft + right - left
        val relativeBottom = relativeTop + bottom - top
        return Rect(relativeLeft, relativeTop, relativeRight, relativeBottom)
    }

    private fun View.getRelativeRectTo(otherView: View): Rect {
        val parentRect = Rect().also { otherView.getGlobalVisibleRect(it) }
        val childRect = Rect().also { getGlobalVisibleRect(it) }
        return childRect.relativeTo(parentRect)
    }
    //</editor-fold>

    sealed class Speed {
        object Sync : Speed()
        object Async : Speed()
    }
}

class ViewPosition(var left: Int, var top: Int, var data: Any)