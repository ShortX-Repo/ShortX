# patch-xposed/patch-common

## Role

`patch-xposed/patch-common` is the runtime-agnostic Xposed infrastructure layer. It provides the shared hook facade, callback contracts, parameter model, logging bridge, and runtime adapter abstraction used by the concrete legacy/API 100/API 101 modules.

## Module Type

- Kotlin/JVM library

## Key Build Facts

- Depends on `core`, `core-api`, utility support, and MVEL.
- Compiles against shared project types only; concrete runtime APIs live in the entry modules instead of this module.

## Key Source Areas

- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/XposedAdapter.kt`
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/Hooks.kt`
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/XposedLog.kt`
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ShortXHookParam.kt`
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/CustomHookUtil.kt`
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/CustomHookApp.kt`
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/api/`

## Important Entry Points

- `XposedAdapter.kt`: defines the shared adapter contract and the singleton `XposedRuntime` holder.
- `Hooks.kt`: shared hook registration facade that delegates to the current runtime adapter.
- `XposedLog.kt`: shared logging facade routed through the current runtime adapter.
- `ShortXHookParam.kt`: project-level callback contract used by business hooks and dynamic method hooks.
- `CustomHookUtil.kt`: rule-driven hook installation support.
- `CustomHookApp.kt`: per-app hook orchestration.
- `IPackageLoaded` and `ISystemServerLoaded`: shared callback contracts.

## How It Connects

`patch-main`, `patch-systemui`, `patch-legacy`, `patch-100`, and `patch-101` all depend on this module. The concrete runtime modules install their own adapter into `XposedRuntime`, and everything above that line uses a single shared hook/log facade.

## Reading Outcome

Read this before reading the runtime-specific entry modules. It explains the shared mechanics that let legacy, API 100, and API 101 all reuse the same business-hook layer.
