package tornaco.apps.shortx.ui.addrule.fact.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject


data class MethodHookSelectorState(
    val packageName: String = "android",
    val className: String = "",
    val methodName: String = "",
) {
    fun toFact(): Fact.MethodHook {
        return Fact.MethodHook(
            packageName = packageName,
            className = className,
            methodName = methodName,
            beforeMethod = false,
            argExpressionsMVEL = emptyList()
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
                    methodName = it.methodName
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
}