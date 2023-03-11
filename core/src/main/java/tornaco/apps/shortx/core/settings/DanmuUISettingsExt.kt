package tornaco.apps.shortx.core.settings

import tornaco.apps.shortx.core.proto.settings.DanmuUISettings

const val DANMU_COLOR_AUTO = -1
const val DANMU_DEFAULT_TEXT_SIZE = 14
const val DANMU_DEFAULT_DURATION = 5000L
const val DANMU_DEFAULT_ALPHA = 0.88f

val defaultDanmuUISettings: DanmuUISettings =
    DanmuUISettings.newBuilder().setAlpha(DANMU_DEFAULT_ALPHA).setDuration(DANMU_DEFAULT_DURATION)
        .setTextColor(DANMU_COLOR_AUTO)
        .setTextSizeSp(DANMU_DEFAULT_TEXT_SIZE)
        .build()