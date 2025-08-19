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

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tornaco.apps.shortx.core.util.Logger
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * We are using this to hide the default error page.
 * The extra "?app" is an indicator that it is us loading the blank page.
 */
private const val CUSTOM_BLANK = "about:blank?app"

@HiltViewModel
class WebViewViewModel @Inject constructor() : ViewModel() {
    private val logger = Logger("WebViewViewModel")

    private val _state = MutableStateFlow(WebViewState())
    val state: StateFlow<WebViewState>
        get() = _state

    // ----------------------------------------------------------------

    private var _swipeRefreshLayout: WeakReference<SwipeRefreshLayout>? = null

    private var _webView: WeakReference<WebView>? = null

    // ----------------------------------------------------------------

    fun initSwipeRefresh(swipeRefreshLayout: SwipeRefreshLayout) {
        logger.e("initSwipeRefresh")

        this._swipeRefreshLayout = WeakReference(swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            _webView?.get()?.reload()
        }
    }

    // ----------------------------------------------------------------

    @SuppressLint("SetJavaScriptEnabled")
    fun initWebView(webView: WebView) {
        logger.e("initWebView")

        WebView.setWebContentsDebuggingEnabled(false)

        this._webView = WeakReference(webView)

        webView.apply {

            val webSettings = this.settings

            webSettings.run {
                javaScriptEnabled = true
                domStorageEnabled = true
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(
                    view: WebView?,
                    url: String?,
                    favicon: Bitmap?
                ) {
                    logger.e("onPageStarted")

                    if (url != CUSTOM_BLANK) {
                        _state.value = _state.value.copy(
                            loadingProgress = 0,
                            error = null,
                        )
                    }

                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    logger.e("onPageFinished")

                    _state.value = _state.value.copy(
                        loadingProgress = null,
                    )

                    _swipeRefreshLayout?.get()?.isRefreshing = false

                    super.onPageFinished(view, url)
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    logger.e("onReceivedError(): error: ${error?.errorCode}")

                    request?.let {
                        // Help: https://stackoverflow.com/a/44273685/2263329
                        if (it.isForMainFrame) {
                            error?.let {
                                // Help: https://stackoverflow.com/a/33419123/2263329
                                onReceivedError(
                                    view,
                                    error.errorCode,
                                    error.description.toString(),
                                    request.url.toString()
                                )
                            }
                        }
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    logger.e("onReceivedError(): errorCode: $errorCode | description: $description | failingUrl: $failingUrl")

                    try {
                        view?.stopLoading()
                        view?.loadUrl(CUSTOM_BLANK)
                    } catch (e: Exception) {
                        logger.e(e)
                    }

                    _state.value = _state.value.copy(
                        error = WebViewError(
                            errorCode = errorCode,
                            description = description,
                            failingUrl = failingUrl,
                        )
                    )
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(
                    view: WebView?,
                    newProgress: Int
                ) {
                    logger.e("onProgressChanged: newProgress: $newProgress")

                    _state.value = _state.value.copy(
                        loadingProgress = newProgress,
                    )
                }
            }

            // WebView Cookies
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
        }
    }

    fun webViewCanGoBack(): Boolean {
        _webView?.get()?.let {
            logger.e("webViewCanGoBack")

            return it.canGoBack()
        }
        return false
    }

    fun webViewGoBack() {
        logger.e("webViewGoBack")

        _webView?.get()?.goBack()
    }

    fun webViewReload() {
        _webView?.get()?.reload()
    }
}

data class WebViewState(
    val loadingProgress: Int? = null,
    val error: WebViewError? = null,
)

data class WebViewError(
    val errorCode: Int,
    val description: String?,
    val failingUrl: String?,
)
