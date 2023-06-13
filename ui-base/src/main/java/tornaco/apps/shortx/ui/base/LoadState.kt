package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

sealed interface LoadState<out T> {
    data class Success<T>(val data: T) : LoadState<T>
    object Loading : LoadState<Nothing>
    data class Error(val err: Throwable? = null) : LoadState<Nothing>

    fun isLoading() = this is Loading
    fun isSuccess() = this is Success

    val <T> LoadState<T>.data: T?
        get() = (this as? Success)?.data
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            strokeWidth = 6.dp
        )
    }
}

@Composable
fun LoadError(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp)
                .offset(y = (-80).dp)
                .fillMaxWidth(),
            painter = painterResource(
                id = github.tornaco.shortx.ui.base.R.drawable.ui_load_error
            ),
            contentDescription = null,
        )
    }
}


@Composable
fun EmptyList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp)
                .offset(y = (-80).dp)
                .fillMaxWidth(),
            painter = painterResource(
                id = github.tornaco.shortx.ui.base.R.drawable.ui_empty_list
            ),
            contentDescription = null,
        )
    }
}