# PIE Gesture System - EdXpSrc 逆向分析与移植指南

> 来源: `EdXpSrc/app/src/main/java/com/jozein/xedgepro/xposed/` (JADX 反编译, 高度混淆)

---

## 1. 整体架构

PIE 是一个径向弧形菜单(Radial Arc Menu), 当用户在屏幕边缘触发手势后弹出。支持 4 个边缘(左/右/上/下), 每个边缘最多 12 个可配置项(2 层 x 6 个)。

```
┌─────────────────────────────┐
│         Top Edge            │
│  ┌─────────────────────┐    │
│  │                     │    │
│L │                     │ R  │
│e │     Screen          │ i  │
│f │                     │ g  │
│t │                     │ h  │
│  │                     │ t  │
│  └─────────────────────┘    │
│        Bottom Edge          │
└─────────────────────────────┘
```

### 核心类映射表

| 混淆名 | 职责 | 移植对应 |
|---------|------|----------|
| `y2` | PIE 径向菜单渲染器 | `PieMenuView` |
| `y2.b` (内部类) | 实际绘制弧形菜单的 View | `PieArcView` |
| `b0` | 边缘手势检测器 | `EdgeGestureDetector` |
| `b0.a` | PIE 回调接口 (show/move/up/cancel) | `PieGestureCallback` |
| `z` | 触摸事件拦截基类 (定时器管理) | `TouchInterceptor` |
| `t3` | 系统窗口 Overlay 管理器 | `OverlayWindowManager` |
| `a.p` | 配置/偏好读取 | `PieConfig` |
| `d/w0` | PIE 配置 UI Fragment | `PieSettingsFragment` |
| `w1` | 主 Xposed 模块类 (持有 y2 实例) | `ModuleMain` |

---

## 2. PIE 渲染器详解 (`y2.java` -> `PieMenuView`)

### 2.1 类结构

```java
class y2 implements b0.a {
    Context z;              // 应用 Context
    Context A;              // 显示 Context (p2.d() 获取)
    Handler B;              // 主线程 Handler
    a.p C;                  // 配置读取器
    w1 D;                   // 模块主类, 用于执行 Action
    Paint E;                // 主弧形画笔 (Style.STROKE)
    Paint F;                // 高亮/选中画笔 (比主色亮/暗 48)
    Paint G;                // 分隔线画笔 (PorterDuff.DST_OUT 擦除)
    b I;                    // 当前显示的 PIE View 实例
}
```

### 2.2 回调接口 (`b0.a`)

PIE 实现了 4 个手势回调:

```java
interface PieGestureCallback {
    void onShow(int edge, float x, float y, long time);   // 原 e(): 创建并显示 PIE
    void onMove(float x, float y, long time);              // 原 d(): 手指移动, 更新选中项
    void onUp(float x, float y, long time);                // 原 b(): 手指抬起, 执行选中 Action
    void onCancel();                                        // 原 c(): 取消 PIE
}
```

### 2.3 弧形 View (`y2.b` -> `PieArcView`)

#### 构造参数

```java
PieArcView(
    int edge,           // 0=左, 1=右, 2=上, 3=下 (注意: 原代码中 e() 返回 true 当 edge==1 或 edge==2)
    float centerX,      // 触摸点 X (弧形圆心)
    float centerY,      // 触摸点 Y (弧形圆心)
    Drawable[] level1,  // Level 1 图标数组 (最多 6 个)
    Drawable[] level2,  // Level 2 图标数组 (最多 6 个, 可为 null)
    int itemCount,      // 实际有效项数 (max of level1 和 level2 中最大非空索引+1)
    int pieColor        // 弧形颜色
)
```

#### 关键几何参数

```
fM = displayMetrics * scale(config[26], default=100) / 100

弧形宽度 (strokeWidth):
    i4 = (int)(0.4 * fM + 0.5)       // 弧形条带宽度
    i5 = i4 / 2                       // 半宽
    i6 = (int)(i4 * 0.23 + 0.5)       // 图标大小 = G = F
    i7 = (int)(0.02 * fM + 0.5)       // 分隔线宽度

半径:
    i8 = (int)(0.93 * fM + 0.5)       // Level 1 外半径 = I
    H  = i8 - i4                      // Level 1 内半径
    i9 = i8 - i5                      // Level 1 中心半径 (用于 RectF)
    J  = i8 + i4 (如果有 level2)      // Level 2 外半径
    i10= J - i5                       // Level 2 中心半径

弧度:
    总弧度 = 160 度
    每项弧度 = 160 / itemCount
```

