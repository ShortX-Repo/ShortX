# Quickstart: Sidebar Action

## Goal

Validate the new sidebar action from editing through runtime display and button gesture execution.

## Prerequisites

- A ShortX build with the sidebar action feature included.
- Runtime display/overlay capability enabled according to existing product requirements.
- At least two simple test actions available so tap and long-press results can be distinguished.

## Scenario 1: Create and display a sidebar

1. Create or edit a rule/direct action.
2. Add the new sidebar action.
3. Add three sidebar buttons.
4. Configure each button with distinct icon and text.
5. Select a sidebar position: left, right, top, or bottom.
6. Save the action.
7. Run the rule/direct action.
8. Verify that a compact sidebar appears from the configured screen edge and shows all configured buttons.

Expected result: the sidebar appears within 1 second, uses a side-panel style suitable for quick access, and slides in from the configured edge.

## Scenario 2: Verify tap and long-press behavior

1. Configure one sidebar button with one tap action and a different long-press action.
2. Run the sidebar action.
3. Tap the configured button.
4. Verify that only the tap action runs.
5. Run or reopen the sidebar again.
6. Long-press the same button.
7. Verify that only the long-press action runs.

Expected result: tap and long-press are independent and do not trigger each other.

## Scenario 3: Verify edit persistence

1. Reopen the sidebar action editor.
2. Verify that all buttons retain icon, text, order, tap action, and long-press action settings.
3. Reorder one button.
4. Save and run the action.
5. Verify the displayed order matches the editor order.
6. Change the sidebar position, save, reopen the editor, and verify the position persists.

Expected result: saved configuration is preserved and reflected at runtime.

## Scenario 4: Verify edge behavior

1. Trigger the sidebar action repeatedly while the sidebar is visible.
2. Verify that duplicate overlapping sidebars do not appear.
3. Configure a button with no long-press action.
4. Long-press that button.
5. Verify no action runs and the sidebar remains stable.

Expected result: repeated triggers and unconfigured gestures are handled gracefully.

## Suggested Validation Command

Run the affected UI/runtime compile tasks after implementation. At minimum:

```bash
./gradlew :ui:compileRowDebugKotlin
```

Add affected service/core-api validation when the action model or runtime actor changes.

## Validation Results

Validated on 2026-05-18 from `/Users/tornaco/Documents/workspace/ShortX`:

- `./gradlew :core-api:compileKotlin`: PASS
- `./gradlew :services:compileKotlin`: PASS
- `./gradlew :services-ui:compileReleaseKotlin`: PASS
- `./gradlew :ui:compileRowDebugKotlin`: PASS

Notes:

- `./gradlew :services-ui:compileKotlin` was attempted, but Gradle reported the task name is ambiguous for the Android module. `:services-ui:compileReleaseKotlin` was used as the concrete validation task.
- Runtime scenarios 1-4 require an Android device or runtime service session with overlay capability enabled. They were not manually executed in this shell-only environment.
