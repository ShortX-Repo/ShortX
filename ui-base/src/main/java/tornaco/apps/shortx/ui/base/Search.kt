@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import tornaco.apps.shortx.core.res.Remix

@Composable
fun rememberSearchBarState(
    itemCount: Int = 0,
    keywordChanged: (String) -> Unit = {}
): SearchBarState {
    return remember(itemCount) {
        SearchBarState(itemCount, keywordChanged)
    }
}

@Composable
fun SearchBar(searchBarState: SearchBarState, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchTextField(searchBarState)
        }
    }
}

@Composable
fun SearchTextField(searchBarState: SearchBarState) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    TextField(modifier = Modifier
        .fillMaxWidth(),
        value = searchBarState.keyword,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        label = if (searchBarState.count > 0) {
            {
                Text(
                    text = LocalI18N.current["ui.search.title.search.n.items", mapOf(
                        "count" to searchBarState.count.toString()
                    )]
                )
            }
        } else {
            null
        },
        shape = ShortXTextFieldCornerShape,
        maxLines = 1,
        trailingIcon = {
            if (searchBarState.keyword.isEmpty()) {
                IconButton(onClick = {
                }) {
                    RemixIcon(remixName = Remix.System.search_2_line)
                }
            } else {
                IconButton(onClick = {
                    searchBarState.inputKeyword("")
                    keyboardController?.hide()
                    focusManager.clearFocus(true)
                }) {
                    RemixIcon(remixName = Remix.System.close_line)
                }
            }
        },
        onValueChange = {
            searchBarState.inputKeyword(it)
        })
}

class SearchBarState(val count: Int, val keywordChanged: (String) -> Unit) {
    private var _keyword by mutableStateOf("")
    val keyword get() = _keyword

    fun inputKeyword(value: String) {
        _keyword = value

        keywordChanged(value)
    }
}