package tornaco.apps.shortx.ui.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

object TipIds {
    val actionContent by lazy {
        "ActionContent"
    }

    val commonRuleItem by lazy {
        "CommonRule"
    }
}

@Composable
fun UserTip(modifier: Modifier = Modifier, tipId: String, message: String) {
    val context = LocalContext.current
    val pref = remember { Pref(context) }
    val userReadTips by pref.userTipsHasRead.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = tipId.isNotBlank() && message.isNotBlank() &&
                userReadTips != null
                && !userReadTips!!.contains(tipId)
    ) {
        TipCard(
            modifier = modifier
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

                ShortXTextButton(onClick = {
                    scope.launch { pref.userReadTip(tipId) }
                }) {
                    Text(text = LocalI18N.current[I18N_R.UI_COMMON_GOT_IT])
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
            override fun get(key: String, args: Map<String, String>, fallback: String?): String {
                return "OK"
            }
        }) {
            Column {
                UserTip(tipId = "x", message = "This is message.")
            }
        }
    }
}