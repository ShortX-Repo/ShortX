package tornaco.apps.shortx.ui.addrule.fact.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject


data class MethodHookSelectorState(
    val packageName: String = "android",
    val className: String = "",
    val methodName: String = "",
    val lifecycle: MethodHookLifecycle = MethodHookLifecycle.LoadedPackage,
) {
    fun toFact(): Fact.MethodHook {
        return Fact.MethodHook(
            packageName = packageName,
            className = className,
            methodName = methodName,
            beforeMethod = false,
            argExpressionsMVEL = emptyList(),
            lifecycle = lifecycle
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
        current?.let {
            updateState {
                copy(
                    packageName = it.packageName,
                    className = it.className,
                    methodName = it.methodName,
                    lifecycle = it.lifecycle
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
}