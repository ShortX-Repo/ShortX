# Feature Specification: Sidebar Action

**Feature Branch**: `008-add-sidebar-action`  
**Created**: 2026-05-18  
**Status**: Draft  
**Input**: User description: "构建一个新功能：侧边栏动作，参考手机厂商自带的侧边栏的操作和UI，添加一个新的展示侧边栏动作（现有代码里已经有类似动作，忽略现有代码，新做一个）；动作支持用户配置侧边栏的按钮，按钮图标，文本，每个按钮可配置点击、长按对应的执行的动作（配置部分可参考现有动作：显示悬浮工具）。"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Show a configurable sidebar (Priority: P1)

As an automation user, I want to add a new action that shows a system-style sidebar so I can quickly access frequently used ShortX actions from a compact floating panel.

**Why this priority**: Showing the sidebar is the core value of the feature. Without a reliable sidebar display action, button configuration and gesture behavior cannot deliver user value.

**Independent Test**: Can be fully tested by creating a rule or direct action that runs the new sidebar action and verifying that a sidebar appears with the configured button list.

**Acceptance Scenarios**:

1. **Given** a user has configured a sidebar action with at least one button, **When** the action runs, **Then** a sidebar appears on screen using a phone-vendor-inspired visual style and displays the configured buttons.
2. **Given** the sidebar is visible, **When** the user taps outside the sidebar or uses the provided close behavior, **Then** the sidebar closes without running unintended button actions.
3. **Given** the sidebar is visible over another app or screen, **When** the user interacts with the sidebar, **Then** the sidebar remains responsive and does not block unrelated screen areas beyond its displayed panel.

---

### User Story 2 - Configure sidebar buttons (Priority: P2)

As a user creating the sidebar action, I want to configure which buttons appear in the sidebar, including each button's icon and text, so the sidebar matches my preferred shortcuts.

**Why this priority**: A sidebar without configurable buttons would be static and less useful. Button configuration makes the action personal and reusable across workflows.

**Independent Test**: Can be tested by configuring several buttons with distinct icons and labels, saving the action, and confirming that the displayed sidebar matches the saved configuration.

**Acceptance Scenarios**:

1. **Given** the user is editing the sidebar action, **When** the user adds a button with icon, text, tap action, and long-press action, **Then** the button is saved as part of the sidebar action configuration.
2. **Given** a sidebar action has multiple configured buttons, **When** the user reopens the editor, **Then** all buttons appear with their saved icon, text, order, tap action, and long-press action settings.
3. **Given** a button has no custom label, **When** the sidebar is displayed, **Then** the system uses a sensible fallback label or icon-only presentation without breaking the layout.

---

### User Story 3 - Execute button tap and long-press actions (Priority: P3)

As a user operating the sidebar, I want each button to run one action when tapped and another action when long-pressed so a compact sidebar can expose multiple related operations.

**Why this priority**: Separate tap and long-press behaviors make the sidebar powerful, but they depend on the sidebar display and button configuration flows.

**Independent Test**: Can be tested by assigning visibly different actions to tap and long-press for one button, then confirming that each gesture runs only its assigned action.

**Acceptance Scenarios**:

1. **Given** a sidebar button has a tap action configured, **When** the user taps the button, **Then** only the tap action is executed.
2. **Given** a sidebar button has a long-press action configured, **When** the user long-presses the button, **Then** only the long-press action is executed.
3. **Given** a button has only one gesture action configured, **When** the user uses the unconfigured gesture, **Then** no action runs and the sidebar remains stable.

---

### Edge Cases

