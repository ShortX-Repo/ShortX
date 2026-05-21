# Tasks: Upgrade LSPosed API 101

**Input**: Design documents from `/specs/010-upgrade-lsposed-api/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/runtime-behavior.md, quickstart.md

**Tests**: Tests are not mandated as TDD by the specification. This task list includes compile, packaging, and manual/runtime validation tasks because the feature explicitly requires debug and release validation.

**Organization**: Tasks are grouped by user story to enable independent implementation and validation.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel because it touches different files and does not depend on incomplete tasks
- **[Story]**: Maps to the user story phase, such as [US1], [US2], [US3], [US4]
- Every task includes an exact file or directory path

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Establish the API 101-only migration baseline and remove ambiguity before code migration starts.

- [X] T001 Verify `patch-xposed/patch-main/libs/libxposed-api.jar` is API 101 and record the jar source/version in `specs/010-upgrade-lsposed-api/research.md`
- [X] T002 [P] Inspect API 101 runtime callback signatures from `patch-xposed/patch-main/libs/libxposed-api.jar` and record usable entry/hook callback names in `specs/010-upgrade-lsposed-api/research.md`
- [X] T003 [P] Inventory all remaining legacy Xposed references in `patch-xposed/` and write the migration checklist to `specs/010-upgrade-lsposed-api/legacy-reference-inventory.md`
- [X] T004 [P] Inventory UI/service libxposed service jar usage in `ui/build.gradle.kts` and document whether each jar is compile-time or runtime-required in `specs/010-upgrade-lsposed-api/legacy-reference-inventory.md`
- [X] T005 Add an API 101-only migration status section to `Xposed-API-101-Upgrade-Tasks.md`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Create the shared API 101 hook and context foundation that all user stories depend on.

**CRITICAL**: No user story implementation should begin until this phase is complete.

- [X] T006 Remove legacy Xposed compile dependency from `patch-xposed/patch-main/build.gradle.kts` while keeping API 101 dependency available
- [X] T007 Remove legacy Xposed compile dependency from `patch-xposed/patch-common/build.gradle.kts` and add the API 101 compile dependency needed by shared hook code
- [X] T008 Remove legacy Xposed compile dependency from `patch-xposed/patch-systemui/build.gradle.kts` and add the API 101 compile dependency needed by SystemUI hook code
- [X] T009 Delete the legacy Xposed entry implementation in `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/XposedEntry.kt`
- [X] T010 Update Xposed module metadata for API 101-only support in `app/src/main/AndroidManifest.xml`
- [X] T011 Create API 101 runtime holder for the active Xposed interface and process metadata in `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/Api101Runtime.kt`
- [X] T012 Create a legacy-free hook callback context model in `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ShortXHookParam.kt`
- [X] T013 Create a hook registration facade for method and constructor hooks in `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/Hooks.kt`
- [X] T014 Create API 101 hook callback registry support in `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/Api101HookRegistry.kt`
- [X] T015 Create API 101 before/after Hooker bridge classes in `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/Api101Hookers.kt`
- [X] T016 Replace direct XposedBridge logging with an API 101-aware log facade in `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/XposedLog.kt`
- [X] T017 Update `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ShortXLSPosedHookEntry.kt` to initialize `Api101Runtime` and use `XposedLog`
- [X] T018 Update ProGuard keep rules for API 101 entry, hookers, and hook registry in `app/proguard-rules.pro`
- [X] T019 [P] Remove obsolete legacy Xposed keep/dontwarn rules from `app/proguard-rules.pro`
- [X] T020 [P] Update dynamic ProGuard rules for API 101-only callback retention in `app/proguard-rules-dynamic.pro`

**Checkpoint**: API 101 runtime foundation is available and legacy entry/dependency support is no longer part of the build baseline.

---

## Phase 3: User Story 1 - Run Module On API 101 Only (Priority: P1) MVP

**Goal**: The module is recognized and loaded through API 101-only runtime behavior, with no supported legacy path.

**Independent Test**: Install the upgraded module on a supported LSPosed API 101 environment, enable scope, restart target processes, and confirm module loading uses only the API 101 path.

### Implementation for User Story 1

- [X] T021 [US1] Remove references to deleted legacy entry class from app packaging resources or generated module lists under `app/src/main/`
- [X] T022 [US1] Update `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ShortXLSPosedHookEntry.kt` so `onPackageLoaded` uses the API 101 package/process values directly and does not depend on legacy process fallback
- [X] T023 [US1] Update `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/api/IPackageLoaded.kt` to document API 101-only package load semantics
- [X] T024 [US1] Update `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/api/ISystemServerLoaded.kt` to document API 101-only system server load semantics
- [X] T025 [US1] Replace any remaining `de.robv.android.xposed.callbacks.XC_LoadPackage` imports in `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/`
- [X] T026 [US1] Replace any remaining `de.robv.android.xposed.callbacks.XC_LoadPackage` imports in `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/`
- [X] T027 [US1] Remove `patch-xposed/patch-main/libs/api-82.jar` from build usage and document the deletion or retention decision in `specs/010-upgrade-lsposed-api/legacy-reference-inventory.md`
- [X] T028 [US1] Run `./gradlew :patch-xposed:patch-main:compileKotlin` and record the result in `specs/010-upgrade-lsposed-api/validation-results.md`
- [X] T029 [US1] Run `./gradlew :app:assemblePrcDebug` and record API 101 module metadata validation notes in `specs/010-upgrade-lsposed-api/validation-results.md`

**Checkpoint**: User Story 1 is functional when the module compiles and metadata/entry behavior shows API 101-only support.

---

## Phase 4: User Story 2 - Preserve Existing Automation Behavior (Priority: P1)

**Goal**: Existing rule, direct-action, method-hook, SystemUI gesture, quick settings, status bar, and system-event automations continue to work without user data recreation.

**Independent Test**: Run representative pre-upgrade automations after the migration and confirm the same user-visible outcomes for system server, SystemUI, and ordinary app hook paths.

### Implementation for User Story 2

- [X] T030 [US2] Migrate dynamic method hook callback types from legacy `MethodHookParam` to `ShortXHookParam` in `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/CustomHookUtil.kt`
- [X] T031 [US2] Preserve MVEL `param` and `helper` expression semantics in `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/CustomHookUtil.kt`
- [X] T032 [US2] Replace `AndroidAppHelper` dependency with API 101-safe process/application context provider in `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/InjectedAndroidAppHelper.kt`
- [X] T033 [US2] Update app method hook orchestration to use `ShortXHookParam` and retain self-protection in `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/CustomHookApp.kt`
- [X] T034 [US2] Migrate all helper methods in `patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/Hooks.kt` to API 101 hook registration and remove `XC_MethodHook` usage
- [X] T035 [US2] Migrate system server hook callback parameter usage in `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ProcessListHook.kt`
- [X] T036 [US2] Migrate system server hook callback parameter usage in `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ActivityTaskSupervisorHook.kt`
- [X] T037 [US2] Migrate system server hook callback parameter usage in `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ActivityRecordHook.kt`
- [X] T038 [US2] Replace legacy class lookup helper usage in `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ComputerEngineHook.kt`
- [X] T039 [US2] Migrate media provider hook callback and logging usage in `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/MediaProviderHook.kt`
- [X] T040 [US2] Search `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/` and migrate remaining direct `XC_MethodHook`, `XposedBridge`, `XposedHelpers`, and `AndroidAppHelper` references
- [X] T041 [US2] Replace XposedBridge logging and preserve main-process filtering in `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/SystemUIHook.kt`
- [X] T042 [US2] Preserve SystemUI onCreate and system-ready ordering while migrating hook registration in `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/hookApp.kt`
- [X] T043 [US2] Migrate SystemUI quick settings hook registration in `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/hookQS.kt`
- [X] T044 [US2] Migrate SystemUI tile hook registration in `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/hookTile.kt`
- [X] T045 [US2] Migrate SystemUI tile query hook registration in `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/hookTileQuery.kt`
- [X] T046 [US2] Migrate SystemUI view/status bar hook registration in `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/hookView.kt`
- [X] T047 [US2] Migrate SystemUI custom tile and dark icon hook registration in `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/`
- [X] T048 [US2] Run `./gradlew :patch-xposed:patch-common:compileKotlin` and record the result in `specs/010-upgrade-lsposed-api/validation-results.md`
- [X] T049 [US2] Run `./gradlew :patch-xposed:patch-systemui:compileKotlin` and record the result in `specs/010-upgrade-lsposed-api/validation-results.md`
- [X] T050 [US2] Run existing SystemUI gesture tests with `./gradlew :patch-xposed:patch-systemui:test` and record the result in `specs/010-upgrade-lsposed-api/validation-results.md`

**Checkpoint**: User Story 2 is functional when compile checks pass and representative system server, SystemUI, and app method-hook behavior can be validated without user rule recreation.

---

## Phase 5: User Story 3 - Remove Legacy Support Surface (Priority: P2)

**Goal**: Legacy Xposed support is fully retired from product behavior, documentation, diagnostics, and build surface.

**Independent Test**: Inspect build dependencies, release metadata, diagnostics, and documentation to confirm no legacy mode is advertised, required, or reachable as a supported path.

### Implementation for User Story 3

- [X] T051 [P] [US3] Remove legacy compatibility claims from `Xposed-API-101-Upgrade-Guide.md`
- [X] T052 [P] [US3] Update `AI/patches/patch-xposed-patch-main.md` to state API 101 entry is the only supported Xposed entry
- [X] T053 [P] [US3] Update `AI/patches/patch-xposed-patch-common.md` to describe the API 101 hook callback contract and no legacy callback support
- [X] T054 [P] [US3] Update `AI/patches/patch-xposed-patch-systemui.md` to describe API 101 SystemUI startup and gesture validation requirements
- [X] T055 [P] [US3] Update `AI/architecture/cross-module-architecture-map.md` to mark `patch-xposed` as API 101-only
- [X] T056 [US3] Remove legacy Xposed dependency and compatibility references from `ui/build.gradle.kts`
- [X] T057 [US3] Remove remaining `de.robv.android.xposed` references from `app/proguard-rules.pro`
- [X] T058 [US3] Remove remaining legacy Xposed references from `app/proguard-rules-dynamic.pro`
- [X] T059 [US3] Run a repository search for `de.robv.android.xposed`, `XposedBridge`, `XposedHelpers`, `XC_MethodHook`, `IXposed`, `api-82.jar`, and `AndroidAppHelper`, then record zero supported references or justified non-build references in `specs/010-upgrade-lsposed-api/legacy-reference-inventory.md`
- [X] T060 [US3] Update `specs/010-upgrade-lsposed-api/contracts/runtime-behavior.md` with final legacy-removal acceptance evidence

**Checkpoint**: User Story 3 is functional when no supported legacy surface remains in build files, runtime code, metadata, diagnostics, or documentation.

---

## Phase 6: User Story 4 - Validate Safe Release Behavior (Priority: P2)

**Goal**: Debug and release artifacts are validated for module recognition, event delivery, SystemUI behavior, method hooks, failure isolation, and diagnostics.

**Independent Test**: Run quickstart validation against debug and release artifacts and confirm the recorded results meet the runtime behavior contract.

### Implementation for User Story 4

- [X] T061 [US4] Create validation result template with debug/release/system-server/SystemUI/app-method-hook sections in `specs/010-upgrade-lsposed-api/validation-results.md`
- [X] T062 [US4] Run `./gradlew :patch-xposed:patch-main:compileKotlin :patch-xposed:patch-common:compileKotlin :patch-xposed:patch-systemui:compileKotlin` and record results in `specs/010-upgrade-lsposed-api/validation-results.md`
- [X] T063 [US4] Run `./gradlew :app:assemblePrcDebug` and record artifact path and module metadata checks in `specs/010-upgrade-lsposed-api/validation-results.md`
- [X] T064 [US4] Run the release assemble task used by this project and record artifact path and module metadata checks in `specs/010-upgrade-lsposed-api/validation-results.md`
- [ ] T065 [US4] Validate API 101 module recognition on a supported device and record LSPosed environment details in `specs/010-upgrade-lsposed-api/validation-results.md`
- [ ] T066 [US4] Validate system server hook-driven automation reaches ShortX runtime and record result in `specs/010-upgrade-lsposed-api/validation-results.md`
- [ ] T067 [US4] Validate SystemUI gesture and quick settings scenarios and record results in `specs/010-upgrade-lsposed-api/validation-results.md`
- [ ] T068 [US4] Validate ordinary app method-hook before-call and after-call scenarios and record results in `specs/010-upgrade-lsposed-api/validation-results.md`
- [ ] T069 [US4] Validate a missing target class or method produces diagnostics without blocking unrelated automations and record result in `specs/010-upgrade-lsposed-api/validation-results.md`
- [ ] T070 [US4] Validate ShortX self-hook protection for user-configured app method hooks and record result in `specs/010-upgrade-lsposed-api/validation-results.md`
- [X] T071 [US4] Update `specs/010-upgrade-lsposed-api/quickstart.md` with final validated commands, artifact locations, and manual validation notes

**Checkpoint**: User Story 4 is functional when debug and release validation evidence exists for all runtime behavior contract sections.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Clean up migration residue and ensure the feature is maintainable after implementation.

- [X] T072 [P] Normalize terminology from `Xposed API 101` and `LSposed API 101` to `LSPosed API 101` in `specs/010-upgrade-lsposed-api/`
- [X] T073 [P] Update `AGENTS.md` Speckit references if any generated artifact path changed
- [X] T074 [P] Update `Xposed-API-101-Upgrade-Tasks.md` with completed migration outcomes and removed legacy surface notes
- [X] T075 Run `./gradlew :app:assemblePrcDebug` after all cleanup and record final result in `specs/010-upgrade-lsposed-api/validation-results.md`
- [X] T076 Run repository search for remaining legacy API terms and record final status in `specs/010-upgrade-lsposed-api/legacy-reference-inventory.md`
- [X] T077 Review `specs/010-upgrade-lsposed-api/spec.md`, `specs/010-upgrade-lsposed-api/plan.md`, and `specs/010-upgrade-lsposed-api/tasks.md` for consistency after implementation

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies; can start immediately.
- **Foundational (Phase 2)**: Depends on Setup completion; blocks all user story work.
- **User Story 1 (Phase 3)**: Depends on Foundational completion and is the MVP.
- **User Story 2 (Phase 4)**: Depends on Foundational completion; can start after the API 101 hook foundation exists, but final validation benefits from US1 metadata work.
- **User Story 3 (Phase 5)**: Depends on US1 and most of US2, because legacy cleanup should happen after API 101 replacement paths exist.
- **User Story 4 (Phase 6)**: Depends on US1, US2, and US3 for complete release validation.
- **Polish (Phase 7)**: Depends on all desired user stories being complete.

### User Story Dependencies

- **US1 Run Module On API 101 Only (P1)**: MVP; no dependency on other user stories after Foundational.
- **US2 Preserve Existing Automation Behavior (P1)**: Can proceed after Foundational; requires US1 for final module-loading validation.
- **US3 Remove Legacy Support Surface (P2)**: Should follow US1/US2 to avoid deleting legacy code before API 101 replacements are available.
- **US4 Validate Safe Release Behavior (P2)**: Final verification story; depends on implemented behavior from US1-US3.

### Parallel Opportunities

- T002, T003, T004 can run in parallel after T001 starts.
- T019 and T020 can run in parallel after ProGuard API 101 requirements are understood.
- Documentation tasks T051-T055 can run in parallel.
- Runtime validation tasks T066-T070 can be split by process area once debug/release artifacts are built.
- Polish tasks T072-T074 can run in parallel.

---

## Parallel Example: User Story 2

```bash
# Parallelizable by area after Foundational completion:
Task: "Migrate dynamic method hook callback types in patch-xposed/patch-common/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/CustomHookUtil.kt"
Task: "Migrate system server hook callback parameter usage in patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ProcessListHook.kt"
Task: "Replace XposedBridge logging and preserve main-process filtering in patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/SystemUIHook.kt"
```

## Parallel Example: User Story 3

```bash
# Documentation updates can run together:
Task: "Update AI/patches/patch-xposed-patch-main.md"
Task: "Update AI/patches/patch-xposed-patch-common.md"
Task: "Update AI/patches/patch-xposed-patch-systemui.md"
Task: "Update AI/architecture/cross-module-architecture-map.md"
```

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 setup inventories.
2. Complete Phase 2 API 101 hook foundation and remove legacy build baseline.
3. Complete Phase 3 module metadata and entry migration.
4. Stop and validate API 101-only module compilation and debug packaging.

### Incremental Delivery

1. Establish API 101-only foundation.
2. Deliver US1 so the module has one supported loading path.
3. Deliver US2 by restoring behavior through the new runtime foundation.
4. Deliver US3 by removing remaining legacy support surface.
5. Deliver US4 with debug/release runtime validation evidence.

### Quality Gates

- No user story is complete without its checkpoint validation.
- No legacy Xposed dependency or supported reference may remain after US3.
- No release is ready without US4 validation evidence.
