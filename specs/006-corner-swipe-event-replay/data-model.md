# Data Model: Corner Swipe Event Caching and Replay

**Date**: 2026-04-30  
**Feature**: Corner Swipe Event Caching and Replay

No new entities. Modifications to existing entity only.

## Modified Entity: CornerSwipeToCenterDetector

### New State Fields

| Field | Type | Description |
|-------|------|-------------|
| cachedDownEvent | MotionEvent? | Deep copy of initial DOWN event for replay |
| injectionBridge | InputInjectionBridge | Dependency for injecting events into system pipeline |

### State Transitions

```
IDLE → CORNER_DETECTED (on ACTION_DOWN in corner region)
  - Cache DOWN event
  - Return true (consume)

CORNER_DETECTED → TRACKING (on ACTION_MOVE with valid direction)
  - Process gesture as before

CORNER_DETECTED → CANCELLED (on ACTION_UP/CANCEL without trigger)
  - Replay cached event
  - Reset state

CORNER_DETECTED → TRIGGERED (on ACTION_MOVE meeting threshold)
  - Consume cached event (recycle)
  - Fire action

TRACKING → CANCELLED (on direction change, wrong direction)
  - Replay cached event
  - Reset state

TRACKING → TRIGGERED (on threshold met)
  - Consume cached event
  - Fire action

TRIGGERED → IDLE (on ACTION_UP)
  - Reset state
```
