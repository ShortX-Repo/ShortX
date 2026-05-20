# Feature Specification: Normalize UI Fixes

**Feature Branch**: `009-normalize-ui-fixes`  
**Created**: 2026-05-19  
**Status**: Draft  
**Input**: User description: "Normalize selected condition UI names/options without changing behavior; fix OCR Engine capitalization; improve editor search controls, shell independent run placement, code repository search, JS console output line actions, editor shortcut-bar dynamic coloring, duplicate initialization in method hook/context and command parameters, and paste cursor retention in shell/JS/MVEL editors."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Clearer Condition Selection (Priority: P1)

Users configuring automation conditions need selected state-style conditions to appear as normalized categories with explicit positive and negative options, so they can choose the intended state without learning that a missing inverse condition must be represented by negation.

**Why this priority**: The condition picker is core to rule creation. Ambiguous one-sided condition labels make rules harder to understand and maintain.

**Independent Test**: Open the condition picker and verify only the requested condition groups are renamed and expose the requested positive/negative choices while existing saved rules and rule execution behavior remain unchanged.

**Acceptance Scenarios**:

1. **Given** a user opens the screen-lock related condition, **When** the condition details are shown, **Then** the category is labeled with the localized equivalent of "screen lock state" and offers localized "locked" and "unlocked" choices.
2. **Given** a user selects "Unlocked" under screen lock state, **When** the rule is saved and reviewed, **Then** the condition represents the inverse of the existing locked condition without introducing a separate runtime behavior.
3. **Given** a user opens VPN, keep-screen-on, ringing, call, and orientation conditions, **When** the condition details are shown, **Then** they appear as state categories with the requested state choices and no unrelated conditions or actions are changed.

---

### User Story 2 - Consistent Editor Controls and Layout (Priority: P1)

Users editing shell, JS, or MVEL content need search controls, shortcut symbols, paste behavior, and shell independent-run controls to be visually consistent, easy to tap, and reliable during editing.

**Why this priority**: These editors are high-frequency authoring tools. Visual inconsistency and cursor jumps directly interrupt editing work.

**Independent Test**: Use each editor's search, paste, shortcut symbol bar, and shell independent-run control in light and dark modes, then verify controls are visible, accessible, styled consistently, and do not disrupt cursor position.

**Acceptance Scenarios**:

1. **Given** a user opens search in a shell, JS, or MVEL editor, **When** search actions such as previous, next, replace, and options are displayed, **Then** their text uses dynamic app coloring rather than pink styling and the controls have outlined button styling with visible press feedback.
2. **Given** search options are expanded, **When** options such as whole-word match and case sensitivity are visible, **Then** the expanded area has a polished container with appropriate rounding and remains readable in light and dark modes.
3. **Given** a user opens the shell editor, **When** the top controls are displayed, **Then** independent run appears to the right of the close control with a visible switch, not hidden inside a more-options menu.
4. **Given** a user pastes text in a shell, JS, or MVEL editor, **When** paste completes, **Then** the cursor remains at the expected paste location instead of jumping to the beginning of the content.

---

### User Story 3 - Working Search and Line Actions (Priority: P2)

Users browsing the code repository or reading JS console output need search and line actions to perform the action they selected instead of presenting inactive controls.

**Why this priority**: Broken search and inactive copy/share actions reduce trust and make diagnostics slower.

**Independent Test**: Search the code repository by author, title, and description, then operate on JS console output lines and verify copy, share, and line-relative actions produce the expected user-visible result.

**Acceptance Scenarios**:

1. **Given** the code repository page contains items with authors, titles, and descriptions, **When** a user enters matching text in the search box, **Then** matching repository items are shown and non-matching items are filtered out.
2. **Given** the code repository page search box is visible, **When** it is compared with the one-click automatic command page search box, **Then** it follows the same visual pattern and interaction behavior.
3. **Given** a JS console output line has an operation menu, **When** the user selects copy current line, share, or a from-current-line action, **Then** each selected operation performs a visible and useful result.

---

### User Story 4 - Correct Labels and Fresh Input Defaults (Priority: P3)

Users need minor labels and newly added input rows to be correct, predictable, and free from stale copied content.

**Why this priority**: These are smaller polish and correctness issues, but they prevent confusion in advanced authoring flows.

**Independent Test**: Verify the sidebar label, then add multiple method hook context expressions and command parameters and confirm newly added entries start empty or with the intended default instead of copying the previous entry.

**Acceptance Scenarios**:

1. **Given** a user views sidebar actions, **When** the OCR engine entry is displayed, **Then** it is labeled "OCR Engine" with the correct capitalization.
2. **Given** a user completes the first method hook context expression, **When** the user adds a second context expression, **Then** the new expression does not initialize with the just-finished expression content.
3. **Given** a user completes one command parameter, **When** the user adds another parameter, **Then** the new parameter does not initialize with the previous parameter content.

### Edge Cases

