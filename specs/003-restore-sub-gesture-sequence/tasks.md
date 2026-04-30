# Tasks: Restore Sub-Gesture Sequence Detection

**Input**: Design documents from `/specs/003-restore-sub-gesture-sequence/`
**Prerequisites**: plan.md, spec.md, contracts/

**Tests**: Not explicitly requested. Existing tests (EdgeGestureSequenceDetectorTest) should continue to pass.

**Organization**: This is a focused integration task. Only 2 files need modification. Tasks are organized by integration point.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., [US1], [US2])
- Include exact file paths in descriptions

## Path Conventions

All files in: `services/src/main/java/tornaco/apps/shortx/services/gesture/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Wire sequence detector and hint infrastructure into ShortXGestureMain

- [x] T001 Create `GestureHintController`, `GestureHapticController`, `EdgeGestureSequenceHintReporter`, and `EdgeGestureSequenceDetector` in `ShortXGestureMain.kt` and pass `sequenceDetector` to the engine in `services/src/main/java/tornaco/apps/shortx/services/gesture/ShortXGestureMain.kt`

---

## Phase 2: Foundational (Engine Integration)

**Purpose**: Add sequence tracking to EdgeGestureEngine

**⚠️ CRITICAL**: This phase MUST complete before any user story can be tested

- [x] T002 Add `sequenceDetector: EdgeGestureSequenceDetector` constructor parameter to `EdgeGestureEngine` in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T003 In `startExecution()`, after firing the simple rule, call `service.findMatchingEdgeGestureSequences()` and if candidates exist, call `sequenceDetector.startTracking()` in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T004 In `handleMove()`, add check: if `sequenceDetector.isTracking()`, forward event to `sequenceDetector.onTouchEvent()` and return true in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T005 In `handleUp()`, add check: if `sequenceDetector.isTracking()`, forward event to `sequenceDetector.onTouchEvent()` and return true (skip normal UP handling) in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T006 In `handleCancel()`, add check: if `sequenceDetector.isTracking()`, call `sequenceDetector.cancelTracking()` in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T007 In `handleEdgeDownStart()`, call `sequenceDetector.cancelTracking()` before processing new touch in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`

**Checkpoint**: Sequence detector is wired. Multi-step gestures should work. Simple gestures should still work.

---

## Phase 3: User Story 1 - Multi-Step Edge Gesture Sequences (Priority: P1) 🎯 MVP

**Goal**: User swipes from edge, then swipes in another direction to trigger a sequence-specific action

**Independent Test**: Configure a rule with child steps (e.g., SwipeFromBottom + SwipeLeft), perform the sequence, verify the rule fires. Also verify simple (non-sequence) rules still work.

### Implementation for User Story 1

All implementation is in Phase 2 (T002-T007). No additional tasks needed.

**Checkpoint**: Multi-step gestures work. Both simple and sequence rules fire for the same initial swipe.

---

## Phase 4: User Story 2 - Hover Child Step (Priority: P1)

**Goal**: User swipes from edge, then holds finger still for configured duration to trigger hover-based action

**Independent Test**: Configure a rule with child steps [Hover], swipe and hold, verify the rule fires after hover duration.

### Implementation for User Story 2

No additional implementation needed. The `EdgeGestureSequenceDetector` already handles hover steps via `tryCompleteHoverOnPointerRelease()`. The event forwarding in T005 (handleUp) ensures UP events reach the detector for hover completion.

**Checkpoint**: Hover steps work. Finger must stay within radius for configured duration.

---

## Phase 5: User Story 3 - Visual Feedback During Sequence (Priority: P2)

**Goal**: Visual hints show next required direction during sequence tracking

**Independent Test**: Start a sequence, observe hint showing next direction.

### Implementation for User Story 3

No additional implementation needed. The `EdgeGestureSequenceHintReporter` (already implemented) bridges sequence detector hints to `GestureHintController`. Wiring in T001 connects everything.

**Checkpoint**: Visual hints appear during sequence tracking. Ring indicator for Hover, arrow for directional steps.

---

## Phase 6: User Story 4 - Haptic Feedback During Sequence (Priority: P3)

**Goal**: Haptic feedback at key moments during sequence

**Independent Test**: Perform sequence with haptic enabled, verify vibration at appropriate moments.

### Implementation for User Story 4

No additional implementation needed. The `EdgeGestureSequenceHintReporter` already calls `gestureHapticController.onGestureCandidate/Ready/Success()`. Wiring in T001 connects everything.

**Checkpoint**: Haptic feedback works. Escalating vibration at candidate/ready/success stages.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies — can start immediately
- **Phase 2 (Foundational)**: Depends on Phase 1 — BLOCKS all user stories
- **User Stories (Phase 3-6)**: All depend on Phase 2 completion. No additional implementation needed — the wiring in Phase 2 enables all user stories.
- **No Polish phase needed** — this is a focused integration task.

### Task Dependencies

- T001: No dependencies (creates infrastructure)
- T002: Depends on T001 (needs sequenceDetector to exist)
- T003-T007: Depend on T002 (need sequenceDetector parameter in engine)

### Parallel Opportunities

- T003-T007 can all be implemented together since they modify different methods in the same file

---

## Implementation Strategy

### MVP First (Phase 1 + Phase 2)

1. Complete T001: Wire infrastructure in ShortXGestureMain
2. Complete T002-T007: Add sequence tracking to EdgeGestureEngine
3. **STOP and VALIDATE**: Test multi-step gestures on device
4. Done — all user stories (US1-US4) are enabled by the same wiring

### Why So Few Tasks?

This feature is an **integration task**, not a rewrite. The components are already implemented:
- `EdgeGestureSequenceDetector` (578 lines, 7 unit tests)
- `EdgeGestureSequenceHintReporter` (60 lines)
- `DetectorService.findMatchingEdgeGestureSequences()` (implemented)
- `DetectorServiceImpl.fireEdgeGestureSequence()` (implemented)

The only work is wiring them into `EdgeGestureEngine` (~50 lines of new code in 2 files).

---

## Notes

- All existing unit tests (`EdgeGestureSequenceDetectorTest.kt`) should continue to pass — no changes to the detector itself
- The key insight is that `service.handleEdgeGestureEvent()` already excludes rules with `childStepsCount > 0`, so simple and sequence rules naturally coexist
- T001 is the most critical task — it creates all the infrastructure
- T003 is the second most critical — it starts sequence tracking when candidates are found
