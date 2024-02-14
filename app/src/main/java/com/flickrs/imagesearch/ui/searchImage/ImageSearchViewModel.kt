package com.flickrs.imagesearch.ui.searchImage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flickrs.imagesearch.domain.entities.MappedImageItemModel
import com.flickrs.imagesearch.domain.usecases.ImageSearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageSearchViewModel @Inject constructor(
    private val useCase: ImageSearchUseCase
) : ViewModel() {

    val uiState = MutableStateFlow(SearchImageState())

    init {
        viewModelScope.launch {
            loadInitialImages()
        }
    }

    /**
     * Handles events related to the image search feature.
     *
     * This function processes various events associated with the image search functionality,
     * such as error dismissal, initiating search, updating search query, handling errors,
     * and updating the current image item.
     *
     * @param imageSearchEvent The event related to image search to be handled.
     */
    fun handleEvent(imageSearchEvent: SearchImageEvent) {
        when (imageSearchEvent) {
            is SearchImageEvent.ErrorDismissed -> {
                dismissError()
            }

            is SearchImageEvent.InitiateSearch -> {
                fetchData(imageSearchEvent.query)
            }

            is SearchImageEvent.QueryChanged -> {
                updateQuery(imageSearchEvent.query)
                fetchDataByQuery(imageSearchEvent.query)
            }

            is SearchImageEvent.OnError -> {
                onError(imageSearchEvent.error)
            }

            is SearchImageEvent.UpdateCurrentItem -> {
                updateCurrentImage(imageSearchEvent.item)
            }
        }
    }

    /**
     * Updates the current image item in the UI state.
     * This function updates the current image item in the UI state with the provided [item].
     *
     * @param item The new current image item.
     */
    private fun updateCurrentImage(item: MappedImageItemModel) {
        uiState.value = uiState.value.copy(
            currentImageNode = item
        )
    }

    /**
     * Dismisses the error state and resets the current image item.
     * This function dismisses the error state in the UI state and resets the current image item to null.
     */
    private fun dismissError() {
        uiState.value = uiState.value.copy(
            error = null,
            currentImageNode = null
        )
    }

    /**
     * Fetches image search data based on the provided query.
     *
     * This function initiates a data fetching process based on the provided [query].
     * It updates the UI state to indicate loading, performs the data fetching operation
     * using a use case, handles potential errors, and updates the UI state accordingly
     * upon receiving the search results.
     *
     * @param query The search query string.
     */
    private fun fetchData(query: String) {
        uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch {
            useCase.execute(formatQuery(query)).catch { error ->
                handleEvent(SearchImageEvent.OnError(error.message.toString()))
            }.collect { result ->
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    success = result,
                    currentImageNode = null
                )
            }
        }
    }

    /**
     * Updates the search query in the UI state.
     *
     * This function updates the search query in the UI state with the provided [query].
     * If the query is empty, it clears the success result and sets the query and current image item to null.
     *
     * @param query The new search query string.
     */
    private fun updateQuery(query: String) {
        if (query.isEmpty()) {
            uiState.value = uiState.value.copy(
                success = emptyList(),
                query = null,
                currentImageNode = null
            )
        } else {
            uiState.value = uiState.value.copy(
                query = query,
                currentImageNode = null
            )
        }
    }

    /**
     * Fetches image search data based on the provided query string.
     *
     * This function initiates a data fetching process based on the provided [query].
     * It updates the UI state to indicate loading, performs the data fetching operation
     * using a use case, handles potential errors, and updates the UI state accordingly
     * upon receiving the search results.
     *
     * @param query The search query string.
     */
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun fetchDataByQuery(query: String?) {
        uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch {
            flowOf(query)
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    useCase.execute(formatQuery(query)).catch { error ->
                        handleEvent(SearchImageEvent.OnError(error.message.toString()))
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch { error ->
                    handleEvent(SearchImageEvent.OnError(error.message.toString()))
                }
                .collect {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        success = it,
                        currentImageNode = null
                    )
                }
        }
    }

    /**
     * Handles error state in the UI.
     *
     * This function updates the UI state to indicate an error state.
     * It sets isLoading to false, updates the error message, clears the success result,
     * and sets the current image item to null.
     *
     * @param error The error message.
     */
    private fun onError(error: String) {
        uiState.value = uiState.value.copy(
            isLoading = false,
            error = error,
            success = emptyList(),
            currentImageNode = null
        )
    }

    /**
     * Formats the search query.
     *
     * This function formats the provided [query] by returning null if it is empty,
     * or returning the original query if it is not empty.
     *
     * @param query The search query string.
     * @return The formatted search query string, or null if the original query is empty.
     */
    private fun formatQuery(query: String?) : String?{
        return if(query?.isEmpty() == true) null else query
    }

    /**
     * Loads initial image list.
     *
     * This function initiates the process of loading initial images by fetching image search data
     * with a null query parameter, which typically results in fetching default or trending images.
     */
    private fun loadInitialImages() {
        fetchDataByQuery(null)
    }

}
