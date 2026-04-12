# Corner Swipe Intercept Mode Design

## Purpose

This note captures two things together:

1. how edge gesture intercept mode actually works today in `patch-systemui`
2. how the same concept should be extended to `CornerSwipeToCenter`

The goal is to avoid implementing corner intercept mode as a superficial UI flag. In the current architecture, intercept mode is a patch-side input ownership strategy, not just a rule property.

## Existing Edge Intercept Mode

### User-visible configuration

`EdgeGesture` already exposes an `isIntercept` switch in the rule editor.

That switch is configured in:

1. `ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/selector/EdgeGestureSelectDialog.kt`
2. `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/Fact.kt`
3. `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/FactToProtoFact.kt`

The field is persisted in `Facts.proto::EdgeGesture.isIntercept`.

### Runtime meaning

The field does not mean “after the gesture triggers, block the system gesture”.

Its real meaning is:

1. precompute this rule's hot area into an intercept geometry cache
2. when a raw touch stream starts in that geometry, allow the SystemUI input monitor to call `pilferPointers()` early

That logic lives in:

1. `patch-systemui/input/opls/InputMonitorInstaller.kt`
2. `patch-systemui/input/opls/gesture/DetectorServiceImpl.kt`

The runtime order is:

1. raw `MotionEvent` arrives in the monitor callback
2. `Detector.consumeEvent(...)` gets first chance to fully recognize and consume the gesture
3. if not consumed yet, `DetectorServiceImpl.shouldStealMotionEvent(...)` checks intercept regions
4. if the point lies in an intercept region, `InputMonitorHelper.pilferPointers()` steals the stream

So intercept mode is an early stream-stealing policy.

### What data is cached today

`DetectorServiceImpl.updateQuickCheckDataOnRuleChange()` builds:

1. `enabledEdgeGestures`
2. `interceptMotionPortRects`
3. `interceptMotionLandRects`

Only `EdgeGesture` facts with `isIntercept == true` are inserted into the intercept rect caches.

For each such fact:

1. if `customRect` is set, the exact rect is cached
2. otherwise the edge enum is converted to portrait and landscape `RectF`

### Important limitation of current edge intercept mode

The intercept cache is geometry-only.

It does not encode:

1. the eventual gesture subtype
2. per-hot-area front-app constraints
3. any later-stage recognition state

That is intentional: it is a fast pre-check inside SystemUI.

So the current semantics are best described as:

1. if input starts inside an intercept-enabled hot region, ShortX may steal the stream first
2. final fact firing still depends on the normal gesture matcher later

This is the behavior that the corner design should align with unless there is a strong reason to tighten semantics.

## Current Corner Swipe State

`CornerSwipeToCenter` already supports:

1. UI rule creation
2. proto persistence
3. patch-side detection through `CornerSwipeToCenterDetector`
4. runtime event publication through `fireCornerSwipeToCenter(...)`
5. per-trigger gesture indicator toggle

But it does not yet support intercept mode.

Current patch-side state for corner swipes is:

1. `enabledCornerSwipeToCenterFacts` cache exists
2. there is no `isIntercept` field on `CornerSwipeToCenter`
3. there is no corner intercept cache in `DetectorServiceImpl`
4. `shouldStealMotionEvent(...)` only checks edge/custom-rect intercept regions

So corner swipes can be recognized, but they cannot currently preemptively steal the pointer stream.

## Design Goal For Corner Intercept Mode

We want corner swipes to support the same product-level concept as edge gestures:

1. allow a rule author to decide whether this gesture should proactively intercept the touch stream

But the implementation cannot literally reuse edge rect caches because the hot-zone model is different.

Edge intercept geometry is rectangle-based.

Corner swipe start geometry is circle-based, centered on one of the four screen corners.

So the design should reuse the policy pattern, not the exact data structure.

## Proposed Schema Changes

Add an intercept field to `CornerSwipeToCenter` in `Facts.proto`.

Recommended shape:

1. `optional bool isIntercept = 5;`

Why optional:

1. it preserves field presence
2. absence can be treated deliberately during compatibility handling
3. it matches the recent indicator-toggle compatibility strategy used for edge gestures

Recommended compatibility default:

1. missing `isIntercept` should be treated as `false`

That is the safer default because interception changes ownership behavior, not just visuals.

## Proposed UI Changes

Add a switch to `CornerSwipeToCenterSelectDialog.kt`:

1. label: reuse the existing `ui.fact.key.event.adv.is.intercept.mode`
2. placement: alongside the indicator switch

Update:

