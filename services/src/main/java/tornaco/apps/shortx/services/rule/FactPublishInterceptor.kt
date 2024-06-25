@file:OptIn(FlowPreview::class)

package tornaco.apps.shortx.services.rule

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import tornaco.apps.shortx.core.proto.findTypeUrls
import tornaco.apps.shortx.core.rule.DAObserver
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.RuleObserver
import tornaco.apps.shortx.core.rule.ToggleObserver
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext

class FactPublishInterceptor(private val shortXContext: ShortXContext) {
    companion object {
        const val BUILD_CACHE_DELAY_MS = 1000L
    }

    private val logger = Logger("FactPublishInterceptor")
    private val changeTime = MutableStateFlow(System.currentTimeMillis())

    private val protoUrlCache: MutableSet<String> = mutableSetOf()

    private val protoUrlCacheFlow = changeTime
        .debounce(BUILD_CACHE_DELAY_MS)
        .map {
            buildCache().apply {
                protoUrlCache.clear()
                protoUrlCache.addAll(this)
            }
        }

    init {
        shortXContext.rule.registerRuleObs(object : RuleObserver() {
            override fun onRuleChanged(id: String) {
                super.onRuleChanged(id)
                postInvalidate()
            }

            override fun onRuleSetChanged(id: String) {
                super.onRuleSetChanged(id)
                postInvalidate()
            }
        })

        shortXContext.rule.registerDAObs(object : DAObserver() {
            override fun onDAChanged(id: String) {
                super.onDAChanged(id)
                postInvalidate()
            }

            override fun onDASetChanged(id: String) {
                super.onDASetChanged(id)
                postInvalidate()
            }
        })

        shortXContext.rule.registerToggleObs(object : ToggleObserver() {
            override fun onToggleChanged(id: String) {
                super.onToggleChanged(id)
                postInvalidate()
            }

            override fun onToggleSetChanged(id: String) {
                super.onToggleSetChanged(id)
                postInvalidate()
            }
        })

        postInvalidate()
    }

    fun intercept(fact: ProtoMessage): Boolean {
        return protoUrlCache.contains(fact.pack_().typeUrl)
    }

    private fun postInvalidate() {
        logger.w("postInvalidate.")
        changeTime.value = System.currentTimeMillis()
    }

    private fun buildCache(): Set<String> {
        logger.w("buildCache.")

        val enabledRules =
            shortXContext.rule.getAllEnabledRules().map { it.findTypeUrls() }.flatten()
        logger.d("rules: ${enabledRules.joinToString(System.lineSeparator())}")

        val allDAs = shortXContext.rule.getAllDirectAction().map { it.findTypeUrls() }.flatten()
        logger.d("das: ${allDAs.joinToString(System.lineSeparator())}")

        val allToggles = shortXContext.rule.getAllToggles().map { it.findTypeUrls() }.flatten()
        logger.d("toggles: ${allToggles.joinToString(System.lineSeparator())}")

        val allFacts = enabledRules + allDAs + allToggles

        return allFacts.toSet()
    }
}