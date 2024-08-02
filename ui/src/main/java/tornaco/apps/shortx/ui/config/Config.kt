package tornaco.apps.shortx.ui.config

interface Config {
    val isFeatureCVFindImageEnabled: Boolean
}

val config by lazy { Provider().provideConfig() }