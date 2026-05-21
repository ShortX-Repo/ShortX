# Legacy Reference Inventory

## Scope

Inventory created during Phase 1 setup for the API 101-only migration.

## Remaining Legacy References In `patch-xposed/`

Current direct references found before migration:

- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/XposedEntry.kt`: legacy `IXposedHookLoadPackage`, `IXposedHookZygoteInit`, and `XC_LoadPackage` entry.
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/Hooks.kt`: legacy `XC_MethodHook`, `MethodHookParam`, and `XposedBridge` hook implementation.
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/CustomHookUtil.kt`: legacy `MethodHookParam` and `AndroidAppHelper` expression helper.
- `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/CustomHookApp.kt`: `AndroidAppHelper` for current package lookup.
- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ProcessListHook.kt`: legacy hook callback parameter type.
- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/MediaProviderHook.kt`: legacy hook callback parameter type and logging.
- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ActivityTaskSupervisorHook.kt`: legacy hook callback parameter type.
- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ActivityRecordHook.kt`: legacy hook callback parameter type.
- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ComputerEngineHook.kt`: legacy `XposedHelpers.findClass` usage.
- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ATMSHook.kt` and `StatusBarDebugHook.kt`: stale legacy callback imports.
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/SystemUIHook.kt`: legacy `XposedBridge.log` usage.
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/hookApp.kt`: legacy `XposedBridge.log` usage.
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/hookTile.kt` and `hookTileQuery.kt`: stale legacy callback imports.

Comment-only references exist in stack traces and should be removed or retained only if they are clearly historical diagnostics and not supported API references.

## UI / Service libxposed Usage

Current direct references found in `ui/`:

- `ui/build.gradle.kts` uses `libxposed-api.jar` as `compileOnly`.
- `ui/build.gradle.kts` uses `libxposed-service.jar` and `libxposed-service-interface.jar` as `implementation` dependencies.
- `ui/src/main/java/tornaco/apps/shortx/ui/util/XposedScope.kt` uses `io.github.libxposed.service.XposedService` and `XposedServiceHelper` for scope requests.
- `ui/src/main/java/tornaco/apps/shortx/ui/EntryPoint.kt` initializes `XposedScope`.
- `ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/SystemFactSelectHandler2.kt` requests Xposed scope for media provider packages.

These UI service references are modern libxposed service usage, not legacy `de.robv.android.xposed` support. They must be validated for API 101 compatibility but are not legacy mode by themselves.

## Local Jar Decisions

`patch-xposed/patch-main/libs/api-82.jar` is a legacy compile artifact. It must be removed from Gradle build usage for API 101-only support. Physical deletion can happen after compile migration confirms no active source depends on it.

`patch-xposed/patch-main/libs/libxposed-api.jar` is not the desired API 101 artifact. It must be removed and replaced by Gradle dependency `io.github.libxposed:api:101.0.1`.

## Post-Migration Active Source Status

After migrating the active `patch-xposed` sources and recompiling:

- `patch-common`: no direct active `de.robv.android.xposed`, `XC_MethodHook`, `XposedBridge`, or `AndroidAppHelper` usage remains.
- `patch-systemui`: no direct active `de.robv.android.xposed` or `XposedBridge` usage remains.
- `patch-main`: no direct active `de.robv.android.xposed`, `XC_MethodHook`, `XC_LoadPackage`, `XposedBridge`, or `XposedHelpers` usage remains.

Comment-only historical stack traces remain in:

- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/WMServiceHook.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/data/GlobalSettings.kt`

## Final Search Status

Final local search after cleanup found no supported legacy Xposed runtime surface in active app or `patch-xposed` source paths.

Remaining repository-level matches are expected and not active legacy support:

- `io.github.libxposed.service.IXposedService` and `IXposedScopeCallback` references are modern libxposed service APIs used by UI scope management.
- `InjectedAndroidAppHelper` is the project-owned replacement helper name and does not use Android's legacy `AndroidAppHelper` API.
- `AI/`, `specs/`, and `Xposed-API-101-Upgrade-*.md` matches are historical documentation or migration records.
- `out/` matches are generated build artifacts from local validation and are not source support surface.
