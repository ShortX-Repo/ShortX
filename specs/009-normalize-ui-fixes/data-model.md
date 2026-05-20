# Data Model: Normalize UI Fixes

## Condition Presentation

Represents the user-facing label and selectable state choices for an existing condition.

**Fields**:

- `conditionKind`: Existing condition identity such as screen lock, VPN, screen awake, ringing, call, or orientation.
- `categoryLabelKey`: Localized key for the normalized state-style category label.
- `stateChoices`: Positive and negative or alternate choices shown to the user.
- `modelMapping`: Existing condition data plus inverse flag or existing state value.
- `scope`: Indicates this presentation applies only to the six requested conditions.

**Validation Rules**:

- Must not create unrelated condition or action presentation changes.
- Must map screen unlocked to inverse screen locked behavior.
- Must map landscape to the existing orientation semantics without changing runtime evaluation.
- Must keep saved rules readable and editable.

**State Transitions**:

- Selector opens existing condition category.
- User selects state choice.
- UI maps choice to existing condition representation.
- Existing save/evaluate flow continues unchanged.

## Editor Search Control

Represents the visible search, navigation, replacement, and option controls inside shell, JS, and MVEL editors.

**Fields**:

- `editorType`: Shell, JS, or MVEL.
- `query`: Current search text.
- `actions`: Previous, next, replace, and related search commands.
- `options`: Whole-word matching, case sensitivity, and any existing search options.
- `visualState`: Normal, pressed, expanded options, light mode, or dark mode.

**Validation Rules**:

- Action text must not use pink-only styling.
- Action controls must have outlined-button styling and visible press feedback.
- Expanded options must be readable and rounded.
- Styling must remain readable in light and dark modes.

## Shell Independent Run Control

Represents the shell editor's independent-run mode toggle.

**Fields**:

- `enabled`: Whether independent run/single-shot mode is active according to the existing shell editor state.
- `visiblePlacement`: Top-bar location next to the close control.
- `controlType`: Visible switch control.

**Validation Rules**:

- Must be visible without opening a more-options menu.
- Must preserve the existing saved shell action behavior.
- Must continue to expose unrelated editor options through existing menus as appropriate.

## Repository Search Query

Represents text entered by a user to filter code repository items.

**Fields**:

- `keyword`: User-entered search text.
- `matchedFields`: Author, title, and description.
- `emptyQueryBehavior`: Shows the full repository list.
- `matchingMode`: Case-insensitive partial matching.

**Validation Rules**:

- Must search author, title, and description.
- Must tolerate missing or blank fields.
- Must reuse the visual pattern of the one-click automatic command page search box.

## Console Output Line Action

Represents an operation chosen for a JS console output line.

**Fields**:

- `lineIndex`: Selected output line position.
- `lineText`: Text for the selected line.
- `actionType`: Copy current line, share current line, or from-current-line operation.
- `result`: Clipboard update, share flow, or range action result visible to the user.

**Validation Rules**:

- Copy current line must copy exactly the selected line text.
- Share must share the selected line or documented line range.
- From-current-line actions must start from the selected line.
- Empty and final lines must be handled without failure.

## New Input Row

Represents a newly added method hook context expression or command parameter row.

**Fields**:

- `rowType`: Method hook context expression or command parameter.
- `defaultValue`: Empty or intended default value for a new row.
- `previousRowValue`: Previously edited row content, which must not leak into the new row.

**Validation Rules**:

- Adding a new row must not copy the previous completed row unless the user explicitly duplicates.
- The row must remain editable and save through the existing flow.

## Editor Paste Operation

Represents text insertion through paste in shell, JS, or MVEL editors.

**Fields**:

- `editorType`: Shell, JS, or MVEL.
- `prePasteSelection`: Cursor or selection range before paste.
- `pastedText`: Text inserted.
- `postPasteSelection`: Cursor position or selection range after paste.

**Validation Rules**:

- Cursor must remain after inserted text or at the expected replacement position.
- Cursor must not jump to the beginning after paste.
- Empty documents, selected text replacement, multi-line paste, and paste near document boundaries must be supported.

## Localized Text Asset

Represents product text changed or introduced by this feature.

**Fields**:

- `locale`: Supported product locale such as en-US, zh-CN, or zh-TW.
- `key`: Translation key.
- `value`: Localized display string.

**Validation Rules**:

- All new or changed user-facing labels must be present in checked-in locale JSON assets.
- Keys must remain aligned across supported product locales.