- If the sidebar action has no buttons configured, the editor must prevent saving or the runtime must show a clear empty-state message rather than displaying a broken sidebar.
- If a configured button action is unavailable, deleted, or invalid, the sidebar must keep displaying and report the affected button action failure without crashing.
- If button labels are long, the sidebar must truncate or wrap them consistently without pushing buttons outside the visible panel.
- If many buttons are configured, the sidebar must remain usable through scrolling or compact layout behavior.
- If both tap and long-press are configured, the system must not accidentally run the tap action before a long-press action.
- If the sidebar is triggered repeatedly while already visible, the system must avoid duplicate overlapping sidebars.
- If overlay/display permission or required runtime capability is missing, the user must receive a clear explanation and a recovery path.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide a new action type for displaying a sidebar, separate from any existing similar action.
- **FR-002**: Users MUST be able to add the new sidebar action anywhere actions can normally be selected and configured.
- **FR-003**: Users MUST be able to configure one or more sidebar buttons for the sidebar action.
- **FR-004**: Each sidebar button MUST support a user-configured icon.
- **FR-005**: Each sidebar button MUST support user-configured display text.
- **FR-006**: Each sidebar button MUST support an optional tap action.
- **FR-007**: Each sidebar button MUST support an optional long-press action.
- **FR-008**: Users MUST be able to review and edit saved sidebar button configuration after creating the action.
- **FR-009**: Users MUST be able to reorder sidebar buttons before saving the sidebar action.
- **FR-010**: System MUST display the sidebar in a compact, phone-vendor-inspired floating side panel style.
- **FR-011**: System MUST visually present each configured button with its saved icon and text when the sidebar is displayed.
- **FR-012**: System MUST execute the configured tap action when a user taps a sidebar button.
- **FR-013**: System MUST execute the configured long-press action when a user long-presses a sidebar button.
- **FR-014**: System MUST keep tap and long-press behaviors distinct so a long press does not also trigger the tap behavior.
- **FR-015**: System MUST provide a reliable way for users to dismiss the sidebar.
- **FR-016**: System MUST avoid displaying multiple overlapping instances of the same sidebar action when the action is triggered repeatedly.
- **FR-017**: System MUST handle invalid, missing, or unconfigured button actions gracefully without closing the sidebar unexpectedly.
- **FR-018**: System MUST preserve configured sidebar action data across app restarts, export/import, and action editing flows consistent with other configurable actions.
- **FR-019**: System MUST present clear user-facing feedback when the sidebar cannot be displayed due to missing permission or runtime capability.
- **FR-020**: System MUST keep the action editor experience consistent with the existing configurable action editor pattern used for floating tool-style actions.

### Key Entities *(include if feature involves data)*

- **Sidebar Action**: A configurable action that displays a floating sidebar. Key attributes include title/identity inherited from normal action configuration, button list, and display behavior.
- **Sidebar Button**: A single shortcut entry inside the sidebar. Key attributes include icon, text, order, tap action, and long-press action.
- **Button Gesture Action**: The action assigned to a button gesture. Each sidebar button may have zero or one tap action and zero or one long-press action.
- **Sidebar Runtime Session**: A visible instance of the sidebar shown after the action runs. It tracks visibility, dismissal, and user interaction results.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can create a sidebar action with three configured buttons in under 3 minutes during usability testing.
- **SC-002**: At least 95% of sidebar action executions with valid configuration show the sidebar within 1 second of being triggered.
- **SC-003**: At least 95% of tap and long-press interactions execute the intended configured action without triggering the other gesture action.
- **SC-004**: The sidebar remains usable with at least 12 configured buttons without content becoming inaccessible.
- **SC-005**: At least 90% of users in a task test can identify how to dismiss the sidebar without external instruction.
- **SC-006**: Invalid or missing button actions produce user-visible feedback and do not crash or freeze the app in 100% of tested cases.

## Assumptions

- The feature targets users who already understand ShortX actions, rules, and direct actions.
- The new sidebar action is a separate new action and does not modify or replace any existing similar action.
- The visual style should be inspired by common phone-vendor sidebars: compact side panel, vertically arranged shortcut buttons, icon-first presentation, and quick dismiss behavior.
- Button configuration can reuse the same conceptual action-selection experience used by existing configurable actions.
- Tap action and long-press action are optional independently, but a sidebar button should have at least one meaningful visual identifier through icon, text, or both.
- Export/import behavior should follow the same expectations as other user-configured actions.
- Permission and runtime capability handling should follow existing product conventions for user-facing recovery guidance.
