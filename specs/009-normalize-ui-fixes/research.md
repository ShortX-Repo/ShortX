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

## Decision: Reuse shared search UI for the repository page

**Rationale**: The project already has `SearchBar`, `SearchBarState`, and usage in one-click/direct-action selector flows. Reusing the same visible search component is the lowest-risk path to make the code repository search box match adjacent product pages.

**Alternatives considered**: Building a custom repository-only search field was rejected because the spec asks the repository page to match an existing page's search UI.

## Decision: Improve editor search and shortcut bar through existing editor host/wrapper surfaces

**Rationale**: Shell, JS, and MVEL editors share code editor host patterns and Sora editor integration. Styling and paste cursor retention should be fixed in the shared editor wrapper or common editor UI where possible, then verified across all three editors.

**Alternatives considered**: Patching each editor independently was rejected unless discovery shows one editor has unique behavior, because duplicated fixes would increase inconsistency risk.

## Decision: Restore shell independent run as a top-bar switch next to close

**Rationale**: The user describes this as a regression from prior behavior. Restoring the switch near the close control makes the state visible and avoids hiding a mode-setting action inside a menu.

**Alternatives considered**: Keeping the action in the more menu was rejected because it conflicts with the requested behavior and hides persistent execution mode state.

## Decision: Define JS console output line actions by visible result

**Rationale**: The current complaint is that actions are exposed but inactive. The contract should require copy, share, and from-current-line actions to produce visible effects such as clipboard update, share sheet, or range operation output. Exact internal routing can follow existing UI utilities during implementation.

**Alternatives considered**: Removing inactive actions was rejected because the user asked to complete them, not remove them.

## Decision: Treat method hook expressions and command parameters as fresh input rows

**Rationale**: Newly added rows should not inherit recently edited content unless explicitly duplicating. This is a state-isolation bug in repeated input flows and should be fixed at row creation or state reset boundaries.

**Alternatives considered**: Keeping previous-content prefill as a productivity shortcut was rejected because it is described as unintended behavior and affects two separate flows.

## Decision: Validation should combine compile checks with targeted manual UI verification

**Rationale**: The feature is largely UI styling and interaction behavior, including light/dark dynamic colors and editor cursor placement. Compile checks catch integration regressions, but acceptance depends on manual or UI-level behavior verification.

**Alternatives considered**: Relying only on unit tests was rejected because dynamic visual behavior and embedded editor cursor behavior are difficult to validate meaningfully without UI interaction.
