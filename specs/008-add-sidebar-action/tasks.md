# Tasks: Sidebar Action

**Input**: Design documents from `/Users/tornaco/Documents/workspace/ShortX/specs/008-add-sidebar-action/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/interfaces.md, quickstart.md

**Tests**: No TDD or automated tests were explicitly requested. Tasks include compile validation and manual quickstart validation only.

**Organization**: Tasks are grouped by user story so each story can be implemented and tested independently after the shared model/runtime foundation is complete.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel because it touches a different file and does not depend on incomplete tasks.
- **[Story]**: User story label for story phases only.
- Every task includes an exact repository file path.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Confirm the target feature surface and prepare shared feature files without implementing user-story behavior.

- [X] T001 Review existing action registration, conversion, selector, actor, and sidebar overlay patterns in `/Users/tornaco/Documents/workspace/ShortX/core-api/src/main/proto/shortx/Actions.proto`, `/Users/tornaco/Documents/workspace/ShortX/ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/action/model/Action.kt`, `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/selector/UiActions.kt`, `/Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/rule/actor/Actor.kt`, and `/Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/overlay/OverlayService.kt`
- [X] T002 [P] Create the new service-owned sidebar UI package directory placeholder by adding `/Users/tornaco/Documents/workspace/ShortX/services-ui/src/main/java/tornaco/apps/shortx/ui/actionsidebar/ActionSidebarModels.kt`
- [X] T003 [P] Create the new runtime sidebar package directory placeholder by adding `/Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/overlay/actionsidebar/ActionSidebarSession.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Define the new independent action model and register it across shared conversion, metadata, actor dispatch, and i18n paths before user-story implementation.

**CRITICAL**: No user story work can begin until this phase is complete.

- [X] T004 Add new `ShowActionSidebar`, `ActionSidebarButton`, and `ActionSidebarDismissBehavior` protobuf messages with common action fields in `/Users/tornaco/Documents/workspace/ShortX/core-api/src/main/proto/shortx/Actions.proto`
- [X] T005 Regenerate core-api AIDL/protobuf sources with `./gradlew :core-api:generateAidlFiles` and `./gradlew :core-api:generateProto` from `/Users/tornaco/Documents/workspace/ShortX`
- [X] T006 Add `Action.ShowActionSidebar` and `ActionSidebarButton` UI model classes in `/Users/tornaco/Documents/workspace/ShortX/ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/action/model/Action.kt`
- [X] T007 Add proto-to-UI conversion for `ShowActionSidebar` in `/Users/tornaco/Documents/workspace/ShortX/ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/action/model/ProtoAnyToAction.kt`
- [X] T008 Add UI-to-proto conversion for `Action.ShowActionSidebar` in `/Users/tornaco/Documents/workspace/ShortX/ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/action/model/ActionToProtoAction.kt`
- [X] T009 Register `Action.ShowActionSidebar::class` in `/Users/tornaco/Documents/workspace/ShortX/ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/action/model/AllActionClasses.kt`
- [X] T010 [P] Add action icon mapping for `Action.ShowActionSidebar` in `/Users/tornaco/Documents/workspace/ShortX/ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/action/mapper/ActionIcons.kt`
- [X] T011 [P] Add action color mapping for `Action.ShowActionSidebar` in `/Users/tornaco/Documents/workspace/ShortX/ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/action/mapper/ActionColors.kt`
- [X] T012 Add i18n display mapping for `Action.ShowActionSidebar` in `/Users/tornaco/Documents/workspace/ShortX/ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/action/mapper/I18NMapper.kt`
- [X] T013 Add English sidebar action translation keys in `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/assets/i18n/en-US/i18n-en-US.json`
- [X] T014 [P] Add Simplified Chinese sidebar action translation keys in `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/assets/i18n/zh-CN/i18n-zh-CN.json`
- [X] T015 [P] Add Traditional Chinese sidebar action translation keys in `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/assets/i18n/zh-TW/i18n-zh-TW.json`
- [X] T016 Add `ShowActionSidebar` actor enum registration in `/Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/rule/actor/Actor.kt`
- [X] T017 Add `ShowActionSidebar` action subscription metadata if required by existing action filtering in `/Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/rule/SubscribeOnlyModels.kt`

**Checkpoint**: The new action type exists as a shared persisted model, can be converted by UI model code, has labels/icons/colors, and has runtime actor registration points.

---

