# Change Log / 变更日志

## 2026-04-12

### Documentation and Architecture Notes / 文档与架构说明
- 阅读并梳理了 `patch-xposed/patch-systemui` 的手势链路，包括启动 Hook、输入监听安装、检测器栈、策略流转和服务发布。 / Read and documented the `patch-xposed/patch-systemui` gesture pipeline, including startup hooks, input monitor installation, detector stack, policy flow, and service publication.
- 在 `AI/` 下补充了 patch 侧手势架构和 UI -> service -> patch 端到端执行链路文档。 / Added AI documentation covering patch-side gesture architecture and the full UI -> service -> patch gesture execution path.
- 为四角滑动触发器、手势提示反馈、四角拦截模式补充了需求与设计文档。 / Added design and requirement notes for the corner swipe trigger, gesture hint feedback, and corner intercept mode.

### New Corner Swipe Trigger / 新的四角滑动触发器
- 新增 `CornerSwipeToCenter` 触发器，用于识别从屏幕四角向中心滑动的手势。 / Added a new `CornerSwipeToCenter` trigger that recognizes swipes from the four screen corners toward the center.
- 完成了 proto、AIDL、manager、service、UI 选择与配置、patch 检测链路的全链路接入。 / Completed the full integration across proto definitions, AIDL, manager wrappers, service layer, UI selector/configuration, and patch-side detection.
- 修复了后续集成问题，包括 protobuf 枚举命名冲突、proto/Kotlin 生成依赖问题以及无效图标常量。 / Fixed follow-up integration issues including protobuf enum naming conflicts, proto/Kotlin generation dependency issues, and an invalid icon constant.

### Gesture Hint Feedback / 手势提示反馈
- 在 `patch-systemui` 中新增运行时手势提示反馈能力。 / Added runtime gesture hint feedback in `patch-systemui` for gesture activation guidance.
- 经过多轮视觉调整，最终定稿为更轻量、锚定按下点的提示样式。 / Iterated the hint presentation several times and finalized a lighter touch-down anchored style based on repeated UI refinements.
- 为 `CornerSwipeToCenter` 和 `EdgeGesture` 都增加了按触发器维度的提示开关。 / Added per-trigger gesture hint switches for both `CornerSwipeToCenter` and `EdgeGesture`.
- 保持了边缘手势的兼容性：旧数据缺少该字段时默认不展示提示。 / Preserved backward compatibility for edge gestures so missing legacy hint config defaults to not showing the hint.

### Corner Intercept Mode / 四角拦截模式
- 先逆向理解了现有边缘手势的拦截行为。 / Reverse-engineered the existing edge gesture intercept behavior.
- 基于 `isIntercept` 为 `CornerSwipeToCenter` 实现了对应的拦截模式，贯通 UI、proto、service 与 patch 运行时。 / Implemented analogous intercept mode support for `CornerSwipeToCenter` using `isIntercept` across UI, proto, services, and patch runtime.

### Corner Trigger Preview and Runtime Settings / 四角触发器预览与运行时设置
- 新增触发器选择页的四角热区预览能力，走新的 `showCornerHint` 预览调用链。 / Added selector-page corner hot-zone preview support through the new preview call chain (`showCornerHint`).
- 调整 `IShortX.aidl`，把新增接口统一放到文件尾部。 / Reordered newly added methods in `IShortX.aidl` so newly introduced APIs remain grouped at the end of the file.
- 新增四角手势全局设置项：角落热区大小、最小滑动距离。 / Added corner-specific global settings for corner hot area size and minimum swipe distance.
- 将这些设置贯通到存储、AIDL、manager、service、预览和 patch 检测运行时。 / Wired these settings through storage, AIDL, manager, services, preview, and patch-side runtime detection.

### Key And Gesture Settings UX / 手势与按键设置页体验优化
- 在设置页明确说明触摸手势总开关同时控制边缘手势和四角手势。 / Clarified in settings UI that the touch gesture master switch controls both edge gestures and corner gestures.
- 将边缘手势设置和四角手势设置拆分成更清晰的独立分组/卡片。 / Split edge gesture settings and corner gesture settings into clearer separate UI groups/cards.

### Fact Selector Improvements / Fact 选择器优化
- 重新整理 Fact 选择器分类，使其更符合实际使用场景、更易扫描。 / Refactored the Fact selector categorization to be more practical and easier to scan.
- 参考 Action 选择器增加了顶部分类过滤栏。 / Added a top category filter row similar to the Action selector.
- 将 Fact 条目重组为 Apps、Input、Screen & time、State、Power、Sensor、System、Advanced 等更清晰的分组。 / Reorganized fact sections into clearer groups such as Apps, Input, Screen & time, State, Power, Sensor, System, and Advanced.

### Condition Selector Improvements / Condition 选择器优化
- 对 Condition 选择器应用了同样的分类与过滤改造。 / Applied the same selector treatment to the Condition selector.
- 增加顶部分类过滤栏，并把条件重组为 UI、Apps、Screen & time、Connectivity & state、Devices & hardware、System events、Advanced & misc 等更清晰的分组。 / Added a top category filter row and regrouped conditions into clearer categories such as UI, Apps, Screen & time, Connectivity & state, Devices & hardware, System events, and Advanced & misc.
- 新增“可用内存”条件，可按 MB 配置整数比较，例如“小于 512MB”。 / Added an available-memory condition with integer comparison in MB, for example "less than 512MB".
- 同时修复了整型条件编辑器之前默认把输入截断到 100 的限制，使大于 100 的阈值条件也能正常配置。 / Also fixed the generic integer-condition editor so values are no longer always capped at 100, allowing thresholds above 100 to be configured correctly.

