package tornaco.apps.shortx.ui.addrule.condition.selector

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.composeLogger

@AndroidEntryPoint
class ConditionSelectorActivity : BaseActivity() {
    companion object {
        const val INTENT_EXTRA_KEY_CONDITION = "condition"
        const val INTENT_EXTRA_ARG1_CONDITION = "arg1"
        const val INTENT_EXTRA_ARG2_CONDITION = "arg2"

        fun ActivityResult.toConditionOrNull(): Condition? {
            return if (this.resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    this.data?.getParcelableExtra(INTENT_EXTRA_KEY_CONDITION, Condition::class.java)
                } else {
                    (this.data?.getParcelableExtra(INTENT_EXTRA_KEY_CONDITION)) as Condition?
                }
            } else {
                null
            }
        }

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, ConditionSelectorActivity::class.java)
            context.startActivity(starter)
        }

        fun newIntent(context: Context, arg1: String? = null, arg2: String? = null): Intent {
            return Intent(context, ConditionSelectorActivity::class.java).apply {
                arg1?.let { putExtra(INTENT_EXTRA_ARG1_CONDITION, arg1) }
                arg2?.let { putExtra(INTENT_EXTRA_ARG2_CONDITION, arg2) }
            }
        }
    }

    @Composable
    override fun Content() {
        val arg1 = remember {
            intent.getStringExtra(INTENT_EXTRA_ARG1_CONDITION)
        }
        val arg2 = remember {
            intent.getStringExtra(INTENT_EXTRA_ARG2_CONDITION)
        }
        composeLogger.d("arg1: $arg1, arg2: $arg2")

        ConditionSelector(close = {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }, onSelected = {
            setResult(Activity.RESULT_OK, Intent().putExtra(INTENT_EXTRA_KEY_CONDITION, it)
                .apply {
                    arg1?.let { putExtra(INTENT_EXTRA_ARG1_CONDITION, arg1) }
                    arg2?.let { putExtra(INTENT_EXTRA_ARG2_CONDITION, arg2) }
                })
            finish()
        })
    }
}