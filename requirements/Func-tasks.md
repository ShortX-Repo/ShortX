自定义函数功能实施任务清单

说明

1. 本清单基于 requirements/Func-tech-design.md 拆分。
2. 每个任务尽量保持单一目标，便于逐项实施和验收。
3. 默认按建议实施顺序排列。

一、协议与数据模型

- [x] 1. 在 core-api/src/main/proto/shortx/Functions.proto 中新增 Function message。
- [x] 2. 为 Function 增加 id、name、returnType、parameters、actions、createTime、lastUpdateTime 字段。
- [x] 3. 在 core-api/src/main/proto/shortx/Functions.proto 中新增 FunctionList message。
- [x] 4. 为 Function.actions 增加 google.protobuf.Any 依赖并确认 proto import 正确。
- [x] 5. 将 Function.returnType 默认约定为 string，并在文档或常量中固定该约定。
- [x] 6. 在 core-api/src/main/proto/shortx/Actions.proto 中新增 ExecuteFunction message。
- [x] 7. 为 ExecuteFunction 增加 functionId 字段。
- [x] 8. 为 ExecuteFunction 增加 repeated FuncParameterInput funcParameterInputs 字段。
- [x] 9. 为 ExecuteFunction 补齐 customContextDataKey、actionOnError、isDisabled、note、id 通用字段。
- [x] 10. 在 core-api/src/main/proto/shortx/Actions.proto 中新增 SetFunctionReturnValue message。
- [x] 11. 为 SetFunctionReturnValue 增加 value 字段。
- [x] 12. 为 SetFunctionReturnValue 补齐 customContextDataKey、actionOnError、isDisabled、note、id 通用字段。
- [x] 13. 重新生成 proto 相关产物并确认工程可编译。

二、AIDL 与管理接口

- [x] 14. 在 AIDL 接口中新增 addFunction(in ByteArrayWrapper function) 方法，并通过 ByteArrayWrapper 传输序列化后的 Function。
- [x] 15. 在 AIDL 接口中新增 deleteFunction(String id) 方法。
- [x] 16. 在 AIDL 接口中新增 getFunctionById(String id) 方法，返回序列化后的 ByteArrayWrapper。
- [x] 17. 在 AIDL 接口中新增 getAllFunctions() 方法，返回序列化后的 ByteArrayWrapper 列表。
- [x] 18. 在 ShortXManager 中新增 addFunction 包装方法。
- [x] 19. 在 ShortXManager 中新增 deleteFunction 包装方法。
- [x] 20. 在 ShortXManager 中新增 getFunctionById 包装方法。
- [x] 21. 在 ShortXManager 中新增 getAllFunctions 包装方法。
- [x] 22. 检查 binder 传输的 proto 类型是否需要额外序列化处理，并确认继续沿用 ByteArrayWrapper 方案。

三、函数存储层

- [x] 23. 新增函数配置 store 类，职责对齐 DirectActionSettingsStore。
- [x] 24. 让 store 底层读写 FunctionList。
- [x] 25. 为 store 增加 getAllFunctions 方法。
- [x] 26. 为 store 增加 getFunctionById 方法。
- [x] 27. 为 store 增加 addFunction 方法。
- [x] 28. 为 store 增加 deleteFunction 方法。
- [x] 29. 为 addFunction 实现“同 ID 覆盖更新”逻辑。
- [x] 30. 为 store 增加空数据初始化与兼容旧版本处理。

四、服务端管理逻辑

- [x] 31. 在 services 层接入函数 store。
- [x] 32. 在 RuleService 或对应服务类中实现 addFunction。
- [x] 33. 在 RuleService 或对应服务类中实现 deleteFunction。
- [x] 34. 在 RuleService 或对应服务类中实现 getFunctionById。
- [x] 35. 在 RuleService 或对应服务类中实现 getAllFunctions。
- [x] 36. 为 deleteFunction 增加“函数不存在”日志或错误处理。
- [x] 37. 为函数管理接口增加基础调试日志。

五、动作模型与转换

- [ ] 38. 在 ui-base 的 Action sealed model 中新增 Action.ExecuteFunction。
- [ ] 39. 在 ui-base 的 Action sealed model 中新增 Action.SetFunctionReturnValue。
- [ ] 40. 为 Action.ExecuteFunction 定义 UI 侧字段结构。
- [ ] 41. 为 Action.SetFunctionReturnValue 定义 UI 侧字段结构。
- [ ] 42. 在 ProtoAnyToAction 中增加 ExecuteFunction 到 UI Action 的转换。
- [ ] 43. 在 ProtoAnyToAction 中增加 SetFunctionReturnValue 到 UI Action 的转换。
- [ ] 44. 在 ActionToProtoAction 中增加 UI Action.ExecuteFunction 到 proto 的转换。
- [ ] 45. 在 ActionToProtoAction 中增加 UI Action.SetFunctionReturnValue 到 proto 的转换。
- [ ] 46. 为这两个新动作补齐 clone/复制行为。

六、执行函数运行时

- [ ] 47. 在 services actor 层新增 ExecuteFunction actor 实现类。
- [ ] 48. 在 ActionEvaluatorImpl 中注册 ExecuteFunction 的处理分支。
- [ ] 49. 在 ExecuteFunction actor 中按 functionId 读取函数。
- [ ] 50. 在函数不存在时返回明确错误。
- [ ] 51. 为 ExecuteFunction 增加默认参数与调用参数合并逻辑。
- [ ] 52. 复用现有 FuncParameter.asDefaultInput 生成默认参数输入。
- [ ] 53. 构造函数执行专用的上下文数据对象。
- [ ] 54. 在函数执行开始前初始化 funcReturnValue 为空字符串。
- [ ] 55. 顺序执行函数内部动作列表。
- [ ] 56. 函数执行结束后读取 funcReturnValue 并作为结果返回。
- [ ] 57. 评估是否需要防止函数递归调用；若需要，补保护逻辑。

