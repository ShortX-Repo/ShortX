手势提示线与动态反馈技术方案

一、设计目标

在 `patch-systemui` 的全局输入监控链路上增加一层独立的手势提示反馈能力。

该能力需要满足：

1. 与现有手势识别链路解耦。
2. 首期服务 `CornerSwipeToCenter`。
3. 后续可复用于普通边缘手势。
4. 不改变现有触摸事件消费逻辑。
5. UI 表现可做动态箭头和拉伸反馈。

二、总体方案

建议把该能力拆成两层：

1. 候选手势状态层。
2. 提示渲染控制层。

职责划分如下：

1. 检测器负责识别“当前是否存在一个可展示提示的候选手势”，并持续输出进度数据。
2. 提示层 controller 负责创建、更新和销毁实际的 overlay/view。

三、为什么不要把提示逻辑硬塞进 Detector

当前 `Detector` 的职责是输入识别和事实触发。

如果把提示 UI 的创建、动画、布局更新都塞到 `Detector` 或 `CornerSwipeToCenterDetector` 中，会带来几个问题：

1. 检测逻辑和渲染逻辑强耦合。
2. 后续普通边缘手势复用困难。
3. 代码测试边界不清晰。
4. 后续想替换 UI 表现时改动范围过大。

因此建议新增一个独立 controller，例如：

1. `GestureHintController`

检测器只向它发送状态变更和进度更新。

四、建议的模块结构

建议在 `patch-xposed/patch-systemui/.../gesture/` 下新增以下类：

1. `GestureHintController.kt`
2. `GestureHintView.kt`
3. `GestureHintModel.kt`
4. `GestureHintProgress.kt`

如果希望继续收敛职责，也可以把首期实现压缩为：

1. `GestureHintController.kt`
2. `GestureHintView.kt`

首期不要求过度抽象，但接口要围绕“方向、进度、状态”展开，而不是围绕“四角”硬编码。

五、输入模型设计

建议为提示层定义一个通用的输入模型，例如：

1. `anchorX`
2. `anchorY`
3. `directionX`
4. `directionY`
5. `dragDistancePx`
6. `normalizedProgress`
7. `isReady`
8. `gestureType`
9. `gestureToken`

说明：

1. `anchorX/anchorY` 表示提示锚点，一般是手势起始区域附近的固定点，不一定等于手指实时位置。
2. `directionX/directionY` 表示目标方向单位向量。
3. `dragDistancePx` 表示当前拖动距离。
4. `normalizedProgress` 用于驱动 UI 动画，可取 $[0, 1]$。
5. `isReady` 表示是否进入“接近触发”态。
6. `gestureToken` 用于区分不同触摸流，防止旧状态串到新手势。

六、状态机设计

建议的提示状态机：

1. `Idle`
2. `Candidate`
3. `Showing`
4. `Ready`
5. `Success`
6. `Dismiss`

状态迁移：

1. `Idle -> Candidate`
   条件：`ACTION_DOWN` 命中某个启用手势的有效热区。

2. `Candidate -> Showing`
   条件：拖动距离大于等于 `GESTURE_HINT_SHOW_DISTANCE_DP`。

3. `Showing -> Ready`
   条件：拖动方向稳定有效，且进度达到接近触发阈值。

4. `Showing/Ready -> Success`
   条件：底层手势识别器确认触发。

5. `Candidate/Showing/Ready -> Dismiss`
   条件：抬手、取消、方向严重偏离、候选态失效。

6. `Success/Dismiss -> Idle`
   条件：结束动画完成。

七、首期四角手势接入方案

以 `CornerSwipeToCenterDetector` 为首期接入点。

建议在该 detector 内新增“提示候选态”输出，而不改变现有“识别成功就触发事实”的路径。

建议逻辑：

1. `ACTION_DOWN` 命中启用角区域时：
   创建一个候选提示上下文，但不显示。

2. `ACTION_MOVE` 时：
   计算当前位移和方向。
   若位移达到显示阈值，则调用 controller.showOrUpdate(...)。

3. 若方向偏离过大：
   调用 controller.dismiss(...)。

4. 若手势最终成功触发：
   调用 controller.success(...)。

5. `ACTION_UP/ACTION_CANCEL`：
   调用 controller.dismiss(...) 并清理状态。

八、普通边缘手势的复用设计

