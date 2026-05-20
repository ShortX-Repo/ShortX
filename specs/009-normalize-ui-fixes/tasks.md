# Tasks: Normalize UI Fixes

**Input**: Design documents from `/specs/009-normalize-ui-fixes/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/ui-behavior.md, quickstart.md

**Tests**: No explicit TDD or automated test request was made. Tasks include compile validation and manual verification from quickstart.md.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel with other tasks in the same phase because it touches different files or only reads context
- **[Story]**: User story label for story-phase tasks only
- Every task includes an exact file path or path pattern to inspect or modify

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Confirm affected surfaces and current patterns before changing behavior.

- [X] T001 Review current condition presentation and conversion flow in `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/condition/mapper/I18NMapper.kt`, `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/condition/model/Condition.kt`, `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/condition/model/ConditionToProtoCondition.kt`, and `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/condition/model/ProtoAnyToCondition.kt`
- [X] T002 [P] Review editor host and embedded editor wrapper behavior in `ui/src/main/java/tornaco/apps/shortx/ui/base/code/`, `ui/src/main/java/tornaco/apps/shortx/ui/base/code/js/`, `ui/src/main/java/tornaco/apps/shortx/ui/base/code/mvel/`, and `external/codeditor/`
- [X] T003 [P] Review shared search UI and repository page usage in `ui-base/src/main/java/tornaco/apps/shortx/ui/base/Search.kt`, `ui/src/main/java/tornaco/apps/shortx/ui/remote/`, and repository browser screens under `ui/src/main/java/tornaco/apps/shortx/ui/`
- [X] T004 [P] Review i18n keys for affected labels in `ui/src/main/assets/i18n/en-US/`, `ui/src/main/assets/i18n/zh-CN/`, and `ui/src/main/assets/i18n/zh-TW/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Shared prerequisites that prevent inconsistent UI changes across stories.

**Critical**: Complete these before user story implementation.

- [X] T005 Define or identify reusable localized keys for normalized condition categories and state choices in `ui/src/main/assets/i18n/en-US/*.json`, `ui/src/main/assets/i18n/zh-CN/*.json`, and `ui/src/main/assets/i18n/zh-TW/*.json`
- [X] T006 Identify the shared editor search and paste integration point used by shell, JS, and MVEL editors in `ui/src/main/java/tornaco/apps/shortx/ui/base/code/` or `external/codeditor/`
- [X] T007 Identify the repository item model fields for author, title, and description in `ui/src/main/java/tornaco/apps/shortx/ui/remote/` and the repository browser screen under `ui/src/main/java/tornaco/apps/shortx/ui/`
- [X] T008 Identify the JS console output screen and line action menu implementation under `ui/src/main/java/tornaco/apps/shortx/ui/base/code/js/` or related JS output screens under `ui/src/main/java/tornaco/apps/shortx/ui/`

**Checkpoint**: Foundation ready. User stories can now proceed independently where files do not overlap.

---

## Phase 3: User Story 1 - Clearer Condition Selection (Priority: P1) MVP

**Goal**: Show the six requested conditions as normalized state categories with explicit choices while preserving existing runtime behavior and saved-rule compatibility.

**Independent Test**: Open the condition picker, configure each affected condition, save and reopen rules, and verify only the requested condition presentations changed.

### Implementation for User Story 1

