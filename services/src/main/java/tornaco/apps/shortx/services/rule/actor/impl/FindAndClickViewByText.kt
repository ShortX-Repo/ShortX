package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.action.FindAndClickViewByText
import tornaco.apps.shortx.core.proto.common.FindTextMethod
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.common.RectSourceRect
import tornaco.apps.shortx.core.proto.toAndroidRect
import tornaco.apps.shortx.core.proto.toProtoRect
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object FindAndClickViewByText : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.FindAndClickViewByText::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        val d = action unpack_ FindAndClickViewByText::class.java
        require(androidContext.powerManager.isInteractive) {
            "Can not FindAndClickViewByText when device is not interactive"
        }
        val method: FindTextMethod = d.method
        logger.d("method: $method")
        when (method) {
            FindTextMethod.FTM_UI_AUTO, FindTextMethod.UNRECOGNIZED -> {
                val uiSuccess = clickTextByUIAuto(params, d)
                logger.d("FTM_UI_AUTO: $uiSuccess")
            }

            FindTextMethod.FTM_OCR -> {
                val ocrSuccess = clickTextByOCR(params, d)
                logger.d("FTM_UI_AUTO_OCR, ocrSuccess: $ocrSuccess")
            }

            FindTextMethod.FTM_UI_AUTO_OCR -> {
                val uiSuccess = clickTextByUIAuto(params, d)
                logger.d("FTM_UI_AUTO_OCR, uiSuccess: $uiSuccess")
                if (!uiSuccess) {
                    val ocrSuccess = clickTextByOCR(params, d)
                    logger.d("FTM_UI_AUTO_OCR, ocrSuccess: $ocrSuccess")
                }
            }
        }
        Actor.Res.EMPTY
    }

    private suspend fun HandleParams.clickTextByUIAuto(
        params: HandleParams,
        d: FindAndClickViewByText
    ): Boolean {
        return shortXContext.wm.withUiAutomation("FindAndClickViewByText") {
            val cv = RuleContextAndVars(params)
            it.findAndClickViewByText(
                text = d.text.compileContextAndVars(cv),
                isRegex = d.isRegex,
                timeout = maxOf(d.timeout, 1000L)
            )
        }
    }

    private suspend fun HandleParams.clickTextByOCR(
        params: HandleParams,
        d: FindAndClickViewByText
    ) {
        val rectOrNull = d.rect.takeIf { it != Rect.getDefaultInstance() }

        val src = if (rectOrNull == null) {
            RectSourceRect.newBuilder().setRect(androidContext.screenSize().let {
                generateFullScreenRect(it.x, it.y).toProtoRect()
            }).build().pack_()
        } else {
            RectSourceRect.newBuilder().setRect(rectOrNull).build().pack_()
        }
        logger.d("clickTextByOCR, src: $src")

        val result: List<Rect> = OcrDetect.findAllContinuousTextPositions(params, src, d.text)

        logger.logOnDebug("clickTextByOCR, result: $result")

        val centerXY = result.firstOrNull()?.toAndroidRect()?.let {
            it.centerX() to it.centerY()
        }

        logger.d("clickTextByOCR, centerXY: $centerXY")
        centerXY?.let { xy ->
            shortXContext.wm.withUiAutomation("clickTextByOCR") {
                it.click(xy.first, xy.second)
            }
        }
    }
}

fun generateFullScreenRect(width: Int, height: Int): android.graphics.Rect {
    return android.graphics.Rect(0, 0, width, height)
}