package tornaco.apps.shortx.ui.remote

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.util.Logger
import javax.inject.Inject

class RemoteRepo @Inject constructor(@ApplicationContext private val context: Context) {
    private val logger = Logger("RemoteRepo")

    private val api by lazy {
        RemoteApi.create(context)
    }

    suspend fun getActions(): List<RemoteExecuteMVELAction> = api.getActions()
}