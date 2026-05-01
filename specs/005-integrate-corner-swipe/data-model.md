# Data Model: Integrate Corner Swipe-to-Center Detector

**Date**: 2026-04-30  
**Feature**: Integrate Corner Swipe-to-Center Detector

No new entities. All entities already exist in the codebase.

## Existing Entities (preserved)

### ScreenCorner

Defined in `Facts.proto`. Enum with 4 values:
- `CornerTopLeft` (0) — top-left corner
- `CornerTopRight` (1) — top-right corner
- `CornerBottomLeft` (2) — bottom-left corner
- `CornerBottomRight` (3) — bottom-right corner

### CornerSwipeToCenter

Defined in `Facts.proto`. Protobuf message:
- `corner: ScreenCorner` — which corner was swiped
- `downX: Int` — touch-down X coordinate
- `downY: Int` — touch-down Y coordinate
- `enableGestureHint: Boolean` — whether to show visual hint
- `enableGestureHaptic: Boolean` — whether to provide haptic feedback
- `isIntercept: Boolean` — whether to intercept the event

### CornerSwipeToCenterDetector

Already implemented (219 lines). Key state:
- `trackedCorner: ScreenCorner?` — the corner detected at ACTION_DOWN
- `trackedPointerId: Int` — the pointer being tracked
- `downX/downY: Float` — initial touch coordinates
- `hasTriggered: Boolean` — prevents double-firing
- `trackedToken: Long` — correlates with hint/haptic controllers

### CornerSwipeToCenterGeometry

Already implemented (41 lines). Singleton utility:
- `radius(width, height, radiusRatio)` — computes corner region radius
- `contains(corner, x, y, width, height, radiusRatio)` — quarter-circle hit test
- `cornerPoint(corner, width, height)` — returns corner pixel coordinates
