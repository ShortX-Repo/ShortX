# Research: Integrate Corner Swipe-to-Center Detector

**Date**: 2026-04-30  
**Feature**: Integrate Corner Swipe-to-Center Detector

## Decision 1: Corner detector runs before edge engine

**Decision**: Process corner swipe detection BEFORE edge gesture detection in `filterMotionEvent()`.

**Rationale**: Corner touches may overlap with edge sub-regions (e.g., `LeftTop` or `TopLeft`). If a touch starts in a corner, it should be claimed by the corner detector and not processed as an edge gesture.

**Alternatives considered**:
- Run corner detector after edge engine: Rejected — the edge engine might consume the event first.
- Integrate corner detection into the engine: Rejected — mixes two distinct spatial models (circular vs rectangular).

## Decision 2: Standalone detector architecture

**Decision**: Keep `CornerSwipeToCenterDetector` as a separate class, not integrated into `EdgeGestureEngine`.

**Rationale**: Corner swipe uses `ScreenCorner` (quarter-circle regions) while edge gestures use `Edge` (rectangular bands). These are fundamentally different spatial models. Keeping them separate maintains clean separation of concerns.

**Alternatives considered**:
- Integrate into engine: Rejected — would add complexity to the engine and mix two distinct gesture models.

## Decision 3: Backend pipeline already works

**Decision**: No changes needed to `GestureBridge`, `GestureBridgeImpl`, or `ShortXService`. The backend pipeline is fully intact.

**Rationale**: `GestureBridgeImpl.handleCornerSwipeToCenterEvent()` correctly fires the `CornerSwipeToCenter` protobuf to the rule engine. `GestureBridge.enabledCornerSwipeToCenterFacts` is correctly populated from rules. Only the frontend touch detection is missing.
