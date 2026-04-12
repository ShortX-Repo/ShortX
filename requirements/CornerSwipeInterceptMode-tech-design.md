四角向中心滑动手势拦截模式技术方案

一、设计目标

在现有 `CornerSwipeToCenter` 架构上增加“拦截模式”能力，同时尽量复用现有边缘手势已经验证过的整体思路：

1. UI 侧配置一个布尔开关。
2. proto 持久化该配置。
3. patch-systemui 在规则缓存阶段构建用于早期偷取输入流的快速判定数据。
4. 输入监控阶段在最终识别成功前就允许调用 `pilferPointers()`。
5. 真正的规则触发仍然复用现有四角识别器和事实发布链路。

二、总体思路

建议把四角手势拦截模式设计成与边缘手势相同的“两阶段模型”：

1. 早期拦截阶段：决定是否偷取输入流。
2. 最终识别阶段：决定是否真的触发 `CornerSwipeToCenter` 事实。

该设计的关键点在于：

1. 拦截模式不改变 `CornerSwipeToCenterDetector` 的成功条件。
2. 拦截模式只扩展 patch 侧的输入所有权策略。
3. 拦截模式的几何判定必须与四角热区定义保持一致。

三、协议设计

（一）proto 扩展

建议在 `core-api/src/main/proto/shortx/Facts.proto` 的 `CornerSwipeToCenter` 中新增：

1. `optional bool isIntercept = 5;`

推荐使用 `optional bool` 而不是普通 `bool`，原因如下：

1. 需要区分“历史规则没有该字段”和“新规则明确设置为 false”。
2. 拦截模式是行为语义，不是纯展示字段，兼容性默认值必须可控。

兼容性建议：

1. absence = false

即历史规则没有该字段时，默认不拦截。

（二）UI-base 模型

在 `Fact.CornerSwipeToCenter` 中新增：

1. `isIntercept: Boolean = false`

并同步更新：

1. `FactToProtoFact.kt`
2. `ProtoAnyToFact.kt`

反序列化策略应保持为：

1. `hasIsIntercept() && isIntercept`

四、UI 设计

（一）选择器界面

在 `CornerSwipeToCenterSelectDialog.kt` 中新增一个 `ShortXSwitch`：

1. label 复用现有 `ui.fact.key.event.adv.is.intercept.mode`
2. 与 `enableGestureHint` 同页展示

建议顺序：

1. 角位置选择
2. 手势指示器开关
3. 拦截模式开关

（二）事实摘要展示

在 `FactI18N.kt` 中为 `Fact.CornerSwipeToCenter` 的摘要追加：

1. 开启拦截模式时显示“拦截模式”
2. 关闭时不额外展示

与边缘手势保持一致即可，不需要单独新增新的文案 key。

五、patch-systemui 架构设计

（一）为什么不能只改 CornerSwipeToCenterDetector

`CornerSwipeToCenterDetector` 的职责是识别：

1. 起点是否在角圆区
2. 方向是否达标
3. 距离是否达标

而边缘手势的拦截模式实际上是输入监控层的“早期偷取”策略，落点在：

1. `InputMonitorInstaller.kt`
2. `DetectorServiceImpl.shouldStealMotionEvent(...)`

因此四角拦截模式的核心也必须落在 `DetectorServiceImpl`，而不是只在 detector 内部增加一个布尔判断。

（二）缓存设计

目前 `DetectorServiceImpl` 已有：

1. `enabledCornerSwipeToCenterFacts`
2. `interceptMotionPortRects`
3. `interceptMotionLandRects`

建议新增一个角手势拦截缓存，例如：

1. `interceptCornerSwipeToCenterFacts`

类型可以直接使用：

1. `MutableSet<CornerSwipeToCenter>`

原因：

1. 当前角手势拦截只依赖 `corner` 和 `isIntercept`
2. 缓存完整 fact 更简单，后续如果角热区参数可配置，也不需要重新设计缓存结构

