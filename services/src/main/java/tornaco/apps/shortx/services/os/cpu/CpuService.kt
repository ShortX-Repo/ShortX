package tornaco.apps.shortx.services.os.cpu

import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.CpuAvgAvailability
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.os.cpu.CpuAvailabilityMonitoringConfig.CPUSET_ALL
import tornaco.apps.shortx.services.os.cpu.CpuAvailabilityMonitoringConfig.CPUSET_BACKGROUND
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.watchdog.launchSafety

val MONITORING_CONFIG_ALL_CPUSET = CpuAvailabilityMonitoringConfig.Builder(
    CPUSET_ALL
).apply {
    (0..100 step 2).toList().forEach {
        addThreshold(it)
    }
}.build()

val MONITORING_CONFIG_BG_CPUSET = CpuAvailabilityMonitoringConfig.Builder(
    CPUSET_BACKGROUND
).apply {
    arrayOf(10, 20, 30, 40, 50, 60, 70, 80, 90).forEach {
        addThreshold(it)
    }
}.build()

class CpuService(shortXContext: ShortXContext) : SystemService(shortXContext, "CpuService") {
    private val cpuInfoReader by lazy { CpuInfoReader() }
    private val cupMonitorService: CpuMonitorService by lazy { CpuMonitorService(context) }
    private val isCpuMonitorStartCalled = java.util.concurrent.atomic.AtomicBoolean(false)
    private val cpuAvailabilityCallback = object : CpuMonitorInternal.CpuAvailabilityCallback {
        override fun onAvailabilityChanged(info: CpuAvailabilityInfo?) {
            logger.d("onAvailabilityChanged: $info")
            info?.let {
                shortXContext.rule.publishFactChecked(CpuAvgAvailability.getDefaultInstance()) {
                    FactPublishData(
                        "onAvailabilityChanged",
                        CpuAvgAvailability.newBuilder().build()
                    ) {
                        mapOf(
                            ContextDataMapping.CpuAvgAvailability.latestAvgAvailabilityPercent.name to info.latestAvgAvailabilityPercent,
                            ContextDataMapping.CpuAvgAvailability.pastNMillisAvgAvailabilityPercent.name to info.pastNMillisAvgAvailabilityPercent,
                            ContextDataMapping.CpuAvgAvailability.pastNMillisDuration.name to info.pastNMillisDuration,
                        )
                    }
                }
            }
        }

        override fun onMonitoringIntervalChanged(intervalMilliseconds: Long) {
            logger.d("onMonitoringIntervalChanged, intervalMilliseconds: $intervalMilliseconds")
        }
    }

    override fun systemReady() {
        super.systemReady()
        // Initialize CPU info reader and perform the first read to make sure the CPU stats are
        // readable without any issues.
        if (!cpuInfoReader.init() || cpuInfoReader.readCpuInfos() == null) {
            logger.e(
                "Failed to initialize CPU info reader. This happens when the CPU "
                        + "frequency stats are not available or the sysfs interface has changed in "
                        + "the Kernel. Cannot monitor CPU without these stats. Terminating CPU monitor "
                        + "service"
            )
        }
        listenRule()
    }

    private fun listenRule() {
        systemServiceScope.launchSafety("listenRuleUpdate") {
            shortXContext.rule.publishInterceptor.apply {
                enabledProtoUrlsChangedTimeStamp.collectLatest {
                    val shouldWatch = !intercept(CpuAvgAvailability.getDefaultInstance())
                    logger.d("enabledProtoUrlsChangedTimeStamp changed. $it shouldWatch: $shouldWatch")
                    if (shouldWatch) {
                        if (isCpuMonitorStartCalled.compareAndSet(false, true)) {
                            cupMonitorService.start(context)
                        }
                        cupMonitorService.localService.addCpuAvailabilityCallback(
                            null,
                            MONITORING_CONFIG_ALL_CPUSET,
                            cpuAvailabilityCallback
                        )
                        logger.w("addCpuAvailabilityCallback: $cpuAvailabilityCallback")
                    } else {
                        cupMonitorService.localService.removeCpuAvailabilityCallback(
                            cpuAvailabilityCallback
                        )
                        logger.w("removeCpuAvailabilityCallback: $cpuAvailabilityCallback")
                    }
                }
            }
        }
    }
}