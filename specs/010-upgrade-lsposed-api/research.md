# Research: Upgrade LSPosed API 101

## Decision 1: API 101 Is The Only Supported Xposed Runtime Mode

**Decision**: Treat LSPosed API 101 as the only supported Xposed runtime mode for this feature. Remove legacy Xposed mode from supported metadata, documentation, diagnostics, and compatibility claims.

**Rationale**: The feature request explicitly requires upgrading to API 101 and completely abandoning legacy support. Keeping any supported fallback would preserve the ambiguity this feature is meant to remove. The project already has a modern LSPosed entry point, so planning should consolidate around that path rather than maintaining dual support.

**Alternatives considered**:

- Keep dual API support temporarily: rejected because the user explicitly requested complete legacy removal.
- Keep legacy code hidden but unsupported: rejected for planning because it risks accidental use and makes validation ambiguous.
- Split into two release artifacts: rejected because the requested product behavior is API 101-only support, not parallel distribution.

## Decision 2: Preserve User Data And Runtime Rule Semantics

**Decision**: Keep existing user rule, direct action, method-hook, gesture, quick settings, and status bar configuration semantics stable. The migration changes the injected-process runtime path, not the user-authored automation model.

**Rationale**: The spec requires existing automations to remain usable without manual recreation. The AI architecture notes identify Xposed as an event/control source feeding the existing `services` runtime. Therefore, the safest user-facing plan is to preserve runtime event shapes and rule evaluation behavior while replacing how external events are observed and delivered.

**Alternatives considered**:

- Migrate user configurations to a new method-hook model: rejected because it would introduce unnecessary data migration risk.
- Require users to recreate method-hook rules: rejected by FR-003 and SC-003.
- Limit preservation to system hooks only: rejected because SystemUI gestures and app method hooks are core existing behaviors.

## Decision 3: Use A Project-Level Hook Parameter Contract

**Decision**: Plan for a project-level hook parameter contract that preserves the user-visible method-hook context fields: method, receiver object, arguments, result, throwable, trigger timing, and process/package context.

**Rationale**: Existing dynamic method-hook rules and MVEL expressions depend on parameter/result/context access. Directly exposing legacy callback objects after legacy removal is not acceptable. A project-level contract lets the API 101 runtime provide the same user-facing context without retaining legacy support as a supported surface.

**Alternatives considered**:

- Expose API 101 callback objects directly to expressions: rejected because it would force user expressions to understand runtime implementation details and could break existing rules.
- Keep legacy callback object as the expression contract: rejected because it contradicts full legacy removal.
- Drop expression compatibility: rejected because method-hook context extraction is part of existing user value.

## Decision 4: Preserve SystemUI Startup Ordering

**Decision**: Preserve the observable SystemUI startup sequence: process filtering, app context/resource initialization, service readiness coordination, client attachment, and only then global input monitor installation.

**Rationale**: AI notes identify this ordering as a key architectural constraint for gestures and SystemUI integrations. Installing input monitoring before the runtime can answer configuration queries risks gesture misbehavior or missed events.

**Alternatives considered**:

- Install input monitoring immediately on SystemUI process load: rejected because it can race ShortX runtime readiness.
- Move gesture logic out of SystemUI during this feature: rejected as out of scope and too disruptive.
- Disable gestures until a later migration: rejected because existing SystemUI gesture behavior must be preserved.

## Decision 5: Retain Scope And Self-Protection Semantics

**Decision**: Preserve existing package/process scope behavior and self-protection that prevents ShortX from applying user-configured app method hooks to itself.

**Rationale**: The feature must preserve intended automation scope and avoid regressions in target-process behavior. Self-hooking can cause recursion, crashes, or incorrect diagnostics.

**Alternatives considered**:

- Re-evaluate scope from scratch under API 101: rejected because the feature is a runtime migration, not a scope redesign.
- Permit self-hooking as an advanced mode: rejected because it conflicts with safety requirements and current behavior.

## Decision 6: Release Validation Must Cover Debug And Release Artifacts

**Decision**: Require validation of both debug and release artifacts for module recognition, process loading, hook-driven event delivery, SystemUI behavior, and method-hook rule behavior.

**Rationale**: Runtime callback entry points and metadata can be affected by packaging and shrinking. The spec explicitly requires release behavior safety and module metadata retention.

**Alternatives considered**:

- Compile-only validation: rejected because Xposed runtime failures often appear only after installation and process restart.
- Debug-only validation: rejected because release packaging can differ materially from debug.
- Manual smoke test only: rejected because system server, SystemUI, and app hook paths must each be covered.

## Decision 7: Documentation Must Mark Legacy Environments Unsupported

**Decision**: Update project-facing and user-facing compatibility documentation to state that legacy-only Xposed environments are unsupported after this feature.

**Rationale**: Removing support only in code is not sufficient. Users and maintainers need a clear compatibility boundary to avoid filing or chasing unsupported legacy-mode issues.

**Alternatives considered**:

- Leave compatibility documentation unchanged: rejected because it would contradict runtime behavior.
- Mention legacy as partially supported: rejected because the feature requires full deprecation.

## Resolved Unknowns

- Compatibility strategy: API 101-only.
- Data migration: no new user data migration expected; existing rule models remain valid.
- Method-hook expression compatibility: preserve stable context semantics through a project-level contract.
- SystemUI behavior: preserve startup and gesture ordering.
- Validation scope: compile, debug runtime, release runtime, system server, SystemUI, and ordinary app process paths.

## API 101 Dependency Verification

**Decision**: Do not use the checked-in `patch-xposed/patch-main/libs/libxposed-api.jar` as the API 101 source. Replace local jar usage with the Gradle dependency `io.github.libxposed:api:101.0.1`.

**Observed API 101 integration source**:

- The upstream `libxposed/api` README documents `compileOnly("io.github.libxposed:api:101.0.1")` for module developers.
- The repository advertises API 101 and release `101.0.1`.

**Observed signatures from the pre-existing local jar before deletion**:

- `io.github.libxposed.api.XposedInterface.API` is present.
- `XposedInterface.hook(Method, Class<? extends Hooker>)` is present.
- `XposedInterface.hook(Constructor<T>, Class<? extends Hooker>)` is present.
- `XposedInterface.BeforeHookCallback` exposes `getMember()`, `getThisObject()`, `getArgs()`, `returnAndSkip(Object)`, and `throwAndSkip(Throwable)`.
- `XposedInterface.AfterHookCallback` exposes `getMember()`, `getThisObject()`, `getArgs()`, `getResult()`, `getThrowable()`, `isSkipped()`, `setResult(Object)`, and `setThrowable(Throwable)`.
- `XposedInterface.log(String)` and `XposedInterface.log(String, Throwable)` are present.

**Rationale**: The user confirmed the local `libxposed-api.jar` is not the desired API 101 artifact. Maven Central dependency resolution is now supported by the upstream project and is already available through repository configuration, so the project should stop carrying the local API jar.

**Alternatives considered**:

- Keep the local jar: rejected because it is not API 101.
- Depend on legacy `api-82.jar`: rejected because the feature requires full legacy support removal.
- Vendor the new jar manually: rejected because upstream Gradle dependency is available.
