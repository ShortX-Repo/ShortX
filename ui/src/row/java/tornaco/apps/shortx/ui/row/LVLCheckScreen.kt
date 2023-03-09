package tornaco.apps.shortx.ui.row

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.ui.R
import tornaco.apps.shortx.ui.row.util.isSkipping
import tornaco.apps.shortx.ui.theme.ShortXTheme

@Composable
fun LVLCheckScreen(toNav: () -> Unit) {
    val viewModel: LVLCheckViewModel = viewModel()
    val uiState = viewModel.uiState.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.check(context)
    }

    val canSkip = remember {
        viewModel.canSkip(context)
    }
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val scope = rememberCoroutineScope()

            MainContent(
                uiState = uiState.value,
                onRetry = {
                    viewModel.check(context)
                },
                onSkip = {
                    isSkipping = true
                    delayToNav(scope = scope, toNav = toNav)
                    viewModel.onSkip(context)
                },
                canSkip = canSkip,
                toNav = toNav
            )
        }
    }
}

@Composable
private fun MainContent(
    uiState: UiState,
    onRetry: () -> Unit,
    canSkip: Boolean,
    onSkip: () -> Unit,
    toNav: () -> Unit
) {
    when (uiState.checkState) {
        CheckState.CHECKING -> {
            LoadingContent()
        }
        CheckState.SUCCESS -> {
            SuccessContent(toNav)
        }
        else -> {
            FailContent(
                checkState = uiState.checkState,
                onRetry = onRetry,
                canSkip = canSkip,
                onSkip = onSkip
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieLoaderLoadingView()
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        )
        HeadlineText(R.string.lvl_checking)
    }
}

@Composable
private fun SuccessContent(toNav: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieLoaderSuccessView()
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
        )
        HeadlineText(R.string.lvl_check_ok)


        val scope = rememberCoroutineScope()
        delayToNav(scope, 3000, toNav)
    }
}

@Composable
private fun FailContent(
    checkState: CheckState,
    canSkip: Boolean,
    onSkip: () -> Unit,
    onRetry: () -> Unit
) {
    Box {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmojiFailView()
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            )

            when (checkState) {
                CheckState.FAIL_NETWORK -> {
                    HeadlineText(R.string.lvl_unlicensed_dialog_retry_body)
                }
                CheckState.FAIL_NOT_LICENSED -> {
                    HeadlineText(R.string.lvl_unlicensed_dialog_body)
                }
                CheckState.FAIL -> {
                    HeadlineText(R.string.lvl_check_not_ok)
                }
                else -> {
                }
            }

            TipsText(textRes = R.string.lvl_unlicensed_dialog_error_tips)
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 32.dp, bottom = 24.dp)
        ) {
            if (canSkip) {
                HighLightText(
                    modifier = Modifier.padding(top = 24.dp),
                    textRes = R.string.lvl_check_skip_for_now
                ) {
                    onSkip()
                }
            }

            HighLightText(
                modifier = Modifier.padding(top = 24.dp),
                textRes = R.string.lvl_checking_retry
            ) {
                onRetry()
            }
        }
    }

}

@Composable
private fun EmojiFailView() {
    LottieLoaderFailView()
}

@Composable
private fun HeadlineText(textRes: Int) {
    Text(
        modifier = Modifier.padding(16.dp),
        textAlign = TextAlign.Center,
        text = stringResource(textRes),
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
private fun HighLightText(modifier: Modifier, textRes: Int, onClick: () -> Unit) {
    TextButton(modifier = modifier, onClick = onClick) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(textRes),
        )
    }
}

@Composable
private fun TipsText(modifier: Modifier = Modifier, textRes: Int) {
    Text(
        modifier = modifier.padding(vertical = 12.dp, horizontal = 32.dp),
        textAlign = TextAlign.Start,
        text = stringResource(textRes),
        style = MaterialTheme.typography.bodySmall
    )
}

private fun delayToNav(scope: CoroutineScope, delay: Long = 0L, toNav: () -> Unit) {
    scope.launch {
        delay(delay)
        toNav()
    }
}