- [X] T009 [US1] Update condition selector labels for KeyguardIsLocked, VPNIsConnected, ScreenStayAwake, IsRinging, IsInCall, and ScreenOrientationIsPort in `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/condition/mapper/I18NMapper.kt`
- [X] T010 [US1] Update affected condition summary labels to show normalized category plus selected state in `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/condition/mapper/I18NMapper.kt`
- [X] T011 [US1] Add or adjust localized category and state-choice text for affected conditions in `ui/src/main/assets/i18n/en-US/i18n-en-US.json`, `ui/src/main/assets/i18n/zh-CN/i18n-zh-CN.json`, and `ui/src/main/assets/i18n/zh-TW/i18n-zh-TW.json`
- [X] T012 [US1] Locate the condition editor UI for boolean/invertible conditions under `ui/src/main/java/tornaco/apps/shortx/ui/addrule/condition/` and update screen lock state to offer locked and unlocked choices while mapping unlocked to the existing invert behavior
- [X] T013 [US1] Update the condition editor UI for screen orientation under `ui/src/main/java/tornaco/apps/shortx/ui/addrule/condition/` to offer portrait and landscape choices while preserving existing orientation semantics
- [X] T014 [US1] Verify condition-to-proto and proto-to-condition mappings preserve existing model semantics in `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/condition/model/ConditionToProtoCondition.kt` and `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/condition/model/ProtoAnyToCondition.kt`
- [ ] T015 [US1] Manually verify US1 acceptance criteria from `specs/009-normalize-ui-fixes/quickstart.md` using the affected condition picker and saved-rule summary screens

**Checkpoint**: User Story 1 is fully functional and independently testable.

---

## Phase 4: User Story 2 - Consistent Editor Controls and Layout (Priority: P1)

**Goal**: Make shell, JS, and MVEL editor search controls, shortcut bar, paste behavior, and shell independent-run placement visually consistent and reliable.

**Independent Test**: Use each editor's search, expanded search options, paste, shortcut symbol bar, and shell independent-run control in light and dark modes.

### Implementation for User Story 2

- [X] T016 [US2] Replace pink editor search action text styling with dynamic app color styling in the shared editor search UI under `ui/src/main/java/tornaco/apps/shortx/ui/base/code/` or `external/codeditor/`
- [X] T017 [US2] Add outlined button affordance and visible press feedback for previous, next, replace, and related search controls in the shared editor search UI under `ui/src/main/java/tornaco/apps/shortx/ui/base/code/` or `external/codeditor/`
- [X] T018 [US2] Improve expanded whole-word and case-sensitive search options with a rounded readable container in the shared editor search UI under `ui/src/main/java/tornaco/apps/shortx/ui/base/code/` or `external/codeditor/`
- [X] T019 [US2] Update shortcut symbol bar dynamic color and dark-mode readability in `ui/src/main/java/tornaco/apps/shortx/ui/base/code/EditorToolsBar.kt`
- [X] T020 [US2] Restore shell independent-run switch next to the close control and remove it from the more-only path in `ui/src/main/java/tornaco/apps/shortx/ui/base/code/ShellCommandEditorActivity.kt`
- [X] T021 [US2] Preserve shell independent-run state saving behavior when finishing the editor in `ui/src/main/java/tornaco/apps/shortx/ui/base/code/ShellCommandEditorActivity.kt`
- [X] T022 [US2] Fix paste cursor retention for shell editor integration in `ui/src/main/java/tornaco/apps/shortx/ui/base/code/ShellCommandEditorActivity.kt` or the shared editor wrapper in `external/codeditor/`
- [X] T023 [US2] Fix paste cursor retention for JS editor integration in `ui/src/main/java/tornaco/apps/shortx/ui/base/code/js/JSEditorActivity.kt` or the shared editor wrapper in `external/codeditor/`
- [X] T024 [US2] Fix paste cursor retention for MVEL editor integration in `ui/src/main/java/tornaco/apps/shortx/ui/base/code/mvel/MVELEditorActivity.kt` or the shared editor wrapper in `external/codeditor/`
- [ ] T025 [US2] Manually verify US2 acceptance criteria from `specs/009-normalize-ui-fixes/quickstart.md` in shell, JS, and MVEL editors under light and dark modes

**Checkpoint**: User Story 2 is fully functional and independently testable.

---

## Phase 5: User Story 3 - Working Search and Line Actions (Priority: P2)

**Goal**: Make code repository search work by author/title/description and complete JS console output line actions.

**Independent Test**: Search repository items by each supported field, then copy/share/use from-current-line actions from JS console output.

### Implementation for User Story 3

