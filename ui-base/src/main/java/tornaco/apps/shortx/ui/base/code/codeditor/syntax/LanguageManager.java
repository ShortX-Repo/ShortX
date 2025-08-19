package tornaco.apps.shortx.ui.base.code.codeditor.syntax;

import android.content.Context;

import com.amrdeveloper.codeview.Code;
import com.amrdeveloper.codeview.CodeView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LanguageManager {

    private final Context context;
    private final CodeView codeView;

    public LanguageManager(Context context, CodeView codeView) {
        this.context = context;
        this.codeView = codeView;
    }

    public void applyTheme(LanguageName language, ThemeName theme, boolean isDarkTheme) {
        switch (theme) {
            case DYNAMIC:
                switch (language) {
                    case JAVA:
                        if (isDarkTheme) {
                            JavaLanguage.applyMonokaiTheme(context, codeView);
                        } else {
                            JavaLanguage.applyNoctisWhiteTheme(context, codeView);
                        }
                        break;
                }
                break;
        }
    }

    public String[] getLanguageKeywords(LanguageName language) {
        switch (language) {
            case JAVA:
                return JavaLanguage.getKeywords(context);
            default:
                return new String[]{};
        }
    }

    public List<Code> getLanguageCodeList(LanguageName language) {
        switch (language) {
            case JAVA:
                return JavaLanguage.getCodeList(context);
            default:
                return new ArrayList<>();
        }
    }

    public Set<Character> getLanguageIndentationStarts(LanguageName language) {
        switch (language) {
            case JAVA:
                return JavaLanguage.getIndentationStarts();
            default:
                return new HashSet<>();
        }
    }

    public Set<Character> getLanguageIndentationEnds(LanguageName language) {
        switch (language) {
            case JAVA:
                return JavaLanguage.getIndentationEnds();
            default:
                return new HashSet<>();
        }
    }

    public String getCommentStart(LanguageName language) {
        switch (language) {
            case JAVA:
                return JavaLanguage.getCommentStart();
            default:
                return "";
        }
    }

    public String getCommentEnd(LanguageName language) {
        switch (language) {
            case JAVA:
                return JavaLanguage.getCommentEnd();
            default:
                return "";
        }
    }
}
