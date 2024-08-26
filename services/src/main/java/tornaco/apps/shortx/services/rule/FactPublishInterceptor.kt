@file:OptIn(FlowPreview::class)

package tornaco.apps.shortx.services.rule

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.fact.AnyFact
import tornaco.apps.shortx.core.proto.fact.SystemReady
import tornaco.apps.shortx.core.proto.findTypeUrls
import tornaco.apps.shortx.core.proto.getTypeUrl
import tornaco.apps.shortx.core.rule.DAObserver
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.RuleObserver
import tornaco.apps.shortx.core.rule.ToggleObserver
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.mapToString
import tornaco.apps.shortx.services.ShortXContext
import java.util.concurrent.atomic.AtomicReference

class FactPublishInterceptor(
    private val shortXContext: ShortXContext,
    private val scope: CoroutineScope
) {
    companion object {
        const val BUILD_CACHE_DELAY_MS = 1000L
        private const val ENABLE_DUMP_LOG = false
    }

    private val logger = Logger("FactPublishInterceptor")
    private val changeTime = MutableStateFlow(0L)

    private val enabledProtoUrlCache: AtomicReference<ProtoUrlCache> = AtomicReference(null)

    private val alwaysPublishUrls = listOf(
        AnyFact.getDefaultInstance(),
        SystemReady.getDefaultInstance(),
    ).map { it.getTypeUrl() }

    private val protoUrlCacheFlow = changeTime
        .debounce(BUILD_CACHE_DELAY_MS)
        .filter {
            shortXContext.serviceLifecycle.isSystemReadyCalled
        }
        .map {
            buildCache()
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

        scope.launch {
            withContext(Dispatchers.IO) {
                protoUrlCacheFlow.collectLatest {
                    logger.d("protoUrlCacheFlow update: $it")
                    enabledProtoUrlCache.set(it)
                }
            }
        }

        shortXContext.serviceLifecycle.registerLifecycleSystemReadyCallback {
            postInvalidate()
        }
    }

    // True: block.
    fun intercept(fact: ProtoMessage): Boolean {
        if (alwaysPublishUrls.contains(fact.getTypeUrl())) return false
        val cacheSnapshot = enabledProtoUrlCache.get()
        if (cacheSnapshot == null) {
            logger.i("Cache has not built, do not intercept.")
            return false
        }
        if (cacheSnapshot.allFlat.contains(fact.getTypeUrl())) return false

        if (ENABLE_DUMP_LOG) {
            logger.p { "-------- DUMP START ---------" }
            logger.p { cacheSnapshot.toString() }
            logger.p { "-------- DUMP END ---------" }
        }
        return true
    }

    private fun postInvalidate() {
        logger.i("postInvalidate.")
        changeTime.value = System.currentTimeMillis()
    }

    private fun buildCache(): ProtoUrlCache {
        logger.i("buildCache.")

        val enabledRules: Map<String, Set<String>> =
            shortXContext.rule.getAllEnabledRules().associate { it.id to it.findTypeUrls().toSet() }
        logger.d("rules: ${enabledRules.mapToString(System.lineSeparator())}")

        val allDAs: Map<String, Set<String>> =
            shortXContext.rule.getAllDirectAction().associate { it.id to it.findTypeUrls().toSet() }
        logger.d("das: ${allDAs.mapToString(System.lineSeparator())}")

        val allToggles: Map<String, Set<String>> =
            shortXContext.rule.getAllToggles().associate { it.id to it.findTypeUrls().toSet() }
        logger.d("toggles: ${allToggles.mapToString(System.lineSeparator())}")

        return ProtoUrlCache(enabledRules, allDAs, allToggles)
    }
}

internal data class ProtoUrlCache(
    val rules: Map<String, Set<String>> = emptyMap(),
    val das: Map<String, Set<String>> = emptyMap(),
    val toggles: Map<String, Set<String>> = emptyMap()
) {
    val allFlat: Set<String> by lazy {
        rules.values.flatten().toSet() + das.values.flatten() + toggles.values.flatten()
    }
}