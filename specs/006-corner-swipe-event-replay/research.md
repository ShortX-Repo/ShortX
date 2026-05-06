# Research: Corner Swipe Event Caching and Replay

**Date**: 2026-04-30  
**Feature**: Corner Swipe Event Caching and Replay

## Decision 1: Follow EdgeGestureEngine pattern

**Decision**: Use the same event caching/replay pattern as `EdgeGestureEngine`.

**Rationale**: The `EdgeGestureEngine` already implements this pattern successfully:
- Cache DOWN via `MotionEvent.obtain(event)`
- Replay via `injectionBridge.injectEvent(copy)` on cancellation
- Recycle (consume) on success

The same approach applies to corner swipe.

**Alternatives considered**:
- Don't cache/replay: Rejected — corner touches would be "lost" when gesture cancelled
- Cache as serialized bytes: Rejected — `MotionEvent.obtain()` is the standard approach

## Decision 2: Return true on DOWN when corner detected

**Decision**: Return `true` from `onTouchEvent()` on `ACTION_DOWN` when a corner is detected.

**Rationale**: Currently the method returns `false` on DOWN, which causes the event to pass through to the edge engine. If a corner is detected, the corner detector should claim the event to prevent the edge engine from also processing it.

**Alternatives considered**:
- Let both detectors process DOWN: Rejected — causes conflicts when both try to trigger
- Use a separate flag to indicate corner was detected: Rejected — returning `true` is cleaner

## Decision 3: Pass InputInjectionBridge as constructor parameter

**Decision**: Add `injectionBridge: InputInjectionBridge` to the `CornerSwipeToCenterDetector` constructor.

**Rationale**: The detector needs access to the injection bridge for replaying events. The same bridge is already used by `EdgeGestureEngine` and is available in `ShortXGestureMain`.

**Alternatives considered**:
- Create a new bridge instance: Rejected — reuses existing instance
- Pass as method parameter: Rejected — constructor is cleaner for a dependency
