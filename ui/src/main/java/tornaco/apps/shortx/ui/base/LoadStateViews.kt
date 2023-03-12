package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.ui.R

@Composable
fun LoadError(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp)
                .fillMaxWidth(),
            painter = painterResource(
                id = R.drawable.ui_load_error
            ),
            contentDescription = null,
        )
    }
}

@Composable
fun NetworkError(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp)
                .fillMaxWidth(),
            painter = painterResource(
                id = R.drawable.ui_network_error
            ),
            contentDescription = null,
        )
    }
}