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
- [traces/edge-gesture-ui-service-patch-flow.md](traces/edge-gesture-ui-service-patch-flow.md)

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
