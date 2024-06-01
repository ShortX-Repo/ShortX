package tornaco.apps.shortx.ui.addrule.fact.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.fact.MethodHookExpressions
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject


data class MethodHookSelectorState(
    val packageName: String = "android",
    val className: String = "",
    val methodName: String = "",
    val lifecycle: MethodHookLifecycle = MethodHookLifecycle.LoadedPackage,
    val expressions: List<MethodHookExpressions> = emptyList(),
    val constraints: List<Fact.MethodHook.Constraint> = emptyList()
) {
    fun toFact(): Fact.MethodHook {
        return Fact.MethodHook(
            packageName = packageName,
            className = className,
            methodName = methodName,
            beforeMethod = false,
            expressions = expressions,
            lifecycle = lifecycle,
            constraints = constraints
        )
    }
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MethodHookSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<MethodHookSelectorState>(
        context, { MethodHookSelectorState() }
    ) {

    fun initFrom(current: Fact.MethodHook?) {
        logger.d("init form MethodHook: $current")
        current?.let {
            updateState {
                copy(
                    packageName = it.packageName,
                    className = it.className,
                    methodName = it.methodName,
                    lifecycle = it.lifecycle,
                    expressions = it.expressions,
                    constraints = it.constraints
                )
            }
        }
    }

    fun setPackageName(it: String) {
        updateState { copy(packageName = it) }
    }

    fun setClassName(it: String) {
        updateState { copy(className = it) }
    }

    fun setMethodName(it: String) {
        updateState { copy(methodName = it) }
    }

    fun setLifecycle(lifecycle: MethodHookLifecycle) {
        updateState { copy(lifecycle = lifecycle) }
    }

    fun addExpression(expression: MethodHookExpressions) {
        updateState {
            copy(
                expressions = expressions.toMutableList().apply {
                    val index = indexOfFirst { it.contextKey == expression.contextKey }
                    if (index >= 0) {
                        set(index, expression)
                    } else {
                        add(expression)
                    }
                }
            )
        }
    }

    fun removeExpression(expression: MethodHookExpressions) {
        updateState {
            copy(
                expressions = expressions.toMutableList().apply {
                    removeIf { it.contextKey == expression.contextKey }
                }
            )
        }
    }

    fun addConstraint(constraint: Fact.MethodHook.Constraint) {
        logger.d("addConstraint: $constraint")
        updateState {
            copy(
                constraints = constraints.toMutableList().apply {
                    add(constraint)
                }
            )
        }
    }

    fun updateConstraint(constraint: Fact.MethodHook.Constraint, index: Int) {
        logger.d("updateConstraint: $constraint")
        updateState {
            copy(
                constraints = constraints.toMutableList().apply {
                    set(index, constraint)
                }
            )
        }
    }

    fun removeConstraint(constraint: Fact.MethodHook.Constraint) {
        updateState {
            copy(
                constraints = constraints.toMutableList().apply {
                    remove(constraint)
                }
            )
        }
    }
}