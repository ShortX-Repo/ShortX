@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.res.Remix

fun List<App>.filterSearchKeyword(keyword: String?): List<App> {
    return if (keyword.isNullOrBlank()) this else filter {
        it.label.contains(keyword, ignoreCase = true)
                || it.pkg.pkgName.contains(keyword, ignoreCase = true)
    }
}

@Composable
fun rememberSearchBarState(itemCount: Int): SearchBarState {
    return remember(itemCount) {
        SearchBarState(itemCount)
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
    TextField(modifier = Modifier
        .fillMaxWidth(),
        value = searchBarState.keyword,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        label = {
            Text(
                text = LocalI18N.current["ui.search.title.search.n.items", mapOf(
                    "count" to searchBarState.count.toString()
                )]
            )
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
                }) {
                    RemixIcon(remixName = Remix.System.close_line)
                }
            }
        },
        onValueChange = {
            searchBarState.inputKeyword(it)
        })
}

class SearchBarState(val count: Int) {
    private var _keyword by mutableStateOf("")
    val keyword get() = _keyword

    fun inputKeyword(value: String) {
        _keyword = value
    }
}