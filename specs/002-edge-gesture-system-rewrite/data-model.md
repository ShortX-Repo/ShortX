# Data Model: Edge Gesture System Rewrite

**Date**: 2026-04-30  
**Feature**: Edge Gesture System Rewrite

## Entities

### EdgeRegion

Represents one of the 16 predefined edge zones on the screen.

| Field | Type | Description |
|-------|------|-------------|
| id | Int (0-15) | Unique region identifier, maps to protobuf `Edge` enum |
| parentEdge | EdgeGroup | Parent edge (Left/Right/Top/Bottom) for fallback lookup |
| segment | Segment | Sub-segment position (All/Top/Center/Bottom or All/Left/Center/Right) |
| enabled | Boolean | Whether this region is active for gesture detection |
| rect | RectF | Screen-space rectangle for hit testing |

**Relationships**:
- Each `EdgeRegion` belongs to one `EdgeGroup`
- Each `EdgeGroup` has 4 `EdgeRegion`s (All + 3 segments)
- `EdgeRegion` has many `GestureSlot`s (7 per region)

**State transitions**: `enabled` toggled by user configuration via `DetectorService`

---

### EdgeGroup

The 4 parent edges (Left, Right, Top, Bottom).

| Field | Type | Description |
|-------|------|-------------|
| id | Int (0-3) | Left=0, Right=1, Top=2, Bottom=3 |
| regions | List<EdgeRegion> | The 4 child regions (All + 3 segments) |

---

### GestureSlot

One of the 7 gesture types within an edge region.

| Field | Type | Description |
|-------|------|-------------|
| type | GestureType | Tap, DoubleTap, LongPress, SwipeLeft, SwipeRight, SwipeUp, SwipeDown |
| action | Action? | Bound action (null if unconfigured) |

**GestureType enum values**:
- `Tap` (slot 0)
- `DoubleTap` (slot 1)
- `LongPress` (slot 2)
- `SwipeLeft` (slot 3)
- `SwipeRight` (slot 4)
- `SwipeUp` (slot 5)
- `SwipeDown` (slot 6)

---

### ActionTable

The 16×7 configuration matrix.

| Field | Type | Description |
|-------|------|-------------|
| bindings | Action?[16][7] | 2D array: [edgeRegionId][gestureSlotType] → Action |

**Lookup logic**:
1. Check `bindings[细分RegionId][gestureType]`
2. If null, fall back to `bindings[parentEdgeRegionId][gestureType]`
3. If still null, gesture is not configured → replay cached event

---

### GestureState

The mutable state of an in-progress gesture sequence.

| Field | Type | Description |
|-------|------|-------------|
| downX | Float | Raw X coordinate of ACTION_DOWN |
| downY | Float | Raw Y coordinate of ACTION_DOWN |
| hitRegion | Int | Detected edge region ID (-1 if none) |
| recognizedDirection | Direction | UNKNOWN, LEFT, RIGHT, UP, DOWN |
| gestureCaptured | Boolean | Whether this sequence is owned by the edge gesture system |
| executionStarted | Boolean | Whether the action execution phase has begun |
| cachedDownEvent | MotionEvent? | Deep copy of initial DOWN event for replay |
| firstTapUpEvent | MotionEvent? | First tap's UP event (for double-tap detection) |
| continuousHandler | ContinuousActionHandler? | Active continuous action handler |
| maxTravelDistance | Float | Maximum squared distance traveled during swipe |
| lockedDeviceId | Int | Input device ID locked for this sequence |

**State transitions**:
```
IDLE → EDGE_DOWN (on edge touch)
EDGE_DOWN → DIRECTION_RESOLVED (on move exceeding min threshold)
EDGE_DOWN → LONG_PRESS (on timer expiry)
EDGE_DOWN → TAP (on release before timeout)
TAP → DOUBLE_TAP_WAITING (if double-tap configured)
DOUBLE_TAP_WAITING → DOUBLE_TAP (on second tap within timeout)
DOUBLE_TAP_WAITING → TAP_ACTION (on timeout)
DIRECTION_RESOLVED → EXECUTING (on swipe threshold reached)
DIRECTION_RESOLVED → CANCELLED (on direction change or no action)
EXECUTING → COMPLETED (on action finish)
EXECUTING → CONTINUOUS (if continuous action)
ANY → CANCELLED (on multi-pointer, rotation, etc.)
```

---

### ContinuousActionHandler

Interface for actions that span the gesture lifecycle.

| Method | Parameters | Description |
|--------|-----------|-------------|
| onStart | edgeGroup: Int, x: Float, y: Float, time: Long | Continuous action begins |
| onMove | x: Float, y: Float, time: Float | Finger moved during continuous action |
| onFinish | x: Float, y: Float, time: Long | Finger lifted, finalize action |
| onCancel | (none) | Action cancelled |

---

### GestureHintModel (existing, preserved)

Visual feedback data model.

| Field | Type | Description |
|-------|------|-------------|
| token | Long | Unique identifier for this gesture interaction |
| anchorX | Float | Touch-down X position |
| anchorY | Float | Touch-down Y position |
| directionX | Float | Normalized direction vector X |
| directionY | Float | Normalized direction vector Y |
| progress | Float | 0.0 to 1.0 progress toward completion |
| state | GestureHintState | Showing, Ready, Success |
| showArrow | Boolean | Whether to show directional arrow |

---

### GestureRecord (existing, preserved)

A recorded gesture sequence.

| Field | Type | Description |
|-------|------|-------------|
| id | String | Unique record identifier |
| eventData | List<Bytes> | Serialized MotionEvent byte arrays |
| label | String | User-assigned label |
| duration | Long | Total duration in milliseconds |
| recordingTimeMillis | Long | Wall-clock recording time |
| screenOrientation | ScreenOrientation | Portrait or Landscape at recording time |

---

## Validation Rules

1. Edge region ID must be 0-15 (or -1 for no edge)
2. Gesture slot type must be 0-6
3. Direction must be one of UNKNOWN/LEFT/RIGHT/UP/DOWN
4. Down coordinates must be within screen bounds
5. Swipe distance threshold must be > 0
6. Long-press timeout must be > 0
7. Double-tap timeout must be > 0
8. Edge width/height must be > 0

## Derived Data

- **Parent edge region ID**: For region 0-2 (LeftTop/LeftCenter/LeftBottom) → 3 (LeftAll). For 4-6 → 7 (RightAll). For 8-10 → 11 (TopAll). For 12-14 → 15 (BottomAll). Region 3,7,11,15 are their own parents.
- **Direction parameter**: SwipeLeft→1, SwipeRight→0, SwipeUp→3, SwipeDown→2 (for action dispatch)
- **Sub-gesture slot**: SwipeLeft→1, SwipeRight→2, SwipeUp→3, SwipeDown→4, other→0 (for sub-gesture container)
