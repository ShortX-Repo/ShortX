package tornaco.apps.shortx.core.proto

import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny

val Rule.factsAndQuitFacts
    get() = this.factsList + if (this.quit.isEnabled) {
        this.quit.factsList
    } else {
        emptyList()
    }

val DirectAction.quitFacts: List<ProtoAny>
    get() = if (this.quit.isEnabled) {
        this.quit.factsList
    } else {
        emptyList()
    }