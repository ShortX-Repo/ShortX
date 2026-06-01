# ShortX Xposed API 101 升级 Tasks

本文基于 `AI/` 目录中的架构说明和当前源码扫描结果，梳理 ShortX 升级到 Xposed API 101 的执行任务。目标不是泛化迁移手册，而是面向当前项目结构拆出可落地、可验证的任务清单。

## API 101 Migration Status

- 当前 feature：`specs/010-upgrade-lsposed-api`
- 分支：`010-upgrade-lsposed-api`
- 策略：最终完全废弃 legacy Xposed 支持，只支持 LSPosed API 101。
- API 101 依赖：使用 Gradle 依赖 `io.github.libxposed:api:101.0.1`，不再使用本地 `patch-xposed/patch-main/libs/libxposed-api.jar`。
- Legacy 盘点：见 `specs/010-upgrade-lsposed-api/legacy-reference-inventory.md`。
- 当前结果：最终目标仍是 API101-only；若中途尝试过 dual-support，请以删除 `XposedEntry`、删除 `xposed_init`、删除 `api-82.jar` 构建使用为收口标准。
- 本地验证：`:app:assemblePrcDebug` 和 `:app:assemblePrcRelease` 已通过；默认 `:app:assembleDebug` 受本地 `google-services.json` 缺失阻塞；SystemUI 测试任务受既有测试 API drift 阻塞。
- 待验证：system_server、SystemUI、普通 app Method Hook、失败隔离和自 hook 保护仍需在支持 LSPosed API 101 的设备环境上完成。

## 1. 当前项目理解

ShortX 是分层自动化系统，关键链路如下：

| 层 | 主要职责 | API 101 升级影响 |
| --- | --- | --- |
| `ui` | 规则、动作、Method Hook、设置和诊断 UI | 主要受 libxposed service jar、Method Hook 配置和日志展示影响 |
| `core-api` | Protobuf、AIDL、`ShortXManager` 共享契约 | 通常不直接依赖 Xposed API，但 Method Hook 模型会影响 Hook 回调数据结构 |
| `services` | 运行时引擎、规则评估、动作执行、状态存储 | Xposed 事件最终进入这里，需验证升级后事件发布不变 |
| `patch-xposed:patch-main` | Xposed / LSPosed 主入口、system_server 和 app hook 调度 | API 101 升级核心模块之一 |
| `patch-xposed:patch-common` | 通用 Hook 包装、动态 Method Hook、回调接口 | API 101 升级最核心模块，当前仍大量使用传统 API |
| `patch-xposed:patch-systemui` | SystemUI 进程 Hook、全局输入监听、手势、QS、状态栏、OCR | 需迁移日志和依赖传统回调类型的文件 |
| `patch-magisk:*` | Magisk 注入、bridge、framework patch | 不直接迁移 Xposed API，但需做集成验证 |

AI 文档中的关键结论：

- Xposed 不是自动化引擎，而是事件源和控制源。
- `patch-xposed` 观察或拦截 system_server、SystemUI、App 进程行为，然后通过 `ShortXManager` / `IShortX` 或直接服务调用进入 `services`。
- Method Hook 是用户可配置能力，`CustomHookUtil.installHookForRules(...)` 会扫描规则和 DirectAction 中的 `MethodHook` fact 并安装动态 Hook。
- SystemUI 全局手势链路依赖 `SystemUIHook -> hookApp.kt -> installInputMonitor(app)`，升级不能破坏 binder ready 后再安装 input monitor 的顺序。

## 2. 当前源码状态

迁移前项目已经部分接入现代 libxposed：

- `patch-xposed/patch-main/src/main/java/.../ShortXLSPosedHookEntry.kt` 已继承 `io.github.libxposed.api.XposedModule`。
- `patch-xposed/patch-main/build.gradle.kts` 已同时编译 `api-82.jar` 和 `libxposed-api.jar`。
- `ui/build.gradle.kts` 需要迁移到 Maven 的 `io.github.libxposed:service:101.0.0`，并配合 `compileSdk 36` 直接消费。
- `app/proguard-rules.pro` 已包含 `io.github.libxposed.api.*` 的 `dontwarn`。

