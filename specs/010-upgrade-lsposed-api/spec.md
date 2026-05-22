# Feature Specification: Upgrade LSPosed API 101

**Feature Branch**: `010-upgrade-lsposed-api`  
**Created**: 2026-05-21  
**Status**: Draft  
**Input**: User description: "升级项目的LSposed API 到101版本，完全废弃legacy模式的支持。参考这两个文档里整理的信息： Xposed-API-101-Upgrade-Guide.md Xposed-API-101-Upgrade-Tasks.md"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Run Module On API 101 Only (Priority: P1)

As a ShortX user on a supported LSPosed environment, I want the module to be recognized and loaded through the API 101 mode only, so that the automation engine runs on the current supported platform without legacy compatibility ambiguity.

**Why this priority**: This is the core outcome of the upgrade. If the module cannot be recognized and loaded in the supported environment, no other automation behavior matters.

**Independent Test**: Can be fully tested by installing the upgraded module on a supported LSPosed API 101 environment, enabling the expected scope, restarting the target processes, and confirming the module loads without using any legacy compatibility path.

**Acceptance Scenarios**:

1. **Given** a device with a supported LSPosed API 101 environment and the upgraded ShortX module installed, **When** the user enables the module and restarts target processes, **Then** ShortX is recognized as a module and enters the supported runtime path.
2. **Given** a device that only supports the retired legacy Xposed mode, **When** the upgraded module is installed or enabled, **Then** the module does not claim or provide legacy compatibility.
3. **Given** the user reviews module metadata and diagnostics, **When** the upgraded module is active, **Then** the reported support mode indicates API 101-only support.

---

### User Story 2 - Preserve Existing Automation Behavior (Priority: P1)

As an existing ShortX user, I want my existing rules, direct actions, method-hook rules, SystemUI gestures, quick settings behavior, and system-event automations to continue working after the upgrade, so that the platform migration does not break my current workflows.

**Why this priority**: The upgrade is only acceptable if existing user-facing automation capabilities remain intact. The Xposed layer is an event source for the runtime engine, so regressions here would break core product value.

**Independent Test**: Can be tested by running a representative set of existing automations before and after the upgrade and confirming the same user-visible outcomes occur.

**Acceptance Scenarios**:

1. **Given** existing enabled rules that depend on system events, **When** those events occur after the upgrade, **Then** the same rules are evaluated and actions are executed as before.
2. **Given** existing method-hook rules configured by the user, **When** the matching target behavior occurs after the upgrade, **Then** ShortX records and publishes the hook-driven event with the configured context data.
3. **Given** existing SystemUI gesture rules, **When** the user performs configured gestures after the upgrade, **Then** the matched gesture rules fire without requiring the user to recreate rules.
4. **Given** existing quick settings and status bar customizations, **When** SystemUI is restarted after the upgrade, **Then** the customizations remain available and consistent with the user's settings.

---

### User Story 3 - Remove Legacy Support Surface (Priority: P2)

As a maintainer, I want legacy Xposed support to be fully retired from the supported product surface, so that the module has one supported runtime mode and future changes do not need to preserve legacy behavior.

**Why this priority**: The user explicitly requires complete legacy deprecation. This reduces support burden and eliminates ambiguous dual-mode behavior.

**Independent Test**: Can be tested by reviewing product behavior, release metadata, diagnostics, and compatibility documentation to confirm no legacy mode is advertised or required.

**Acceptance Scenarios**:

1. **Given** the upgraded release artifacts, **When** maintainers inspect compatibility metadata and release documentation, **Then** legacy Xposed support is not listed as supported.
2. **Given** users read the upgrade documentation, **When** they check compatibility requirements, **Then** they can clearly understand that API 101 support is required.
3. **Given** the upgraded module runs in supported environments, **When** runtime diagnostics are viewed, **Then** no legacy-mode status or fallback path is presented as active.

---

### User Story 4 - Validate Safe Release Behavior (Priority: P2)

As a release owner, I want a clear validation path for debug and release builds, so that the upgraded module can be shipped without silent runtime failures in injected processes.

**Why this priority**: Xposed-related failures can occur only after installation, process restart, or release packaging. Explicit validation protects users from hard-to-diagnose regressions.

**Independent Test**: Can be tested by running the defined compatibility scenarios on both debug and release builds and checking module loading, rule events, diagnostics, and absence of target-process crashes.

**Acceptance Scenarios**:

1. **Given** a debug build and a release build of the upgraded module, **When** both are installed in supported environments, **Then** both builds load the module and trigger the same representative automation scenarios.
2. **Given** a hook installation or callback failure in one target behavior, **When** other configured automations are triggered, **Then** unrelated automations continue to work and diagnostics identify the failed behavior.
3. **Given** target processes restart after module activation, **When** ShortX initializes in those processes, **Then** initialization failures are visible in diagnostics and do not cause repeated user-facing crashes.

