# Implementation Plan: Normalize UI Fixes

**Branch**: `009-normalize-ui-fixes` | **Date**: 2026-05-19 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/009-normalize-ui-fixes/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Normalize a bounded set of condition presentation labels/options and correct the `OCR Engine` label without changing rule/action execution semantics. The approach is UI-first: update localized labels and selector mapping where needed, keep the existing inversion/model behavior, and avoid unrelated editor, repository, logging, or parameter-flow changes.

## Technical Context

**Language/Version**: Kotlin/JVM and Android Kotlin, using the repository's current Gradle and Android toolchain  
**Primary Dependencies**: Jetpack Compose Material 3 UI, shared `ui-base` components, Sora code editor integration, existing ShortX i18n JSON assets  
**Storage**: Existing rule/action models and checked-in translation JSON assets; no new persistent storage planned  
**Testing**: Module compile validation plus targeted manual verification for UI behavior in light/dark modes and editor interactions  
**Target Platform**: Android application UI and editor flows supported by the existing ShortX app
**Project Type**: Android mobile app with shared UI libraries and runtime service integration  
**Performance Goals**: Condition selection and rule editing remain as responsive as before after the UI wording changes  
**Constraints**: Preserve existing condition/action execution behavior; do not modify unrelated conditions/actions; update localized user-facing text in checked-in i18n assets  
**Scale/Scope**: Two requested fixes only: selected condition UI normalization and OCR Engine capitalization correction

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

The constitution file currently contains placeholder principles and no enforceable project-specific gates. Planning gates for this feature are therefore derived from the feature spec and repository guidance:

- **Behavior Preservation Gate**: PASS. Plan keeps existing rule/action runtime behavior and limits condition work to UI presentation and mapping.
- **Localization Gate**: PASS. Plan includes checked-in JSON i18n asset updates for changed user-facing text.
- **Scope Gate**: PASS. Plan is bounded to the selected condition UI normalization and OCR Engine capitalization correction only.
- **Contract Gate**: PASS. No AIDL/protobuf contract changes are planned.

## Project Structure

### Documentation (this feature)

```text
specs/009-normalize-ui-fixes/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
ui-base/src/main/java/tornaco/apps/shortx/ui/
├── addrule/condition/model/Condition.kt
├── addrule/condition/model/ConditionToProtoCondition.kt
├── addrule/condition/model/ProtoAnyToCondition.kt
├── addrule/condition/mapper/I18NMapper.kt
└── base/Search.kt

ui/src/main/java/tornaco/apps/shortx/ui/
├── settings/SettingsScreen.kt
└── addrule/condition/ and related rule-authoring screens

ui/src/main/assets/i18n/
├── en-US/*.json
├── zh-CN/*.json
└── zh-TW/*.json

external/codeditor or editor integration sources, if implementation discovery shows search/paste behavior belongs to the embedded editor wrapper rather than the ShortX editor host.
```

**Structure Decision**: Implement as a UI-focused Android feature across `ui` and `ui-base`, with i18n asset updates. Runtime modules, editor wrappers, code library screens, and logging screens are out of scope.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | N/A | N/A |

## Phase 0 Research Summary

See [research.md](research.md). All planning questions are resolved with existing app patterns: keep behavior unchanged, use condition inversion where needed, update checked-in i18n assets, and avoid contract changes.

## Phase 1 Design Summary

See [data-model.md](data-model.md), [contracts/ui-behavior.md](contracts/ui-behavior.md), and [quickstart.md](quickstart.md). The design defines presentation-level entities and UI behavior contracts for validation; no external service contracts are introduced.

## Post-Design Constitution Check

- **Behavior Preservation Gate**: PASS. Design contracts explicitly state no rule/action runtime behavior changes.
- **Localization Gate**: PASS. Data model and quickstart include locale verification.
- **Scope Gate**: PASS. Artifacts remain bounded to the condition normalization and OCR Engine label correction.
- **Contract Gate**: PASS. UI behavior contracts only; no AIDL/protobuf/interface contract changes.
