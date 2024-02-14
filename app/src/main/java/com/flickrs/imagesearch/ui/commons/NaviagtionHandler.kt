package com.flickrs.imagesearch.ui.commons

import SearchScreen
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flickrs.imagesearch.domain.entities.MappedImageItemModel
import com.flickrs.imagesearch.ui.searchImage.ImageDetailScreen
import com.flickrs.imagesearch.ui.searchImage.ImageSearchViewModel
import com.flickrs.imagesearch.ui.searchImage.ImageSearchWithDetailTwoPane

/**
 * Handles navigation and layout based on the window size.
 *
 * This function manages navigation and layout based on the provided [windowSize] parameter.
 * It observes the UI state from the [ImageSearchViewModel] and determines the appropriate
 * navigation and layout strategy accordingly.
 *
 * @param windowSize The window size class indicating the size of the application window.
 */
@Composable
fun Navigation(
    windowSize: WindowWidthSizeClass
) {
    val viewModel: ImageSearchViewModel = hiltViewModel()
    val replyUiState = viewModel.uiState.collectAsState().value
    val navController = rememberNavController()

    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            NavigationBuilder(viewModel, navController)
        }

        WindowWidthSizeClass.Medium -> {
            ImageSearchWithDetailTwoPane(viewModel, replyUiState)
        }

        WindowWidthSizeClass.Expanded -> {
            ImageSearchWithDetailTwoPane(viewModel, replyUiState)
        }

        else -> {
            NavigationBuilder(viewModel, navController)

        }
    }
}

/**
 * Builds the navigation graph for the application.
 *
 * This function constructs the navigation graph using Jetpack Navigation Compose components.
 * It sets up destinations and associated composable for each screen in the application.
 *
 * @param viewModel The [ImageSearchViewModel] instance for managing image search data.
 * @param navController The NavHostController for managing navigation within the app.
 */
@Composable
fun NavigationBuilder(viewModel: ImageSearchViewModel, navController: NavHostController) {

    NavHost(navController = navController, startDestination = Destinations.Home.path) {
        composable(Destinations.Home.path) {
            SearchScreen(viewModel, onImageClicked = { imageItem ->
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    key = "imageItem",
                    value = imageItem
                )
                navController.navigate(Destinations.Details.path)
            })
        }

        composable(Destinations.Details.path) {
            val result =
                navController.previousBackStackEntry?.savedStateHandle?.get<MappedImageItemModel>("imageItem")
            result?.let { it1 ->
                ImageDetailScreen(result = it1) {
                    navController.navigateUp()
                }
            }
        }

    }
}

sealed class Destinations(val path: String) {
    object Home : Destinations("home")
    object Details : Destinations("details")
}
