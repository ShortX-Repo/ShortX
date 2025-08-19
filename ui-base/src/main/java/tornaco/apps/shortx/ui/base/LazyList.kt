@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun LazyListScope.itemWithPadding(
    key: Any? = null,
    contentType: Any? = null,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp),
    content: @Composable LazyItemScope.() -> Unit
) {
    item(key, contentType) {
        Column(
            Modifier
                .padding(paddingValues)
                .animateItem(
                    spring()
                )
        ) {
            content()
        }
    }
}