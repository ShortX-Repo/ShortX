package tornaco.apps.shortx.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.KeyEvent
import tornaco.apps.shortx.core.proto.Rule
import tornaco.apps.shortx.core.proto.StartActivity
import tornaco.apps.shortx.core.proto.True
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {}

        val fact = KeyEvent.newBuilder().setKeyCode(20).build().pack_()
        val conditions = listOf(
            True.newBuilder().build().pack_(),
        )
        val actions =
            listOf(StartActivity.newBuilder().setComponentNameAsString("name").build().pack_())
        val rule =
            Rule.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setLastUpdateTime(System.currentTimeMillis())
                .setFact(fact)
                .addAllConditions(conditions)
                .addAllActions(actions)
                .build()

        shortXManager.addRule(rule)

        Logger("MainActivity").w(shortXManager.getAllRules().joinToString(System.lineSeparator()) {
            it.id + " " + Date(it.lastUpdateTime)
        })
    }
}

