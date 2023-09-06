package tornaco.apps.shortx.ui.main.da

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.ShortXBottomSheetState
import tornaco.apps.shortx.ui.main.model.DirectActionUM

@Composable
fun getLocalDASetItemHandler(
    bottomSheetState: ShortXBottomSheetState,
    vm: DAListViewModel
): DASetItemHandler {
    val props = LocalShortXProps.current
    val handler = object : DASetItemHandler {
        override fun dismiss(da: DirectActionUM) {
            vm.dismissDASet(da, props.firstOrNull())
        }

        override fun rename(da: DirectActionUM, newName: String) {
            vm.rename(da, newName, props.firstOrNull())
        }

        override fun click(da: DirectActionUM) {
            bottomSheetState.show(da.id)
        }

        override fun share(da: DirectActionUM) {
        }
    }

    return handler
}