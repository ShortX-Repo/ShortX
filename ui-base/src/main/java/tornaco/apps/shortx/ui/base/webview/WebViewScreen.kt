/*
 * Copyright 2021 Md. Mahmudul Hasan Shohag
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ------------------------------------------------------------------------
 *
 * Project: Why Not Compose!
 * Developed by: @ImaginativeShohag
 *
 * Md. Mahmudul Hasan Shohag
 * imaginativeshohag@gmail.com
 *
 * Source: https://github.com/ImaginativeShohag/Why-Not-Compose
 */

package tornaco.apps.shortx.ui.base.webview

import android.content.res.Configuration
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.webview.components.ErrorView
import tornaco.apps.shortx.ui.theme.ShortXTheme

data class WebViewTarget(val name: String, val url: String)

@Composable
fun WebViewScreen(
    target: WebViewTarget,
    goBack: () -> Unit,
) {
    val viewModel = hiltViewModel<WebViewViewModel>()
    val state by viewModel.state.collectAsState()

    BackHandler {
        if (viewModel.webViewCanGoBack()) {
            viewModel.webViewGoBack()
        } else {
            goBack()
        }
    }

    WebViewSkeleton(
        title = target.name,
        goBack = {
            goBack()
        },
        webView = { modifier ->
            WebViewContainer(
                modifier = modifier,
                url = target.url,
                loadingProgress = state.loadingProgress,
                initSwipeRefresh = viewModel::initSwipeRefresh,
                initWebView = viewModel::initWebView,
            )
        },
        webViewError = state.error,
        onRetry = viewModel::webViewReload,
    )
}

@Preview
@Composable
fun WebViewSkeletonPreview() {
    ShortXTheme {
        WebViewSkeleton(
            title = "About Me",
            goBack = {},
            webView = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Cyan)
                )
            }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WebViewSkeletonPreviewDark() {
    ShortXTheme {
        WebViewSkeleton(
            title = "About Me",
            goBack = {},
            webView = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Cyan)
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewSkeleton(
    title: String,
    goBack: () -> Unit,
    webView: @Composable (Modifier) -> Unit,
    webViewError: WebViewError? = null,
    onRetry: () -> Unit = {},
) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        title = { Text(title) },
                        navigationIcon = {
                            IconButton(onClick = { goBack() }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                    )
                }
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    Box(
                        Modifier.weight(1f)
                    ) {
                        webView(Modifier.fillMaxSize())

                        androidx.compose.animation.AnimatedVisibility(
                            visible = webViewError != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            webViewError?.let {
                                ErrorView(
                                    errorCode = webViewError.errorCode,
                                    description = webViewError.description,
                                    failingUrl = webViewError.failingUrl,
                                    onRetry = onRetry
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WebViewContainer(
    modifier: Modifier,
    url: String,
    loadingProgress: Int?,
    initSwipeRefresh: (swipeRefreshLayout: SwipeRefreshLayout) -> Unit,
    initWebView: (webView: WebView) -> Unit,
) {
    Box(modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                SwipeRefreshLayout(context).apply {

                    initSwipeRefresh(this)

                    addView(
                        WebView(context).apply {
                            id = github.tornaco.shortx.ui.base.R.id.ui_webView
                            initWebView(this)
                        }
                    )
                }
            },
            update = { swipeRefreshLayout ->
                val webView = swipeRefreshLayout.findViewById<WebView>(github.tornaco.shortx.ui.base.R.id.ui_webView)

                swipeRefreshLayout.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                webView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                webView.loadUrl(url)
            }
        )

        LoadingContainer(
            progress = loadingProgress ?: 0,
            visible = loadingProgress != null,
        )
    }
}

@Preview
@Composable
private fun LoadingContainerPreview() {
    val scope = rememberCoroutineScope()
    val progress = remember { mutableStateOf(0) }

    LaunchedEffect(true) {
        scope.launch {
            while (true) {
                progress.value = 0

                delay(1000)

                progress.value = 33

                delay(1000)

                progress.value = 66

                delay(1000)

                progress.value = 100

                delay(1000)
            }
        }
    }

    LoadingContainer(
        progress.value
    )
}

@Composable
private fun LoadingContainer(
    progress: Int,
    visible: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition()
    val color by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.primary,
        targetValue = MaterialTheme.colorScheme.primary,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {

        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(.25f))
        ) {

            Row(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(24.dp)
                    .padding(start = 32.dp, end = 32.dp)
                    .background(Color(0xffdddddd), CircleShape)
            ) {
                BoxWithConstraints {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .animateContentSize()
                            .width(maxWidth * progress / 100)
                            .background(color, CircleShape)
                    )
                }
            }
        }
    }
}
