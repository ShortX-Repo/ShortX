# Data Model: Restore Sub-Gesture Sequence Detection

**Date**: 2026-04-30  
**Feature**: Restore Sub-Gesture Sequence Detection

No new entities. All entities already exist in the codebase.

## Existing Entities (preserved)

### EdgeGestureSequenceCandidate

Defined in `DetectorService.kt:9-12`.

| Field | Type | Description |
|-------|------|-------------|
| area | DetectedArea | The screen edge/region that triggered |
| rule | EdgeGesture | The protobuf rule with child steps |

### EdgeGestureStep

Defined in `Facts.proto`.

| Field | Type | Description |
|-------|------|-------------|
| type | EdgeGestureStepType | The step type (SwipeLeft, SwipeRight, SwipeUp, SwipeDown, Hover, etc.) |

### EdgeGestureStepType

Defined in `Facts.proto`.

| Value | Number | Description |
|-------|--------|-------------|
| SwipeLeft | 0 | Swipe left during sequence |
| SwipeRight | 1 | Swipe right during sequence |
| SwipeUp | 2 | Swipe up during sequence |
| SwipeDown | 3 | Swipe down during sequence |
| Hover | 4 | Hold finger still for duration |
| SingleTapStep | 5 | Single tap during sequence |
| DoubleTapStep | 6 | Double tap during sequence |
| LongTapStep | 7 | Long tap during sequence |

### EdgeGesture (childSteps field)

Defined in `Facts.proto`. The `repeated EdgeGestureStep childSteps = 11` field determines if a rule is a sequence rule (childStepsCount > 0) or a simple rule (childStepsCount == 0).

## State Transitions

The `EdgeGestureSequenceDetector` manages its own state internally:
- `IDLE` → `TRACKING` (on `startTracking()`)
- `TRACKING` → `STEP_MATCHED` (on directional/hover match)
- `STEP_MATCHED` → `TRACKING` (if more steps remain)
- `STEP_MATCHED` → `COMPLETED` (if all steps matched)
- `TRACKING` → `CANCELLED` (on wrong direction, timeout, pointer up without hover)
- `COMPLETED` → `IDLE` (after firing event)
- `CANCELLED` → `IDLE` (after cleanup)
