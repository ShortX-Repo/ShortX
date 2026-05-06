# Research: Integrate Gesture Indicator for Simple Swipes

**Date**: 2026-04-30  
**Feature**: Integrate Gesture Indicator for Simple Swipes

## Decision 1: Where to forward events to hint detector

**Decision**: Forward events in the engine's handler methods (`handleEdgeDownStart`, `handleMove`, `handleUp`, `handleCancel`), BEFORE the engine's own processing logic.

**Rationale**: The hint detector needs to see DOWN events to show the initial candidate indicator. The hint detector's `onTouchEvent()` returns `false` (it never consumes events), so forwarding before the engine's logic doesn't affect the engine's behavior.

**Alternatives considered**:
- Forward in `ShortXGestureMain.filterMotionEvent()`: Rejected — the hint detector should be co-located with the engine.
- Forward after engine processing: Rejected — the engine might cancel/replay before the hint detector sees the event.

## Decision 2: detectAreas implementation

**Decision**: Use `EdgeRegionResolver.resolve()` and wrap the result in a list of `DetectedArea.PredefinedEdge`.

**Rationale**: The hint detector expects `detectAreas: (Float, Float) -> List<DetectedArea>`. The `EdgeRegionResolver` returns a single `Edge`. Wrapping in a list is clean and reuses existing logic.

**Alternatives considered**:
- Duplicate area detection logic: Rejected — `EdgeRegionResolver` already handles this.
- Pass `EdgeRegionResolver` directly: Rejected — the hint detector expects a lambda, not a resolver object.

## Decision 3: Coexistence with sequence hints

**Decision**: Call `hintDetector.onEdgeGestureSequenceStarted()` when sequence candidates are found, to cancel the simple hint before the sequence hint takes over.

**Rationale**: Both hints use the same `GestureHintController` overlay. The simple hint must be cancelled before the sequence hint starts, otherwise they would conflict on the same overlay token.

**Alternatives considered**:
- Let both hints coexist: Rejected — they share the same overlay window and token system.
- Use separate overlay windows: Rejected — unnecessary complexity.
