# Tasks: Corner Swipe Event Caching and Replay

**Input**: Design documents from `/specs/006-corner-swipe-event-replay/`
**Prerequisites**: plan.md, spec.md, contracts/

**Tests**: Not explicitly requested.

**Organization**: This is a focused modification task — 2 files need changes.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., [US1], [US2])
- Include exact file paths in descriptions

## Path Conventions

All files in: `services/src/main/java/tornaco/apps/shortx/services/gesture/`

---

## Phase 1: Setup (Modify Corner Detector)

**Purpose**: Add event caching/replay to `CornerSwipeToCenterDetector`

- [x] T001 Add `injectionBridge: InputInjectionBridge` constructor parameter and `cachedDownEvent: MotionEvent?` field to `CornerSwipeToCenterDetector` in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/CornerSwipeToCenterDetector.kt`
- [x] T002 In `ACTION_DOWN`, cache event via `MotionEvent.obtain(event)` when corner is detected and return `true` (consume event) in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/CornerSwipeToCenterDetector.kt`
- [x] T003 In `ACTION_UP/CANCEL/POINTER_UP`, replay cached event via `injectionBridge.injectEvent(copy)` when not triggered in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/CornerSwipeToCenterDetector.kt`
- [x] T004 When gesture triggers successfully, recycle cached event (consume, don't replay) in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/CornerSwipeToCenterDetector.kt`
- [x] T005 Pass `injectionBridge` to `CornerSwipeToCenterDetector` constructor in `services/src/main/java/tornaco/apps/shortx/services/gesture/ShortXGestureMain.kt`

---

## Phase 2: User Story 1 - Corner Touch Consumes DOWN Event (Priority: P1) 🎯 MVP

**Goal**: Corner touches consume DOWN event, replay on cancel, consume on success.

**Independent Test**: Touch corner, don't swipe, release — verify app receives tap (event replayed).

### Implementation for User Story 1

All implementation is in Phase 1 (T001-T005). No additional tasks needed.

**Checkpoint**: Corner touches consume DOWN event. Cancelled gestures replay. Successful gestures consume.

---

## Phase 3: User Story 2 - Successful Corner Swipe Consumes Event (Priority: P1)

**Goal**: Successful corner swipe consumes event cleanly without replay.

**Independent Test**: Perform successful corner swipe, verify event consumed and action fires.

### Implementation for User Story 2

No additional implementation needed. T004 handles consuming on success.

**Checkpoint**: Successful gestures consume event cleanly.

---

## Phase 4: User Story 3 - Coexistence with Edge Gestures (Priority: P2)

**Goal**: Touches outside corner regions pass through to edge engine.

**Independent Test**: Touch edge region (not corner), verify edge engine processes normally.

### Implementation for User Story 3

No additional implementation needed. The detector already returns `false` when no corner is detected.

**Checkpoint**: Edge gestures work normally when touching outside corners.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies — can start immediately
- **User Stories (Phase 2-4)**: All depend on Phase 1 completion. No additional implementation needed.

### Task Dependencies

- T001: No dependencies (add constructor parameter and field)
- T002-T004: Depend on T001 (need cachedDownEvent field)
- T005: Depends on T001 (needs injectionBridge parameter)

---

## Implementation Strategy

### MVP First (Phase 1)

1. Complete T001-T005: Add event caching/replay to corner detector
2. **STOP and VALIDATE**: Test corner swipe with event replay on device
3. Done — all user stories (US1-US3) are enabled by the same changes

### Why So Few Tasks?

This is a focused modification. The `CornerSwipeToCenterDetector` (219 lines) already exists. Only ~30 lines of changes needed:
- Add constructor parameter + field
- Cache on DOWN, return true
- Replay on cancel
- Consume on success

---

## Notes

- Follows the same pattern as `EdgeGestureEngine` for event caching/replay
- The key change is returning `true` on DOWN when corner is detected (currently returns `false`)
- `InputInjectionBridge` is already implemented and used by `EdgeGestureEngine`