## Phase 3: User Story 1 - Show a configurable sidebar (Priority: P1) MVP

**Goal**: A valid `ShowActionSidebar` action displays one compact phone-vendor-style floating sidebar and can be dismissed without unintended button actions.

**Independent Test**: Create a rule or direct action that runs `ShowActionSidebar` with at least one button and verify a compact sidebar appears, remains responsive, and dismisses by outside tap or close behavior.

### Implementation for User Story 1

- [X] T018 [P] [US1] Implement runtime session state fields for one visible action-sidebar instance in `/Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/overlay/actionsidebar/ActionSidebarSession.kt`
- [X] T019 [P] [US1] Implement sidebar button/view data models for rendering icons, labels, order, and empty-state text in `/Users/tornaco/Documents/workspace/ShortX/services-ui/src/main/java/tornaco/apps/shortx/ui/actionsidebar/ActionSidebarModels.kt`
- [X] T020 [P] [US1] Implement compact side-panel Compose content with scrollable vertical buttons, label truncation, and close affordance in `/Users/tornaco/Documents/workspace/ShortX/services-ui/src/main/java/tornaco/apps/shortx/ui/actionsidebar/ActionSidebarPanel.kt`
- [X] T021 [US1] Implement `ActionSidebarDialogView` as a service-owned Compose dialog wrapper in `/Users/tornaco/Documents/workspace/ShortX/services-ui/src/main/java/tornaco/apps/shortx/ui/actionsidebar/ActionSidebarDialogView.kt`
- [X] T022 [US1] Implement `ActionSidebarManager` to show, animate, dismiss, and prevent duplicate visible sessions for the same action id in `/Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/overlay/actionsidebar/ActionSidebarManager.kt`
- [X] T023 [US1] Expose `ActionSidebarManager` from the overlay service locator in `/Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/overlay/OverlayService.kt`
- [X] T024 [US1] Implement the `ShowActionSidebar` actor to validate buttons and call `ActionSidebarManager` on the main thread in `/Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/rule/actor/impl/ShowActionSidebar.kt`
- [X] T025 [US1] Add runtime failure feedback for no buttons or missing display capability using existing service UI/i18n conventions in `/Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/overlay/actionsidebar/ActionSidebarManager.kt`
- [X] T026 [US1] Add the new sidebar action to the normal action selector list and launch path in `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/selector/UiActions.kt`

**Checkpoint**: User Story 1 is fully functional and independently testable as an MVP display action.

---

## Phase 4: User Story 2 - Configure sidebar buttons (Priority: P2)

**Goal**: Users can configure, save, reopen, and reorder sidebar buttons with icon, text, tap actions, and long-press actions.

**Independent Test**: Configure several buttons with distinct icons and labels, save the action, reopen the editor, verify all fields and order persist, then run the action and verify the displayed sidebar matches the saved configuration.

### Implementation for User Story 2

- [X] T027 [P] [US2] Implement `ShowActionSidebarSelectorState`, `ActionSidebarButtonEditorState`, and conversion to `Action.ShowActionSidebar` in `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/selector/actionsidebar/ShowActionSidebarSelectorViewModel.kt`
- [X] T028 [US2] Implement add, remove, icon update, label update, tap action update, long-press action update, and init-from-existing behavior in `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/selector/actionsidebar/ShowActionSidebarSelectorViewModel.kt`
- [X] T029 [P] [US2] Implement reorder dialog/state for sidebar buttons in `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/selector/actionsidebar/ActionSidebarButtonSortDialog.kt`
- [X] T030 [US2] Implement `ShowActionSidebarSelectorActivity` and Compose editor content for button icon, text, tap action list, long-press action list, and validation feedback in `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/selector/actionsidebar/ShowActionSidebarSelector.kt`
- [X] T031 [US2] Wire `ShowActionSidebarSelectorActivity` launch and result handling into the action selector flow in `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/selector/UiActions.kt`
- [X] T032 [US2] Block save or show clear validation when no buttons exist or a button has neither icon nor text in `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/selector/actionsidebar/ShowActionSidebarSelector.kt`
- [X] T033 [US2] Add action error checking for invalid sidebar button configuration in `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/ActionErrorChecker.kt`
- [X] T034 [US2] Verify export/import and edit persistence use the new protobuf fields without custom migration code in `/Users/tornaco/Documents/workspace/ShortX/ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/action/model/ActionToProtoAction.kt` and `/Users/tornaco/Documents/workspace/ShortX/ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/action/model/ProtoAnyToAction.kt`

