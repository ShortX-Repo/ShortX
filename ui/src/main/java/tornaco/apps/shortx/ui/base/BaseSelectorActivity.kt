package tornaco.apps.shortx.ui.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.util.Logger
import java.util.concurrent.atomic.AtomicInteger


abstract class BaseSelectorActivity<T : Parcelable> : BaseActivity() {
    companion object {
        const val INTENT_EXTRA_KEY_CURRENT_CACHE_KEY = "selector.current.cache.key"

        val selectorCache = mutableMapOf<String, Parcelable>()
        val Intent.cacheKeyOrNull get() = getStringExtra(INTENT_EXTRA_KEY_CURRENT_CACHE_KEY)

        fun newCacheKey() = "CacheKey-${cacheKeyIndex.getAndIncrement()}"

        private val cacheKeyIndex = AtomicInteger(0)
        val logger = Logger("SelectorActivity")

        inline fun <reified T : Parcelable> ActivityResult.popupSelectorResult(): T? {
            val t: T? = if (this.resultCode == Activity.RESULT_OK) {
                val cacheKey = this.data?.cacheKeyOrNull
                cacheKey?.let {
                    selectorCache.remove(it) as T?
                }
            } else {
                null
            }

            logger.i("selectorCache: $selectorCache")

            return t
        }

        inline fun <reified T : Parcelable> newIntent(
            context: Context,
            currentData: T? = null,
            clazz: Class<out Activity>
        ): Intent {
            return Intent(context, clazz).apply {
                val cacheKey = newCacheKey()
                putExtra(INTENT_EXTRA_KEY_CURRENT_CACHE_KEY, cacheKey)
                currentData?.let {
                    selectorCache[cacheKey] = it
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun Intent.popupSelectorCache(): T? {
        return cacheKeyOrNull?.let {
            selectorCache.remove(it) as T?
        }
    }

    fun cancelAndFinish() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    fun setResultAndFinish(action: T) {
        setResult(action)
        finish()
    }

    fun setResult(action: T) {
        val cacheKey = newCacheKey()
        selectorCache[cacheKey] = action
        setResult(
            Activity.RESULT_OK,
            Intent().putExtra(INTENT_EXTRA_KEY_CURRENT_CACHE_KEY, cacheKey)
        )
    }

    @Composable
    override fun Content() {
        val current = intent.popupSelectorCache()
        SelectorContent(current)
    }

    @Composable
    abstract fun BaseSelectorActivity<T>.SelectorContent(currentData: T?)
}