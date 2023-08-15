package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
import tornaco.apps.shortx.core.proto.fact.HasFoundTextOnScreen
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext

class AccessibilityNodeOnScreenTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("AccessibilityNodeOnScreenTrigger")

    override fun register() {
        val textRegexList = mutableListOf<String>()
        shortXContext.rule.getAllEnabledRules().forEach { rule ->
            rule.factsList.forEach {
                if (it is_ HasFoundTextOnScreen::class.java) {
                    val fact = it unpack_ HasFoundTextOnScreen::class.java
                    if (fact.textRegex.isNotEmpty()) {
                        textRegexList.add(fact.textRegex)
                    }
                }
            }
            rule.quit.factsList.forEach {
                if (it is_ HasFoundTextOnScreen::class.java) {
                    val fact = it unpack_ HasFoundTextOnScreen::class.java
                    if (fact.textRegex.isNotEmpty()) {
                        textRegexList.add(fact.textRegex)
                    }
                }
            }
        }

        logger.p("textRegexList: $textRegexList")
        logger.d("textRegexList size: ${textRegexList.size}")

        if (textRegexList.isEmpty()) {
            shortXContext.wm.setOnAccessibilityEvent(null)
        } else {
            shortXContext.wm.setOnAccessibilityEvent { event ->
                if (event.eventType == TYPE_WINDOW_CONTENT_CHANGED) {
                    val allText = shortXContext.wm.getAllTextsFromScreen()
                    logger.d("allText: $allText")
                    val foundAnyMatch = textRegexList.any { factRegex ->
                        val regex = factRegex.toRegex()
                        allText.any { regex.matches(it.text) }
                    }
                    logger.d("foundAnyMatch: $foundAnyMatch")
                }
            }
        }
    }

    override fun unRegister() {

    }
}