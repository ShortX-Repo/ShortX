# patch-xposed/patch-common

## Role

`patch-xposed/patch-common` is the shared LSPosed API 101 hook infrastructure layer. It provides common hook wrappers, callback interfaces, and utilities used by the concrete injected modules.

## Module Type

- Kotlin/JVM library

## Key Build Facts

- Depends on `core`, `core-api`, utility support, and MVEL.
- Compiles against LSPosed API 101 and Android framework stubs.

## Key Source Areas

- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/Hooks.kt`
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ShortXHookParam.kt`
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/Api101HookRegistry.kt`
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/CustomHookUtil.kt`
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/CustomHookApp.kt`
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/api/`

## Important Entry Points

- `Hooks.kt`: safe wrappers over LSPosed API 101 hook registration.
- `ShortXHookParam.kt`: project-level callback contract used by business hooks and dynamic method hooks.
- `Api101HookRegistry.kt`: registry bridging executables to shared callbacks.
- `CustomHookUtil.kt`: rule-driven hook installation support.
- `CustomHookApp.kt`: per-app hook orchestration.
- `IPackageLoaded` and `ISystemServerLoaded`: shared callback contracts.

## How It Connects

`patch-main` and `patch-systemui` depend on this module. It is the reusable base layer that prevents Xposed hook logic from being duplicated.

## Reading Outcome

Read this before reading the concrete Xposed modules. It explains the common mechanics behind all hooks.
