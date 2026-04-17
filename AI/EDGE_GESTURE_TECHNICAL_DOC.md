# XEdgePro 边缘手势实现技术文档

## 1. 目标

本文档描述当前仓库中边缘手势系统的核心实现原理、系统 hook 点、事件流、状态机、配置结构、动作映射、特殊动作、可视化层以及伪代码级复现方案。

目标不是解释 UI，而是让另一个 AI 或工程实现者拿到本文档后，能够按相同逻辑重写出一套行为尽量一致的实现。

本文档依据当前反编译结果整理。少数方法体在反编译中丢失，但可通过控制流、字段赋值、相邻调用链和数据结构高置信还原。

## 2. 总体架构

边缘手势不是通过某个应用页面的 View 触摸监听实现的，而是通过 Xposed 在系统输入链路上 hook `InputManagerService`，在输入事件分发到目标应用前先拦截并判断。

整体链路如下：

1. `HookMain` 在 `android` 进程中初始化系统级 hook。
2. `w1` 作为系统侧能力聚合器，持有输入、动作执行、UI、状态服务等对象。本文后面称它为“系统动作协调器”。
3. `y0` hook `com.android.server.input.InputManagerService` 的输入过滤链。本文后面称它为“输入网关控制器”。
4. `输入网关控制器` 的 `U(...)` 接收到系统 `MotionEvent` 后，交给 `b0` 进行边缘手势状态机处理。本文后面称 `b0` 为“边缘手势状态机”。
5. `边缘手势状态机` 根据按下位置、移动方向、距离阈值、时间阈值、当前系统状态和用户配置决定：
   - 放行原始事件
   - 吞掉原始事件并执行普通动作
   - 吞掉原始事件并进入连续调节动作
   - 吞掉原始事件并进入子手势模式/边缘 UI 模式

关键类：

- `com.jozein.xedgepro.xposed.HookMain`
- `com.jozein.xedgepro.xposed.w1`，下文称“系统动作协调器”
- `com.jozein.xedgepro.xposed.y0`，下文称“输入网关控制器”
- `com.jozein.xedgepro.xposed.b0`，下文称“边缘手势状态机”
- `a.p`，下文称“手势配置中心”
- `a.b`，下文称“动作定义中心”

## 2.1 逆向命名对照表

为了让文档更容易复刻实现，下面统一把常见混淆类名、方法名、字段名翻译成语义名。后文优先使用语义名，必要时在括号里保留原始符号。

### 2.1.1 类名对照

| 原始符号 | 建议语义名 | 说明 |
|---|---|---|
| `w1` | 系统动作协调器 | 系统侧的总调度中心，负责执行动作、显示 UI、协调输入注入和状态服务 |
| `y0` | 输入网关控制器 | 直接 hook 系统 `InputManagerService`，负责接收和重新注入输入事件 |
| `b0` | 边缘手势状态机 | 负责边缘命中、方向判断、时序判断、动作触发 |
| `a.p` | 手势配置中心 | 所有边缘动作、开关位、阈值配置都从这里读取 |
| `a.b` | 动作定义中心 | 所有动作类型 id 和动作对象定义都在这里 |
| `d3` | 轨迹反馈层 | 一种跟手的视觉引导/反馈 UI |
| `y2` | 扇形反馈层 | 一种边缘弧形/扇区选择 UI |
| `g0` | 手势录制器 | 将真实 `MotionEvent` 序列录下来并提交保存 |
| `t3` | 系统覆盖层管理器 | 负责把反馈 View 加到系统覆盖层上 |

### 2.1.2 方法名对照

| 原始符号 | 建议语义名 | 说明 |
|---|---|---|
| `y0.U(...)` | 处理触摸输入事件 | `MotionEvent` 的系统层入口 |
| `y0.T(...)` | 处理按键输入事件 | `KeyEvent` 的系统层入口 |
| `y0.j0(...)` | 把事件重新注入系统 | 将缓存或改写后的事件送回系统 |
| `b0.G(...)` | 处理边缘按下开始 | `ACTION_DOWN` 阶段入口 |
| `b0.I(...)` | 处理滑动中的方向判断 | `ACTION_MOVE` 阶段入口 |
| `b0.M(...)` | 处理抬手收尾判定 | `ACTION_UP` 阶段入口 |
| `b0.K(...)` | 处理双击第二次点击 | 双击分支入口 |
| `b0.H(...)` | 提交已确认的滑动动作 | 在抬手时真正执行方向动作 |
| `b0.L()` | 执行单击动作 | 单击收尾 |
| `b0.f()` | 执行长按动作 | 长按定时器到点后的回调 |
| `b0.B(...)` | 计算边缘分区 | 判断按下点命中哪个边缘区域 |
| `b0.C(...)` | 计算滑动方向 | 判断左/右/上/下 |
| `b0.s(...)` | 启动动作执行流程 | 进入普通动作、连续动作或复合动作模式 |
| `b0.N(...)` | 派发最终动作 | 交给动作协调器执行 |
| `b0.x(...)` | 查询当前分区动作 | 查表并做大边缘回退 |
| `b0.z()` | 读取判定阈值 | 初始化边缘尺寸、距离阈值、超时 |

### 2.1.3 状态字段对照

