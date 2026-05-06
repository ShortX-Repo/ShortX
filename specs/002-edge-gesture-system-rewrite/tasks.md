# Tasks: Edge Gesture System Rewrite

**Input**: Design documents from `/specs/002-edge-gesture-system-rewrite/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Tests**: Not explicitly requested in spec. Unit tests included for core state machine only.

**Organization**: Tasks grouped by user story. US1-US4 are P1 (core), US5-US6 are P2 (feedback), US7-US8 are P3 (polish).

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

All new files go in: `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Create enums, interfaces, and utility classes that all user stories depend on

- [x] T001 Create `Direction` enum (UNKNOWN, LEFT, RIGHT, UP, DOWN) and `GestureType` enum (Tap, DoubleTap, LongPress, SwipeLeft, SwipeRight, SwipeUp, SwipeDown) in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/GestureEnums.kt`
- [x] T002 Create `GestureEngineListener` interface with callbacks (onEdgeTouchDown, onDirectionResolved, onSwipeProgress, onGestureTriggered, onGestureCancelled) in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/GestureEngineListener.kt`
- [x] T003 Create `ContinuousActionHandler` interface with lifecycle methods (onStart, onMove, onFinish, onCancel) in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/ContinuousActionHandler.kt`
- [x] T004 Create `InputInjectionBridge` interface (injectEvent, setFilterEnabled) in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/InputInjectionBridge.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core resolvers and state machine that ALL user stories depend on

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T005 [P] Implement `EdgeRegionResolver` with 16-region hit testing and parent-edge fallback in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeRegionResolver.kt`
- [x] T006 [P] Implement `GestureDirectionResolver` with dominant-axis logic in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/GestureDirectionResolver.kt`
- [x] T007 [P] Implement `GestureTimerController` with long-press and double-tap timers in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/GestureTimerController.kt`
- [x] T008 [P] Implement `GestureActionResolver` with 16×7 action table lookup and parent-edge fallback in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/GestureActionResolver.kt`
- [x] T009 [P] Implement `InputInjectionBridge` wrapping IMSFunc injection paths in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/InputInjectionBridgeImpl.kt`
- [x] T010 Implement `EdgeGestureEngine` core state machine (handleTouchSequence, handleEdgeDownStart, handleMoveBeforeTrigger, finalizeGestureOnFingerUp, handleSecondTapForDoubleTap, event caching/replay) in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt` (depends on T005-T009)
- [x] T011 Wire `EdgeGestureEngine` into `ShortXGestureMain.kt` replacing `Detector` in `services/src/main/java/tornaco/apps/shortx/services/gesture/ShortXGestureMain.kt`
- [x] T012 Wire `EdgeGestureEngine` into `DetectorServiceImpl.kt` using `GestureActionResolver` in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/DetectorServiceImpl.kt`

**Checkpoint**: Foundation ready — user story implementation can now begin

---

## Phase 3: User Story 1 - Edge Swipe Gesture Recognition (Priority: P1) 🎯 MVP

**Goal**: User swipes from screen edge to trigger configured action

**Independent Test**: Swipe from any edge, verify configured action executes. Touch outside edge, verify pass-through.

### Implementation for User Story 1

