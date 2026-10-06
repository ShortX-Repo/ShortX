# ShortX AI Notes

This folder contains module-by-module reading notes generated from the repository structure, build files, and key source entry points.

## Development Note

When changing AIDL or protobuf definitions in `core-api`, regenerate sources before compile validation:

1. `./gradlew :core-api:generateAidlFiles`
2. `./gradlew :core-api:generateProto`

Then run the relevant compile task such as `./gradlew :core-api:compileKotlin` or module-specific validation.

When adding or changing product translations, update the checked-in JSON assets under `ui/src/main/assets/i18n/` instead of only changing code-side fallback text.

## Structure

- `build/`: build infrastructure and generated asset modules
- `architecture/`: cross-module architecture overviews
- `core/`: shared contracts and core logic modules
- `app-ui/`: app assembly and UI-facing modules
- `runtime/`: runtime services and Android-version-specific modules
- `patches/`: Xposed and Magisk integration modules
- `support/`: support utilities and vendored modules

## Architecture

- [architecture/cross-module-architecture-map.md](architecture/cross-module-architecture-map.md)
- [architecture/i18n-architecture.md](architecture/i18n-architecture.md)

## Build

- [build/buildSrc.md](build/buildSrc.md)
- [build/prebuilts-gen.md](build/prebuilts-gen.md)

## Release

PRC 发布脚本位于 `scripts/release_prc.py`，用于串起 tag、构建、GitHub Release 和 Telegram 发布流程。

前置条件:

1. 已安装并登录 GitHub CLI: `gh auth status`
2. 本地可执行 Gradle 构建
3. `CHANGELOG.md` 已更新本次发布内容
4. 如果需要发 Telegram，先配置环境变量:

```bash
export TG_BOT_TOKEN="123456:ABC-DEF"
export TG_CHAT_ID="-1001234567890"
export TG_TOPIC_ID="42"
```

常用命令示例:

```bash
# 完整发布：创建 tag -> 构建 -> GitHub Release -> Telegram
python3 scripts/release_prc.py 1.8

# 不传版本号时，自动基于最近 tag 推导下一个 minor
python3 scripts/release_prc.py

# tag 已存在时跳过 tag 创建
python3 scripts/release_prc.py 1.8 --skip-tag

# 已经构建过 APK，只补发 GitHub Release 和 Telegram
python3 scripts/release_prc.py 1.8 --skip-tag --skip-build

# 只发 Telegram，不创建 GitHub Release
python3 scripts/release_prc.py 1.8 --skip-github

# 不发 Telegram，只做 tag + build + GitHub Release
python3 scripts/release_prc.py 1.8 --skip-telegram

# GitHub 已发过，只修复 Telegram 消息 caption
python3 scripts/release_prc.py 1.8 --skip-build --skip-github --tg-edit-message-id 12345

# 通过命令行显式传 Telegram 参数
python3 scripts/release_prc.py 1.8 \
	--tg-bot-token "123456:ABC-DEF" \
	--tg-chat-id "-1001234567890" \
	--tg-topic-id "42"
```

关键选项:

- `--skip-tag`: tag 已存在时跳过创建和推送
- `--skip-build`: 直接使用本地已有 APK
- `--skip-github`: 跳过 GitHub Release
- `--skip-telegram`: 跳过 Telegram 发布
- `--tg-edit-message-id`: 修改已有 Telegram 消息 caption

脚本日志会写到 `out/release_*.log`。

## Core

- [core/core-api.md](core/core-api.md)
- [core/core.md](core/core.md)

## App And UI

- [app-ui/app.md](app-ui/app.md)
- [app-ui/ui.md](app-ui/ui.md)
- [app-ui/ui-base.md](app-ui/ui-base.md)
- [app-ui/services-ui.md](app-ui/services-ui.md)

## Runtime

- [runtime/services.md](runtime/services.md)
- [runtime/services-jni.md](runtime/services-jni.md)
- [runtime/services-28.md](runtime/services-28.md)
- [runtime/services-29.md](runtime/services-29.md)
- [runtime/services-30.md](runtime/services-30.md)
- [runtime/services-31.md](runtime/services-31.md)
- [runtime/services-32.md](runtime/services-32.md)
- [runtime/services-33.md](runtime/services-33.md)
- [runtime/services-34.md](runtime/services-34.md)
- [runtime/services-35.md](runtime/services-35.md)

## Patches

- [patches/patch-xposed-patch-common.md](patches/patch-xposed-patch-common.md)
- [patches/patch-xposed-patch-main.md](patches/patch-xposed-patch-main.md)
- [patches/patch-xposed-patch-systemui.md](patches/patch-xposed-patch-systemui.md)
- [patches/patch-magisk-module.md](patches/patch-magisk-module.md)
- [patches/patch-magisk-bridge.md](patches/patch-magisk-bridge.md)
- [patches/patch-magisk-bridge-dex-app.md](patches/patch-magisk-bridge-dex-app.md)
- [patches/patch-magisk-patch-framework.md](patches/patch-magisk-patch-framework.md)

## Traces

- [traces/notification-rule-flow.md](traces/notification-rule-flow.md)
- [traces/method-hook-flow.md](traces/method-hook-flow.md)
- [traces/corner-swipe-intercept-mode-design.md](traces/corner-swipe-intercept-mode-design.md)
- [traces/workday-calendar-design.md](traces/workday-calendar-design.md)
- [traces/workday-calendar-tasking.md](traces/workday-calendar-tasking.md)

## Gesture

- [EDGE_GESTURE_OVERVIEW.html](EDGE_GESTURE_OVERVIEW.html)

## Support

- [support/external-utils.md](support/external-utils.md)
- [support/external-codeditor.md](support/external-codeditor.md)
- [support/external-dex-maker.md](support/external-dex-maker.md)
- [support/external-rhino-android.md](support/external-rhino-android.md)
- [support/external-sec_net.md](support/external-sec_net.md)
- [support/external-reorderable.md](support/external-reorderable.md)
- [support/external-compose-color-picker.md](support/external-compose-color-picker.md)
- [support/external-dateformatter.md](support/external-dateformatter.md)
- [support/external-icons.md](support/external-icons.md)
- [support/external-picasso.md](support/external-picasso.md)
- [support/external-sardine-android.md](support/external-sardine-android.md)
- [support/external-drawbox.md](support/external-drawbox.md)

## Reading Order

Recommended order if you are new to the repo:

1. `core/core-api.md`
2. `core/core.md`
3. `runtime/services.md`
4. `app-ui/ui.md`
5. `app-ui/app.md`
6. `patches/patch-xposed-patch-main.md`

After that, use the versioned runtime docs and support docs only when your work touches those areas.