- Existing saved rules using the old screen-locked, VPN-connected, keep-screen-on, ringing, in-call, or portrait condition representations must remain readable and executable after the UI wording changes.
- The screen "unlocked" and orientation "landscape" selections must be represented through existing inverse or alternative state semantics without changing the current runtime rule engine behavior.
- Search boxes must handle empty queries by restoring the full repository list.
- Repository search must handle mixed case, partial words, and fields with missing author, title, or description text without hiding valid matches unexpectedly.
- Editor search controls and shortcut-bar backgrounds must remain readable in both light and dark modes and across dynamic color themes.
- Paste cursor retention must hold for empty documents, multi-line pasted text, selected text replacement, and paste near the start or end of the document.
- Console output line actions must handle empty lines, very long lines, and the final line in the output.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST rename the selected condition presentations as state-oriented categories using localized product text: screen locked to screen lock state, VPN connected to VPN state, keep screen on to screen-on state, ringing to ringing state, in call to call state, and portrait screen to screen orientation.
- **FR-002**: The screen lock state condition UI MUST offer localized locked and unlocked choices, where unlocked uses the inverse of the existing locked condition behavior.
- **FR-003**: The screen orientation condition UI MUST offer localized portrait and landscape choices while preserving the existing orientation behavior semantics.
- **FR-004**: The requested condition UI normalization MUST NOT change rule execution behavior, stored rule compatibility, or any unrelated condition and action presentations.
- **FR-005**: The sidebar OCR engine label MUST display as "OCR Engine" everywhere the affected sidebar entry is shown.
- **FR-006**: Shell, JS, and MVEL editor search action controls MUST use dynamic app coloring instead of pink text styling.
- **FR-007**: Shell, JS, and MVEL editor search action controls MUST have outlined-button styling and visible press feedback for actions such as previous, next, replace, and related search commands.
- **FR-008**: Expanded editor search options, including whole-word matching and case sensitivity, MUST be presented in a rounded, readable container that fits the surrounding editor UI.
- **FR-009**: The shell editor MUST show independent run next to the close control with a visible switch and MUST NOT require users to open a more-options menu to access it.
- **FR-010**: The code repository page MUST provide functional search over repository author, title, and description.
- **FR-011**: The code repository search box MUST match the visual and interaction pattern used by the one-click automatic command page search box.
- **FR-012**: JS console output line operations MUST make copy current line, share, and from-current-line actions functional with clear user-visible results.
- **FR-013**: Editor shortcut symbol bar backgrounds MUST use dynamic app coloring and remain readable in light and dark modes.
- **FR-014**: Adding a new method hook context expression MUST initialize the new entry independently rather than copying the previously edited expression content.
- **FR-015**: Adding a new command parameter MUST initialize the new entry independently rather than copying the previously edited parameter content.
- **FR-016**: Paste operations in shell, JS, and MVEL editors MUST preserve the expected cursor position after the pasted content is inserted or replaces a selection.
- **FR-017**: All visible user-facing text introduced or changed by this feature MUST be available through the product translation system for supported product locales.

### Key Entities *(include if feature involves data)*

- **Condition Presentation**: The user-facing name and selectable state options for an existing rule condition, including positive and inverse choices shown during rule authoring.
- **Editor Search Control**: The visible set of search, navigation, replacement, and option controls shown inside shell, JS, and MVEL editors.
- **Repository Search Query**: User-entered text used to filter code repository entries by author, title, and description.
- **Console Output Line Action**: A user operation performed against one selected JS console output line or the output range starting from that line.
- **New Input Row**: A newly added method hook context expression or command parameter entry that must start from an independent default value.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of the six requested condition groups display their normalized category names and expected state choices during rule authoring.
- **SC-002**: Existing saved rules using the affected conditions continue to open, display, save, and execute without behavior regression in manual verification.
- **SC-003**: Users can find a repository item by author, title, or description within one search attempt for at least 95% of representative repository entries.
- **SC-004**: In shell, JS, and MVEL editors, paste cursor placement remains correct in at least 99% of repeated paste attempts across empty, selected, and non-empty documents.
- **SC-005**: All tested editor search controls and shortcut-bar backgrounds remain readable and visibly interactive in both light and dark modes.
- **SC-006**: 100% of the requested JS console output line actions produce the selected visible result during manual verification.
- **SC-007**: Adding a second method hook context expression or command parameter never pre-fills it with the immediately previous row's completed content during repeated manual tests.

## Assumptions

- The requested condition normalization is limited to the six examples named by the user; other conditions and actions are intentionally out of scope for this feature.
- The user-facing wording will be localized according to the existing product translation process rather than only changing fallback text.
- "Monet" and dynamic app coloring refer to the app's existing dynamic color behavior, with readable fallbacks on devices or themes where dynamic colors are unavailable.
- Repository search is expected to be local to the currently displayed repository content unless a later planning step identifies an existing remote-search behavior.
- The JS console output "from current line" actions should operate on the selected line as the starting point and provide a clear visible result consistent with existing output operations.
