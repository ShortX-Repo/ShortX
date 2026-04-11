# ui

## Role

`ui` is the main feature UI module. It contains Compose screens, editors, navigation, settings, import/export, shortcuts, licensing, and the user-facing flows for rules and one-click commands.

## Module Type

- Android library module

## Key Build Facts

- Compose-heavy module with Navigation Compose, Orbit, Room, Glide, Lottie, and Datastore.
- Depends on `core`, `core-api`, `services-ui`, `services-jni`, `ui-base`, and several external support modules.
- Carries flavor-aware behavior through `row` and `prc` variants.

## Key Source Areas

- `ui/src/main/java/tornaco/apps/shortx/ui/EntryPoint.kt`
- `ui/src/main/java/tornaco/apps/shortx/ui/FlavorEntryPoint.kt`
- `ui/src/main/java/tornaco/apps/shortx/ui/main/`
- `ui/src/main/java/tornaco/apps/shortx/ui/addrule/`
- `ui/src/main/java/tornaco/apps/shortx/ui/addda/`
- `ui/src/main/java/tornaco/apps/shortx/ui/settings/`

## Important Entry Points

- `EntryPoint.kt`: app-wide UI initialization.
- `FlavorEntryPoint.kt`: market-specific initialization.
- `MainActivity` and navigation graph: user entry into the app.
- Add/edit packages for rules and direct actions: core configuration flows.

## How It Connects

This module is the user-facing shell over the core model. It translates user intent into `core-api` and `core` data structures and calls service APIs through manager classes.

## Reading Outcome

Read this module after `core-api`, `core`, and `services`. Otherwise the screens look busy without the domain model behind them.
