package tornaco.apps.shortx.core.settings

import tornaco.apps.shortx.core.proto.common.CustomContextMenu
import tornaco.apps.shortx.core.proto.common.CustomContextMenuSetting

fun defaultCustomContextMenuSetting(menu: CustomContextMenu): CustomContextMenuSetting =
    CustomContextMenuSetting.newBuilder()
        .setMenu(menu)
        .setLabel("Menu ${menu.number + 1}")
        .build()
