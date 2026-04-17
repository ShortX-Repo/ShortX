# Change Log / 变更日志

## 2026-04-17

### Legacy IMS Edge Gesture Sequence Migration / 旧版 IMS 边缘手势序列迁移
- 把 SystemUI 侧的新式边缘手势二段序列识别思路迁回旧 IMS 路径：旧 `GestureService` 现在可以识别 `childSteps`，`Detector` 支持预定义边缘和 `customRect` 两类区域，并在手势触发时直接按系统 UI 同样的序列候选流程发回规则引擎。 / Migrated the SystemUI-style edge-gesture sequence detection back into the legacy IMS path: the old `GestureService` now recognizes `childSteps`, `Detector` supports both predefined edges and `customRect` areas, and gesture triggering now follows the same sequence-candidate flow before publishing back into the rule engine.
- 进一步收敛了旧 `GestureService` 的职责，去掉额外的手势回调桥，直接由服务侧按规则引擎的 `shouldFire` / `publishFact` 路径发出普通边缘手势事实，同时保留序列手势的直接发布逻辑。 / Further narrowed the legacy `GestureService` responsibility by removing the extra gesture callback bridge and publishing normal edge-gesture facts directly through the rule engine's `shouldFire` / `publishFact` path, while keeping the direct publish flow for sequence gestures.
- 删除了旧手势链路里的“拦截模式”分支：`GestureService` 不再维护特殊拦截矩形，边缘手势和四角手势的 selector 也不再暴露对应开关，当前仅保留 InputFilter + 正常手势匹配路径。 / Removed the legacy gesture-chain intercept-mode branch: `GestureService` no longer maintains special intercept rectangles, the edge and corner gesture selectors no longer expose the corresponding switch, and the code now keeps only the InputFilter plus normal gesture-matching path.
- 进一步删除了服务侧的子手势运行时逻辑：旧 `GestureService` / `Detector` 不再做 `childSteps` 序列跟踪、候选匹配和发布，当前只保留普通边缘手势与自定义矩形触发。 / Further removed the service-side child-gesture runtime logic: the legacy `GestureService` / `Detector` no longer perform `childSteps` sequence tracking, candidate matching, or publishing, and now keep only normal edge gestures plus custom-rect triggering.

### Legacy IMS Gesture Path Coexistence / 旧版 IMS 手势链路共存
- 把历史 `InputManagerService.filterInputEvent` 边缘手势链路重新接回 active 代码：恢复了 `patch-main` 的 `filterInputEvent` / `setInputFilterEnabled` hook，补回了 active `GestureService` 的 legacy 输入分发、旧 detector 以及规则变化回调桥接，当前 SystemUI 路径保持不变。 / Reconnected the historical `InputManagerService.filterInputEvent` edge-gesture chain back into the active code: restored the `patch-main` `filterInputEvent` / `setInputFilterEnabled` hook, brought back the active `GestureService` legacy input dispatch, old detector, and rule-change callback bridge, while keeping the current SystemUI path intact.

## 2026-04-16

