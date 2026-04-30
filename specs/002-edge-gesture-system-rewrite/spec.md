# Feature Specification: Edge Gesture System Rewrite

**Feature Branch**: `feature/002-edge-gesture-system-rewrite`  
**Created**: 2026-04-30  
**Status**: Draft  
**Input**: User description: "基于AI/EDGE_GESTURE_TECHNICAL_DOC.md的设计文档，重新实现项目里的手势功能。当前实现在services/src/main/java/tornaco/apps/shortx/services/gesture下。"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Edge Swipe Gesture Recognition (Priority: P1)

As a user, I want to swipe from the edge of my screen (left/right/top/bottom) to trigger configurable actions, so that I can quickly perform system operations without navigating through menus.

**Why this priority**: This is the core functionality of the edge gesture system. Without reliable edge swipe detection, no other feature works.

**Independent Test**: Can be fully tested by swiping from any screen edge and verifying the configured action executes.

**Acceptance Scenarios**:

1. **Given** the user has configured a "back" action for left-edge swipe, **When** the user swipes from the left edge toward the center, **Then** the system executes the back action.
2. **Given** the user swipes from the right edge, **When** the swipe distance exceeds the configured threshold, **Then** the system triggers the right-edge configured action.
3. **Given** the user starts a touch outside the edge zone, **When** they swipe, **Then** the gesture is NOT recognized as an edge gesture and the touch event passes through normally.

---

### User Story 2 - Edge Region Subdivision (Priority: P1)

As a user, I want each screen edge to be divided into segments (top/center/bottom for left/right edges; left/center/right for top/bottom edges), so that I can bind different actions to different parts of the same edge.

**Why this priority**: Subdivision is fundamental to the 16-region model. Without it, users only get 4 gesture zones instead of 16.

**Independent Test**: Configure different actions for left-top vs left-center vs left-bottom, then verify each triggers independently.

**Acceptance Scenarios**:

1. **Given** left edge is divided into top/center/bottom segments, **When** user swipes from the top third of the left edge, **Then** the system triggers the left-top segment action.
2. **Given** a segment has no configured action, **When** user swipes from that segment, **Then** the system falls back to the parent edge (e.g., left-top → left) and checks for an action there.
3. **Given** both a segment and its parent edge have actions configured, **When** user swipes from that segment, **Then** the segment-specific action takes priority over the parent edge action.

---

### User Story 3 - Multiple Gesture Types per Edge Region (Priority: P1)

As a user, I want to bind different actions to tap, double-tap, long-press, and swipe in four directions for each edge region, so that I have maximum flexibility in gesture configuration.

**Why this priority**: The 7-slot model (tap/double-tap/long-press/left/right/up/down) per region is the core configuration structure.

**Independent Test**: Configure a tap action and a swipe-up action on the same edge region, then verify each triggers independently.

**Acceptance Scenarios**:

1. **Given** the bottom edge has a "notification shade" action bound to swipe-up, **When** user swipes up from the bottom edge, **Then** the notification shade opens.
2. **Given** the bottom edge has a "home" action bound to tap, **When** user taps the bottom edge without significant movement, **Then** the system executes the home action.
3. **Given** the bottom edge has a "recent apps" action bound to long-press, **When** user presses and holds the bottom edge for the configured duration, **Then** the recent apps screen opens.

---

### User Story 4 - Event Caching and Replay (Priority: P1)

As a user, when I touch the edge but the gesture doesn't meet the criteria for a valid edge gesture, I want my touch to be passed through to the underlying app normally, so that edge gestures don't interfere with regular app interactions.

**Why this priority**: Without proper event caching and replay, edge gestures would "eat" touches near screen edges, breaking normal app usage.

**Independent Test**: Touch near an edge, move slightly without triggering a gesture, then release. Verify the app receives the touch events.

**Acceptance Scenarios**:

1. **Given** the user touches the edge but doesn't move enough to determine direction, **When** the touch is released before the long-press timeout, **Then** the cached down event is replayed to the system and the app receives a normal tap.
2. **Given** the user swipes from an edge in a direction that has no configured action, **When** the direction is determined, **Then** the cached down event is replayed and the gesture is cancelled.
3. **Given** the user starts a valid edge gesture, **When** the gesture enters the execution phase, **Then** the cached down event is consumed (not replayed) and the action executes.

---

### User Story 5 - Continuous Adjustment Actions (Priority: P2)

As a user, I want to use edge swipes for continuous adjustments (brightness, volume, etc.) where the value changes as I drag, so that I can fine-tune settings with a single gesture.

**Why this priority**: Continuous actions are a key differentiator from simple tap-based gestures but depend on the core swipe detection working first.

**Independent Test**: Configure brightness control on an edge, swipe and hold, then drag up/down to adjust. Verify the brightness changes continuously.

**Acceptance Scenarios**:

1. **Given** the right edge has brightness control configured for swipe-left, **When** user swipes left from the right edge and holds, **Then** brightness adjustment mode activates and dragging up/down changes brightness.
2. **Given** a continuous action is active, **When** user moves their finger, **Then** the adjustment value updates in real-time proportional to finger displacement.
3. **Given** a continuous action is active, **When** user lifts their finger, **Then** the adjustment is finalized and the continuous action handler receives the finish event.

---

### User Story 6 - Visual Feedback During Gestures (Priority: P2)

As a user, I want to see visual feedback when I start an edge gesture (progress indicator, direction hint), so that I know the system is recognizing my gesture and can adjust my swipe accordingly.

**Why this priority**: Visual feedback significantly improves user confidence and accuracy but is not strictly required for core functionality.

**Independent Test**: Start an edge swipe and observe the visual hint. Verify it appears at the touch point and progresses as the swipe continues.

**Acceptance Scenarios**:

1. **Given** the user touches an enabled edge region, **When** the touch is detected, **Then** a visual hint appears near the touch point indicating a candidate gesture.
2. **Given** the user is swiping from an edge, **When** the swipe progresses toward the threshold, **Then** the visual hint shows progress (growing/moving indicator).
3. **Given** the swipe reaches the trigger threshold, **When** the gesture is ready to fire, **Then** the visual hint changes to a "ready" state with appropriate animation.
4. **Given** the gesture fires successfully, **When** the action is triggered, **Then** a success animation plays briefly before the hint disappears.

---

### User Story 7 - Haptic Feedback During Gestures (Priority: P3)

As a user, I want to feel haptic feedback at key gesture stages (candidate, ready, success), so that I have tactile confirmation of gesture recognition.

**Why this priority**: Haptic feedback enhances the gesture experience but is optional. The system works without it.

**Independent Test**: Perform an edge gesture with haptic enabled. Verify vibration occurs at appropriate stages.

**Acceptance Scenarios**:

1. **Given** haptic feedback is enabled, **When** the user touches an edge region, **Then** a light vibration occurs.
2. **Given** haptic feedback is enabled, **When** the swipe reaches the trigger threshold, **Then** a medium vibration occurs.
3. **Given** haptic feedback is enabled, **When** the gesture fires successfully, **Then** a stronger vibration pattern occurs.

---

### User Story 8 - Gesture Recording and Playback (Priority: P3)

As a user, I want to record gesture sequences and play them back, so that I can automate repetitive touch interactions.

**Why this priority**: Recording/playback is a utility feature that extends the gesture system's value but is independent of core edge gesture functionality.

**Independent Test**: Record a gesture sequence, save it, then play it back. Verify the recorded events are injected correctly.

**Acceptance Scenarios**:

1. **Given** the user starts gesture recording, **When** they perform a touch sequence, **Then** all MotionEvents are captured with correct timing.
2. **Given** a gesture is recorded, **When** the user initiates playback, **Then** the events are injected into the system at the configured speed.
3. **Given** playback is in progress, **When** the user cancels, **Then** playback stops immediately.

---

### Edge Cases

