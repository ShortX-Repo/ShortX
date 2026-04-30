# Feature Specification: Integrate Gesture Indicator for Simple Swipes

**Feature Branch**: `feature/004-integrate-gesture-indicator`  
**Created**: 2026-04-30  
**Status**: Draft  
**Input**: User description: "阅读手势指示器相关功能，基于新代码，将手势指示功能接入。"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Visual Hint for Simple Edge Swipes (Priority: P1)

As a user, when I touch an enabled edge region and start swiping, I want to see a visual hint indicator showing the swipe direction and progress, so that I know the system is recognizing my gesture.

**Why this priority**: This is the core gesture indicator functionality. Without it, simple edge swipes have no visual feedback — the user doesn't know if the gesture is being recognized until it fires.

**Independent Test**: Touch an enabled edge region, observe the hint dot appears. Swipe in the configured direction, observe the hint shows progress. Reach the threshold, observe the hint changes to "ready" state.

**Acceptance Scenarios**:

1. **Given** a simple swipe rule is configured with hint enabled, **When** the user touches the edge region, **Then** a small dot indicator appears at the touch point.
2. **Given** the user is swiping in the correct direction, **When** the swipe progresses, **Then** the hint shows a directional arrow growing in the swipe direction.
3. **Given** the swipe reaches the trigger threshold, **When** the gesture is ready to fire, **Then** the hint changes to "ready" state with appropriate visual feedback.
4. **Given** the gesture fires successfully, **When** the action is triggered, **Then** a success animation plays briefly before the hint disappears.
5. **Given** the user swipes in the wrong direction or cancels, **When** the gesture is cancelled, **Then** the hint fades out.

---

### User Story 2 - Haptic Feedback for Simple Edge Swipes (Priority: P1)

As a user, when I perform an edge swipe, I want to feel haptic feedback at key moments (touch, ready, success), so that I have tactile confirmation of gesture recognition.

**Why this priority**: Haptic feedback complements visual feedback and is part of the existing indicator system.

**Independent Test**: Perform an edge swipe with haptic enabled, verify vibration at appropriate stages.

**Acceptance Scenarios**:

1. **Given** haptic is enabled, **When** the user touches an edge region, **Then** a light vibration occurs (candidate stage).
2. **Given** haptic is enabled, **When** the swipe reaches the trigger threshold, **Then** a medium vibration occurs (ready stage).
3. **Given** haptic is enabled, **When** the gesture fires successfully, **Then** a success vibration pattern occurs.

---

### User Story 3 - Coexistence with Sequence Hints (Priority: P2)

As a user, when I start an edge swipe that has both simple and sequence rules, the simple hint should show during the initial swipe, and the sequence hint should take over if I continue with child steps.

**Why this priority**: The simple hint and sequence hint use the same overlay system. They must not conflict.

**Independent Test**: Configure both simple and sequence rules for the same gesture. Swipe and observe the transition from simple hint to sequence hint.

**Acceptance Scenarios**:

1. **Given** a gesture has both simple and sequence rules, **When** the user starts swiping, **Then** the simple hint shows first.
2. **Given** the user continues into a sequence, **When** the sequence detector starts tracking, **Then** the simple hint is replaced by the sequence hint.
3. **Given** the user only performs the simple swipe (no child steps), **When** the gesture fires, **Then** the simple hint shows the success animation.

---

### Edge Cases

- What happens when the user touches an edge region that has no feedback-enabled rules?
  → No hint should appear.
- What happens when the user touches outside all edge regions?
  → No hint should appear.
- What happens when multiple rules have different hint/haptic settings for the same gesture?
  → The hint should show if ANY rule has hint enabled; haptic should fire if ANY rule has haptic enabled.
- What happens when the screen rotates while a hint is showing?
  → The hint should be cancelled (existing `GestureHintController.onConfigurationChanged()` behavior).

## Requirements *(mandatory)*

### Functional Requirements

#### Hint Detector Integration

- **FR-001**: System MUST create an `EdgeGestureHintDetector` instance and wire it to receive touch events.
- **FR-002**: System MUST forward ACTION_DOWN events to the hint detector when the touch lands on a feedback-enabled edge region.
- **FR-003**: System MUST forward ACTION_MOVE events to the hint detector during gesture tracking.
- **FR-004**: System MUST forward ACTION_UP and ACTION_CANCEL events to the hint detector.
- **FR-005**: System MUST call `onEdgeGestureTriggered()` on the hint detector when a simple gesture fires, so the hint transitions to success state.

#### Feedback Configuration

- **FR-006**: System MUST respect the `enableGestureHint` flag on each rule to determine whether to show visual hints.
- **FR-007**: System MUST respect the `enableGestureHaptic` flag on each rule to determine whether to provide haptic feedback.
- **FR-008**: System MUST show the hint if ANY matching rule has hint enabled (OR logic).
- **FR-009**: System MUST fire haptic if ANY matching rule has haptic enabled (OR logic).

#### Coexistence

- **FR-010**: System MUST cancel the simple hint when a sequence starts (`onEdgeGestureSequenceStarted()`).
- **FR-011**: System MUST NOT interfere with the sequence hint reporter when both are active.

### Key Entities

- **EdgeGestureHintDetector**: Detects simple swipe gestures and drives hint feedback. Key state: trackedToken, trackedAreas, downX/Y, hasHintShown, hasTriggered.
- **GestureHintController**: Manages the overlay window lifecycle. Key state: activeToken, view, isAttached.
- **GestureHapticController**: Manages haptic feedback. Key state: stageByToken.
- **GestureHintModel**: Immutable data for rendering one frame. Key fields: token, anchorX/Y, directionX/Y, progress, state, showArrow.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Visual hint appears within one frame of touching an enabled edge region (16ms at 60fps).
- **SC-002**: Hint progress updates smoothly as the user swipes, with no visible stutter.
- **SC-003**: Hint transitions through Candidate → Showing → Ready → Success states correctly.
- **SC-004**: Haptic feedback fires at the correct stages (candidate, ready, success).
- **SC-005**: Simple hints and sequence hints coexist without visual glitches or overlay conflicts.
- **SC-006**: Hint overlay does not interfere with touch input (non-interactive overlay).

## Assumptions

- `EdgeGestureHintDetector` is already fully implemented (340 lines).
- `GestureHintController`, `GestureHintView`, `GestureHapticController` are already implemented and working.
- The sequence hint path (`EdgeGestureSequenceHintReporter`) is already active.
- The `GestureEngineListener` interface already exposes `onEdgeTouchDown()` and `onSwipeProgress()` callbacks.
- The integration point is `ShortXGestureMain` — create the hint detector and wire it to the engine.
