自定义函数技术设计文档

一、设计目标

在现有 ShortX 架构上新增“自定义函数”能力，尽量复用 Direct Action 的数据组织、编辑流程和运行时参数传递机制，降低实现复杂度。本期目标是实现：

1. 自定义函数的持久化存储与 CRUD。
2. 新动作“执行函数”。
3. 新动作“设置返回值”。
4. 函数执行时的参数透传与字符串返回值写入。

二、现有可复用基础

1. 参数定义模型已存在。
位置：core-api/src/main/proto/shortx/Functions.proto

当前已有：
- FuncParameter
- FuncParameterInput
- FuncReturnValue

2. 参数替换逻辑已存在。
位置：core-api/src/main/java/tornaco/apps/shortx/core/rule/Func.kt

现有逻辑已支持：
- 使用 argOf$ 前缀引用函数调用参数
- 执行前按参数名将字符串中的参数引用替换为实际输入值

3. “执行已保存对象并传参”的动作模式已存在。
参考：
- core-api/src/main/proto/shortx/Actions.proto 中的 FromDA
- ui-base/src/main/java/tornaco/apps/shortx/ui/addrule/action/model/Action.kt 中的 Action.FromDA
- ui/src/main/java/tornaco/apps/shortx/ui/addrule/action/selector/FromDASelectorDialog.kt
- services/src/main/java/tornaco/apps/shortx/services/rule/actor/impl/FromDA.kt

4. Direct Action 的编辑与存储流程可作为函数编辑能力的主要参考。
参考：
- core-api/src/main/proto/shortx/DirectActions.proto
- ui/src/main/java/tornaco/apps/shortx/ui/addda/AddDAViewModel.kt
- services/src/main/java/tornaco/apps/shortx/services/rule/RuleService.kt

5. 返回值上下文容器已有基础。
参考：
- core-api/src/main/java/tornaco/apps/shortx/core/context/GenericContextData.kt
- core-api/src/main/java/tornaco/apps/shortx/core/context/ContextMapUtil.kt

三、总体方案

总体上将“自定义函数”实现为一类新的可持久化配置对象：

1. 函数本体保存：
- id
- name
- returnType，固定 string
- parameters: repeated FuncParameter
- actions: repeated Any
- createTime
- lastUpdateTime

2. 运行时新增两个动作：
- ExecuteFunction：按函数 ID 加载并执行函数
- SetFunctionReturnValue：写入函数返回值

3. 执行模型：
- ExecuteFunction 从存储中加载函数
- 将调用端传入的 FuncParameterInput 传入函数执行环境
- 顺序执行函数内部动作
- SetFunctionReturnValue 把字符串写入上下文中的 funcReturnValue
- ExecuteFunction 结束时返回当前 funcReturnValue

四、建议的数据模型改动

建议在 core-api/src/main/proto/shortx/Functions.proto 中新增函数实体模型。

建议新增：

1. Function
- string id
- string name
- string returnType
- repeated FuncParameter parameters
- repeated google.protobuf.Any actions
- int64 createTime
- int64 lastUpdateTime

2. FunctionList
- repeated Function functions

说明：

1. returnType 虽然当前固定为 string，仍建议保留字段，便于后续扩展。
2. 本期写入时统一固定为 string。
3. 不增加函数级上下文变量字段。

五、动作协议改动建议

建议在 core-api/src/main/proto/shortx/Actions.proto 中新增两个动作：

1. ExecuteFunction
- string functionId
- repeated FuncParameterInput funcParameterInputs
- CustomContextDataKey customContextDataKey
- ActionOnError actionOnError
- bool isDisabled
- string note
- string id

2. SetFunctionReturnValue
- string value
- CustomContextDataKey customContextDataKey
- ActionOnError actionOnError
- bool isDisabled
- string note
- string id

说明：

1. ExecuteFunction 结构基本对齐现有 FromDA。
2. SetFunctionReturnValue 的 value 为字符串表达式，运行时会先经过现有参数与上下文展开逻辑。

六、管理接口改动建议

需要在 ShortXManager 与 AIDL 接口中增加函数管理接口，风格对齐 Direct Action。

建议新增：

1. addFunction(Function function)
2. deleteFunction(String id)
3. getFunctionById(String id): Function?
4. getAllFunctions(): List<Function>

服务端对应由 RuleService 或等价配置服务负责：

1. 读取函数存储。
2. 写入函数存储。
3. 按 ID 查询函数。
4. 删除函数。

七、存储设计

建议复用 Direct Action 当前的“proto 配置持久化”思路，为函数新增一份独立 store。

建议：