| 原始符号 | 建议语义名 | 说明 |
|---|---|---|
| `W` | 按下起点X | down 时的 `rawX` |
| `X` | 按下起点Y | down 时的 `rawY` |
| `Y` | 命中边缘分区 | 当前 down 命中的边缘编号 |
| `Z` | 已识别手势类型 | 0 未定，2 长按，3 左，4 右，5 上，6 下 |
| `b0` | 当前序列已被接管 | 本次触摸序列是否交由边缘手势系统处理 |
| `a0` | 已进入动作执行阶段 | 进入后 move/up 不再是判定，而是动作生命周期 |
| `U` | 缓存的首次按下事件 | 用于判定失败时回放给系统 |
| `V` | 双击等待中的第一次抬手事件 | 用于等待第二次点击 |
| `f0` | 连续动作回调器 | 处理开始、移动、结束、取消 |
| `Q` | 长按超时 | 长按触发时间 |
| `R` | 双击超时 | 双击判定窗口 |
| `L` | 最小移动阈值 | 小于它不开始判方向 |
| `P` | 滑动触发距离阈值 | 超过它才真正触发滑动动作 |

## 2.2 模块设计图

这一节不是补充新逻辑，而是把前文拆出来的角色重新组织成“实现视角”的总览，方便其他 AI 或工程实现者先搭骨架，再逐块填细节。

### 2.2.1 运行时模块分层

```text
SystemServer / android 进程
    |
    v
HookMain
    |
    +--> 系统动作协调器
            |
            +--> 输入网关控制器
            |       |
            |       +--> 系统输入过滤 hook
            |       +--> 输入事件重新注入适配器
            |       +--> 手势录制器（可选旁路）
            |
            +--> 边缘手势状态机
            |       |
            |       +--> 边缘分区判定
            |       +--> 手势类型判定
            |       +--> 定时器状态管理
            |       +--> 原始事件缓存/回放决策
            |
            +--> 动作执行层
            |       |
            |       +--> 普通一次性动作执行
            |       +--> 连续调节动作处理器工厂
            |       +--> 条件动作/包装动作展开
            |
            +--> 中间态 UI 层
                    |
                    +--> 轨迹反馈层
                    +--> 扇形反馈层
                    +--> 系统覆盖层管理器
```

### 2.2.2 各模块的最小职责边界

| 模块 | 必须负责的事情 | 不应该负责的事情 |
|---|---|---|
| HookMain | 在系统进程启动时装配整套边缘手势运行时 | 不直接参与手势判定 |
| 输入网关控制器 | 接收系统输入、筛选事件、吞掉或回放原始事件 | 不决定具体边缘动作绑定 |
| 边缘手势状态机 | 识别边缘起手、方向、单击/双击/长按/滑动、进入哪种执行模式 | 不直接和 Android 版本差异的注入 API 打交道 |
| 手势配置中心 | 提供边缘分区开关、阈值、动作映射表 | 不直接执行动作 |
| 系统动作协调器 | 执行动作、展开条件动作、创建连续处理器、拉起特殊 UI | 不直接监听原始 MotionEvent |
| 中间态 UI 层 | 显示轨迹/扇区/边缘面板等反馈界面 | 不自己做手势命中判定 |

### 2.2.3 推荐的重写工程骨架

如果目标是重新写出一份行为等价的版本，可以先按下面的类职责切分，而不是一开始就照抄混淆类名：

```text
EdgeGestureBootstrap
SystemInputGateway
InputInjectionBridge
EdgeGestureEngine
EdgeRegionResolver
GestureDirectionResolver
GestureTimerController
GestureActionResolver
SystemActionDispatcher
ContinuousActionHandler
TrajectoryOverlayController
RadialOverlayController
GestureRecorder
GestureConfigRepository
```

这样切分的目的，是把“系统接入层”“判定层”“动作层”“UI 层”“配置层”彻底拆开。原实现虽然被混淆压缩在少数类里，但逻辑上已经呈现出这几个稳定层次。

## 3. 系统 Hook 点

### 3.1 主 Hook 类

`HookMain` 在 `android` 包加载时执行系统 hook 初始化。

职责：

- 初始化偏好和运行时支持对象
- 构造系统动作协调器（原 `w1`）
- 由系统动作协调器构造输入网关控制器（原 `y0`）
- 让输入网关控制器接管输入事件过滤链

### 3.2 Hook 的系统类

主目标类：

- `com.android.server.input.InputManagerService`

### 3.3 Hook 的系统 API

#### 3.3.1 `filterInputEvent`

这是最核心的 hook 点。

`输入网关控制器` 对 `InputManagerService.filterInputEvent(InputEvent, int, ...)` 做前置 hook。

逻辑：

- 输入事件到达系统输入服务
- 如果是 `KeyEvent`，交给“处理按键输入事件”（原 `y0.T(...)`）
- 如果是 `MotionEvent`，交给“处理触摸输入事件”（原 `y0.U(...)`）

这意味着边缘手势是在应用收到事件之前完成识别与拦截的。

#### 3.3.2 `setInputFilter`

用于监控系统当前是否安装了输入过滤器。

作用：

- 记录系统过滤链是否处于启用状态
- 如果系统过滤器被移除，并且本模块处于工作状态，则重新启用输入过滤