#### 起始角度 (按边缘方向)

```
Edge 0 (Left):   startAngle = -80°  (默认 else 分支)
Edge 1 (Right):  startAngle = 100°
Edge 2 (Top):    startAngle = 10°   (注意原代码: i!=2 ? ... : 10.0f)
Edge 3 (Bottom): startAngle = 190°
```

> 注意: 右边和上边(`e()` 返回 true)的图标顺序需要反转, 使得视觉上从上到下/从左到右保持一致。

#### 图标中心点预计算

构造时为每个 item 预计算了图标中心坐标:

```java
float angleCenter = startAngle + itemAngle / 2;  // 第一个 item 的中心角度
for (int i = 0; i < itemCount; i++) {
    float angle = angleCenter + itemAngle * i;
    // Level 1 图标中心
    O[i*2]   = centerX + cos(angle) * level1CenterRadius;
    O[i*2+1] = centerY + sin(angle) * level1CenterRadius;
    // Level 2 图标中心
    P[i*2]   = centerX + cos(angle) * level2CenterRadius;
    P[i*2+1] = centerY + sin(angle) * level2CenterRadius;
}
```

#### 颜色计算

高亮色 `h(int baseColor)`:
```java
// 如果 R+G+B >= 384 (偏亮), 减暗 48
// 如果 R+G+B <  384 (偏暗), 加亮 48
// Alpha 不变
int shift = (R + G + B >= 384) ? -48 : +48;
return argb(A, clamp(R+shift), clamp(G+shift), clamp(B+shift));
```

### 2.4 命中检测 (`k()` 方法)

```java
boolean hitTest(float touchX, float touchY) {
    float dx = touchX - centerX;
    float dy = touchY - centerY;
    float distSq = dx*dx + dy*dy;

    // 1. 检查是否在弧形环带内 (内半径 H ~ 外半径 J)
    if (distSq < H*H || distSq > J*J) {
        selectedItem = -1;
        return changed;
    }

    // 2. 计算触摸角度 (自定义 atan2, 范围 0~360)
    float angle = customAtan2(touchX, touchY);

    // 3. 特殊处理 Edge 0 (Left): 角度 >= 270 时减 360
    if (edge == 0 && angle >= 270) angle -= 360;

    // 4. 遍历每个 item, 检查角度是否落在该 item 的弧度范围内
    for (int i = 0; i < itemCount; i++) {
        float itemStart = startAngle + i * itemAngle;
        if (angle > itemStart && angle < itemStart + itemAngle) {
            // 5. 判断 Level: 距离 > I*I 则为 Level 2 (高位标记 +65536)
            if (distSq > I*I) {
                selectedItem = i + 65536;  // 高16位=1表示Level2
            } else {
                selectedItem = i;          // Level 1
            }
            return changed;
        }
    }
}
```

自定义角度计算 `a()`:
```java
float customAtan2(float x, float y) {
    float dx = x - centerX;
    float dy = y - centerY;
    float base = (float)(Math.atan2(dy, dx) * 180 / PI + 360) % 90;
    // 根据象限调整到 0-360 范围
    if (dx > 0)  return dy > 0 ? base : base + 270;
    else         return dy > 0 ? base + 90 : base + 180;
}
```

### 2.5 Action 执行 (`f()` 方法)

```java
void executeAction() {
    if (cancelled || selectedItem < 0 || animProgress < 1.0) return;

    int itemIndex = selectedItem & 0xFFFF;    // 低16位 = item 索引
    boolean isLevel2 = (selectedItem >>> 16) != 0;

    // 右边/上边需要反转索引 (因为构造时反转了图标顺序)
    if (isRightOrTop()) {
        itemIndex = (itemCount - 1) - itemIndex;
    }
    if (isLevel2) {
        itemIndex += 6;  // Level 2 偏移 6
    }

    // 读取配置中绑定的 Action
    Action action = config.getAction(edge, itemIndex);
    if (action.type != NONE && action.type != SEPARATOR) {
        moduleMain.executeAction(action);  // w1.u3()
    }
}
```

### 2.6 入场动画

```java
// 0 -> 1, 100ms, 使用 ObjectAnimator
ObjectAnimator.ofFloat(this, animProperty, 0f, 1f)
    .setDuration(100)
    .start();
// animProgress 控制绘制时的缩放/透明度
```