迁移前项目仍保留大量传统 API 依赖：

- `XposedEntry.kt` 实现 `IXposedHookLoadPackage` 和 `IXposedHookZygoteInit`。
- `patch-common/Hooks.kt` 以 `XposedBridge.hookMethod`、`hookAllMethods`、`hookAllConstructors`、`XC_MethodHook` 为核心。
- `CustomHookUtil.kt` 将 `XC_MethodHook.MethodHookParam` 作为 Method Hook fact 的回调上下文。
- `CustomHookApp.kt` 和 `InjectedAndroidAppHelper` 使用 `android.app.AndroidAppHelper`。
- `SystemUIHook.kt`、`hookApp.kt`、`MediaProviderHook.kt` 等直接使用 `XposedBridge.log`。
- 部分文件只是类型导入仍来自传统回调包，例如 `XC_LoadPackage`。

迁移后状态：活动 `patch-xposed` 源码中不再有受支持的 legacy `de.robv.android.xposed` 入口或直接 hook API 使用；保留的匹配仅限历史说明、迁移文档、现代 libxposed service AIDL 名称，或构建输出。

## 3. 升级目标

API 101 升级应达到以下目标：

- `patch-main` 以 API 101 入口作为主加载路径。
- `patch-common` 的通用 Hook 基建从传统 `XC_MethodHook` 抽象迁移到 API 101 兼容抽象。
- 用户配置的动态 Method Hook 能继续安装、触发、提取表达式上下文并回调 `services`。
- system_server、SystemUI、普通 App 进程的 Hook 调度语义保持不变。
- Release 混淆后 API 101 入口和 Hooker 类不被删除。
- 保留或明确废弃传统 API 兼容路径，避免代码中长期混杂两套 API。

## 4. 任务总览

| 阶段 | 目标 | 优先级 |
| --- | --- | --- |
| Phase 0 | 明确兼容策略和 API 101 真实 jar 状态 | P0 |
| Phase 1 | 构建依赖和 Manifest 元信息升级 | P0 |
| Phase 2 | 建立 API 101 Hook 抽象层 | P0 |
| Phase 3 | 迁移动态 Method Hook 能力 | P0 |
| Phase 4 | 迁移具体业务 Hook 调用点 | P1 |
| Phase 5 | 迁移日志、AndroidAppHelper、表达式上下文 | P1 |
| Phase 6 | 混淆、打包和兼容策略收口 | P1 |
| Phase 7 | 验证 system_server、SystemUI、App hook 全链路 | P0 |
| Phase 8 | 文档、回归清单和清理历史依赖 | P2 |

## 5. Phase 0：确认升级策略

### Task 0.1 确认 API 101 jar 版本

检查项：

- 确认 `patch-xposed/patch-main/libs/libxposed-api.jar` 是否就是 API 101。
- 如果不是，替换为 API 101 对应 jar，或改为可信 Maven 坐标。
- 记录 `XposedInterface`、`XposedModule`、`BeforeHookCallback`、`AfterHookCallback`、`Hooker`、`hook(...)` 的真实签名。

验收标准：

- 文档中记录当前 jar 来源、版本和 API 签名差异。
- `./gradlew :patch-xposed:patch-main:compileKotlin` 能使用 API 101 jar 编译到入口类。

### Task 0.2 确认是否继续兼容传统 API 82

需要决策：

- 方案 A：只支持 API 101，移除 `XposedEntry.kt` 和 `api-82.jar` 编译依赖。
建议：

- 不要做双入口兼容。ShortX 已验证双支持运行时不稳定，应直接收口到 API101-only。

验收标准：

- 在升级任务中明确 legacy 支持边界。
- Manifest、ProGuard、构建依赖与选择的策略一致。

## 6. Phase 1：构建依赖与 Manifest

### Task 1.1 更新 `patch-xposed` 子模块依赖

涉及文件：

