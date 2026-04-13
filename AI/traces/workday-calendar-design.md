# Workday Calendar Support Design

## Purpose

This note consolidates the requirement and technical design for adding statutory workday support into ShortX.

The feature has two main goals:

1. add a workday-related condition that can participate in normal rule evaluation
2. extend existing date/time-related triggers so they can optionally respect statutory workday data instead of only plain weekdays

This is not the same as existing weekday support.

Current `RepeatDays` in [core-api/src/main/proto/shortx/Facts.proto](core-api/src/main/proto/shortx/Facts.proto) expresses weekday recurrence such as Monday or Friday. The new feature needs to model Chinese statutory calendars, including:

1. public holidays that fall on weekdays but are non-working days
2. transfer workdays that fall on weekends but are working days

## Requirement Summary

### User-facing requirement

The requested product capability is:

1. add a dedicated workday-related condition
2. allow existing time/date triggers to support workday-aware scheduling
3. bundle local holiday/workday JSON with the app so the feature works offline
4. add a settings entry so users can manually refresh workday data from a public online source

### Data source requirement

The external source should be compatible with the `holiday-calendar` dataset, for example:

1. `https://app.unpkg.com/holiday-calendar@1.3.3/files/data/CN/2026.json`

Observed source shape:

1. top-level `year`
2. top-level `region`
3. `dates[]`
4. each date item contains `date`, `name`, `name_cn`, `name_en`, `type`
5. relevant `type` values include `public_holiday` and `transfer_workday`

### In-scope behavior

This phase should cover:

1. a reusable workday calendar repository and resolver
2. one or more rule conditions that can tell whether a date is a workday, holiday, weekend, or transferred workday
3. workday-aware filtering in `Alarm` and `RandomInPeriod`
4. local packaged data plus manual refresh in settings

### Out of scope for the first implementation

This phase should not attempt to solve:

1. automatic background sync for holiday data
2. multi-region holiday support beyond `CN`
3. user-edited holiday calendars
4. cloud merge or account-level calendar sync
5. replacing existing weekday-based `RepeatDays`

## Existing Architecture Anchors

The current implementation should be extended around these files:

1. facts and repeat models in [core-api/src/main/proto/shortx/Facts.proto](core-api/src/main/proto/shortx/Facts.proto)
2. condition models in [core-api/src/main/proto/shortx/Conditions.proto](core-api/src/main/proto/shortx/Conditions.proto)
3. time conditions in [services/src/main/java/tornaco/apps/shortx/services/rule/cond/impl/TimeInRange.kt](services/src/main/java/tornaco/apps/shortx/services/rule/cond/impl/TimeInRange.kt) and [services/src/main/java/tornaco/apps/shortx/services/rule/cond/impl/TheXXTimeToday.kt](services/src/main/java/tornaco/apps/shortx/services/rule/cond/impl/TheXXTimeToday.kt)
4. date formatting helpers in [services/src/main/java/tornaco/apps/shortx/services/os/DateScopeUtil.kt](services/src/main/java/tornaco/apps/shortx/services/os/DateScopeUtil.kt)
5. time trigger scheduling in [services/src/main/java/tornaco/apps/shortx/services/rule/fact/trigger/AlarmTrigger.kt](services/src/main/java/tornaco/apps/shortx/services/rule/fact/trigger/AlarmTrigger.kt) and [services/src/main/java/tornaco/apps/shortx/services/rule/fact/trigger/RandomInPeriodTrigger.kt](services/src/main/java/tornaco/apps/shortx/services/rule/fact/trigger/RandomInPeriodTrigger.kt)
6. settings state in [ui/src/main/java/tornaco/apps/shortx/ui/settings/pref/Pref.kt](ui/src/main/java/tornaco/apps/shortx/ui/settings/pref/Pref.kt)
7. settings UI in [ui/src/main/java/tornaco/apps/shortx/ui/settings/SettingsScreen.kt](ui/src/main/java/tornaco/apps/shortx/ui/settings/SettingsScreen.kt)

## Core Design Decision

The correct design is to introduce a centralized workday calendar layer rather than scattering holiday parsing logic into individual conditions or triggers.

Recommended shape:

1. `WorkdayRepository`: load bundled assets, load refreshed cache, expose year-level calendar data
2. `WorkdayResolver`: answer date-level questions such as whether a date is workday, holiday, weekend, or transfer-workday
3. thin adapters in conditions and triggers that depend on the resolver instead of parsing JSON themselves

