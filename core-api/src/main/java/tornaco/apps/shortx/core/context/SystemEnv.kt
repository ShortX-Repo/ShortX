package tornaco.apps.shortx.core.context

object SystemEnv {
    private val variables: MutableMap<String, (parameter: String?) -> String?> = mutableMapOf()

    // No prefix.
    fun addEnvInflater(env: Pair<String, (parameter: String?) -> String?>) {
        variables[env.first] = env.second
    }

    fun String.asEnvName() = "%$this%"

    fun String.inflateSystemEnv(valueTransformer: (String) -> String = { it }): String {
        // 正则表达式匹配变量 (%variableName@parameter%)
        val regex = Regex("%(\\w+)(?:@(\\w+))?%")
        return regex.replace(this) { matchResult ->
            // 提取变量名和参数
            val variableName = matchResult.groups[1]?.value
            val parameter = matchResult.groups[2]?.value

            // 根据变量名和参数替换
            variables[variableName]?.invoke(parameter)?.let {
                valueTransformer(it)
            } ?: matchResult.value // 如果未定义变量则原样返回
        }
    }
}