package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object AppHasAudioFocus : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.AppHasAudioFocus::class.java

    override fun evaluate(param: Cond.CondParam): Boolean {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.AppHasAudioFocus::class.java

            val requiredPkgList = if (data.pkgSetsList.isNotEmpty()) {
                data.pkgSetsList.flatMap {
                    shortXContext.pkgSet.getPkgSetByLabel(it)?.appPkgsList ?: emptyList()
                }
            } else {
                data.pkgsList
            }

            val res = requiredPkgList.all {
                with(shortXContext.audio) { it.hasFocus() }
            }
            if (data.isInvert) {
                !res
            } else {
                res
            }
        }
    }
}