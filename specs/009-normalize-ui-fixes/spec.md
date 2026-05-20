# Feature Specification: Normalize UI Fixes

**Feature Branch**: `009-normalize-ui-fixes`  
**Created**: 2026-05-19  
**Status**: Draft  
**Input**: User description: "Normalize selected condition UI names/options without changing behavior, and fix OCR Engine capitalization in the sidebar/settings UI."

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

### User Story 2 - Correct OCR Engine Label (Priority: P2)

Users need the OCR engine entry in sidebar/settings-related UI to use the correct product capitalization.

**Why this priority**: This is a small but user-visible terminology correction that improves consistency.

**Independent Test**: Open the affected OCR engine entry and verify it displays `OCR Engine` consistently.

**Acceptance Scenarios**:

1. **Given** a user views sidebar actions, **When** the OCR engine entry is displayed, **Then** it is labeled "OCR Engine" with the correct capitalization.

### Edge Cases

- Existing saved rules using the old screen-locked, VPN-connected, keep-screen-on, ringing, in-call, or portrait condition representations must remain readable and executable after the UI wording changes.
- The screen "unlocked" and orientation "landscape" selections must be represented through existing inverse or alternative state semantics without changing the current runtime rule engine behavior.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST rename the selected condition presentations as state-oriented categories using localized product text: screen locked to screen lock state, VPN connected to VPN state, keep screen on to screen-on state, ringing to ringing state, in call to call state, and portrait screen to screen orientation.
- **FR-002**: The screen lock state condition UI MUST offer localized locked and unlocked choices, where unlocked uses the inverse of the existing locked condition behavior.
- **FR-003**: The screen orientation condition UI MUST offer localized portrait and landscape choices while preserving the existing orientation behavior semantics.
- **FR-004**: The requested condition UI normalization MUST NOT change rule execution behavior, stored rule compatibility, or any unrelated condition and action presentations.
- **FR-005**: The sidebar OCR engine label MUST display as "OCR Engine" everywhere the affected sidebar entry is shown.
- **FR-006**: All visible user-facing text introduced or changed by this feature MUST be available through the product translation system for supported product locales.

### Key Entities *(include if feature involves data)*

- **Condition Presentation**: The user-facing name and selectable state options for an existing rule condition, including positive and inverse choices shown during rule authoring.
- **Localized Text Asset**: Checked-in translation keys and values used to display normalized condition labels and the OCR Engine label.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of the six requested condition groups display their normalized category names and expected state choices during rule authoring.
- **SC-002**: Existing saved rules using the affected conditions continue to open, display, save, and execute without behavior regression in manual verification.
- **SC-003**: 100% of affected OCR engine entries display `OCR Engine` with consistent capitalization during manual verification.

## Assumptions

- The requested condition normalization is limited to the six examples named by the user; other conditions and actions are intentionally out of scope for this feature.
- The user-facing wording will be localized according to the existing product translation process rather than only changing fallback text.
