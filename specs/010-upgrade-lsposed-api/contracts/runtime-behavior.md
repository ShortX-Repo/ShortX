# Runtime Behavior Contract: Upgrade LSPosed API 101

This contract describes observable behavior that must hold after the API 101-only migration. It is intentionally written as runtime behavior rather than implementation API documentation.

## Contract 1: Module Compatibility

### Supported Behavior

- The module is recognized and loadable in supported LSPosed API 101 environments.
- The module reports API 101-only compatibility in diagnostics and documentation.
- The module does not advertise legacy Xposed compatibility.

### Unsupported Behavior

- Legacy-only Xposed environments are not supported.
- No legacy fallback mode is part of the supported product surface.

### Acceptance Checks

- Installing and enabling the module in a supported API 101 environment loads the module after target process restart.
- Inspecting compatibility documentation and diagnostics shows API 101-only support.
- Inspecting release metadata shows no claim that legacy mode is supported.

## Contract 2: Scope And Process Safety

### Supported Behavior

- Hook-driven behavior only applies to intended package and process scopes.
- SystemUI-specific behavior only runs in the intended SystemUI process.
- User-configured app method hooks do not apply to ShortX's own app process.
- Multi-process targets do not cause global uncontrolled hook installation.

### Failure Behavior

- If a target class, method, process, or lifecycle does not match, the module records diagnostics and continues unrelated behavior.
- A failure in one hook point does not block unrelated hook-driven events in the same validation run.

### Acceptance Checks

- A SystemUI scenario initializes only in the SystemUI main process.
- A user-configured method hook targeting an ordinary app does not run against ShortX itself.
- A missing target method produces diagnostics without breaking other tested automations.

## Contract 3: Method Hook Event Delivery

### Supported Behavior

- Existing method-hook configurations remain usable without user recreation.
- Before-call and after-call triggers are both supported.
- Context extraction preserves access to method, receiver, arguments, result, throwable, and process/package context semantics.
- Matching method-hook events are delivered into the existing ShortX runtime rule evaluation flow.

### Failure Behavior

- Invalid targets or changed target signatures are reported as diagnostics.
- Invalid or failing context extraction does not crash target processes.
- Other method-hook rules continue to be evaluated when one rule fails.

### Acceptance Checks

- A representative before-call method-hook rule triggers and publishes context data.
- A representative after-call method-hook rule triggers and publishes context data.
- A configured expression reading arguments and result preserves equivalent user-visible behavior after the upgrade.

## Contract 4: SystemUI Behavior

### Supported Behavior

- SystemUI app context and resources are initialized after SystemUI startup.
- Runtime readiness is respected before installing global gesture input behavior.
- Existing gesture recognition, gesture interception where configured, quick settings behavior, tile customizations, and status bar automations remain available.

### Failure Behavior

- If ShortX runtime services are not ready when SystemUI starts, the module waits or reports diagnostics rather than installing behavior in an invalid state.
- SystemUI-specific failures do not cause repeated user-facing SystemUI crashes.

### Acceptance Checks

- Restarting SystemUI initializes ShortX SystemUI behavior in the expected order.
- Representative edge gesture and corner gesture rules fire after the upgrade.
- Representative quick settings and status bar scenarios behave consistently in debug and release validation.

## Contract 5: Release Artifact Safety

### Supported Behavior

- Debug and release artifacts both retain the metadata required for API 101 module recognition.
- Release artifacts retain the runtime callback entry points required for hook-driven behavior.
- Release behavior matches debug behavior for representative validation scenarios.

### Acceptance Checks

- Release installation is recognized by the supported environment.
- After one target-process restart cycle, release validation confirms module loading and hook-driven event delivery.
- Release validation confirms no legacy compatibility claim remains.
