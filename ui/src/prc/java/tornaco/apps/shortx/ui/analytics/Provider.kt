package tornaco.apps.shortx.ui.analytics

class Provider {
    fun provideAnalytics(): Analytics {
        return PrcAnalytics()
    }
}