### Edge Gesture Sub-Gesture Runtime / 边缘手势子手势运行时调整
- 继续补齐了旧版 IMS 边缘手势方案的历史辅助类参考副本，包括 `GesturePlayer`、`GestureRecorder`、`PointerEventDispatcher`、`WithPointerLocation`、`InputChannelCompat`、`InputContextCompat`、`InputManagerGlobalFunc`、`InputMonitorCompat`、`InputService`、`InputShellCommand` 以及 Oplus 输入监控辅助类，便于后续对照重构。 / Restored more legacy IMS edge-gesture auxiliary source references, including `GesturePlayer`, `GestureRecorder`, `PointerEventDispatcher`, `WithPointerLocation`, `InputChannelCompat`, `InputContextCompat`, `InputManagerGlobalFunc`, `InputMonitorCompat`, `InputService`, `InputShellCommand`, and the Oplus input-monitor helpers, so the upcoming refactor can compare against the older implementation more easily.
- 从历史提交 `6cad2d45d7ef7cb3cd2d825105e45bbc6b2172a2` 恢复了一批旧版 IMS `filterInputEvent` + `setInputFilter` 边缘手势核心源码到 `AI/legacy-edge-gesture-ims-source/`，用于后续重构时直接参考旧的 `InputManagerHook`、`GestureService`、`Detector`、`InputFilterInstaller` 与 `IMSFunc` 实现。 / Restored a first batch of legacy IMS `filterInputEvent` plus `setInputFilter` edge-gesture source files from historical commit `6cad2d45d7ef7cb3cd2d825105e45bbc6b2172a2` into `AI/legacy-edge-gesture-ims-source/` so the old `InputManagerHook`, `GestureService`, `Detector`, `InputFilterInstaller`, and `IMSFunc` implementations are directly available for the upcoming refactor.
- 新增一份 `filterInputEvent` 迁移设计文档，规划把边缘手势从当前 SystemUI `InputMonitor` 架构迁移到 `android` 进程的 `InputManagerService.filterInputEvent` hook，并明确复用现有 fact/rule 管线、输入回放、反馈宿主拆分与分阶段落地策略。 / Added a dedicated `filterInputEvent` migration design document that plans moving edge gesture interception from the current SystemUI `InputMonitor` path to an `android`-process `InputManagerService.filterInputEvent` hook, while preserving the existing fact/rule pipeline and defining replay, feedback-host split, and phased rollout strategy.
- 根据历史提交补充了迁移文档的考古结论，明确旧版 IMS 手势路径实际是 `filterInputEvent -> GestureService.filterMotionEvent -> Detector` 再配合 `ACTION_CANCEL` 注入接管，而不是完整原始事件回放；同时标注了切到 SystemUI `InputMonitor` 的关键提交节点。 / Extended the migration design document with repository-history findings that the old IMS gesture path was effectively `filterInputEvent -> GestureService.filterMotionEvent -> Detector` plus synthetic `ACTION_CANCEL` takeover rather than full original-event replay, and documented the key commits that moved the feature to the SystemUI `InputMonitor` path.
- 进一步调整 `Hover` 子手势语义：达到停留时长后不再在按住过程中立即触发，而是仅在同一触摸流抬手时根据停留时长和位移半径结算是否成功。 / Refined the `Hover` child-gesture semantics so reaching the hover duration no longer fires immediately while the finger is still down; success is now decided only when that same touch stream is released, based on both hold duration and movement radius.
- 调整 `patch-systemui` 的子手势 sequence detector，移除了主手势成功后等待子手势的自动超时；现在只要用户不松手，就会持续等待后续子手势。 / Updated the `patch-systemui` child-gesture sequence detector to remove the automatic timeout while waiting after the primary edge gesture; the detector now keeps waiting for the next step as long as the finger remains down.
- 为该行为补充了聚焦单元测试，覆盖“长时间等待但未松手后仍能成功触发方向型子手势”的场景。 / Added focused unit coverage for the case where the user waits a long time without lifting the finger and can still successfully trigger a directional child gesture.
- 调整 sequence 提示器样式：方向型子手势继续使用箭头提示，`Hover` 子手势改为停留环形提示，使等待态更容易理解。 / Refined the sequence hint presentation so directional child gestures keep the arrow indicator while `Hover` now uses a hold-ring indicator, making the waiting state easier to understand.
- 收口了后端残留的 `StepTimeout` 配置链，移除了不再使用的 AIDL、manager、service 存储与 patch 侧读取路径，避免产品语义与运行时配置继续冲突。 / Removed the obsolete backend `StepTimeout` configuration chain, including unused AIDL, manager, service storage, and patch-side reads, so runtime configuration no longer conflicts with the updated product semantics.
- 删除了已无代码引用的 `ui.settings.edge.gesture.sub.step.timeout*` 多语言翻译 key，避免 i18n 资产继续保留失效文案。 / Removed the unused `ui.settings.edge.gesture.sub.step.timeout*` translation keys from the checked-in locale assets so obsolete copy no longer remains in i18n data.
- 收紧了边缘手势选择器的子手势编辑行为，同一个 `EdgeGesture` 触发器现在最多只能配置一个子手势；旧数据进入编辑时也会自动按首个子手势收口。 / Tightened the edge gesture selector so a single `EdgeGesture` trigger can now configure at most one child gesture, and older multi-step edit data is normalized to the first child step when reopened.