#### 3.3.3 `setInputFilterEnabled` / `nativeSetInputFilterEnabled`

不同 Android 版本调用路径不同。

Android 13 及以上使用：

- `setInputFilterEnabled`
- `injectInputEvent`

较低版本使用：

- `nativeSetInputFilterEnabled`
- `nativeInjectInputEvent`

这些 API 的作用有两个：

- 控制系统输入过滤链是否启用
- 在边缘手势失败或取消时，把缓存的原始事件重新注入系统

#### 3.3.4 `InputFilterHost.sendInputEvent`

当 `y0` 能拿到 `InputFilterHost` 时，会优先通过它回送输入事件；否则退回到系统注入 API。

### 3.4 版本兼容策略

`输入网关控制器` 中有两个输入注入适配器：

- `y0.e`：旧版 native 输入注入适配器
- `y0.f`：新版 `mNative.injectInputEvent` 输入注入适配器

它们都实现统一接口：

- 注入输入事件
- 启停输入过滤

因此重写时应当保留一个抽象层封装不同 Android 版本的输入注入能力。

## 4. 输入事件总体流转

### 4.1 总入口“处理触摸输入事件”（原 `y0.U(...)`）

`输入网关控制器.U(XC_MethodHook.MethodHookParam, MotionEvent)` 是系统 `MotionEvent` 处理入口。

输入事件进入后，会依次做以下检查：

1. 边缘手势状态机是否已初始化（原字段 `L`）
2. `motionEvent.getDownTime()` 是否有效
3. 当前显示设备是否为主屏（会过滤掉非主显示输入）
4. 当前输入设备与按钮状态是否允许进入手势系统
5. 事件是否属于当前已锁定的输入 deviceId

若满足条件，则将事件交给：

- `this.L.k(methodHookParam, motionEvent)`，也就是把事件交给边缘手势状态机继续判断

这里的 `L` 是边缘手势状态机实例。

### 4.2 为什么要锁定 `deviceId`

`输入网关控制器` 用字段 `Z` 记录当前正在跟踪的输入设备 id。为了方便理解，后文称它为“当前锁定的输入设备 id”。

目的：

- 确保一次手势序列只处理同一个触摸设备
- 避免多输入源或悬浮设备干扰
- down/up/move/cancel 一致归属于同一输入源

### 4.3 原始事件是如何被吞掉的

如果边缘手势状态机判定当前是边缘手势，`输入网关控制器.U(...)` 会执行：

- `methodHookParam.setResult(Boolean.FALSE)`

效果：

- 阻止当前原始 `MotionEvent` 继续正常下发到系统后续目标
- 该事件由边缘手势逻辑接管

### 4.4 原始事件是如何放回系统的

当系统判断“这次不是有效边缘手势”或手势在中途取消时，会把缓存的事件重新注入回系统。

注入出口：

- `输入网关控制器.j0(...)`
- `InputFilterHost.sendInputEvent(...)`
- `injectInputEvent(...)` 或 `nativeInjectInputEvent(...)`

这一步非常关键，因为它保证边缘手势失败时，不会无条件吞掉用户正常触摸。

## 5. 边缘手势状态机类 `b0`

`com.jozein.xedgepro.xposed.b0` 是真正的边缘手势控制器。下文统一称它为“边缘手势状态机”。

它的职责：

- 判定是否从边缘起手
- 判定滑动方向
- 判定单击、双击、长按、滑动
- 控制原始事件是否回放给系统
- 将最终动作交给系统动作协调器执行
- 对连续动作维持 move/up/cancel 生命周期

### 5.1 核心字段语义

下面统一按“好理解的名字（原始符号）”来解释。

#### 按下起点X / 按下起点Y（原 `W`, `X`）

- down 点的原始 `rawX`, `rawY`

#### 命中边缘分区（原 `Y`）

- 当前命中的边缘分区编号

#### 已识别手势类型（原 `Z`）

- 当前已识别的手势状态/方向编号

取值：

- `0`: 已从边缘起手，但方向未定
- `2`: 长按
- `3`: 左滑
- `4`: 右滑
- `5`: 上滑
- `6`: 下滑

#### 当前序列已被接管（原 `b0`）

- 当前触摸序列是否已经被边缘系统接管

#### 已进入动作执行阶段（原 `a0`）

- 当前是否已进入动作执行阶段

进入动作执行阶段后，后续 move/up/cancel 不再用于判定，而是转交给持续动作回调器（原 `f0`）

#### 缓存的首次按下事件（原 `U`）

- 缓存的初始 down 事件包装体

用途：

- 若最终不触发边缘手势，将其重新注入系统
- 若最终确认触发，则回收释放，不再放行

#### 双击等待中的第一次抬手事件（原 `V`）

- 双击等待窗口中的第一下 up 事件

#### 连续动作回调器（原 `f0`）

- 持续动作回调器，类型是 `b0.a`

接口方法：

- `e(int edgeGroup, float x, float y, long t)` 开始
- `d(float x, float y, long t)` 移动更新
- `b(float x, float y, long t)` 结束
- `c()` 取消

#### 长按超时（原 `Q`）

- 长按超时阈值

#### 双击超时（原 `R`）

- 双击超时阈值

