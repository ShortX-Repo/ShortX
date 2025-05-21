package tornaco.apps.shortx.services.os

data class MemAvailableInfo(
    val memTotalSizeString: String,
    val memUsageSizeString: String,
    val memAvailableSizeString: String,
    val memUsedPercent: Int,
    val swapTotalSizeString: String,
    val swapUsageSizeString: String,
    val swapAvailableSizeString: String,
    val swapUsedPercent: Int,
    val swapEnabled: Boolean
)
