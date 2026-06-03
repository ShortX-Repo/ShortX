# patch-xposed/patch-main

## Role

`patch-xposed/patch-main` is the shared business-hook module inside the Xposed tree. It no longer owns a concrete runtime entry point. Instead, it contains the main package/system-server hook orchestration that is reused by the dedicated runtime modules: `patch-legacy`, `patch-100`, and `patch-101`.

## Module Type

- Kotlin/JVM library

## Key Build Facts

- Depends on `patch-common`, `patch-systemui`, `services`, `core`, `core-api`, and support libraries such as `external:dex-maker`.
- Compiles against Android framework stubs plus the extracted libxposed API jar.
- Is a shared implementation module, not the owner of legacy/API 100/API 101 lifecycle entry classes.

## Key Source Areas

- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/HookStub2.kt`
- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/CustomHookSystemServer.kt`
- `patch-xposed/patch-main/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/ShortXposedUtils.kt`

## Important Entry Points

- `HookStub2.kt`: central dispatch for zygote, package, and system-server hook flows after a runtime-specific entry initializes `XposedRuntime`.
- `CustomHookSystemServer.kt`: installs system-server rule-driven hooks.
- `HookStub2.initZygote()/packageLoaded()/systemServerLoaded()`: shared lifecycle bridge used by all runtime-specific entry modules.

## How It Connects

`patch-legacy`, `patch-100`, and `patch-101` translate their own callback model into shared calls on `HookStub2`. From there, `patch-main` delegates reusable hook mechanics to `patch-common`, SystemUI-specialized logic to `patch-systemui`, and rule/runtime integration to `services`.

## Reading Outcome

Read this when you need to understand the main hook orchestration used after a runtime-specific entry module has already bootstrapped ShortX inside a foreign process.