### 2.7 PIE 生命周期

```
onShow(edge, x, y) {
    pieColor = config.getInt(KEY_4, default=-14509620);   // 青色
    iconColor = config.getInt(KEY_30, default=0);         // 0 = 不着色; -1 = 白色

    // 加载 Level 1 & Level 2 的 Action 图标
    for (i in 0..5) {
        level1Icons[i] = config.getAction(edge, i).getDrawable();
        level2Icons[i] = config.getAction(edge, i+6).getDrawable();
        // 着色处理
        tintHelper.tint(icon, iconColor);
    }

    // 计算有效 item 数量 = max(level1最大非空索引, level2最大非空索引) + 1, 最少1

    pieView = new PieArcView(edge, x, y, level1Icons, level2Icons, count, pieColor);
    overlayManager.addView(pieView, LAYER_PIE=6);  // t3.n().j(view, 6)
}

onMove(x, y) {
    if (pieView.hitTest(x, y)) {
        pieView.invalidate();  // 选中项变化, 重绘
    }
}

onUp(x, y) {
    pieView.hitTest(x, y);      // 最终选中
    pieView.executeAction();    // 执行
    dismiss();                  // 移除 overlay
}

onCancel() {
    dismiss();  // t3.n().t(6) 移除 Layer 6
}
```

---

## 3. 边缘手势检测器详解 (`b0.java` -> `EdgeGestureDetector`)

### 3.1 继承关系

```
z (TouchInterceptor)
  └─ b0 (EdgeGestureDetector)
```

`z` 提供:
- `m(timeout)`: 启动长按定时器 (到期调用 `f()`)
- `l(timeout)`: 启动双击定时器 (到期调用 `e()`)
- `o()` / `n()`: 取消对应定时器
- `g(param)`: 放行触摸事件 (不拦截)

### 3.2 边缘区域划分 (`B()` 方法)

屏幕边缘被划分为 16 个区域:

```
             Top
         ┌──┬──┬──┐
         │10│11│12│  (上边, 3段)
    ┌────┼──┴──┴──┼────┐
    │ 4  │        │  7 │
Left│ 5  │ Center │  8 │ Right (左/右各3段)
    │ 6  │        │  9 │
    └────┼──┬──┬──┼────┘
         │13│14│15│  (下边, 3段)
         └──┴──┴──┘
            Bottom
```

每条边被三等分, 加上合并 edge (`a.p.h()` 将子区域映射到父 edge):
- 区域 4,5,6 -> Edge Left (0)
- 区域 7,8,9 -> Edge Right (1)  
- 区域 10,11,12 -> Edge Top (2)
- 区域 13,14,15 -> Edge Bottom (3)

边缘宽度: `S = config.getFloat(KEY_36, default=0.12) * displayDiagonal`
高度偏移: `T = config.getFloat(KEY_36, ...) * displayDiagonal`

### 3.3 手势识别状态机

```
               ACTION_DOWN
                   │
                   ▼
            G(): 记录起始点
            确定 edge zone (Y)
            启动长按定时器
                   │
        ┌──────────┼──────────┐
        ▼          ▼          ▼
   ACTION_MOVE  长按超时    ACTION_UP
        │      (f方法)         │
        ▼          │           ▼
  I(): 检测滑动    │     M(): 手指抬起
  方向 (C方法)     │           │
        │          ▼      ┌───┴────┐
        │     Z=2(长按)   │        │
        │     调用s()     │        │
        ▼          │   有双击配置? 无双击
   Z=滑动方向      │      │        │
   (3=左,4=右,     │   等待双击    执行单击
    5=上,6=下)     │   定时器     (N方法)
   检查swipe配置   │      │
        │          │   K(): 第二次DOWN
        ▼          │      │
   PIE/Pointer?───►│   在时间窗口内?
   是:调用s()      │      │
   (持续触摸追踪)  │   Z=1(双击)
   否:执行N()      │   调用s()
                   │
                   ▼
              s(): 分发
              判断 action type
              51=PIE -> f0=y2, 调用f0.e()
              68=Pointer -> f0=d3, 调用f0.e()
              其他 -> N(action)
```

### 3.4 滑动方向检测 (`C()` 方法)

