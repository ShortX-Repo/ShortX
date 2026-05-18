# Data Model: Sidebar Action

## Entity: SidebarAction

Represents the new configurable action that displays a floating sidebar.

### Fields

- `id`: inherited action identity from the containing action model.
- `buttons`: ordered list of `SidebarButton` entries.
- `dismissBehavior`: user-facing behavior for closing the sidebar. Default: close on outside tap or explicit close control.
- `position`: screen edge where the sidebar appears. Supported values: left, right, top, bottom. Default: left.
- `displayTitle`: optional descriptive label for editor and diagnostics.

### Validation Rules

- Must contain at least one valid `SidebarButton` before the action can be considered complete.
- Button order must be deterministic.
- Repeated trigger behavior must not create overlapping visible instances for the same action execution context.

### Relationships

- Contains many `SidebarButton` entries.
- Creates a `SidebarRuntimeSession` when executed.

## Entity: SidebarButton

Represents one visible shortcut button in the sidebar.

### Fields

- `id`: stable identifier for editing, reordering, and diagnostics.
- `icon`: user-selected visual icon.
- `text`: optional display label.
- `order`: position in the sidebar.
- `tapActions`: ordered action list executed on tap.
- `longPressActions`: ordered action list executed on long press.

### Validation Rules

- Must have an icon, text, or both so the button is identifiable.
- May have tap actions, long-press actions, or both.
- If both gesture action lists are empty, the editor should warn or prevent saving the button unless it is intentionally visual-only.
- Long labels must be safely truncated or wrapped in runtime UI.

### Relationships

- Belongs to one `SidebarAction`.
- References zero or more nested actions through `tapActions` and `longPressActions`.

## Entity: ButtonGestureAction

Represents actions assigned to one gesture on one sidebar button.

### Fields

- `gestureType`: tap or long press.
- `actions`: ordered action list.

### Validation Rules

- Tap and long-press gesture actions must remain independent.
- A long press must not also execute the tap action.
- Invalid nested actions must be reported without crashing or closing the sidebar unexpectedly.

## Entity: SidebarRuntimeSession

Represents the visible sidebar after a sidebar action runs.

### Fields

- `sourceActionId`: identity of the sidebar action that opened the session.
- `visibleButtons`: button list rendered for this session.
- `state`: visible, dismissing, dismissed, or failed.
- `failureMessage`: optional user-facing failure description.

### State Transitions

- `visible`: created when the sidebar is successfully shown.
- `dismissing`: entered when the user dismisses the sidebar or when replacement begins.
- `dismissed`: final state after the sidebar is closed.
- `failed`: used when the sidebar cannot be displayed due to missing permission or runtime capability.

### Validation Rules

- Only one visible runtime session should exist for the same sidebar action at a time.
- Dismissal must not trigger button tap or long-press actions.
