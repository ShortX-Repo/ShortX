# patch-xposed/patch-main

## Role

`patch-xposed/patch-main` is the main Xposed integration module. It owns the primary Xposed and LSPosed entry points and coordinates the large set of system and app hooks used by ShortX.

## Module Type

- Kotlin/JVM library

## Key Build Facts

- Depends on `patch-common`, `patch-systemui`, `services`, `core`, `core-api`, and dynamic support libraries like `external:dex-maker`.
- Includes both traditional Xposed and newer LSPosed entry patterns.

## Key Source Areas

- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ShortXLSPosedHookEntry.kt`
- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/XposedEntry.kt`
- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/HookStub2.kt`
- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/CustomHookSystemServer.kt`

## Important Entry Points

- `ShortXLSPosedHookEntry.kt`: LSPosed entry.
- `XposedEntry.kt`: classic Xposed entry.
- `HookStub2.kt`: central dispatch for package and system hooks.
- `CustomHookSystemServer.kt`: installs system server rule-driven hooks.

## How It Connects

This module turns Xposed lifecycle callbacks into real ShortX behavior. It delegates shared logic to `patch-common`, SystemUI-specialized logic to `patch-systemui`, and eventually interacts with the main `services` runtime.

## Reading Outcome

Read this when you need to understand how ShortX enters foreign processes and bridges those events back into its runtime engine.
