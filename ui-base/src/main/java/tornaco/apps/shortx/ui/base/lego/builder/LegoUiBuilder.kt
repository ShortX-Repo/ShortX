package tornaco.apps.shortx.ui.base.lego.builder

import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.theme.BluePrintBgColor
import tornaco.apps.shortx.ui.theme.ShortXTheme

data class LegoUiBuilderState(
    val pageTitle: String,
    val childes: List<ProtoAny>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegoUiBuilderScreen() {
    BottomSheetScaffold(
        sheetContent = {},
        topBar = {
            TopAppBar(title = {
                Text("Build UI")
            }, actions = {
                IconButton(onClick = {

                }) {
                    RemixIcon(Remix.System.add_line)
                }
            })
        },
        containerColor = BluePrintBgColor
    ) {
        GridPage {

        }
    }
}

@Preview
@Composable
private fun LegoUiBuilderScreenPreview() {
    ShortXTheme {
        LegoUiBuilderScreen()
    }
}