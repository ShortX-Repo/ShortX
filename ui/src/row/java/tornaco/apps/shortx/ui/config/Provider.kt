package tornaco.apps.shortx.ui.config

class Provider {
    fun provideConfig(): Config {
        return FirebaseConfig()
    }
}