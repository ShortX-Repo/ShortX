package tornaco.apps.shortx.ui.addrule.fact

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.selector.GVSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberGVSelectorState
import tornaco.apps.shortx.ui.settings.TileSettingsActivity

@Composable
fun miscFactSelectHandler(addFact: (Fact) -> Unit): (FactSelectSource) -> Boolean {
    val context = LocalContext.current

    val qsTileSelectState = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                val tileNum = it.data?.getIntExtra("tile", 0) ?: 0
                addFact(Fact.OnQSTileClick(tile = StatusBarTile.forNumber(tileNum)))
            }
        }
    )

    val gvSelectorDialogState = rememberGVSelectorState(onSelected = { varName ->
        addFact(Fact.GlobalVarChanged(gvId = varName))
    })
    GVSelectorDialog(state = gvSelectorDialogState)

    return { source ->
        when (source.clazz) {
            Fact.AnyFact::class -> {
                addFact(Fact.AnyFact())
                true
            }

            Fact.OnQSTileClick::class -> {
                qsTileSelectState.launch(TileSettingsActivity.intent(context, true))
                true
            }

            Fact.GlobalVarChanged::class -> {
                gvSelectorDialogState.show(source.getFactAs<Fact.GlobalVarChanged>()?.gvId)
                true
            }

            else -> false
        }
    }
}
