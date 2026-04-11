四角向中心滑动手势技术方案

一、设计目标

在现有 ShortX 架构中新增一种新的手势事实：四角向中心滑动。

方案目标如下：

1. 尽量复用现有 UI -> services -> patch-systemui -> services -> rule engine 的整条链路。
2. 尽量避免破坏现有 `EdgeGesture` 功能。
3. 将新能力设计为独立事实类型，减少语义混淆。
4. 保持后续可扩展性，例如未来扩展为角外滑、角对角滑、角长按后滑等。

二、总体方案

建议把本次能力实现为一个新的事实类型，例如：

1. `CornerSwipeToCenter`

它在系统中的生命周期如下：

1. UI 规则编辑器创建 `Fact.CornerSwipeToCenter`。
2. 保存规则时转换为新的 proto fact。
3. services 持久化该规则。
4. patch-systemui 启动后，从 services 读取所有启用规则，提取所有 `CornerSwipeToCenter` 事实。
5. patch 中新增一个角滑识别器，对全局输入流做识别。
6. 一旦识别成功，patch 构造运行时 `CornerSwipeToCenter` fact，并通过 binder 回灌到 `ShortXService`。
7. `ShortXService` 调用 `RuleService.publishFact(...)` 发布事实。
8. `DefaultRuleEngine` 和 `FactMatcherImpl` 完成事实匹配和动作执行。

三、为什么不复用现有 EdgeGesture

不建议把该能力继续塞进现有 `EdgeGesture`，原因如下：

1. 区域模型不同。
现有 `EdgeGesture` 支持的是边缘矩形热区或用户自定义矩形热区。
本次需求需要的是四个角的圆形热区。

2. 方向语义不同。
现有 `Gesture` 枚举主要是 `SwipeFromTop/Bottom/Left/Right` 等边缘方向。
新需求是“从角向中心”，语义不是简单的上、下、左、右之一。

3. 检测逻辑不同。
现有边缘滑动更偏向边界方向识别，本次需要先命中角圆区，再校验与屏幕中心方向的夹角。

因此建议新增独立 fact 类型，而不是挤进旧模型。

四、协议设计建议

（一）proto 设计

建议在 `core-api/src/main/proto/shortx/Facts.proto` 中新增：

1. `message CornerSwipeToCenter`
2. `enum ScreenCorner`

建议字段如下：

1. `ScreenCorner corner = 1;`
表示四个角中的哪一个。

2. `int32 downX = 2;`
运行时触发时的起点 X。

3. `int32 downY = 3;`
运行时触发时的起点 Y。

4. `string tag = 97;`
5. `CustomContextDataKey customContextDataKey = 98;`
6. `string note = 99;`
7. `string id = 100;`
8. `bool isDisabled = 101;`

说明：

1. 配置态主要需要 `corner`。
2. 运行时附带 `downX/downY`，便于上下文使用和调试。
3. 继续沿用现有 fact 通用字段布局。

（二）enum 设计建议

建议新增：

1. `TopLeft`
2. `TopRight`
3. `BottomLeft`
4. `BottomRight`

命名风格保持与现有 proto 枚举一致即可。

五、UI 层设计方案

（一）UI 编辑模型

建议在 `ui-base` 的 `Fact` sealed model 中新增：

1. `Fact.CornerSwipeToCenter`

建议字段如下：

1. `corner`
2. `note`
3. `isDisabled`
4. `tag`
5. `customContextDataKey`
6. `id`

并补齐：

1. `FactToProtoFact.kt`
2. `ProtoAnyToFact.kt`
3. i18n label mapper
4. icon mapper

（二）事实选择器

建议新增一个简单 selector，例如：

1. `CornerSwipeToCenterSelectorDialog.kt`

交互方式不需要复杂：

1. 直接列出四个角供用户选择。
2. 可选地增加示意图或小文案说明。

在 `SystemFactSelectHandler.kt` 中新增该事实入口，并在 `FactSelector` 分类中可见。

