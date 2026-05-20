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

## Localized Text Asset

Represents product text changed or introduced by this feature.

**Fields**:

- `locale`: Supported product locale such as en-US, zh-CN, or zh-TW.
- `key`: Translation key.
- `value`: Localized display string.

**Validation Rules**:

- All new or changed user-facing labels must be present in checked-in locale JSON assets.
- Keys must remain aligned across supported product locales.
