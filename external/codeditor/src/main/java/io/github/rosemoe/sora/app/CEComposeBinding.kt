package io.github.rosemoe.sora.app

import android.graphics.Typeface
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.core.widget.doAfterTextChanged
import androidx.core.view.isVisible
import io.github.rosemoe.sora.app.R
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
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.text.LineSeparator
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.EditorSearcher.SearchOptions
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.getComponent
import io.github.rosemoe.sora.widget.subscribeEvent
import org.eclipse.tm4e.core.registry.IThemeSource
import java.util.regex.PatternSyntaxException


class CEComposeBinding(
    private val binding: CodeEditorComposeBinding,
    private val showToolbar: Boolean,
    private val initialText: String,
    private val languageScopeName: String? = null,
    private val autoCompleteEnabled: Boolean = false,
    private val showWordwrapInBottomBar: Boolean = true,
    private val showNonPrintableInBottomBar: Boolean = true,
    private val footerTitle: String? = null,
    private val onContentChange: (String) -> Unit = {}
) {
    companion object {
        private const val DEFAULT_NON_PRINTABLE_FLAGS =
            CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or
                CodeEditor.FLAG_DRAW_LINE_SEPARATOR or
                CodeEditor.FLAG_DRAW_WHITESPACE_IN_SELECTION

        private val textMateRegistryLock = Any()

        @Volatile
        private var fileProviderRegistered = false

        @Volatile
        private var themesLoaded = false

        @Volatile
        private var languagesLoaded = false
    }

    private val context get() = binding.root.context
    private var isApplyingExternalText = false
    private var showNonPrintable = true
    private var searchOptions = SearchOptions(SearchOptions.TYPE_NORMAL, true)
    private lateinit var searchMenu: PopupMenu

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
                "=",
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
            ), arrayOf("=", "\t", "{}", "}", "(", ")", ",", ".", ";", "\"", "?", "+", "-", "*", "/")
        )
        inputView.forEachButton {
            it.typeface = typeface
        }

        configureSearch()

        binding.editor.apply {
            typefaceText = typeface
            props.stickyScroll = true
            isWordwrap = true
            setLineSpacing(2f, 1.1f)
            setTextSize(14f)
            nonPrintablePaintingFlags = DEFAULT_NON_PRINTABLE_FLAGS
            // Update display dynamically
            subscribeEvent<SelectionChangeEvent> { _, _ -> updatePositionText() }
            subscribeEvent<PublishSearchResultEvent> { _, _ -> updatePositionText() }
            subscribeEvent<ContentChangeEvent> { event, _ ->
                if (!isApplyingExternalText) {
                    onContentChange(event.editor.text.toString())
                }
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

        binding.btnSearchToggle.setOnClickListener {
            toggleSearchPanel()
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
        binding.btnWordwrap.setOnClickListener {
            binding.editor.isWordwrap = !binding.editor.isWordwrap
            updateFeatureState()
        }
        binding.btnNonPrintable.setOnClickListener {
            showNonPrintable = !showNonPrintable
            binding.editor.nonPrintablePaintingFlags = if (showNonPrintable) {
                DEFAULT_NON_PRINTABLE_FLAGS
            } else {
                0
            }
            updateFeatureState()
        }
        binding.btnSearchPrev.setOnClickListener {
            navigateSearch(previous = true)
        }
        binding.btnSearchNext.setOnClickListener {
            navigateSearch(previous = false)
        }
        binding.btnReplace.setOnClickListener {
            replaceCurrent()
        }
        binding.btnReplaceAll.setOnClickListener {
            replaceAll()
        }
        binding.searchOptions.setOnClickListener {
            searchMenu.show()
        }

        binding.editorTools.isVisible = true
        binding.editorActionBar.isVisible = showToolbar
        binding.btnWordwrap.isVisible = showWordwrapInBottomBar
        binding.btnNonPrintable.isVisible = showNonPrintableInBottomBar

        ensureTextMateRegistryPrepared()

        ensureTextmateTheme()

        binding.editor.setEditorLanguage(
            languageScopeName?.takeIf { it.isNotBlank() }?.let {
                TextMateLanguage.create(it, autoCompleteEnabled)
            } ?: EmptyLanguage()
        )

        setEditorText(initialText)

        updateFeatureState()
        updatePositionText()

        switchThemeIfRequired(context, binding.editor)
    }

    fun updateText(text: String) {
        setEditorText(text)
    }

    private fun setEditorText(text: String) {
        val currentText = binding.editor.text.toString()
        if (currentText == text) {
            return
        }
        isApplyingExternalText = true
        try {
            binding.editor.setText(text)
        } finally {
            isApplyingExternalText = false
        }
        updateBtnState()
        updatePositionText()
    }

    fun toggleSearchPanel() {
        val willShow = !binding.searchPanel.isVisible
        binding.searchPanel.isVisible = willShow
        if (willShow) {
            binding.searchEditor.requestFocus()
            binding.searchEditor.setSelection(binding.searchEditor.text?.length ?: 0)
        } else {
            binding.searchEditor.setText("")
            binding.replaceEditor.setText("")
            binding.editor.searcher.stopSearch()
        }
        updateFeatureState()
        updatePositionText()
    }

    fun undo() {
        if (binding.editor.canUndo()) {
            binding.editor.undo()
            updateBtnState()
            updatePositionText()
        }
    }

    fun redo() {
        if (binding.editor.canRedo()) {
            binding.editor.redo()
            updateBtnState()
            updatePositionText()
        }
    }

    fun toggleWordwrap() {
        binding.editor.isWordwrap = !binding.editor.isWordwrap
        updateFeatureState()
    }

    fun toggleNonPrintable() {
        showNonPrintable = !showNonPrintable
        binding.editor.nonPrintablePaintingFlags = if (showNonPrintable) {
            DEFAULT_NON_PRINTABLE_FLAGS
        } else {
            0
        }
        updateFeatureState()
        updatePositionText()
    }

    private fun updateBtnState() {
        binding.btnUndo.isEnabled = binding.editor.canUndo()
        binding.btnRedo.isEnabled = binding.editor.canRedo()
    }

    private fun updateFeatureState() {
        binding.btnSearchToggle.alpha = if (binding.searchPanel.isVisible) 1f else 0.6f
        binding.btnWordwrap.alpha = if (binding.editor.isWordwrap) 1f else 0.6f
        binding.btnNonPrintable.alpha = if (showNonPrintable) 1f else 0.6f
    }

    private fun configureSearch() {
        binding.searchEditor.doAfterTextChanged {
            tryCommitSearch()
        }

        searchMenu = PopupMenu(context, binding.searchOptions)
        searchMenu.inflate(R.menu.menu_search_options)
        searchMenu.setOnMenuItemClickListener { item ->
            item.isChecked = !item.isChecked
            when (item.itemId) {
                R.id.search_option_regex -> {
                    if (item.isChecked) {
                        searchMenu.menu.findItem(R.id.search_option_whole_word)?.isChecked = false
                    }
                }

                R.id.search_option_whole_word -> {
                    if (item.isChecked) {
                        searchMenu.menu.findItem(R.id.search_option_regex)?.isChecked = false
                    }
                }
            }
            searchOptions = buildSearchOptions(searchMenu.menu.findItem(R.id.search_option_match_case))
            tryCommitSearch()
            true
        }
    }

    private fun buildSearchOptions(matchCaseItem: MenuItem?): SearchOptions {
        val ignoreCase = matchCaseItem?.isChecked != true
        val type = when {
            searchMenu.menu.findItem(R.id.search_option_regex)?.isChecked == true -> SearchOptions.TYPE_REGULAR_EXPRESSION
            searchMenu.menu.findItem(R.id.search_option_whole_word)?.isChecked == true -> SearchOptions.TYPE_WHOLE_WORD
            else -> SearchOptions.TYPE_NORMAL
        }
        return SearchOptions(type, ignoreCase)
    }

    private fun tryCommitSearch() {
        val query = binding.searchEditor.text?.toString().orEmpty()
        if (query.isEmpty()) {
            binding.editor.searcher.stopSearch()
            updatePositionText()
            return
        }
        try {
            binding.editor.searcher.search(query, searchOptions)
        } catch (_: PatternSyntaxException) {
        }
    }

    private fun navigateSearch(previous: Boolean) {
        try {
            if (previous) {
                binding.editor.searcher.gotoPrevious()
            } else {
                binding.editor.searcher.gotoNext()
            }
        } catch (_: IllegalStateException) {
        }
    }

    private fun replaceCurrent() {
        try {
            binding.editor.searcher.replaceThis(binding.replaceEditor.text?.toString().orEmpty())
        } catch (_: IllegalStateException) {
        }
    }

    private fun replaceAll() {
        try {
            binding.editor.searcher.replaceAll(binding.replaceEditor.text?.toString().orEmpty())
        } catch (_: IllegalStateException) {
        }
    }

    private fun ensureTextMateRegistryPrepared() {
        synchronized(textMateRegistryLock) {
            if (!fileProviderRegistered) {
                FileProviderRegistry.getInstance().addFileProvider(
                    AssetsFileResolver(context.assets)
                )
                fileProviderRegistered = true
            }

            if (!themesLoaded) {
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
                themesLoaded = true
            }

            if (!languagesLoaded) {
                GrammarRegistry.getInstance().loadGrammars("textmate/languages.json")
                languagesLoaded = true
            }
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
        footerTitle?.takeIf { it.isNotBlank() }?.let {
            text = "$it  |  $text"
        }
        binding.positionDisplay.text = text
    }

    private fun ensureTextmateTheme() {
        val editor = binding.editor
        var editorColorScheme = editor.colorScheme
        if (editorColorScheme !is TextMateColorScheme) {
            editorColorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance())
            editor.colorScheme = editorColorScheme
        }
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
        binding.editor.searcher.stopSearch()
        binding.editor.release()
    }
}