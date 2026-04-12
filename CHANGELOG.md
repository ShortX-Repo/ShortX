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