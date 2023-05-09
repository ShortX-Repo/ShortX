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

@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.subscribe

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.main.AppRoute
import tornaco.apps.shortx.ui.theme.ShortXTheme

@Composable
fun SubscribeScreen(back: () -> Unit) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        title = {
                            Text(
                                text = LocalI18N.current["ui.subscription.intro.title"]
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        scrollBehavior = scrollBehavior,
                    )
                }) { contentPadding ->
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val progressDialog = rememberProgressDialogState(
                        title = LocalI18N.current["ui.subscription.code.verify.title"],
                        message = LocalI18N.current["ui.subscription.code.verify.message"]
                    )
                    ProgressDialog(state = progressDialog)

                    val context = LocalContext.current
                    val failMsg = LocalI18N.current["ui.subscription.code.verify.fail"]
                    val failNetworkMsg =
                        LocalI18N.current["ui.subscription.code.verify.fail.network"]

                    val vm = hiltViewModel<SubscribeViewModel>()
                    LaunchedEffect(vm) {
                        vm.codeVerifyEvent.collectLatest {
                            when (it) {
                                CodeVerifyEvent.Verifying -> {
                                    progressDialog.show()
                                }
                                CodeVerifyEvent.Success -> {
                                    progressDialog.dismiss()
                                }
                                CodeVerifyEvent.Fail -> {
                                    progressDialog.dismiss()
                                    Toast.makeText(context, failMsg, Toast.LENGTH_LONG).show()
                                }
                                CodeVerifyEvent.NetworkError -> {
                                    progressDialog.dismiss()
                                    Toast.makeText(context, failNetworkMsg, Toast.LENGTH_LONG)
                                        .show()
                                }
                                CodeVerifyEvent.Error -> {
                                    progressDialog.dismiss()
                                    Toast.makeText(context, failMsg, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }

                    val navHost = LocalNavHostController.current


                    val inputDialogState = rememberTextInputState(
                        title = LocalI18N.current["Input code"],
                        message = null,
                        onSelected = {
                            vm.verify(it.trim().replace(System.lineSeparator(), ""))
                        }
                    )
                    TextInputDialog(state = inputDialogState)

                    FilledTonalButton(onClick = {
                        inputDialogState.show()
                    }) {
                        Text(text = "Activation Code verify")
                    }


                    FilledTonalButton(onClick = {
                        navHost.navigate(AppRoute.LVLCheck.name)
                    }) {
                        Text(text = "Google Play verify")
                    }


                }
            }
        }
    }
}