/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tornaco.apps.shortx.services.overlay.clipboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.util.Size
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import tornaco.apps.shortx.core.util._dp
import java.io.IOException
import java.util.function.Consumer

// Android 29+
@SuppressLint("NewApi")
class ClipboardImageLoader
constructor(
    private val context: Context,
    private val bgDispatcher: CoroutineDispatcher,
    private val mainScope: CoroutineScope
) {
    private val TAG: String = "ClipboardImageLoader"

    suspend fun load(uri: Uri, timeoutMs: Long = 300) =
        withTimeoutOrNull(timeoutMs) {
            withContext(bgDispatcher) {
                try {
                    val size = 80._dp
                    context.contentResolver.loadThumbnail(uri, Size(size, size * 4), null)
                } catch (e: IOException) {
                    Log.e(TAG, "Thumbnail loading failed!", e)
                    null
                }
            }
        }

    fun loadAsync(uri: Uri, callback: Consumer<Bitmap?>) {
        mainScope.launch { callback.accept(load(uri)) }
    }
}
