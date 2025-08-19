package tornaco.apps.shortx.core.settings

import tornaco.apps.shortx.core.proto.common.DynamicShortcut
import tornaco.apps.shortx.core.proto.common.DynamicShortcutSetting

fun defaultDynamicShortcutSettings(sc: DynamicShortcut): DynamicShortcutSetting =
    DynamicShortcutSetting.newBuilder()
        .setShortcut(sc)
        .setLabel("Shortx ${sc.number + 1}")
        .build()
