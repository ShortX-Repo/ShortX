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
- [X] T002 [P] Review OCR entry display surface in `ui/src/main/java/tornaco/apps/shortx/ui/settings/SettingsScreen.kt`
- [X] T003 [P] Review localized condition and OCR label keys in `ui/src/main/assets/i18n/en-US/`, `ui/src/main/assets/i18n/zh-CN/`, and `ui/src/main/assets/i18n/zh-TW/`
- [X] T004 [P] Review existing condition inversion behavior in `ui/src/main/java/tornaco/apps/shortx/ui/addrule/condition/ConditionContent.kt` and `ui/src/main/java/tornaco/apps/shortx/ui/addrule/condition/selector/SystemConditionSelectHandler.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Shared prerequisites that prevent inconsistent UI changes across stories.

**Critical**: Complete these before user story implementation.

- [X] T005 Define or identify reusable localized keys for normalized condition categories and state choices in `ui/src/main/assets/i18n/en-US/*.json`, `ui/src/main/assets/i18n/zh-CN/*.json`, and `ui/src/main/assets/i18n/zh-TW/*.json`
- [X] T006 Identify condition selector entry points in `ui/src/main/java/tornaco/apps/shortx/ui/addrule/condition/selector/SystemConditionSelectHandler.kt`
- [X] T007 Identify summary label entry points in `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/condition/mapper/I18NMapper.kt`
- [X] T008 Identify OCR label entry points in `ui/src/main/java/tornaco/apps/shortx/ui/settings/SettingsScreen.kt`

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

## Phase 4: User Story 2 - Correct OCR Engine Label (Priority: P2)

**Goal**: Correct the `OCR Engine` label while keeping the surrounding setting behavior unchanged.

**Independent Test**: Open the OCR-related setting entry and verify it displays `OCR Engine`.

### Implementation for User Story 2

- [X] T016 [US2] Correct the affected OCR label in `ui/src/main/java/tornaco/apps/shortx/ui/settings/SettingsScreen.kt`
- [X] T017 [US2] Align OCR label capitalization in locale assets under `ui/src/main/assets/i18n/en-US/`, `ui/src/main/assets/i18n/zh-CN/`, and `ui/src/main/assets/i18n/zh-TW/`
- [ ] T018 [US2] Manually verify US2 acceptance criteria from `specs/009-normalize-ui-fixes/quickstart.md` for the OCR Engine label

**Checkpoint**: User Story 2 is fully functional and independently testable.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Final validation and cleanup across all stories.

- [X] T038 Run UI module compile validation with `./gradlew :ui-base:compileDebugKotlin :ui:compileRowDebugKotlin` from repository root `/Users/tornaco/Documents/workspace/ShortX`
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
- **Phase 5 Polish**: Depends on all selected user stories being complete.

### User Story Dependencies

- **US1 Clearer Condition Selection**: No dependency on other stories; recommended MVP because it is core rule-authoring behavior.
- **US2 Correct OCR Engine Label**: No dependency on US1; small terminology correction with isolated settings/UI impact.

### Parallel Opportunities

- T002, T003, and T004 can run in parallel during setup.
- After T005-T008, US1, US2, US3, and US4 can be implemented in parallel by different agents if overlapping i18n changes are coordinated.
- T016 and T017 can run in parallel in US2.

---

## Parallel Example: User Story 1

```text
Task: "Update condition selector labels in ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/condition/mapper/I18NMapper.kt"
Task: "Add localized condition text in ui/src/main/assets/i18n/en-US/i18n-en-US.json, ui/src/main/assets/i18n/zh-CN/i18n-zh-CN.json, and ui/src/main/assets/i18n/zh-TW/i18n-zh-TW.json"
```

## Parallel Example: User Story 2

```text
Task: "Correct OCR Engine label in ui/src/main/java/tornaco/apps/shortx/ui/settings/SettingsScreen.kt"
Task: "Align OCR label capitalization in ui/src/main/assets/i18n/en-US/, ui/src/main/assets/i18n/zh-CN/, and ui/src/main/assets/i18n/zh-TW/"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 and Phase 2.
2. Complete Phase 3 for the six requested condition presentations.
3. Stop and validate that saved rule behavior and summaries remain compatible.

### Incremental Delivery

1. Deliver US1 to normalize condition authoring without runtime behavior changes.
2. Deliver US2 to correct the OCR Engine label.
3. Run Phase 5 validation after selected stories are complete.

### Single-Agent Strategy

1. Complete setup and foundational discovery.
2. Implement stories in priority order: US1, US2.
3. Run compile validation and quickstart manual verification.
4. Review final diff for scope discipline before handoff.

---

## Notes

- Do not change rule engine, action execution, AIDL, or protobuf behavior for this feature.
- Keep condition normalization limited to the six requested examples.
- Update checked-in i18n JSON assets for all changed product text.
- If implementation discovery finds that an exact file path differs, keep the task intent and update the nearest matching file in the same documented module.
