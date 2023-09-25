package tornaco.apps.shortx.ui.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.action.model.Action


abstract class BaseSelectorActivity<T : Action> : BaseActivity() {
    companion object {
        const val INTENT_EXTRA_KEY_ACTION = "selector.action"

        inline fun <reified T : Action> ActivityResult.toBaseSelectorActionOrNull(): T? {
            return if (this.resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    this.data?.getParcelableExtra(
                        INTENT_EXTRA_KEY_ACTION,
                        T::class.java
                    )
                } else {
                    (this.data?.getParcelableExtra(INTENT_EXTRA_KEY_ACTION)) as T?
                }
            } else {
                null
            }
        }

        inline fun <reified T : Action> newIntent(
            context: Context,
            currentAction: T? = null,
            clazz: Class<out Activity>
        ): Intent {
            return Intent(context, clazz).apply {
                currentAction?.let {
                    putExtra(INTENT_EXTRA_KEY_ACTION, it)
                }
            }
        }
    }

    private fun Intent.toBaseSelectorActionOrNull(): T? {
        return (this.getParcelableExtra(INTENT_EXTRA_KEY_ACTION)) as T?
    }

    fun cancelAndFinish() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    fun setResultAndFinish(action: T) {
        setResult(Activity.RESULT_OK, Intent().putExtra(INTENT_EXTRA_KEY_ACTION, action))
        finish()
    }

    @Composable
    override fun Content() {
        val current = intent.toBaseSelectorActionOrNull()
        SelectorContent(current)
    }

    @Composable
    abstract fun BaseSelectorActivity<T>.SelectorContent(currentAction: T?)
}