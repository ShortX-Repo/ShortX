package shortx.plugin

import android.content.Context
import androidx.annotation.Keep

interface ActionEvaluator {
    data class Input(
        val androidContext: Context,
        val contextData: Map<String, String>,
        val paramsData: ParamsData
    )

    @Keep
    suspend fun evaluate(input: Input)
}