- [X] T026 [US3] Replace the code repository page search field with the shared `SearchBar` pattern from `ui-base/src/main/java/tornaco/apps/shortx/ui/base/Search.kt` in the repository browser screen under `ui/src/main/java/tornaco/apps/shortx/ui/`
- [X] T027 [US3] Implement case-insensitive partial repository filtering by author, title, and description in the repository screen or view model under `ui/src/main/java/tornaco/apps/shortx/ui/remote/` or the repository browser package under `ui/src/main/java/tornaco/apps/shortx/ui/`
- [X] T028 [US3] Ensure empty repository search restores the full loaded list in the repository screen or view model under `ui/src/main/java/tornaco/apps/shortx/ui/remote/` or the repository browser package under `ui/src/main/java/tornaco/apps/shortx/ui/`
- [X] T029 [US3] Implement copy-current-line behavior with user-visible confirmation in the JS console output line action menu under `ui/src/main/java/tornaco/apps/shortx/ui/base/code/js/` or related JS output screen under `ui/src/main/java/tornaco/apps/shortx/ui/`
- [X] T030 [US3] Implement share behavior for the selected JS console output line in the JS console output line action menu under `ui/src/main/java/tornaco/apps/shortx/ui/base/code/js/` or related JS output screen under `ui/src/main/java/tornaco/apps/shortx/ui/`
- [X] T031 [US3] Implement from-current-line behavior starting at the selected JS console output line in the JS console output line action menu under `ui/src/main/java/tornaco/apps/shortx/ui/base/code/js/` or related JS output screen under `ui/src/main/java/tornaco/apps/shortx/ui/`
- [ ] T032 [US3] Manually verify US3 acceptance criteria from `specs/009-normalize-ui-fixes/quickstart.md` for repository search and JS console output line actions

**Checkpoint**: User Story 3 is fully functional and independently testable.

---

## Phase 6: User Story 4 - Correct Labels and Fresh Input Defaults (Priority: P3)

**Goal**: Correct OCR Engine capitalization and prevent stale previous-row content in method hook context expressions and command parameters.

**Independent Test**: View the sidebar OCR entry, add multiple method hook context expressions, and add multiple command parameters.

### Implementation for User Story 4

- [X] T033 [P] [US4] Correct the affected sidebar OCR label from `OCR engine` to `OCR Engine` in `ui/src/main/java/tornaco/apps/shortx/ui/settings/SettingsScreen.kt`
- [X] T034 [P] [US4] Align OCR Engine capitalization in locale assets under `ui/src/main/assets/i18n/en-US/`, `ui/src/main/assets/i18n/zh-CN/`, and `ui/src/main/assets/i18n/zh-TW/`
- [X] T035 [US4] Fix newly added method hook context expression rows so they initialize independently in the method hook selector flow under `ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/selector/`
- [X] T036 [US4] Fix newly added command parameter rows so they initialize independently in the command parameter editing flow under `ui/src/main/java/tornaco/apps/shortx/ui/addrule/` or `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/`
- [ ] T037 [US4] Manually verify US4 acceptance criteria from `specs/009-normalize-ui-fixes/quickstart.md` for OCR Engine label and fresh-row defaults

**Checkpoint**: User Story 4 is fully functional and independently testable.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final validation and cleanup across all stories.

