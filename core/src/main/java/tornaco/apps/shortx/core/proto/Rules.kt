package tornaco.apps.shortx.core.proto

import tornaco.apps.shortx.core.proto.rule.Rule

val Rule.factsAndQuitFacts get() = this.factsList + this.quit.factsList