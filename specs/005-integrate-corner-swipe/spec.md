# Feature Specification: Integrate Corner Swipe-to-Center Detector

**Feature Branch**: `feature/005-integrate-corner-swipe`  
**Created**: 2026-04-30  
**Status**: Draft  
**Input**: User description: "基于新代码接入屏幕四角触发器，你必须理解之前代码中的屏幕四角手势需求。"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Corner Swipe Gesture Recognition (Priority: P1)

As a user, I want to swipe from a screen corner toward the center to trigger a configured action, so that I can quickly perform system operations using corner gestures.

**Why this priority**: This is the core corner swipe functionality. Without it, corner gestures don't work at all.

**Independent Test**: Configure a corner swipe rule (e.g., swipe from top-left corner), perform the gesture, verify the rule fires.

**Acceptance Scenarios**:

1. **Given** a corner swipe rule is configured for top-left corner, **When** the user touches the top-left corner and swipes diagonally toward the center, **Then** the system fires the corner swipe event.
2. **Given** a corner swipe rule is configured for bottom-right corner, **When** the user touches the bottom-right corner and swipes diagonally toward the center, **Then** the system fires the corner swipe event.
3. **Given** the user touches a corner but swipes in the wrong direction (not toward center), **When** the swipe direction doesn't match the expected diagonal, **Then** the system does NOT fire the corner swipe event.
4. **Given** the user touches outside the corner detection region, **When** they swipe, **Then** the system does NOT recognize a corner gesture.

---

### User Story 2 - Visual Feedback for Corner Swipes (Priority: P2)

As a user, I want to see a visual hint when I start a corner swipe, so that I know the system is recognizing my gesture.

**Why this priority**: Visual feedback improves usability but the corner swipe works without it.

**Independent Test**: Touch a corner region, observe the hint appears. Swipe toward center, observe the hint shows progress.

**Acceptance Scenarios**:

1. **Given** a corner swipe rule has hint enabled, **When** the user touches the corner region, **Then** a visual hint appears at the touch point.
2. **Given** the user is swiping toward the center, **When** the swipe progresses, **Then** the hint shows progress toward the trigger threshold.
3. **Given** the swipe reaches the trigger threshold, **When** the gesture is ready to fire, **Then** the hint changes to "ready" state.
4. **Given** the gesture fires successfully, **When** the action is triggered, **Then** a success animation plays.

---

### User Story 3 - Haptic Feedback for Corner Swipes (Priority: P3)

As a user, I want haptic feedback at key moments during a corner swipe (touch, ready, success).

**Why this priority**: Haptic feedback enhances the experience but is optional.

**Independent Test**: Perform a corner swipe with haptic enabled, verify vibration at appropriate stages.

**Acceptance Scenarios**:

1. **Given** haptic is enabled, **When** the user touches a corner region, **Then** a light vibration occurs.
2. **Given** haptic is enabled, **When** the swipe reaches the trigger threshold, **Then** a medium vibration occurs.
3. **Given** haptic is enabled, **When** the gesture fires successfully, **Then** a success vibration pattern occurs.

---

### Edge Cases

- What happens when the user touches a corner that overlaps with an edge region?
  → The corner detector should take priority over the edge gesture engine.
- What happens when the user touches a corner but doesn't move?
  → No gesture fires (corner swipe requires movement toward center).
- What happens when multiple corners have rules configured?
  → Each corner is independent; the detector uses the first matching corner.
- What happens when the screen rotates?
  → Corner detection regions are recalculated based on new screen dimensions.

## Requirements *(mandatory)*

### Functional Requirements

#### Corner Swipe Detection

- **FR-001**: System MUST detect touches in four screen corner regions (top-left, top-right, bottom-left, bottom-right).
- **FR-002**: System MUST define corner regions as quarter-circles with configurable radius (default 18% of shorter screen dimension).
- **FR-003**: System MUST recognize diagonal swipes toward the screen center from corner regions.
- **FR-004**: System MUST use dot-product matching with configurable maximum angle deviation (default 30 degrees).
- **FR-005**: System MUST require a minimum swipe distance (default 72dp) to trigger the gesture.
- **FR-006**: System MUST fire the corner swipe event to the rule engine when the gesture is confirmed.

#### Priority and Coexistence

- **FR-007**: System MUST process corner swipe detection BEFORE edge gesture detection (corner takes priority).
- **FR-008**: System MUST NOT interfere with edge gesture detection when no corner is matched.

#### Feedback

- **FR-009**: System MUST show visual hints during corner swipe if the rule has hint enabled.
- **FR-010**: System MUST provide haptic feedback at candidate, ready, and success stages if the rule has haptic enabled.

### Key Entities

- **CornerSwipeToCenter**: Protobuf message for corner swipe events. Key fields: corner (ScreenCorner), downX, downY.
- **ScreenCorner**: Enum with values: CornerTopLeft, CornerTopRight, CornerBottomLeft, CornerBottomRight.
- **CornerSwipeToCenterDetector**: State machine for corner swipe detection. Key state: trackedCorner, trackedPointerId, downX/Y, hasTriggered.
- **CornerSwipeToCenterGeometry**: Geometry utility for quarter-circle corner hit testing.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Corner swipe gestures trigger correctly when the user swipes from a corner toward the center.
- **SC-002**: Corner swipe gestures do NOT trigger when the user swipes in the wrong direction.
- **SC-003**: Corner swipe detection does not interfere with edge gesture detection.
- **SC-004**: Visual hints appear during corner swipes when hint is enabled.
- **SC-005**: Haptic feedback fires at appropriate stages when haptic is enabled.

## Assumptions

- `CornerSwipeToCenterDetector` is already fully implemented (219 lines).
- `CornerSwipeToCenterGeometry` is already implemented (41 lines).
- The backend pipeline (`GestureBridge.handleCornerSwipeToCenterEvent()`, `ShortXService.fireCornerSwipeToCenter()`) is already working.
- The `GestureBridge.enabledCornerSwipeToCenterFacts` property is already populated from rules.
- The integration point is `ShortXGestureMain` — create the detector and forward events.