#### 最小移动阈值（原 `L`）

- 最小移动阈值，未超出时不判断方向

#### 滑动触发距离阈值（原 `P`）

- 滑动动作触发距离阈值

## 6. 边缘区域判定逻辑

### 6.1 入口方法

“计算边缘分区”（原 `b0.B(float rawX, float rawY)`）

该方法判断按下点是否位于边缘触发区。

### 6.2 依赖参数

参数由“读取判定阈值”（原 `b0.z()`）从配置中读取并计算：

- `S`: 边缘宽度像素值
- `T`: 边缘高度像素值
- `P`: 触发滑动距离阈值
- `Q`: 长按超时
- `R`: 双击超时

配置源来自手势配置中心（原 `a.p`）：

- `36`: edge_width
- `37`: edge_height
- `38`: distance_to_trigger_swipe
- `10`: gesture_double_tap_timeout
- `11`: gesture_long_press_timeout

备注：

当前反编译结果中“读取判定阈值”在读取 edge height 时看起来仍然调用了 `t(36, ...)`，这可能是反编译误差，也可能是原实现里存在读错配置键的 bug。手势配置中心的 `a.p.t(...)` 明确支持 `36/37/38` 三项。

### 6.3 分区编号定义

边缘分区共 16 个编号：

| 编号 | 含义 |
|---|---|
| 0 | 左边缘 |
| 1 | 右边缘 |
| 2 | 上边缘 |
| 3 | 下边缘 |
| 4 | 左上 |
| 5 | 左中 |
| 6 | 左下 |
| 7 | 右上 |
| 8 | 右中 |
| 9 | 右下 |
| 10 | 上左 |
| 11 | 上中 |
| 12 | 上右 |
| 13 | 下左 |
| 14 | 下中 |
| 15 | 下右 |

判定规则：

- 如果按下 x 落在左边缘宽度内，则再按 y 切成上/中/下三段
- 如果按下 x 落在右边缘宽度内，则再按 y 切成上/中/下三段
- 如果按下 y 落在上边缘高度内，则再按 x 切成左/中/右三段
- 如果按下 y 落在下边缘高度内，则再按 x 切成左/中/右三段

若未命中任何边缘分区，则返回 `-1`。

### 6.4 特殊分区 `-2`

当 `m0` 标记为 true 时，`B(...)` 会返回 `-2`。

该分区不是普通边缘分区，而是内部特殊入口，后续会映射到“边缘入口动作”（原动作 id `65537`），用于唤起边缘专用 UI。

## 7. 手势判定逻辑

### 7.1 ACTION_DOWN 阶段

入口：“处理边缘按下开始”（原 `b0.G(...)`）

流程：

1. 重置“已识别手势类型”为 0，表示方向未定（原 `Z = 0`）
2. 重置“已进入动作执行阶段”为 false（原 `a0 = false`）
3. 如果当前被外部标记为忽略下一次边缘触发，则直接退出
4. 如果锁屏时禁用手势且当前处于 keyguard，则退出
5. 调用“读取判定阈值”（原 `z()`）初始化本轮判定参数
6. 记录按下起点坐标（原 `W = rawX`, `X = rawY`）
7. 调用“计算边缘分区”（原 `B(W, X)`）得到命中边缘分区（原 `Y`）
8. 如果当前软键盘状态或忽略边缘策略要求跳过该分区，则退出
9. 如果该分区未启用，则退出
10. 将当前序列标记为已被边缘系统接管（原 `b0 = true`）
11. 缓存当前 down 事件到“缓存的首次按下事件”（原 `U`）
12. 启动长按定时器（原 `m(Q)`）

### 7.2 方向判定

入口：“计算滑动方向”（原 `b0.C(startX, startY, currentX, currentY)`）

算法：

1. 计算 `dx = currentX - startX`
2. 计算 `dy = currentY - startY`
3. 计算 `dx^2 + dy^2`
4. 若小于最小移动阈值（原 `L`），返回 `0`
5. 若 `dx^2 > dy^2`：
   - `dx < 0` 返回 `3` 左滑
   - `dx > 0` 返回 `4` 右滑
6. 否则：
   - `dy < 0` 返回 `5` 上滑
   - `dy > 0` 返回 `6` 下滑

这套方向识别不是通过角度分桶实现的，而是“哪一轴位移更大就按哪一轴解释”。

### 7.3 ACTION_MOVE 阶段

入口：“处理滑动中的方向判断”（原 `b0.I(...)`）

#### 7.3.1 方向未决状态“已识别手势类型 == 0”（原 `Z == 0`）

如果方向尚未确定：

1. 调“计算滑动方向”（原 `C(...)`）判定方向
2. 若仍为 `0`，继续跟踪
3. 若方向刚刚明确，则：
   - 取消单击/长按等待中的部分状态
    - 用“查询当前分区在某方向上的动作”（原 `O(direction)`）查当前分区在该方向上的动作
   - 若该方向没有动作，则放弃边缘手势，补发原始 down 给系统

#### 7.3.2 方向已确定“已识别手势类型属于左/右/上/下”（原 `Z in {3,4,5,6}`）

继续处理 move：

