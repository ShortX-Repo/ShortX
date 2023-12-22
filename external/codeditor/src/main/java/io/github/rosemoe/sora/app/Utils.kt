package io.github.rosemoe.sora.app

import android.content.Context
import android.content.res.Configuration
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.schemes.SchemeDarcula
import io.github.rosemoe.sora.widget.schemes.SchemeGitHub

fun switchThemeIfRequired(context: Context, editor: CodeEditor) {
    if ((context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
        editor.colorScheme = SchemeDarcula()
    } else {
        editor.colorScheme = SchemeGitHub()
    }
    editor.invalidate()
}