- `patch-xposed/patch-main/build.gradle.kts`
- `patch-xposed/patch-common/build.gradle.kts`
- `patch-xposed/patch-systemui/build.gradle.kts`
- `ui/build.gradle.kts`

工作内容：

- 将 `libxposed-api.jar` 明确升级或替换为 API 101。
- 让 `patch-common` 和 `patch-systemui` 也能编译 API 101 抽象需要的类型。
- 如果保留 legacy，继续 `compileOnly(api-82.jar)`，但不要让业务代码直接扩散新旧 API 类型。
- 如果废弃 legacy，删除 `api-82.jar` 依赖和传统 API import。

验收标准：

- `patch-main`、`patch-common`、`patch-systemui` 都能编译。
- 没有业务模块因缺失 Xposed 类型编译失败。

### Task 1.2 更新 App Manifest 的 Xposed 元信息

涉及文件：

- `app/src/main/AndroidManifest.xml`

当前状态：

- `xposedmodule=true`
- `xposeddescription=ShortX`
- `xposedminversion=53`
- `xposedscope=@array/xposed_scope`

工作内容：

- 按最终兼容策略调整 `xposedminversion`。
- 如果只支持 API 101，考虑提升到 `101`。
- 如果保留 legacy，暂不直接提升到 `101`，避免旧框架拒绝加载。
- 检查 API 101 / LSPosed 新入口是否需要额外 metadata 或 asset 声明。

验收标准：

- 安装后模块能在目标框架中被识别。
- 作用域仍能读取 `@array/xposed_scope`。

### Task 1.3 固化 libxposed service 依赖策略

涉及文件：

- `ui/build.gradle.kts`
- `patch-xposed/patch-main/libs/`

工作内容：

- 确认 `libxposed-service.jar` 和 `libxposed-service-interface.jar` 与 API 101 兼容。
- 确认 UI 侧是否实际使用 service API；若只是编译依赖，保持 `compileOnly` 的可能性。
- 明确 jar 是本地预置还是 Maven 依赖，避免未来升级不可追踪。

验收标准：

- UI 编译通过。
- service 相关 API 没有运行时类冲突。

## 7. Phase 2：建立 API 101 Hook 抽象层

### Task 2.1 设计项目内 Hook 回调模型

涉及文件：

- `patch-xposed/patch-common/src/main/java/.../Hooks.kt`
- 新增或改造 `patch-common` 中的 hook api 包

当前问题：

- 业务 Hook 回调直接暴露 `XC_MethodHook.MethodHookParam`。
- 动态 Method Hook 表达式中也把 `param` 暴露为传统类型。

工作内容：

- 新增项目内中立类型，例如 `ShortXHookParam`。
- 至少封装：`method`、`thisObject`、`args`、`result`、`throwable`、设置返回值、判断 before/after。
- 让业务 Hook 和动态 Method Hook 尽量依赖项目内类型，而不是直接依赖 `XC_MethodHook.MethodHookParam` 或 API 101 callback。

验收标准：

- `CustomHookUtil` 的核心逻辑能基于中立 `ShortXHookParam` 表达。
- 旧 API 和 API 101 都可以适配到该中立类型。

### Task 2.2 改造 `Hooks.kt` 为门面层

涉及文件：

- `patch-xposed/patch-common/src/main/java/.../Hooks.kt`

工作内容：

- 保留现有函数名：`beforeMethod`、`afterMethod`、`beforeConstruct`、`afterConstruct`，降低业务 Hook 改动量。
- 将具体实现拆到 legacy 和 api101 adapter。
- 由当前运行入口或全局 hook runtime 选择实际 adapter。

建议结构：

```text
patch-common/.../hook/
  Hooks.kt
  ShortXHookParam.kt
  HookRuntime.kt
  LegacyHookAdapter.kt
  Api101HookAdapter.kt
```

验收标准：

- 业务 Hook 文件不直接调用 `XposedBridge.hookMethod`。
- 新增 API 101 实现时不需要重写每个业务 Hook。

