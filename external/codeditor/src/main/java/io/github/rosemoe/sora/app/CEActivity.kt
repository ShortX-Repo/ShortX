/*******************************************************************************
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/Rosemoe/sora-editor
 *    Copyright (C) 2020-2023  Rosemoe
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact Rosemoe by email 2073412493@qq.com if you need
 *     additional information or have any questions
 ******************************************************************************/
package io.github.rosemoe.sora.app

import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import io.github.rosemoe.sora.app.databinding.ActivityCodeEditorBinding
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
import io.github.rosemoe.sora.widget.EditorSearcher.SearchOptions
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.getComponent
import io.github.rosemoe.sora.widget.subscribeEvent
import org.eclipse.tm4e.core.registry.IThemeSource
import java.util.regex.PatternSyntaxException


class CEActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCodeEditorBinding
    private lateinit var searchMenu: PopupMenu
    private var searchOptions = SearchOptions(false, false)
    private var undo: MenuItem? = null
    private var redo: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodeEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val inputView = binding.symbolInput
        inputView.bindEditor(binding.editor)
        val typeface =
            Typeface.createFromAsset(assets, "fonts/google/jetbrains/JetBrainsMonoRegular.ttf")
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
        binding.searchEditor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                tryCommitSearch()
            }
        })
        searchMenu = PopupMenu(this, binding.searchOptions)
        searchMenu.inflate(R.menu.menu_search_options)
        searchMenu.setOnMenuItemClickListener {
            it.isChecked = !it.isChecked
            val ignoreCase = !searchMenu.menu.findItem(R.id.search_option_match_case)!!.isChecked
            if (it.isChecked) {
                when (it.itemId) {
                    R.id.search_option_regex -> {
                        searchMenu.menu.findItem(R.id.search_option_whole_word)!!.isChecked = false
                    }

                    R.id.search_option_whole_word -> {
                        searchMenu.menu.findItem(R.id.search_option_regex)!!.isChecked = false
                    }
                }
            }
            var type = SearchOptions.TYPE_NORMAL
            val regex = searchMenu.menu.findItem(R.id.search_option_regex)!!.isChecked
            if (regex) {
                type = SearchOptions.TYPE_REGULAR_EXPRESSION
            }
            val wholeWord = searchMenu.menu.findItem(R.id.search_option_whole_word)!!.isChecked
            if (wholeWord) {
                type = SearchOptions.TYPE_WHOLE_WORD
            }
            searchOptions = SearchOptions(type, ignoreCase)
            tryCommitSearch()
            true
        }
        binding.editor.apply {
            typefaceText = typeface
            props.stickyScroll = true
            isWordwrap = true
            setLineSpacing(2f, 1.1f)
            nonPrintablePaintingFlags =
                CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or CodeEditor.FLAG_DRAW_LINE_SEPARATOR or CodeEditor.FLAG_DRAW_WHITESPACE_IN_SELECTION
            // Update display dynamically
            subscribeEvent<SelectionChangeEvent> { _, _ -> updatePositionText() }
            subscribeEvent<PublishSearchResultEvent> { _, _ -> updatePositionText() }
            subscribeEvent<ContentChangeEvent> { _, _ ->
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


        loadDefaultThemes()
        loadDefaultLanguages()

        ensureTextmateTheme()

        val editor = binding.editor
        val language = TextMateLanguage.create(
            "source.java", true
        )

        editor.setEditorLanguage(language)

        // Load file here.

        updatePositionText()
        updateBtnState()

        switchThemeIfRequired(this, binding.editor)
    }

    private fun tryCommitSearch() {
        val editable = binding.searchEditor.editableText
        if (editable.isNotEmpty()) {
            try {
                binding.editor.searcher.search(
                    editable.toString(),
                    searchOptions
                )
            } catch (e: PatternSyntaxException) {
                // Regex error
            }
        } else {
            binding.editor.searcher.stopSearch()
        }
    }


    private /*suspend*/ fun loadDefaultThemes() /*= withContext(Dispatchers.IO)*/ {

        //add assets file provider
        FileProviderRegistry.getInstance().addFileProvider(
            AssetsFileResolver(
                applicationContext.assets
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

    private fun resetColorScheme() {
        binding.editor.apply {
            val colorScheme = this.colorScheme
            // reset
            this.colorScheme = colorScheme
        }
    }


    private fun ensureTextmateTheme() {
        val editor = binding.editor
        var editorColorScheme = editor.colorScheme
        if (editorColorScheme !is TextMateColorScheme) {
            editorColorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance())
            editor.colorScheme = editorColorScheme
        }
    }

    private fun generateKeybindingString(event: KeyBindingEvent): String {
        val sb = StringBuilder()
        if (event.isCtrlPressed) {
            sb.append("Ctrl + ")
        }

        if (event.isAltPressed) {
            sb.append("Alt + ")
        }

        if (event.isShiftPressed) {
            sb.append("Shift + ")
        }

        sb.append(KeyEvent.keyCodeToString(event.keyCode))
        return sb.toString()
    }

    private fun updateBtnState() {
        undo?.isEnabled = binding.editor.canUndo()
        redo?.isEnabled = binding.editor.canRedo()
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        switchThemeIfRequired(this, binding.editor)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        undo = menu.findItem(R.id.text_undo)
        redo = menu.findItem(R.id.text_redo)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.editor.release()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val editor = binding.editor
        when (id) {
            R.id.text_undo -> editor.undo()
            R.id.text_redo -> editor.redo()
            R.id.goto_end -> editor.setSelection(
                editor.text.lineCount - 1,
                editor.text.getColumnCount(editor.text.lineCount - 1)
            )

            R.id.move_up -> editor.moveSelectionUp()
            R.id.move_down -> editor.moveSelectionDown()
            R.id.home -> editor.moveSelectionHome()
            R.id.end -> editor.moveSelectionEnd()
            R.id.move_left -> editor.moveSelectionLeft()
            R.id.move_right -> editor.moveSelectionRight()

            R.id.search_panel_st -> {
                if (binding.searchPanel.visibility == View.GONE) {
                    binding.apply {
                        replaceEditor.setText("")
                        searchEditor.setText("")
                        editor.searcher.stopSearch()
                        searchPanel.visibility = View.VISIBLE
                        item.isChecked = true
                    }
                } else {
                    binding.searchPanel.visibility = View.GONE
                    editor.searcher.stopSearch()
                    item.isChecked = false
                }
            }

            R.id.search_am -> {
                binding.replaceEditor.setText("")
                binding.searchEditor.setText("")
                editor.searcher.stopSearch()
                editor.beginSearchMode()
            }

            R.id.text_wordwrap -> {
                item.isChecked = !item.isChecked
                editor.isWordwrap = item.isChecked
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun gotoNext(view: View?) {
        try {
            binding.editor.searcher.gotoNext()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun gotoLast(view: View?) {
        try {
            binding.editor.searcher.gotoPrevious()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun replace(view: View?) {
        try {
            binding.editor.searcher.replaceThis(binding.replaceEditor.text.toString())
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun replaceAll(view: View?) {
        try {
            binding.editor.searcher.replaceAll(binding.replaceEditor.text.toString())
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun showSearchOptions(view: View?) {
        searchMenu.show()
    }
}