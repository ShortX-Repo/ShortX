package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import com.google.protobuf.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.common.RegexMatchOptions
import tornaco.apps.shortx.core.proto.fact.Logcat
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.util.logcat.LoggingRepository
import tornaco.apps.shortx.services.watchdog.launchSafety

class LogcatTrigger(
    context: Context,
    shortXContext: ShortXContext
) :
    DAAndRuleAwareTrigger(context, shortXContext) {
    companion object {
        const val LOG_TAG = "LogcatTrigger"
    }

    private val logger = Logger(LOG_TAG)

    private val regexMatchers = mutableListOf<Pair<Logcat, Regex>>()

    private val logScope = CoroutineScope(SupervisorJob())
    private val logcat by lazy { LoggingRepository(shortXContext, logScope) }
    private val mutex = Mutex()

    override fun register() {
        logScope.launchSafety("LogcatTrigger.reg") {
            withContext(Dispatchers.Default) {
                logcat.logsFlow.collect { logLine ->
                    if (logLine == null) return@collect
                    if (logLine.original.contains(LOG_TAG)) return@collect

                    logger.logOnDebug("---- catching log: ${logLine.original}")

                    val content = logLine.original
                    regexMatchers.forEach {
                        val regex = it.second
                        val options = it.first.regexMatchOptions
                        val isMatched = if (options == RegexMatchOptions.RegexMatchOptions_Match) {
                            regex.matches(content)
                        } else {
                            regex.containsMatchIn(content)
                        }
                        if (isMatched) {
                            shortXContext.rule.publishFactChecked(Logcat.getDefaultInstance()) {
                                FactPublishData(
                                    reason = "Logcat",
                                    fact = Logcat.newBuilder().setId(it.first.id).build()
                                ) { mapOf(ContextDataMapping.Logcat.line.name to content) }
                            }
                        }
                    }
                }
            }
        }

        logScope.launchSafety("LogcatTrigger.updateTriggerState") { updateTriggerState() }
    }

    override fun unRegister() {
        logScope.launchSafety("updateTriggerState.un.reg") { logcat.stopLogging() }
    }

    override val factClass: Class<out Message> = Logcat::class.java

    override fun updateTriggerState() {
        logScope.launchSafety("updateTriggerState.updateTriggerState") {
            mutex.withLock {
                logger.d("updateTriggerState")
                regexMatchers.clear()
                shortXContext.rule.forEachEnabledFacts { _, factProtoAny ->
                    if (factProtoAny is_ Logcat::class.java) {
                        val fact = factProtoAny unpack_ Logcat::class.java
                        if (!fact.isDisabled) {
                            runCatching { fact.regex.toRegex() }.getOrElse {
                                logger.e(it, "updateTriggerState, regex error")
                                null
                            }?.let {
                                regexMatchers.add(fact to it)
                            }
                        }
                    }
                }

                logger.d("regexMatchers: $regexMatchers")

                if (regexMatchers.isEmpty()) {
                    logger.i("Disable logcat manager, no fact.")
                    logcat.stopLogging()
                } else {
                    logger.i("Start logging.")
                    logcat.startLoggingIfNot()
                }
            }
        }
    }
}