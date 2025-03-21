package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object AppHasTaskByPkg : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.AppHasTaskByPkg::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.AppHasTaskByPkg::class.java
            val requiredPkgList: List<AppPkg> = data.pkgAndUsersList.toRealApps(param)
            AppHasTask.evaluate(shortXContext, context, requiredPkgList, data.op, data.isInvert)
        }
    }
}