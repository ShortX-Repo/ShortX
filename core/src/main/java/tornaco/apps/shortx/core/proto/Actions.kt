package tornaco.apps.shortx.core.proto

import tornaco.apps.shortx.core.proto.action.IfThenElse
import tornaco.apps.shortx.core.proto.action.ShowAlertDialog
import tornaco.apps.shortx.core.proto.action.ShowOverlayButton
import tornaco.apps.shortx.core.proto.action.WhileLoop
import tornaco.apps.shortx.core.rule.ProtoAny

fun List<ProtoAny>.flatActions(): List<ProtoAny> {
    return filterIsInstance<WhileLoop>().flatMap { it.actionsList.flatActions() } +
            filterIsInstance<IfThenElse>().flatMap {
                it.ifActionsList.flatActions() + it.elseActionsList.flatActions()
            } +
            filterIsInstance<ShowOverlayButton>().map { button -> button.buttonSettingsList.map { it.actionsList.flatActions() } }
                .flatten()
                .flatten() +
            filterIsInstance<ShowAlertDialog>().flatMap { it.onPositiveList.flatActions() + it.onNegativeList.flatActions() + it.onNeutralList.flatActions() }
}