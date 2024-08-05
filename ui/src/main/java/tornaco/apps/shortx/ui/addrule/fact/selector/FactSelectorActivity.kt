package tornaco.apps.shortx.ui.addrule.fact.selector

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.getTypeUrl
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.toProtoFact
import tornaco.apps.shortx.ui.analytics.Event
import tornaco.apps.shortx.ui.analytics.analytics
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.composeLogger

@AndroidEntryPoint
class FactSelectorActivity : BaseActivity() {
    companion object {
        const val INTENT_EXTRA_KEY_FACT = "fact"
        const val INTENT_EXTRA_ARG1_FACT = "arg1"
        const val INTENT_EXTRA_ARG2_FACT = "arg2"

        fun ActivityResult.toFactOrNull(): Fact? {
            return if (this.resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    this.data?.getParcelableExtra(INTENT_EXTRA_KEY_FACT, Fact::class.java)
                } else {
                    (this.data?.getParcelableExtra(INTENT_EXTRA_KEY_FACT)) as Fact?
                }
            } else {
                null
            }
        }

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, FactSelectorActivity::class.java)
            context.startActivity(starter)
        }

        fun newIntent(context: Context, arg1: String? = null, arg2: String? = null): Intent {
            return Intent(context, FactSelectorActivity::class.java).apply {
                arg1?.let { putExtra(INTENT_EXTRA_ARG1_FACT, arg1) }
                arg2?.let { putExtra(INTENT_EXTRA_ARG2_FACT, arg2) }
            }
        }
    }

    @Composable
    override fun Content() {
        val arg1 = remember {
            intent.getStringExtra(INTENT_EXTRA_ARG1_FACT)
        }
        val arg2 = remember {
            intent.getStringExtra(INTENT_EXTRA_ARG2_FACT)
        }
        composeLogger.d("arg1: $arg1, arg2: $arg2")

        FactSelector(close = {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }, onSelected = {
            kotlin.runCatching {
                // Analytics
                val typeUrl = it.toProtoFact().getTypeUrl("")
                analytics.logEvent(Event.AddFact(typeUrl))
            }

            setResult(Activity.RESULT_OK, Intent().putExtra(INTENT_EXTRA_KEY_FACT, it)
                .apply {
                    arg1?.let { putExtra(INTENT_EXTRA_ARG1_FACT, arg1) }
                    arg2?.let { putExtra(INTENT_EXTRA_ARG2_FACT, arg2) }
                })
            finish()
        })
    }
}