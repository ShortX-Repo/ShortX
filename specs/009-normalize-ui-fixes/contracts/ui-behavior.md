# UI Behavior Contracts: Normalize UI Fixes

This feature has no external service API, AIDL, protobuf, or network contract changes. The contracts below define observable UI behavior that implementation and validation must preserve.

## Condition Presentation Contract

**Given** the condition selector or an existing rule condition summary is displayed  
**When** the condition is one of the six requested normalized conditions  
**Then** the UI displays the normalized localized category label and supported state choice  
**And** the saved condition continues to use the existing condition semantics  
**And** unrelated condition and action labels remain unchanged.

Required mappings:

| Existing Presentation | Normalized Category | Required Choices |
|-----------------------|---------------------|------------------|
| Screen locked | Screen lock state | Locked, unlocked |
| VPN connected | VPN state | Connected, disconnected |
| Keep screen on | Screen-on state | Keeping awake, not keeping awake |
| Ringing | Ringing state | Ringing, not ringing |
| In call | Call state | In call, not in call |
| Portrait screen | Screen orientation | Portrait, landscape |

## OCR Engine Label Contract

**Given** the affected OCR engine entry is displayed in settings or sidebar-related UI  
**When** the user views the label  
**Then** it is written as `OCR Engine`  
**And** the capitalization is consistent anywhere this specific entry is shown.
