# Quickstart: Upgrade LSPosed API 101

This quickstart describes the validation path for the LSPosed API 101-only migration plan.

## Prerequisites

- Work on branch `010-upgrade-lsposed-api`.
- Read the feature spec: `specs/010-upgrade-lsposed-api/spec.md`.
- Read the runtime behavior contract: `specs/010-upgrade-lsposed-api/contracts/runtime-behavior.md`.
- Use a supported LSPosed API 101 environment for runtime validation.
- Treat legacy-only Xposed environments as unsupported for this feature.

## Planning Validation

1. Confirm `research.md` resolves all planning unknowns.
2. Confirm `data-model.md` introduces no new persistent user data migration.
3. Confirm `contracts/runtime-behavior.md` covers API 101-only compatibility, scope safety, method-hook delivery, SystemUI behavior, and release artifact safety.
4. Confirm `AGENTS.md` points to `specs/010-upgrade-lsposed-api/plan.md` between the Speckit markers.

## Compile Validation

Run the core compile checks after implementation tasks are complete:

```bash
./gradlew :patch-xposed:patch-common:compileKotlin
./gradlew :patch-xposed:patch-systemui:compileKotlin
./gradlew :patch-xposed:patch-main:compileKotlin
./gradlew :app:assembleDebug
```

In this workspace, `:app:assembleDebug` is blocked by missing Google Services configuration for the default task graph. The validated local debug artifact command is:

```bash
./gradlew :app:assemblePrcDebug
```

The validated local release artifact command is:

```bash
./gradlew :app:assemblePrcRelease
```

Validated artifact locations:

- Debug: `out/app/outputs/apk/prc/debug/shortx-1.1-237-ef3c27f-010-upgrade-lsposed-api-dirty(1487770)-prc-debug.apk`
- Release: `out/app/outputs/apk/prc/release/shortx-1.1-237-ef3c27f-010-upgrade-lsposed-api-dirty(1487768)-prc-release.apk`

If implementation changes AIDL or protobuf definitions, regenerate first:

```bash
./gradlew :core-api:generateAidlFiles
./gradlew :core-api:generateProto
```

## Runtime Validation

Validate on a supported LSPosed API 101 environment:

1. Install the upgraded debug artifact.
2. Enable the module and intended scope.
3. Restart target processes or the device.
4. Confirm the module is recognized as API 101-only.
5. Validate a system-event automation reaches the ShortX runtime.
6. Validate a SystemUI scenario, including gesture and quick settings behavior.
7. Validate an ordinary app method-hook scenario with before-call and after-call triggers.
8. Confirm ShortX does not apply user-configured app method hooks to itself.
9. Repeat representative checks with a release artifact.

Runtime validation has not been executed in this workspace session because it requires a supported LSPosed API 101 device or emulator environment. Record device, framework, scope, and process-restart details in `validation-results.md` when those checks are run.

## Release Readiness Checks

- Compatibility documentation states legacy Xposed environments are unsupported.
- Release metadata supports API 101 module recognition.
- Release runtime callback behavior matches debug validation for representative scenarios.
- A failed target hook produces diagnostics without blocking unrelated automations.
- Existing user rules and direct actions do not require manual recreation.

## Expected Completion State

- API 101 is the only supported Xposed runtime mode.
- Legacy Xposed compatibility is removed from supported product behavior.
- Existing ShortX automations continue to work through the existing runtime rule engine.
- Debug and release validation results are available for system server, SystemUI, and ordinary app process paths.