- [x] T013 [US1] Implement ACTION_DOWN handling in `EdgeGestureEngine.handleEdgeDownStart()` — reset state, detect region via `EdgeRegionResolver`, start long-press timer in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T014 [US1] Implement ACTION_MOVE handling in `EdgeGestureEngine.handleMoveBeforeTrigger()` — resolve direction via `GestureDirectionResolver`, check action via `GestureActionResolver`, start execution if swipe threshold met in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T015 [US1] Implement ACTION_UP swipe finalization in `EdgeGestureEngine.finalizeGestureOnFingerUp()` — validate final swipe direction and distance, execute confirmed swipe action in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T016 [US1] Implement multi-pointer cancel handling (ACTION_POINTER_DOWN/UP) in `EdgeGestureEngine.handleTouchSequence()` in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`

**Checkpoint**: Basic edge swipe detection works — swipe from left/right/top/bottom triggers configured action

---

## Phase 4: User Story 2 - Edge Region Subdivision (Priority: P1)

**Goal**: Each edge divided into 3 segments + "all" variant = 16 regions total

**Independent Test**: Configure different actions for left-top vs left-center vs left-bottom, verify each triggers independently. Verify parent-edge fallback when segment has no action.

### Implementation for User Story 2

- [x] T017 [US2] Verify `EdgeRegionResolver.resolve()` correctly maps coordinates to 16 regions (0-15) matching protobuf `Edge` enum in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeRegionResolver.kt`
- [x] T018 [US2] Verify `EdgeRegionResolver.getParentEdgeRegion()` correctly returns parent edge (LeftTop→LeftAll, RightCenter→RightAll, etc.) in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeRegionResolver.kt`
- [x] T019 [US2] Verify `GestureActionResolver.resolve()` implements parent-edge fallback (check细分 region first, then parent) in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/GestureActionResolver.kt`
- [x] T020 [US2] Update `Area.kt` to expose edge region rectangles for all 16 regions with configurable width/height in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/Area.kt`

**Checkpoint**: All 16 edge regions independently configurable with parent-edge fallback

---

## Phase 5: User Story 3 - Multiple Gesture Types per Edge Region (Priority: P1)

**Goal**: Each region supports 7 gesture slots: tap, double-tap, long-press, swipe left/right/up/down

**Independent Test**: Configure tap and swipe-up on same region, verify each triggers independently.

### Implementation for User Story 3

- [x] T021 [US3] Implement tap detection in `EdgeGestureEngine.finalizeGestureOnFingerUp()` — detect release before long-press timeout with no significant movement in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T022 [US3] Implement long-press detection in `EdgeGestureEngine` — handle timer expiry callback, execute long-press action in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T023 [US3] Implement double-tap detection in `EdgeGestureEngine.handleSecondTapForDoubleTap()` — track first tap UP, start double-tap timer, handle second tap within timeout in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T024 [US3] Implement double-tap timeout handling — treat first tap as single-tap action when timeout expires in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T025 [US3] Verify `GestureType` enum maps correctly to action table slots (Tap=0, DoubleTap=1, LongPress=2, SwipeLeft=3, SwipeRight=4, SwipeUp=5, SwipeDown=6) in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/GestureEnums.kt`

**Checkpoint**: All 7 gesture types work per region — tap, double-tap, long-press, 4 swipe directions

---

## Phase 6: User Story 4 - Event Caching and Replay (Priority: P1)

**Goal**: Invalid gestures replay cached DOWN event; valid gestures consume it

**Independent Test**: Touch edge, don't swipe, release. Verify app receives tap. Swipe from edge with no action configured, verify pass-through.

### Implementation for User Story 4

