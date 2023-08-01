package tornaco.apps.shortx.ui.overlay

import android.os.Bundle
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


fun AbstractComposeView.addToLifecycle() {
    val viewModelStore = ViewModelStore()
    val lifecycleOwner = MyLifecycleOwner()
    lifecycleOwner.performRestore(null)
    lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    setViewTreeLifecycleOwner(lifecycleOwner)
    setViewTreeViewModelStoreOwner(object : ViewModelStoreOwner {
        override val viewModelStore: ViewModelStore
            get() = viewModelStore
    })
    setViewTreeSavedStateRegistryOwner(lifecycleOwner)
    val coroutineContext = AndroidUiDispatcher.CurrentThread
    val runRecomposeScope = CoroutineScope(coroutineContext)
    val reComposer = Recomposer(coroutineContext)
    this.compositionContext = reComposer
    runRecomposeScope.launch {
        reComposer.runRecomposeAndApplyChanges()
    }
}


internal class MyLifecycleOwner : SavedStateRegistryOwner {
    private var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private var mSavedStateRegistryController: SavedStateRegistryController =
        SavedStateRegistryController.create(this)

    /**
     * @return True if the Lifecycle has been initialized.
     */
    val isInitialized: Boolean
        get() = true

    override val lifecycle: Lifecycle
        get() = mLifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = mSavedStateRegistryController.savedStateRegistry


    fun setCurrentState(state: Lifecycle.State) {
        mLifecycleRegistry.currentState = state
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        mLifecycleRegistry.handleLifecycleEvent(event)
    }

    fun performRestore(savedState: Bundle?) {
        mSavedStateRegistryController.performRestore(savedState)
    }

    fun performSave(outBundle: Bundle) {
        mSavedStateRegistryController.performSave(outBundle)
    }
}