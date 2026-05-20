# Quickstart: Normalize UI Fixes

## Prerequisites

- Work on branch `009-normalize-ui-fixes`.
- Read `specs/009-normalize-ui-fixes/spec.md` and `specs/009-normalize-ui-fixes/contracts/ui-behavior.md` before implementation.
- Preserve existing rule/action runtime behavior unless a later approved spec change says otherwise.

## Implementation Focus

1. Normalize only the six requested condition presentations.
2. Update localized user-facing text in `ui/src/main/assets/i18n/` for supported product locales.
3. Correct the affected `OCR Engine` label in settings/sidebar-related UI.

## Suggested Validation

Run relevant compile checks after implementation, starting with UI modules affected by the change:

```bash
./gradlew :ui-base:compileDebugKotlin :ui:compileRowDebugKotlin
```

If implementation touches shared contracts, generated sources, or runtime modules, add the relevant module validation. No AIDL or protobuf changes are planned for this feature.

## Manual Verification Checklist

- Open condition selection and verify screen lock state, VPN state, screen-on state, ringing state, call state, and screen orientation labels and options.
- Save rules using locked/unlocked and portrait/landscape choices, then reopen them and confirm summaries remain correct.
- Verify existing saved rules with affected conditions still open and remain behavior-compatible.
- Confirm sidebar displays `OCR Engine` with correct capitalization.

## Out of Scope

- Normalizing additional conditions beyond the six requested examples.
- Changing action behavior or unrelated action labels.
- Changing runtime rule engine evaluation semantics.
- Adding new persistent storage or external interfaces.
- Editor search, editor toolbar, code repository, JS console output, method hook expression defaults, command parameter defaults, and paste cursor behavior.