**Checkpoint**: User Story 2 is independently testable through edit, save, reopen, reorder, and display verification.

---

## Phase 5: User Story 3 - Execute button tap and long-press actions (Priority: P3)

**Goal**: Each visible sidebar button runs only its configured tap actions on tap and only its configured long-press actions on long press.

**Independent Test**: Assign visibly different actions to tap and long-press for one button, then verify tap and long-press each run only the intended action; verify an unconfigured gesture does nothing and leaves the sidebar stable.

### Implementation for User Story 3

- [X] T035 [P] [US3] Add tap and long-press callback plumbing to the sidebar UI model in `/Users/tornaco/Documents/workspace/ShortX/services-ui/src/main/java/tornaco/apps/shortx/ui/actionsidebar/ActionSidebarModels.kt`
- [X] T036 [US3] Implement distinct click and long-click gesture handling without duplicate tap execution in `/Users/tornaco/Documents/workspace/ShortX/services-ui/src/main/java/tornaco/apps/shortx/ui/actionsidebar/ActionSidebarPanel.kt`
- [X] T037 [US3] Execute button tap action lists through existing `RuleService.executeActions` semantics in `/Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/overlay/actionsidebar/ActionSidebarManager.kt`
- [X] T038 [US3] Execute button long-press action lists through existing `RuleService.executeActions` semantics in `/Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/overlay/actionsidebar/ActionSidebarManager.kt`
- [X] T039 [US3] Handle empty gesture action lists as no-op without dismissing or crashing the sidebar in `/Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/overlay/actionsidebar/ActionSidebarManager.kt`
- [X] T040 [US3] Add user-facing failure feedback for invalid nested action execution while keeping the sidebar session stable in `/Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/overlay/actionsidebar/ActionSidebarManager.kt`

**Checkpoint**: All user stories are independently functional and satisfy the tap/long-press gesture contract.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final validation, cleanup, and documentation across the full feature.

