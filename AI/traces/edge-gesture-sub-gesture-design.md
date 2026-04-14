# Edge Gesture Sub-Gesture Design

## Purpose

This document defines a concrete implementation plan for layered edge gestures, also referred to here as sub-gestures.

The target product behavior is:

1. a user first triggers a configured edge gesture hot area
2. after the primary gesture succeeds, the system enters a short follow-up recognition window
3. one or more additional gesture steps can then be matched from the same touch stream
4. the rule only fires when the entire sequence matches

Example sequences:

1. right-center edge, swipe left, then swipe down
2. bottom-center edge, swipe up, then hover

The design is intentionally based on the current `EdgeGesture` pipeline instead of inventing a parallel gesture engine.

## Requirements

The first implementation should support:

1. one primary edge gesture plus up to one child gesture in UI
2. internal data model that can expand to multiple child steps later
3. child gesture types:
   - left
   - right
   - up
   - down
   - hover
4. same-pointer continuous recognition without lifting the finger between steps
5. rule firing only after the full sequence succeeds
6. compatibility with existing edge hot area concepts:
   - predefined edge
   - custom rect
   - intercept mode
   - front app constraints
   - gesture hint

## Current Architecture Summary

Today edge gesture flow is:

1. UI saves `Fact.EdgeGesture`
2. proto fact is persisted as `Facts.proto::EdgeGesture`
3. patch-side `Detector` recognizes a single gesture event
4. `DetectorServiceImpl.handleEdgeGestureEvent(...)` decides whether it should fire
5. patch sends runtime `EdgeGesture` back to `services` through `fireEdgeGesture(...)`
6. `ShortXService` republishes that runtime fact into the rule engine

Current limitation:

1. `EdgeGesture` only models a single `gesture`
2. `Gesture` enum has no `Hover`
3. `Detector` assumes success is immediate and fires as soon as the first gesture is recognized

That means sub-gesture support requires both schema and runtime recognition changes.

## Core Design Decision

Sub-gesture support should extend `EdgeGesture` instead of introducing a separate fact type.

Reasoning:

1. the first-class trigger is still an edge hot area
2. all existing edge-specific settings already belong to this concept
3. users think of this as one advanced edge gesture, not as two unrelated facts
4. the patch-side recognition still begins from the existing edge/custom-rect hit test

So the correct model is:

1. one `EdgeGesture` fact
2. one legacy primary `gesture`
3. zero or more child steps

## Proposed Schema Changes

### 1. Add a new gesture-step enum

Current `Gesture` enum is tightly coupled to the legacy one-step detector.

To avoid ambiguous reuse, introduce a new step enum for sequence matching.

Suggested proto shape:

```proto
enum EdgeGestureStepType {
  EdgeGestureStepType_SwipeLeft = 0;
  EdgeGestureStepType_SwipeRight = 1;
  EdgeGestureStepType_SwipeUp = 2;
  EdgeGestureStepType_SwipeDown = 3;
  EdgeGestureStepType_Hover = 4;
  EdgeGestureStepType_SingleTap = 5;
  EdgeGestureStepType_DoubleTap = 6;
  EdgeGestureStepType_LongTap = 7;
}

message EdgeGestureStep {
  EdgeGestureStepType type = 1;
}
```

Notes:

1. the old `Gesture` enum can remain for migration compatibility initially
2. the new step enum uses direction names independent of edge names
3. hover is explicitly modeled here

### 2. Extend `Facts.proto::EdgeGesture`

Suggested shape:

```proto
message EdgeGesture {
  Edge edge = 1;
  Gesture gesture = 2;
  bool isIntercept = 3;
  repeated AppPkg apps = 4;
  repeated string pkgSets = 5;
  Rect customRect = 6;
  int32 downX = 7;
  int32 downY = 8;
  bool isTriggerCustom = 9;
  optional bool enableGestureHint = 10;

   repeated EdgeGestureStep childSteps = 11;

  string tag = 97;
  CustomContextDataKey customContextDataKey = 98;
  string note = 99;
  string id = 100;
  bool isDisabled = 101;
}
```

Migration policy:

1. old rules with only `gesture` continue to behave as one-step edge gestures
2. new rules keep using `gesture` as the primary step and write optional `childSteps`
3. runtime matcher compares legacy `gesture` first, then optional `childSteps`

### 3. Runtime fact content

