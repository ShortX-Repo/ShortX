/*
 * (C) Copyright 2022 Thanox
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
 *
 */

package tornaco.apps.shortx.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.skydoves.landscapist.glide.GlideImage
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.ui.util.GlideApp

@Composable
fun AppIcon(modifier: Modifier, pkg: AppPkg) {
    GlideImage(
        modifier = modifier,
        imageModel = pkg,
        // Crop, Fit, Inside, FillHeight, FillWidth, None
        contentScale = ContentScale.Crop,
        error = github.tornaco.shortx.android.third.party.icons.R.drawable.ic_remix_android_fill,
        requestBuilder = {
            GlideApp.with(LocalContext.current.applicationContext).asDrawable()
                .transition(withCrossFade())
        },
    )
}