This keeps policy logic in one place and avoids divergence between trigger scheduling and condition evaluation.

## Data Model Design

### Why existing `RepeatDays` is not enough

`RepeatDays` already supports plain weekday recurrence, but it cannot express these cases:

1. a weekday that is a statutory holiday
2. a weekend day that is a transferred working day

So the new feature should be additive rather than mutating current weekday semantics.

### Suggested proto additions

Add a small workday filter model in [core-api/src/main/proto/shortx/Facts.proto](core-api/src/main/proto/shortx/Facts.proto) for fact-side scheduling.

Recommended direction:

1. `enum CalendarDayType`
2. `message WorkdayFilter`

Suggested enum values:

1. `AnyDay`
2. `WorkdayOnly`
3. `HolidayOnly`
4. `WeekendOnly`
5. `TransferWorkdayOnly`

Suggested message shape:

1. `CalendarDayType dayType = 1;`

Then extend:

1. `Alarm` with optional `WorkdayFilter workdayFilter`
2. `RandomInPeriod` with optional `WorkdayFilter workdayFilter`

Add a corresponding condition-side model in [core-api/src/main/proto/shortx/Conditions.proto](core-api/src/main/proto/shortx/Conditions.proto).

Recommended direction:

1. `message CalendarDayMatch`

Suggested fields:

1. `CalendarDayType dayType = 1;`
2. `bool useCurrentDate = 2;`

For the first phase, `useCurrentDate = true` is enough for the UI. The field mainly preserves room for future date-source extension.

## Data Storage Design

### Bundled data

The app should ship built-in annual JSON assets for `CN`.

Because the current date is 2026-04-13, the initial packaged set should include:

1. `CN/2026.json`
2. `CN/2027.json`

Longer-term rule:

1. always package current year plus next year during release preparation

Recommended asset location:

1. under `ui` or a shared Android assets/resources location that can be read by runtime code without depending on DataStore payload size

### Refreshed cache

Manual refresh should download the latest JSON and persist it as cache files rather than stuffing the raw payload into DataStore.

Recommended split:

1. raw JSON files stored in app files directory or a small dedicated cache directory
2. refresh metadata stored in DataStore through [ui/src/main/java/tornaco/apps/shortx/ui/settings/pref/Pref.kt](ui/src/main/java/tornaco/apps/shortx/ui/settings/pref/Pref.kt)

Suggested metadata fields:

1. last refresh time
2. last refresh status
3. last successful year set
4. source URL or version tag

## Resolver Semantics

The resolver should answer day classification using this order:

1. if the date exists in annual JSON with `type == transfer_workday`, classify as `TransferWorkday` and also treat as workday
2. if the date exists in annual JSON with `type == public_holiday`, classify as holiday and not workday
3. otherwise fall back to natural calendar semantics
4. Monday to Friday becomes normal workday
5. Saturday and Sunday become weekend

This precedence matters because transferred workdays override normal weekend behavior.

## Condition Design

### First condition to add

Add a general calendar-day condition rather than multiple narrowly scoped conditions.

Recommended first condition:

1. `CalendarDayMatch`

Evaluation behavior:

1. resolve today using `WorkdayResolver`
2. compare with configured `CalendarDayType`
3. honor existing `isInvert`, `isDisabled`, `note`, and `id` conventions used by other conditions

This fits the existing condition pipeline used by [services/src/main/java/tornaco/apps/shortx/services/rule/cond/impl/TimeInRange.kt](services/src/main/java/tornaco/apps/shortx/services/rule/cond/impl/TimeInRange.kt).

### Why not patch `TimeInRange` directly first

`TimeInRange` currently answers only time-window membership. Mixing date-type semantics into that message would make it harder to understand and harder to reuse.

A dedicated workday condition is simpler and composes naturally with existing condition operators.

## Trigger Design

### `Alarm`

Current `AlarmTrigger` schedules and republishes alarm facts from [services/src/main/java/tornaco/apps/shortx/services/rule/fact/trigger/AlarmTrigger.kt](services/src/main/java/tornaco/apps/shortx/services/rule/fact/trigger/AlarmTrigger.kt).

Required extension:

1. when computing the next firing date, keep the current `RepeatDays` weekday filtering
2. after a candidate date is selected, apply `workdayFilter`
3. if the candidate does not match the requested day type, continue searching the next valid date

This keeps compatibility with existing rules:

1. old rules without `workdayFilter` behave exactly as before
2. new rules can combine weekday recurrence and workday semantics instead of replacing one with the other

### `RandomInPeriod`

Current scheduling path is in [services/src/main/java/tornaco/apps/shortx/services/rule/fact/trigger/RandomInPeriodTrigger.kt](services/src/main/java/tornaco/apps/shortx/services/rule/fact/trigger/RandomInPeriodTrigger.kt).

Required extension mirrors `Alarm`:

1. pick candidate date from current repeat rules
2. validate the candidate against `workdayFilter`
3. if invalid, search the next date before generating the random time in range

### Why `Alarm` and `RandomInPeriod` first

These two triggers already own explicit future-date scheduling. They are the best first targets because workday support here produces immediate user-visible value without redesigning the rest of the rule engine.

## Settings Design

### UI placement

The refresh entry fits best under the data-related settings section in [ui/src/main/java/tornaco/apps/shortx/ui/settings/SettingsScreen.kt](ui/src/main/java/tornaco/apps/shortx/ui/settings/SettingsScreen.kt).

Suggested settings item behavior:

1. title: refresh workday calendar
2. summary: show built-in years, last refresh time, and last result
3. click action: fetch supported year files and overwrite local cache on success

### Refresh policy

For the first phase, keep refresh manual.

Recommended behavior:

1. try current year and next year
2. if both succeed, atomically replace local cache metadata
3. if one fails, do not destroy the previous usable cache
4. always retain bundled fallback assets

## UI Editing Design

### Trigger editors

Existing UI fact models for `Alarm` and `RandomInPeriod` already live under `ui-base` and corresponding selectors in `ui`.

The workday extension should add one optional selector row:

1. day type: any day, workday, holiday, weekend, transfer workday

The default must remain `AnyDay` so existing UX and saved rules do not change behavior.

### Condition editor

Add a lightweight selector for the new calendar-day condition in the existing condition selection flow.

The selector only needs:

1. day type choice
2. optional invert through the common condition UI

## Runtime Layer Placement

Recommended service-side package:

1. `services/.../calendar/workday/`

Suggested classes:

1. `WorkdayRepository`
2. `WorkdayResolver`
3. `WorkdayJsonParser`
4. `WorkdayRefreshCoordinator`

Responsibilities:

1. parser converts raw annual JSON into internal year maps
2. repository decides whether to use bundled asset or refreshed cache
3. resolver answers date classification
4. refresh coordinator handles fetch, validation, and atomic replacement

## Validation Strategy

Minimum validation should include:

1. parser tests for `public_holiday` and `transfer_workday`
2. resolver tests for weekday fallback and override precedence
3. condition evaluation tests for workday and holiday cases
4. trigger scheduling tests for `Alarm` and `RandomInPeriod`
5. UI conversion tests for proto and UI model round-trip
6. settings refresh flow test or at least manual verification

## Risks

### Data drift risk

Bundled annual data becomes stale over time.

Mitigation:

1. always retain built-in fallback
2. expose manual refresh
3. package current year plus next year for each release

### Semantic confusion risk

Users may confuse weekday and workday.

Mitigation:

1. keep `RepeatDays` unchanged
2. label the new option explicitly as statutory workday or holiday-related filtering
3. show both weekday repeat and workday filter in fact summaries when both are set

### Storage risk

DataStore is not a good home for raw annual calendar JSON.

Mitigation:

1. use file storage for payloads
2. use DataStore only for small refresh metadata

## Recommended Implementation Order

1. add proto models for `CalendarDayType`, `WorkdayFilter`, and `CalendarDayMatch`
2. introduce repository, parser, and resolver with bundled assets only
3. add the new workday condition end to end
4. extend `Alarm` and `RandomInPeriod` scheduling with `workdayFilter`
5. add settings refresh and cache replacement flow
6. add UI summaries, i18n, and final validation

## Acceptance Criteria

The first release of this feature is complete when:

1. rules can express a workday-related condition
2. `Alarm` and `RandomInPeriod` can restrict firing to workday-aware day types
3. the app works offline using bundled annual JSON
4. users can manually refresh workday data in settings
5. old rules without new fields behave exactly as before