（三）缓存刷新逻辑

在 `updateQuickCheckDataOnRuleChange()` 中处理 `CornerSwipeToCenter` 时：

1. 所有启用且未禁用的四角事实继续加入 `enabledCornerSwipeToCenterFacts`
2. 其中 `isIntercept == true` 的事实额外加入 `interceptCornerSwipeToCenterFacts`

这样可保持与边缘手势“enabled facts”和“intercept cache”分离的模式一致。

（四）早期偷取判定

扩展 `DetectorServiceImpl.shouldStealMotionEvent(input: MotionEvent)`：

当前逻辑：

1. 仅判断输入点是否落在边缘/自定义矩形拦截区内

目标逻辑：

1. 若命中边缘拦截区，则返回 true
2. 否则继续判断是否命中任一启用拦截模式的角圆区
3. 若命中角圆区，则返回 true
4. 否则返回 false

角圆区命中算法必须与四角识别器一致：

1. 圆心为四个屏幕角
2. 半径为 `0.18 * min(screenWidth, screenHeight)`

建议把角圆区命中算法抽成 `DetectorServiceImpl` 内的独立辅助函数，或者提取成共享工具函数，避免与 `CornerSwipeToCenterDetector` 发生参数漂移。

六、Detector 层交互

`Detector.kt` 不需要为了拦截模式做结构性修改。

原因：

1. `InputMonitorInstaller` 已经先调用 `detector.consumeEvent(...)`
2. 若未消费，再调用 `service.shouldStealMotionEvent(...)`
3. 一旦偷取成功，后续同一输入流仍会继续进入 detector

因此四角拦截模式只要扩展 `DetectorServiceImpl.shouldStealMotionEvent(...)` 即可自然接入现有输入链路。

七、与手势提示器的关系

四角手势当前已经支持：

1. `enableGestureHint`
2. `CornerSwipeToCenterDetector` 内部提示控制

本期不应让拦截模式依赖提示器状态。

正确关系应为：

1. `enableGestureHint` 控制是否显示提示
2. `isIntercept` 控制是否早期偷取输入流

两者独立配置、独立生效。

八、兼容性设计

对于旧规则：

1. 如果没有 `isIntercept` 字段，解析为 `false`

对于新规则：

1. 明确打开时才加入角拦截缓存
2. 关闭时保持当前行为不变

九、建议实施顺序

1. 扩展 proto 和 UI-base 模型。
2. 更新 `CornerSwipeToCenterSelectDialog.kt`。
3. 更新 `FactI18N.kt` 摘要展示。
4. 扩展 `DetectorServiceImpl` 增加角拦截缓存。
5. 扩展 `shouldStealMotionEvent(...)` 做角圆区判定。
6. 做编译验证。
7. 做实机验证，重点观察与现有边缘拦截区的共同生效情况。

十、风险点

1. 若角圆区判定与 detector 内部实现不一致，可能出现“已偷取但最终永远不触发”的体验偏差。
2. 若后续把拦截逻辑错误地耦合到提示器逻辑，可能导致视觉开关影响输入行为。
3. 若兼容性默认值处理错误，旧规则可能在升级后意外开始拦截。

十一、涉及文件

1. `core-api/src/main/proto/shortx/Facts.proto`
2. `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/Fact.kt`
3. `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/FactToProtoFact.kt`
4. `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/ProtoAnyToFact.kt`
5. `ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/mapper/FactI18N.kt`
6. `ui/src/main/java/tornaco/apps/shortx/ui/addrule/fact/selector/CornerSwipeToCenterSelectDialog.kt`
7. `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/DetectorServiceImpl.kt`
8. `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/gesture/CornerSwipeToCenterDetector.kt`
9. `patch-xposed/patch-systemui/src/main/java/tornaco/apps/shortx/services/xposed/hooks/hook/systemui/input/opls/InputMonitorInstaller.kt`