# services-ui

## Role

`services-ui` contains UI components that are owned by the runtime/service side instead of the main app screen flow. Its focus is overlays, floating buttons, dialogs, clipboard UI, screenshots, sidebars, and related service-driven surfaces.

## Module Type

- Android library module

## Key Build Facts

- Uses Compose and Hilt like the main UI, but is scoped to service and overlay use cases.
- Depends on `core`, `core-api`, `ui-base`, and selected external UI utilities.
- Avoids depending on the main `ui` module to prevent circular ownership.

## Key Source Areas

- `services-ui/src/main/java/tornaco/apps/shortx/ui/overlay/`
- `services-ui/src/main/java/tornaco/apps/shortx/ui/dialog/`
- `services-ui/src/main/java/tornaco/apps/shortx/ui/notification/`
- `services-ui/src/main/java/tornaco/apps/shortx/ui/screencap/`

## Important Entry Points

- `ComposeOverlay`: runtime floating UI container.
- `OverlayViewModel`: state and interaction logic for overlays.
- `AbstractComposeOverlay`: shared overlay behavior.
- Service dialog and biometric packages: user interaction surfaces launched from services.

## How It Connects

This module is the visual companion to the runtime `services` layer. It lets background or system-facing logic present rich UI without routing everything through the main app activity stack.

## Reading Outcome

Read this module when you need to understand how runtime actions show UI outside the normal app screens.
