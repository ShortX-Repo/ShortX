package tornaco.apps.shortx.ui.addrule.action

import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.condition.getGlobalVarRefs
import tornaco.apps.shortx.core.i18n.I18N_R

class ActionErrorChecker {
    private val logger = Logger("ActionErrorChecker")

    fun generateWarningMessages(
        actions: List<Action>,
        allActionsOnRule: List<Action>,
        shortXManager: ShortXManager,
        i18N: I18N
    ): List<String> {
        val gvWarnings = emptyList<String>()

        val pkgSetWarning = getPkgSetRefs(actions, shortXManager)
            .distinct()
            .apply { logger.d("getPkgSetRefs: $this") }
            .filter { shortXManager.getPkgSetByLabel(it) == null }
            .mapIndexed { index, it ->
                "${gvWarnings.size + index + 1}. " + i18N[I18N_R.UI_CONDITION_WARNING_MESSAGE_PKGSET_MISSING, mapOf(
                    "label" to it
                )]
            }

        return gvWarnings + pkgSetWarning
    }


    private fun getWillBeCreatedGlobalVars(actions: List<Action>): List<String> {
        return actions.filterIsInstance(Action.CreateGV::class.java).map { it.globalVar.name } +
            actions.filterIsInstance(Action.WriteGV::class.java)
                .filter { it.autoCreateIfMissing }
                .map { it.varName }
    }

    private fun getGlobalVarRefs(
        actions: List<Action>,
        shortXManager: ShortXManager
    ): List<String> {
        val writeGV =
            actions.filterIsInstance(Action.WriteGV::class.java).map { it.varName }

        val ifElseCondition = actions.filterIsInstance(Action.IfThenElse::class.java).map {
            it.ifConditions.getGlobalVarRefs()
        }.flatten()

        val ifElseAction = actions.filterIsInstance(Action.IfThenElse::class.java).map {
            getGlobalVarRefs(it.ifActions, shortXManager) + getGlobalVarRefs(
                it.elseActions,
                shortXManager
            )
        }.flatten()

        val showDialogAction = actions.filterIsInstance(Action.ShowAlertDialog::class.java).map {
            getGlobalVarRefs(
                it.positiveActions,
                shortXManager
            ) + getGlobalVarRefs(
                it.negativeActions,
                shortXManager
            ) + getGlobalVarRefs(it.neutralActions, shortXManager)
        }.flatten()

        val showOverlayAction = actions.filterIsInstance<Action.ShowOverlay>().map { overlay ->
            overlay.settings.map { buttonSetting ->
                buttonSetting.actionsList.mapNotNull {
                    it.toAction(shortXManager)
                }
            }.map { getGlobalVarRefs(it, shortXManager) }.flatten()
        }.flatten()

        val showActionSidebarAction = actions.filterIsInstance<Action.ShowActionSidebar>().map { sidebar ->
            sidebar.buttons.map { button ->
                val tapActions = button.tapActionsList.mapNotNull { it.toAction(shortXManager) }
                val longPressActions = button.longPressActionsList.mapNotNull { it.toAction(shortXManager) }
                getGlobalVarRefs(tapActions + longPressActions, shortXManager)
            }.flatten()
        }.flatten()

        val fromDA = actions.filterIsInstance<Action.FromDA>().mapNotNull { fromDA ->
            shortXManager.getDirectActionById(fromDA.daId)?.let { directAction ->
                directAction.actionsList.mapNotNull { it.toAction(shortXManager) }
            }?.let { getGlobalVarRefs(it, shortXManager) }
        }.flatten()

        return writeGV + ifElseCondition + ifElseAction + showDialogAction + showOverlayAction + showActionSidebarAction + fromDA
    }

    private fun getPkgSetRefs(
        actions: List<Action>,
        shortXManager: ShortXManager
    ): List<String> {
        val StopApp: List<String> =
            actions.filterIsInstance(Action.StopApp::class.java)
                .map { stopApp -> stopApp.pkgSets.map { it.label } }
                .flatten()
        val StartAppProcess: List<String> =
            actions.filterIsInstance(Action.StartAppProcess::class.java)
                .map { ac -> ac.pkgSets.map { it.label } }
                .flatten()
        val EnableApp: List<String> =
            actions.filterIsInstance(Action.EnableApp::class.java)
                .map { ac -> ac.pkgSets.map { it.label } }
                .flatten()
        val DisableApp: List<String> =
            actions.filterIsInstance(Action.DisableApp::class.java)
                .map { ac -> ac.pkgSets.map { it.label } }
                .flatten()
        val SuspendApp: List<String> =
            actions.filterIsInstance(Action.SuspendApp::class.java)
                .map { ac -> ac.pkgSets.map { it.label } }
                .flatten()
        val UnSuspendApp: List<String> =
            actions.filterIsInstance(Action.UnSuspendApp::class.java)
                .map { ac -> ac.pkgSets.map { it.label } }
                .flatten()



        return StopApp + StartAppProcess + EnableApp + DisableApp + SuspendApp + UnSuspendApp
    }

}
