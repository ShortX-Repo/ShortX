# patch-xposed/patch-100

## Role

`patch-xposed/patch-100` is the libxposed API 100 runtime entry module. It owns the constructor-based `XposedModule(base, moduleParam)` entry path and adapts API 100's annotated before/after hooker callbacks into ShortX's shared hook abstraction.

## Module Type

- Kotlin/JVM library

## Key Build Facts

- Compiles against Android framework stubs and the local `libxposed100api.jar`.
- Depends on `patch-main`, `patch-systemui`, and `patch-common`.
- Keeps API 100-specific hook annotations and callback wiring local to this module.

## Key Source Areas

- `patch-xposed/patch-100/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ShortXLSPosedHook100Entry.kt`
- `patch-xposed/patch-100/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/Api100XposedAdapter.kt`

## Important Entry Points

- `ShortXLSPosedHook100Entry.kt`: API 100 entry class.
- `Api100XposedAdapter.kt`: API 100 adapter that registers hookers, owns the before/after callback registry, and maps callbacks into `ShortXHookParam`.

## How It Connects

This module receives libxposed API 100 lifecycle callbacks, initializes `XposedRuntime` with `Api100XposedAdapter`, and then forwards the same shared lifecycle events into `HookStub2`. API 100-specific hook semantics stay local here, while the actual business hooks remain shared in `patch-main` and `patch-systemui`.

## Reading Outcome

Read this when you need to understand the API 100 compatibility path and why it has its own annotated hooker implementation.