- What happens when the user touches with multiple fingers while an edge gesture is being tracked?
  → The system should cancel the current edge gesture and replay the cached event.
- What happens when the screen rotates during an edge gesture?
  → The system should cancel the active gesture and invalidate cached geometry.
- What happens when the soft keyboard is visible and the user touches the bottom edge?
  → The system should skip the bottom edge gesture zone when the soft keyboard is active.
- What happens when the device is locked and edge gestures are configured to be disabled on lock screen?
  → The system should not process edge gestures while the keyguard is active.
- What happens when two edge regions overlap (e.g., corner areas)?
  → The system should use a deterministic priority order for hit-testing.
- What happens when a gesture is in the double-tap waiting window and the timeout expires?
  → The first tap should be treated as a single-tap gesture and execute the single-tap action.

## Requirements *(mandatory)*

### Functional Requirements

#### Input Gateway

- **FR-001**: System MUST intercept MotionEvents at the system input filter level (before they reach target applications).
- **FR-002**: System MUST support re-injecting cached MotionEvents back into the system input pipeline when a gesture is cancelled or invalid.
- **FR-003**: System MUST lock the input device ID for the duration of a gesture sequence to prevent multi-source interference.
- **FR-004**: System MUST filter out non-primary-display input events.
- **FR-005**: System MUST support both legacy (native) and modern input injection APIs for Android version compatibility.

#### Edge Region Detection

- **FR-006**: System MUST define 16 edge regions: 4 edges (left/right/top/bottom), each subdivided into 3 segments plus an "all" variant.
- **FR-007**: System MUST detect whether a touch-down point falls within an enabled edge region.
- **FR-008**: System MUST support configurable edge width and height (in pixels) for hit-testing.
- **FR-009**: System MUST fall back from a细分 region to its parent edge when the细分 region has no action for the detected gesture type.
- **FR-010**: System MUST support a "special entry" region (internal region -2) for edge-specific UI invocation.

#### Gesture State Machine

- **FR-011**: System MUST implement a state machine that processes ACTION_DOWN, ACTION_MOVE, ACTION_UP, ACTION_CANCEL, ACTION_POINTER_DOWN, and ACTION_POINTER_UP.
- **FR-012**: System MUST determine swipe direction using dominant-axis logic (whichever axis has greater displacement), not angle-based bucketing.
- **FR-013**: System MUST support a configurable minimum movement threshold before direction determination begins.
- **FR-014**: System MUST support a configurable swipe distance threshold that must be exceeded to trigger a swipe action.
- **FR-015**: System MUST detect single-tap gestures (touch and release without significant movement, within long-press timeout).
- **FR-016**: System MUST detect double-tap gestures (two taps within a configurable timeout window).
- **FR-017**: System MUST detect long-press gestures (touch held beyond a configurable timeout without significant movement).
- **FR-018**: System MUST cancel the gesture if the swipe direction changes after being determined.
- **FR-019**: System MUST cache the initial ACTION_DOWN event and replay it to the system when the gesture is determined to be invalid.

#### Action Configuration

- **FR-020**: System MUST maintain a 16×7 action table (16 edge regions × 7 gesture slots: tap/double-tap/long-press/left/right/up/down).
- **FR-021**: System MUST support reading action bindings from a configuration repository.
- **FR-022**: System MUST support condition-based action resolution (actions that evaluate conditions before executing).
- **FR-023**: System MUST support wrapped/nested action resolution.

#### Action Execution

- **FR-024**: System MUST execute one-shot actions immediately upon gesture confirmation.
- **FR-025**: System MUST support continuous adjustment actions that receive start/move/end/cancel lifecycle events.
- **FR-026**: System MUST support sub-gesture container actions (action ID 107) that perform secondary gesture selection (hold/left/right/up/down).
- **FR-027**: System MUST support edge entry actions (action ID 65537) that invoke edge-specific UI at the touch point.
- **FR-028**: System MUST dispatch final actions to a system action coordinator for execution.

#### Visual Feedback

