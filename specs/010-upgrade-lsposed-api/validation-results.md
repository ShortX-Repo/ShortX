# Validation Results

## Environment

- Branch: `010-upgrade-lsposed-api`
- Date: 2026-05-21
- API 101 dependency source: Gradle dependency `io.github.libxposed:api:101.0.1`
- JVM hook modules consume the published AAR by extracting `classes.jar` during build time.

## Compile Validation

### `:patch-xposed:patch-common:compileKotlin`

- Status: PASS
- Notes:
  - API 101 hook facade compiles.
  - Dynamic method-hook callback abstraction compiles.
  - Remaining warning in `ExtAppClassHelper.kt` is unrelated to legacy Xposed removal.

### `:patch-xposed:patch-systemui:compileKotlin`

- Status: PASS
- Notes:
  - SystemUI logging and direct legacy callback imports were removed.
  - Existing SystemUI hook registrations compile through the shared API 101 hook facade.

### `:patch-xposed:patch-main:compileKotlin`

- Status: PASS
- Notes:
  - Legacy entry `XposedEntry.kt` was removed.
  - `ShortXLSPosedHookEntry` was adapted to the real API 101 lifecycle.
  - Remaining legacy direct references in active `patch-main` source were eliminated; only comment-only stack traces remain.

## App Build Validation

### `:app:assembleDebug`

- Status: BLOCKED
- Blocker:
  - `:app:processPrcDebugGoogleServices` failed because `google-services.json` is missing.
  - Final cleanup run on 2026-05-21 still fails at the same task for the same missing file.
- Searched locations reported by Gradle:
  - `app/src/prc/debug/google-services.json`
  - `app/src/debug/prc/google-services.json`
  - `app/src/prc/google-services.json`
  - `app/src/debug/google-services.json`
  - `app/src/prcDebug/google-services.json`
  - `app/google-services.json`
- Interpretation:
  - The current blocker is local environment/configuration, not an API 101 migration compile error in `patch-xposed`.

### `:app:assemblePrcDebug`

- Status: PASS
- Artifact:
  - `out/app/outputs/apk/prc/debug/shortx-1.1-237-ef3c27f-010-upgrade-lsposed-api-dirty(1487770)-prc-debug.apk`
- Notes:
  - `patch-common`, `patch-systemui`, and `patch-main` all compiled inside the app packaging graph.
  - The previous R8 missing-class failure for `io.github.libxposed.api.*` was resolved by adding the API 101 dependency to the app shrink classpath.
  - Build completed through `:app:packagePrcDebug` and `:app:assemblePrcDebug`.
  - Final cleanup validation used `./gradlew :app:assemblePrcDebug` per project instruction and passed on 2026-05-21.

### `:patch-xposed:patch-systemui:test`

- Status: BLOCKED
- Blocker:
  - `patch-xposed/patch-systemui/src/test/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/EdgeGestureSequenceDetectorTest.kt` does not compile against the current production gesture API.
  - Latest run on 2026-05-21 failed at `:patch-xposed:patch-systemui:compileTestKotlin` because `FakeGestureBridge` does not implement the current `GestureBridge` methods with `PredefinedEdgeCoordinateMode` parameters.
- Interpretation:
  - This is existing test drift in the SystemUI gesture test suite, not a direct compile failure in the API 101 migration foundation.

### `:app:assemblePrcRelease`

- Status: PASS
- Artifact:
  - `out/app/outputs/apk/prc/release/shortx-1.1-237-ef3c27f-010-upgrade-lsposed-api-dirty(1487768)-prc-release.apk`
- Notes:
  - Release build completed through `:app:minifyPrcReleaseWithR8`, `:app:packagePrcRelease`, and `:app:assemblePrcRelease`.
  - R8 emitted Kotlin metadata warnings, but they did not fail packaging.
  - This validates that API 101 entry and hook support classes remain on the release build classpath during minification.

## Runtime Validation

### API 101 module recognition

- Status: NOT RUN
- Reason: Requires installation on a supported LSPosed API 101 device or emulator environment, which is not available in this workspace session.

### System server hook-driven automation

- Status: NOT RUN
- Reason: Requires supported-device runtime validation after enabling module scope and restarting target processes.

### SystemUI gesture and quick settings scenarios

- Status: NOT RUN
- Reason: Requires supported-device runtime validation with SystemUI restart and configured gesture/QS rules.

### Ordinary app method-hook before-call and after-call scenarios

- Status: NOT RUN
- Reason: Requires supported-device runtime validation against an in-scope target app.

### Missing target diagnostics and failure isolation

- Status: NOT RUN
- Reason: Requires supported-device runtime validation with a deliberately invalid target hook plus unrelated valid automation.

### ShortX self-hook protection

- Status: NOT RUN
- Reason: Requires supported-device runtime validation with an app method-hook configuration targeting ShortX's own package.

## Legacy Reference Status In Active `patch-xposed` Sources

- Direct legacy imports/usages in active `patch-common`: removed.
- Direct legacy imports/usages in active `patch-systemui`: removed.
- Direct legacy imports/usages in active `patch-main`: removed.
- Remaining matches under active `patch-xposed` are comment-only stack traces:
  - `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/WMServiceHook.kt`
  - `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/data/GlobalSettings.kt`
- Focused final search found no legacy entry reference under `app/src/main` after deleting `app/src/main/assets/xposed_init`.
- Focused final search found no `de.robv.android.xposed`, `XC_MethodHook`, `XposedBridge`, `XposedHelpers`, `IXposed`, or `api-82.jar` references in active `app/src/main`, `patch-xposed` Kotlin source, or app ProGuard files.

## Current Migration State

- Core API 101 migration foundation: complete enough to compile all three `patch-xposed` modules.
- Default `:app:assembleDebug` remains blocked by missing local Google Services configuration, but the requested PRC debug artifact validation passed.
- PRC debug artifact validation: successful.
- PRC release artifact validation: successful.
- Runtime/device validation: pending supported LSPosed API 101 environment.
