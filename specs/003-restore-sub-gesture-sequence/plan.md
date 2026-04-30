# Implementation Plan: Restore Sub-Gesture Sequence Detection

**Branch**: `feature/003-restore-sub-gesture-sequence` | **Date**: 2026-04-30 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/003-restore-sub-gesture-sequence/spec.md`

## Summary

Wire the existing `EdgeGestureSequenceDetector` into the new `EdgeGestureEngine` state machine. The sequence detector is fully implemented (442 lines, 7 unit tests) but was disconnected when `Detector.kt` was replaced by `EdgeGestureEngine.kt`. This is an integration task, not a rewrite.

Key changes:
- Add `EdgeGestureSequenceDetector` as a dependency of `EdgeGestureEngine`
- When a swipe is recognized, also check for sequence candidates via `service.findMatchingEdgeGestureSequences()`
- If candidates exist, start sequence tracking and forward subsequent events to the detector
- Create `EdgeGestureSequenceHintReporter` and wire it to existing `GestureHintController` and `GestureHapticController`

## Technical Context

**Language/Version**: Kotlin (primary), targeting JVM/Android  
**Primary Dependencies**: Android SDK, Protobuf (core-api Facts.proto)  
**Storage**: N/A (stateful in-memory detector)  
**Testing**: JUnit 5 + Kotest (existing `EdgeGestureSequenceDetectorTest.kt` with 7 tests)  
**Target Platform**: Android 10+ (API 29+)  
**Project Type**: Android system service module  
**Performance Goals**: No perceptible latency added to simple gesture recognition  
**Constraints**: Must not break existing simple gesture rules; both simple and sequence rules must coexist  
**Scale/Scope**: ~10 files modified/created, ~200 lines of new integration code

## Constitution Check

The project constitution is a template (not filled in). No gates to evaluate.

## Project Structure

### Documentation (this feature)

```text
specs/003-restore-sub-gesture-sequence/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
└── contracts/           # Phase 1 output
```

### Source Code (repository root)

```text
services/src/main/java/tornaco/apps/shortx/services/gesture/
├── ShortXGestureMain.kt                      # MODIFY: Wire sequence detector
└── bpok/gesture/
    ├── EdgeGestureEngine.kt                  # MODIFY: Add sequence tracking
    ├── EdgeGestureSequenceDetector.kt        # PRESERVE: Already implemented
    ├── EdgeGestureSequenceHintReporter.kt    # PRESERVE: Already implemented
    ├── EdgeGestureHintDetector.kt            # PRESERVE: Already implemented
    ├── GestureHintController.kt              # PRESERVE: Already implemented
    ├── GestureHapticController.kt            # PRESERVE: Already implemented
    ├── GestureHintModel.kt                   # PRESERVE: Already implemented
    ├── GestureHintState.kt                   # PRESERVE: Already implemented
    └── DetectorService.kt                    # PRESERVE: Already implemented
