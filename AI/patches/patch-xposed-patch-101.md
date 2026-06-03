# patch-xposed/patch-101

## Role

`patch-xposed/patch-101` is the libxposed API 101 runtime entry module. It owns the no-arg `XposedModule()` entry path and adapts API 101's chain-based interceptor model into ShortX's shared hook abstraction.

## Module Type

- Kotlin/JVM library

## Key Build Facts

- Extracts `classes.jar` from the resolved libxposed API AAR and compiles against that generated jar.
- Depends on `patch-main`, `patch-systemui`, and `patch-common`.
- Owns API 101-specific lifecycle mapping such as `onModuleLoaded`, `onPackageReady`, and `onSystemServerStarting`.

## Key Source Areas

- `patch-xposed/patch-101/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ShortXLSPosedHook101Entry.kt`
- `patch-xposed/patch-101/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/Api101XposedAdapter.kt`

## Important Entry Points

- `ShortXLSPosedHook101Entry.kt`: API 101 entry class.
- `Api101XposedAdapter.kt`: API 101 adapter that converts chain interceptors into `ShortXHookParam` callbacks.

## How It Connects

This module receives libxposed API 101 lifecycle callbacks, initializes `XposedRuntime` with `Api101XposedAdapter`, and forwards the resulting lifecycle events into `HookStub2`. It is the newest runtime path, but it still shares the same business-hook layer as legacy and API 100.

## Reading Outcome

Read this when you need to understand the current API 101 path without mixing it up with the shared hook/business layer.