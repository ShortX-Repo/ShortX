/*
 * (C) Copyright 2022 Thanox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.subscribe

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.R
import tornaco.apps.shortx.ui.base.ImageDialog
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalAppName
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalNavHostController
import tornaco.apps.shortx.ui.base.MarkdownText
import tornaco.apps.shortx.ui.base.ProgressDialog
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TextInputState
import tornaco.apps.shortx.ui.base.TipDialog
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.UiState
import tornaco.apps.shortx.ui.base.rememberCommonDialogState
import tornaco.apps.shortx.ui.base.rememberProgressDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.base.rememberTipDialogState
import tornaco.apps.shortx.ui.main.AppRoute
import tornaco.apps.shortx.ui.subscribe.code.SubscriptionConfig
import tornaco.apps.shortx.ui.theme.AliBlue
import tornaco.apps.shortx.ui.theme.PayPalBlue
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.theme.VipColorGold
import tornaco.apps.shortx.ui.theme.WeChatGreen
import tornaco.apps.shortx.ui.util.ClipboardUtils

@Composable
fun SubscribeScreen(back: () -> Unit) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        title = {
                            Text(
                                text = LocalI18N.current["ui.subscription.intro.title"]
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                    )
                }) { contentPadding ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(contentPadding)
                ) {
                    val vm = hiltViewModel<SubscribeViewModel>()
                    LaunchedEffect(vm) {
                        vm.loadState()
                    }

                    val state by vm.state.collectAsState()
                    if (state.isSubscribed) {
                        SubscribedUi()
                    } else {
                        when (val subState = state.subscriptionConfigState) {
                            is UiState.Loaded -> {
                                UnsubscribedUi(subState.data)
                            }

                            is UiState.Error -> {
                                LoadError(
                                    modifier = Modifier
                                        .fillMaxSize()
                                )
                            }

                            UiState.Loading -> {
                                Loading(
                                    modifier = Modifier
                                        .fillMaxSize()
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
private fun SubscribedUi() {
    val vm = hiltViewModel<SubscribeViewModel>()
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        when (state.source) {
            is SubscriptionSource.ActivationCode -> {
                LottieLoaderSuccessView()
            }

            SubscriptionSource.GooglePlay -> {
                LottieLoaderSuccessView()
            }

            null -> {}
        }
    }
}


@Composable
private fun UnsubscribedUi(config: SubscriptionConfig) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        val i18N = LocalI18N.current

        val progressDialog = rememberProgressDialogState(
            title = i18N["ui.subscription.code.verify.title"],
            message = i18N["ui.subscription.code.verify.message"]
        )
        ProgressDialog(state = progressDialog)

        val context = LocalContext.current
        val failMsg = i18N["ui.subscription.code.verify.fail"]
        val failNetworkMsg =
            i18N["ui.subscription.code.verify.fail.network"]

        val vm = hiltViewModel<SubscribeViewModel>()
        LaunchedEffect(vm) {
            vm.codeVerifyEvent.collectLatest {
                when (it) {
                    CodeVerifyEvent.Verifying -> {
                        progressDialog.show()
                    }

                    CodeVerifyEvent.Success -> {
                        progressDialog.dismiss()
                    }

                    CodeVerifyEvent.Fail -> {
                        progressDialog.dismiss()
                        Toast.makeText(context, failMsg, Toast.LENGTH_LONG).show()
                    }

                    CodeVerifyEvent.NetworkError -> {
                        progressDialog.dismiss()
                        Toast.makeText(context, failNetworkMsg, Toast.LENGTH_LONG)
                            .show()
                    }

                    CodeVerifyEvent.Error -> {
                        progressDialog.dismiss()
                        Toast.makeText(context, failMsg, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        val navHost = LocalNavHostController.current


        val inputDialogState = rememberTextInputState(
            title = i18N["Input code"],
            message = null,
            onSelected = {
                vm.verify(it.trim().replace(System.lineSeparator(), ""))
            }
        )
        TextInputDialog(state = inputDialogState)


        LargeSpacer()

        if (config.extra1.isNotBlank()) {
            TipRow(text = config.extra1)
            LargeSpacer()
        }

        val textColor = LocalContentColor.current.takeOrElse {
            MaterialTheme.colorScheme.onPrimaryContainer
        }
        val appName = LocalAppName.current
        MarkdownText(
            markdown = i18N["ui.subscription.subscribe.how.to.page", mapOf(
                "priceCNY" to "${config.priceCNY.toInt()}",
                "priceUSD" to "${config.priceUSD}",
                "mail" to config.email,
                "qq" to config.qq,
                "app_name" to appName,
            )],
            color = textColor,
            onLinkClicked = {
                kotlin.runCatching {
                    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                        data = it.toUri()
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                }

                ClipboardUtils.copyToClipboard(context, "link", it)
                Toast.makeText(
                    context,
                    i18N["ui.common.copied.to.clipboard"],
                    Toast.LENGTH_LONG
                ).show()
            }
        )


        LargeSpacer()
        LargeSpacer()


        Fabs(inputDialogState, navHost)
    }
}

@Composable
private fun Fabs(
    inputDialogState: TextInputState,
    navHost: NavHostController
) {
    val context = LocalContext.current
    val i18N = LocalI18N.current

    val wechatPayQRDialogState = rememberCommonDialogState()
    ImageDialog(wechatPayQRDialogState, R.drawable.ui_pay_qr_wechat)

    val aliPayQRDialogState = rememberCommonDialogState()
    ImageDialog(aliPayQRDialogState, R.drawable.ui_pay_qr_alipay)

    val paypalDialogState = rememberTipDialogState(
        title = "Paypal",
        tip = "https://www.paypal.me/tornaco"
    )
    TipDialog(state = paypalDialogState)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 64.dp),
        horizontalAlignment = Alignment.End
    ) {

        ExtendedFloatingActionButton(
            containerColor = WeChatGreen,
            onClick = { wechatPayQRDialogState.show() },
            text = {
                Text(text = "Wechat", color = Color.White)
            },
            icon = {
                RemixIcon(tint = Color.White, remixName = Remix.Logos.wechat_pay_fill)
            })
        StandardSpacer()

        ExtendedFloatingActionButton(
            containerColor = AliBlue,
            onClick = { aliPayQRDialogState.show() },
            text = {
                Text(text = "Ali Pay", color = Color.White)
            },
            icon = {
                RemixIcon(tint = Color.White, remixName = Remix.Logos.alipay_fill)
            })
        StandardSpacer()

        ExtendedFloatingActionButton(
            containerColor = PayPalBlue,
            onClick = {
                paypalDialogState.show()
                ClipboardUtils.copyToClipboard(
                    context,
                    "paypal",
                    "https://www.paypal.me/tornaco"
                )
                Toast.makeText(
                    context,
                    i18N["ui.common.copied.to.clipboard"],
                    Toast.LENGTH_LONG
                ).show()
            },
            text = {
                Text(text = "PayPal", color = Color.White)
            },
            icon = {
                RemixIcon(tint = Color.White, remixName = Remix.Logos.paypal_fill)
            })
        StandardSpacer()
        StandardSpacer()

        ExtendedFloatingActionButton(
            containerColor = VipColorGold,
            onClick = { inputDialogState.show() },
            text = {
                Text(
                    text = LocalI18N.current["ui.subscription.subscribe.verify.code"],
                    color = Color.White
                )
            },
            icon = {
                RemixIcon(tint = Color.White, remixName = Remix.System.login_circle_fill)
            })
        StandardSpacer()

        ExtendedFloatingActionButton(
            containerColor = VipColorGold,
            onClick = { navHost.navigate(AppRoute.LVLCheck.name) },
            text = {
                Text(
                    text = LocalI18N.current["ui.subscription.subscribe.verify.play"],
                    color = Color.White
                )
            },
            icon = {
                RemixIcon(tint = Color.White, remixName = Remix.Logos.google_play_fill)
            })
    }
}