1. 若当前 move 方向与已确定方向不一致，则取消当前边缘手势
2. 计算当前位置与 down 点的距离平方，源码里用临时变量 `fQ`
3. 若本次距离大于当前记录的最大位移，则更新“最大位移记录”，源码里存在字段 `d0`
4. 当 `fQ > P` 且满足以下任一条件时，立即进入执行阶段：
   - 配置允许“滑动中立即触发”
    - 当前方向动作是“子手势容器动作”（原动作 id `107`）
5. 进入执行阶段后：
    - 标记已进入动作执行阶段（原 `a0 = true`）
    - 回收并丢弃缓存的首次按下事件（原 `U`）
    - 调用“启动动作执行流程”（原 `s(action, mappedSubIndex, gestureType, eventTime)`）

### 7.4 ACTION_UP 阶段

入口：“处理抬手收尾判定”（原 `b0.M(...)`）

#### 7.4.1 已经识别到方向滑动

若“已识别手势类型”已经不是 0：

1. 当前抬手方向必须与已识别方向一致（原 `Z`）
2. `A(ev)` 还会额外检查：
   - 最终位移是否超过阈值 `P`
   - 当前点是否没有明显反向回撤
3. 若满足则走 `H(...)` 执行滑动动作
4. 否则取消当前序列

#### 7.4.2 尚未识别方向滑动“已识别手势类型 == 0”

此时按时间和是否配置双击动作继续分流：

1. 若按住时间已超过长按超时（原 `Q`）：
    - 把已识别手势类型置为长按（原 `Z = 2`）
   - 执行长按动作

2. 否则，如果当前分区配置了双击动作：
    - 将当前 up 缓存到“双击等待中的第一次抬手事件”（原 `V`）
    - 启动双击等待定时器（原 `l(R)`）

3. 否则：
    - 直接执行单击动作（原 `L()`）

### 7.5 双击判定

入口：“处理双击第二次点击”（原 `b0.K(...)`）

当第二次点按到来时：

1. 如果两次点击间隔极短（小于约 40ms），视为异常过快，可能重置并重新开始一轮 down 处理
2. 若第二次点击与第一次点击之间距离足够小，且时间小于双击超时 `R`，则执行双击动作
3. 否则回退，把第一次 up 当作单击处理并开始新的判定流程

### 7.6 长按判定

基类 `z` 会在长按定时器到点后回调“执行长按动作”（原 `b0.f()`）。

`b0.f()` 的逻辑：

1. 读取当前分区的长按动作（原 `w(2)`）
2. 若无长按动作，则释放缓存事件给系统
3. 若有长按动作：
    - 标记已进入动作执行阶段（原 `a0 = true`）
    - 标记本次序列已由边缘系统接管（原 `b0 = true`）
    - 回收缓存的首次按下事件（原 `U`）
    - 调“启动动作执行流程”（原 `s(action, -1, 2, now)`）

## 8. 计时器机制

边缘手势的时间相关状态由基类 `z` 管理。

### 8.1 `m(long)`

- 启动长按定时器
- 到时回调 `f()`

### 8.2 `l(long)`

- 启动双击等待定时器
- 到时回调 `e()`

### 8.3 `n()`

- 取消双击等待定时器

### 8.4 `o()`

- 取消长按等待定时器

## 9. 手势类型编号与动作槽位映射

### 9.1 手势编号

每个边缘分区最多可以绑定 7 个动作槽位。

| 槽位编号 | 含义 |
|---|---|
| 0 | 单击 |
| 1 | 双击 |
| 2 | 长按 |
| 3 | 左滑 |
| 4 | 右滑 |
| 5 | 上滑 |
| 6 | 下滑 |

配置表在手势配置中心的 `a.p.D[16][7]` 中。可以把它理解成“16 个边缘分区 x 7 种手势槽位”的二维动作表。

对应读取方法：

- 手势配置中心的 `a.p.i(edgeIndex, gestureIndex)`

### 9.2 子手势容器动作的子槽位映射

动作 id `107` 是动作定义中心里的 `a.b.h3`，下文称“子手势容器动作”。

它内部有 5 个子动作槽位：

| 子槽位 | 含义 |
|---|---|
| 0 | hold |
| 1 | swipe left |
| 2 | swipe right |
| 3 | swipe up |
| 4 | swipe down |

方向到子槽位的映射通过“方向到子槽位映射函数”（原 `b0.v(int gestureType)`）：

| 手势编号 | 子槽位 |
|---|---|
| 3 左滑 | 1 |
| 4 右滑 | 2 |
| 5 上滑 | 3 |
| 6 下滑 | 4 |
| 其它 | 0 |

### 9.3 执行动作时的方向参数映射

普通滑动动作执行时，会通过“方向到执行参数映射函数”（原 `b0.u(int gestureType)`）将方向映射成另外一组参数：

| 手势编号 | 映射值 |
|---|---|
| 3 左滑 | 1 |
| 4 右滑 | 0 |
| 5 上滑 | 3 |
| 6 下滑 | 2 |

这个映射值会作为 `subIndex` 或方向参数传给动作执行层，用于区分方向依赖的行为。

## 10. 配置系统“手势配置中心”（原 `a.p`）

手势配置中心（原 `a.p`）是边缘手势与其它系统级动作的配置中心。

### 10.1 与边缘手势相关的主要结构

