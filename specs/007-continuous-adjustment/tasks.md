# Tasks: Continuous Adjustment Actions

**Input**: Design documents from `/specs/007-continuous-adjustment/`
**Prerequisites**: plan.md, spec.md, data-model.md, contracts/, research.md

**Tests**: Not explicitly requested.

**Organization**: This is a complex feature spanning proto definitions, gesture detection, system APIs, and UI.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., [US1], [US2])
- Include exact file paths in descriptions

---

## Phase 1: Setup (Proto Definitions)

**Purpose**: Define proto messages for continuous adjustment actions

- [x] T001 Add `ContinuousAdjustBrightness` message with fields: sensitivityScale (float), minValue (int32), maxValue (int32), plus standard action fields in `core-api/src/main/proto/shortx/Actions.proto`
- [x] T002 Add `ContinuousAdjustVolume` message with fields: streamType (int32), sensitivityScale (float), plus standard action fields in `core-api/src/main/proto/shortx/Actions.proto`

---

## Phase 2: Foundational (Gesture Detection Integration)

**Purpose**: Wire continuous action detection into the engine

**⚠️ CRITICAL**: This phase MUST complete before any user story can be tested

- [x] T003 Add `createContinuousActionHandler(actionType: String): ContinuousActionHandler?` to `GestureBridge` interface in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/GestureBridge.kt`
- [x] T004 Update `GestureBridgeImpl.createContinuousActionHandler()` to create real handlers with Context in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/GestureBridgeImpl.kt`
- [x] T005 Add `resolveContinuousActionType(action: Any?): String?` helper method to detect continuous action types in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`
- [x] T006 Modify `startExecution()` to check for continuous actions, create handler, call onStart, and return early in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`

**Checkpoint**: Engine detects continuous action types and creates handlers. One-shot actions still work.

---

## Phase 3: User Story 1 - Continuous Brightness Adjustment (Priority: P1) 🎯 MVP

**Goal**: User swipes from edge and drags to continuously adjust screen brightness

**Independent Test**: Configure continuous brightness action, swipe and drag, verify brightness changes.

### Implementation for User Story 1

- [x] T007 [US1] Implement real brightness control in `BrightnessContinuousHandler`: read current brightness on start, adjust on move, clamp to range in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/handlers/BrightnessContinuousHandler.kt`
- [x] T008 [US1] Implement auto-brightness handling: disable on start, restore on cancel in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/handlers/BrightnessContinuousHandler.kt`
- [x] T009 [US1] Pass Context, sensitivityScale, minValue, maxValue to handler constructor in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/GestureBridgeImpl.kt`

**Checkpoint**: Brightness changes in real-time during gesture. Cancel reverts to original value.

---

## Phase 4: User Story 2 - Continuous Volume Adjustment (Priority: P1)

**Goal**: User swipes from edge and drags to continuously adjust volume

**Independent Test**: Configure continuous volume action, swipe and drag, verify volume changes.

### Implementation for User Story 2

- [x] T010 [US2] Implement real volume control in `VolumeContinuousHandler`: read current volume on start, adjust on move, clamp to stream max in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/handlers/VolumeContinuousHandler.kt`
- [x] T011 [US2] Add stream type support (music, ring, alarm, notification) to handler in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/handlers/VolumeContinuousHandler.kt`
- [x] T012 [US2] Pass Context, streamType, sensitivityScale to handler constructor in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/GestureBridgeImpl.kt`

**Checkpoint**: Volume changes in real-time during gesture. Cancel reverts to original value.

---

## Phase 5: User Story 3 - Visual Feedback During Adjustment (Priority: P2)

**Goal**: Overlay shows current adjustment level during continuous adjustment

**Independent Test**: Perform continuous adjustment, observe overlay shows current level.

### Implementation for User Story 3

- [x] T013 [P] [US3] Create `ContinuousAdjustOverlayView` with progress bar, value text, and icon in `services-ui/src/main/java/tornaco/apps/shortx/ui/overlay/ContinuousAdjustOverlayView.kt`
- [x] T014 [US3] Create overlay controller to manage lifecycle (attach/detach/update) similar to `GestureHintController` in `services-ui/src/main/java/tornaco/apps/shortx/ui/overlay/ContinuousAdjustOverlayController.kt`
- [x] T015 [US3] Wire overlay to `BrightnessContinuousHandler` — show on start, update on move, dismiss on finish/cancel in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/handlers/BrightnessContinuousHandler.kt`
- [x] T016 [US3] Wire overlay to `VolumeContinuousHandler` — show on start, update on move, dismiss on finish/cancel in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/handlers/VolumeContinuousHandler.kt`

**Checkpoint**: Overlay appears during adjustment, updates in real-time, fades out on finish.

---