- **FR-029**: System MUST display a visual hint overlay when a touch enters an enabled edge region.
- **FR-030**: System MUST show gesture progress (direction, distance) as the user swipes.
- **FR-031**: System MUST animate the hint through states: Candidate → Showing → Ready → Success.
- **FR-032**: System MUST support both directional arrow mode (for swipes) and ring mode (for hover/sequence start).
- **FR-033**: System MUST manage the hint view as a system overlay window.

#### Haptic Feedback

- **FR-034**: System MUST provide haptic feedback at three stages: candidate (light), ready (medium), success (strong pattern).
- **FR-035**: System MUST track haptic stage per gesture token to prevent duplicate vibrations.

#### Gesture Recording

- **FR-036**: System MUST support recording raw MotionEvent sequences during a recording session.
- **FR-037**: System MUST serialize recorded events with correct timing metadata.
- **FR-038**: System MUST support playing back recorded gestures by injecting events at configurable speed.

#### Configuration & Settings

- **FR-039**: System MUST support configurable edge width, edge height, swipe distance threshold, long-press timeout, and double-tap timeout.
- **FR-040**: System MUST support enabling/disabling individual edge regions.
- **FR-041**: System MUST support disabling edge gestures on the lock screen.
- **FR-042**: System MUST support disabling edge gestures when the soft keyboard is visible.
- **FR-043**: System MUST notify registered listeners when gesture settings change.

### Key Entities

- **EdgeRegion**: Represents one of the 16 predefined edge zones. Key attributes: region ID (0-15), parent edge, segment position, enabled state, geometry (rect).
- **GestureSlot**: Represents one of the 7 gesture types within an edge region. Key attributes: slot type (tap/double-tap/long-press/left/right/up/down), bound action.
- **ActionTable**: The 16×7 configuration matrix mapping (edge region, gesture slot) → action.
- **GestureState**: The current state of an in-progress gesture. Key attributes: start coordinates, hit region, recognized direction, execution phase flag, cached event, continuous handler.
- **GestureHintModel**: Visual feedback data model. Key attributes: anchor position, direction vector, progress (0-1), state enum, arrow display flag.
- **GestureRecord**: A recorded gesture sequence. Key attributes: ID, events array with timing, screen orientation, duration.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Edge gesture recognition latency is imperceptible to users (gesture fires within the same touch sequence as the swipe, no visible delay).
- **SC-002**: Gesture false-positive rate is near zero — normal app touches near screen edges pass through without being intercepted.
- **SC-003**: Users can configure and use all 16 edge regions independently with all 7 gesture types.
- **SC-004**: Visual feedback appears within one frame of the touch entering an edge region (16ms at 60fps).
- **SC-005**: The system correctly replays cached events for 100% of cancelled/invalid gestures — users never experience "lost touches" at screen edges.
- **SC-006**: Continuous adjustment actions (brightness, volume) respond smoothly with no perceptible jitter during drag.
- **SC-007**: Gesture recording captures events with timing accuracy sufficient for faithful playback reproduction.
- **SC-008**: The system handles screen rotation, soft keyboard show/hide, and lock/unlock transitions without crashing or leaving stale gesture state.

## Assumptions

- The system runs in a privileged context (system service or Xposed hook) with access to the system input filter pipeline.
- The ShortX rule engine provides the action definitions and execution context; this feature focuses on gesture detection and dispatch.
- The existing `GestureRecorder` and `GesturePlayer` implementations can be preserved or minimally adapted.
- The 16 edge regions map to the existing protobuf `Edge` enum values (TopAll, TopLeft, TopCenter, TopRight, BottomAll, BottomLeft, BottomCenter, BottomRight, LeftAll, LeftTop, LeftCenter, LeftBottom, RightAll, RightTop, RightCenter, RightBottom).
- The existing `GestureHintController`, `GestureHintView`, and `GestureHapticController` can be preserved or adapted for the new detection engine.
- The system targets Android 10+ with version-compatible input injection APIs.
- The existing `DetectorService` interface boundary between detection and rule engine should be preserved.
