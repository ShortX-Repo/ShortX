# Implementation Plan: Integrate Corner Swipe-to-Center Detector

**Branch**: `feature/005-integrate-corner-swipe` | **Date**: 2026-04-30 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/005-integrate-corner-swipe/spec.md`

## Summary

Wire the existing `CornerSwipeToCenterDetector` into `ShortXGestureMain` to enable corner swipe gestures. The detector is fully implemented (219 lines) but completely disconnected after the refactor. The backend pipeline (rule engine) is already working — only the frontend touch detection is missing.

Key changes:
- Create `CornerSwipeToCenterDetector` instance in `ShortXGestureMain`
- Forward touch events to the detector BEFORE the edge gesture engine (corner takes priority)
- If the corner detector consumes the event, skip the edge engine

## Technical Context

**Language/Version**: Kotlin (primary), targeting JVM/Android  
**Primary Dependencies**: Android SDK (MotionEvent, WindowManager, VibrationEffect)  
**Storage**: N/A  
**Testing**: N/A (existing component, integration only)  
**Target Platform**: Android 10+ (API 29+)  
**Project Type**: Android system service module  
**Performance Goals**: No perceptible latency added to gesture recognition  
**Constraints**: Must not interfere with edge gesture detection when no corner is matched  
**Scale/Scope**: ~1 file modified, ~15 lines of new integration code

## Constitution Check

The project constitution is a template (not filled in). No gates to evaluate.

## Project Structure

### Source Code

```text
services/src/main/java/tornaco/apps/shortx/services/gesture/
├── ShortXGestureMain.kt                      # MODIFY: Create corner detector, forward events
└── bpok/gesture/
    ├── CornerSwipeToCenterDetector.kt        # PRESERVE: Already implemented
    ├── CornerSwipeToCenterGeometry.kt        # PRESERVE: Already implemented
    ├── GestureBridge.kt                      # PRESERVE: Already implemented
    └── GestureBridgeImpl.kt                  # PRESERVE: Already implemented
```

## Implementation

### Step 1: Create corner detector in ShortXGestureMain

Create `CornerSwipeToCenterDetector` with:
- `context` — for screen size queries
- `service` (GestureBridge) — for enabled facts and event firing
- `gestureHintController` — already created
- `gestureHapticController` — already created

### Step 2: Forward events before edge engine

In `filterMotionEvent()`, call `cornerSwipeDetector.onTouchEvent(event)` BEFORE `engine.handleTouchSequence(event)`. If the corner detector consumes the event, return `false` (consumed) and skip the edge engine.

### Step 3: Verify coexistence

Ensure that when no corner is matched, the edge engine still processes the event normally.

## Research

### Decision: Corner detector runs before edge engine

**Decision**: Process corner swipe detection BEFORE edge gesture detection in `filterMotionEvent()`.

**Rationale**: Corner touches may overlap with edge sub-regions (e.g., `LeftTop` or `TopLeft`). If a touch starts in a corner, it should be claimed by the corner detector and not processed as an edge gesture.

**Alternative**: Run corner detector after edge engine — rejected because the edge engine might consume the event first.

### Decision: Standalone detector, not integrated into engine

**Decision**: Keep `CornerSwipeToCenterDetector` as a separate class, not integrated into `EdgeGestureEngine`.

**Rationale**: Corner swipe uses `ScreenCorner` (circular regions) while edge gestures use `Edge` (rectangular bands). These are two distinct spatial models. Keeping them separate maintains clean separation of concerns.

**Alternative**: Integrate into engine — rejected because it mixes two distinct gesture models in one engine.

## Contracts

### ShortXGestureMain (modified)

```kotlin
class ShortXGestureMain(private val context: Context) {
    // Existing: service, gestureHintController, gestureHapticController, engine

    // NEW: corner swipe detector
    private val cornerSwipeDetector = CornerSwipeToCenterDetector(
        context = context,
        bridge = service,
        gestureHintController = gestureHintController,
        gestureHapticController = gestureHapticController,
    )

    fun filterMotionEvent(event: MotionEvent): Boolean? {
        if (!service.shouldDetectMotion) return null

        // Corner swipe takes priority over edge gestures
        val cornerConsumed = cornerSwipeDetector.onTouchEvent(event)
        if (cornerConsumed) return false

        val consumed = engine.handleTouchSequence(event)
        if (consumed) return false
        return null
    }
}
```

## Quickstart

### Verification
1. Configure a corner swipe rule (e.g., top-left corner)
2. Touch the top-left corner — observe hint appears (if enabled)
3. Swipe diagonally toward center — observe progress
4. Reach threshold — gesture fires
5. Verify edge gestures still work when touching outside corners

### Running Tests
```bash
./gradlew :services:test --tests "tornaco.apps.shortx.services.gesture.*"
```
