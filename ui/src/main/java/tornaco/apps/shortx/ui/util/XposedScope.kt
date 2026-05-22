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

package tornaco.apps.shortx.ui.util

import github.tornaco.android.thanos.core.pm.Pkg
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import tornaco.apps.shortx.core.util.Logger

object XposedScope {
    private var _service: XposedService? = null
    val service get() = _service
    private val logger = Logger("XposedScope")

    private inline fun withService(onService: (XposedService) -> Unit) {
        service?.let { onService(it) }
    }

    fun requestScope(pkg: Pkg) {
        withService {
            kotlin.runCatching {
                if (!it.scope.contains(pkg.pkgName)) {
                    logger.w("XposedScope requestScope: $pkg")
                    it.requestScope(listOf(pkg.pkgName), object : XposedService.OnScopeEventListener {})
                } else {
                    logger.w("XposedScope already scoped: $pkg")
                }
            }.onFailure {
                logger.e(it)
            }
        }
    }

    fun init() {
        logger.w("Init.")
        kotlin.runCatching {
            XposedServiceHelper.registerListener(object : XposedServiceHelper.OnServiceListener {
                override fun onServiceBind(service: XposedService) {
                    logger.w("XposedScope onServiceBind: $service")
                    _service = service
                }

                override fun onServiceDied(service: XposedService) {
                    logger.w("XposedScope onServiceDied: $service")
                    _service = null
                }
            })
        }.onFailure {
            logger.e(it)
        }
    }
}