- `D[16][7]`: 边缘分区动作表
- `L[]`: 标量配置数组，例如边缘宽高、距离阈值、超时
- 若干 bitset：记录边缘是否启用、忽略策略、状态开关等

### 10.2 分区启用逻辑

手势配置中心的 `a.p.s(int edgeIndex)` 用于判断边缘分区是否启用。

16 个分区并不是直接用 0..15 的 bit，而是映射到另外一组开关位。

### 10.3 大边缘回退逻辑

手势配置中心的 `a.p.h(int edgeIndex)` 会把细分边缘回退到大边缘：

- 左上/左中/左下 -> 左边缘
- 右上/右中/右下 -> 右边缘
- 上左/上中/上右 -> 上边缘
- 下左/下中/下右 -> 下边缘

“查询当前分区动作”（原 `b0.x(gesture, resolveConditional)`）会先查细分分区；若无动作，再回退查大边缘动作。

### 10.4 条件型动作的解析

系统动作协调器的 `w1.y4(...)` 会在真正执行前解析条件型动作，例如：

- 条件动作 id `62`
- 嵌套包裹动作 id `72`

因此查表得到的动作并不一定是最终动作，还会在执行前再展开一次。

## 11. 动作执行模型

边缘手势动作分为三层。

### 11.1 普通一次性动作

执行入口：

- “派发最终动作”（原 `b0.N(...)`）
- `系统动作协调器.v3(...)`（原 `w1.v3(...)`）

特点：

- 一旦确认手势，立即执行一次
- 不再依赖后续 move

例如：

- 返回
- 主页
- 最近任务
- 启动应用
- 打开通知栏
- 广播动作
- 注入按键动作

### 11.2 连续调节动作（原动作 id `68`）

动作类：动作定义中心中的 `a.b.z2`。下文称“连续调节动作”。

语义：滑动连续调节类动作

例如：

- 亮度
- 屏幕滤镜透明度
- 音量类调节
- 光标调节

这类动作不会立即完成，而会通过“创建连续调节处理器”（原 `w1.S1(b.z2)`）生成一个连续动作回调器实例，在后续 move/up/cancel 中持续接收事件。

“创建连续调节处理器”目前可返回四个连续调节器子类：

- `w1.f`，近似可理解为屏幕滤镜或透明度调节器
- `w1.g`，近似可理解为亮度调节器
- `w1.h`，近似可理解为音量调节器
- `w1.i`，近似可理解为其它连续值调节器

这些类本质都是“把手势位移离散成档位，然后不断更新目标值”的连续调节器。

### 11.3 特殊内部动作

#### 11.3.1 子手势容器动作（原动作 id `107`）

动作类：动作定义中心中的 `a.b.h3`

作用：

- 当前动作不是一个最终动作，而是一个二级分发容器
- 在 hold / 左 / 右 / 上 / 下 5 个子槽位里继续选动作

它会让手势流程进入“二次判定”或“特殊 UI 引导”模式，而不是直接执行普通动作。

#### 11.3.2 边缘入口动作（原动作 id `65537`）

动作类：动作定义中心中的 `a.b.f3`

作用：

- 不代表普通业务动作
- 被用作内部边缘入口动作
- 最终不会走系统动作协调器的普通动作执行入口 `w1.v3(...)`
- 而会走 `w1.t4(rawX, rawY)`，在当前触点位置唤起边缘专用 UI

该动作通常由特殊边缘编号 `-2` + 单击组合生成。

## 12. 可视化与中间态 UI

边缘手势执行并不总是“立即完成”。有些模式会拉起中间态视觉层。

主要有两套：

### 12.1 轨迹反馈层（原 `d3`）

实现了 `b0.a`。

特点：

- 带轨迹或指示动画
- move 时跟随手指更新
- up 时播放动画或触发后续动作

### 12.2 扇形反馈层（原 `y2`）

实现了 `b0.a`。

特点：

- 更接近边缘扇区/弧形选择 UI
- 用于在边缘触点附近弹出可视化菜单或方向选择层

### 12.3 系统覆盖层管理器（原 `t3`）

用于将这些 View 添加到系统覆盖层中，并控制显示/隐藏。

## 13. 事件回放与取消逻辑

### 13.1 为什么要缓存初始 down

因为边缘手势必须先看 down 点位置和后续 move 才能决定是否接管。

如果一开始就直接放行 down，后续就很难稳定拦截整套手势。

因此策略是：

1. 先缓存 down
2. 继续观察 move/up
3. 若确认不是边缘手势，则把 down 补发回系统

### 13.2 何时补发

补发典型场景：

- down 在边缘，但当前方向对应的动作未配置
- 手势方向变化过大，视为无效
- 双击窗口失败，需要回退为普通事件
- 长按动作不存在

### 13.3 何时回收并不再补发

一旦进入真正执行阶段：

- `a0 = true`
- `U` 被回收
- 原始 down 不再放回系统

## 14. 录制功能

边缘手势系统还支持录制真实 `MotionEvent` 序列。

入口类：`com.jozein.xedgepro.xposed.g0`，下文称“手势录制器”

机制：

1. 输入网关控制器的“处理触摸输入事件”在配置开启时会创建手势录制器（原 `g0`）
2. down/move/up 每个事件都会转成 `g.q` 结构存入数组
3. 手势结束时通过 `BinderService.m(...)` 提交 `COMMIT_GESTURE`

