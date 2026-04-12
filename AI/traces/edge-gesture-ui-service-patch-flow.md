# Edge Gesture UI-Service-Patch Flow

## Purpose

This document traces the end-to-end flow for ShortX edge gestures across three layers:

- `ui` where users create and configure edge gesture facts and gesture settings
- `services` where gesture settings, recording, fact publication, and rule matching are managed
- `patch-xposed/patch-systemui` where SystemUI captures raw input and turns it back into `EdgeGesture` facts

The goal is to explain how these parts cooperate as one pipeline instead of treating them as isolated features.

## Scope

This trace covers two related but distinct paths:

1. The configuration path: user creates or edits an `EdgeGesture` fact and gesture settings in UI.
2. The runtime path: SystemUI detects a matching gesture, sends it to `services`, and the rule engine fires actions.

It also covers the gesture recording path because it reuses the same service-side gesture infrastructure.

## High-Level Model

There are three different concepts that work together:

1. `Fact.EdgeGesture` in the rule editor
   This is the user-configured rule trigger definition.
2. Gesture settings in `GestureService`
   This controls geometry and thresholds, such as edge width and swipe distance.
3. SystemUI patch detection
   This is the runtime detector that listens to real input, recognizes gestures, and republishes them into the main rule engine.

The key idea is that the patch side does not own rule definitions. It only reads service-provided settings and enabled facts, then emits runtime `EdgeGesture` events back into `services`.

## Part 1: UI Configuration Path

### 1. User adds an edge gesture fact in the rule editor

The rule editor UI is driven by `FactSelectors` and `FactContentStateHandler`.

Relevant files:

- `ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/Facts.kt`
- `ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/FactContentStateHandler.kt`
- `ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/SystemFactSelectHandler.kt`
- `ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/selector/EdgeGestureSelectDialog.kt`
- `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/Fact.kt`

When the user chooses a system fact of type `EdgeGesture`, `systemFactSelectHandler(...)` opens `EdgeGestureSelector`.

That selector builds a UI-side `Fact.EdgeGesture` model with fields such as:

- `edge`
- `gesture`
- `isIntercept`
- `enableGestureHint`
- `apps`
- `pkgSets`
- `customRect`

So UI configuration is not editing the protobuf directly. It first edits a parcelable UI model and only later converts that model to the proto representation.

### 2. What the selector actually configures

`EdgeGestureSelectDialog.kt` supports two hot area modes:

1. Predefined edge mode
   The user picks one of the predefined `Edge` enums such as `BottomRight` or `TopCenter`.
2. Custom rectangle mode
   The user enters or picks an explicit screen rectangle, which overrides edge-based area calculation.

The selector also configures:

- gesture type such as `SingleTap`, `DoubleTap`, `LongTap`, or directional swipe
- whether the area should intercept motion
- whether swipe-type edge gestures should show the runtime indicator
- optional front-app restriction through app list or package sets

The selector can call:

- `shortXManager.showEdgeHint(...)` to preview predefined areas
- `shortXManager.showScreenRectHint(...)` to preview custom rectangles

Those previews are implemented by the service layer, not by the UI itself.

### 3. UI edit-state is stored in `FactContentStateHandler`

`FactContentStateHandler` keeps an in-memory list of current rule facts.

It supports:

- add or replace fact
- remove fact
- insert above or below
- toggle disabled state
- set note and tag

An important detail is that insert, note, and tag changes are normalized by round-tripping through:

- `fact.toProtoFact(...).pack_()`
- `.toFact(shortXManager)`

That means UI fact editing is consistently aligned with the proto schema instead of relying only on loose local state.

### 4. Rule save converts UI facts to proto facts

Saving happens in `AddRuleViewModel`.

Relevant file:

- `ui/src/main/java/tornaco/apps/shortx/ui/addrule/AddRuleViewModel.kt`

When the rule is saved, the view model serializes current facts using:

- `facts.map { it.toProtoFact().pack_() }`

Those packed facts are then written into the `Rule` proto and persisted through the binder side.

When an existing rule is loaded back into UI, the inverse conversion is used:

- `r.factsList.mapNotNull { it.toFact(shortx) }`

The actual edit model and conversion helpers live in `ui-base`:

- `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/Fact.kt`
- `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/FactToProtoFact.kt`
- `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/ProtoAnyToFact.kt`

For `Fact.EdgeGesture`, the mapping is direct:

- UI model fields become `Facts.proto::EdgeGesture`
- `apps` are converted to `AppPkg`
- `pkgSets` are converted to string labels
- `customRect` is only written when present

## Part 2: UI Gesture Settings And Recording

Edge gesture runtime behavior is controlled by a separate UI surface from the rule editor.

### 5. Global gesture settings UI

Relevant file:

- `ui/src/main/java/tornaco/apps/shortx/ui/settings/KeyAndGestureSettingsActivity.kt`

This screen manages:

- global edge gesture feature enablement
- gesture area width
- swipe distance threshold
- swipe area length scale

The view model reads and writes these values through `ShortXManager`, which forwards to the service binder.

Notable calls include:

- `getGestureAreaSize()` / `setGestureAreaSize(...)`
- `getSwipeDistanceThresholdDp()` / `setSwipeDistanceThresholdDp(...)`
- `getSwipeLengthScale()` / `setSwipeLengthScale(...)`
- `setAppFeatureEnabled(AppFeature_EdgeGesture, enabled)`

When geometry settings change, the UI immediately calls `shortXManager.showEdgeHint(...)` so the user can visually preview the new area shape.

### 6. Gesture recording UI

Relevant files:

- `ui/src/main/java/tornaco/apps/shortx/ui/gesture/GestureRecordListActivity.kt`
- `ui/src/main/java/tornaco/apps/shortx/ui/gesture/GestureRecordListViewModel.kt`
- `ui/src/main/java/tornaco/apps/shortx/ui/gesture/GestureRecorderActivity.kt`

This is adjacent to edge gesture rules rather than identical to them.

The recording UI does not define an `EdgeGesture` rule trigger. Instead, it uses service-side gesture recording APIs to capture raw motion sequences as `GestureRecord` entries.

UI calls include:

- `startGestureRecording()`
- `stopGestureRecording(save)`
- `getAllGestureRecords(...)`
- `deleteGestureRecordById(...)`

Recording is disabled in UI if the global edge gesture feature itself is disabled.

## Part 3: Binder Contract Between UI, Services, And Patch

The binder contract lives in `core-api`.

Relevant files:

- `core-api/src/main/java/tornaco/apps/shortx/core/IShortX.aidl`
- `core-api/src/main/java/tornaco/apps/shortx/core/ShortXManager.kt`
- `core-api/src/main/proto/shortx/Facts.proto`

### 7. The important `IShortX` gesture APIs

`IShortX.aidl` exposes the cross-process operations used by both UI and SystemUI patch code.

From UI into services:

- `getGestureAreaSize()` / `setGestureAreaSize(...)`
- `getSwipeDistanceThresholdDp()` / `setSwipeDistanceThresholdDp(...)`
- `getSwipeLengthScale()` / `setSwipeLengthScale(...)`
- `startGestureRecording()` / `stopGestureRecording(...)`
- `registerGestureSettingsListener(...)`
- `registerRecorderStateListener(...)`
- `showEdgeHint(...)` and `showScreenRectHint(...)` through manager wrappers

From patch into services:

- `registerGestureSettingsListener(...)`
- `registerRecorderStateListener(...)`
- `onMotionEvent(...)`
- `fireEdgeGesture(...)`
- `shouldFireEdgeGesture(...)`
- `isAppFeatureEnabled(...)`
- `getScreenSize()`

`ShortXManager.kt` is the Kotlin wrapper used on the client side by both UI and patch code.

### 8. The `EdgeGesture` proto is the shared language

`Facts.proto` defines the runtime fact schema:

- `edge`
- `gesture`
- `isIntercept`
- `apps`
- `pkgSets`
- `customRect`
- `downX`
- `downY`
- `isTriggerCustom`
- `enableGestureHint`

This same proto serves two roles:

1. Stored rule fact definition inside the rule itself.
2. Runtime trigger event published by the patch after recognition.

That shared schema is why matching can stay simple on the service side.

## Part 4: Services-Side Gesture Management

### 9. `ShortXService` is the binder entry point

Relevant file:

- `services/src/main/java/tornaco/apps/shortx/services/ShortXService.kt`

`ShortXService` does not implement gesture behavior directly. It mostly forwards gesture calls to `shortXContext.gesture` and rule publication calls to `shortXContext.rule`.

Important forwarding methods include:

- `registerGestureSettingsListener(...)`
- `registerRecorderStateListener(...)`
- `onMotionEvent(...)`
- `fireEdgeGesture(...)`
- `shouldFireEdgeGesture(...)`