### Task 2.3 处理 Hooker 类注册策略

工作内容：

- API 101 通常需要 `Hooker` 类和静态 `before` / `after` 回调。
- 当前项目大量使用 Kotlin lambda 动态注册 Hook，不能简单为每个 Hook 点手写 Hooker。
- 需要确认 API 101 是否支持动态 callback，或是否必须借助统一 Hooker + method-to-callback registry。

可选方案：

- 方案 A：为 API 101 建立统一 Hooker，并用 `Method` 作为 key 映射到 before/after lambda。
- 方案 B：为高频稳定 Hook 点逐个生成 Hooker，动态 Method Hook 另行处理。
- 方案 C：保留 legacy adapter 承载动态 Hook，API 101 入口只负责生命周期调度。

建议：

- 优先验证方案 A 是否可行。它最符合当前 `Hooks.kt` 的门面设计。

验收标准：

- 至少能用 API 101 adapter 完成一个 `afterMethod(Method)` 和一个 `beforeMethod(Method)` 的端到端测试。

## 8. Phase 3：迁移动态 Method Hook

### Task 3.1 迁移 `CustomHookUtil.installHookForRules(...)`

涉及文件：

- `patch-xposed/patch-common/src/main/java/.../CustomHookUtil.kt`

当前职责：

- 扫描规则和 DirectAction。
- 找到 `MethodHook` fact。
- 按 package、lifecycle、class、method、参数和返回值约束匹配目标方法。
- 根据 `beforeMethod` 安装 before 或 after Hook。
- 回调 `onMethodCall(param, hook)`。

工作内容：

- 将 `onMethodCall: (MethodHookParam, MethodHook) -> Unit` 改为项目内 `ShortXHookParam`。
- `compileExpressions(...)` 同步改为接收 `ShortXHookParam`。
- 保持 MVEL 变量名 `param` 不变，降低用户规则破坏面。
- 兼容表达式中常用的 `param.args`、`param.result`、`param.thisObject`、`param.method`。

验收标准：

- 现有 Method Hook 规则不需要改配置即可继续执行。
- MVEL 表达式能读取参数、返回值和当前 App helper。

### Task 3.2 迁移 `CustomHookApp.hookApp(...)`

涉及文件：

- `patch-xposed/patch-common/src/main/java/.../CustomHookApp.kt`

工作内容：

- 适配新的 `ShortXHookParam`。
- 确认 `currentProcessPkgName == null` 时的 init-zygote 场景是否仍需调用 `AndroidAppHelper.currentPackageName()`。
- 如果 API 101 没有 `AndroidAppHelper` 等价能力，设计 `ProcessContextProvider`。

验收标准：

- `LoadedPackage` 生命周期下的 app Method Hook 正常安装。
- `InitZygote` 生命周期下的规则行为被明确保留、调整或废弃。

### Task 3.3 处理 `InjectedAndroidAppHelper`

涉及文件：

- `CustomHookUtil.kt`

当前暴露给 MVEL：

- `helper.currentProcessName`
- `helper.currentPackageName`
- `helper.currentApplication`
- `helper.currentApplicationInfo`

工作内容：

- 确认 API 101 下是否还可依赖 `android.app.AndroidAppHelper`。
- 若不可靠，提供项目自有 helper：从 `PackageLoadedParam`、进程缓存、`ActivityThread` 反射获取所需信息。
- 保持 MVEL 中 `helper.xxx` 字段兼容。

验收标准：

- 老规则里的 `helper.currentPackageName` 和 `helper.currentApplication` 不崩溃。
- 无 Application 的早期生命周期场景返回 null 或空值时有日志。

## 9. Phase 4：迁移具体业务 Hook 调用点

### Task 4.1 迁移 system_server Hook 组

涉及入口：

- `HookStub2.androidHooks(...)`

涉及文件示例：

