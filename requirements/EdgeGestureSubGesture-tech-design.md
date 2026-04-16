边缘手势子手势技术方案

一、设计目标

在现有 ShortX 架构中，为 `EdgeGesture` 增加“主手势 + 子手势”的顺序识别能力。

方案目标如下：

1. 尽量复用现有 UI -> services -> patch-systemui -> services -> rule engine 的整条链路。
2. 尽量避免破坏现有单段 `EdgeGesture` 行为。
3. 将子手势设计为现有 `EdgeGesture` 的能力扩展，而不是新增平行 fact 类型。
4. 第一阶段 UI 只支持一个子手势，但内部数据结构允许未来扩展为多步序列。
5. 明确支持 `Hover` 作为子手势步骤，并保证其定义基于触摸流而不是系统 hover 事件。
6. 主手势成功后，在用户松手前不设置等待子手势的自动超时。
7. 在等待子手势阶段向用户展示明确的运行时提示器。

二、总体方案

建议把本次能力实现为：扩展现有 `EdgeGesture`。

它在系统中的生命周期如下：

1. UI 规则编辑器创建或编辑 `Fact.EdgeGesture`。
2. 保存规则时转换为扩展后的 `Facts.proto::EdgeGesture`。
3. services 持久化该规则。
4. patch-systemui 启动后，从 services 读取所有启用规则，提取所有 `EdgeGesture` 事实。
5. patch 在现有边缘热区命中基础上，把单段规则与序列规则分流。
6. 对单段规则保持现有即时识别和触发逻辑。
7. 对包含子手势的规则，交给新的 `EdgeGestureSequenceDetector` 继续识别后续步骤。
8. 只有当整段序列全部匹配成功后，patch 才构造运行时 `EdgeGesture` 并通过 binder 回灌到 `ShortXService`。
9. `ShortXService` 调用 `RuleService.publishFact(...)` 发布事实。
10. `DefaultRuleEngine` 和 `FactMatcherImpl` 完成事实匹配和动作执行。

三、为什么扩展现有 EdgeGesture

不建议把该能力设计为新的独立事实类型，原因如下：

1. 触发入口仍然是边缘热区。
现有边缘位置、自定义矩形热区、前台应用约束、手势提示、拦截模式都属于 `EdgeGesture` 语义。

2. 用户理解上仍然是一个高级边缘手势。
用户配置的是“从某个边缘开始，再做一个后续动作”，而不是两个互相独立的规则事实。

3. 运行时入口仍然依赖当前边缘命中逻辑。
patch 侧第一阶段并没有变化，变化只发生在主手势成功后的后续识别阶段。

因此建议扩展 `EdgeGesture`，而不是新建平行 fact。

四、协议设计建议

（一）新增 step enum 与 step message

当前 `Gesture` enum 主要用于旧的一段式边缘手势，语义与未来多步序列不完全一致。

建议在 `core-api/src/main/proto/shortx/Facts.proto` 中新增：

1. `enum EdgeGestureStepType`
2. `message EdgeGestureStep`

建议枚举值如下：

1. `SwipeLeft`
2. `SwipeRight`
3. `SwipeUp`
4. `SwipeDown`
5. `Hover`

如需为后续扩展留口，也可同时补齐：

1. `SingleTap`
2. `DoubleTap`
3. `LongTap`

建议字段如下：

1. `EdgeGestureStepType type = 1;`

说明：

1. 使用新的 step enum，避免继续复用旧 `Gesture` 导致语义混淆。
2. `Hover` 在该层成为一等能力。

（二）扩展 Facts.proto::EdgeGesture

建议在 `Facts.proto::EdgeGesture` 中新增：

1. `repeated EdgeGestureStep childSteps = 11;`

保留旧字段：

1. `Gesture gesture = 2;`

原因：

1. 旧规则仍然依赖它进行兼容解析。
2. 一级主手势继续直接复用现有 `Gesture` 枚举，不新增新的一级 step 字段。

（三）兼容策略

建议兼容策略如下：

