四角向中心滑动手势拦截模式实施任务清单

说明

1. 本清单基于 requirements/CornerSwipeInterceptMode-tech-design.md 拆分。
2. 目标是在不改变四角手势最终识别条件的前提下，为其增加与边缘手势一致风格的早期拦截能力。
3. 默认按建议实施顺序排列。

一、协议与数据模型

- [ ] 1. 在 `core-api/src/main/proto/shortx/Facts.proto` 的 `CornerSwipeToCenter` 中新增 `optional bool isIntercept` 字段。
- [ ] 2. 确认字段号不与现有字段冲突。
- [ ] 3. 重新生成 proto 相关产物并确认工程可编译。

二、UI-base 模型与转换

- [ ] 4. 在 `ui-base/.../Fact.kt` 的 `Fact.CornerSwipeToCenter` 中新增 `isIntercept: Boolean = false`。
- [ ] 5. 在 `FactToProtoFact.kt` 中把 `isIntercept` 写入 proto。
- [ ] 6. 在 `ProtoAnyToFact.kt` 中按 `hasIsIntercept() && isIntercept` 的策略回填到 UI 模型。
- [ ] 7. 确认历史规则无该字段时，UI 默认显示为未开启。

三、UI 选择器与摘要展示

- [ ] 8. 在 `CornerSwipeToCenterSelectDialog.kt` 中新增拦截模式开关。
- [ ] 9. 开关文案复用 `ui.fact.key.event.adv.is.intercept.mode`。
- [ ] 10. 确认该开关与手势指示器开关可独立修改。
- [ ] 11. 在 `FactI18N.kt` 中为开启拦截模式的四角手势追加摘要显示。
- [ ] 12. 确认编辑已有规则时可以正确回填并再次保存。

四、patch 侧缓存扩展

- [ ] 13. 在 `DetectorServiceImpl.kt` 中新增四角拦截缓存集合，例如 `interceptCornerSwipeToCenterFacts`。
- [ ] 14. 在缓存刷新前正确清空该集合。
- [ ] 15. 在 `updateQuickCheckDataOnRuleChange()` 中扫描 `CornerSwipeToCenter` 事实时，继续维护 `enabledCornerSwipeToCenterFacts`。
- [ ] 16. 把开启拦截模式的四角事实额外加入四角拦截缓存。

五、四角热区快速判定

- [ ] 17. 在 `DetectorServiceImpl.kt` 中新增四角圆区命中辅助逻辑。
- [ ] 18. 使用与 `CornerSwipeToCenterDetector` 一致的角圆区规则：圆心为四个角、半径为 `0.18 * min(width, height)`。
- [ ] 19. 评估是否需要抽取共享几何函数，避免 detector 与 service 两边常量漂移。
- [ ] 20. 若提取共享函数，完成相关重构并保证现有行为不变。

六、输入流早期偷取逻辑

- [ ] 21. 扩展 `DetectorServiceImpl.shouldStealMotionEvent(input)`，在现有边缘矩形拦截判断之后增加四角圆区判断。
- [ ] 22. 确认当命中边缘拦截区时，现有行为保持不变。
- [ ] 23. 确认当未命中边缘拦截区但命中四角拦截区时，返回 `true`。
- [ ] 24. 确认当两个条件都未命中时，返回 `false`。

七、兼容性与行为验证

- [ ] 25. 确认旧的 `CornerSwipeToCenter` 规则在升级后默认不进入拦截缓存。
- [ ] 26. 确认关闭拦截模式时，四角手势行为与当前实现一致。
- [ ] 27. 确认开启拦截模式后，仅改变输入流偷取策略，不改变最终触发条件。
- [ ] 28. 确认 `enableGestureHint` 与 `isIntercept` 独立生效。

八、编译与回归验证

- [ ] 29. 编译 `:core-api:compileKotlin` 验证 proto 改动。
- [ ] 30. 编译 `:ui-base:compileDebugKotlin` 验证 UI-base 模型与转换。
- [ ] 31. 编译 `:ui:compileRowDebugKotlin` 验证 selector 与摘要展示。
- [ ] 32. 编译 `:patch-xposed:patch-systemui:compileKotlin` 验证 patch 侧缓存与偷取逻辑。

九、手工验收

- [ ] 33. 手工验证可在四角手势配置页看到拦截模式开关。
- [ ] 34. 手工验证开关状态保存后能正确回填。
- [ ] 35. 手工验证开启拦截模式后，命中角热区时可尽早偷取输入流。
- [ ] 36. 手工验证关闭拦截模式时，不会因为四角逻辑额外偷取输入流。
- [ ] 37. 手工验证方向偏差过大或距离不足时，即使已偷取输入流也不会误触发规则。
- [ ] 38. 手工验证与现有边缘手势拦截区并存时没有明显冲突。

十、文档回写

- [ ] 39. 若实现时字段命名或默认值与方案不一致，回写 `requirements/CornerSwipeInterceptMode-tech-design.md`。
- [ ] 40. 若最终产品语义调整，回写 `requirements/CornerSwipeInterceptMode.md`。
- [ ] 41. 在 AI 文档中补充最终实现差异和运行链路说明。