Runtime `EdgeGesture` should include the full matched sequence, not only the primary step.

Suggested addition later if needed:

```proto
repeated EdgeGestureStep matchedSteps = 13;
```

For first implementation this is optional because matching can still compare configured `gesture + childSteps` against the runtime event's same fields.

## UI Model Changes

Files to change:

1. `ui-base/.../fact/model/Fact.kt`
2. `ui-base/.../fact/model/FactToProtoFact.kt`
3. `ui-base/.../fact/model/ProtoAnyToFact.kt`
4. `ui/.../fact/selector/EdgeGestureSelectDialog.kt`
5. related i18n / summary / icon mapper files

### Proposed UI model

Extend `Fact.EdgeGesture` with:

1. existing `gesture`
2. `childSteps: List<EdgeGestureStepModel>`

Suggested Kotlin model:

```kotlin
enum class EdgeGestureStepModel {
    SwipeLeft,
    SwipeRight,
    SwipeUp,
    SwipeDown,
    Hover,
    SingleTap,
    DoubleTap,
    LongTap,
}
```

First UI release can limit editing to:

1. exactly one primary step
2. zero or one child step

but the model should still store a list.

### UI interaction proposal

In `EdgeGestureSelectDialog.kt`:

1. rename current gesture selector label to primary gesture
2. add a child gesture section with:
   - disabled by default
   - one dropdown for child step type
   - optional delete action
3. for first version, support only one child step in UI

Summary rendering should read like:

1. `BottomCenter -> SwipeUp`
2. `BottomCenter -> SwipeUp -> Hover`

## Hover Definition

`Hover` should not rely on Android hover events.

It should be defined as a touch hold state after a previous gesture step succeeds.

Recommended definition:

1. same pointer remains down
2. movement stays within a small radius from the last step completion anchor
3. hold duration exceeds a threshold such as 300 ms

Suggested defaults:

1. hover radius: 18 dp to 24 dp
2. hover duration: 300 ms

This matches finger-touch semantics and works with the existing MotionEvent pipeline.

## Patch-Side Runtime Design

### 1. Keep the existing area detection stage

No change is needed for:

1. edge / custom rect hit-testing in `Detector.detectArea(...)`
2. intercept geometry caching in `DetectorServiceImpl`

The first stage remains:

1. input begins inside a configured edge area

### 2. Introduce a dedicated sequence recognizer

Do not overload the existing one-step logic inside `Detector`.

Add a new class such as:

1. `EdgeGestureSequenceDetector.kt`

Responsibilities:

1. start when `Detector` finds candidate areas
2. track one active pointer and one matching candidate sequence
3. step through configured gesture sequence
4. produce success only after the final child step matches
5. cancel on timeout, wrong direction, pointer loss, or finger-up before completion

### 3. State machine

Suggested states:

1. `Idle`
2. `TrackingPrimary`
3. `WaitingNextStep(stepIndex)`
4. `HoverPending(stepIndex)`
5. `Succeeded`
6. `Cancelled`

Tracked runtime data:

1. `pointerId`
2. `areas`
3. `matchedRule`
4. `stepIndex`
5. `stepAnchorX`
6. `stepAnchorY`
7. `stepStartedAtMillis`
8. `downX`
9. `downY`

### 4. Recognition strategy per step

Directional steps:

1. after previous step anchor is fixed, measure delta from that anchor
2. require minimum travel distance
3. require direction cosine threshold similar to current corner detector
4. on success, advance to next step and reset anchor to the current point

Tap steps:

1. for now only needed if product wants them as child steps
2. can reuse `GestureDetector`, but only while sequence tracking is active

Hover step:

1. enter `HoverPending`
2. continuously verify movement stays within hover radius
3. if duration threshold is reached, mark step success
4. if movement exceeds radius, cancel hover

### 5. Timeout policy

Recommended defaults:

1. step transition timeout: 700 ms
2. hover timeout window: 300 ms to success

If the next step does not arrive in time, cancel the sequence.

## `Detector.kt` Integration Plan

Current `Detector` immediately converts single swipe/tap callbacks into `handleEdgeGestureEvent(...)`.

That should change to:

1. retain existing tap/swipe detectors for legacy one-step gestures
2. route candidate edge events through `EdgeGestureSequenceDetector`
3. only if the rule is legacy one-step, allow the old immediate fire path

