# Feature Specification: Restore Sub-Gesture Sequence Detection

**Feature Branch**: `feature/003-restore-sub-gesture-sequence`  
**Created**: 2026-04-30  
**Status**: Draft  
**Input**: User description: "基于新的手势代码，恢复重构后丢失的子手势功能，必须要分析现有子手势代码，理解需求后基于新代码接入。"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Multi-Step Edge Gesture Sequences (Priority: P1)

As a user, I want to perform a sequence of gestures starting from an edge swipe (e.g., swipe up from bottom, then swipe left) to trigger a specialized action that differs from a simple single-swipe gesture.

**Why this priority**: This is the core sub-gesture functionality. Without it, users can only bind one action per edge-gesture combination, losing the ability to create gesture shortcuts like "swipe up then left" for frequently used actions.

**Independent Test**: Configure a rule with child steps (e.g., SwipeFromBottom + SwipeLeft), perform the sequence, verify the rule fires.

**Acceptance Scenarios**:

1. **Given** a rule is configured with primary gesture "SwipeFromBottom" and child steps [SwipeLeft], **When** the user swipes up from the bottom edge and then swipes left, **Then** the system fires the edge gesture sequence event with the matched child steps.
2. **Given** a rule is configured with child steps [SwipeLeft, SwipeDown], **When** the user completes the full sequence, **Then** the system fires the event only after ALL child steps are matched.
3. **Given** a rule has child steps [SwipeLeft, SwipeDown], **When** the user swipes up from bottom then swipes left but does NOT swipe down, **Then** the system does NOT fire the sequence event.
4. **Given** multiple sequence rules match the same primary gesture, **When** the user performs different child step sequences, **Then** the system fires the correct rule based on the actual child steps performed.

---

### User Story 2 - Hover Child Step (Priority: P1)

As a user, I want to include a "hover" step in a gesture sequence, where I hold my finger still for a configured duration after the initial swipe, to trigger a different action than directional swipe steps.

**Why this priority**: Hover is a unique child step type that enables "swipe and hold" gestures, which are a common and intuitive interaction pattern.

**Independent Test**: Configure a rule with child steps [SwipeLeft, Hover], perform swipe-left then hold, verify the rule fires after the hover duration.

**Acceptance Scenarios**:

1. **Given** a rule has child steps [Hover], **When** the user swipes from an edge and holds the finger still for the configured hover duration (default 300ms), **Then** the system fires the sequence event.
2. **Given** a rule has child steps [Hover], **When** the user swipes from an edge but moves the finger outside the hover radius (default 24dp) before the duration elapses, **Then** the system cancels the sequence.
3. **Given** a rule has child steps [SwipeLeft, Hover], **When** the user swipes from an edge, then swipes left, then holds still, **Then** the system fires after the hover completes.

---

### User Story 3 - Visual Feedback During Sequence (Priority: P2)

As a user, I want to see visual hints during a multi-step gesture sequence showing what direction to swipe next, so that I can complete the sequence accurately.

**Why this priority**: Visual feedback significantly improves the usability of multi-step gestures, but the sequence works without it.

**Independent Test**: Start a sequence gesture, observe the hint showing the next required direction.

**Acceptance Scenarios**:

1. **Given** a sequence is being tracked, **When** the user is on step N, **Then** a visual hint shows the direction for step N+1.
2. **Given** a sequence has a Hover step, **When** the user reaches that step, **Then** a ring indicator (not arrow) is shown.
3. **Given** a sequence completes successfully, **When** all steps are matched, **Then** a success animation plays.
4. **Given** a sequence is cancelled (wrong direction, timeout), **When** the cancellation occurs, **Then** the hint fades out.

---

### User Story 4 - Haptic Feedback During Sequence (Priority: P3)

As a user, I want haptic feedback at key moments during a multi-step gesture (step ready, step complete, sequence success).

**Why this priority**: Haptic feedback enhances the experience but is optional.

**Independent Test**: Perform a sequence gesture with haptic enabled, verify vibration at appropriate moments.

**Acceptance Scenarios**:

1. **Given** haptic is enabled, **When** a child step reaches the ready threshold, **Then** a medium vibration occurs.
2. **Given** haptic is enabled, **When** the full sequence completes, **Then** a success vibration pattern occurs.

---

### Edge Cases

- What happens when the user lifts their finger during a directional child step before reaching the minimum distance?
  → The sequence is cancelled.
