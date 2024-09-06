package tornaco.apps.shortx.ui.main

import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.main.model.ToggleUM

val DirectActionUM.uniqueUIKey get() = "$id-$updateTime-${hashCode()}"
val RuleUM.uniqueUIKey get() = "$id-$updateTime-${hashCode()}"
val ToggleUM.uniqueUIKey get() = "$id-$updateTime-${hashCode()}"