这个录制逻辑与边缘手势判定共用同一条系统输入流，但它是旁路功能，不参与边缘判定本身。

## 15. 高置信伪代码

这一节里的代码不再使用混淆变量名，而是只在注释里保留原始符号，便于直接按语义实现。

### 15.1 “处理整轮触摸事件状态机”（原 `b0.d(...)`）

```java
boolean handleTouchSequence(MethodHookParam hookParam, MotionEvent event) {
    switch (event.getActionMasked()) {
        case ACTION_DOWN:
            return handleEdgeDownStart(hookParam, event); // 原 G(...)

        case ACTION_MOVE:
            if (!gestureSequenceCaptured) { // 原 b0
                return false;
            }
            if (actionExecutionStarted) { // 原 a0
                if (continuousActionHandler != null) { // 原 f0
                    continuousActionHandler.onMove(event.getRawX(), event.getRawY(), event.getEventTime());
                }
                event.recycle();
                return true;
            }
            return handleMoveBeforeTrigger(hookParam, event); // 原 I(...)

        case ACTION_UP:
            if (!gestureSequenceCaptured) {
                return false;
            }
            if (actionExecutionStarted) {
                if (continuousActionHandler != null) {
                    continuousActionHandler.onFinish(event.getRawX(), event.getRawY(), event.getEventTime());
                }
                cancelAndCleanupGesture(); // 原 p()
                event.recycle();
                return true;
            }
            if (pendingFirstTapUpEvent != null) { // 原 V
                return handleSecondTapForDoubleTap(hookParam, event); // 原 K(...)
            }
            return finalizeGestureOnFingerUp(hookParam, event); // 原 M(...)

        case ACTION_CANCEL:
            if (!gestureSequenceCaptured) {
                return false;
            }
            if (actionExecutionStarted && continuousActionHandler != null) {
                continuousActionHandler.onCancel();
            }
            cancelAndCleanupGesture();
            event.recycle();
            return true;

        case ACTION_POINTER_DOWN:
        case ACTION_POINTER_UP:
            if (!gestureSequenceCaptured) {
                return false;
            }
            cancelAndCleanupGesture();
            event.recycle();
            return true;

        default:
            return false;
    }
}
```

### 15.2 “启动动作执行流程”（原 `b0.s(...)`）

```java
void startResolvedGestureAction(Action action, int directionParameter, int recognizedGestureType, long triggerTime) {
    // 前置：当前已经确定要进入动作执行阶段

    if (action == null || action.actionId == 0) {
        cancelAndCleanupGesture();
        return;
    }

    // 1. 特殊内部边缘入口动作
    if (action.actionId == EDGE_ENTRY_ACTION_ID /* 原 65537 */) {
        actionExecutionStarted = true;   // 原 a0
        gestureSequenceCaptured = true;  // 原 b0
        dispatchResolvedAction(action, directionParameter); // 原 N(...)
        return;
    }

    // 2. 连续调节动作
    if (action instanceof ContinuousAdjustAction /* 原 a.b.z2 */) {
        continuousActionHandler = systemActionCoordinator.createContinuousAdjustHandler((ContinuousAdjustAction) action); // 原 w1.S1(...)
        actionExecutionStarted = true;
        gestureSequenceCaptured = true;
        if (cachedInitialDownEvent != null) { // 原 U
            recycle(cachedInitialDownEvent);
            cachedInitialDownEvent = null;
        }
        if (continuousActionHandler != null) {
            continuousActionHandler.onStart(
                GestureConfig.getParentEdgeGroup(hitEdgeRegion),
                downRawX,
                downRawY,
                triggerTime
            );
        }
        return;
    }

    // 3. 子手势容器，后续还会做二次选择
    if (action.actionId == SUB_GESTURE_CONTAINER_ACTION_ID /* 原 107 */) {
        actionExecutionStarted = true;
        gestureSequenceCaptured = true;
        // 根据原始实现，后续可能拉起 d3 / y2 / 其它中间层，
        // 再根据 hold/left/right/up/down 选 action.I[] 中的子动作。
        // 最终对子动作再次应用同样的执行逻辑。
        return;
    }

    // 4. 普通一次性动作
    actionExecutionStarted = true;
    gestureSequenceCaptured = true;
    if (cachedInitialDownEvent != null) {
        recycle(cachedInitialDownEvent);
        cachedInitialDownEvent = null;
    }
    dispatchResolvedAction(action, directionParameter);
}
```

### 15.3 一屏版复刻总流程

下面这段不是对应某一个原始方法，而是把“系统接入 -> 边缘判定 -> 动作执行 -> 失败回放”压缩成一段最适合复刻的总流程伪代码。