1. 历史规则如果只有 `gesture`，仍然按单段边缘手势处理。
2. 新规则继续使用 `gesture` 表示一级主手势，只额外写入 `childSteps`。
3. proto -> UI model 时，直接读取原有 `gesture` 作为一级主手势。
4. matcher 层需要支持 `gesture + childSteps` 的组合匹配。

五、UI 层设计方案

（一）UI 编辑模型

建议在 `ui-base` 的 `Fact.EdgeGesture` 中扩展：

1. 原有 `gesture`
2. `childSteps: List<EdgeGestureStepModel>`

建议新增：

1. `EdgeGestureStepModel`

候选值与 proto `EdgeGestureStepType` 一致。

第一阶段 UI 限制如下：

1. 必须有一个主手势。
2. 最多配置一个子手势。
3. `childSteps` 虽然是列表，但编辑器只暴露 0 或 1 个元素。

（二）事实选择器

建议修改：

1. `ui/.../fact/selector/EdgeGestureSelectDialog.kt`

调整方式如下：

1. 现有 gesture 选择器改名为主手势选择器。
2. 新增子手势配置区。
3. 子手势区默认关闭或为空。
4. 用户选择后可以清空该子手势。

（三）摘要与展示

建议在 FactI18N 和相关 mapper 中支持序列展示。

摘要建议表达为：

1. `底部中间 -> 上滑`
2. `底部中间 -> 上滑 -> 悬停`

（四）等待态提示器

建议在主手势成功后立即展示一个轻量级等待态提示器。

建议目标如下：

1. 提示器锚定在当前手势 anchor 附近，而不是固定在屏幕角落。
2. 对方向型子手势，显示一个明确方向箭头和“继续滑动”语义。
3. 对 `Hover` 子手势，显示一个保持按住的环形或停留型提示。
4. 等待阶段不自动消失，直到成功、失败或松手。
5. 提示器应与现有 `EdgeGestureHintDetector` / hint controller 体系保持同层协作，而不是新造一套完全独立的窗口栈。

六、services 层设计方案

（一）运行时发布入口

建议继续复用现有边缘手势的发布通道。

即：

1. patch 侧最终仍调用 `fireEdgeGesture(...)` 或等价边缘手势发布方法。

原因：

1. 这是现有 `EdgeGesture` 的能力扩展。
2. 不需要为子手势单独再造一条新的事实发布链路。

（二）ShortXService 处理

建议在 `ShortXService.kt` 中：

1. 继续解析 runtime `EdgeGesture`。
2. 将原有 `gesture` 和新增 `childSteps` 一并带入发布事实。
3. 保持 `downX/downY` 等上下文字段的注入方式不变。

（三）FactMatcherImpl

建议扩展 `EdgeGesture` 的匹配规则：

1. legacy 规则：沿用旧 `gesture` 匹配逻辑。
2. 新规则：比较 `gesture + childSteps` 序列。
3. 当配置态和运行时序列完全一致时，视为匹配成功。

七、patch-systemui 层设计方案

（一）识别器设计原则

建议新增独立序列识别器，不把多步状态机硬塞进现有 one-shot 逻辑中。

建议新增例如：

1. `EdgeGestureSequenceDetector.kt`

它的职责：

1. 绑定一次触摸流。
2. 跟踪一个 pointer 的连续动作。
3. 从主手势成功后开始识别后续 step。
4. 当全部步骤成功时才返回成功结果。
5. 当发生方向错误、抬手、pointer 丢失时立即取消。
6. 在等待阶段不因为时间流逝自动取消。

（二）保留现有热区识别

以下能力不建议改动：

1. `Detector.detectArea(...)` 的边缘 / 自定义矩形命中逻辑。
2. `DetectorServiceImpl` 中现有 intercept 相关缓存与快速判断。

第一阶段仍然是：

1. 输入起点命中某个边缘热区。

（三）状态机设计

建议状态如下：

1. `Idle`
2. `TrackingPrimary`
3. `WaitingNextStep(stepIndex)`
4. `HoverPending(stepIndex)`
5. `Succeeded`
6. `Cancelled`

建议跟踪以下运行时字段：

1. `pointerId`
2. `matchedRule`
3. `stepIndex`
4. `stepAnchorX`
5. `stepAnchorY`
6. `stepStartedAtMillis`
7. `downX`
8. `downY`

