# Contracts: Sidebar Action

## Action Configuration Contract

The sidebar action must be selectable and editable wherever normal configurable actions are supported.

### Inputs

- Sidebar action title or editor context inherited from the parent action editing flow.
- Ordered list of sidebar buttons.
- Sidebar screen position: left, right, top, or bottom.
- For each button: icon, optional text, optional tap action list, optional long-press action list.

### Outputs

- A saved sidebar action configuration that can be reopened, edited, exported, imported, and executed.
- Validation feedback when required button configuration is missing or invalid.

### Acceptance Rules

- Saving with no buttons must be blocked or produce clear validation feedback.
- Reopening the editor must preserve the configured sidebar position.
- Reopening the editor must preserve button icon, text, order, tap actions, and long-press actions.
- Export/import must preserve all sidebar button configuration.

## Runtime Display Contract

When the sidebar action executes, the runtime must show a compact sidebar panel.

### Inputs

- A valid sidebar action configuration.
- Current runtime display capability and permission state.

### Outputs

- A visible sidebar session, or a user-facing failure message when display is unavailable.

### Acceptance Rules

- Valid configuration should show within 1 second under normal runtime conditions.
- The sidebar must appear from the configured screen edge and dismiss with a matching reverse slide animation.
- Repeated triggers must not create overlapping duplicate sidebars for the same action.
- The user must be able to dismiss the sidebar reliably.
- Sidebar content must remain accessible with up to 12 configured buttons.

## Button Gesture Contract

Each visible sidebar button exposes two independent interaction paths.

### Tap

- Trigger: user taps the button.
- Result: execute only the configured tap action list.
- If no tap action is configured, no action runs and the sidebar remains stable.

### Long Press

- Trigger: user long-presses the button.
- Result: execute only the configured long-press action list.
- If no long-press action is configured, no action runs and the sidebar remains stable.

### Error Handling

- If a nested action is invalid or unavailable, show user-facing feedback and keep the sidebar runtime stable.
- Gesture execution must not crash or freeze the app.
