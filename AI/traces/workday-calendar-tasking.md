# Workday Calendar Tasking

This tasking follows the design in [AI/traces/workday-calendar-design.md](AI/traces/workday-calendar-design.md) and is ordered to minimize churn across proto, runtime, and UI.

## Phase 1: Protocol And Assets

- [ ] Add `CalendarDayType` and `WorkdayFilter` to [core-api/src/main/proto/shortx/Facts.proto](core-api/src/main/proto/shortx/Facts.proto).
- [ ] Extend `Alarm` in [core-api/src/main/proto/shortx/Facts.proto](core-api/src/main/proto/shortx/Facts.proto) with optional `workdayFilter`.
- [ ] Extend `RandomInPeriod` in [core-api/src/main/proto/shortx/Facts.proto](core-api/src/main/proto/shortx/Facts.proto) with optional `workdayFilter`.
- [ ] Add `CalendarDayMatch` to [core-api/src/main/proto/shortx/Conditions.proto](core-api/src/main/proto/shortx/Conditions.proto).
- [ ] Regenerate proto outputs for `core-api` and verify generated sources compile.
- [ ] Decide the packaged asset path for bundled `CN` annual JSON.
- [ ] Check in bundled data for 2026 and 2027.

## Phase 2: Runtime Calendar Layer

- [ ] Create a `services/.../calendar/workday/` package under `services`.
- [ ] Implement `WorkdayJsonParser` for the `holiday-calendar` payload shape.
- [ ] Implement `WorkdayRepository` with bundled asset loading.
- [ ] Implement local cache lookup that overrides bundled assets when valid refreshed data exists.
- [ ] Implement `WorkdayResolver` with precedence for `transfer_workday` over weekend fallback.
- [ ] Add unit tests for parser success, invalid JSON handling, and date lookup semantics.

## Phase 3: Workday Condition

- [ ] Add `Condition.CalendarDayMatch` to the `ui-base` condition model.
- [ ] Add proto-to-UI and UI-to-proto conversions for `CalendarDayMatch`.
- [ ] Register the new condition class in the condition registry lists.
- [ ] Add a condition evaluator implementation in `services/rule/cond/impl/`.
- [ ] Register the evaluator in the service-side condition mapping.
- [ ] Add a selector dialog in `ui` for choosing day type.
- [ ] Add i18n labels, summary mapping, and icon mapping for the new condition.
- [ ] Verify condition save, edit, and round-trip restore flows.

## Phase 4: Trigger Model And Editor Support

- [ ] Extend `Fact.Alarm` in `ui-base` with `workdayFilter`.
- [ ] Extend `Fact.RandomInPeriod` in `ui-base` with `workdayFilter`.
- [ ] Update fact conversion layers for both trigger types.
- [ ] Update [ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/selector/AlarmSelectDialog.kt](ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/selector/AlarmSelectDialog.kt) to expose day-type filtering.
- [ ] Update [ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/selector/RandomInPeriodSelectDialog.kt](ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/selector/RandomInPeriodSelectDialog.kt) to expose day-type filtering.
- [ ] Add i18n and summary rendering so weekday repeat and workday filter are both visible.
- [ ] Verify existing saved rules still deserialize with default `AnyDay` behavior.

## Phase 5: Scheduler Integration

- [ ] Identify the date-selection points used by `AlarmTrigger` and its scheduler.
- [ ] Apply `workdayFilter` after weekday repeat filtering and before final schedule commit.
- [ ] Identify the date-selection points used by `RandomInPeriodTrigger` and its scheduler.
- [ ] Apply `workdayFilter` before random time generation for the chosen day.
- [ ] Keep old scheduling behavior unchanged when `workdayFilter` is unset or `AnyDay`.
- [ ] Add tests that cover weekday holiday skips and weekend transfer-workday matches.

## Phase 6: Settings Refresh Flow

- [ ] Add refresh metadata keys to [ui/src/main/java/tornaco/apps/shortx/ui/settings/pref/Pref.kt](ui/src/main/java/tornaco/apps/shortx/ui/settings/pref/Pref.kt).
- [ ] Implement a refresh coordinator that fetches current year and next year JSON.
- [ ] Validate downloaded payloads before replacing local cache.
- [ ] Add a settings item in [ui/src/main/java/tornaco/apps/shortx/ui/settings/SettingsScreen.kt](ui/src/main/java/tornaco/apps/shortx/ui/settings/SettingsScreen.kt) under data settings.
- [ ] Show refresh result, last refresh time, and effective year set in the settings summary.
- [ ] Make refresh failure non-destructive so the last good cache remains usable.

## Phase 7: Verification And Cleanup

- [ ] Run proto generation and module compile validation for affected modules.
- [ ] Add or update tests for parser, resolver, condition evaluation, and trigger scheduling.
- [ ] Manually verify offline bundled-data behavior with network unavailable.
- [ ] Manually verify refresh success and refresh failure behavior.
- [ ] Manually verify `Alarm` and `RandomInPeriod` on weekday holiday and transfer-workday cases.
- [ ] Update AI notes if final implementation diverges from this design.
