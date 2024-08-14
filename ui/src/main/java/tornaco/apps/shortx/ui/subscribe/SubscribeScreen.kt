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

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.R
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ImageDialog
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LargeTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LoadingAnimationDot
import tornaco.apps.shortx.ui.base.LocalAppName
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MarkdownText
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.NiaGradientBackground
import tornaco.apps.shortx.ui.base.ProgressDialog
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TipDialog
import tornaco.apps.shortx.ui.base.UiState
import tornaco.apps.shortx.ui.base.rememberCommonDialogState
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberProgressDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.base.rememberTipDialogState
import tornaco.apps.shortx.ui.subscribe.code.SubscriptionConfig2
import tornaco.apps.shortx.ui.theme.GoogleBlue
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.theme.VipColorGold
import tornaco.apps.shortx.ui.util.ClipboardUtils

@AndroidEntryPoint
class SubscribeActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, SubscribeActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        SubscribeScreen {
            finish()
        }
    }
}

@Composable
fun SubscribeScreen(back: () -> Unit) {
    val vm = hiltViewModel<SubscribeViewModel>()
    LaunchedEffect(vm) {
        vm.loadState()
    }

    val state by vm.state.collectAsState()

    ShortXTheme {
        val appName = LocalAppName.current
        val googleIntroDialogState = rememberTipDialogState(
            title = LocalI18N.current["Google play"],
            tip = LocalI18N.current["ui.subscription.subscribe.verify.play.intro", mapOf(
                "app_name" to appName
            )]
        )
        TipDialog(state = googleIntroDialogState)

        NiaGradientBackground(modifier = Modifier.fillMaxSize()) {
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
                        actions = {
                            AnimatedVisibility(visible = !state.isSubscribed) {
                                IconButton(onClick = {
                                    googleIntroDialogState.show()
                                }) {
                                    RemixIcon(remixName = Remix.Logos.google_play_fill)
                                }
                            }
                        }
                    )
                }) { contentPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(contentPadding)
                ) {
                    if (state.isSubscribed) {
                        SubscribedUi(vm)
                    } else {
                        UnSubscribedUi(vm)
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscribedUi(vm: SubscribeViewModel) {
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
                SectionSpacer()
                SectionSpacer()
                SectionSpacer()
                when (val remainingState = state.remainingState) {
                    is UiState.Loaded -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MediumTitle(text = LocalI18N.current["ui.subscription.subscribe.validation.left"])
                            SmallSpacer()
                            Text(remainingState.data)
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingAnimationDot()
                        }
                    }
                }
            }

            SubscriptionSource.GooglePlay -> {
                LottieLoaderSuccessView()
            }

            null -> {}
        }
    }
}


@Composable
private fun UnSubscribedUi(vm: SubscribeViewModel) {
    LaunchedEffect(vm) {
        vm.loadSubConfig()
    }
    val state by vm.state.collectAsState()
    when (val subState = state.subscriptionConfigState) {
        is UiState.Loaded -> {
            SubscribeIntroUi(subState.data)
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

@Composable
private fun SubscribeIntroUi(config: SubscriptionConfig2) {
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
                        Toast.makeText(context, "\uD83E\uDD70", Toast.LENGTH_LONG).show()
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

        LargeSpacer()
        LargeTitle(text = i18N["ui.subscription.subscribe.flav"])
        MediumSpacer()
        config.flavors.forEach {
            ShortXCard {
                ListItem(
                    modifier = Modifier.heightIn(min = 72.dp),
                    titleLines = listOf(it.text, it.description),
                    icon = {
                        RemixIcon(remixName = Remix.Finance.vip_crown_line)
                    })
            }
            StandardSpacer()
        }
        LargeSpacer()

        val textColor = LocalContentColor.current.takeOrElse {
            MaterialTheme.colorScheme.onPrimaryContainer
        }
        val appName = LocalAppName.current
        MarkdownText(
            markdown = i18N["ui.subscription.subscribe.how.to.page", mapOf(
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

        Fabs(vm)
    }
}

@Composable
private fun Fabs(
    vm: SubscribeViewModel
) {
    val context = LocalContext.current
    val i18N = LocalI18N.current

    val verifyCodeInputState = rememberTextInputState(
        title = i18N["ui.subscription.subscribe.verify.code"],
        message = null,
        onSelected = {
            vm.verify(it.trim().replace(System.lineSeparator(), ""))
        }
    )
    TextInputDialog(state = verifyCodeInputState)

    val wechatPayQRDialogState = rememberCommonDialogState()
    ImageDialog(wechatPayQRDialogState, R.drawable.ui_pay_qr_wechat)

    val aliPayQRDialogState = rememberCommonDialogState()
    ImageDialog(aliPayQRDialogState, R.drawable.ui_pay_qr_alipay)

    val paypalDialogState = rememberTipDialogState(
        title = "Paypal",
        tip = "https://www.paypal.me/tornaco"
    )
    TipDialog(state = paypalDialogState)

    val payMenuDialogState =
        rememberMenuDialogState<String>(title = LocalI18N.current["ui.subscription.pay"],
            menuItems = listOf(
                MenuDialogItem(
                    id = "wechat",
                    title = "Wechat",
                    iconName = Remix.Logos.wechat_pay_fill
                ),
                MenuDialogItem(
                    id = "alipay",
                    title = "Ali Pay",
                    iconName = Remix.Logos.alipay_fill
                ),
                MenuDialogItem(
                    id = "paypal",
                    title = "Paypal",
                    iconName = Remix.Logos.paypal_fill
                )
            ),
            onItemSelected = { data, id ->
                when (id) {
                    "wechat" -> {
                        wechatPayQRDialogState.show()
                    }

                    "alipay" -> {
                        aliPayQRDialogState.show()
                    }

                    "paypal" -> {
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
                    }
                }
            })
    MenuDialog(state = payMenuDialogState)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 64.dp),
        horizontalAlignment = Alignment.End
    ) {

        ExtendedFloatingActionButton(
            containerColor = MaterialTheme.colorScheme.primary,
            onClick = {
                payMenuDialogState.show()
            },
            text = {
                Text(text = LocalI18N.current["ui.subscription.pay"], color = Color.White)
            },
            icon = {
                RemixIcon(
                    tint = Color.White,
                    remixName = Remix.Finance.shopping_cart_2_fill
                )
            })
        StandardSpacer()

        ExtendedFloatingActionButton(
            containerColor = VipColorGold,
            onClick = { verifyCodeInputState.show() },
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
            containerColor = GoogleBlue,
            onClick = { LVLCheckActivity.start(context) },
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