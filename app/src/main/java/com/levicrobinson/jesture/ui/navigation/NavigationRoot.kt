package com.levicrobinson.jesture.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.levicrobinson.jesture.ui.home.HomeView
import kotlinx.serialization.Serializable

@Serializable
data object HomeDestination: NavKey
data object GestureDestination: NavKey

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(HomeDestination)
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
            rememberSceneSetupNavEntryDecorator()
        ),
        entryProvider = { navKey ->
            when (navKey) {
                HomeDestination -> {
                    NavEntry(
                        key = navKey
                    ) {
                        HomeView()
                    }
                }
                GestureDestination -> {
                    NavEntry(
                        key = navKey
                    ) {

                    }
                }
                else -> throw RuntimeException("Invalid nav entry")
            }
        }
    )
}