1. `ui-base/.../Fact.kt`
2. `FactToProtoFact.kt`
3. `ProtoAnyToFact.kt`
4. `FactI18N.kt`

The corner fact summary should display intercept mode in the same way edge gesture summaries already do.

## Proposed Patch-Side Architecture

### Core idea

Keep the same two-layer separation already used by edge gestures:

1. enabled fact cache for final firing
2. intercept cache for early stream stealing

But the corner intercept cache should be corner-oriented rather than rect-oriented.

### Recommended cache shape

Add a new cache in `DetectorServiceImpl`, for example:

1. `interceptCornerSwipeFacts`

This can be stored as one of:

1. the full `CornerSwipeToCenter` facts with `isIntercept == true`
2. a reduced derived model containing just `corner` and any future geometry parameters

Given the current feature size, caching the full facts is simpler and consistent with the rest of the service.

### Cache build rule

During `updateQuickCheckDataOnRuleChange()`:

1. keep adding all enabled corner facts to `enabledCornerSwipeToCenterFacts`
2. additionally add only intercept-enabled corner facts to `interceptCornerSwipeFacts`

This mirrors the existing edge split between `enabledEdgeGestures` and intercept rect caches.

### `shouldStealMotionEvent(...)` extension

Extend `DetectorServiceImpl.shouldStealMotionEvent(...)` so it returns `true` if either condition is met:

1. the point lies in an edge/custom-rect intercept region
2. the point lies in any intercept-enabled corner circle

For corners, the check should use the same geometry contract as the actual detector:

1. screen corner as circle center
2. radius = `0.18 * min(width, height)`

That keeps intercept semantics aligned with actual recognition semantics.

### Why this belongs in `DetectorServiceImpl`

It would be a mistake to put corner intercept behavior only inside `CornerSwipeToCenterDetector`.

Reason:

1. `CornerSwipeToCenterDetector` is the recognizer
2. `DetectorServiceImpl.shouldStealMotionEvent(...)` is the ownership policy fast path

Edge gestures already follow this split. Corner intercept should follow the same split so the input monitor behavior stays consistent.

## Detector Interaction After Intercept

No major detector rewrite should be required.

Once the monitor steals the stream on `ACTION_DOWN`, subsequent events continue to flow through the same `Detector.consumeEvent(...)` path, so:

1. `CornerSwipeToCenterDetector` can keep its existing recognition logic
2. the gesture hint pipeline can keep working
3. final fact firing can continue to use `handleCornerSwipeToCenterEvent(...)`

So intercept mode should be additive to the policy layer rather than invasive to the detector layer.

## Recommended Behavioral Semantics

Corner intercept mode should mean:

1. if touch starts inside a corner's configured circle and that corner rule has intercept enabled, ShortX may steal the stream immediately
2. actual fact firing still requires the existing direction and distance checks

This intentionally matches the edge implementation style:

1. early steal
2. later recognition

## Risks And Non-Goals

### Do not conflate intercept with indicator

The indicator is visual feedback only.

Intercept mode changes pointer ownership.

These should remain separate switches.

### Do not make intercept dependent on hint visibility

The hint may appear only after some movement threshold.

Interception, by contrast, should happen on the initial hot-zone hit so it can actually protect the stream from SystemUI.

### Do not encode corner intercept as a fake rectangle

Using bounding rects for corners would drift away from the real corner-circle semantics and make steal behavior inconsistent near the diagonals.

## Suggested Implementation Order For Tasking

1. Add `optional bool isIntercept` to `CornerSwipeToCenter` proto and UI fact model.
2. Add the switch to `CornerSwipeToCenterSelectDialog.kt` and summary rendering.
3. Extend `DetectorServiceImpl.updateQuickCheckDataOnRuleChange()` with a corner intercept cache.
4. Extend `DetectorServiceImpl.shouldStealMotionEvent(...)` with corner-circle hit testing.
5. Keep `CornerSwipeToCenterDetector` recognition logic unchanged except for any needed cleanup around state transitions.
6. Validate that indicator mode and intercept mode can be enabled independently.

## Main Files For The Future Task

- `core-api/src/main/proto/shortx/Facts.proto`
- `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/Fact.kt`
- `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/FactToProtoFact.kt`
- `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/ProtoAnyToFact.kt`
- `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/mapper/FactI18N.kt`
- `ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/selector/CornerSwipeToCenterSelectDialog.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/InputMonitorInstaller.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/DetectorServiceImpl.kt`
- `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/CornerSwipeToCenterDetector.kt`