1. 新增函数配置 store，职责类似 DirectActionSettingsStore。
2. 底层保存 FunctionList。
3. 支持按 ID 增删改查。
4. 新增时若 ID 已存在，则覆盖更新。

原因：

1. 当前项目已有 Direct Action 配置型对象的完整读写链路。
2. 函数也是同类型对象，直接复用这套设计最稳妥。

八、UI 设计建议

（一）函数列表页

建议新增函数列表页，能力如下：

1. 展示全部函数。
2. 点击进入函数编辑页或详情页。
3. 提供新建入口。
4. 提供删除入口。

（二）函数编辑页

建议整体复用 Add Direct Action 页的结构思路。

页面区块建议：

1. 基本信息
- 函数名称
- 返回值类型，只展示 string

2. 参数区块
- 复用现有 FuncParamList 与 NewFunctionParameterDialog

3. 动作区块
- 复用现有动作编辑器 ActionContent

4. 保存/删除操作
- 删除前二次确认
- 若被引用则提示，但仍允许删除

（三）执行函数动作编辑器

建议新增 ExecuteFunctionSelector，行为参考 FromDASelectorDialog：

1. 先选择函数。
2. 读取函数参数定义。
3. 为每个参数生成 FuncParameterInput 编辑项。
4. 允许用户修改调用值。

九、运行时执行设计

（一）ExecuteFunction 执行流程

建议在 services 层新增对应 actor 实现，流程如下：

1. 从动作中读取 functionId 与 funcParameterInputs。
2. 调用函数查询接口按 ID 获取函数。
3. 若函数不存在，则抛出错误并返回 Fail。
4. 基于函数参数定义补齐默认参数输入。
5. 构造函数执行上下文。
6. 顺序执行函数内部 actions。
7. 从上下文中读取 funcReturnValue。
8. 将该值作为 ExecuteFunction 的执行结果返回。

（二）参数处理规则

建议沿用 FromDA 的参数合并方式：

1. 先取函数定义中的默认参数。
2. 再用调用方传入的 FuncParameterInput 覆盖同名值。
3. 函数内部动作通过 argOf$paramName 使用参数。

（三）返回值处理规则

建议：

1. 函数执行开始时，将 funcReturnValue 初始化为空字符串。
2. 每次执行 SetFunctionReturnValue，更新该值。
3. 函数执行完成后，返回最后一次写入的值。
4. 若函数内未执行设置返回值，则最终返回空字符串。

（四）错误处理规则

1. 函数不存在：直接报错。
2. 参数缺失：按现有默认值与必填规则处理。
3. 函数内部动作失败：沿用现有 ActionOnError 机制。

十、删除引用检查设计

删除函数时需要检查是否仍被 ExecuteFunction 动作引用。

建议流程：

1. 删除前扫描规则、Direct Action、其他函数中的动作列表。
2. 查找 ExecuteFunction.functionId == 目标函数 ID 的引用。
3. 若有引用，返回“存在引用”的提示信息给 UI。
4. UI 展示提示后仍允许用户确认删除。

说明：

1. 本期提示即可，不阻断删除。
2. 运行时兜底由 ExecuteFunction 的“函数不存在时报错”保证。

十一、兼容性与风险

1. 当前 Functions.proto 只有参数与返回值容器，没有函数实体，需要补协议并处理 proto 版本兼容。
2. ExecuteFunction 与 SetFunctionReturnValue 都是新增动作，需要补齐：
- proto
- UI model
- proto/action 双向转换
- selector/editor UI
- services actor 实现
- i18n 文案
- 图标映射

3. 若未来支持多类型返回值，需要重新设计 FuncReturnValue 与上下文中的值承载方式。

十二、建议实施顺序

1. 扩展 Functions.proto，增加 Function 与 FunctionList。
2. 扩展 AIDL 和 ShortXManager 的函数 CRUD 接口。
3. 在服务端增加函数 store 与增删改查实现。
4. 在 Actions.proto 中新增 ExecuteFunction 与 SetFunctionReturnValue。
5. 打通 ui-base 的 Action model / proto 转换。
6. 实现 ExecuteFunction 和 SetFunctionReturnValue 的运行时 actor。
7. 实现函数列表页与函数编辑页。
8. 实现 ExecuteFunction 动作选择器。
9. 增加删除引用提示。
10. 增加测试。

十三、测试建议

1. 单元测试
- 参数默认值覆盖
- argOf$ 参数替换
- SetFunctionReturnValue 最终结果写入
- 删除后执行函数时报错

2. 集成测试
- 新建函数并保存
- 在动作中选择执行函数并传参
- 编辑函数后再次执行使用最新版本
- 删除被引用函数时提示

3. UI 测试
- 函数参数增删改
- 执行函数动作参数输入编辑
- 删除二次确认弹窗