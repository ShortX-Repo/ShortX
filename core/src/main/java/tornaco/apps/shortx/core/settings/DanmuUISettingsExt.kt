package tornaco.apps.shortx.core.settings

import tornaco.apps.shortx.core.proto.settings.DanmuUISettings

const val DANMU_COLOR_AUTO = 0

val defaultDanmuUISettings =
    DanmuUISettings.newBuilder().setAlpha(1f).setDuration(5000).setTextColor(DANMU_COLOR_AUTO)
        .setTextSizeSp(14)
        .build()