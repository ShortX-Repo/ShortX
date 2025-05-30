package tornaco.apps.shortx.services.os.cpu

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.text.format.Formatter
import com.android.internal.os.ProcessCpuTracker
import github.tornaco.android.thanos.core.os.SwapInfo
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.CpuAvgAvailability
import tornaco.apps.shortx.core.util.activityManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.os.MemAvailableInfo
import tornaco.apps.shortx.services.os.MemoryReader
import tornaco.apps.shortx.services.os.ProcMemoryInfo
import tornaco.apps.shortx.services.os.cpu.CpuAvailabilityMonitoringConfig.CPUSET_ALL
import tornaco.apps.shortx.services.os.cpu.CpuAvailabilityMonitoringConfig.CPUSET_BACKGROUND
import tornaco.apps.shortx.services.os.cpu.CpuInfoReader.CpuInfo
import tornaco.apps.shortx.services.os.cpu.fallback.FallbackProcessCpuTracker
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.watchdog.launchSafety
import java.io.File

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

    private val processCpuTracker by lazy { ProcessCpuTracker(false) }
    private val processCpuTrackerFallback by lazy { FallbackProcessCpuTracker(false) }

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

    @SuppressLint("UseKtx")
    fun getCpuTotalAvailablePercent(): Int {
        val cpuInfosByCoreId = requireNotNull(cpuInfoReader.readCpuInfos()) {
            "Unable to read cpu info."
        }
        var totalNormalizedAvailableCpuFreqKHz = 0L
        var totalOnlineMaxCpuFreqKHz = 0L

        for (i in 0..<cpuInfosByCoreId.size()) {
            val cpuInfo: CpuInfo = cpuInfosByCoreId.valueAt(i)
            totalNormalizedAvailableCpuFreqKHz += cpuInfo.normalizedAvailableCpuFreqKHz
            totalOnlineMaxCpuFreqKHz += cpuInfo.maxCpuFreqKHz
        }
        logger.d("totalNormalizedAvailableCpuFreqKHz $totalNormalizedAvailableCpuFreqKHz")
        logger.d("totalOnlineMaxCpuFreqKHz $totalOnlineMaxCpuFreqKHz")

        val percent =
            ((totalNormalizedAvailableCpuFreqKHz * 100.0) / totalOnlineMaxCpuFreqKHz).toInt()
        if (percent < 0) {
            // TODO(b/279478586): This case should never happen. But this case happens
            // rarely on certain hardware, which indicates a deeper issue. Once this
            // issue is reproduced, use this log to debug the issue and fix it.
            logger.e("Computed negative CPU availability percent for $percent")
            return 0
        }
        return percent
    }

    @SuppressLint("UseKtx")
    fun getCpuTotalAvailablePercentFallback(): Int {
        return runCatching {
            processCpuTracker.update()
            100 - (processCpuTracker.totalCpuPercent).toInt()
        }.getOrElse {
            processCpuTrackerFallback.update()
            100 - (processCpuTrackerFallback.totalCpuPercent).toInt()
        }
    }

    @SuppressLint("UseKtx")
    fun getCpuAvailablePercents(): Map<Int, Int> {
        val cpuInfosByCoreId = requireNotNull(cpuInfoReader.readCpuInfos()) {
            "Unable to read cpu info."
        }
        val res = mutableMapOf<Int, Int>()
        for (i in 0..<cpuInfosByCoreId.size()) {
            val cpuInfo: CpuInfo = cpuInfosByCoreId.valueAt(i)
            val percent =
                ((cpuInfo.normalizedAvailableCpuFreqKHz * 100.0) / cpuInfo.maxCpuFreqKHz).toInt()
            res[cpuInfo.cpuCore] = percent.takeIf { it >= 0 } ?: 0
        }
        return res
    }

    fun getMemAvailableInfo(): MemAvailableInfo {
        var memTotalSizeString = ""
        var memUsageSizeString = ""
        var memAvailableSizeString = ""
        var memUsedPercent = 0
        var swapTotalSizeString = ""
        var swapUsageSizeString = ""
        var swapAvailableSizeString = ""
        var swapUsedPercent = 0
        var swapEnabled = false

        val memoryInfo = getMemoryInfo()

        memTotalSizeString = Formatter.formatFileSize(context, memoryInfo.totalMem)
        memUsageSizeString =
            Formatter.formatFileSize(
                context,
                memoryInfo.totalMem - memoryInfo.availMem
            )
        memAvailableSizeString =
            Formatter.formatFileSize(context, memoryInfo.availMem)
        memUsedPercent =
            (100 * ((memoryInfo.totalMem - memoryInfo.availMem).toFloat() / memoryInfo.totalMem.toFloat()
                .coerceAtLeast(1f))).toInt()
        val swapInfo = getSwapInfo()
        swapEnabled = swapInfo.totalSwap > 0
        if (swapEnabled) {
            swapTotalSizeString =
                Formatter.formatFileSize(context, swapInfo.totalSwap)
            swapUsageSizeString = Formatter.formatFileSize(
                context,
                swapInfo.totalSwap - swapInfo.freeSwap
            )
            swapAvailableSizeString =
                Formatter.formatFileSize(context, swapInfo.freeSwap)
            swapUsedPercent =
                (100 * ((swapInfo.totalSwap - swapInfo.freeSwap).toFloat() / swapInfo.totalSwap.toFloat()
                    .coerceAtLeast(1f))).toInt()
        }

        return MemAvailableInfo(
            memTotalSizeString = memTotalSizeString,
            memUsageSizeString = memUsageSizeString,
            memAvailableSizeString = memAvailableSizeString,
            memUsedPercent = memUsedPercent,
            swapTotalSizeString = swapTotalSizeString,
            swapUsageSizeString = swapUsageSizeString,
            swapAvailableSizeString = swapAvailableSizeString,
            swapUsedPercent = swapUsedPercent,
            swapEnabled = swapEnabled
        )
    }

    private fun getMemoryInfo(): ActivityManager.MemoryInfo {
        val m = ActivityManager.MemoryInfo()
        context.activityManager.getMemoryInfo(m)
        return m
    }

    private fun getSwapInfo(): SwapInfo {
        val procMemoryInfo: ProcMemoryInfo = MemoryReader.read()
        return SwapInfo(procMemoryInfo.totalSwap, procMemoryInfo.freeSwap)
    }

    fun getCpuTemperature(): Float {
        val possiblePaths = listOf(
            "/sys/class/thermal/thermal_zone0/temp",
            "/sys/class/thermal/thermal_zone1/temp",
            "/sys/class/thermal/thermal_zone2/temp",
            "/sys/devices/virtual/thermal/thermal_zone0/temp"
        )
        for (path in possiblePaths) {
            try {
                val tempFile = File(path)
                if (tempFile.exists()) {
                    val raw = tempFile.readText().trim()
                    val temp = raw.toFloatOrNull()
                    if (temp != null) {
                        return if (temp > 100) temp / 1000f else temp // 有些是毫度，有些是摄氏度
                    }
                }
            } catch (e: Exception) {
                // 忽略异常
            }
        }

        return -1f // 表示获取失败
    }

}
