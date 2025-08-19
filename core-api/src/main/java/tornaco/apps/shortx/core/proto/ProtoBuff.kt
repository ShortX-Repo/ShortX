package tornaco.apps.shortx.core.proto

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import com.google.protobuf.TextFormat

fun Message.containsTypeUrl(typeUrl: String): Boolean {
    return TextFormat.printer().printToString(this).contains(typeUrl)
}

fun Message.containsAnyTypeUrl(typeUrl: List<String>): Boolean {
    return TextFormat.printer().printToString(this).let { messageString ->
        typeUrl.any { messageString.contains(it) }
    }
}

fun Message.printToString(): String {
    return TextFormat.printer().printToString(this)
}

fun Message.findTypeUrls(): List<String> {
    val typeUrls = mutableListOf<String>()
    findTypeUrlsRecursive(this, typeUrls)
    return typeUrls
}

private fun findTypeUrlsRecursive(message: Message, typeUrls: MutableList<String>) {
    val fields = message.allFields

    for ((field, value) in fields) {
        if (field.name == "type_url" && value is String) {
            typeUrls.add(value)
        }

        if (field.javaType == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            if (field.isRepeated) {
                val list = value as List<*>
                for (item in list) {
                    if (item is Message) {
                        findTypeUrlsRecursive(item, typeUrls)
                    }
                }
            } else {
                findTypeUrlsRecursive(value as Message, typeUrls)
            }
        }
    }
}

// Note: Should align with ProtoAny.getTypeUrl, it's private.
fun Message.getTypeUrl(
    typeUrlPrefix: String = "type.googleapis.com",
): String {
    return if (typeUrlPrefix.endsWith("/")
    ) typeUrlPrefix + descriptorForType.fullName
    else typeUrlPrefix + "/" + descriptorForType.fullName
}