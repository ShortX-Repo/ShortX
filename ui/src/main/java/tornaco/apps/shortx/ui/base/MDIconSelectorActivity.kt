@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base


import android.content.Intent
import android.os.Parcelable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.selector.PhotoSelectorActivity
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.Compressor.compressAndToBase64

@Parcelize
data class MDIcon(
    val iconNameOrBase64: String,
    val isIconName: Boolean
) : Parcelable

class MDIconSelectorActivity : BaseSelectorActivity<MDIcon>() {
    @Composable
    override fun BaseSelectorActivity<MDIcon>.SelectorContent(currentData: MDIcon?) {
        MDIconSelector()
    }
}

@Composable
private fun BaseSelectorActivity<MDIcon>.MDIconSelector() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val uri = result.data?.data
            uri?.let {
                runCatching {
                    scope.launch {
                        val base64 = compressAndToBase64(context, uri)
                        setResultAndFinish(MDIcon(base64, false))
                    }
                }.onFailure {
                    TextDisplayActivity.start(context, "OOPS! WTF!", it.stackTraceToString())
                }
            }
        }
    )

    ShortXTheme {
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        Scaffold(
            modifier = Modifier,
            topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = { Text("Icons") },
                    navigationIcon = {
                        IconButton(onClick = {
                            cancelAndFinish()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            photoPicker.launch(Intent(context, PhotoSelectorActivity::class.java))
                        }) {
                            RemixIcon(remixName = Remix.System.external_link_line)
                        }
                    }
                )
            }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                val iconCategories: List<String> = listOf("All") + Remix.all.keys.toList()
                val indexAll = 0

                ShortXScrollableTabRow(selectedTabIndex = selectedTabIndex) {
                    iconCategories.forEachIndexed { index, categoryName ->
                        Tab(selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = categoryName,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            })
                    }
                }

                val searchBarState = rememberSearchBarState()
                SearchBar(
                    searchBarState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                )

                val icons =
                    remember(selectedTabIndex, searchBarState.keyword) {
                        val src =
                            if (selectedTabIndex == indexAll) Remix.allFlat else Remix.all[iconCategories[selectedTabIndex]]
                        src?.filterKeys {
                            searchBarState.keyword.isBlank() || it.contains(searchBarState.keyword)
                        }?.map { it.key to it.value }
                    }

                icons?.let {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize(),
                        columns = GridCells.Fixed(3),
                        content = {
                            itemsIndexed(icons) { _, nameAndResId ->
                                Column(
                                    modifier = Modifier
                                        .clickable {
                                            setResultAndFinish(MDIcon(nameAndResId.first, true))
                                        }
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    RemixIcon(
                                        modifier = Modifier.size(32.dp),
                                        remixName = nameAndResId.first
                                    )
                                    TinySpacer()
                                    Text(
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                                        text = nameAndResId.first,
                                        overflow = TextOverflow.Visible,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        })
                }
            }
        }
    }
}