- `AMSHook.kt`
- `BackNavHooks.kt`
- `InputManagerHook.kt`
- `AppWidgetServiceHook.kt`
- `NotificationUsageStatsHook.kt`
- `ActivityTaskSupervisorHook.kt`
- `ProcessListHook.kt`
- `ActivityRecordHook.kt`
- `VPNHook.kt`
- `ClipboardServiceHook.kt`
- `ActivityStarterHook.kt`
- `ActiveServiceHook.kt`
- `DisplayRotationHook.kt`
- `IMEServiceHook.kt`
- `WMServiceHook.kt`
- `DisplayPolicyHook.kt`
- `AccManagerHook.kt`
- `ActivityMetricsLoggerHook.kt`
- `ShortcutServiceHook.kt`
- `TaskOrganizerControllerHooks.kt`
- `ActivityClientControllerHooks.kt`
- `StatusBarManagerServiceHook.kt`
- `ComputerEngineHook.kt`
- `ASPHook.kt`

工作内容：

- 优先迁移直接 import `XC_MethodHook` 的文件。
- 将业务处理函数参数从 `XC_MethodHook.MethodHookParam` 改为 `ShortXHookParam`。
- 将直接使用 `XposedHelpers.findClass` 的地方改为 `classLoader.loadClass(...)` 或项目反射工具。

已发现直接点：

- `ProcessListHook.kt`
- `MediaProviderHook.kt`
- `ActivityTaskSupervisorHook.kt`
- `ActivityRecordHook.kt`
- `ComputerEngineHook.kt`

验收标准：

- `HookStub2.systemServerLoaded(...)` 完整执行不因 API 类型缺失崩溃。
- system_server 中规则、剪贴板、通知、输入、窗口、快捷方式等核心事件仍能进入 `services`。

### Task 4.2 迁移 SystemUI Hook 组

涉及入口：

- `SystemUIHook.handleLoadPackage(...)`
- `hookApp.kt`
- `hookTile.kt`
- `hookQS.kt`
- `hookTileQuery.kt`
- `hookView.kt`
- `hookDarkIcon.kt`
- `hookCustomTile.kt`

当前关键约束：

- 只在 `com.android.systemui` 主进程执行。
- `SystemUIApplication.onCreate` 后初始化 ShortX app context 和资源。
- binder side ready 后才安装 input monitor。

工作内容：

- 移除 `XposedBridge.log` 直接依赖，改为项目日志门面。
- 清理 `XC_LoadPackage` 类型导入，统一使用 `IPackageLoaded.Param`。
- 确认 `hookApp.kt` 的 onCreate Hook 迁移后仍保持安装顺序。

验收标准：

- SystemUI 主进程启动后能初始化 `AppGlobals` 和 `AppResources`。
- QS tile、自定义 tile、状态栏相关 Hook 正常。
- `installInputMonitor(app)` 仍只在系统 ready 后执行。
- Edge gesture、CornerSwipe、tap、swipe、intercept 行为不回退。

### Task 4.3 迁移普通 App Hook 组

涉及入口：

- `HookStub2.packageLoaded(...)`
- `CustomHookApp.hookApp(...)`
- `MediaProviderHook.hook(...)`
- `NFCServiceHook.hook(...)`
- `MMSHook.hook(...)`

工作内容：

- 确认非 SystemUI 包仍会安装用户配置的 Method Hook。
- 对固定包 Hook 做 API 101 adapter 适配。
- 保持 `BuildPropInternal.APP_PACKAGE_NAME` 自保护逻辑，避免 Hook ShortX 自身。

验收标准：

- 用户配置 app Method Hook 能触发 `shortXManager.onHookedMethodCalled(...)`。
- MediaProvider、NFC、MMS 相关固定 Hook 仍工作。

## 10. Phase 5：日志、上下文和错误处理

### Task 5.1 建立 Xposed 日志门面

涉及文件：

- `ShortXLSPosedHookEntry.kt`
- `SystemUIHook.kt`
- `hookApp.kt`
- `MediaProviderHook.kt`
- `Hooks.kt`

工作内容：

- 新增统一日志实现，支持 API 101 的 `XposedInterface.log(...)`。
- legacy 场景下继续使用 `XposedBridge.log(...)` 或 Android `Log`。
- 业务代码只调用项目日志门面，不直接 import `XposedBridge`。