- [X] T041 Run `./gradlew :core-api:compileKotlin` from `/Users/tornaco/Documents/workspace/ShortX` and fix sidebar action model compile issues in `/Users/tornaco/Documents/workspace/ShortX/core-api/src/main/proto/shortx/Actions.proto`
- [X] T042 Run `./gradlew :services:compileKotlin` from `/Users/tornaco/Documents/workspace/ShortX` and fix runtime actor/manager compile issues in `/Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/overlay/actionsidebar/ActionSidebarManager.kt`
- [X] T043 Run `./gradlew :services-ui:compileKotlin` from `/Users/tornaco/Documents/workspace/ShortX` and fix sidebar Compose compile issues in `/Users/tornaco/Documents/workspace/ShortX/services-ui/src/main/java/tornaco/apps/shortx/ui/actionsidebar/ActionSidebarPanel.kt`
- [X] T044 Run `./gradlew :ui:compileRowDebugKotlin` from `/Users/tornaco/Documents/workspace/ShortX` and fix selector/editor compile issues in `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/selector/actionsidebar/ShowActionSidebarSelector.kt`
- [X] T045 [P] Validate i18n key coverage for `en-US`, `zh-CN`, and `zh-TW` sidebar strings in `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/assets/i18n/en-US/i18n-en-US.json`, `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/assets/i18n/zh-CN/i18n-zh-CN.json`, and `/Users/tornaco/Documents/workspace/ShortX/ui/src/main/assets/i18n/zh-TW/i18n-zh-TW.json`
- [X] T046 [P] Document manual validation results for quickstart scenarios in `/Users/tornaco/Documents/workspace/ShortX/specs/008-add-sidebar-action/quickstart.md`
- [ ] T047 Manually validate quickstart scenarios for create/display, tap/long-press, edit persistence, repeated trigger, and unconfigured gesture behavior using `/Users/tornaco/Documents/workspace/ShortX/specs/008-add-sidebar-action/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies; can start immediately.
- **Foundational (Phase 2)**: Depends on Setup completion; blocks all user stories.
- **User Story 1 (Phase 3)**: Depends on Foundational completion; MVP display action.
- **User Story 2 (Phase 4)**: Depends on Foundational completion and can be developed alongside US1, but final display verification benefits from US1.
- **User Story 3 (Phase 5)**: Depends on Foundational completion and the service UI/manager surfaces from US1.
- **Polish (Phase 6)**: Depends on all desired user stories being complete.

### User Story Dependencies

- **User Story 1 (P1)**: Starts after Phase 2; no dependency on US2 or US3.
- **User Story 2 (P2)**: Starts after Phase 2; independently testable through editor persistence, with runtime display verification after US1.
- **User Story 3 (P3)**: Starts after US1 sidebar UI and manager files exist; uses button configuration from US2 for full end-to-end testing.

### Within Each User Story

- Models and state classes before UI and manager integration.
- UI rendering before runtime manager show/dismiss integration.
- Runtime manager integration before actor dispatch.
- Gesture callback plumbing before tap/long-press action execution.
- Story checkpoint validation before moving to the next priority if implementing sequentially.

### Parallel Opportunities

- T002 and T003 can run in parallel after T001.
- T010, T011, T014, and T015 can run in parallel after the foundational action model is understood.
- T018, T019, and T020 can run in parallel within US1 because they touch separate runtime state and UI files.
- T027 and T029 can run in parallel within US2 because view-model state and sort dialog are separate files.
- T035 can run in parallel with manager-side US3 preparation once US1 files exist.
- T045 and T046 can run in parallel during polish.

---

## Parallel Example: User Story 1

```bash
Task: "Implement runtime session state fields for one visible action-sidebar instance in /Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/overlay/actionsidebar/ActionSidebarSession.kt"
Task: "Implement sidebar button/view data models for rendering icons, labels, order, and empty-state text in /Users/tornaco/Documents/workspace/ShortX/services-ui/src/main/java/tornaco/apps/shortx/ui/actionsidebar/ActionSidebarModels.kt"
Task: "Implement compact side-panel Compose content with scrollable vertical buttons, label truncation, and close affordance in /Users/tornaco/Documents/workspace/ShortX/services-ui/src/main/java/tornaco/apps/shortx/ui/actionsidebar/ActionSidebarPanel.kt"
```

## Parallel Example: User Story 2

```bash
Task: "Implement ShowActionSidebarSelectorState, ActionSidebarButtonEditorState, and conversion to Action.ShowActionSidebar in /Users/tornaco/Documents/workspace/ShortX/ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/selector/actionsidebar/ShowActionSidebarSelectorViewModel.kt"
Task: "Implement reorder dialog/state for sidebar buttons in /Users/tornaco/Documents/workspace/ShortX/ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/selector/actionsidebar/ActionSidebarButtonSortDialog.kt"
```

## Parallel Example: User Story 3

```bash
Task: "Add tap and long-press callback plumbing to the sidebar UI model in /Users/tornaco/Documents/workspace/ShortX/services-ui/src/main/java/tornaco/apps/shortx/ui/actionsidebar/ActionSidebarModels.kt"
Task: "Prepare tap and long-press action execution handling in /Users/tornaco/Documents/workspace/ShortX/services/src/main/java/tornaco/apps/shortx/services/overlay/actionsidebar/ActionSidebarManager.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 setup.
2. Complete Phase 2 foundational model, conversion, metadata, actor registration, and translations.
3. Complete Phase 3 User Story 1 display and dismissal behavior.
4. Stop and validate that a valid sidebar action displays within 1 second, is compact, and dismisses reliably.

### Incremental Delivery

1. Complete Setup and Foundational phases to make the new action available across modules.
2. Deliver US1 so the action can show a sidebar and handle display failures.
3. Deliver US2 so users can configure and persist buttons in the editor.
4. Deliver US3 so sidebar button tap and long-press actions execute independently.
5. Run Polish compile validation and quickstart scenarios.

### Parallel Team Strategy

1. One developer completes `core-api` and `ui-base` foundational model work.
2. One developer builds service-owned sidebar UI in `services-ui` after foundational model shape is stable.
3. One developer builds editor UI in `ui` after conversion classes exist.
4. Runtime manager and actor integration should be serialized with service UI callback contracts to avoid file conflicts.

---

## Notes

- This feature must create a new action separate from existing `ShowSideBar` and similar overlay actions.
- Export/import should be preserved through protobuf-backed action persistence and verified through conversion paths.
- User-facing text should be added to checked-in JSON i18n assets, not only fallback strings in code.
- Permission and display failures should follow existing service-side user feedback conventions.
