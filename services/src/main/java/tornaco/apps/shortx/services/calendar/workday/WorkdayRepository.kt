package tornaco.apps.shortx.services.calendar.workday

import android.content.Context
import tornaco.apps.shortx.core.AppGlobals
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.data.Dirs
import java.io.File

class WorkdayRepository(
    private val context: Context,
    private val parser: WorkdayJsonParser = WorkdayJsonParser(),
) : WorkdayYearSource {
    companion object {
        private val logger = Logger("WorkdayRepository")
        private const val REGION = "CN"
        private const val ASSET_PREFIX = "workday-calendar"
        private const val UNIFIED_FILE_NAME = "holidayAPI.json"

        private val yearCache = mutableMapOf<Int, WorkdayYearData?>()

        fun invalidateAll() {
            synchronized(yearCache) {
                yearCache.clear()
            }
        }
    }

    override fun getYear(year: Int): WorkdayYearData? {
        synchronized(yearCache) {
            if (yearCache.containsKey(year)) {
                logger.d("getYear($year): hit in-memory cache, hasData=${yearCache[year] != null}")
                return yearCache[year]
            }
        }

        val loaded = loadFromCache(year) ?: loadFromAssets(year)
        logger.d("getYear($year): resolved source=${if (loaded == null) "none" else "cache-or-assets"}, hasData=${loaded != null}")

        synchronized(yearCache) {
            yearCache[year] = loaded
        }

        return loaded
    }

    private fun appContext(): Context? {
        AppGlobals.appContext?.let {
            logger.d("Using AppGlobals.appContext for workday data access")
            return it
        }
        logger.w("AppGlobals.appContext not ready, fallback to AppResources(app package context)")
        return runCatching {
            AppResources(context, BuildPropInternal.APP_PACKAGE_NAME).appContext
        }.onFailure {
            logger.e(it, "Failed to build app context for workday data access")
        }.getOrNull()
    }

    private fun loadFromCache(year: Int): WorkdayYearData? {
        val file = File(Dirs.workdayCacheDir(), "$REGION/$UNIFIED_FILE_NAME").apply {
            parentFile?.mkdirs()
        }
        if (!file.exists() || !file.isFile) {
            logger.d("No cached workday file for year=$year at ${file.absolutePath}")
            return null
        }
        logger.i("Loading cached workday file for year=$year from ${file.absolutePath}")
        return runCatching { parser.parse(file.readText(), requestedYear = year) }
            .onFailure {
                logger.e(it, "Failed to parse cached workday file for year=$year")
            }
            .getOrNull()
    }

    private fun loadFromAssets(year: Int): WorkdayYearData? {
        val appContext = appContext() ?: return null
        val path = "$ASSET_PREFIX/$REGION/$UNIFIED_FILE_NAME"
        logger.i("Loading bundled workday asset for year=$year from $path")
        return runCatching {
            appContext.assets.open(path).use { input ->
                parser.parse(input.bufferedReader().readText(), requestedYear = year)
            }
        }.onFailure {
            logger.e(it, "Failed to load bundled workday asset for year=$year from $path")
        }.getOrNull()
    }
}