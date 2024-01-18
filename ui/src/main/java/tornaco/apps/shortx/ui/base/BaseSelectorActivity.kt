package tornaco.apps.shortx.ui.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.core.os.BundleCompat


abstract class BaseSelectorActivity<T : Parcelable> : BaseActivity() {
    companion object {
        const val INTENT_EXTRA_KEY_CURRENT = "selector.current"

        inline fun <reified T : Parcelable> ActivityResult.toBaseSelectorCurrentDataOrNull(): T? {
            return if (this.resultCode == Activity.RESULT_OK) {
                this.data?.extras?.let {
                    // Even though API was introduced in 33, we use 34 as 33 is bugged in some scenarios.
                    BundleCompat.getParcelable(
                        it,
                        INTENT_EXTRA_KEY_CURRENT,
                        T::class.java
                    )
                }
            } else {
                null
            }
        }

        inline fun <reified T : Parcelable> newIntent(
            context: Context,
            currentData: T? = null,
            clazz: Class<out Activity>
        ): Intent {
            return Intent(context, clazz).apply {
                currentData?.let {
                    putExtra(INTENT_EXTRA_KEY_CURRENT, it)
                }
            }
        }
    }

    private fun Intent.toBaseSelectorDataOrNull(): T? {
        return (this.getParcelableExtra(INTENT_EXTRA_KEY_CURRENT)) as T?
    }

    fun cancelAndFinish() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    fun setResultAndFinish(action: T) {
        setResult(Activity.RESULT_OK, Intent().putExtra(INTENT_EXTRA_KEY_CURRENT, action))
        finish()
    }

    @Composable
    override fun Content() {
        val current = intent.toBaseSelectorDataOrNull()
        SelectorContent(current)
    }

    @Composable
    abstract fun BaseSelectorActivity<T>.SelectorContent(currentData: T?)
}