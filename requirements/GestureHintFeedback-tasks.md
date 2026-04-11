手势提示线与动态反馈实施任务清单

说明

1. 本清单基于 requirements/GestureHintFeedback-tech-design.md 拆分。
2. 首期以四角手势接入为目标，但抽象必须可复用到普通边缘手势。
3. 默认按建议实施顺序排列。

一、需求边界确认

- [ ] 1. 明确本期仅为四角手势实现提示反馈。
- [ ] 2. 明确未来普通边缘手势要复用同一提示层架构。
- [ ] 3. 明确提示在轻微滑动后显示，而不是按下即显示。
- [ ] 4. 明确提示层只展示，不拦截触摸事件。
- [ ] 5. 明确首期视觉方向为“接近系统返回手势”的动态拉伸箭头。

二、通用数据模型与状态机

- [ ] 6. 设计 GestureHintModel，包含锚点、方向、距离、进度、token、状态等字段。
- [ ] 7. 设计 GestureHintState 枚举或 sealed class，覆盖 Idle/Candidate/Showing/Ready/Success/Dismiss。
- [ ] 8. 设计 progress 计算规则，并定义显示阈值、ready 阈值、最大视觉距离。
- [ ] 9. 设计候选态失效规则，如方向偏离、抬手、取消、pointer 丢失等。
- [ ] 10. 设计单次触摸流与手势提示 token 的绑定方式。

三、patch-systemui Controller 骨架

- [ ] 11. 在 patch-systemui 中新增 GestureHintController.kt。
- [ ] 12. 为 GestureHintController 增加 show/update/success/dismiss 接口。
- [ ] 13. 为 GestureHintController 增加 WindowManager 初始化与生命周期管理。
- [ ] 14. 为 GestureHintController 增加配置变化后的重建或重布局逻辑。
- [ ] 15. 确保 controller 只处理展示，不做手势识别判断。

四、提示 View 实现

- [ ] 16. 新增 GestureHintView.kt。
- [ ] 17. 用 Canvas 或 Path 实现首期箭头主体绘制。
- [ ] 18. 实现根据方向向量绘制箭头朝向。
- [ ] 19. 实现根据 progress 更新箭头长度、透明度或位移。
- [ ] 20. 实现 ready 态强化效果。
- [ ] 21. 实现 success 态短动画。
- [ ] 22. 实现 dismiss 态淡出或回弹动画。
- [ ] 23. 确认 View 自身不消费输入、不持有焦点。

五、Overlay 挂载方案

- [ ] 24. 为 GestureHintController 选择在 SystemUI 环境下稳定可用的 WindowManager.LayoutParams 配置。
- [ ] 25. 配置 NOT_FOCUSABLE、NOT_TOUCHABLE 等必要 flag。
- [ ] 26. 实现首次 addView。
- [ ] 27. 实现 updateViewLayout 或等价刷新方案。
- [ ] 28. 实现 removeViewImmediate 或安全移除逻辑。
- [ ] 29. 验证 overlay 生命周期不会泄漏。

六、四角手势 detector 接入

- [ ] 30. 在 CornerSwipeToCenterDetector 中增加候选提示状态字段。
- [ ] 31. 在 ACTION_DOWN 命中角区域时记录提示候选上下文。
- [ ] 32. 定义 GESTURE_HINT_SHOW_DISTANCE_DP 常量。
- [ ] 33. 在 ACTION_MOVE 中当位移超过显示阈值时触发 controller.show/update。
- [ ] 34. 在 ACTION_MOVE 中持续计算 progress 并更新 controller。
- [ ] 35. 在 ACTION_MOVE 中当方向偏离严重时触发 dismiss。
- [ ] 36. 在真实手势识别成功时调用 controller.success。
- [ ] 37. 在 ACTION_UP/ACTION_CANCEL 时调用 controller.dismiss。
- [ ] 38. 确保提示逻辑不影响现有 CornerSwipeToCenter 识别成功条件。

七、方向与锚点计算

- [ ] 39. 为四个角定义固定的目标方向单位向量。
- [ ] 40. 为四个角定义合理的提示锚点偏移策略。
- [ ] 41. 实现从角枚举到锚点的映射。
- [ ] 42. 实现从角枚举到方向向量的映射。
- [ ] 43. 验证四个角下提示朝向都正确。

八、抽象复用预留

- [ ] 44. 把 controller 输入抽象为与具体手势类型无关的数据结构。
- [ ] 45. 避免在 GestureHintView 中直接硬编码 CornerSwipeToCenter 枚举分支。
- [ ] 46. 为未来普通边缘手势预留同类 show/update 接口。
- [ ] 47. 评估后续 EdgeGesture detector 接入点，并在代码注释或文档中说明。

九、视觉参数调优

- [ ] 48. 确定首版箭头大小、线宽、圆角、透明度范围。
- [ ] 49. 调整 show 阈值，使提示既不太早也不太迟出现。
- [ ] 50. 调整 ready 态门槛，使视觉强化与真实触发感一致。
- [ ] 51. 调整 success 与 dismiss 动画时长。
- [ ] 52. 验证不同分辨率和横竖屏下视觉位置是否合理。

十、测试与验收

- [ ] 53. 手工验证四角按下但不移动时不会显示提示。
- [ ] 54. 手工验证四角轻微滑动后会显示提示。
- [ ] 55. 手工验证四个角的提示方向均正确。
- [ ] 56. 手工验证提示具有随距离变化的动态拉伸效果。
- [ ] 57. 手工验证触发成功时能看到短暂成功态反馈。
- [ ] 58. 手工验证取消、抬手、方向偏离时提示稳定消失。
- [ ] 59. 手工验证提示层不会拦截输入，不影响现有手势触发。
- [ ] 60. 手工验证接入提示层后 CornerSwipeToCenter 识别逻辑未回归。

十一、文档回写

- [ ] 61. 若最终阈值与方案不一致，回写 requirements/GestureHintFeedback.md。
- [ ] 62. 若最终类名或接口与方案不一致，回写 requirements/GestureHintFeedback-tech-design.md。
- [ ] 63. 在 AI 文档中补充手势提示层与手势识别链路的最终配合说明。
