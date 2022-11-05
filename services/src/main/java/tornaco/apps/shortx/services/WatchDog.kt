package tornaco.apps.shortx.services

class WatchDogService : SystemService() {
    private val fatalErrors = mutableListOf<Throwable>()

    override fun systemReady() {
        super.systemReady()
    }

    fun onFatalError(error: Throwable) {
        logger.e(error, "FATAL: got error in SYSTEM PROCESS")
        fatalErrors.add(error)
    }
}