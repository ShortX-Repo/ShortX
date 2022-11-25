package tornaco.apps.shortx.core.rule

typealias   ProtoAny = com.google.protobuf.Any
typealias   ProtoMessage = com.google.protobuf.Message

fun <T : ProtoMessage> T.pack_(): ProtoAny {
    return ProtoAny.pack(this)
}

infix fun <T : ProtoMessage> ProtoAny.is_(clazz: Class<T>): Boolean {
    return this.`is`(clazz)
}

infix fun <T : ProtoMessage> ProtoAny.unpack_(clazz: Class<T>): T {
    return this.unpack(clazz)
}