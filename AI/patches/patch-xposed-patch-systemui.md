# patch-xposed/patch-systemui

## Role

`patch-xposed/patch-systemui` contains SystemUI-specific hook logic. It is the specialization layer that runs inside the `com.android.systemui` process under the LSPosed API 101 runtime and adds ShortX behavior that only makes sense in SystemUI, such as quick settings integration, status bar/view hooks, OCR helpers, and global gesture interception.

## Module Type

- Kotlin/JVM library

## Key Build Facts

- Depends on `patch-common`, `core`, `core-api`, and selected runtime/UI support libraries.
- Is narrower than `patch-main`, but deeper in SystemUI-specific behavior.

## What This Module Actually Owns

- SystemUI process bootstrap after Xposed package load.
- Global input monitoring inside SystemUI via `InputMonitor`.
- Edge gesture detection and gesture-to-rule dispatch.
- Quick settings tile and tile host related hooks.
- Status bar and view-layer hooks.
- OCR-related bridging exposed through the SystemUI client.

## Key Source Areas

- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/SystemUIHook.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/hookApp.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/ocr/`

## Important Entry Points

- `SystemUIHook.kt`: package-specific SystemUI entry.
- `hookApp.kt`: SystemUI process setup.
- `input/opls/`: low-level input monitor bridge.
- `input/opls/gesture/`: gesture recognition, area detection, and rule dispatch.
- OCR implementations: text extraction support inside SystemUI-related paths.

## How It Connects

This module is invoked from `patch-main` when the loaded package is SystemUI. It is not generic app-hook logic; it is the SystemUI specialization layer. Under the API 101-only migration, it relies on `patch-common`'s shared hook facade instead of direct legacy callback types.

## SystemUI Startup Chain

The SystemUI-specific flow starts in `SystemUIHook.handleLoadPackage(...)`.

1. It only proceeds when the loaded package is `com.android.systemui`.
2. It further requires the main SystemUI process, not an auxiliary process.
3. It installs a process-local log adapter and then registers multiple SystemUI hook groups.

The actual runtime bootstrap happens in `hookApp.kt` by hooking `SystemUIApplication.onCreate` variants.

Once the hooked application finishes `onCreate`:

1. ShortX app context and resources are initialized for the foreign process.
2. `GlobalSettings.init(app)` runs, but in this process it is intentionally a lightweight stub.
3. Picasso/resource helpers are installed.
4. A system-ready listener is registered against the ShortX binder side.
5. After system-ready, the module attaches a `SystemUIClient` back to ShortX.
6. Only then does it install the global input monitor with `installInputMonitor(app)`.

This ordering matters: gesture logic is not brought up until the binder side is ready to answer configuration queries and receive fired gesture events.

## Gesture Architecture Overview

The gesture implementation is deliberately split into three layers:

1. Input capture layer: obtains raw screen-wide input events from SystemUI.
2. Detection layer: turns `MotionEvent` into a gesture type plus one or more matched hot areas.
3. Policy and dispatch layer: decides whether detection is active, whether an event should be intercepted, and whether a matched gesture should actually fire a ShortX rule event.

This separation is the main design choice that keeps the feature extensible. New gestures usually belong in the detection layer, while rule constraints and app gating belong in the service layer.

## Input Capture Layer

The low-level bridge lives under `input/opls/`.

### `InputMonitorInstaller.kt`

This is the installer entry used by `hookApp.kt`.

It creates:

1. `DetectorServiceImpl`, which owns all configuration, rule-derived caches, and quick checks.
2. `Detector`, which owns recognition logic.
3. `InputMonitorHelper`, which registers the actual SystemUI-side input receiver.

For each received input event:

1. The event is optionally reported for gesture recording.
2. If `service.shouldDetectMotion` is false, the event is ignored immediately.
3. If the event is a `MotionEvent`, `detector.consumeEvent(...)` tries to recognize it.
4. If recognition returns `true`, `pilferPointers()` is called and the event is consumed.
5. If recognition did not consume it but the down point lies in an intercept area, `pilferPointers()` is still called to steal the stream.

So there are two distinct concepts:

1. Recognition consumed the gesture.
2. The touch began in a region that should be intercepted even before final recognition finishes.

### `InputMonitorHelper.java`

This class manages a dedicated asynchronous `InputEventReceiver` on a background `HandlerThread`.

Important details:

- It delegates monitor creation to `InputConsumerManager`.
- It supports re-registration by changing the channel name.
- It exposes `pilferPointers()` so the caller can steal pointer ownership after deciding a gesture belongs to ShortX.

### `InputConsumerManager.java` and `InputConsumer.java`

These classes wrap the actual Android input monitoring APIs.

`InputConsumer` uses `InputManager.monitorGestureInput(...)` via reflection to obtain an `InputMonitor`, then copies or transfers the returned `InputChannel` into the client channel used by `InputEventReceiver`.

The practical consequence is important: this module does not hook a particular View. It listens to screen-level gesture input from inside SystemUI itself.

That is why the feature can work across different SystemUI surfaces and can steal pointers mid-stream with `InputMonitor.pilferPointers()`.

## Detection Layer

The detection entry is `Detector.kt`.

Its job is not to decide whether a rule is enabled. Its job is narrower:

1. Maintain precomputed hot-area rectangles.
2. Feed touch events into gesture recognizers.
3. Convert a recognized gesture into `DetectedArea + Gesture` pairs.
4. Delegate the final decision to `DetectorService.handleEdgeGestureEvent(...)`.

### Supported Gestures In Current Tree

The current repository state supports:

- `SingleTap`
- `DoubleTap`
- `LongTap`
- `SwipeFromTop`
- `SwipeFromBottom`
- `SwipeFromLeft`
- `SwipeFromRight`
- `CornerSwipeToCenter`

The detector stack is no longer edge-only.

`Detector.kt` now coordinates:

1. `GestureDetector` for tap-like edge gestures.
2. `SystemGesturesPointerEventListener` for edge swipes.
3. `CornerSwipeToCenterDetector` for diagonal corner-to-center swipes.

It also owns the shared gesture-hint pipeline through `GestureHintController`, `EdgeGestureHintDetector`, and the corner detector's hint callbacks.

### Tap Detection

Tap-like gestures are handled with Android's `GestureDetector`.

- `onSingleTapUp` maps to `Gesture.SingleTap`
- `onDoubleTap` maps to `Gesture.DoubleTap`
- `onLongPress` maps to `Gesture.LongTap`

All three then reuse the same area detection and dispatch path.

### Swipe Detection

Directional swipes are handled by `SystemGesturesPointerEventListener.java`, a trimmed SystemUI-style pointer listener.

It tracks the original pointer-down location and time, then on move checks whether the pointer has crossed the configured distance threshold within `SWIPE_TIMEOUT_MS`.

This means swipe detection is based on:

1. Initial down point.
2. Net direction of movement.
3. Distance threshold.
4. Time window.

When a swipe is detected, the callback returns the original down coordinates, not the current coordinates. That is consistent with the rest of the design: hot area matching is based on where the gesture started.

### Area Detection And Caching

`Detector` maintains cached `RectF` lists for portrait and landscape.

These caches are rebuilt when:

- gesture settings change
- configuration changes
- user data or rule-derived input changes

For every enabled `EdgeGesture` fact, the detector creates a cached area entry:

1. If `customRect` is present, it uses that rectangle directly.
2. Otherwise it computes a predefined edge rectangle from the edge enum.

This makes hit testing cheap at runtime: detection becomes a simple point-in-rect scan over already prepared geometry.

### `DetectedArea`

`DetectedArea` has two shapes only:

- `PredefinedEdge(edge)`
- `CustomRect(rect)`

That abstraction is important because it lets recognition stay agnostic about how a rule defined its hot area.

## Hot Area Geometry

The predefined edge geometry is defined in `Area.kt`.

The edge model is richer than just top/bottom/left/right. It supports segmented edges such as:

- `TopLeft`, `TopCenter`, `TopRight`, `TopAll`
- `BottomLeft`, `BottomCenter`, `BottomRight`, `BottomAll`
- `LeftTop`, `LeftCenter`, `LeftBottom`, `LeftAll`
- `RightTop`, `RightCenter`, `RightBottom`, `RightAll`

Geometry is computed from:

1. Screen width and height.
2. `gestureAreaWidthDp`, which acts like edge thickness.
3. `gestureLengthScale`, which shrinks or expands the usable length inside a third-segment.

Portrait and landscape are both supported. Landscape reuses the portrait math by swapping width and height.

The practical result is that the hot areas are deterministic, cheap to recompute, and configurable without changing the detector itself.

## Policy, Gating, And Dispatch Layer

`DetectorServiceImpl.kt` is the policy center.

This is the most important file if the work is about behavior rather than raw gesture math.

### Responsibilities

`DetectorServiceImpl` owns:

- gesture settings loading
- app-feature enablement checks
- rule-feature enablement checks
- gesture recording state
- enabled `EdgeGesture` fact cache
- intercept rectangle cache
- front-app matching cache
- final gesture-to-rule firing

### Why `DetectorService` Exists

`DetectorService` is a small interface used by `Detector` so the recognizer layer does not need to know how ShortX stores settings or rules.

That keeps `Detector` reusable and testable in isolation from binder/state details.

### Observers And Cache Refresh

`DetectorServiceImpl` subscribes to many change sources:

- gesture settings listener
- recorder state listener
- rule observer
- DA observer
- pkg set observer
- front app change listener
- rule feature state listener
- system ready listener

Whenever relevant user data changes, it recomputes quick-check data instead of re-reading rules during every gesture.

This precomputation strategy is one of the main reasons the implementation is practical inside SystemUI.

### Quick Checks

The gate for whether detection should even run is:

- rule feature is enabled
- edge gesture app feature is enabled
- there is at least one enabled edge gesture fact
- the current front app matches the gesture's app constraints, or no app restriction exists

That logic is surfaced through `shouldDetectMotion`.

So even though input is globally monitored, most events can still be rejected cheaply before any serious work happens.

### Intercept Versus Fire

This code intentionally distinguishes between two caches derived from rules:

1. `enabledEdgeGestures`: used to decide whether a recognized gesture should fire.
2. `interceptMotionPortRects` and `interceptMotionLandRects`: used to decide whether touch streams should be stolen.

The second cache only includes rules with `isIntercept == true`.

This separation matters because a rule can define a gesture without requiring pointer interception, and vice versa the system can preemptively steal a stream in an intercept region before the final recognition result is produced.

In runtime order the behavior is:

1. `InputMonitorInstaller` forwards the raw event into `Detector.consumeEvent(...)`.
2. If recognition fully handles the gesture, the monitor calls `pilferPointers()` and returns consumed.
3. If recognition did not yet consume the stream, `DetectorServiceImpl.shouldStealMotionEvent(...)` still gets a chance to steal it.
4. If the current touch point lies in an intercept cache region, `pilferPointers()` is called even before final recognition succeeds.

So edge intercept mode is an early stream-ownership decision, not a post-trigger side effect.

Another important detail is that the current intercept cache is geometry-oriented. It stores hot regions only. It does not encode gesture subtype or per-region app constraints. App filtering still exists, but only through the coarser `shouldDetectMotion` quick check.

### Corner Gesture State Versus Edge Intercept State

`CornerSwipeToCenter` currently participates in:

1. enabled-fact caching
2. gesture recognition
3. runtime fact firing
4. gesture hint display gating

But it does not yet participate in the intercept cache used by `shouldStealMotionEvent(...)`.

That means the current corner gesture can fire and can show visual feedback, but it cannot proactively steal the pointer stream the way edge gestures can when `isIntercept == true`.

### Building Rule-Derived Caches

`updateQuickCheckDataOnRuleChange()` scans all enabled rules and extracts `EdgeGesture` facts.

For each enabled fact:

1. If it is not disabled, it is added to `enabledEdgeGestures`.
2. If it is also intercepting, its region is converted into portrait and landscape intercept rectangles.
3. App restrictions are collected from direct app lists and referenced package sets.

If any enabled gesture fact has no app restriction, the service flips `requiredFrontAppAny` and the front-app check becomes a fast unconditional pass.

### Final Firing Logic

When `Detector` reports a recognized gesture with matching areas, `DetectorServiceImpl.handleEdgeGestureEvent(...)` loops through those areas and calls `onEdgeGestureEvent(...)`.

The final fire condition is:

1. Gesture type must match.
2. Area must match.
3. For predefined edges, the stored edge enum must match and `customRect` must be empty.
4. For custom areas, the stored `customRect` must equal the detected rectangle.

If matched, the service rebuilds an `EdgeGesture` proto containing:

- the recognized gesture type
- either the edge enum or the custom rect
- whether the trigger came from a custom area
- the original down coordinates

That proto is then sent into the runtime through `shortx.fireEdgeGesture(...)`.

At that point, the gesture subsystem has finished its job and the rest of the rule engine takes over.

## Recording Support

`reportEvent(...)` forwards raw `MotionEvent` instances to `shortx.onMotionEvent(...)` only when gesture recording is enabled.

So the same monitor serves both runtime gesture triggering and user-facing gesture recording/debug workflows.

## Global Settings Caveat In SystemUI

`GlobalSettings.kt` is intentionally minimal in this module.

The comments explain that loading native-backed storage inside the SystemUI Xposed process can fail because of linker namespace restrictions. Because of that, `GlobalSettings.init(...)` is a stub and the effective gesture enablement flag is refreshed from the ShortX binder side instead.

This is an important architectural constraint for future work: avoid assuming that SystemUI-side code can safely initialize every native-backed dependency used in the main app process.

## Practical Guidance For Future Gesture Work

If you need to add a new gesture feature, decide first which layer the change belongs to.

### Add A New Recognition Pattern

Change `Detector.kt` or add a new recognizer when the feature needs a new gesture shape, such as:

- a drag pattern
- a multi-stage swipe
- a multi-finger gesture
- a hold-then-swipe interaction

### Change Trigger Constraints Or Rule Matching

Change `DetectorServiceImpl.kt` when the gesture is already recognizable but the rules for when it should trigger are changing, such as:

- front-app restrictions
- new intercept semantics
- feature flags
- custom area matching rules

### Change Hot Area Geometry

Change `Area.kt` when the meaning of edge regions changes, such as:

- new edge partitions
- different width or segment calculations
- orientation-specific region behavior

### Preserve The Existing Split

The current implementation works because it preserves a clean split:

- `InputMonitorHelper/InputConsumer`: obtain global input
- `Detector`: recognize gesture and locate the start area
- `DetectorServiceImpl`: apply ShortX business rules and fire runtime events

Keeping that split intact will make new gesture work much less fragile.

## Reading Outcome

Read this module when your task touches any of the following:

- SystemUI startup hooks
- global gesture behavior
- pointer interception and `pilferPointers()` behavior
- rule-driven edge gesture triggering
- status bar or quick settings customization

For gesture work specifically, start with `hookApp.kt`, then `InputMonitorInstaller.kt`, then `Detector.kt`, and finally `DetectorServiceImpl.kt`.