This makes `ShortXService` the bridge point where both UI clients and SystemUI patch clients enter the main runtime.

### 10. `GestureService` owns settings, previews, and recording

Relevant files:

- `services/src/main/java/tornaco/apps/shortx/services/gesture/GestureService.kt`
- `services/src/main/java/tornaco/apps/shortx/services/gesture/GestureRecorder.kt`

`GestureService` is responsible for:

- storing gesture-related settings in `Settings`
- notifying listeners when settings change
- showing overlay guideline previews for edges or custom rectangles
- managing `GestureRecorder`
- injecting saved gesture recordings

This is the service-side owner of gesture configuration state.

#### Settings management

`GestureService` stores and exposes:

- `GestureAreaWidthDp`
- `SwipeDistanceThresholdDp`
- `GestureAreaLengthScale`
- key long-press timeout

When any gesture setting changes, it calls `notifyGestureSettingsChanged()` so patch-side listeners can rebuild their detector caches.

#### Hint preview management

The UI preview APIs are implemented here:

- `showEdgeHint(...)`
- `showScreenRectHint(...)`

These draw `GuideLineView` overlays using the current stored geometry settings. So the user sees the same edge geometry that the runtime detector will later use.

#### Recording management

`GestureRecorder` stores raw `MotionEvent` marshaled bytes while recording is active.

At stop time it builds a `GestureRecord` containing:

- id
- label
- recording timestamp
- duration
- screen orientation
- serialized event list

`GestureService.reportMotionEvent(...)` forwards real motion events into `GestureRecorder` only when recording is active.

That means the recorder is passive until the patch side or another producer calls `onMotionEvent(...)` through the binder.

## Part 5: Patch Runtime Detection In SystemUI

### 11. Patch bootstrap inside SystemUI

Relevant files:

- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/SystemUIHook.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/hookApp.kt`

Only the main `com.android.systemui` process installs this gesture monitor.

After SystemUI app `onCreate` and after ShortX reports system-ready, `hookApp.kt` attaches a `SystemUIClient` and calls:

- `installInputMonitor(app)`

So the patch detector is started only after the service side is ready to answer settings and receive fired gestures.

### 12. Input monitor installation

Relevant files:

- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/InputMonitorInstaller.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/InputMonitorHelper.java`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/InputConsumer.java`

The patch side uses `InputManager.monitorGestureInput(...)` to receive screen-wide gesture input from inside SystemUI.

This is not view-specific event handling. It is process-level input monitoring with the ability to call `pilferPointers()` and steal the active pointer stream.

`InputMonitorInstaller.kt` creates:

1. `DetectorServiceImpl`
2. `Detector`
3. the input receiver callback

For every input event:

1. it reports the event for recording with `service.reportEvent(it)`
2. it checks `service.shouldDetectMotion`
3. it forwards `MotionEvent` into `detector.consumeEvent(...)`
4. if consumed, it calls `pilferPointers()`
5. if not consumed but the start point lies in an intercept region, it still steals the stream

### 13. `Detector` turns raw input into runtime gesture facts

Relevant files:

- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/Detector.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/SystemGesturesPointerEventListener.java`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/Area.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/DetectedArea.kt`

`Detector` has two main jobs:

1. recognize gesture type
2. determine which configured hot area the gesture started in

In the current tree it supports:

- `SingleTap`
- `DoubleTap`
- `LongTap`
- `SwipeFromTop`
- `SwipeFromBottom`
- `SwipeFromLeft`
- `SwipeFromRight`
- `CornerSwipeToCenter`

Area detection is based on:

- predefined edge geometry from `Area.kt`, or
- exact `customRect`

For corner swipes, `Detector` also delegates the same input stream to `CornerSwipeToCenterDetector`, which uses corner-circle hit testing, diagonal direction validation toward the screen center, and the shared gesture-hint overlay controller.

`Detector` caches portrait and landscape rectangles derived from enabled rule facts so hit testing stays cheap.

### 14. `DetectorServiceImpl` is the patch-side policy layer

Relevant file:

- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/DetectorServiceImpl.kt`

This class listens to the service binder for:

- gesture settings changes
- recorder state changes
- rule changes
- DA changes
- package set changes
- front-app changes
- rule feature state changes

It builds quick-check caches from all enabled rule facts, especially enabled `EdgeGesture` facts.

Its responsibilities are:

- determine whether gesture detection should run at all
- maintain enabled edge gesture facts
- maintain intercept regions
- maintain front-app restrictions
- fire a runtime `EdgeGesture` back to the service when a match occurs

This is where UI-configured rule facts become patch-runtime detection configuration.

### 14.1 What `isIntercept` really means

The `isIntercept` field does not mean “trigger first, then block SystemUI later”.

Its actual runtime meaning is:

1. during cache rebuild, convert this rule's hot area into an intercept geometry cache
2. if a raw touch stream starts in that cache, let the input monitor call `pilferPointers()` before final recognition succeeds

The concrete path is:

1. `EdgeGestureSelectDialog.kt` writes `isIntercept`
2. `FactToProtoFact.kt` serializes it into `Facts.proto::EdgeGesture`
3. `DetectorServiceImpl.updateQuickCheckDataOnRuleChange()` scans enabled rules
4. facts with `isIntercept == true` are converted into `interceptMotionPortRects` and `interceptMotionLandRects`
5. `InputMonitorInstaller.kt` checks `service.shouldStealMotionEvent(it)` when `detector.consumeEvent(...)` has not yet consumed the stream
6. if true, `InputMonitorHelper.pilferPointers()` steals the active pointer stream

So intercept mode is a pre-recognition fast path based on geometry, not on the eventual fact matcher result.

### 14.2 Current edge intercept semantics are intentionally coarse

The current implementation is optimized for cheap quick checks inside SystemUI.

Because of that, the intercept cache stores only region geometry. It does not encode:

1. gesture subtype
2. per-region front-app constraint metadata
3. later-stage recognition state

This means current edge intercept mode should be understood as:

1. if input starts in an intercept-enabled hot region, ShortX may steal first
2. detailed gesture matching still happens later in the normal fire path

This distinction matters when designing corner-gesture intercept mode.

### 14.3 Corner gestures currently do not support intercept mode

`CornerSwipeToCenter` is now part of the detector and service cache, but there is still no corner-specific intercept cache in `DetectorServiceImpl`.

So corner gestures currently support:

1. fact enablement
2. recognition
3. runtime publication
4. optional gesture indicator

But not early pointer stealing.

## Part 6: Runtime Trigger Back Into Services

### 15. Patch sends a runtime `EdgeGesture` fact back to the binder service

Once `DetectorServiceImpl` confirms a gesture match, it rebuilds an `EdgeGesture` proto containing:

- actual recognized `gesture`
- actual `edge` or `customRect`
- `isTriggerCustom`
- `downX`
- `downY`

Then it calls:

- `shortx.fireEdgeGesture(data)`

That crosses the binder boundary into `ShortXService.fireEdgeGesture(...)`.

### 16. `ShortXService.fireEdgeGesture(...)` republishes it as a rule fact

Relevant file:

- `services/src/main/java/tornaco/apps/shortx/services/ShortXService.kt`

`ShortXService.fireEdgeGesture(...)` parses the proto and calls:

- `shortXContext.rule.publishFact("fireEdgeGesture", edgeGesture) { ... }`

It also injects initial context data for:

- `eventX`
- `eventY`

So once the patch has recognized the gesture, the rest of the system treats it like any other published fact.

## Part 7: Rule Matching And Action Execution

### 17. `RuleService` publishes the fact

Relevant file:

- `services/src/main/java/tornaco/apps/shortx/services/rule/RuleService.kt`

`publishFact(...)` posts work onto the rule handler thread and delegates to:

- `engine.fire(...)`

This is the same path used by notifications, broadcasts, method hooks, and other runtime fact sources.

### 18. `DefaultRuleEngine` evaluates matching rules

Relevant file:

- `services/src/main/java/tornaco/apps/shortx/services/rule/DefaultRuleEngine.kt`

`DefaultRuleEngine.fire(...)`:

1. checks whether rule feature is enabled
2. checks whether the system is ready
3. asks `FactMatcherImpl` whether the published `EdgeGesture` matches each rule's fact list
4. builds `ContextData` and evaluation state
5. evaluates rule conditions
6. runs actions for matched rules

### 19. `FactMatcherImpl` decides whether runtime and stored edge gestures match

Relevant file:

- `services/src/main/java/tornaco/apps/shortx/services/rule/fact/FactMatcherImpl.kt`

For `EdgeGesture`, the matcher checks:

1. `gesture` must match
2. either both sides use the same predefined edge with default rect
3. or both sides use the same custom rect

This is why patch-side `DetectorServiceImpl` rebuilds a runtime `EdgeGesture` proto instead of sending a custom event type. It can be matched directly against the stored rule fact schema.

## Part 8: How The Pieces Cooperate

The full cooperation can be summarized as this lifecycle:

1. The user creates an `EdgeGesture` fact in rule UI.
2. UI stores it as `Fact.EdgeGesture` edit state.
3. `AddRuleViewModel` serializes it into the rule's proto fact list.
4. The rule is persisted in the main service runtime.
5. SystemUI patch starts and subscribes to service-side gesture settings and rule-related changes.
6. `DetectorServiceImpl` scans enabled rules and extracts active `EdgeGesture` facts.
7. `Detector` uses those facts plus gesture settings to build runtime hot areas.
8. The user performs a real gesture on screen.
9. The patch detector recognizes the gesture and matched area.
10. The patch rebuilds a runtime `EdgeGesture` proto and sends it back with `fireEdgeGesture(...)`.
11. `ShortXService` republishes it through `RuleService.publishFact(...)`.
12. `DefaultRuleEngine` and `FactMatcherImpl` match it against the stored rule facts.
13. If conditions pass, rule actions execute.

So the patch side is effectively a runtime fact producer for `EdgeGesture`, while the UI side is the configuration producer for `EdgeGesture` rule facts.

## Part 9: Important Design Observations

### Stored fact and runtime fact share the same schema

This is the main simplification in the design. UI configuration, patch emission, and rule matching all speak the same `Facts.proto::EdgeGesture` schema.

### Settings are centralized in `GestureService`

Edge size, swipe threshold, and length scale are not stored in the patch. The patch reads them from the main service through listeners.

### Rule ownership stays in `services`

The patch does not execute actions directly. It only recognizes and republishes gestures into the central rule engine.

### Recording is orthogonal but shares the same input stream

Gesture recording is not the same as edge gesture triggering, but it reuses the same service and binder infrastructure. The patch reports `MotionEvent` data, and `GestureRecorder` caches it only when recording is enabled.

## Main Files

- `ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/SystemFactSelectHandler.kt`
- `ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/selector/EdgeGestureSelectDialog.kt`
- `ui/src/main/java/tornaco/apps/shortx/ui/settings/KeyAndGestureSettingsActivity.kt`
- `ui/src/main/java/tornaco/apps/shortx/ui/gesture/GestureRecorderActivity.kt`
- `ui/src/main/java/tornaco/apps/shortx/ui/gesture/GestureRecordListActivity.kt`
- `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/Fact.kt`
- `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/FactToProtoFact.kt`
- `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/ProtoAnyToFact.kt`
- `core-api/src/main/java/tornaco/apps/shortx/core/IShortX.aidl`
- `core-api/src/main/java/tornaco/apps/shortx/core/ShortXManager.kt`
- `core-api/src/main/proto/shortx/Facts.proto`
- `services/src/main/java/tornaco/apps/shortx/services/ShortXService.kt`
- `services/src/main/java/tornaco/apps/shortx/services/gesture/GestureService.kt`
- `services/src/main/java/tornaco/apps/shortx/services/gesture/GestureRecorder.kt`
- `services/src/main/java/tornaco/apps/shortx/services/rule/RuleService.kt`
- `services/src/main/java/tornaco/apps/shortx/services/rule/DefaultRuleEngine.kt`
- `services/src/main/java/tornaco/apps/shortx/services/rule/fact/FactMatcherImpl.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/hookApp.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/InputMonitorInstaller.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/Detector.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/DetectorServiceImpl.kt`

## Debugging Checklist

If an edge gesture rule does not work, check these in order:

1. whether the edge gesture feature is globally enabled in settings UI
2. whether the target rule is enabled and contains a matching `Fact.EdgeGesture`
3. whether the configured edge or `customRect` matches the actual touch start area
4. whether front-app restrictions or package-set restrictions exclude the current app
5. whether the configured gesture type matches what the patch detector can currently recognize
6. whether `DetectorServiceImpl.shouldDetectMotion` is true in SystemUI
7. whether the patch side called `fireEdgeGesture(...)`
8. whether `ShortXService.fireEdgeGesture(...)` republished the fact
9. whether `FactMatcherImpl` matched the runtime fact against the stored rule fact
10. whether rule conditions or conflict policy suppressed action execution