### Current Location Action / 当前位置信息动作
- 新增系统动作“获取当前位置信息”，执行后会把当前 `latitude`、`longitude`、`provider`、`accuracy` 写入上下文数据。 / Added a new system action "Get current location info" that writes the current `latitude`, `longitude`, `provider`, and `accuracy` into context data.
- 该动作可在规则、直达动作和函数内部复用；如果函数需要返回位置结果，可以继续配合已有的“设置函数返回值”动作使用。 / This action can be reused in rules, direct actions, and functions; if a function needs to return location data, it can be combined with the existing "Set function return value" action.
- 服务侧定位逻辑运行在系统进程，不依赖应用 manifest 里的定位权限声明，因此没有引入额外权限配置。 / The location lookup runs in the system-process service side and does not rely on location permissions declared in the app manifest, so no extra manifest permission was added.
- 为该动作新增了可选配置：定位源偏好（自动 / 优先网络 / 优先 GPS）和定位超时毫秒数；会优先按配置尝试实时定位，超时后再回退到最近一次已知位置。 / Added optional configuration for this action: provider preference (auto / prefer network / prefer GPS) and timeout in milliseconds; it now tries live location using the configured preference first and falls back to the last known location on timeout.
- 为排查拿不到位置的问题，补充了关键调试日志，覆盖动作启动参数、启用的 provider、实时定位回调、超时回退和 last-known 路径。 / Added focused diagnostic logs for location lookup failures, covering action input parameters, enabled providers, live location callbacks, timeout fallback, and the last-known-location path.
- 进一步把内部定位异常改成直接抛错结束动作：例如 `isProviderEnabled`、`getCurrentLocation`、`getLastKnownLocation`、`getProviders(true)` 失败时，不再只记日志后继续回退，而是直接返回明确错误，便于用户在动作失败时看到具体原因。 / Tightened internal error handling so location-internal failures now abort the action immediately: when `isProviderEnabled`, `getCurrentLocation`, `getLastKnownLocation`, or `getProviders(true)` fails, the action no longer just logs and continues fallback, but instead returns a clear failure reason to the user.
- 新增系统动作“获取当前位置地址描述”，会先复用同一套当前位置解析逻辑，再通过 `Geocoder` 做逆地理编码，把 `addressLine`、`countryName`、`adminArea`、`locality`、`thoroughfare`、`postalCode` 等地址字段写入上下文数据。 / Added a new system action "Get current location address" that reuses the same current-location resolution flow and then reverse-geocodes it through `Geocoder`, writing address fields such as `addressLine`, `countryName`, `adminArea`, `locality`, `thoroughfare`, and `postalCode` into context data.
- 按最新架构调整，这两个位置动作已迁移到 app 进程执行，并复用了 `AppServiceProxy` 风格的 binder 通道；服务侧现在只负责转发请求、接收结果和写回上下文。 / Adjusted the architecture so both location actions now execute in the app process through an `AppServiceProxy`-style binder bridge; the service side now only forwards the request, receives the result, and writes context values.
- `ui` 模块新增了应用侧定位权限声明与设置页授权入口，明确区分精确定位和 Android 10+ 的后台定位授权；未授权时 app 侧 service 会直接返回清晰错误。 / Added app-side location permission declarations and a settings entry in the `ui` module that explicitly guides precise-location and Android 10+ background-location authorization; when permission is missing, the app-side service now returns a clear failure immediately.
- 顺手修正了 app 侧实时定位实现的兼容性：Android 11+ 使用 `getCurrentLocation(...)`，Android 10 及以下回退到 `requestSingleUpdate(...)`，避免低版本设备运行时调用缺失 API。 / Also fixed compatibility in the app-side live-location implementation: Android 11+ uses `getCurrentLocation(...)`, while Android 10 and below fall back to `requestSingleUpdate(...)`, avoiding runtime failures from missing newer APIs on older devices.

### Method Hook Related Functionality / 函数 Hook 相关功能
- 补充了函数 Hook 日志页相关能力，复用共享日志列表组件统一承接日志交互。 / Added method-hook related log-page capability by reusing the shared log list component for consistent log interactions.
- 现在函数 Hook 日志页与 JS 日志页、HTTP 日志页保持同一套复制与分享行为。 / Method-hook log pages now share the same copy and share interactions as JS log and HTTP log pages.

### Log Viewer UX / 日志查看页体验优化
- 为共享日志列表增加长按复制能力，覆盖 JS log、HTTP log 和 method-hook log 页面。 / Added long-press copy support to the shared log list used by JS log, HTTP log, and method-hook log pages.
- 扩展共享日志页操作能力：顶部一键复制全部日志、单行长按菜单、复制当前行、从当前行复制到末尾、分享当前行。 / Extended the shared log viewer actions with copy all logs from the top app bar, a long-press menu for a single log line, copy current line, copy from current line to the end, and share current line.
- 实现保持集中在共享日志 UI 层，三个日志页同步受益。 / Kept the implementation centralized in the shared log UI so all three log pages benefit together.

### Validation / 验证
- 在实现过程中多次对相关模块进行了编译验证。 / Repeatedly validated the affected modules during implementation.
- 最近一次相关验证已通过：`./gradlew :ui:compileRowDebugKotlin --console=plain`。 / Latest related verification completed successfully with `./gradlew :ui:compileRowDebugKotlin --console=plain`.

## Maintenance Rule / 维护规则
- 后续在本仓库中的所有代码改动，都需要在同一次变更中同步更新根目录这个文件。 / For all future code changes made in this workspace, update this file in the repository root as part of the same change.