package tornaco.apps.shortx.core.code

import tornaco.apps.shortx.core.proto.common.CodeLibraryItem
import tornaco.apps.shortx.core.proto.common.CodeLibraryQuery
import tornaco.apps.shortx.core.proto.common.CodeLibrarySortBy
import tornaco.apps.shortx.core.proto.common.CodeType
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper

// Extension functions for converting between protobuf and ByteArrayWrapper

// CodeLibraryItem conversions
fun CodeLibraryItem.toByteArrayWrapper(): ByteArrayWrapper {
    return ByteArrayWrapper(this.toByteArray())
}

fun ByteArrayWrapper.toCodeLibraryItem(): CodeLibraryItem {
    return CodeLibraryItem.parseFrom(this.byteData)
}

// CodeLibraryQuery conversions
fun CodeLibraryQuery.toByteArrayWrapper(): ByteArrayWrapper {
    return ByteArrayWrapper(this.toByteArray())
}

fun ByteArrayWrapper.toCodeLibraryQuery(): CodeLibraryQuery {
    return CodeLibraryQuery.parseFrom(this.byteData)
}

// Helper functions for creating protobuf objects
object CodeLibraryHelper {

    fun createCodeLibraryItem(
        id: String = "",
        name: String = "",
        description: String = "",
        type: CodeType = CodeType.CodeType_MVEL,
        content: String = "",
        createdAt: Long = System.currentTimeMillis(),
        updatedAt: Long = System.currentTimeMillis(),
        tags: List<String> = emptyList()
    ): CodeLibraryItem {
        return CodeLibraryItem.newBuilder()
            .setId(id)
            .setName(name)
            .setDescription(description)
            .setType(type)
            .setContent(content)
            .setCreatedAt(createdAt)
            .setUpdatedAt(updatedAt)
            .addAllTags(tags)
            .build()
    }

    fun createCodeLibraryQuery(
        keyword: String = "",
        type: CodeType? = null,
        tags: List<String> = emptyList(),
        sortBy: CodeLibrarySortBy = CodeLibrarySortBy.CodeLibrarySortBy_NAME_ASC
    ): CodeLibraryQuery {
        val builder = CodeLibraryQuery.newBuilder()
            .setKeyword(keyword)
            .addAllTags(tags)
            .setSortBy(sortBy)

        type?.let { builder.setType(it) }

        return builder.build()
    }
}
