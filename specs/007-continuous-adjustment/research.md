# Research: Continuous Adjustment Actions

**Date**: 2026-04-30  
**Feature**: Continuous Adjustment Actions

## Decision 1: Action type detection mechanism

**Decision**: Check the proto action type in `startExecution()` to determine if it's continuous.

**Rationale**: The current architecture resolves actions via `actionResolver.resolve()` which returns an `EdgeGesture` proto. The action type is determined by the proto message type. We need to check if the resolved action is a `ContinuousAdjustBrightness` or `ContinuousAdjustVolume` message.

**Alternatives considered**:
- Add a flag to the `EdgeGesture` proto: Rejected — would require modifying existing proto structure
- Use a string-based type check: Rejected — less type-safe

## Decision 2: Overlay architecture

**Decision**: Create a new `ContinuousAdjustOverlayView` similar to `GestureHintView`.

**Rationale**: The existing hint system is designed for gesture detection feedback (circles, arrows), not continuous adjustment feedback (slider/progress bar). A separate overlay provides cleaner separation.

**Alternatives considered**:
- Extend `GestureHintView`: Rejected — visual design is fundamentally different
- Use Android system brightness dialog: Rejected — can't control appearance

## Decision 3: Handler receives Context

**Decision**: Pass `Context` to handlers via the factory method.

**Rationale**: Handlers need `ContentResolver` (for Settings.System) and `AudioManager`. These are obtained from `Context`.

**Alternatives considered**:
- Pass individual system service references: Rejected — less flexible
- Use static singleton: Rejected — harder to test

## Decision 4: Auto-brightness handling

**Decision**: Disable auto-brightness when manual adjustment starts, restore on cancel.

**Rationale**: Manual brightness adjustment doesn't work when auto-brightness is enabled. The user expects the gesture to control brightness directly.

**Alternatives considered**:
- Skip adjustment if auto-brightness is on: Rejected — confusing user experience
- Show a toast message: Rejected — too intrusive

## Decision 5: Sensitivity scale

**Decision**: Use a float sensitivity scale (default 1.0) to control adjustment speed.

**Rationale**: Different users may prefer different adjustment speeds. A scale factor allows customization without changing the core algorithm.

**Alternatives considered**:
- Fixed sensitivity: Rejected — not customizable
- DPI-based sensitivity: Rejected — too complex
