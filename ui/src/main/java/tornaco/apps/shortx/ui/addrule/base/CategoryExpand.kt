package tornaco.apps.shortx.ui.addrule.base

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.settings.pref.Pref

class CategoryState(
    private val categoryId: String,
    private val pref: Pref,
    private val scope: CoroutineScope
) {
    var isExpand by mutableStateOf(false)

    fun toggle() {
        scope.launch {
            if (isExpand) {
                scope.launch { pref.miscUiTogglesOn(categoryId) }
            } else {
                scope.launch { pref.miscUiTogglesOff(categoryId) }
            }
        }
    }
}

@Composable
fun rememberCategoryState(categoryTitle: String): CategoryState {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val pref = remember { Pref(context) }
    val id = remember {
        "action-category-collapse-${categoryTitle}"
    }

    val state = remember {
        CategoryState(id, pref, scope)
    }

    LaunchedEffect(categoryTitle) {
        pref.miscUiToggles.collectLatest {
            val isExpand = !it.contains(id)
            state.isExpand = isExpand
        }
    }

    return state
}

@Composable
fun ActionCategory(categoryTitle: String, categoryState: CategoryState) {
    val dropdownRotate by animateFloatAsState(
        targetValue = if (categoryState.isExpand) 0f else 180f,
        label = "dropdownRotate"
    )

    Row(
        modifier = Modifier.fillMaxWidth().clip(ShortXCardRoundedCornerShape).clickable {
            categoryState.toggle()
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ActionCategoryTitle(categoryTitle)
        IconButton(onClick = {
            categoryState.toggle()
        }) {
            RemixIcon(
                modifier = Modifier.rotate(dropdownRotate),
                remixName = Remix.Arrows.arrow_drop_down_fill
            )
        }
    }
}

@Composable
private fun ActionCategoryTitle(categoryTitle: String) {
    SmallTitle(
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
        text = categoryTitle
    )
}