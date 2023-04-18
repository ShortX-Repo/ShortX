package tornaco.apps.shortx.core.proto

import com.google.protobuf.Message
import com.google.protobuf.MessageOrBuilder
import com.google.protobuf.util.JsonFormat
import tornaco.apps.shortx.core.proto.action.NoAction
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.proto.gv.GlobalVarList
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.settings.DanmuUISettings
import tornaco.apps.shortx.core.util.Logger

private val logger = Logger("JsonConverter")

private val typeRegistry = JsonFormat.TypeRegistry.newBuilder()
    .add(DirectAction.getDescriptor())
    .add(Rule.getDescriptor())
    .add(NoAction.getDescriptor())
    .add(AppPkg.getDescriptor())
    .add(MatchMVEL.getDescriptor())
    .add(KeyEvent.getDescriptor())
    .add(GlobalVarList.getDescriptor())
    .add(PkgSet.getDescriptor())
    .add(DanmuUISettings.getDescriptor())
    .build()

private val parser by lazy {
    JsonFormat.parser().usingTypeRegistry(typeRegistry);
}

private val printer by lazy {
    JsonFormat.printer().usingTypeRegistry(typeRegistry)
}

fun MessageOrBuilder.toJson(): String {
    return kotlin.runCatching {
        printer.print(this)
    }.getOrElse {
        logger.e(it, "toJson")
        "{}"
    }
}

fun Message.Builder.toProto(json: String?): Message {
    parser.merge(json, this)
    return this.build()
}