说明：

1. `stepStartedAtMillis` 仅用于需要时间阈值的步骤，例如 `Hover`。
2. 对“等待用户开始下一步”的空闲等待阶段，不应使用该时间戳做自动超时失败判断。

（四）步骤识别策略

方向型步骤：

1. 从上一步完成锚点开始计算位移向量。
2. 满足最小位移阈值后，再校验方向。
3. 方向校验可复用点积 / 夹角方法。
4. 成功后推进到下一步，并把当前点作为新的锚点。
5. 在用户真正做出满足最小位移阈值的动作前，系统持续保持等待，不因静止时间过长失败。

悬停步骤：

1. 主手势或上一步成功后进入 `HoverPending`。
2. 同一 pointer 必须保持按下。
3. 位移必须持续保持在一个小半径内。
4. 持续时间达到阈值后，判定悬停成功。
5. 一旦超出半径或抬手，则判定失败。

（五）默认阈值建议

建议先在 detector 内部定义固定常量：

1. 子手势最小位移：`72dp`
2. 方向最大偏差角：`30°`
3. 悬停半径：`18dp ~ 24dp`
4. 悬停时长：`300ms`

其中“等待用户开始子手势”的阶段不建议设置固定超时。
后续如有需要，再把悬停相关阈值抽为配置项。

八、Detector 集成方案

当前 `Detector` 在识别到单段手势后会立即走 `handleEdgeGestureEvent(...)`。

建议改为分流：

1. 单段规则：保留当前即时识别和触发逻辑。
2. 序列规则：主手势成功后，不立即触发，而是交给 `EdgeGestureSequenceDetector` 继续跟踪。
3. sequence detector 进入等待阶段后，立即驱动等待态提示器显示。

这样可以做到：

1. legacy 路径保持稳定。
2. 新能力的状态机复杂度被限制在独立类中。

九、Hover 定义

本需求中的 `Hover` 不依赖 Android 原生 hover 事件。

推荐定义如下：

1. 手指仍保持按下。
2. 当前位置相对上一步完成点的位移持续小于悬停半径。
3. 持续达到阈值时长后，判定为 `Hover` 成功。

这样可以确保：

1. 与现有 `MotionEvent` 触摸链路兼容。
2. 不受设备是否具备真正 hover 能力影响。

十、风险与回归控制

主要风险如下：

1. 旧单段边缘手势回归。
2. 无等待超时后，sequence 跟踪在长按场景下停留过久，影响体验或资源占用。
3. 多步状态机与现有拦截模式交互后，产生误触发或漏触发。
4. 悬停阈值选择不合适，导致体验不稳定。

对应控制策略：

1. legacy 规则严格保留旧路径。
2. sequence detector 做成独立组件，减少对现有 Detector 主逻辑的侵入。
3. 明确把“等待态无超时”和“Hover 自身有时长要求”区分实现，避免语义混淆。
4. 优先写单元测试覆盖方向成功、方向失败、长时间等待不失败、抬手取消、悬停成功、悬停失败。

十一、实施顺序建议

建议按以下顺序落地：

1. 先改 proto 和 UI-base model，建立新旧字段兼容映射。
2. 再改 `EdgeGestureSelectDialog`，先让规则可配置、可回填、可展示。
3. 然后新增 `EdgeGestureSequenceDetector` 并接入 `Detector` / `DetectorServiceImpl`。
4. 接着补齐等待态提示器。
5. 最后扩展 `FactMatcherImpl`、补齐单测和手工验证。

十二、验收结论标准

实现完成后，应满足：

1. 无子手势规则行为与当前版本一致。
2. 配置一个方向型子手势后，只有主手势 + 子手势连续成功才触发规则。
3. 配置 `Hover` 子手势后，只有主手势成功并停留满足阈值才触发规则。
4. 主手势成功后即使等待较长时间，只要用户不松手，系统仍保持对子手势的等待。
5. 方向错误、抬手都不会误触发规则。
6. 等待态提示器能正确显示并在成功、失败、松手后消失。
7. 拦截模式、自定义热区、前台应用约束等现有边缘能力仍可与子手势共存。