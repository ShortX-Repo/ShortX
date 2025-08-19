package tornaco.apps.shortx.core.settings

import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.proto.common.StatusBarTileSetting
import tornaco.apps.shortx.core.res.Remix

fun defaultStatusBarTileSetting(tile: StatusBarTile): StatusBarTileSetting =
    StatusBarTileSetting.newBuilder()
        .setTile(tile)
        .setLabel("Tile ${tile.number + 1}")
        .setIconName(Remix.System.settings_6_line)
        .build()