### Edge Gesture Sub-Gesture Requirements / 边缘手势子手势需求调整
- 更新了 `requirements/EdgeGestureSubGesture.md`，将产品语义改为主手势成功后等待子手势不再有自动超时，只要用户不松手就持续等待。 / Updated `requirements/EdgeGestureSubGesture.md` so waiting for a child gesture after the primary edge gesture no longer auto-times out and remains active as long as the user keeps holding.
- 在需求中新增了等待子手势期间的运行时提示器要求，要求在等待阶段持续向用户提示当前正在等待的子手势。 / Added a runtime indicator requirement for the child-gesture waiting phase so users receive continuous feedback while the system is waiting for the next step.
- 同步更新了 `requirements/EdgeGestureSubGesture-tech-design.md` 和 `requirements/EdgeGestureSubGesture-tasks.md`，把实现方向改成“等待阶段无超时、Hover 自身仍有时长阈值、补充等待态提示器实现与验证”。 / Updated `requirements/EdgeGestureSubGesture-tech-design.md` and `requirements/EdgeGestureSubGesture-tasks.md` to reflect the new implementation direction: no timeout while waiting for the child gesture, `Hover` still keeps its own duration threshold, and a waiting-state indicator must be implemented and validated.

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
- 进一步把 `AppLocationService` 提升为真正的 `foregroundServiceType=location` 前台定位服务，请求期间会显示持续通知，避免 app 进程后台定位时没有真正拉起 GPS 或被系统限流。 / Further promoted `AppLocationService` into a real `foregroundServiceType=location` foreground location service with an ongoing notification during requests, to avoid background app-process location calls never truly activating GPS or being throttled by the system.
- 调整 app 侧 provider 请求策略，不再串行等待单个 provider 吃完整个超时，而是对候选 provider（如 `fused`、`gps`）并发发起实时定位请求，谁先返回就采用谁，避免 `fused` 卡死时 `gps` 根本没有机会参与。 / Adjusted the app-side provider request strategy so candidate providers such as `fused` and `gps` are queried in parallel instead of serially consuming the full timeout one by one, preventing `fused` from starving `gps` when it stalls.
- 开始把 app 侧位置实现切换到高德定位 SDK：`AppLocationServiceSupport` 不再依赖系统 `LocationManager`/`Geocoder`，而是通过 `AMapLocationClient` 单次定位获取当前位置和地址字段；同时补充了高德隐私初始化和 API Key 的 manifest 占位配置。 / Began migrating the app-side location implementation to the AMap location SDK: `AppLocationServiceSupport` no longer depends on the system `LocationManager`/`Geocoder`, and instead uses one-shot `AMapLocationClient` requests for both current location and address fields, together with AMap privacy initialization and manifest API-key placeholder wiring.
- 按高德官方最小接入说明清理了旧的前台定位服务方案：移除了 `AppLocationService` 的前台通知逻辑、相关字符串资源，以及 manifest 中不再需要的 `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_LOCATION` 权限与 `foregroundServiceType` 配置。 / Cleaned up the old foreground-location-service path to match the minimal AMap integration guidance: removed foreground-notification logic from `AppLocationService`, deleted related string resources, and dropped the no-longer-needed `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_LOCATION` permissions and `foregroundServiceType` manifest configuration.

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