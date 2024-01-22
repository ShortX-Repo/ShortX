package shortx.pluginsdk.demo.evaluator

import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import shortx.plugin.ActionEvaluator

class DemoActionEvaluator : ActionEvaluator {

    override suspend fun evaluate(input: ActionEvaluator.Input) {
        withContext(Dispatchers.Main) {
            with(input) {
                Toast.makeText(
                    androidContext,
                    "Hello, ${paramsData.getStringDataOrDefault("input", "N/A")}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}