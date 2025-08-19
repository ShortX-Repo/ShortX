package tornaco.apps.shortx.core.util

// 太好了！以下是一个 Kotlin 原生风格 的 高级计算器工具类：
//✅ 支持 + - * /
//✅ 正确处理运算符优先级（乘除高于加减）
//✅ 支持括号 ( )
//✅ 支持 浮点数（Double）
//✅ 完全使用 Kotlin 标准库方法，无 Java 式 push()/pop()/peek()
object ExpressionCalculator {
    private val operators = mapOf(
        "+" to 1,
        "-" to 1,
        "*" to 2,
        "/" to 2
    )

    fun evaluate(expression: String): String {
        val rpn = toRPN(expression)
        return formatResult(evaluateRPN(rpn))
    }

    private fun formatResult(result: Double): String {
        return if (result % 1.0 == 0.0) result.toLong().toString() else result.toString()
    }

    private fun toRPN(expression: String): List<String> {
        val output = mutableListOf<String>()
        val operatorStack = ArrayDeque<String>()
        val tokens = tokenize(expression)

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> output.add(token)
                token in operators -> {
                    while (
                        operatorStack.isNotEmpty() &&
                        operatorStack.first() in operators &&
                        operators[token]!! <= operators[operatorStack.first()]!!
                    ) {
                        output.add(operatorStack.removeFirst())
                    }
                    operatorStack.addFirst(token)
                }

                token == "(" -> operatorStack.addFirst(token)
                token == ")" -> {
                    while (operatorStack.isNotEmpty() && operatorStack.first() != "(") {
                        output.add(operatorStack.removeFirst())
                    }
                    if (operatorStack.isEmpty() || operatorStack.removeFirst() != "(") {
                        throw IllegalArgumentException("Mismatched parentheses")
                    }
                }

                else -> throw IllegalArgumentException("Invalid token: $token")
            }
        }

        while (operatorStack.isNotEmpty()) {
            val op = operatorStack.removeFirst()
            if (op == "(" || op == ")") throw IllegalArgumentException("Mismatched parentheses")
            output.add(op)
        }

        return output
    }

    private fun evaluateRPN(tokens: List<String>): Double {
        val stack = ArrayDeque<Double>()
        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> stack.addFirst(token.toDouble())
                token in operators -> {
                    val b = stack.removeFirst()
                    val a = stack.removeFirst()
                    val result = when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> {
                            if (b == 0.0) throw ArithmeticException("Division by zero")
                            a / b
                        }

                        else -> throw IllegalArgumentException("Unsupported operator: $token")
                    }
                    stack.addFirst(result)
                }

                else -> throw IllegalArgumentException("Invalid RPN token: $token")
            }
        }
        return stack.first()
    }

    private fun tokenize(expr: String): List<String> {
        val result = mutableListOf<String>()
        val sb = StringBuilder()

        fun flushBuffer() {
            if (sb.isNotEmpty()) {
                result.add(sb.toString())
                sb.clear()
            }
        }

        for (char in expr.replace("\\s+".toRegex(), "")) {
            when (char) {
                in '0'..'9', '.' -> sb.append(char)
                in "+-*/()".toCharArray() -> {
                    flushBuffer()
                    result.add(char.toString())
                }

                else -> throw IllegalArgumentException("Invalid character: $char")
            }
        }
        flushBuffer()
        return result
    }
}

fun String.calculateMathExpression(): String {
    return kotlin.runCatching { ExpressionCalculator.evaluate(this) }.getOrElse { this }
}

fun processExpressions(
    input: String,
    evaluators: Map<String, (String) -> String> = mapOf(
        "math" to { exp ->
            exp.calculateMathExpression()
        }
    ),
    supportedTags: List<String> = evaluators.keys.toList()
): String {
    val output = StringBuilder()
    var i = 0

    while (i < input.length) {
        if (input[i] == '\\' && i + 1 < input.length && input[i + 1] == '@') {
            output.append('@')
            i += 2
            continue
        }

        var matchedTag: String? = null
        for (tag in supportedTags) {
            if (input.startsWith("@$tag{", i)) {
                matchedTag = tag
                break
            }
        }

        if (matchedTag != null) {
            val evaluator = evaluators[matchedTag]
            i += matchedTag.length + 2 // skip @tag{

            var braceCount = 1
            val exprStart = i
            while (i < input.length && braceCount > 0) {
                when (input[i]) {
                    '{' -> braceCount++
                    '}' -> braceCount--
                }
                i++
            }

            val exprEnd = i - 1
            if (braceCount == 0) {
                val innerExpr = input.substring(exprStart, exprEnd)
                val originalExprWithTag = "@$matchedTag{$innerExpr}"
                val evaluated = try {
                    val resolvedInnerExpr = processExpressions(innerExpr, evaluators, supportedTags)
                    evaluator?.invoke(resolvedInnerExpr) ?: originalExprWithTag
                } catch (e: Exception) {
                    originalExprWithTag
                }
                output.append(evaluated)
            } else {
                output.append("@$matchedTag{")
                output.append(input.substring(exprStart))
                break
            }
        } else {
            output.append(input[i])
            i++
        }
    }

    return output.toString()
}
