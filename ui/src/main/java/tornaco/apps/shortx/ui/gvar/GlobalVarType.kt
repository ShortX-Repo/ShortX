package tornaco.apps.shortx.ui.gvar

import tornaco.apps.shortx.core.proto.gv.*
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.pack_

enum class GlobalVarType {
    Int,
    IntList,
    Bool,
    BoolList,
    String,
    StringList,
}

fun GlobalVarType.label(): String {
    return when (this) {
        GlobalVarType.Int -> "int"
        GlobalVarType.IntList -> "int list"
        GlobalVarType.Bool -> "bool"
        GlobalVarType.BoolList -> "bool list"
        GlobalVarType.String -> "string"
        GlobalVarType.StringList -> "string list"
    }
}

fun GlobalVarType.toProtoType(): ProtoAny = when (this) {
    GlobalVarType.Int -> Int64Var.getDefaultInstance().pack_()
    GlobalVarType.IntList -> Int64ListVar.getDefaultInstance().pack_()
    GlobalVarType.Bool -> BoolVar.getDefaultInstance().pack_()
    GlobalVarType.BoolList -> BoolListVar.getDefaultInstance().pack_()
    GlobalVarType.String -> StringVar.getDefaultInstance().pack_()
    GlobalVarType.StringList -> StringListVar.getDefaultInstance().pack_()
}

fun ProtoAny.toType(): GlobalVarType? {
    return when {
        this is_ (Int64Var::class.java) -> {
            GlobalVarType.Int
        }
        this is_ (Int64ListVar::class.java) -> {
            GlobalVarType.IntList
        }
        this is_ (BoolVar::class.java) -> {
            GlobalVarType.Bool
        }
        this is_ (BoolListVar::class.java) -> {
            GlobalVarType.BoolList
        }
        this is_ (StringVar::class.java) -> {
            GlobalVarType.String
        }
        this is_ (StringListVar::class.java) -> {
            GlobalVarType.StringList
        }
        else -> {
            null
        }
    }
}