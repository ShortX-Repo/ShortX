# ui-base

## Role

`ui-base` is the foundation UI library shared by higher-level UI modules. It provides base activity behavior, common composables, theming, shared locals, and reusable dialog/state patterns.

## Module Type

- Android library module

## Key Build Facts

- Keeps dependencies lighter than `ui`.
- Depends mainly on `core` and `core-api`.
- Designed to avoid circular dependencies by staying below `ui` and `services-ui`.

## Key Source Areas

- `ui-base/src/main/java/tornaco/apps/shortx/ui/base/`
- `ui-base/src/main/java/tornaco/apps/shortx/ui/theme/`
- `ui-base/src/main/java/tornaco/apps/shortx/ui/util/`

## Important Entry Points

- `BaseActivity`: shared activity foundation.
- Theme package: common Material/Compose theme behavior.
- Shared dialog and selection state helpers.
- Local UI state and i18n composition locals.

## How It Connects

`ui` and `services-ui` both consume this module. It is the shared visual and activity infrastructure that lets those modules stay separate.

## Reading Outcome

Read this when you need common UI primitives or when a feature exists in both the main UI and service-driven overlays.
