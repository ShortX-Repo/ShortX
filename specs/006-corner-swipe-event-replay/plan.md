# Implementation Plan: Corner Swipe Event Caching and Replay

**Branch**: `feature/006-corner-swipe-event-replay` | **Date**: 2026-04-30 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/006-corner-swipe-event-replay/spec.md`

## Summary

Modify `CornerSwipeToCenterDetector` to cache the initial DOWN event and replay it on cancellation, following the same pattern used by `EdgeGestureEngine`. Currently, the corner detector returns `false` on DOWN (event passes to edge engine) and has no event caching/replay mechanism.

Key changes:
- Add `InputInjectionBridge` as constructor parameter to `CornerSwipeToCenterDetector`
- Cache DOWN event when corner is detected
- Return `true` on DOWN when corner is detected (consume event)
- Replay cached event on cancellation
- Consume cached event on successful trigger
- Pass `injectionBridge` from `ShortXGestureMain`

## Technical Context

**Language/Version**: Kotlin (primary), targeting JVM/Android  
**Primary Dependencies**: Android SDK (MotionEvent)  
**Storage**: N/A  
**Testing**: N/A (existing component, modification only)  
**Target Platform**: Android 10+ (API 29+)  
**Project Type**: Android system service module  
**Performance Goals**: No perceptible latency  
**Constraints**: Must not break existing corner swipe functionality  
**Scale/Scope**: ~2 files modified, ~30 lines of changes

## Constitution Check

The project constitution is a template (not filled in). No gates to evaluate.

## Project Structure

### Source Code

```text
services/src/main/java/tornaco/apps/shortx/services/gesture/
├── ShortXGestureMain.kt                      # MODIFY: Pass injectionBridge to corner detector
└── bpok/gesture/
    ├── CornerSwipeToCenterDetector.kt        # MODIFY: Add event caching/replay
    └── InputInjectionBridge.kt               # PRESERVE: Already implemented
```

## Implementation

### Step 1: Add InputInjectionBridge to CornerSwipeToCenterDetector

Add `injectionBridge: InputInjectionBridge` as constructor parameter.

### Step 2: Add event caching state

Add `cachedDownEvent: MotionEvent?` field. In `ACTION_DOWN`, when corner is detected:
- Cache the event via `MotionEvent.obtain(event)`
- Return `true` (consume the event)

### Step 3: Add replay on cancellation

In `ACTION_UP`, `ACTION_CANCEL`, `ACTION_POINTER_UP` (when not triggered):
- Replay cached event via `injectionBridge.injectEvent(copy)`
- Recycle cached event

### Step 4: Consume on successful trigger

When gesture fires successfully:
- Recycle cached event (don't replay)

### Step 5: Pass injectionBridge from ShortXGestureMain

In `ShortXGestureMain`, pass `injectionBridge` to `CornerSwipeToCenterDetector` constructor.

## Research

### Decision: Follow EdgeGestureEngine pattern

**Decision**: Use the same event caching/replay pattern as `EdgeGestureEngine`.

**Rationale**: The `EdgeGestureEngine` already implements this pattern successfully. The same approach (cache DOWN, replay on cancel, consume on success) applies to corner swipe.

**Pattern**:
```kotlin
// Cache on DOWN
cachedDownEvent = MotionEvent.obtain(event)

// Replay on cancel
val copy = MotionEvent.obtain(cachedDownEvent)
injectionBridge.injectEvent(copy)
cachedDownEvent?.recycle()
cachedDownEvent = null

// Consume on success
cachedDownEvent?.recycle()
cachedDownEvent = null
```

## Contracts

### CornerSwipeToCenterDetector (modified)

```kotlin
class CornerSwipeToCenterDetector(
    private val context: Context,
    private val bridge: GestureBridge,
    private val gestureHintController: GestureHintController,
    private val gestureHapticController: GestureHapticController,
    private val injectionBridge: InputInjectionBridge,  // NEW
) {
    private var cachedDownEvent: MotionEvent? = null  // NEW

    fun onTouchEvent(event: MotionEvent): Boolean
    // ACTION_DOWN: cache event, return true if corner detected
    // ACTION_MOVE: process gesture as before
    // ACTION_UP/CANCEL: replay cached event if not triggered
}
```

### ShortXGestureMain (modified)

```kotlin
private val cornerSwipeDetector = CornerSwipeToCenterDetector(
    context = context,
    bridge = service,
    gestureHintController = gestureHintController,
    gestureHapticController = gestureHapticController,
    injectionBridge = injectionBridge,  // NEW
)
```

## Quickstart

### Verification
1. Configure a corner swipe rule
2. Touch corner, don't swipe, release — verify app receives tap (event replayed)
3. Touch corner, swipe wrong direction — verify app receives tap (event replayed)
4. Touch corner, swipe toward center — verify gesture fires (event consumed)
5. Touch edge (not corner) — verify edge gestures still work

### Running Tests
```bash
./gradlew :services:test --tests "tornaco.apps.shortx.services.gesture.*"
```
