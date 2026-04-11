四角向中心滑动手势实施任务清单

说明

1. 本清单基于 requirements/CornerSwipeTrigger-tech-design.md 拆分。
2. 每个任务尽量保持单一目标，便于逐项实施和验收。
3. 默认按建议实施顺序排列。

一、协议与数据模型

- [ ] 1. 在 core-api/src/main/proto/shortx/Facts.proto 中新增 ScreenCorner enum。
- [ ] 2. 为 ScreenCorner 增加 TopLeft、TopRight、BottomLeft、BottomRight 枚举值。
- [ ] 3. 在 core-api/src/main/proto/shortx/Facts.proto 中新增 CornerSwipeToCenter message。
- [ ] 4. 为 CornerSwipeToCenter 增加 corner 字段。
- [ ] 5. 为 CornerSwipeToCenter 增加 downX 字段。
- [ ] 6. 为 CornerSwipeToCenter 增加 downY 字段。
- [ ] 7. 为 CornerSwipeToCenter 补齐 tag、customContextDataKey、note、id、isDisabled 通用字段。
- [ ] 8. 重新生成 proto 相关产物并确认工程可编译。

二、AIDL 与 manager 接口

- [ ] 9. 在 core-api/src/main/java/tornaco/apps/shortx/core/IShortX.aidl 中新增 fireCornerSwipeToCenter(in ByteArrayWrapper data) 方法。
- [ ] 10. 评估是否需要新增 shouldFireCornerSwipeToCenter(...) 预检查接口；若决定保留，则补齐 AIDL 定义。
- [ ] 11. 在 core-api/src/main/java/tornaco/apps/shortx/core/ShortXManager.kt 中新增 fireCornerSwipeToCenter(...) 包装方法。
- [ ] 12. 如果新增了 shouldFireCornerSwipeToCenter(...)，则在 ShortXManager 中补齐对应包装方法。

三、UI-base 事实模型与转换

- [ ] 13. 在 ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/Fact.kt 中新增 Fact.CornerSwipeToCenter。
- [ ] 14. 为 Fact.CornerSwipeToCenter 定义 corner、note、isDisabled、tag、customContextDataKey、id 字段。
- [ ] 15. 为 Fact.CornerSwipeToCenter 实现 clone 行为。
- [ ] 16. 在 ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/FactToProtoFact.kt 中增加 Fact.CornerSwipeToCenter 到 proto 的转换。
- [ ] 17. 在 ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/fact/model/ProtoAnyToFact.kt 中增加 proto CornerSwipeToCenter 到 Fact.CornerSwipeToCenter 的转换。
- [ ] 18. 确认 rule 保存、回填、复制、插入等通用流程可以自动兼容新 fact。

四、UI 事实选择器与编辑入口

- [ ] 19. 新增 CornerSwipeToCenterSelectorDialog.kt 或等价 selector 组件。
- [ ] 20. 为 selector 增加四个角的选择列表。
- [ ] 21. 为 selector 增加已有配置回填逻辑。
- [ ] 22. 在 SystemFactSelectHandler.kt 中接入 CornerSwipeToCenter selector。
- [ ] 23. 在 FactSelector 分类中加入 CornerSwipeToCenter 事实入口。
- [ ] 24. 确认点击已有 fact 时可以重新打开 selector 并修改角配置。

五、UI 文案、图标与展示映射

- [ ] 25. 为 CornerSwipeToCenter 新增 i18n 标题文案。
- [ ] 26. 为四个角选项新增 i18n 文案。
- [ ] 27. 在 fact label mapper 中增加 CornerSwipeToCenter 的标题映射。
- [ ] 28. 在 fact description/selector mapper 中增加 CornerSwipeToCenter 的说明映射。
- [ ] 29. 为 CornerSwipeToCenter 选择合适图标并接入 icon mapper。
- [ ] 30. 为 CornerSwipeToCenter 选择合适颜色并接入 color mapper。

六、ShortXService 事实发布链路

- [ ] 31. 在 services/src/main/java/tornaco/apps/shortx/services/ShortXService.kt 中新增 fireCornerSwipeToCenter(...) binder 实现。
- [ ] 32. 在 fireCornerSwipeToCenter(...) 中解析 CornerSwipeToCenter proto。
- [ ] 33. 在 fireCornerSwipeToCenter(...) 中调用 shortXContext.rule.publishFact("fireCornerSwipeToCenter", fact) 发布新事实。
- [ ] 34. 在发布时把 downX/downY 写入初始上下文数据。
- [ ] 35. 如果引入 shouldFireCornerSwipeToCenter(...)，则在 ShortXService 中补齐实现并走 rule.shouldFire(...) 预检查。

七、规则匹配层

- [ ] 36. 在 services/src/main/java/tornaco/apps/shortx/services/rule/fact/FactMatcherImpl.kt 中注册 CornerSwipeToCenter 匹配器。
- [ ] 37. 实现 CornerSwipeToCenter 的 corner 相等匹配逻辑。
- [ ] 38. 确认匹配逻辑对配置态 fact 和运行时 fact 都稳定生效。
- [ ] 39. 确认新 fact 能进入 SubscribeOnlyModels 或其他事实注册清单，如该清单有要求则补齐注册。

八、patch DetectorService 接口扩展