```java
int detectSwipeDirection(float startX, float startY, float endX, float endY) {
    float dx = endX - startX;
    float dy = endY - startY;
    float dxSq = dx * dx;
    float dySq = dy * dy;

    // 最小滑动距离阈值
    if (dxSq + dySq < minSwipeDistSq) return 0;  // 未达标

    if (dxSq > dySq) {
        return dx < 0 ? 3 : 4;   // 3=向左滑, 4=向右滑
    } else {
        return dy < 0 ? 5 : 6;   // 5=向上滑, 6=向下滑
    }
}
```

### 3.5 手势类型到 Edge 方向的映射

```java
// u(): gestureType -> edge方向 (用于PIE显示)
static int gestureToEdge(int gesture) {
    switch(gesture) {
        case 3: return 1;   // 向左滑 -> Right edge
        case 4: return 0;   // 向右滑 -> Left edge
        case 5: return 3;   // 向上滑 -> Bottom edge
        case 6: return 2;   // 向下滑 -> Top edge
        default: return -1;
    }
}
```

### 3.6 PIE 触发的关键方法 (`s()`)

```java
// 简化后的 s() 逻辑
void dispatchGesture(Action action, int edgeDir, int gestureType, long time) {
    int actionType = action.type;

    if (actionType == 51) {        // PIE
        f0 = moduleMain.getPieMenu();  // y2 实例
        f0.onShow(edgeDir, touchX, touchY, time);
    } else if (actionType == 68) { // Gesture Pointer
        f0 = moduleMain.getGesturePointer();
        f0.onShow(edgeDir, touchX, touchY, time);
    }
    // 51 和 68 进入持续触摸追踪模式:
    // 后续 MOVE -> f0.onMove()
    // 后续 UP   -> f0.onUp()
    // CANCEL    -> f0.onCancel()
}
```

---

## 4. Overlay 窗口管理 (`t3.java`)

### 4.1 窗口层级

```
Layer 6  = PIE Menu (弧形菜单)
Layer 7  = Gesture Pointer
Layer 16+= 弹出窗口 (focusable, 可接收按键)
Layer 257= 滤光层
```

### 4.2 关键操作

```java
// 添加 View 到 Overlay
t3.n().j(view, layerType);     // 无 LayoutParams
t3.n().k(view, layerType, lp); // 有 LayoutParams

// 移除指定 Layer 的 View
t3.n().t(layerType);           // 立即移除
t3.n().u(layerType, delay);    // 延迟移除 (带淡出动画)

// 窗口类型
// PIE/Pointer: TYPE_SYSTEM_OVERLAY (2015), 不可聚焦
// 弹出窗口:    TYPE_APPLICATION_OVERLAY (2017) 或 TYPE_TOAST (2014), 可聚焦
```

### 4.3 WindowManager.LayoutParams 配置

```java
// 非聚焦层 (PIE, Pointer)
type  = 2015  // TYPE_SYSTEM_OVERLAY
flags = FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCHABLE | FLAG_LAYOUT_IN_SCREEN | FLAG_LAYOUT_NO_LIMITS
format = PixelFormat.TRANSLUCENT
// Android 28+: layoutInDisplayCutoutMode = SHORT_EDGES
// Android 30+: setFitInsetsTypes(0)
// privateFlags |= 16 (PRIVATE_FLAG_TRUSTED_OVERLAY 或类似)
```

---

## 5. 配置系统

### 5.1 PIE 相关配置键

| Config Key | 含义 | 默认值 | 范围 |
|------------|------|--------|------|
| 4 | PIE 颜色 | -14509620 (teal) | ARGB |
| 26 | PIE 缩放 | 100 (%) | 75~200, 步进5 |
| 30 | PIE 图标颜色 | -1 (白色) | ARGB, 0=不着色 |

### 5.2 PIE Item 配置

每个边缘有 12 个 item 槽位:
- 索引 0~5: Level 1 (内圈)
- 索引 6~11: Level 2 (外圈)

读取: `config.l(edgeIndex, itemIndex)` -> 返回 `Action` 对象
保存: `config.l0(context, 2/*type=PIE*/, edgeIndex, itemIndex, action)`

### 5.3 Settings UI 结构 (`d/w0.java`)

