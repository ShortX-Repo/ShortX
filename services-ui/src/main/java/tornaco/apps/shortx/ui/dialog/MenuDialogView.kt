@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.ui.base.ListItem

data class MenuItem(
    val id: Int,
    val title: String,
    val message: String? = null
)

@SuppressLint("ViewConstructor")
class MenuDialogView(
    context: Context,
    i18N: I18N,
    style: DialogUiStyleSettings,
    private val onDismissRequest: () -> Unit,
    private val title: String,
    private val menuItems: List<MenuItem>,
    private val onItemClick: (Int) -> Unit,
) : DialogView(context, i18N, style) {
    @Composable
    override fun DialogContent() {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
                    .heightIn(max = 800.dp),
            ) {
                stickyHeader {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            title, style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.W500
                            ).applyStyle(style)
                        )
                    }
                }
                items(menuItems) {
                    ListItem(
                        titleLines = listOfNotNull(it.title, it.message),
                        onClick = {
                            onItemClick(it.id)
                        }
                    )
                }
            }
        }
    }
}