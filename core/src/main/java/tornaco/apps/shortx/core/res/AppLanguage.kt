package tornaco.apps.shortx.core.res

import tornaco.apps.shortx.core.proto.common.AppLanguage

fun AppLanguage.appLanguageToAssetsFileName(default: () -> String): String {
    return when (this) {
        AppLanguage.enUS -> {
            "i18n-en-US.json"
        }

        AppLanguage.zhCN -> {
            "i18n-zh-CN.json"
        }

        AppLanguage.zhTW -> {
            "i18n-zh-TW.json"
        }

        AppLanguage.UNRECOGNIZED -> {
            default()
        }
    }
}