# Quickstart: Normalize UI Fixes

## Prerequisites

- Work on branch `009-normalize-ui-fixes`.
- Read `specs/009-normalize-ui-fixes/spec.md` and `specs/009-normalize-ui-fixes/contracts/ui-behavior.md` before implementation.
- Preserve existing rule/action runtime behavior unless a later approved spec change says otherwise.

## Implementation Focus

1. Normalize only the six requested condition presentations.
2. Update localized user-facing text in `ui/src/main/assets/i18n/` for supported product locales.
3. Fix editor search styling, shortcut-bar dynamic coloring, shell independent-run placement, and paste cursor retention across shell, JS, and MVEL editors.
4. Make code repository search functional by author, title, and description while matching the one-click automatic command page search UI.
5. Complete JS console output line actions for copy, share, and from-current-line behavior.
6. Fix stale previous-row initialization for method hook context expressions and command parameters.

## Suggested Validation

Run relevant compile checks after implementation, starting with UI modules affected by the change:

```bash
./gradlew :ui-base:compileKotlin :ui:compileDebugKotlin
```

If implementation touches shared contracts, generated sources, or runtime modules, add the relevant module validation. No AIDL or protobuf changes are planned for this feature.

## Manual Verification Checklist

- Open condition selection and verify screen lock state, VPN state, screen-on state, ringing state, call state, and screen orientation labels and options.
- Save rules using locked/unlocked and portrait/landscape choices, then reopen them and confirm summaries remain correct.
- Verify existing saved rules with affected conditions still open and remain behavior-compatible.
- Confirm sidebar displays `OCR Engine` with correct capitalization.
- Open shell, JS, and MVEL editors, use search, expand options, and verify dynamic colors, outlined controls, press feedback, and rounded options area in light and dark modes.
- Verify shell independent run appears next to the close control as a visible switch.
- Paste text in shell, JS, and MVEL editors for empty text, selected text, multi-line text, and boundary positions; confirm cursor placement remains correct.
- Search the code repository by author, title, and description; confirm empty search restores the full list.
- In JS console output, use copy current line, share, and from-current-line actions and confirm each has visible effect.
- Add multiple method hook context expressions and command parameters; confirm new rows do not copy just-entered previous content.

## Out of Scope

- Normalizing additional conditions beyond the six requested examples.
- Changing action behavior or unrelated action labels.
- Changing runtime rule engine evaluation semantics.
- Adding new persistent storage or external interfaces.
