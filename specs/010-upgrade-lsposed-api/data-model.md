# Data Model: Upgrade LSPosed API 101

This feature does not introduce new persistent user-facing data. It preserves existing ShortX rule data while defining the runtime concepts that must remain stable after the API 101-only migration.

## Entity: Compatibility Mode

**Description**: Represents the supported Xposed runtime mode for the upgraded module.

**Fields**:

- `name`: Human-readable mode name. Required value for this feature: `LSPosed API 101`.
- `isSupported`: Whether this mode is supported by the upgraded release.
- `diagnosticLabel`: Label shown in compatibility diagnostics or documentation.

**Validation Rules**:

- Exactly one supported Xposed compatibility mode may be advertised.
- Legacy Xposed modes must not be marked supported.
- Diagnostics and documentation must match the supported mode.

**State Transitions**:

- Before upgrade: mixed or legacy-compatible support may be present.
- After upgrade: API 101-only support is active; legacy support is retired.

## Entity: Hook-Driven Event

**Description**: A runtime event produced by observing or intercepting target process behavior and delivered into the ShortX rule engine.

**Fields**:

- `sourceProcess`: System, SystemUI, or app process that produced the event.
- `sourcePackage`: Package associated with the observed behavior, when available.
- `lifecycle`: Runtime lifecycle phase where the event or hook was installed.
- `eventType`: Category such as system event, method hook, gesture, quick settings, or status bar behavior.
- `contextData`: Key-value data extracted from the observed behavior.
- `failure`: Optional diagnostic details if the event could not be delivered or the hook failed.

**Validation Rules**:

- Events must only be produced for intended scoped packages and processes.
- Event delivery must not require users to recreate existing rules.
- A failed event or hook must not block unrelated valid events.

**Relationships**:

- Feeds the existing runtime rule evaluation flow.
- May be produced from a Method Hook Configuration or SystemUI gesture behavior.

## Entity: Method Hook Configuration

**Description**: Existing user-authored configuration describing a target method behavior and context extraction rules.

**Fields**:

- `targetPackage`: Package where the hook should apply.
- `targetClass`: Class name selected by the user.
- `targetMethod`: Method name selected by the user.
- `triggerTiming`: Before-call or after-call trigger.
- `constraints`: Parameter count, parameter types, return type, lifecycle, or other matching constraints.
- `expressions`: User-configured context extraction expressions.

**Validation Rules**:

- Existing configurations remain valid after the upgrade.
- Configurations must not apply to the ShortX app itself.
- Target mismatches must be diagnostic events, not fatal failures.
- Expressions must retain access to method, receiver, arguments, result, throwable, and process/package context semantics.

**Relationships**:

- Produces Hook-Driven Events when the target behavior matches.
- Uses existing ShortX rule and direct-action data models.

## Entity: Target Process

**Description**: A process where ShortX may observe or intercept behavior.

**Fields**:

- `packageName`: Package associated with the process.
- `processName`: Concrete process name.
- `processType`: System server, SystemUI, ShortX app, or ordinary app.
- `isInScope`: Whether ShortX is intended to operate in the process.
- `isSelfProcess`: Whether the process belongs to ShortX.

**Validation Rules**:

- SystemUI-specific behavior only runs in the intended SystemUI process.
- App method hooks do not run against ShortX's own app process.
- Multi-process apps are handled according to intended package/process scope.

**Relationships**:

- Owns Hook-Driven Events generated in that process.
- Determines whether Method Hook Configurations can be applied.

## Entity: Validation Result

**Description**: Evidence that a build artifact behaves correctly after the API 101-only migration.

**Fields**:

- `artifactType`: Debug or release.
- `scenario`: Module loading, system event, method hook, SystemUI gesture, quick settings, status bar, or failure isolation.
- `environment`: Device and supported LSPosed API 101 environment details.
- `result`: Pass, fail, or blocked.
- `diagnostics`: Relevant logs or observations.

**Validation Rules**:

- Both debug and release artifacts must have validation results.
- At least one representative scenario must cover system server, SystemUI, and ordinary app processes.
- Release validation must confirm module recognition and hook-driven event delivery after process restart.

**Relationships**:

- Verifies Compatibility Mode.
- Verifies Hook-Driven Event behavior.
- Verifies preserved Method Hook Configuration behavior.
