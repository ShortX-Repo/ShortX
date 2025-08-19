@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
fun SearchBar(
    searchBarState: SearchBarState,
    modifier: Modifier = Modifier,
    autoFocus: Boolean = false,
    textFieldStyle: TextFieldStyle = TextFieldStyle.Outlined,
    onSearchClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchTextField(searchBarState, autoFocus, onSearchClick, textFieldStyle)
        }
    }
}

@Composable
fun SearchTextField(
    searchBarState: SearchBarState,
    autoFocus: Boolean,
    onSearchClick: () -> Unit,
    textFieldStyle: TextFieldStyle = TextFieldStyle.Outlined
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    ShortXTextField(modifier = Modifier
        .fillMaxWidth()
        .focusRequester(focusRequester),
        value = searchBarState.keyword,
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
        showSymbolButton = false,
        maxLines = 1,
        trailingIcon = {
            if (searchBarState.keyword.isEmpty()) {
                // Noop.
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
        leadingIcon = {
            IconButton(onClick = {
                onSearchClick()
            }) {
                RemixIcon(remixName = Remix.System.search_2_line)
            }
        },
        onValueChange = {
            searchBarState.inputKeyword(it)
        },
        textFieldStyle = textFieldStyle
    )

    if (autoFocus) LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

class SearchBarState(val count: Int, val keywordChanged: (String) -> Unit) {
    private var _keyword by mutableStateOf("")
    val keyword get() = _keyword

    fun inputKeyword(value: String) {
        _keyword = value

        keywordChanged(value)
    }
}