package tornaco.apps.shortx.core.proto

import tornaco.apps.shortx.core.proto.version.ShortXProtoVersion

val shortXProtoVersion: Int
    get() = ShortXProtoVersion.entries.filterNot { it == ShortXProtoVersion.UNRECOGNIZED }
        .maxOfOrNull { it.number } ?: 0