- [x] T026 [US4] Implement event caching in `EdgeGestureEngine.handleEdgeDownStart()` — deep-copy ACTION_DOWN via `MotionEvent.obtain(event)` in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T027 [US4] Implement event replay on cancellation in `EdgeGestureEngine` — inject cached event via `InputInjectionBridge.injectEvent()` when gesture is invalid in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T028 [US4] Implement event consumption on execution — recycle cached event (don't replay) when gesture enters execution phase in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T029 [US4] Implement `EdgeGestureEngine.cancelAndReplay()` — external cancel trigger (rotation, keyboard show) replays cached event in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T030 [US4] Implement device ID locking — lock `deviceId` on DOWN, reject events from different devices during sequence in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`

**Checkpoint**: Event caching/replay works — no "lost touches" at screen edges

---

## Phase 7: User Story 5 - Continuous Adjustment Actions (Priority: P2)

**Goal**: Edge swipes for continuous adjustments (brightness, volume) with drag-to-adjust

**Independent Test**: Configure brightness on edge, swipe and drag, verify continuous adjustment.

### Implementation for User Story 5

- [x] T031 [US5] Implement continuous action detection in `EdgeGestureEngine.startResolvedGestureAction()` — detect `ContinuousAdjustAction` type, create handler, enter continuous mode in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T032 [US5] Implement continuous action lifecycle forwarding — forward MOVE/UP/CANCEL events to `ContinuousActionHandler` when in execution phase in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T033 [P] [US5] Create `BrightnessContinuousHandler` implementing `ContinuousActionHandler` in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/handlers/BrightnessContinuousHandler.kt`
- [x] T034 [P] [US5] Create `VolumeContinuousHandler` implementing `ContinuousActionHandler` in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/handlers/VolumeContinuousHandler.kt`
- [x] T035 [US5] Add `ContinuousActionHandler` factory in `DetectorServiceImpl` to create handlers by action type in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/DetectorServiceImpl.kt`

**Checkpoint**: Continuous adjustment actions work — brightness/volume changes smoothly during drag

---

## Phase 8: User Story 6 - Visual Feedback During Gestures (Priority: P2)

**Goal**: Visual hint overlay shows gesture progress (candidate → showing → ready → success)

**Independent Test**: Start edge swipe, observe hint appears and progresses through states.

### Implementation for User Story 6

- [x] T036 [US6] Adapt `EdgeGestureHintDetector` to receive events from `EdgeGestureEngine` via `GestureEngineListener` in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureHintDetector.kt`
- [x] T037 [US6] Wire `GestureEngineListener.onEdgeTouchDown()` to trigger candidate hint state in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureHintDetector.kt`
- [x] T038 [US6] Wire `GestureEngineListener.onSwipeProgress()` to trigger showing/ready hint states in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureHintDetector.kt`
- [x] T039 [US6] Wire `GestureEngineListener.onGestureTriggered()` to trigger success hint state in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureHintDetector.kt`
- [x] T040 [US6] Wire `GestureEngineListener.onGestureCancelled()` to cancel hint display in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureHintDetector.kt`

**Checkpoint**: Visual feedback works — hint appears on edge touch, progresses during swipe, animates on success

---

## Phase 9: User Story 7 - Haptic Feedback During Gestures (Priority: P3)

**Goal**: Haptic feedback at candidate, ready, and success stages

**Independent Test**: Perform edge gesture with haptic enabled, verify vibration at each stage.

### Implementation for User Story 7

- [x] T041 [US7] Wire `GestureHapticController` to `GestureEngineListener.onEdgeTouchDown()` for candidate haptic in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureHintDetector.kt`
- [x] T042 [US7] Wire `GestureHapticController` to swipe threshold reached for ready haptic in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureHintDetector.kt`
- [x] T043 [US7] Wire `GestureHapticController` to `GestureEngineListener.onGestureTriggered()` for success haptic in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureHintDetector.kt`

**Checkpoint**: Haptic feedback works — escalating vibration at candidate/ready/success stages

---

## Phase 10: User Story 8 - Gesture Recording and Playback (Priority: P3)

**Goal**: Record and play back gesture sequences

**Independent Test**: Record gesture, play back, verify events injected correctly.

### Implementation for User Story 8

- [x] T044 [US8] Verify `GestureRecorder` works with new engine — recording captures events before engine in `services/src/main/java/tornaco/apps/shortx/services/gesture/GestureRecorder.kt`
- [x] T045 [US8] Verify `GesturePlayer` works with new engine — playback injects events directly via IMSFunc in `services/src/main/java/tornaco/apps/shortx/services/gesture/GesturePlayer.kt`
- [x] T046 [US8] Verify `GestureService.filterInput()` routes events to recorder before engine in `services/src/main/java/tornaco/apps/shortx/services/gesture/GestureService.kt`

**Checkpoint**: Recording/playback works — no changes needed, preserved as-is

---

## Phase 11: Polish & Cross-Cutting Concerns

**Purpose**: Cleanup legacy code and final integration

- [x] T047 Remove `Detector.kt` (replaced by `EdgeGestureEngine.kt`) in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/Detector.kt`
- [x] T048 Remove `SystemGesturesPointerEventListener.java` (functionality absorbed by engine) in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/SystemGesturesPointerEventListener.java`
- [x] T049 Remove `PointerEventDispatcher.java` (already non-functional) in `services/src/main/java/tornaco/apps/shortx/services/gesture/PointerEventDispatcher.java`
- [x] T050 Preserve `EdgeGestureSequenceDetector.kt` and verify it works with new engine in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureSequenceDetector.kt`
- [x] T051 Preserve `CornerSwipeToCenterDetector.kt` and verify it works with new engine in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/CornerSwipeToCenterDetector.kt`
- [x] T052 Preserve `EdgeGestureSequenceHintReporter.kt` in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureSequenceHintReporter.kt`
- [x] T053 Run quickstart.md validation — verify all steps work end-to-end

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion — BLOCKS all user stories
- **User Stories (Phase 3-10)**: All depend on Foundational phase completion
  - US1-US4 (P1): Must complete before US5-US8
  - US5-US6 (P2): Can start after US1-US4 complete
  - US7-US8 (P3): Can start after US5-US6 complete
- **Polish (Phase 11)**: Depends on all user stories being complete

### User Story Dependencies

- **US1 (Edge Swipe)**: Depends on Foundational (Phase 2) — No other story dependencies
- **US2 (Region Subdivision)**: Depends on US1 — extends swipe detection with 16 regions
- **US3 (Gesture Types)**: Depends on US1 — adds tap/double-tap/long-press to swipe framework
- **US4 (Event Caching)**: Depends on US1 — adds caching/replay to swipe detection
- **US5 (Continuous Actions)**: Depends on US1-US4 — extends action execution model
- **US6 (Visual Feedback)**: Depends on US1-US4 — hooks into engine events
- **US7 (Haptic Feedback)**: Depends on US6 — extends visual feedback with haptics
- **US8 (Recording)**: Independent — preserved as-is, just verification

### Within Each User Story

- Resolvers before engine
- Engine before wiring
- Wiring before integration
- Story complete before moving to next priority

### Parallel Opportunities

- Phase 1 tasks T001-T004 can all run in parallel
- Phase 2 tasks T005-T009 can all run in parallel
- US5 tasks T033-T034 can run in parallel (different handler files)
- US7 tasks T041-T043 can run in parallel
- US8 tasks T044-T046 can run in parallel

---

## Parallel Example: Phase 2 (Foundational)

```bash
# Launch all resolvers in parallel:
Task: "Implement EdgeRegionResolver in services/.../EdgeRegionResolver.kt"
Task: "Implement GestureDirectionResolver in services/.../GestureDirectionResolver.kt"
Task: "Implement GestureTimerController in services/.../GestureTimerController.kt"
Task: "Implement GestureActionResolver in services/.../GestureActionResolver.kt"
Task: "Implement InputInjectionBridge in services/.../InputInjectionBridgeImpl.kt"

# After all complete, implement engine:
Task: "Implement EdgeGestureEngine in services/.../EdgeGestureEngine.kt"
```

---

## Implementation Strategy

### MVP First (User Stories 1-4 Only)

1. Complete Phase 1: Setup (T001-T004)
2. Complete Phase 2: Foundational (T005-T012)
3. Complete Phase 3: US1 — Edge Swipe (T013-T016)
4. Complete Phase 4: US2 — Region Subdivision (T017-T020)
5. Complete Phase 5: US3 — Gesture Types (T021-T025)
6. Complete Phase 6: US4 — Event Caching (T026-T030)
7. **STOP and VALIDATE**: Test all P1 user stories independently
8. Deploy/demo if ready

### Incremental Delivery

1. Setup + Foundational → Foundation ready
2. Add US1-US4 → Test independently → Deploy/Demo (MVP!)
3. Add US5 (Continuous Actions) → Test independently → Deploy/Demo
4. Add US6 (Visual Feedback) → Test independently → Deploy/Demo
5. Add US7 (Haptic) + US8 (Recording) → Test independently → Deploy/Demo
6. Polish → Remove legacy code → Final validation

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- US8 (Recording) requires no code changes — just verification that existing code works with new engine