- [ ] 40. 在 patch-systemui 的 DetectorService.kt 中新增 enabledCornerSwipeToCenterFacts 集合接口。
- [ ] 41. 在 DetectorService.kt 中新增 handleCornerSwipeToCenterEvent(...) 接口。
- [ ] 42. 如有需要，在 DetectorService.kt 中补充 corner 手势相关观察或辅助接口。

九、patch DetectorServiceImpl 数据缓存与门禁

- [ ] 43. 在 DetectorServiceImpl.kt 中新增 enabledCornerSwipeToCenterFacts 缓存集合。
- [ ] 44. 在 updateQuickCheckDataOnRuleChange() 中扫描所有启用规则，提取 CornerSwipeToCenter 事实。
- [ ] 45. 将扫描到的 CornerSwipeToCenter 事实写入快速匹配缓存。
- [ ] 46. 更新 shouldDetectMotion 逻辑，使其同时考虑 enabledEdgeGestures 和 enabledCornerSwipeToCenterFacts。
- [ ] 47. 在 handleCornerSwipeToCenterEvent(...) 中构造运行时 CornerSwipeToCenter proto。
- [ ] 48. 在 handleCornerSwipeToCenterEvent(...) 中调用 shortx.fireCornerSwipeToCenter(...)。
- [ ] 49. 若有前台应用限制需求扩展，评估 CornerSwipeToCenter 是否需要接入与 EdgeGesture 一致的前台应用快速过滤机制。

十、patch 角滑识别器

- [ ] 50. 新增 CornerSwipeToCenterDetector.kt。
- [ ] 51. 在 detector 内定义 CORNER_REGION_RADIUS_RATIO 常量。
- [ ] 52. 在 detector 内定义 CORNER_SWIPE_MAX_ANGLE_DEVIATION_DEG 常量。
- [ ] 53. 在 detector 内定义 CORNER_SWIPE_MIN_DISTANCE_DP 常量。
- [ ] 54. 为 detector 增加 ACTION_DOWN 状态初始化逻辑。
- [ ] 55. 为 detector 增加 ACTION_MOVE 识别逻辑。
- [ ] 56. 为 detector 增加 ACTION_UP/ACTION_CANCEL 清理逻辑。
- [ ] 57. 实现四个角圆心计算逻辑。
- [ ] 58. 实现角圆区半径计算逻辑，半径使用 0.18 * min(screenWidth, screenHeight)。
- [ ] 59. 实现起点是否落在角圆区内的判定逻辑。
- [ ] 60. 实现四个角对应的理想方向向量计算逻辑。
- [ ] 61. 实现实际滑动向量计算逻辑。
- [ ] 62. 实现基于点积的夹角判定逻辑。
- [ ] 63. 实现最短滑动距离判定逻辑，并将 72dp 转换为像素。
- [ ] 64. 实现“单次触摸流只触发一次”的状态保护逻辑。

十一、patch Detector 集成

- [ ] 65. 在 Detector.kt 中实例化 CornerSwipeToCenterDetector。
- [ ] 66. 在 Detector.consumeEvent(...) 中接入 CornerSwipeToCenterDetector 的事件消费。
- [ ] 67. 在新识别器成功时调用 service.handleCornerSwipeToCenterEvent(...)。
- [ ] 68. 明确 corner detector 与现有 edge/tap detector 的执行顺序。
- [ ] 69. 验证新 detector 接入后不会破坏现有 GestureDetector 和 SystemGesturesPointerEventListener 逻辑。

十二、上下文与调试辅助

- [ ] 70. 评估是否在 ContextDataMapping 中新增 corner 相关上下文 key；若需要则补齐映射。
- [ ] 71. 确认运行时 downX/downY 已能被现有上下文变量使用。
- [ ] 72. 为新手势发布路径增加必要调试日志，便于排查误判与漏判。

十三、测试

- [ ] 73. 为 proto <-> UI fact 的双向转换增加单元测试。
- [ ] 74. 为 CornerSwipeToCenter fact 的规则保存与回填增加集成测试。
- [ ] 75. 为 FactMatcherImpl 的 CornerSwipeToCenter 匹配逻辑增加单元测试。
- [ ] 76. 为角圆区命中算法增加单元测试。
- [ ] 77. 为方向夹角判定算法增加单元测试。
- [ ] 78. 为最短距离判定算法增加单元测试。
- [ ] 79. 为四个角理想方向向量增加单元测试。
- [ ] 80. 为“单次触摸流只触发一次”增加单元测试。
- [ ] 81. 为 UI selector 的保存与回填增加 UI 测试。
- [ ] 82. 在设备上手工验证左上、右上、左下、右下四个角都可正确触发。
- [ ] 83. 手工验证非对应角起点不会误触发。
- [ ] 84. 手工验证方向明显偏离中心时不会误触发。
- [ ] 85. 手工验证滑动距离不足时不会误触发。
- [ ] 86. 手工验证新增手势不会明显干扰现有 EdgeGesture。

十四、收尾与文档回写

- [ ] 87. 清理开发阶段临时日志和实验代码。
- [ ] 88. 根据最终实现回写 requirements/CornerSwipeTrigger-tech-design.md 中的实现差异。
- [ ] 89. 如实现过程中调整了常量，回写 requirements/CornerSwipeTrigger.md 的需求说明。
- [ ] 90. 在 AI 文档中补充 CornerSwipeToCenter 的最终运行链路说明。