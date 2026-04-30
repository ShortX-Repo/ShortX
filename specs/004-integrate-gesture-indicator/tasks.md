# Tasks: Integrate Gesture Indicator for Simple Swipes

**Input**: Design documents from `/specs/004-integrate-gesture-indicator/`
**Prerequisites**: plan.md, spec.md, contracts/

**Tests**: Not explicitly requested. Existing tests should continue to pass.

**Organization**: This is a focused integration task. Only 2 files need modification.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., [US1], [US2])
- Include exact file paths in descriptions

## Path Conventions

All files in: `services/src/main/java/tornaco/apps/shortx/services/gesture/`

---

## Phase 1: Setup (Create Hint Detector)

**Purpose**: Create `EdgeGestureHintDetector` in `ShortXGestureMain` and pass to engine

- [x] T001 Create `EdgeGestureHintDetector` with `detectAreas` lambda using `EdgeRegionResolver` in `ShortXGestureMain.kt` and pass `hintDetector` to the engine in `services/src/main/java/tornaco/apps/shortx/services/gesture/ShortXGestureMain.kt`

---

## Phase 2: Foundational (Engine Integration)

**Purpose**: Forward events to hint detector and call callbacks

**⚠️ CRITICAL**: This phase MUST complete before any user story can be tested

- [x] T002 Add `hintDetector: EdgeGestureHintDetector` constructor parameter to `EdgeGestureEngine` in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T003 In `handleEdgeDownStart()`, forward event to `hintDetector.onTouchEvent(event)` before processing in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T004 In `handleMove()`, forward event to `hintDetector.onTouchEvent(event)` before processing in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T005 In `handleUp()`, forward event to `hintDetector.onTouchEvent(event)` before processing in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T006 In `handleCancel()`, forward event to `hintDetector.onTouchEvent(event)` before processing in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T007 In `startExecution()`, after firing simple rule, call `hintDetector.onEdgeGestureTriggered(areas, gesture, true)` in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T008 In `startExecution()`, when sequence candidates exist, call `hintDetector.onEdgeGestureSequenceStarted(areas, gesture)` in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`

**Checkpoint**: Visual and haptic feedback works for simple edge swipes. Sequence hints still work.

---

## Phase 3: User Story 1 - Visual Hint for Simple Edge Swipes (Priority: P1) 🎯 MVP

**Goal**: Touch edge region → hint dot appears. Swipe → hint shows progress. Reach threshold → ready state. Release → success animation.

**Independent Test**: Touch an enabled edge region, observe the hint dot appears. Swipe in the configured direction, observe the hint shows progress.

### Implementation for User Story 1

All implementation is in Phase 2 (T001-T008). No additional tasks needed.

**Checkpoint**: Visual hints work for simple edge swipes.

---

## Phase 4: User Story 2 - Haptic Feedback for Simple Edge Swipes (Priority: P1)

**Goal**: Haptic feedback at candidate, ready, and success stages.

**Independent Test**: Perform an edge swipe with haptic enabled, verify vibration at appropriate stages.

### Implementation for User Story 2

No additional implementation needed. The `EdgeGestureHintDetector` already calls `gestureHapticController.onGestureCandidate/Ready/Success()`. Wiring in T001 connects everything.

**Checkpoint**: Haptic feedback works for simple edge swipes.

---

## Phase 5: User Story 3 - Coexistence with Sequence Hints (Priority: P2)

**Goal**: Simple hint shows during initial swipe, sequence hint takes over if user continues with child steps.

**Independent Test**: Configure both simple and sequence rules. Swipe and observe the transition.

### Implementation for User Story 3

No additional implementation needed. T008 calls `onEdgeGestureSequenceStarted()` which cancels the simple hint before the sequence hint starts.

**Checkpoint**: Simple and sequence hints coexist without conflicts.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies — can start immediately
- **Phase 2 (Foundational)**: Depends on Phase 1 — BLOCKS all user stories
- **User Stories (Phase 3-5)**: All depend on Phase 2 completion. No additional implementation needed.

### Task Dependencies

- T001: No dependencies (creates hint detector)
- T002: Depends on T001 (needs hintDetector to exist)
- T003-T008: Depend on T002 (need hintDetector parameter in engine)

---

## Implementation Strategy

### MVP First (Phase 1 + Phase 2)

1. Complete T001: Create hint detector in ShortXGestureMain
2. Complete T002-T008: Wire hint detector into EdgeGestureEngine
3. **STOP and VALIDATE**: Test visual hints on device
4. Done — all user stories (US1-US3) are enabled by the same wiring

### Why So Few Tasks?

This feature is an **integration task**. The `EdgeGestureHintDetector` (340 lines) is already fully implemented. The only work is wiring it into the engine (~30 lines of new code in 2 files).

---

## Notes

- The `EdgeGestureHintDetector.onTouchEvent()` returns `false` always — it never consumes events. Forwarding before the engine's logic is safe.
- The `onEdgeGestureTriggered()` callback transitions the hint to Success state.
- The `onEdgeGestureSequenceStarted()` callback cancels the simple hint when a sequence begins.