（三）编辑回填

规则再次打开时，依然沿用：

1. proto -> UI model
2. UI model -> proto

因此 UI 层只要补齐 `toFact()` 和 `toProtoFact()` 即可自动接入现有 `AddRuleViewModel` 保存/回填链路。

六、services 层设计方案

（一）binder 和 manager

如果沿用现有“直接发布 proto fact”的方式，则不一定需要新增专门的 AIDL 管理接口。

原因：

1. 规则保存已经走统一规则存储。
2. 运行时 patch 侧只需要调用现有 binder 的“发布事实”路径。

因此本期更合理的做法是：

1. 复用规则保存接口。
2. 在 `ShortXService` 增加一个新的事实发布入口，或者复用通用发布路径。

建议做法：

1. 仿照 `fireEdgeGesture(...)` 新增 `fireCornerSwipeToCenter(...)`。

优势：

1. 语义清晰。
2. patch 侧调用简单。
3. 与 `EdgeGesture` 的现有通路并列，便于维护。

（二）ShortXService 处理

建议在 `ShortXService.kt` 中新增：

1. 解析 `CornerSwipeToCenter` proto。
2. 调用 `shortXContext.rule.publishFact("fireCornerSwipeToCenter", fact) { ... }`
3. 把 `downX/downY` 写入初始上下文数据。

（三）RuleService / DefaultRuleEngine

这部分不需要额外定制流程。

原因：

1. 只要新的 proto fact 能被 `FactMatcherImpl` 注册匹配器，规则引擎就能复用现有发布逻辑。

（四）FactMatcherImpl

建议新增 `CornerSwipeToCenter` 匹配规则：

1. 配置态和运行时 fact 的 `corner` 相等即匹配。

这是本期最简单、最稳定的匹配方式。

七、patch-systemui 层设计方案

（一）识别器设计原则

建议新增独立识别器，不改造现有 `SystemGesturesPointerEventListener` 去硬塞角逻辑。

建议新增例如：

1. `CornerSwipeToCenterDetector.kt`

它的职责：

1. 跟踪一次触摸流。
2. 记录 `ACTION_DOWN` 的起点。
3. 判断起点是否命中某个角圆区。
4. 在 `ACTION_MOVE` 中判断方向和距离是否达标。
5. 成功后只触发一次并返回结果给上层。

（二）常量定义

建议先在 detector 内部定义以下常量：

1. `CORNER_REGION_RADIUS_RATIO = 0.18f`
2. `CORNER_SWIPE_MAX_ANGLE_DEVIATION_DEG = 30f`
3. `CORNER_SWIPE_MIN_DISTANCE_DP = 72`

后续如果需要再下沉为配置项。

（三）角圆区命中算法

对四个角分别建立圆心：

1. 左上：`(0, 0)`
2. 右上：`(screenWidth, 0)`
3. 左下：`(0, screenHeight)`
4. 右下：`(screenWidth, screenHeight)`

半径：

1. `radius = 0.18f * min(screenWidth, screenHeight)`

命中条件：

1. `distance(downPoint, cornerCenter) <= radius`

（四）方向判定算法

1. 先求理想方向向量 `idealVector`。
2. 再求实际滑动向量 `actualVector = currentPoint - downPoint`。
3. 归一化后计算两者夹角。
4. 若夹角小于等于 30 度，则视为方向有效。

可使用点积公式：

$$
\cos \theta = \frac{a \cdot b}{|a||b|}
$$

当 $\theta \le 30^\circ$ 时，判定为通过。

（五）距离判定算法

1. 将最短距离阈值 `72dp` 转成像素。
2. 当 `|actualVector| >= thresholdPx` 时，视为距离达标。

（六）触发时机

建议在 `ACTION_MOVE` 中一旦满足以下三个条件立即触发：

1. 已命中角圆区。
2. 方向达标。
3. 距离达标。

同时：

1. 标记本次触摸流已触发。
2. 后续 move 不再重复触发。
3. 上抬或 cancel 时清理状态。