Recommended integration:

1. `Detector.detectArea(...)` still resolves matching hot areas
2. `EdgeGestureSequenceDetector` receives raw MotionEvents and areas
3. for sequence-enabled rules it owns the stream until success/failure
4. on success it calls a new service method such as `handleEdgeGestureSequenceEvent(...)`
5. old `handleEdgeGestureEvent(...)` remains for one-step rules

This avoids destabilizing existing behavior.

## `DetectorServiceImpl` Changes

Files to change:

1. `DetectorService.kt`
2. `DetectorServiceImpl.kt`

### New quick-check data

Today `enabledEdgeGestures` is enough for one-step matching.

For sequence support, we still cache whole `EdgeGesture` facts, but matching becomes:

1. same area
2. same primary step
3. same child step list

No new geometry cache is required beyond current edge intercept caches.

### New handler method

Add a method like:

1. `handleEdgeGestureSequenceEvent(area, matchedRule, downX, downY): Boolean`

Responsibilities:

1. build runtime `EdgeGesture` proto including sequence fields
2. preserve `edge` or `customRect` and `isTriggerCustom`
3. send runtime event through `shortx.fireEdgeGesture(...)`

`shouldFireEdgeGesture(...)` remains as the fast filter for legacy one-step gestures.

## Services-Side Matching

Current service-side rule engine already republishes runtime `EdgeGesture` facts.

Recommended matching strategy:

1. legacy rules with only `gesture` continue using current comparison
2. sequence rules compare:
   - area definition
   - primary step
   - child step list

This likely requires updates in the fact matcher for `EdgeGesture`.

Important rule:

1. do not publish intermediate primary-step-only facts
2. only publish the final full-sequence fact

That keeps rule execution atomic.

## Intercept Mode Behavior

Intercept mode should remain tied to the primary hot area only.

That means:

1. if an edge gesture rule has `isIntercept == true`, pointer stealing still happens as soon as the stream starts inside the edge area
2. sequence recognition then proceeds on the stolen stream
3. child gesture geometry does not need its own intercept cache

This aligns with current edge intercept semantics and avoids complexity.

## Backward Compatibility

Recommended migration behavior:

1. existing rules with `gesture` but no `childSteps` are treated as single-step rules
2. UI loads them with the original `gesture` and an empty child list
3. save path can either:
   - continue writing legacy `gesture` plus child steps during transition, or
   - write only `gesture + childSteps` after full migration

Safer initial choice:

1. keep writing the original `gesture`
2. add `childSteps` only for second-level sequence matching

## Testing Plan

### Unit tests

1. proto/UI roundtrip for `Fact.EdgeGesture` with child steps
2. matcher tests for one-step and two-step gestures
3. hover timeout / radius tests for sequence detector
4. step direction matching tests
5. legacy rule migration tests

### Integration tests

1. UI save/load of edge gesture with child gesture
2. patch quick-check cache update after rule changes
3. sequence detector success path for swipe + swipe
4. sequence detector success path for swipe + hover
5. failure path when second step wrong or timed out
6. intercept mode still steals stream at primary area start

### Manual validation

1. existing one-step edge gestures still work unchanged
2. bottom-center swipe up then hover triggers only after hover completes
3. right-center swipe left then down triggers correctly
4. wrong second direction does not fire
5. lifting finger between steps does not fire

## Delivery Phases

### Phase 1

1. extend proto and UI model
2. add one child gesture to editor
3. implement sequence detector for:
   - swipe left
   - swipe right
   - swipe up
   - swipe down
   - hover
4. keep existing one-step path untouched for legacy rules

### Phase 2

1. allow multiple child steps in UI
2. improve gesture hint visuals for multi-step progress
3. expose advanced per-step timeout or hover duration if needed

## Non-Goals For First Release

1. arbitrary branching gesture trees
2. parallel gesture alternatives in one rule
3. mouse or stylus hover event support
4. multi-finger child gestures
5. child gesture-specific front-app constraints

## Final Recommendation

The implementation should be built as:

1. `EdgeGesture` remains the user-facing fact type
2. `EdgeGesture` gains structured primary and child step fields
3. patch side adds a dedicated sequence recognizer
4. only final full-sequence success publishes the runtime fact

This is the lowest-risk approach that fits the current architecture and keeps legacy one-step edge gestures stable.