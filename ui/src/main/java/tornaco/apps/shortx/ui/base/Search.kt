@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun rememberSearchBarState(itemCount: Int): SearchBarState {
    return remember(itemCount) {
        SearchBarState(itemCount)
    }
}

@Composable
fun SearchBar(searchBarState: SearchBarState) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
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
        shape = RoundedCornerShape(6.dp),
        maxLines = 1,
        trailingIcon = {
            if (searchBarState.keyword.isEmpty()) {
                IconButton(onClick = {
                }) {
                    Icon(
                        imageVector = Icons.Filled.Search, contentDescription = "Search"
                    )
                }
            } else {
                IconButton(onClick = {
                    searchBarState.inputKeyword("")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Close, contentDescription = "Clear keywords"
                    )
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