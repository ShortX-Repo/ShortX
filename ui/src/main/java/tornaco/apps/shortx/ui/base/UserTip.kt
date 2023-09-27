package tornaco.apps.shortx.ui.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.util.UUID

object TipIds {
    val actionContent by lazy {
        BuildProp.APP_IS_DEBUG.select(
            "ActionContent_${UUID.randomUUID()}",
            "ActionContent"
        )
    }
}

@Composable
fun UserTip(tipId: String, message: String) {
    val context = LocalContext.current
    val pref = remember { Pref(context) }
    val userReadTips by pref.userTipsHasRead.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = userReadTips != null
                && !userReadTips!!.contains(tipId)
    ) {
        TipCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f, fill = false),
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )

                TextButton(onClick = {
                    scope.launch { pref.userReadTip(tipId) }
                }) {
                    Text(text = LocalI18N.current["ui.common.got.it"])
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun TipPreview() {
    ShortXTheme {
        CompositionLocalProvider(value = LocalI18N provides object : I18N {
            override fun get(key: String, args: Map<String, String>): String {
                return "OK"
            }
        }) {
            Column {
                UserTip(tipId = "x", message = "This is message.")
            }
        }
    }
}