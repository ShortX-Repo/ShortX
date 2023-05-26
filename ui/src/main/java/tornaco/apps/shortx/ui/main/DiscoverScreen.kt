@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.subscribe.featureAccessCheckUi


@Composable
fun DiscoverScreen() {
    val vm = hiltViewModel<MainViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.refreshStates()
    }

    val appNavController = LocalNavHostController.current
    val context = LocalContext.current

    val featureAccess = featureAccessCheckUi()

    MainScaffold(
        state = state,
        appBarTitleKey = "ui.feat.label.discover",
        appBarActions = {

        },
        onClipboardClick = {

        },
        onAddClick = {

        },
        acceptPrivacyStatement = {
            vm.acceptPrivacyStatement()
        }
    ) {

    }
}