七、设置返回值运行时

- [ ] 58. 在 services actor 层新增 SetFunctionReturnValue actor 实现类。
- [ ] 59. 在 ActionEvaluatorImpl 中注册 SetFunctionReturnValue 的处理分支。
- [ ] 60. 在执行时先对 value 做参数替换。
- [ ] 61. 在执行时再做上下文变量展开。
- [ ] 62. 将最终字符串写入 funcReturnValue。
- [ ] 63. 确认重复执行时会覆盖上一次返回值。

八、函数列表与编辑页面

- [ ] 64. 新增函数列表页面入口。
- [ ] 65. 在函数列表页加载 getAllFunctions 数据。
- [ ] 66. 在函数列表页展示函数名称与基本信息。
- [ ] 67. 为函数列表页增加“新建函数”入口。
- [ ] 68. 为函数列表项增加“查看/编辑函数”入口。
- [ ] 69. 为函数列表项增加“删除函数”入口。
- [ ] 70. 新增函数编辑页 ViewModel。
- [ ] 71. 在函数编辑页状态中增加 id、name、returnType、parameters、actions、时间戳字段。
- [ ] 72. 在函数编辑页复用现有 FuncParamList。
- [ ] 73. 在函数编辑页复用现有参数编辑对话框 NewFunctionParameterDialog。
- [ ] 74. 在函数编辑页复用现有 ActionContent 动作编排能力。
- [ ] 75. 在函数编辑页实现保存逻辑。
- [ ] 76. 在函数编辑页实现读取已有函数逻辑。
- [ ] 77. 在函数编辑页把 returnType 固定展示为 string。

九、执行函数动作编辑器

- [ ] 78. 新增 ExecuteFunctionSelectorState。
- [ ] 79. 新增 ExecuteFunctionSelectorDialog 或对应编辑器 UI。
- [ ] 80. 为执行函数动作提供函数选择器。
- [ ] 81. 选择函数后加载其参数定义。
- [ ] 82. 按参数定义生成 FuncParameterInput 编辑项。
- [ ] 83. 支持用户修改每个参数输入值。
- [ ] 84. 进入编辑已有 ExecuteFunction 动作时正确回填 functionId 与参数值。
- [ ] 85. 选择函数不存在或加载失败时展示错误提示。

十、删除引用提示

- [ ] 86. 实现函数删除前的引用扫描逻辑。
- [ ] 87. 扫描规则中的 ExecuteFunction 引用。
- [ ] 88. 扫描 Direct Action 中的 ExecuteFunction 引用。
- [ ] 89. 扫描其他函数中的 ExecuteFunction 引用。
- [ ] 90. 将引用扫描结果返回给 UI。
- [ ] 91. 在删除确认弹窗中展示“该函数仍被引用”提示。
- [ ] 92. 在提示后仍允许用户继续删除。

十一、动作选择器、文案与图标

- [ ] 93. 在动作分类中加入 ExecuteFunction。
- [ ] 94. 在动作分类中加入 SetFunctionReturnValue。
- [ ] 95. 为 ExecuteFunction 增加 i18n key。
- [ ] 96. 为 SetFunctionReturnValue 增加 i18n key。
- [ ] 97. 为函数列表页与函数编辑页补齐 i18n 文案。
- [ ] 98. 为 ExecuteFunction 选择一个合适图标。
- [ ] 99. 为 SetFunctionReturnValue 选择一个合适图标。
- [ ] 100. 更新 action mapper、icon mapper、标题 mapper。

十二、校验与错误处理

- [ ] 101. 在函数编辑页实现“名称必填”校验。
- [ ] 102. 在函数编辑页实现“同一函数内参数名不重复”校验。
- [ ] 103. 在 ExecuteFunction 运行时对必填参数缺失场景补校验。
- [ ] 104. 为函数不存在错误增加统一错误文案。
- [ ] 105. 为删除不存在函数的场景增加容错提示。

十三、测试

- [ ] 106. 为 Function store 增加 CRUD 单元测试。
- [ ] 107. 为默认参数覆盖逻辑增加单元测试。
- [ ] 108. 为 argOf$ 参数替换链路增加单元测试。
- [ ] 109. 为 SetFunctionReturnValue 写入结果增加单元测试。
- [ ] 110. 为 ExecuteFunction 调用不存在函数增加单元测试。
- [ ] 111. 为函数编辑保存与重新加载增加集成测试。
- [ ] 112. 为执行函数动作传参加执行增加集成测试。
- [ ] 113. 为编辑函数后执行最新版本增加集成测试。
- [ ] 114. 为删除被引用函数时的提示增加集成测试。
- [ ] 115. 为函数参数编辑 UI 增加测试。
- [ ] 116. 为 ExecuteFunction 参数输入编辑 UI 增加测试。
- [ ] 117. 为删除二次确认弹窗增加 UI 测试。

十四、收尾与验收

- [ ] 118. 手工验证函数创建、编辑、删除完整流程。
- [ ] 119. 手工验证执行函数动作的参数传递与结果返回。
- [ ] 120. 手工验证删除后运行报错行为。
- [ ] 121. 清理开发阶段临时日志与调试代码。
- [ ] 122. 回写 requirements/Func-tech-design.md 中已落地实现差异。