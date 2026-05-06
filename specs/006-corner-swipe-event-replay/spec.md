# Feature Specification: Corner Swipe Event Caching and Replay

**Feature Branch**: `feature/006-corner-swipe-event-replay`  
**Created**: 2026-04-30  
**Status**: Draft  
**Input**: User description: "屏幕四角触发器要接入手势检测流程，支持事件取消的回放"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Corner Touch Consumes DOWN Event (Priority: P1)

As a user, when I touch a screen corner region, the touch event should be consumed by the corner detector and NOT passed through to the edge gesture engine, so that corner gestures and edge gestures don't conflict.

**Why this priority**: Without proper event consumption, corner touches would be processed by both the corner detector and the edge engine, causing conflicts.

**Independent Test**: Touch a corner region, verify the edge engine does NOT receive the event.

**Acceptance Scenarios**:

1. **Given** a corner swipe rule is configured, **When** the user touches the corner region, **Then** the corner detector consumes the DOWN event and the edge engine does not process it.
2. **Given** the user touches a corner but doesn't swipe, **When** the touch ends, **Then** the cached DOWN event is replayed to the system so the app receives the tap.
3. **Given** the user touches a corner and swipes in the wrong direction, **When** the gesture is cancelled, **Then** the cached DOWN event is replayed to the system.

---

### User Story 2 - Successful Corner Swipe Consumes Event (Priority: P1)

As a user, when I successfully complete a corner swipe gesture, the cached DOWN event should be consumed (not replayed), so that the gesture action executes cleanly.

**Why this priority**: Successful gestures should consume the event, not replay it.

**Independent Test**: Perform a successful corner swipe, verify the event is consumed and the action fires.

**Acceptance Scenarios**:

1. **Given** a corner swipe rule is configured, **When** the user swipes from corner toward center and triggers the gesture, **Then** the cached DOWN event is consumed (not replayed) and the action fires.
2. **Given** a successful corner swipe, **When** the gesture completes, **Then** the system state is clean (no stale cached events).

---

### User Story 3 - Coexistence with Edge Gestures (Priority: P2)

As a user, when I touch outside the corner region, the corner detector should NOT consume the event, and the edge gesture engine should process it normally.

**Why this priority**: Corner and edge gestures must coexist without interfering with each other.

**Independent Test**: Touch an edge region (not corner), verify the edge engine processes it normally.

**Acceptance Scenarios**:

1. **Given** the user touches an edge region (not a corner), **When** the touch is processed, **Then** the corner detector does NOT consume the event and the edge engine handles it.
2. **Given** the user touches a corner that has no configured rule, **When** the touch is processed, **Then** the corner detector does NOT consume the event and the edge engine handles it.

---

### Edge Cases

- What happens when the user touches a corner that overlaps with an edge region?
  → The corner detector should consume the event if a corner rule is configured.
- What happens when the user multi-touches (one finger on corner, one on edge)?
  → Each touch is processed independently by the appropriate detector.
- What happens when the screen rotates while a corner gesture is in progress?
  → The gesture should be cancelled and the cached event replayed.

## Requirements *(mandatory)*

### Functional Requirements

#### Event Caching

- **FR-001**: System MUST cache the initial ACTION_DOWN event when a corner touch is detected.
- **FR-002**: System MUST consume the DOWN event (return true) when a corner is detected, preventing it from reaching the edge engine.

#### Event Replay

- **FR-003**: System MUST replay the cached DOWN event when the corner gesture is cancelled (wrong direction, finger lift without trigger, etc.).
- **FR-004**: System MUST consume (not replay) the cached DOWN event when the corner gesture fires successfully.
- **FR-005**: System MUST use the `InputInjectionBridge` to replay events back to the system input pipeline.

#### Coexistence

- **FR-006**: System MUST NOT consume the DOWN event when the touch is outside all corner regions.
- **FR-007**: System MUST NOT consume the DOWN event when no corner rule is configured for the touched corner.

### Key Entities

- **CornerSwipeToCenterDetector**: The detector to modify. Key state to add: `cachedDownEvent`, `InputInjectionBridge` dependency.
- **InputInjectionBridge**: Existing interface for injecting events into the system pipeline.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Corner touches consume the DOWN event and prevent edge engine processing.
- **SC-002**: Cancelled corner gestures replay the cached DOWN event — users never experience "lost touches" at corners.
- **SC-003**: Successful corner gestures consume the event cleanly without replay.
- **SC-004**: Edge gestures work normally when touching outside corner regions.

## Assumptions

- `CornerSwipeToCenterDetector` is already implemented (219 lines).
- `InputInjectionBridge` and `InputInjectionBridgeImpl` are already implemented.
- The `EdgeGestureEngine` already uses `InputInjectionBridge` for event replay — the same pattern can be reused.
- The integration point is `ShortXGestureMain` — pass the injection bridge to the corner detector.
