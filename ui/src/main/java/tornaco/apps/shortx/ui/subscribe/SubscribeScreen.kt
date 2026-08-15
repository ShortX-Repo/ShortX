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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.i18n.I18N_R
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.R
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LargeTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LoadingAnimationDot
import tornaco.apps.shortx.ui.base.LocalAppName
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.NiaGradientBackground
import tornaco.apps.shortx.ui.base.ProgressDialog
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXTextButton
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TipDialog
import tornaco.apps.shortx.ui.base.UiState
import tornaco.apps.shortx.ui.base.fontFamilyProductSansBold
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
            tip = LocalI18N.current[I18N_R.UI_SUBSCRIPTION_SUBSCRIBE_VERIFY_PLAY_INTRO, mapOf(
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
                                text = LocalI18N.current[I18N_R.UI_SUBSCRIPTION_INTRO_TITLE]
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
                            MediumTitle(text = LocalI18N.current[I18N_R.UI_SUBSCRIPTION_SUBSCRIBE_VALIDATION_LEFT])
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
            title = i18N[I18N_R.UI_SUBSCRIPTION_CODE_VERIFY_TITLE],
            message = i18N[I18N_R.UI_SUBSCRIPTION_CODE_VERIFY_MESSAGE]
        )
        ProgressDialog(state = progressDialog)

        val context = LocalContext.current
        val failMsg = i18N[I18N_R.UI_SUBSCRIPTION_CODE_VERIFY_FAIL]
        val failNetworkMsg =
            i18N[I18N_R.UI_SUBSCRIPTION_CODE_VERIFY_FAIL_NETWORK]

        val qq = config.qq
        val email = config.email

        // 点击复制客服 QQ 号（仅超时未回复时咨询）
        val copyQq: () -> Unit = {
            ClipboardUtils.copyToClipboard(context, "qq", qq)
            Toast.makeText(
                context,
                i18N[I18N_R.UI_COMMON_COPIED_TO_CLIPBOARD],
                Toast.LENGTH_LONG
            ).show()
        }

        // 点击复制邮箱
        val copyEmail: () -> Unit = {
            ClipboardUtils.copyToClipboard(context, "email", email)
            Toast.makeText(
                context,
                i18N[I18N_R.UI_COMMON_COPIED_TO_CLIPBOARD],
                Toast.LENGTH_LONG
            ).show()
        }

        val vm = hiltViewModel<SubscribeViewModel>()
        LaunchedEffect(vm) {
            vm.codeVerifyEvent.collectLatest {
                when (it) {
                    CodeVerifyEvent.Verifying -> {
                        progressDialog.show()
                    }

                    CodeVerifyEvent.Success -> {
                        progressDialog.dismiss()
                        Toast.makeText(context, "🥰", Toast.LENGTH_LONG).show()
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
        LargeTitle(text = i18N[I18N_R.UI_SUBSCRIPTION_SUBSCRIBE_FLAV])
        MediumSpacer()
        config.flavors.forEach {
            ShortXCard {
                ListItem(
                    title = it.text,
                    text1 = it.description,
                    icon = {
                        RemixIcon(remixName = Remix.Finance.vip_crown_line)
                    },
                    overflow = {
                        Row {
                            Text(
                                text = "${it.priceCNY.toInt()}",
                                color = Color.Red,
                                fontWeight = FontWeight.W600,
                                fontSize = 24.sp,
                                fontFamily = fontFamilyProductSansBold(),
                                modifier = Modifier
                                    .alignByBaseline()
                            )
                            Text(
                                text = "CNY",
                                color = Color.Red,
                                fontWeight = FontWeight.W600,
                                fontSize = 10.sp,
                                fontFamily = fontFamilyProductSansBold(),
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .alignByBaseline()
                            )
                        }
                    },
                    onClick = {})
            }
            StandardSpacer()
        }
        LargeSpacer()

        MediumTitle(text = i18N[I18N_R.UI_SUBSCRIPTION_PURCHASE_PROCESS])

        // 邮箱获取激活码（主方式），QQ 仅超时未回复时咨询
        EmailMethodCard(
            email = email,
            qq = qq,
            onCopyEmail = { copyEmail() },
            onCopyQq = { copyQq() }
        )
        StandardSpacer()

        LargeSpacer()

        Fabs(vm, config, copyEmail)
    }
}

@Composable
private fun Fabs(
    vm: SubscribeViewModel,
    config: SubscriptionConfig2,
    copyEmail: () -> Unit
) {
    val context = LocalContext.current
    val i18N = LocalI18N.current
    val email = config.email

    val verifyCodeInputState = rememberTextInputState(
        title = i18N[I18N_R.UI_SUBSCRIPTION_SUBSCRIBE_VERIFY_CODE],
        message = null,
        onSelected = {
            vm.verify(it.trim().replace(System.lineSeparator(), ""))
        }
    )
    TextInputDialog(state = verifyCodeInputState)

    val wechatPayQRDialogState = rememberCommonDialogState()
    PayQrDialog(
        state = wechatPayQRDialogState,
        qrResId = R.drawable.ui_pay_qr_wechat,
        email = email,
        copyEmail = copyEmail
    )

    val aliPayQRDialogState = rememberCommonDialogState()
    PayQrDialog(
        state = aliPayQRDialogState,
        qrResId = R.drawable.ui_pay_qr_alipay,
        email = email,
        copyEmail = copyEmail
    )

    val paypalUrl = "https://www.paypal.me/tornaco"
    val paypalDialogState = rememberTipDialogState(
        title = "PayPal",
        tip = "$paypalUrl\n\n" + i18N[I18N_R.UI_SUBSCRIPTION_PAY_AFTER_SEND_TO_EMAIL, mapOf("email" to email)]
    )
    TipDialog(state = paypalDialogState)

    val payMenuDialogState =
        rememberMenuDialogState<String>(
            title = i18N[I18N_R.UI_SUBSCRIPTION_PAY],
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
            onItemSelected = { _, id ->
                when (id) {
                    "wechat" -> {
                        wechatPayQRDialogState.show()
                    }

                    "alipay" -> {
                        aliPayQRDialogState.show()
                    }

                    "paypal" -> {
                        ClipboardUtils.copyToClipboard(context, "paypal", paypalUrl)
                        Toast.makeText(
                            context,
                            i18N[I18N_R.UI_COMMON_COPIED_TO_CLIPBOARD],
                            Toast.LENGTH_LONG
                        ).show()
                        paypalDialogState.show()
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
                Text(text = i18N[I18N_R.UI_SUBSCRIPTION_PAY_QR], color = Color.White)
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
                    text = i18N[I18N_R.UI_SUBSCRIPTION_SUBSCRIBE_VERIFY_CODE],
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
                    text = i18N[I18N_R.UI_SUBSCRIPTION_SUBSCRIBE_VERIFY_PLAY],
                    color = Color.White
                )
            },
            icon = {
                RemixIcon(tint = Color.White, remixName = Remix.Logos.google_play_fill)
            })
    }
}

@Composable
private fun PayQrDialog(
    state: CommonDialogState,
    qrResId: Int,
    email: String,
    copyEmail: () -> Unit
) {
    if (state.isShowing) {
        val i18N = LocalI18N.current
        ShortXDialog(
            onDismissRequest = {
                state.dismiss()
            },
            title = {}
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = qrResId),
                contentDescription = ""
            )
            SmallSpacer()
            Text(
                text = i18N[I18N_R.UI_SUBSCRIPTION_PAY_AFTER_SEND_TO_EMAIL, mapOf("email" to email)],
                style = MaterialTheme.typography.bodySmall,
                color = LocalContentColor.current
            )
            ShortXTextButton(onClick = copyEmail) {
                Text(text = i18N[I18N_R.UI_SUBSCRIPTION_COPY_EMAIL])
            }
        }
    }
}

@Composable
private fun EmailMethodCard(
    email: String,
    qq: String,
    onCopyEmail: () -> Unit,
    onCopyQq: () -> Unit
) {
    val i18N = LocalI18N.current
    ShortXCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题行：邮箱图标 + 标题
            Row(verticalAlignment = Alignment.CenterVertically) {
                RemixIcon(
                    remixName = Remix.Business.mail_fill,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = i18N[I18N_R.UI_SUBSCRIPTION_EMAIL_METHOD_TITLE],
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 邮箱地址（醒目的可复制框）
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .clickable(onClick = onCopyEmail)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W600),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    RemixIcon(
                        remixName = Remix.Document.file_copy_line,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 编号步骤
            EmailStep(
                number = "1",
                text = i18N[I18N_R.UI_SUBSCRIPTION_EMAIL_STEP_1]
            )
            EmailStep(
                number = "2",
                text = i18N[I18N_R.UI_SUBSCRIPTION_EMAIL_STEP_2]
            )
            EmailStep(
                number = "3",
                text = i18N[I18N_R.UI_SUBSCRIPTION_EMAIL_STEP_3]
            )

            Spacer(modifier = Modifier.height(12.dp))

            // QQ 咨询行（超时未回复时）
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onCopyQq),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RemixIcon(
                    remixName = Remix.Logos.qq_fill,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = i18N[I18N_R.UI_SUBSCRIPTION_EMAIL_QQ_CONSULT, mapOf("qq" to qq)],
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                RemixIcon(
                    remixName = Remix.Document.file_copy_line,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

        }
    }
}

@Composable
private fun EmailStep(
    number: String,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
        )
    }
}
