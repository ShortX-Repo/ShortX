# Implementation Plan: Sidebar Action

**Branch**: `008-add-sidebar-action` | **Date**: 2026-05-18 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/008-add-sidebar-action/spec.md`

## Summary

Add a new configurable ShortX action that displays a phone-vendor-style floating sidebar. Users can configure sidebar buttons with icon, text, ordering, tap action, and long-press action. The action must be separate from existing similar actions, integrate with the normal action editor/selector flows, display through the runtime overlay/service UI path, execute nested actions from button gestures, and preserve configuration across edit/export/import flows.

## Technical Context

**Language/Version**: Kotlin/JVM and Android Kotlin in existing ShortX modules; protobuf for persisted action models  
**Primary Dependencies**: Existing ShortX `core-api`, `core`, `ui`, `ui-base`, `services`, and `services-ui` modules; existing Compose UI stack; existing action evaluation pipeline  
**Storage**: Existing action protobuf persistence and ShortX rule/direct-action storage  
**Testing**: Gradle Kotlin compile validation; targeted unit tests where model conversion/evaluation logic is isolated; manual Android runtime validation for overlay behavior  
**Target Platform**: Android app/runtime service environment supported by current ShortX project  
**Project Type**: Android automation app with runtime service and overlay UI  
**Performance Goals**: Sidebar should appear within 1 second for valid configurations; tap and long-press should execute the intended action without accidental duplicate gesture execution  
**Constraints**: Must be a new action separate from existing similar actions; must reuse existing action editor concepts; must handle overlay/runtime permission failures gracefully; must preserve export/import compatibility expectations  
**Scale/Scope**: One new action type, configurable button list, runtime sidebar display, nested gesture action execution, action editor integration, and documentation/contracts for the model and UI behavior

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

The constitution file currently contains only template placeholders and no actionable project principles or gates. No constitution violations can be evaluated for this feature.

Gate status before Phase 0: PASS, no applicable gates.

## Project Structure

### Documentation (this feature)

```text
specs/008-add-sidebar-action/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── interfaces.md
└── tasks.md
```

### Source Code (repository root)

```text
core-api/
├── src/main/proto/shortx/
│   └── Actions.proto
└── src/main/java/tornaco/apps/shortx/core/

ui-base/
└── src/main/java/tornaco/apps/shortx/ui/addrule/action/

ui/
└── src/main/java/tornaco/apps/shortx/ui/addrule/action/

services/
└── src/main/java/tornaco/apps/shortx/services/rule/actor/

services-ui/
└── src/main/java/tornaco/apps/shortx/ui/sidebar/
```

**Structure Decision**: Implement the new action across the existing shared model, action editor, runtime actor, and service-owned UI layers. The model belongs in `core-api` because action definitions are shared across UI, services, export/import, and evaluation. Editor-facing models and selectors belong in `ui-base`/`ui`. Runtime evaluation belongs in `services`. The visible sidebar surface belongs in `services-ui` so the runtime can show UI without depending on the main app screen module.

## Complexity Tracking

No constitution violations or extra architectural complexity exceptions are identified. Cross-module work is expected because the feature is a new persisted runtime action with editor UI and service-owned display UI.

## Phase 0: Research Summary

Research output is captured in [research.md](research.md). Key decisions:

- Use a new action model rather than modifying existing similar actions.
- Store each sidebar button as icon, label, order, tap action list, and long-press action list.
- Display the sidebar through runtime/service UI overlay patterns.
- Execute button gesture actions through the existing action evaluation semantics.

## Phase 1: Design Summary

Design output is captured in:

- [data-model.md](data-model.md)
- [contracts/interfaces.md](contracts/interfaces.md)
- [quickstart.md](quickstart.md)

Post-design constitution check: PASS, no applicable gates. The design stays within existing ShortX layering and does not introduce a new subsystem outside the established action/editor/runtime/service-UI flow.
