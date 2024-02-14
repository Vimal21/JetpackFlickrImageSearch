package com.flickrs.imagesearch.ui.searchImage

import SearchScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Composable function for rendering the search screen with detail view in a two-pane layout.
 *
 * This function renders the search screen and the detail view side by side in a two-pane layout.
 * The left pane displays the main search screen UI using the SearchScreen composable,
 * and the right pane displays the detail view using the ImageDetailScreen composable.
 *
 * @param viewModel The view model responsible for managing the state of the search screen.
 * @param replyUiState The current state of the search screen UI.
 */
@Composable
fun ImageSearchWithDetailTwoPane(viewModel: ImageSearchViewModel, replyUiState: SearchImageState) {
    Column {
        Row {
            // Left pane content
            Surface(modifier = Modifier.weight(1f)) {
                // Content for the left pane
                SearchScreen(viewModel, onImageClicked = { imageItem ->
                    viewModel.handleEvent(SearchImageEvent.UpdateCurrentItem(imageItem))
                })
            }

            Spacer(modifier = Modifier
                .fillMaxHeight()
                .width(4.dp))
            // Right pane content
            Surface(modifier = Modifier.weight(1f)) {
                // Content for the right pane
                replyUiState.currentImageNode?.let {
                    ImageDetailScreen(1, it) {}
                } ?: kotlin.run {
                    EmptyCompose()
                }
            }
        }
    }
}

/**
 * Composable function for rendering an empty placeholder.
 *
 * This function renders an empty placeholder when there is no data to show.
 * It displays a surface with text indicating that there is no data available.
 */
@Composable
fun EmptyCompose() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text("NO data to show!")
    }
}