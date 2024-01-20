package io.github.rosemoe.sora.app

import android.graphics.Typeface
import androidx.core.view.isVisible
import io.github.rosemoe.sora.app.databinding.CodeEditorComposeBinding
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.event.EditorKeyEvent
import io.github.rosemoe.sora.event.KeyBindingEvent
import io.github.rosemoe.sora.event.PublishSearchResultEvent
import io.github.rosemoe.sora.event.SelectionChangeEvent
import io.github.rosemoe.sora.event.SideIconClickEvent
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import io.github.rosemoe.sora.text.LineSeparator
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.getComponent
import io.github.rosemoe.sora.widget.subscribeEvent
import org.eclipse.tm4e.core.registry.IThemeSource


class CEComposeBinding(
    private val binding: CodeEditorComposeBinding,
    private val showToolbar: Boolean,
    private val initialText: String,
    private val onContentChange: (String) -> Unit = {}
) {
    private val context get() = binding.root.context

    fun onCreate() {
        val inputView = binding.symbolInput
        inputView.bindEditor(binding.editor)
        val typeface =
            Typeface.createFromAsset(
                context.assets,
                "fonts/google/jetbrains/JetBrainsMonoRegular.ttf"
            )
        inputView.addSymbols(
            arrayOf(
                "->",
                "{",
                "}",
                "(",
                ")",
                ",",
                ".",
                ";",
                "\"",
                "?",
                "+",
                "-",
                "*",
                "/"
            ), arrayOf("\t", "{}", "}", "(", ")", ",", ".", ";", "\"", "?", "+", "-", "*", "/")
        )
        inputView.forEachButton {
            it.typeface = typeface
        }
        binding.editor.apply {
            typefaceText = typeface
            props.stickyScroll = true
            isWordwrap = true
            setLineSpacing(2f, 1.1f)
            setTextSize(14f)
            nonPrintablePaintingFlags =
                CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or CodeEditor.FLAG_DRAW_LINE_SEPARATOR or CodeEditor.FLAG_DRAW_WHITESPACE_IN_SELECTION
            // Update display dynamically
            subscribeEvent<SelectionChangeEvent> { _, _ -> updatePositionText() }
            subscribeEvent<PublishSearchResultEvent> { _, _ -> updatePositionText() }
            subscribeEvent<ContentChangeEvent> { event, _ ->
                onContentChange(event.editor.text.toString())
                postDelayedInLifecycle(
                    ::updateBtnState,
                    50
                )
            }
            subscribeEvent<SideIconClickEvent> { _, _ ->
            }

            subscribeEvent<KeyBindingEvent> { event, _ ->
                if (event.eventType != EditorKeyEvent.Type.DOWN) {
                    return@subscribeEvent
                }
            }

            getComponent<EditorAutoCompletion>()
                .setEnabledAnimation(true)
        }

        binding.btnUndo.setOnClickListener {
            if (binding.editor.canUndo()) {
                binding.editor.undo()
            }
        }
        binding.btnRedo.setOnClickListener {
            if (binding.editor.canRedo()) {
                binding.editor.redo()
            }
        }

        binding.editorTools.isVisible = showToolbar


        loadDefaultThemes()
        loadDefaultLanguages()

        ensureTextmateTheme()

        val editor = binding.editor
        val language = TextMateLanguage.create(
            "source.java", true
        )

        editor.setEditorLanguage(language)

        // Load file here.
        editor.setText(initialText)

        updatePositionText()

        switchThemeIfRequired(context, binding.editor)
    }

    private fun updateBtnState() {
        binding.btnUndo.isEnabled = binding.editor.canUndo()
        binding.btnRedo.isEnabled = binding.editor.canRedo()
    }

    private /*suspend*/ fun loadDefaultThemes() /*= withContext(Dispatchers.IO)*/ {
        //add assets file provider
        FileProviderRegistry.getInstance().addFileProvider(
            AssetsFileResolver(
                context.assets
            )
        )

        val themes = arrayOf("darcula", "abyss", "quietlight", "solarized_drak")
        val themeRegistry = ThemeRegistry.getInstance()
        themes.forEach { name ->
            val path = "textmate/$name.json"
            themeRegistry.loadTheme(
                ThemeModel(
                    IThemeSource.fromInputStream(
                        FileProviderRegistry.getInstance().tryGetInputStream(path), path, null
                    ), name
                ).apply {
                    if (name != "quietlight") {
                        isDark = true
                    }
                }
            )
        }

        themeRegistry.setTheme("quietlight")
    }

    private /*suspend*/ fun loadDefaultLanguages() /*= withContext(Dispatchers.Main)*/ {
        GrammarRegistry.getInstance().loadGrammars("textmate/languages.json")
    }

    private fun ensureTextmateTheme() {
        val editor = binding.editor
        var editorColorScheme = editor.colorScheme
        if (editorColorScheme !is TextMateColorScheme) {
            editorColorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance())
            editor.colorScheme = editorColorScheme
        }
    }

    private fun updatePositionText() {
        val cursor = binding.editor.cursor
        var text =
            (1 + cursor.leftLine).toString() + ":" + cursor.leftColumn + ";" + cursor.left + " "
        text += if (cursor.isSelected) {
            "(" + (cursor.right - cursor.left) + " chars)"
        } else {
            val content = binding.editor.text
            if (content.getColumnCount(cursor.leftLine) == cursor.leftColumn) {
                "(<" + content.getLine(cursor.leftLine).lineSeparator.let {
                    if (it == LineSeparator.NONE) {
                        "EOF"
                    } else {
                        it.name
                    }
                } + ">)"
            } else {
                val char = binding.editor.text.charAt(
                    cursor.leftLine,
                    cursor.leftColumn
                )
                if (char.isLowSurrogate() && cursor.leftColumn > 0) {
                    "(" + String(
                        charArrayOf(
                            binding.editor.text.charAt(
                                cursor.leftLine,
                                cursor.leftColumn - 1
                            ), char
                        )
                    ) + ")"
                } else if (char.isHighSurrogate() && cursor.leftColumn + 1 < binding.editor.text.getColumnCount(
                        cursor.leftLine
                    )
                ) {
                    "(" + String(
                        charArrayOf(
                            char, binding.editor.text.charAt(
                                cursor.leftLine,
                                cursor.leftColumn + 1
                            )
                        )
                    ) + ")"
                } else {
                    "(" + escapeIfNecessary(
                        binding.editor.text.charAt(
                            cursor.leftLine,
                            cursor.leftColumn
                        )
                    ) + ")"
                }
            }
        }
        val searcher = binding.editor.searcher
        if (searcher.hasQuery()) {
            val idx = searcher.currentMatchedPositionIndex
            val count = searcher.matchedPositionCount
            val matchText = if (count == 0) {
                "no match"
            } else if (count == 1) {
                "1 match"
            } else {
                "$count matches"
            }
            if (idx == -1) {
                text += "($matchText)"
            } else {
                text += "(${idx + 1} of $matchText)"
            }
        }
        binding.positionDisplay.text = text
    }

    private fun escapeIfNecessary(c: Char): String {
        return when (c) {
            '\n' -> "\\n"
            '\t' -> "\\t"
            '\r' -> "\\r"
            ' ' -> "<ws>"
            else -> c.toString()
        }
    }


    fun onDestroy() {
        binding.editor.release()
    }
}