# Research: Sidebar Action

## Decision: Create a new sidebar action model

**Rationale**: The feature explicitly requires a new action and says to ignore existing similar code. A distinct model keeps the behavior independently evolvable, avoids hidden compatibility coupling with older actions, and makes editor/runtime behavior clear.

**Alternatives considered**:

- Extend an existing similar action: rejected because the requirement asks for a new action and because legacy assumptions could constrain the sidebar UI and interaction model.
- Represent the sidebar as a generic floating tool variant: rejected because the sidebar has distinct button gesture semantics and phone-vendor-style presentation requirements.

## Decision: Model sidebar buttons as ordered entries with icon, text, tap actions, and long-press actions

**Rationale**: The user needs to configure visible button identity and two independent gesture behaviors per button. Ordered entries directly support deterministic rendering, editing, export/import, and validation.

**Alternatives considered**:

- Store only one action per button: rejected because long-press behavior is required.
- Store a map keyed by button id: rejected for primary ordering because sidebar layout depends on explicit user-defined order.

## Decision: Use action lists for tap and long-press behavior

**Rationale**: Existing ShortX actions commonly compose multiple lower-level actions. Using action lists for each gesture lets a sidebar button perform a complete workflow without requiring users to create extra wrapper actions.

**Alternatives considered**:

- Store only a reference to an existing direct action: rejected because it would reduce configurability and require extra setup.
- Store only one nested action per gesture: rejected because it would be less consistent with ShortX's action composition model.

## Decision: Display sidebar through service-owned UI surface

**Rationale**: The sidebar is runtime UI shown by an action, potentially outside the normal app screen. Service-owned UI is the established ownership model for overlays, dialogs, floating controls, and other runtime surfaces.

**Alternatives considered**:

- Launch a main app screen: rejected because it would not behave like a phone-vendor-style sidebar and would interrupt the current context.
- Keep UI in the main `ui` module: rejected because runtime services should not depend on the main app flow for overlay-style surfaces.

## Decision: Prevent duplicate sidebar instances for repeated triggers

**Rationale**: Repeated triggers can otherwise stack overlapping sidebars and confuse users. A single visible session per action instance is easier to understand and satisfies the spec edge case.

**Alternatives considered**:

- Allow unlimited sidebars: rejected because overlapping runtime UI harms usability.
- Always replace the visible sidebar with a new instance: acceptable as an implementation detail, but the user-facing contract should only require no duplicate overlap.

## Decision: Use existing permission and failure-feedback conventions

**Rationale**: Sidebar display depends on runtime UI capabilities. Users should receive consistent recovery guidance when display is unavailable.

**Alternatives considered**:

- Silently ignore failed display: rejected because users cannot diagnose why the action did nothing.
- Block action creation if permission is missing: rejected because permission state can change between editing and runtime execution.
