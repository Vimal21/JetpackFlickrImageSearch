import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.flickrs.imagesearch.domain.entities.MappedImageItemModel
import com.flickrs.imagesearch.ui.commons.AnimatedShimmer
import com.flickrs.imagesearch.ui.commons.Network
import com.flickrs.imagesearch.ui.searchImage.ImageSearchViewModel
import com.flickrs.imagesearch.ui.searchImage.SearchImageEvent
import com.flickrs.imagesearch.ui.searchImage.SearchImageState
import com.flickrs.imagesearch.R

/**
 * Composable function for rendering the search screen.
 *
 * This function is responsible for rendering the main search screen UI.
 * It includes the search screen content which consists of a search field, image grid,
 * loading indicator, and error dialog. The UI state is managed by the provided [viewModel].
 * Users can interact with the search field to enter a query and initiate a search.
 * Clicking on an image item in the grid triggers the [onImageClicked] callback.
 *
 * @param viewModel The view model responsible for managing the state of the search screen.
 * @param onImageClicked A callback function to be invoked when an image item in the grid is clicked.
 */
@Composable
fun SearchScreen(
    viewModel: ImageSearchViewModel,
    onImageClicked: (item: MappedImageItemModel) -> Unit
) {

    ScreenScreenContent(
        modifier = Modifier.fillMaxSize(),
        handleEvent = viewModel::handleEvent,
        uiState = viewModel.uiState.collectAsState().value,
        onImageItemClicked = onImageClicked
    )
}

/**
 * Composable function for rendering a loading indicator.
 *
 * This function renders a loading indicator consisting of multiple animated shimmer effects.
 * It displays a column layout containing multiple instances of the AnimatedShimmer composable,
 * creating a shimmer effect to indicate loading or data fetching.
 */
@Composable
fun LoadingComposable() {
    Column {
        repeat(7) {
            AnimatedShimmer()
        }
    }
}

/**
 * Composable function for rendering the content of the main screen.
 *
 * This function renders the content of the main screen, including the search field, image grid,
 * loading indicator, and error dialog based on the provided [uiState].
 * Users can interact with the search field to enter a query and initiate a search.
 * Clicking on an image item in the grid triggers the [onImageItemClicked] callback.
 *
 * @param modifier The modifier for the screen content.
 * @param uiState The current state of the main screen.
 * @param handleEvent A callback function to handle events triggered by user interactions.
 * @param onImageItemClicked A callback function to be invoked when an image item in the grid is clicked.
 */
@Composable
internal fun ScreenScreenContent(
    modifier: Modifier = Modifier,
    uiState: SearchImageState,
    handleEvent: (event: SearchImageEvent) -> Unit,
    onImageItemClicked: (item: MappedImageItemModel) -> Unit
) {

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = modifier) {

            SearchFieldComposable(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp, top = 8.dp, bottom = 4.dp
                    ),
                query = uiState.query,
                onSearchChange = { queryChanged ->
                    handleEvent(SearchImageEvent.QueryChanged(queryChanged))
                },
                onSearchClicked = {
                    handleEvent(
                        SearchImageEvent.InitiateSearch(
                            uiState.query ?: ""
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (uiState.isLoading) {
                LoadingComposable()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(10.dp)
                ) {
                    items(uiState.success.size) {
                        GridImageItemCard(
                            modifier = Modifier.fillMaxWidth(),
                            item = uiState.success[it],
                            onNextScreen = onImageItemClicked
                        )
                    }
                }
            }

            uiState.error?.let { error ->
                ErrorDialogComposable(
                    errorMsg = error,
                    dismissError = {
                        handleEvent(
                            SearchImageEvent.ErrorDismissed
                        )
                    }
                )
            }
        }
    }
}

/**
 * Composable function for rendering an error dialog.
 *
 * This function renders an error dialog displaying the provided error message.
 * Users can dismiss the dialog by clicking on the confirm button or outside the dialog.
 *
 * @param modifier The modifier for the error dialog.
 * @param errorMsg The error message to be displayed in the dialog.
 * @param dismissError A callback function to be invoked when the error dialog is dismissed.
 */
@Composable
fun ErrorDialogComposable(
    modifier: Modifier = Modifier,
    errorMsg: String,
    dismissError: () -> Unit
) {
    AlertDialog(
        modifier = modifier.testTag(""),
        onDismissRequest = { dismissError() },
        confirmButton = {
            TextButton(
                onClick = {
                    dismissError()
                }
            ) {
                Text(text = stringResource(id = R.string.error_action))
            }
        },
        title = {
            Text(
                text = stringResource(
                    id = R.string.error_title
                ),
                fontSize = 18.sp
            )
        },
        text = {
            if(!Network.isNetworkAvailable(LocalContext.current)) {
                Text(
                    text = stringResource(
                        id = R.string.no_internet_connection
                    )
                )
            } else {
                Text(
                    text = errorMsg
                )
            }
        }
    )
}


/**
 * Composable function for rendering a grid item card displaying an image.
 *
 * This function renders a grid item card displaying an image with its title.
 * Users can interact with the card to navigate to the detail screen of the image.
 *
 * @param item The MappedImageItemModel representing the image item to be displayed.
 * @param modifier The modifier for the card layout.
 * @param onNextScreen A callback function to be invoked when the card is clicked, typically to navigate to the detail screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GridImageItemCard(
    item: MappedImageItemModel,
    modifier: Modifier = Modifier,
    onNextScreen: (item: MappedImageItemModel) -> Unit
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = {
            onNextScreen(item)
        }) {
        Box(modifier = Modifier.height(200.dp)) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                model = ImageRequest.Builder(LocalContext.current).data(item.imageLink)
                    .crossfade(true).build(),
                contentDescription = item.imageTitle,
                contentScale = ContentScale.Crop,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent, Color.Black
                            ), startY = 350f
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.BottomStart
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.imageTitle,
                        maxLines = 2,
                        style = TextStyle(color = Color.White, fontSize = 16.sp)
                    )
                }
            }

        }

    }
}

/**
 * Composable function for rendering a search field.
 *
 * This function renders a search field where users can input their search query.
 * It includes an input text field with a leading search icon and a trailing clear icon.
 * Users can input their query, perform a search action by pressing the search button on the keyboard,
 * and clear the search field by clicking the clear icon.
 *
 * @param modifier The modifier for the search field.
 * @param query The current search query string.
 * @param onSearchChange A callback function to be invoked when the search query changes.
 * @param onSearchClicked A callback function to be invoked when the search button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFieldComposable(
    modifier: Modifier = Modifier,
    query: String?,
    onSearchChange: (query: String) -> Unit,
    onSearchClicked: () -> Unit
) {
    TextField(modifier = modifier
        .testTag("")
        .background(MaterialTheme.colorScheme.background),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ), keyboardActions = KeyboardActions(onSearch = {
            onSearchClicked()
        }),
        value = query ?: "",
        onValueChange = {
            onSearchChange(it)

        }, label = {
            Text(text = stringResource(id = R.string.label_search))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search, contentDescription = null
            )
        },
        trailingIcon = {
            Icon(
                modifier = Modifier.clickable(
                    onClickLabel = stringResource(id = R.string.cd_clear_search)
                ) {
                    onSearchChange("")
                },
                imageVector = Icons.Default.Cancel, contentDescription = null
            )
        }, singleLine = true,
        maxLines = 1
    )
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    MaterialTheme {
//        SearchScreen(onImageClicked = {})
    }
}