package tornaco.apps.shortx

fun String.trimShellRes() = trimIndent().trim().replace(System.lineSeparator(), "")