# Tasks: Integrate Corner Swipe-to-Center Detector

**Input**: Design documents from `/specs/005-integrate-corner-swipe/`
**Prerequisites**: plan.md, spec.md, contracts/

**Tests**: Not explicitly requested. Existing tests should continue to pass.

**Organization**: This is the simplest integration task — only 1 file needs modification.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., [US1], [US2])
- Include exact file paths in descriptions

## Path Conventions

All files in: `services/src/main/java/tornaco/apps/shortx/services/gesture/`

---

## Phase 1: Setup (Create Corner Detector)

**Purpose**: Create `CornerSwipeToCenterDetector` in `ShortXGestureMain` and forward events

- [x] T001 Create `CornerSwipeToCenterDetector` with `context`, `service`, `gestureHintController`, `gestureHapticController` in `ShortXGestureMain.kt` and forward events in `filterMotionEvent()` before the edge engine in `services/src/main/java/tornaco/apps/shortx/services/gesture/ShortXGestureMain.kt`

---

## Phase 2: User Story 1 - Corner Swipe Gesture Recognition (Priority: P1) 🎯 MVP

**Goal**: User swipes from screen corner toward center to trigger configured action

**Independent Test**: Configure a corner swipe rule, perform the gesture, verify the rule fires.

### Implementation for User Story 1

All implementation is in Phase 1 (T001). No additional tasks needed.

**Checkpoint**: Corner swipe gestures work. Edge gestures still work when touching outside corners.

---

## Phase 3: User Story 2 - Visual Feedback for Corner Swipes (Priority: P2)

**Goal**: Visual hint appears during corner swipe when hint is enabled

**Independent Test**: Touch a corner region, observe the hint appears. Swipe toward center, observe progress.

### Implementation for User Story 2

No additional implementation needed. The `CornerSwipeToCenterDetector` already calls `gestureHintController.onGestureProgress()` and `gestureHintController.onGestureSuccess()`. Wiring in T001 connects everything.

**Checkpoint**: Visual hints work for corner swipes.

---

## Phase 4: User Story 3 - Haptic Feedback for Corner Swipes (Priority: P3)

**Goal**: Haptic feedback at candidate, ready, and success stages

**Independent Test**: Perform a corner swipe with haptic enabled, verify vibration at appropriate stages.

### Implementation for User Story 3

No additional implementation needed. The `CornerSwipeToCenterDetector` already calls `gestureHapticController.onGestureCandidate/Ready/Success()`. Wiring in T001 connects everything.

**Checkpoint**: Haptic feedback works for corner swipes.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies — can start immediately
- **User Stories (Phase 2-4)**: All depend on Phase 1 completion. No additional implementation needed.

---

## Implementation Strategy

### MVP First (Phase 1)

1. Complete T001: Create corner detector and forward events
2. **STOP and VALIDATE**: Test corner swipe on device
3. Done — all user stories (US1-US3) are enabled by the same wiring

### Why Only 1 Task?

This is the simplest integration task. The `CornerSwipeToCenterDetector` (219 lines) and the backend pipeline are already fully implemented. Only `ShortXGestureMain.kt` needs modification (~15 lines).

---

## Notes

- The corner detector runs BEFORE the edge engine in `filterMotionEvent()` — corner takes priority over edge
- If the corner detector consumes the event, the edge engine is skipped
- If no corner is matched, the edge engine processes the event normally
- The `shouldDetectMotion` property already includes `enabledCornerSwipeToCenterFacts.isNotEmpty()` in its check