为了给后续普通边缘手势复用，提示 controller 不应该感知“CornerTopLeft”或“SwipeFromLeft”这些具体业务枚举。

它只接收：

1. 锚点。
2. 目标方向。
3. 当前进度。
4. 状态。

以后边缘手势只需要在边缘 detector 中生成相同格式的 `GestureHintModel` 即可复用同一个 controller 和 view。

九、显示阈值和进度算法

本期建议常量：

1. `GESTURE_HINT_SHOW_DISTANCE_DP = 12`
2. `GESTURE_HINT_READY_PROGRESS = 0.72f`
3. `GESTURE_HINT_MAX_VISUAL_DISTANCE_DP = 72`

建议进度计算：

$$
progress = clamp(\frac{dragDistancePx - showThresholdPx}{maxVisualDistancePx - showThresholdPx}, 0, 1)
$$

说明：

1. 小于显示阈值时不显示。
2. 达到显示阈值后逐步进入可见态。
3. 达到最大视觉距离后视觉进度饱和，但识别逻辑仍可继续独立判断。

十、提示 view 方案

建议首期使用自定义 `View` 绘制，而不是引入 Compose。

原因：

1. 当前运行位置在 `patch-systemui`，轻量 View 更贴合现有环境。
2. 绘制箭头、线段、圆角 path 和 alpha/scale 动画用 Canvas 足够。
3. 减少运行时依赖和接入复杂度。

`GestureHintView` 需要支持：

1. 根据方向旋转或直接按向量绘制。
2. 根据 `progress` 更新箭头长度、头部大小、透明度。
3. 根据 `isReady` 切换强化态，比如更亮、更长或更明显的曲线拉伸。
4. 支持 `success` 和 `dismiss` 的短时过渡动画。

十一、overlay 层方案

建议由 `GestureHintController` 负责通过 `WindowManager` 挂载一个非交互式 view。

约束：

1. view 不可获取焦点。
2. view 不可触摸。
3. view 层级足够高，确保在当前 SystemUI 场景可见。
4. view 的创建和移除必须与当前手势流严格同步。

建议属性方向：

1. `FLAG_NOT_FOCUSABLE`
2. `FLAG_NOT_TOUCHABLE`
3. `FLAG_LAYOUT_IN_SCREEN`
4. `FLAG_LAYOUT_NO_LIMITS`

具体 window type 需结合 SystemUI 运行环境选取首个稳定方案，优先选择当前进程可安全添加、且不影响交互的类型。

十二、锚点策略

提示不建议完全跟手指实时位置跑，否则会更像拖动光标，不像系统返回手势反馈。

首期建议：

1. 锚点取手势起始角附近的固定偏移点。
2. 箭头沿目标方向做有限位移和拉伸。
3. 手指位移主要驱动 progress，而不是驱动 overlay 完全跟手。

四角场景建议：

1. 左上角：锚点位于左上角向内偏移一小段。
2. 右上角：锚点位于右上角向内偏移一小段。
3. 左下角：锚点位于左下角向内偏移一小段。
4. 右下角：锚点位于右下角向内偏移一小段。

十三、与识别链路的关系

提示层不改变现有识别链路，只旁路消费状态。

即：

1. detector 继续负责识别真实手势是否触发。
2. controller 只根据 detector 提供的候选状态和进度更新 UI。
3. 是否显示提示，不影响 `handleCornerSwipeToCenterEvent(...)` 是否触发。

十四、接口建议

建议 `GestureHintController` 暴露以下接口：

1. `onGestureCandidateStart(model)`
2. `onGestureProgress(model)`
3. `onGestureReady(model)`
4. `onGestureSuccess(model)`
5. `onGestureCancel(token)`
6. `onConfigurationChanged()`

十五、首期实施范围建议

首期尽量做最小可用版本：

1. 仅接入四角手势。
2. 仅做一个通用动态箭头 view。
3. 仅做显示、更新、成功态、消失态。
4. 先不接入设置项。
5. 先把抽象接口做成“对边缘手势可复用”。

十六、验证重点

1. 候选态是否只在命中热区后产生。
2. 提示是否在轻微滑动后出现，而不是按下即出现。
3. 提示方向是否正确。
4. 提示是否在拖动时平滑更新。
5. 成功触发时是否有短暂成功态。
6. 取消和抬手时是否稳定消失。
7. 提示层是否完全不拦截事件。
8. 接入后是否不影响现有四角手势识别。