```java
boolean onSystemMotionEvent(MethodHookParam hookParam, MotionEvent event) {
    if (!isPrimaryDisplayEvent(event)) {
        return false;
    }

    if (!isAllowedInputSource(event)) {
        return false;
    }

    lockCurrentInputDeviceIfNeeded(event);

    if (gestureRecorderEnabled()) {
        gestureRecorder.record(event);
    }

    boolean handled = edgeGestureEngine.handleTouchSequence(hookParam, event);
    if (!handled) {
        return false;
    }

    hookParam.setResult(Boolean.FALSE);
    return true;
}

boolean handleEdgeDownStart(MethodHookParam hookParam, MotionEvent event) {
    resetCurrentGestureState();
    refreshThresholdsFromConfig();

    int edgeRegion = resolveEdgeRegion(event.getRawX(), event.getRawY());
    if (edgeRegion == NO_EDGE) {
        return false;
    }

    if (shouldSkipThisEdgeBecauseOfKeyguardImeOrFlags(edgeRegion)) {
        return false;
    }

    if (!gestureConfig.isEdgeEnabled(edgeRegion)) {
        return false;
    }

    gestureSequenceCaptured = true;
    cachedInitialDownEvent = copyOf(event);
    startLongPressTimer();
    return true;
}

boolean handleMoveBeforeTrigger(MethodHookParam hookParam, MotionEvent event) {
    int direction = resolveDirection(downRawX, downRawY, event.getRawX(), event.getRawY());
    if (direction == DIRECTION_UNKNOWN) {
        return true;
    }

    Action action = resolveActionForCurrentEdge(direction);
    if (action == null) {
        replayCachedDownBackToSystem();
        cancelAndCleanupGesture();
        return false;
    }

    recognizedGestureType = direction;
    rememberMaxTravelDistance(event);

    if (shouldTriggerActionNow(action, event)) {
        startResolvedGestureAction(action, mapDirectionToDispatchParam(direction), direction, event.getEventTime());
    }
    return true;
}

boolean finalizeGestureOnFingerUp(MethodHookParam hookParam, MotionEvent event) {
    if (recognizedGestureType == GESTURE_UNRESOLVED) {
        if (hasReachedLongPressTimeout(event)) {
            runLongPressAction();
            return true;
        }

        if (hasDoubleTapActionConfigured()) {
            storeFirstTapUpAndStartDoubleTapWindow(event);
            return true;
        }

        runSingleTapAction();
        return true;
    }

    if (!isFinalSwipeStillValid(event)) {
        replayCachedDownBackToSystem();
        cancelAndCleanupGesture();
        return false;
    }

    runConfirmedSwipeAction(event);
    return true;
}
```

### 15.4 实现顺序建议

如果是让另一个 AI 直接落代码，最稳的顺序不是从 UI 开始，而是按下面的依赖顺序实现：

1. 先实现“输入网关控制器 + 输入重新注入桥接层”。
2. 再实现“边缘分区判定 + 方向判定 + 定时器状态机”。
3. 再接入“手势配置中心”的 16 x 7 动作表和大边缘回退。
4. 再实现“普通动作 + 连续调节动作”的分流。
5. 最后补“子手势容器动作”和“边缘入口动作”的中间态 UI。

这个顺序的原因是：前 4 步已经能跑出绝大多数核心行为，而第 5 步主要影响高级交互形态，不影响主判定链路是否正确。

## 16. 重写实现时的最小等价需求

若目标是实现“行为几乎一致”的复刻版，至少需要保证以下点一致：

1. 在系统输入层拦截 `MotionEvent`，而不是在应用层 View 监听。
2. 只对从边缘起手的 `ACTION_DOWN` 建立跟踪。
3. 方向识别使用 dominant axis 逻辑，而不是角度阈值分桶。
4. 必须缓存 down 事件，并在判定失败时将其回放给系统。
5. 必须支持单击、双击、长按、四方向滑动这 7 个槽位。
6. 必须支持细分边缘与大边缘回退查表。
7. 必须支持连续调节动作的 start/move/up/cancel 生命周期。
8. 必须保留“子手势容器动作”（原动作 id `107`）的语义。
9. 必须保留“边缘入口动作”（原动作 id `65537`）的语义。
10. 必须保留锁屏禁用、软键盘禁用、边缘忽略、立即触发/抬手触发等条件控制。

## 17. 目前仍有的不确定点

以下内容高概率成立，但没有完整方法体逐行证据：

1. “启动动作执行流程”（原 `b0.s(...)`）在子手势容器动作（原 `107`）场景下，究竟先拉起轨迹反馈层还是扇形反馈层，还需要继续顺着 `w1.O2(...)` 等路径定位。
2. “处理整轮触摸事件状态机”（原 `b0.d(...)`）在多指场景下的某些回放分支，现阶段只能还原到“会取消当前边缘手势”，但不能保证与原始字节码逐行一致。
3. “读取判定阈值”（原 `b0.z()`）里 edge height 的读取键位在反编译结果中存在可疑点，需要结合更多上下文或原 smali 再确认。

## 18. 建议的后续工作

如果要继续提高复刻精度，建议下一步补这三块：

1. 继续定位 `w1.t4(...)` 最终唤起的边缘 UI 组件，确认“边缘入口动作”（原 `65537`）完整语义。
2. 继续追“子手势容器动作”（原 `107`）对应的二级手势 UI，确认轨迹反馈层和扇形反馈层的实际触发条件。
3. 若可以接受更底层分析，直接回到原始 smali 或 dex CFG，对“处理整轮触摸事件状态机”（原 `b0.d(...)`）和“启动动作执行流程”（原 `b0.s(...)`）做控制流恢复。
