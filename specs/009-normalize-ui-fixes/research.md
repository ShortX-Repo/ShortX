# Research: Normalize UI Fixes

## Decision: Treat condition normalization as presentation mapping only

**Rationale**: The feature explicitly requires UI normalization without changing existing implementation logic. The affected condition classes already carry inverse flags or state fields, so user-facing state choices can map to existing condition plus inversion or existing boolean/enum values.

**Alternatives considered**: Adding new condition types or changing runtime evaluators was rejected because it would alter stored model semantics and increase regression risk for existing rules.

## Decision: Use existing inverse semantics for missing negative states

**Rationale**: Screen unlocked can be represented as the inverse of the existing screen locked condition. Landscape can follow the same presentation pattern around the existing portrait orientation condition if no separate landscape model exists.

**Alternatives considered**: Introducing explicit unlocked or landscape condition models was rejected because the spec asks not to change behavior and because persisted compatibility would become more complex.

## Decision: Keep all changed user-facing labels in checked-in i18n assets

**Rationale**: `AI/README.md` and `AI/architecture/i18n-architecture.md` state that product translations must be updated under `ui/src/main/assets/i18n/` rather than relying only on fallback strings. The affected labels exist in the locale assets today, at least for Chinese condition names.

**Alternatives considered**: Hardcoding fallback labels in UI code was rejected because it would bypass the product translation system and create inconsistent service/UI text behavior.

## Decision: Limit the feature strictly to the two confirmed changes

**Rationale**: The user explicitly requested that all changes except the selected condition normalization and the `OCR Engine` capitalization fix be removed. The resulting implementation and documentation should therefore exclude editor, repository, logging, and parameter-flow work.

**Alternatives considered**: Keeping the broader UI/editor polish work was rejected because it no longer matches the confirmed scope.

## Decision: Validation should combine compile checks with targeted manual UI verification

**Rationale**: This reduced feature is still UI-facing and localization-sensitive. Compile checks catch integration regressions, and manual verification confirms condition labels/options and the OCR engine label render correctly.

**Alternatives considered**: Relying only on compile validation was rejected because the user-visible wording and selector options still need visual confirmation.