```
PIE 设置页面
├── 标签页: 左边 / 右边 / 上边 / 下边
│   ├── Level 1 Item 1  [Action选择器]
│   ├── Level 1 Item 2  [Action选择器]
│   ├── ...
│   ├── Level 1 Item 6  [Action选择器]
│   ├── Level 2 Item 1  [Action选择器]
│   ├── ...
│   └── Level 2 Item 6  [Action选择器]
└── (外观设置在 Preferences 页面)
    ├── PIE Scale: Slider 75%~200%, 步进5%
    ├── PIE Color: ColorPicker
    └── PIE Icon Color: ColorPicker
```

---

## 6. 绘制流程 (`onDraw` / `b()` 方法)

虽然 `b()` 方法因反编译失败无法完整恢复, 但根据已知信息可以推导出绘制逻辑:

```
onDraw(Canvas canvas):
    1. 保存画布, 设置硬件层 (LAYER_TYPE_HARDWARE for DST_OUT xfermode)

    2. 绘制 Level 1 弧形条带:
       canvas.drawArc(level1RectF, startAngle, 160°, false, mainPaint)
       // 当有选中项且在 Level 1 时:
       canvas.drawArc(level1RectF, selectedItemStartAngle, itemAngle, false, highlightPaint)

    3. 如果有 Level 2:
       canvas.drawArc(level2RectF, startAngle, 160°, false, mainPaint)
       // 选中高亮同上

    4. 绘制分隔线 (DST_OUT 模式, 擦除弧形产生间隙):
       for each item boundary:
           canvas.drawArc(..., gapPaint)  // 在两个 item 之间画细线

    5. 绘制图标:
       for each item in level1:
           if (drawable != null):
               drawable.setBounds(O[i*2]-iconSize, O[i*2+1]-iconSize,
                                  O[i*2]+iconSize, O[i*2+1]+iconSize)
               drawable.setAlpha((int)(255 * animProgress))
               drawable.draw(canvas)
       // Level 2 同上, 使用 P[] 坐标数组

    6. 恢复画布
```

---

## 7. 移植到 ShortX 的建议

### 7.1 需要替换的部分

| EdXpSrc | ShortX 替代方案 |
|---------|----------------|
| Xposed Hook 拦截触摸 | ShortX 自有的无障碍服务 / InputFilter |
| `t3` Overlay 管理 (TYPE_SYSTEM_OVERLAY) | ShortX 的 Overlay 管理器 (需 SYSTEM_ALERT_WINDOW 权限) |
| `a.p` SharedPreferences 读取 | ShortX 的配置系统 |
| `a.b` Action 模型 | ShortX 的 Action 模型 |
| `w1.u3()` 执行 Action | ShortX 的 Action 执行引擎 |

### 7.2 可以直接复用的核心算法

1. **弧形几何计算**: 半径、角度、item 中心点坐标预计算
2. **命中检测**: atan2 角度 + 距离环带判断
3. **颜色亮暗调节**: `h()` 方法 (RGB +/- 48)
4. **滑动方向检测**: dx/dy 比较
5. **入场动画**: ObjectAnimator 0->1, 100ms

### 7.3 移植优先级

1. **Phase 1**: `PieArcView` - 纯 View 绘制 + 命中检测 (独立于手势系统)
2. **Phase 2**: 接入 ShortX 手势触发系统
3. **Phase 3**: 配置 UI (每边缘 12 个 Action 槽位)
4. **Phase 4**: 外观定制 (颜色/缩放/图标着色)

### 7.4 关键数值参考

```kotlin
object PieDefaults {
    const val ARC_SPAN_DEGREES = 160f
    const val ANIMATION_DURATION_MS = 100L

    // 相对于 fM (displayMetric * scale / 100) 的比例
    const val BAND_WIDTH_RATIO = 0.4f       // 弧形条带宽度
    const val ICON_SIZE_RATIO = 0.092f       // 0.4 * 0.23 = 图标大小
    const val GAP_WIDTH_RATIO = 0.02f        // 分隔线宽度
    const val OUTER_RADIUS_RATIO = 0.93f     // Level 1 外半径

    const val DEFAULT_PIE_COLOR = -14509620  // 0xFF229C8C (teal)
    const val DEFAULT_ICON_COLOR = -1        // 白色
    const val DEFAULT_SCALE = 100            // 百分比
    const val MAX_ITEMS_PER_LEVEL = 6
    const val MAX_LEVELS = 2

    // 起始角度
    val START_ANGLES = mapOf(
        Edge.LEFT   to -80f,
        Edge.RIGHT  to 100f,
        Edge.TOP    to 10f,
        Edge.BOTTOM to 190f
    )
}
```