- What happens when the user changes direction during a child step (e.g., starts swiping left then reverses to right)?
  → The dot-product check rejects the reversed direction; if no candidate matches, the sequence continues waiting.
- What happens when multiple sequence rules have the same primary gesture but different child steps?
  → The detector maintains all candidates and narrows them as each step matches. The rule with the best-matching steps wins.
- What happens when the user performs a gesture that matches both a simple rule and a sequence rule?
  → Both fire: the simple rule fires immediately via `handleEdgeGestureEvent`, and the sequence detector starts tracking for the sequence rule.
- What happens when the screen rotates during a sequence?
  → The sequence should be cancelled (existing `cancelAndReplay()` behavior).

## Requirements *(mandatory)*

### Functional Requirements

#### Sequence Detection Integration

- **FR-001**: System MUST check for sequence rule candidates when an initial edge swipe is recognized.
- **FR-002**: System MUST start sequence tracking when matching candidates with `childStepsCount > 0` are found.
- **FR-003**: System MUST forward subsequent touch events (MOVE, UP, CANCEL) to the sequence detector when tracking is active.
- **FR-004**: System MUST NOT interfere with simple gesture rules when sequence tracking is active (both can coexist).
- **FR-005**: System MUST cancel sequence tracking when a new ACTION_DOWN occurs.

#### Child Step Matching

- **FR-006**: System MUST match directional child steps (SwipeLeft, SwipeRight, SwipeUp, SwipeDown) using dot-product against a configurable minimum distance and maximum angle deviation.
- **FR-007**: System MUST match Hover child steps when the finger stays within a configurable radius for a configurable duration.
- **FR-008**: System MUST advance the anchor point to the current finger position after each step matches.
- **FR-009**: System MUST shrink the candidate set after each step match, keeping only rules whose child steps match at the current index.
- **FR-010**: System MUST fire the sequence event only when ALL child steps of a candidate are matched.

#### Configuration

- **FR-011**: System MUST support configurable hover duration (default 300ms).
- **FR-012**: System MUST support configurable hover radius (default 24dp).
- **FR-013**: System MUST support configurable child swipe minimum distance (default 72dp).
- **FR-014**: System MUST support configurable child swipe maximum angle deviation (default 35 degrees).

#### Feedback Integration

- **FR-015**: System MUST report step progress hints to the hint controller during sequence tracking.
- **FR-016**: System MUST report sequence success to the hint and haptic controllers.
- **FR-017**: System MUST report sequence cancellation to the hint and haptic controllers.

### Key Entities

- **EdgeGestureSequenceCandidate**: Pairs a detected area with a rule that has child steps. Key attributes: area (DetectedArea), rule (EdgeGesture).
- **EdgeGestureStep**: A single step in a sequence. Key attributes: type (SwipeLeft/SwipeRight/SwipeUp/SwipeDown/Hover/SingleTap/DoubleTap/LongTap).
- **EdgeGestureSequenceDetector**: State machine that tracks multi-step sequences. Key state: trackedPointerId, primaryGesture, anchorX/Y, currentStepIndex, candidates, matchedSteps.
- **EdgeGestureSequenceHintReporter**: Bridges sequence detector hints to visual/haptic feedback.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can perform 2-step gesture sequences (e.g., swipe-up then swipe-left) and trigger the configured action.
- **SC-002**: Users can perform 3+ step gesture sequences with the same reliability as 2-step sequences.
- **SC-003**: Hover steps trigger correctly when the finger stays within radius for the configured duration.
- **SC-004**: Visual hints appear during sequence tracking showing the next required direction.
- **SC-005**: Sequence detection does not interfere with simple (single-swipe) gesture rules — both work simultaneously.
- **SC-006**: Sequence detection adds no perceptible latency to simple gesture recognition.

## Assumptions

- The `EdgeGestureSequenceDetector` class is already fully implemented and tested (442 lines, 7 unit tests passing).
- The `EdgeGestureSequenceHintReporter` class is already implemented.
- The `DetectorService.findMatchingEdgeGestureSequences()` and `fireEdgeGestureSequence()` methods are already implemented in `DetectorServiceImpl`.
- The protobuf definitions for `EdgeGestureStep`, `EdgeGestureStepType`, and `EdgeGesture.childSteps` already exist.
- The existing unit tests (`EdgeGestureSequenceDetectorTest.kt`) should continue to pass after integration.
- The integration point is `EdgeGestureEngine` — the new state machine that replaced `Detector.kt`.