```

**Structure Decision**: Modify only `EdgeGestureEngine.kt` and `ShortXGestureMain.kt`. All other components are already implemented and just need to be wired.

## Implementation Phases

### Phase 1: Wire Sequence Detector into Engine

**Goal**: When a swipe is recognized and sequence candidates exist, start sequence tracking and forward subsequent events.

#### Step 1.1: Add sequence detector to EdgeGestureEngine

Add `EdgeGestureSequenceDetector` as a constructor parameter or create it internally. Add `EdgeGestureSequenceHintReporter` for feedback.

#### Step 1.2: Check for sequence candidates on swipe recognition

In `startExecution()` (or the swipe threshold check), also call `service.findMatchingEdgeGestureSequences()`. If candidates exist, call `sequenceDetector.startTracking()`.

#### Step 1.3: Forward events to sequence detector

When `sequenceDetector.isTracking()`, forward MOVE/UP/CANCEL events to `sequenceDetector.onTouchEvent()` instead of processing them normally in the engine.

#### Step 1.4: Cancel sequence on new DOWN

In `handleEdgeDownStart()`, call `sequenceDetector.cancelTracking()` before processing the new touch.

#### Step 1.5: Wire in ShortXGestureMain

Create `GestureHintController`, `GestureHapticController`, `EdgeGestureSequenceHintReporter`, and `EdgeGestureSequenceDetector` in `ShortXGestureMain`, pass them to the engine.

### Phase 2: Preserve coexistence with simple rules

**Goal**: Simple rules and sequence rules both fire for the same initial swipe.

The key insight: `service.handleEdgeGestureEvent()` already excludes rules with `childStepsCount > 0` (in `shouldFireEdgeGesture()`). So the simple rule path and sequence path are already separated. We just need to:
1. Fire the simple rule (existing behavior)
2. Also check for sequence candidates (new behavior)
3. If sequence candidates exist, start tracking (new behavior)

Both can happen for the same initial swipe.

## Research: Integration Points

### Decision 1: Where to start sequence tracking

**Decision**: Start sequence tracking in `startExecution()` of `EdgeGestureEngine`, right after firing the simple rule.

**Rationale**: The sequence detector needs the primary gesture and anchor coordinates, which are available when the swipe threshold is met. Starting here ensures:
- The simple rule fires immediately (existing behavior)
- The sequence detector starts tracking if candidates exist (new behavior)
- The finger is already moving, so the sequence detector can immediately start matching child steps

**Alternative**: Start in `handleUp()` — rejected because the finger is already up, and hover steps need the finger to be down.

### Decision 2: Event forwarding strategy

**Decision**: When `sequenceDetector.isTracking()`, forward MOVE/UP/CANCEL to the detector. For MOVE, also continue the engine's normal swipe tracking (for visual feedback). For UP, let the sequence detector handle it (it will cancel or complete).

**Rationale**: The sequence detector needs to see UP events for hover completion. The engine's normal UP handling (execute swipe action) should be skipped when sequence tracking is active, because the sequence detector will fire the final action via `service.fireEdgeGestureSequence()`.

### Decision 3: Sequence detector lifecycle

**Decision**: Create the sequence detector once in `ShortXGestureMain` and inject it into the engine. The sequence detector is stateful but lightweight.

**Rationale**: The sequence detector uses `GestureSequenceScheduler` (wraps `Handler`) and `DetectorService` — both are already available in `ShortXGestureMain`. Creating it once avoids unnecessary allocations.

## Data Model

No new entities. All entities already exist:
- `EdgeGestureSequenceCandidate` (DetectorService.kt:9-12)
- `EdgeGestureStep` (Facts.proto)
- `EdgeGestureStepType` (Facts.proto)
- `EdgeGesture.childSteps` (Facts.proto)

## Contracts

### EdgeGestureEngine (modified)

```kotlin
class EdgeGestureEngine(
    // ... existing params ...
    private val sequenceDetector: EdgeGestureSequenceDetector,
) {
    // No new public methods needed
    // handleTouchSequence() now also forwards to sequenceDetector when tracking
}
```

### ShortXGestureMain (modified)

```kotlin
class ShortXGestureMain(private val context: Context) {
    // New: create sequence infrastructure
    private val gestureHintController = GestureHintController(context)
    private val gestureHapticController = GestureHapticController(context)
    private val sequenceHintReporter = EdgeGestureSequenceHintReporter(
        gestureHintController, gestureHapticController
    )
    private val sequenceDetector = EdgeGestureSequenceDetector(
        handler = detectorHandler,
        service = service,
        hintSink = sequenceHintReporter,
    )

    // Pass sequenceDetector to engine
    val engine = EdgeGestureEngine(
        // ... existing params ...
        sequenceDetector = sequenceDetector,
    )
}
```

## Quickstart

### Prerequisites
- All existing components are already implemented
- `EdgeGestureSequenceDetectorTest.kt` (7 tests) should pass

### Verification
1. Configure a rule with `childSteps` (e.g., SwipeFromBottom + SwipeLeft)
2. Perform the sequence on device
3. Verify the rule fires
4. Verify simple (non-sequence) rules still work
5. Run `EdgeGestureSequenceDetectorTest` — all 7 tests pass

### Running Tests
```bash
./gradlew :services:test --tests "tornaco.apps.shortx.services.gesture.bpok.gesture.EdgeGestureSequenceDetectorTest"
```
