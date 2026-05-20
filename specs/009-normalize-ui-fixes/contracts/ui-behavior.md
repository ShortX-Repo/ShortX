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

## Editor Search Styling Contract

**Given** search is expanded in shell, JS, or MVEL editor  
**When** previous, next, replace, or search option controls are visible  
**Then** controls use dynamic app color styling, outlined button affordance, and visible press feedback  
**And** expanded options are shown inside a rounded readable container.

## Shell Independent Run Contract

**Given** the shell editor is open  
**When** the top bar is displayed  
**Then** independent run is visible next to the close control as a switch  
**And** independent run is not hidden exclusively inside more options.

## Repository Search Contract

**Given** repository items are loaded  
**When** the user types a keyword  
**Then** items match if author, title, or description contains the keyword  
**And** matching is case-insensitive and partial  
**And** empty keyword restores the full list  
**And** the search box matches the one-click automatic command page visual pattern.

## JS Console Output Line Action Contract

**Given** a console output line operation menu is open  
**When** the user chooses copy current line  
**Then** exactly that line's text is available for paste and the user receives visible confirmation.

**Given** a console output line operation menu is open  
**When** the user chooses share  
**Then** a share flow opens with the selected line content or documented selected range.

**Given** a console output line operation menu is open  
**When** the user chooses a from-current-line action  
**Then** the action starts at the selected line and handles the final line without failure.

## Fresh Row Initialization Contract

**Given** the user has edited a method hook context expression or command parameter  
**When** the user adds another row  
**Then** the new row starts with an independent default value  
**And** it does not copy the previous row's completed content unless the user explicitly duplicates.

## Editor Paste Cursor Contract

**Given** a shell, JS, or MVEL editor has focus  
**When** the user pastes text into the editor  
**Then** the cursor remains at the expected post-paste location  
**And** the cursor does not reset to the beginning for empty documents, selected text replacement, multi-line paste, or boundary paste positions.
