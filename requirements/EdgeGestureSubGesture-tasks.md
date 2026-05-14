边缘手势子手势实施任务清单

说明

1. 本清单基于 AI/EDGE_GESTURE_OVERVIEW.html 中的子手势章节与当前实现链路拆分。
2. 默认以“先兼容旧一段式规则，再增量扩展子手势序列”的顺序实施。
3. 第一阶段 UI 只要求支持一个子手势，但内部模型按可扩展序列设计。
4. 最新需求调整为：主手势成功后等待子手势不设置自动超时，但需要在等待阶段展示提示器。

一、协议与数据模型

- [ ] 1. 在 core-api/src/main/proto/shortx/Facts.proto 中新增 EdgeGestureStepType enum。
- [ ] 2. 为 EdgeGestureStepType 增加 SwipeLeft、SwipeRight、SwipeUp、SwipeDown、Hover 枚举值。
- [ ] 3. 评估是否需要把 SingleTap、DoubleTap、LongTap 一并纳入新 step enum；若保留则补齐枚举值。
- [ ] 4. 在 core-api/src/main/proto/shortx/Facts.proto 中新增 EdgeGestureStep message。
- [ ] 5. 为 EdgeGestureStep 增加 type 字段。
- [ ] 6. 在 Facts.proto::EdgeGesture 中新增 childSteps 字段。
- [ ] 7. 明确 legacy `gesture` 字段继续作为一级主手势的兼容策略并在注释中说明。
- [ ] 8. 重新生成 proto 产物并确认工程可编译。

二、UI-base 事实模型与转换

- [ ] 10. 在 ui-base/.../Fact.kt 中扩展 Fact.EdgeGesture，保留原 `gesture` 并增加 childSteps 模型字段。
- [ ] 11. 为 UI 侧新增 EdgeGestureStepModel 或等价枚举。
- [ ] 12. 在 FactToProtoFact.kt 中实现 EdgeGesture step 字段到 proto 的转换。
- [ ] 13. 在 ProtoAnyToFact.kt 中实现 proto step 字段到 UI model 的转换。
- [ ] 14. 确认 clone、插入、复制、保存、回填等通用流程兼容新的字段。

三、UI 规则编辑器

- [ ] 15. 修改 EdgeGestureSelectDialog.kt，保留当前一级 gesture 选择器并新增子手势配置区。
- [ ] 16. 第一阶段 UI 仅支持 0 或 1 个子手势。
- [ ] 17. 为子手势增加删除或清空入口。
- [ ] 18. 为已有 EdgeGesture 规则补齐子手势回填逻辑。
- [ ] 19. 更新保存结果结构 EdgeGestureSelectDialogResult 以包含原 gesture 和子手势信息。

四、文案与展示映射

- [ ] 20. 为子手势、悬停等新增 i18n 文案，并复用现有一级手势文案。
- [ ] 21. 更新 FactI18N 中 EdgeGesture 摘要生成逻辑，使其能显示序列。
- [ ] 22. 更新 EdgeGesture 相关 label / description mapper，确保新配置可读。

五、patch 运行时识别器设计落地

- [ ] 23. 新增 EdgeGestureSequenceDetector.kt 或等价序列识别器。
- [ ] 24. 在识别器中定义状态机：Idle、TrackingPrimary、WaitingNextStep、HoverPending、Succeeded、Cancelled。
- [ ] 25. 在识别器中增加 pointerId、stepIndex、stepAnchorX、stepAnchorY、stepStartedAt 等状态字段。
- [ ] 26. 实现一级主手势成功后进入下一步等待窗口的逻辑。
- [ ] 27. 实现方向型子手势的最小距离和方向点积判定逻辑。
- [ ] 28. 实现 Hover 子手势的半径与持续时间判定逻辑。
- [ ] 29. 实现主手势成功后无自动超时的等待逻辑。
- [ ] 30. 实现抬手或 pointer 丢失后的取消逻辑。
- [ ] 31. 区分“等待阶段无超时”与 `Hover` 步骤自身时长判定，避免二者混淆。

六、Detector 集成

- [ ] 32. 在 patch-systemui/.../gesture/Detector.kt 中集成 EdgeGestureSequenceDetector。
- [ ] 33. 明确 sequence detector 与当前 GestureDetector / SystemGesturesPointerEventListener 的执行顺序。
- [ ] 34. 对 legacy 一段式规则保留当前即时触发路径。
- [ ] 35. 对 sequence 规则改为由 sequence detector 接管后续识别。

七、DetectorService 与 quick-check 数据

- [ ] 36. 在 DetectorServiceImpl.kt 中保留 enabledEdgeGestures 作为完整 EdgeGesture 规则缓存。
- [ ] 37. 明确 quick-check 阶段如何区分 legacy one-step 规则与 sequence 规则。
- [ ] 38. 为 sequence 规则新增最终派发方法，例如 handleEdgeGestureSequenceEvent(...)
- [ ] 39. 构造 runtime EdgeGesture proto 时写入原 `gesture` 与 childSteps。
- [ ] 40. 保持现有 intercept cache 仅由主热区决定。

八、services 侧事实发布与匹配

- [ ] 41. 评估 ShortXService.fireEdgeGesture(...) 是否需要调整 runtime EdgeGesture 的解析和发布逻辑。
- [ ] 42. 在 FactMatcherImpl 中扩展 EdgeGesture 匹配逻辑，使其支持 `gesture + childSteps` 序列比较。
- [ ] 43. 保持 legacy `gesture` 字段规则与 runtime event 的兼容匹配。
- [ ] 44. 确保 sequence 规则只在整段成功后发布最终 fact，不发布中间 fact。

九、手势提示器

- [ ] 45. 为主手势成功后的等待阶段设计并实现一个轻量提示器。
- [ ] 46. 提示器至少表达“等待子手势”状态，并能区分方向型步骤与 `Hover` 步骤。
- [ ] 47. 评估现有 EdgeGestureHintDetector / hint controller 是否扩展复用，避免新造独立显示栈。
- [ ] 48. 确保等待态提示器在成功、失败、松手后及时消失。
- [ ] 49. 如实现多阶段视觉反馈，补充新的 hint 状态与展示文案。

十、测试

- [ ] 50. 为 EdgeGesture child step 的 proto <-> UI model 双向转换增加单元测试。
- [ ] 51. 为 sequence detector 的方向型步骤成功与失败增加单元测试。
- [ ] 52. 为 Hover 步骤的时间阈值与位移阈值增加单元测试。
- [ ] 53. 为“长时间等待但未松手”场景增加单元测试，确保不会因等待时长自动失败。
- [ ] 54. 为 finger up cancel 增加单元测试。
- [ ] 55. 为 EdgeGesture matcher 的 legacy 与 sequence 两种路径增加单元测试。
- [ ] 56. 手工验证：无子手势规则仍然正常工作。
- [ ] 57. 手工验证：主手势后长时间保持按下，再触发方向型子手势，规则仍能成功触发。
- [ ] 58. 手工验证：主手势 + 悬停子手势成功触发。
- [ ] 59. 手工验证：方向错误、抬手不会误触发。
- [ ] 60. 手工验证：等待态提示器在成功、失败、松手后表现正确。

十一、收尾与文档回写

- [ ] 61. 根据最终实现回写 AI/EDGE_GESTURE_OVERVIEW.html 中的子手势章节差异。
- [ ] 62. 如协议字段落地时有调整，回写 requirements/EdgeGestureSubGesture.md。
- [ ] 63. 补充最终运行链路说明，说明 legacy one-step 与 sequence rule 的分流关系。