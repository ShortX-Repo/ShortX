# patch-xposed/patch-legacy

## Role

`patch-xposed/patch-legacy` is the legacy Xposed runtime entry module. It owns the classic `IXposedHookLoadPackage` and `IXposedHookZygoteInit` entry path and adapts legacy `XC_MethodHook` callbacks into ShortX's shared hook abstraction.

## Module Type

- Kotlin/JVM library

## Key Build Facts

- Compiles against Android framework stubs and the legacy Xposed API 82 jar.
- Depends on `patch-main`, `patch-systemui`, and `patch-common`.
- Exists only to host legacy runtime-specific entry and adapter code; business-hook logic remains in shared modules.

## Key Source Areas

- `patch-xposed/patch-legacy/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/XposedEntry.kt`
- `patch-xposed/patch-legacy/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/LegacyXposedAdapter.kt`

## Important Entry Points

- `XposedEntry.kt`: legacy Xposed entry implementing `IXposedHookLoadPackage` and `IXposedHookZygoteInit`.
- `LegacyXposedAdapter.kt`: bridges `XC_MethodHook` callbacks and legacy logging into `ShortXHookParam` and `XposedLog`.

## How It Connects

This module receives legacy Xposed lifecycle callbacks, installs `LegacyXposedAdapter` into `XposedRuntime`, and then forwards into `HookStub2` in `patch-main`. After that handoff, it shares the same business-hook and rule-driven behavior as the newer runtime modules.

## Reading Outcome

Read this when you need to understand how the current codebase still supports the classic pre-libxposed entry path.