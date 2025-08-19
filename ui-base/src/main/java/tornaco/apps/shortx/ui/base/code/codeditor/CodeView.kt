package tornaco.apps.shortx.ui.base.code.codeditor

import android.graphics.Color
import android.view.Gravity
import android.widget.ArrayAdapter
import com.amrdeveloper.codeview.CodeView
import github.tornaco.shortx.ui.base.R
import tornaco.apps.shortx.core.util._sp
import tornaco.apps.shortx.ui.base.code.codeditor.syntax.LanguageManager
import tornaco.apps.shortx.ui.base.code.codeditor.syntax.LanguageName
import tornaco.apps.shortx.ui.base.code.codeditor.syntax.ThemeName
import tornaco.apps.shortx.ui.util.TypefaceHelper


fun initView(
    codeView: CodeView,
    isDarkTheme: Boolean
) {
    setupCodeView(codeView, isDarkTheme)
    setupLanguageAutoComplete(codeView)
}

fun setupCodeView(codeView: CodeView, isDarkTheme: Boolean) {
    codeView.background = null
    codeView.textSize = 14f
    codeView.gravity = Gravity.TOP or Gravity.START
    codeView.typeface = TypefaceHelper.jetbrainsMonoMedium(codeView.context)

    // Setup Line number feature
    codeView.setEnableLineNumber(true)
    codeView.setLineNumberTextColor(Color.GRAY)
    codeView.setLineNumberTextSize(12._sp)

    // Setup Auto indenting feature
    codeView.setTabLength(4)
    codeView.setEnableAutoIndentation(true)

    // Setup the language and theme with SyntaxManager helper class
    val languageManager = LanguageManager(codeView.context, codeView)
    languageManager.applyTheme(LanguageName.JAVA, ThemeName.DYNAMIC, isDarkTheme)

    // Setup auto pair complete
    val pairCompleteMap: MutableMap<Char, Char> = HashMap()
    pairCompleteMap['{'] = '}'
    pairCompleteMap['['] = ']'
    pairCompleteMap['('] = ')'
    pairCompleteMap['<'] = '>'
    pairCompleteMap['"'] = '"'
    pairCompleteMap['\''] = '\''
    codeView.setPairCompleteMap(pairCompleteMap)
    codeView.enablePairComplete(false)
    codeView.enablePairCompleteCenterCursor(false)
    codeView.setHorizontallyScrolling(false)
}

fun setupLanguageAutoComplete(codeView: CodeView) {
    val languageManager = LanguageManager(codeView.context, codeView)
    val languageKeywords: Array<String> = languageManager.getLanguageKeywords(LanguageName.JAVA)

    val layoutId = R.layout.ui_list_item_suggestion

    val viewId = R.id.suggestItemTextView
    val adapter = ArrayAdapter(codeView.context, layoutId, viewId, languageKeywords)

    codeView.setAdapter(adapter)
}