- [X] T038 Run UI module compile validation with `./gradlew :ui-base:compileKotlin :ui:compileDebugKotlin` from repository root `/Users/tornaco/Documents/workspace/ShortX`
- [X] T039 Verify no AIDL or protobuf files were changed for this UI-only feature by reviewing changes under `core-api/src/main/`
- [X] T040 Verify all changed user-facing strings have aligned entries in `ui/src/main/assets/i18n/en-US/`, `ui/src/main/assets/i18n/zh-CN/`, and `ui/src/main/assets/i18n/zh-TW/`
- [ ] T041 Run the full manual verification checklist in `specs/009-normalize-ui-fixes/quickstart.md`
- [ ] T042 Review final diff to ensure unrelated conditions, actions, and runtime services were not changed outside paths required by `specs/009-normalize-ui-fixes/plan.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 Setup**: No dependencies.
- **Phase 2 Foundational**: Depends on Phase 1 and blocks all story implementation.
- **Phase 3 US1**: Depends on Phase 2 and is the MVP.
- **Phase 4 US2**: Depends on Phase 2 and can run in parallel with US1 if file conflicts are managed.
- **Phase 5 US3**: Depends on Phase 2 and can run after repository and console locations are identified.
- **Phase 6 US4**: Depends on Phase 2 and can run in parallel with US1-US3 because it mostly touches separate screens.
- **Phase 7 Polish**: Depends on all selected user stories being complete.

### User Story Dependencies

- **US1 Clearer Condition Selection**: No dependency on other stories; recommended MVP because it is core rule-authoring behavior.
- **US2 Consistent Editor Controls and Layout**: No dependency on US1; shares i18n and editor validation concerns.
- **US3 Working Search and Line Actions**: No dependency on US1 or US2; repository search and JS console actions are independently testable.
- **US4 Correct Labels and Fresh Input Defaults**: No dependency on other stories; small polish/correctness slice.

### Parallel Opportunities

- T002, T003, and T004 can run in parallel during setup.
- After T005-T008, US1, US2, US3, and US4 can be implemented in parallel by different agents if overlapping i18n changes are coordinated.
- T033 and T034 can run in parallel in US4.
- Repository search tasks T026-T028 can be implemented independently from JS console tasks T029-T031 inside US3.
- Paste fixes T022-T024 can run in parallel only if they are confirmed to be editor-host specific; if the fix belongs in a shared wrapper, do the shared wrapper once and then verify all three editors.

---

## Parallel Example: User Story 1

```text
Task: "Update condition selector labels in ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/condition/mapper/I18NMapper.kt"
Task: "Add localized condition text in ui/src/main/assets/i18n/en-US/i18n-en-US.json, ui/src/main/assets/i18n/zh-CN/i18n-zh-CN.json, and ui/src/main/assets/i18n/zh-TW/i18n-zh-TW.json"
```

## Parallel Example: User Story 2

```text
Task: "Update shortcut symbol bar dynamic color in ui/src/main/java/tornaco/apps/shortx/ui/base/code/EditorToolsBar.kt"
Task: "Restore shell independent-run switch in ui/src/main/java/tornaco/apps/shortx/ui/base/code/ShellCommandEditorActivity.kt"
```

## Parallel Example: User Story 3

```text
Task: "Implement repository filtering in ui/src/main/java/tornaco/apps/shortx/ui/remote/ or the repository browser package"
Task: "Implement JS console output copy/share/from-current-line actions in ui/src/main/java/tornaco/apps/shortx/ui/base/code/js/ or related JS output screen"
```

## Parallel Example: User Story 4

```text
Task: "Correct OCR Engine label in ui/src/main/java/tornaco/apps/shortx/ui/settings/SettingsScreen.kt"
Task: "Fix method hook context expression row initialization in ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/selector/"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 and Phase 2.
2. Complete Phase 3 for the six requested condition presentations.
3. Stop and validate that saved rule behavior and summaries remain compatible.

### Incremental Delivery

1. Deliver US1 to normalize condition authoring without runtime behavior changes.
2. Deliver US2 to restore editor reliability and visual consistency.
3. Deliver US3 to make repository search and JS console line actions functional.
4. Deliver US4 to correct labels and stale-row defaults.
5. Run Phase 7 validation after selected stories are complete.

### Single-Agent Strategy

1. Complete setup and foundational discovery.
2. Implement stories in priority order: US1, US2, US3, US4.
3. Run compile validation and quickstart manual verification.
4. Review final diff for scope discipline before handoff.

---

## Notes

- Do not change rule engine, action execution, AIDL, or protobuf behavior for this feature.
- Keep condition normalization limited to the six requested examples.
- Update checked-in i18n JSON assets for all changed product text.
- If implementation discovery finds that an exact file path differs, keep the task intent and update the nearest matching file in the same documented module.