验收标准：

- Debug 构建能在 LSPosed 日志中看到入口、包加载、Hook 安装和 Hook 回调。
- Release 构建不产生过量刷屏日志。

### Task 5.2 优化异常隔离

工作内容：

- API 101 adapter 中每个 callback 都需要 `runCatching`，避免目标进程因 ShortX Hook 异常崩溃。
- 对关键 Hook 安装失败输出 class、method、process、package、lifecycle。
- 动态 Method Hook 失败不应影响其他规则安装。

验收标准：

- 单个 Hook 点失败不会中断同一进程其他 Hook。
- 日志能定位失败的规则、类名和方法签名。

## 11. Phase 6：混淆和打包

### Task 6.1 更新 ProGuard / R8 规则

涉及文件：

- `app/proguard-rules.pro`
- `app/proguard-rules-dynamic.pro`

当前已有：

- `-keep class de.robv.android.xposed.**{*;}`
- `-keepclassmembers class tornaco.apps.shortx.services.xposed.hooks.hook** {*;}`
- `-dontwarn io.github.libxposed.api.*`

工作内容：

- keep API 101 入口：`ShortXLSPosedHookEntry`。
- keep API 101 Hooker 类和静态 `before` / `after` 方法。
- keep `ShortXHookParam`、Hook adapter、Hook registry。
- 如果保留 legacy，保持旧入口 keep。
- 如果移除 legacy，清理不再需要的 `de.robv.android.xposed` keep / dontwarn。

验收标准：

- Release 包中 API 101 入口和 Hooker 未被删除。
- Release 包 Hook 行为与 Debug 包一致。

### Task 6.2 检查最终 APK 中 Xposed 入口资源

工作内容：

- 解包 release APK 检查 Manifest metadata。
- 检查入口类存在。
- 检查 `xposed_scope` 资源存在。

验收标准：

- 框架能识别模块。
- 作用域正确展示。

## 12. Phase 7：验证清单

### Task 7.1 编译验证

建议命令：

```bash
./gradlew :patch-xposed:patch-common:compileKotlin
./gradlew :patch-xposed:patch-systemui:compileKotlin
./gradlew :patch-xposed:patch-main:compileKotlin
./gradlew :app:assembleDebug
```

如果改动 AIDL 或 proto，按 `AI/README.md` 要求先执行：

```bash
./gradlew :core-api:generateAidlFiles
./gradlew :core-api:generateProto
```

验收标准：

- 以上任务通过。
- 没有新增 API 82 / API 101 类型冲突。

### Task 7.2 入口验证

检查项：

- `ShortXLSPosedHookEntry.init` 是否输出日志。
- `onSystemServerLoaded` 是否进入 `HookStub2.systemServerLoaded(...)`。
- `onPackageLoaded` 是否进入 `HookStub2.packageLoaded(...)`。
- `processName` 是否正确，特别是 SystemUI 主进程。

验收标准：

- system_server、SystemUI、普通 App 三类生命周期都能进入对应调度。

### Task 7.3 system_server 功能验证

检查项：

- `ShortXService.main()` 是否仍在 system_server hook 阶段启动。
- AMS / ATMS / Clipboard / Notification / Input / Window / Shortcut 相关 Hook 是否安装成功。
- 规则引擎是否能收到来自 system_server 的 fact。

验收标准：

- 自动化基础规则能执行。
- 日志无连续崩溃或 system_server 重启。

### Task 7.4 SystemUI 功能验证

检查项：

- SystemUI 主进程过滤正确。
- `SystemUIApplication.onCreate` Hook 成功。
- `AppGlobals` 和 `AppResources` 初始化成功。
- SystemUIClient attach 成功。
- input monitor 安装成功。
- Edge gesture、CornerSwipe、tap、swipe、intercept 正常。
- QS tile label/icon/custom tile Hook 正常。

验收标准：

- SystemUI 不崩溃。
- 手势和 QS 能按现有规则工作。