## Phase 6: User Story 4 - Configuration in Rule Editor (Priority: P2)

**Goal**: Users can configure continuous adjustment actions in the rule editor

**Independent Test**: Open rule editor, add continuous brightness action, configure, verify it works.

### Implementation for User Story 4

- [x] T017 [P] [US4] Create `ContinuousBrightnessSelectorDialog` with sensitivity and range settings in `ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/selector/ContinuousBrightnessSelectorDialog.kt`
- [x] T018 [P] [US4] Create `ContinuousVolumeSelectorDialog` with stream type and sensitivity settings in `ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/selector/ContinuousVolumeSelectorDialog.kt`
- [x] T019 [US4] Register continuous action types in the rule editor action selector in `ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/selector/SystemActions.kt`
- [x] T020 [US4] Add action evaluator for `ContinuousAdjustBrightness` in `services/src/main/java/tornaco/apps/shortx/services/rule/actor/impl/ContinuousAdjustBrightness.kt` and register it in `services/src/main/java/tornaco/apps/shortx/services/rule/actor/Actor.kt`
- [x] T021 [US4] Add action evaluator for `ContinuousAdjustVolume` in `services/src/main/java/tornaco/apps/shortx/services/rule/actor/impl/ContinuousAdjustVolume.kt` and register it in `services/src/main/java/tornaco/apps/shortx/services/rule/actor/Actor.kt`

**Checkpoint**: Users can configure continuous adjustment actions in the rule editor.

---

## Phase 7: User Story 5 - Cancel and Revert (Priority: P3)

**Goal**: Cancelled adjustments revert to original value

**Independent Test**: Start adjustment, cancel (multi-touch), verify value reverts.

### Implementation for User Story 5

- [x] T022 [US5] Verify `BrightnessContinuousHandler.onCancel()` reverts brightness and restores auto-brightness state in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/handlers/BrightnessContinuousHandler.kt`
- [x] T023 [US5] Verify `VolumeContinuousHandler.onCancel()` reverts volume in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/handlers/VolumeContinuousHandler.kt`
- [x] T024 [US5] Verify `EdgeGestureEngine.cancelAndReplay()` calls `continuousHandler.onCancel()` in `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/EdgeGestureEngine.kt`

**Checkpoint**: Cancelled adjustments revert to original value without residual changes.

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Cleanup and final integration

- [x] T025 Remove skeleton logging from `BrightnessContinuousHandler` and `VolumeContinuousHandler` — replace with real implementation
- [ ] T026 Verify one-shot actions (SetBrightness, SetVolume) still work after changes
- [ ] T027 Verify sequence gestures still work after engine changes
- [ ] T028 Verify edge hint detection still works after engine changes

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies — can start immediately
- **Phase 2 (Foundational)**: Depends on Phase 1 — BLOCKS all user stories
- **Phase 3 (US1 - Brightness)**: Depends on Phase 2
- **Phase 4 (US2 - Volume)**: Depends on Phase 2 — can run in parallel with US1
- **Phase 5 (US3 - Visual Feedback)**: Depends on US1 or US2 (needs working adjustment)
- **Phase 6 (US4 - Configuration)**: Depends on Phase 2 — can run in parallel with US1/US2
- **Phase 7 (US5 - Cancel/Revert)**: Depends on US1 or US2 (needs working adjustment)
- **Phase 8 (Polish)**: Depends on all user stories

### Task Dependencies

- T001-T002: No dependencies (proto definitions)
- T003-T006: Depend on T001-T002 (need proto types)
- T007-T009: Depend on T003-T006 (need engine integration)
- T010-T012: Depend on T003-T006 (need engine integration) — parallel with T007-T009
- T013-T016: Depend on T007 or T010 (need working adjustment)
- T017-T021: Depend on T001-T002 (need proto types) — parallel with US1/US2
- T022-T024: Depend on T007 or T010 (need working adjustment)

---

## Implementation Strategy

### MVP First (Phase 1 + Phase 2 + Phase 3)

1. Complete T001-T002: Define proto messages
2. Complete T003-T006: Wire engine integration
3. Complete T007-T009: Implement real brightness control
4. **STOP and VALIDATE**: Test brightness adjustment on device
5. Deploy/demo if ready

### Incremental Delivery

1. Proto + Engine + Brightness → MVP (brightness adjustment works)
2. Add Volume → Both brightness and volume work
3. Add Visual Feedback → Overlay shows during adjustment
4. Add Configuration UI → Users can set up rules
5. Polish → Cleanup and verification

---

## Notes

- T007-T009 (brightness) and T010-T012 (volume) can run in parallel
- T017-T021 (configuration UI) can run in parallel with US1/US2
- The overlay (T013-T016) is the most complex new component
- One-shot actions must continue working after engine changes