### Edge Cases

- Device has a legacy-only Xposed environment and no API 101 support.
- User upgrades from a previous ShortX release with existing rules, direct actions, method-hook rules, gesture settings, and quick settings customizations.
- Target app or system process is multi-process and only some processes are in scope.
- Target app class or method no longer exists after an app update.
- Method-hook expressions reference parameter, result, current package, or current application context data.
- SystemUI restarts while ShortX services are not yet ready.
- One hook point fails to install while other hook points in the same process are valid.
- Release packaging removes or obscures runtime metadata needed for module loading.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The upgraded module MUST support LSPosed API 101 as the only supported Xposed runtime mode.
- **FR-002**: The upgraded module MUST NOT advertise, expose, or depend on legacy Xposed compatibility as a supported mode.
- **FR-003**: Existing user rules, direct actions, toggles, method-hook configurations, gesture settings, and quick settings customizations MUST remain usable without requiring user recreation after the upgrade.
- **FR-004**: The system MUST continue to deliver hook-driven events from system processes, SystemUI, and ordinary app processes into the existing ShortX runtime rule evaluation flow.
- **FR-005**: The system MUST preserve user-configured method-hook behavior, including before-call triggers, after-call triggers, argument access, return-value access, and configured expression context values.
- **FR-006**: The system MUST preserve SystemUI-specific behavior that users can observe, including gesture recognition, pointer interception behavior where configured, quick settings behavior, tile customizations, and status bar related automation.
- **FR-007**: The system MUST prevent ShortX from applying user-configured app method hooks to the ShortX app itself.
- **FR-008**: The system MUST handle target class, method, process, or lifecycle mismatches gracefully by recording diagnostics and continuing unrelated automation behavior.
- **FR-009**: The system MUST clearly indicate in compatibility documentation and diagnostics that legacy Xposed environments are unsupported after this upgrade.
- **FR-010**: The system MUST support validation of both debug and release artifacts for module recognition, process loading, hook-driven event delivery, and user-facing automation behavior.
- **FR-011**: The system MUST ensure release artifacts retain all metadata required for API 101 module recognition and runtime callback execution.
- **FR-012**: The system MUST update project-facing upgrade documentation so future maintainers understand the API 101-only support boundary and the removed legacy support surface.
- **FR-013**: The system MUST avoid repeated target-process crashes caused by ShortX callback failures; failures in one hook-driven behavior must not block unrelated hook-driven behaviors.
- **FR-014**: The system MUST preserve existing scope behavior so only intended target packages and processes are affected by ShortX automation.

### Key Entities *(include if feature involves data)*

- **Compatibility Mode**: The supported runtime mode for the module. For this feature, the only supported mode is LSPosed API 101.
- **Hook-Driven Event**: An observed or intercepted behavior from a target process that is converted into a ShortX runtime event for rule evaluation.
- **Method Hook Configuration**: User-created configuration that identifies a target behavior, trigger timing, constraints, and expression-derived context data.
- **Target Process**: A system, SystemUI, or app process where ShortX may observe behavior according to configured scope and runtime rules.
- **Validation Result**: Evidence that a debug or release artifact loads correctly, preserves automation behavior, and reports failures without causing unrelated regressions.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of supported module-loading scenarios use the API 101-only mode, with zero supported scenarios relying on legacy compatibility.
- **SC-002**: At least 95% of representative pre-upgrade automation scenarios complete with the same user-visible result after the upgrade.
- **SC-003**: Existing user rules and direct actions require zero manual recreation as part of the upgrade.
- **SC-004**: Representative method-hook rules successfully trigger in both before-call and after-call scenarios during validation.
- **SC-005**: Representative SystemUI gesture, quick settings, and status bar scenarios pass in both debug and release validation.
- **SC-006**: A single hook installation or callback failure does not prevent unrelated validated automations from executing in the same release test run.
- **SC-007**: Release validation confirms module recognition and runtime event delivery within one target-process restart cycle.
- **SC-008**: Compatibility documentation and diagnostics contain no claim that legacy Xposed mode remains supported.

## Assumptions

- API 101 support is the required compatibility baseline for this upgrade.
- Legacy Xposed support is intentionally removed rather than maintained as a fallback.
- Existing ShortX user data models for rules, direct actions, method-hook configurations, gestures, and quick settings remain conceptually valid.
- Existing runtime rule evaluation behavior remains the source of truth; this feature changes how external events enter that runtime, not how rules are authored by users.
- Supported validation includes system process, SystemUI process, and ordinary app process scenarios because all three are part of current ShortX behavior.
- Environments without API 101 support are out of scope for compatibility guarantees after this feature.