（七）与现有 Detector 的集成

建议在 `Detector.kt` 中像现有 `GestureDetector` 和 `SystemGesturesPointerEventListener` 一样并列接入新的识别器。

建议流程：

1. `consumeEvent(event)` 中在现有识别逻辑之后增加 corner detector 检查。
2. 新识别器成功时，调用 `service.handleCornerSwipeToCenterEvent(...)` 或等价接口。

为保持分层清晰，建议在 `DetectorService` 中新增一个专门的方法，而不是把它伪装成 `handleEdgeGestureEvent(...)`。

八、patch 侧服务协作方案

（一）DetectorService 接口扩展

建议在 `DetectorService.kt` 中增加：

1. `enabledCornerSwipeToCenterFacts`
2. `handleCornerSwipeToCenterEvent(corner, downX, downY): Boolean`

（二）DetectorServiceImpl 缓存策略

建议像现有 `enabledEdgeGestures` 一样，新增一个集合缓存所有启用的 `CornerSwipeToCenter` 事实。

在 `updateQuickCheckDataOnRuleChange()` 中：

1. 扫描所有启用规则。
2. 提取新 fact。
3. 放入快速匹配集合。

（三）shouldDetectMotion 复用

建议继续复用现有 `shouldDetectMotion` 大门禁逻辑，不需要再新增一套总开关。

但要把新 fact 计入“当前是否存在可检测手势”的判断中。

也就是说，原本只看 `enabledEdgeGestures.isNotEmpty()`，后续应改为看：

1. `enabledEdgeGestures`
2. `enabledCornerSwipeToCenterFacts`

二者任一非空即可启动检测。

九、上下文数据方案

建议新手势触发时继续写入：

1. `eventX`
2. `eventY`

如果后续需要，也可以增加：

1. `corner`

但本期不是必须项。

十、推荐实施顺序

1. 在 `Facts.proto` 中新增 `CornerSwipeToCenter` 和 `ScreenCorner`。
2. 生成 proto 产物。
3. 在 `ui-base` 增加 `Fact.CornerSwipeToCenter` 和双向转换。
4. 在 `ui` 增加 selector、入口、文案、图标映射。
5. 在 `ShortXService` 和 `IShortX.aidl` 中增加 `fireCornerSwipeToCenter(...)`。
6. 在 `FactMatcherImpl` 中新增匹配规则。
7. 在 `patch-systemui` 中新增 corner detector。
8. 在 `DetectorService` / `DetectorServiceImpl` 中接入新事实缓存与触发逻辑。
9. 在 `Detector.kt` 中接入新识别器。
10. 进行编译和手工验证。

十一、风险与注意点

1. 角圆区如果过大，容易与现有边缘手势产生交叉误判。
2. 方向偏差如果过宽，容易把普通斜滑识别成角向中心滑动。
3. 方向偏差如果过窄，用户体验会变差。
4. 横竖屏下圆区半径需要使用短边比例，否则体验不稳定。
5. 若 corner detector 先于 edge detector 执行，可能影响既有手势优先级；接入顺序需要明确。

十二、建议的首版策略

为了降低风险，首版建议采用以下策略：

1. 新 fact 独立建模，不复用 `EdgeGesture`。
2. 常量固定，不新增设置 UI。
3. 角圆区使用短边比例。
4. 识别器独立实现，不侵入现有边缘滑动识别器内部。
5. 先实现单次触摸流触发一次，不做更复杂连续识别。

十三、测试建议

1. 单元测试：角命中判定。
2. 单元测试：方向夹角判定。
3. 单元测试：最短距离判定。
4. 单元测试：四个角理想向量是否正确。
5. 集成测试：proto <-> UI model 双向转换。
6. 集成测试：规则保存和回填。
7. 手工测试：四个角各自触发。
8. 手工测试：错误角不触发。
9. 手工测试：方向偏差过大不触发。
10. 手工测试：短距离不触发。