### Task 7.5 App Method Hook 验证

检查项：

- UI 中创建 Method Hook fact。
- 选择普通 App，配置 class、method、参数约束和 before/after。
- 触发目标方法。
- MVEL 表达式能读取 `param` 和 `helper`。
- `shortXManager.onHookedMethodCalled(...)` 被调用。
- `services` 收到 fact 并触发规则。

验收标准：

- 老的 Method Hook 配置模型不需要迁移数据。
- before 与 after 两类 Hook 都能触发。

### Task 7.6 Release 包验证

检查项：

- 安装 release 包。
- 重新启用模块和作用域。
- 重启目标 App / SystemUI / 设备。
- 检查入口、Hook 安装和规则触发。

验收标准：

- Release 包无 Hooker 被混淆导致的失效。

## 13. Phase 8：清理和文档

### Task 8.1 清理传统 API 直接引用

工作内容：

- 对 `patch-xposed` 运行搜索：`de.robv.android.xposed`、`XposedBridge`、`XposedHelpers`、`XC_MethodHook`、`AndroidAppHelper`。
- 对每个剩余引用标注原因：legacy adapter、兼容入口、待迁移业务点或可删除。

验收标准：

- 传统 API 引用只存在于 legacy adapter 或明确保留的兼容入口。

### Task 8.2 更新 AI 文档

涉及文件：

- `AI/patches/patch-xposed-patch-main.md`
- `AI/patches/patch-xposed-patch-common.md`
- `AI/patches/patch-xposed-patch-systemui.md`
- `AI/architecture/cross-module-architecture-map.md`

工作内容：

- 记录 API 101 入口成为主路径。
- 记录 `ShortXHookParam` / Hook adapter 的设计。
- 更新 Method Hook 流程。
- 更新 SystemUI hook 启动链路中与 API 101 相关的说明。

验收标准：

- 后续开发者能从 AI 文档理解新 Hook 基建。

### Task 8.3 清理历史 jar 和任务文档

工作内容：

- 如果不再兼容 legacy，删除 `api-82.jar` 依赖和旧入口。
- 如果继续兼容，在文档中标注 legacy 删除计划。
- 将升级过程中发现的 API 101 限制补充到 `Xposed-API-101-Upgrade-Guide.md` 或独立设计文档。

验收标准：

- 仓库中不会出现未解释的双 API 混用。

## 14. 风险点

| 风险 | 影响 | 缓解 |
| --- | --- | --- |
| API 101 不支持当前 lambda 式动态 Hook | `CustomHookUtil` 迁移困难 | 先验证统一 Hooker + registry 方案 |
| Method Hook 表达式依赖传统 `MethodHookParam` 细节 | 用户规则可能失效 | 用 `ShortXHookParam` 保持常用字段兼容 |
| SystemUI 初始化顺序变化 | 手势和 QS 功能失效 | 保留现有 `onCreate -> system ready -> installInputMonitor` 顺序 |
| Release 混淆删除 Hooker | Debug 正常、Release 失效 | 增强 keep 规则并做 release 实机验证 |
| 保留 legacy 导致两套 API 行为不一致 | 难以排查 Hook 问题 | 将新旧 API 差异封装在 adapter，不让业务层感知 |
| `AndroidAppHelper` 替代不完整 | MVEL helper 失效 | 建立 `ProcessContextProvider`，逐步替代 |

## 15. 建议执行顺序

1. 完成 Phase 0，确认 API 101 jar 和兼容策略。
2. 完成 Task 2.1 和 Task 2.2，先建立中立 Hook 参数和门面层。
3. 用一个简单 system_server Hook 点验证 API 101 adapter。
4. 迁移 `CustomHookUtil`，保证用户 Method Hook 能继续工作。
5. 迁移 SystemUI Hook，重点验证手势链路。
6. 迁移剩余业务 Hook 的传统 API 类型引用。
7. 更新 ProGuard，并跑 Debug / Release 实机验证。
8. 最后清理 legacy 引用和更新 AI 文档。
