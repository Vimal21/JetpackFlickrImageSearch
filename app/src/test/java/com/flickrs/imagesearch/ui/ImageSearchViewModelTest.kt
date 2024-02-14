package com.flickrs.imagesearch.ui

import com.google.common.truth.Truth.assertThat
import com.flickrs.imagesearch.data.SampleFlickrProvider
import com.flickrs.imagesearch.domain.usecases.ImageSearchUseCase
import com.flickrs.imagesearch.ui.searchImage.SearchImageEvent
import com.flickrs.imagesearch.ui.searchImage.ImageSearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
internal class ImageSearchViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val useCase: ImageSearchUseCase = mock()
    private fun createViewModel(): ImageSearchViewModel = ImageSearchViewModel(useCase = useCase)

    private val viewModel: ImageSearchViewModel by lazy { createViewModel() }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {

        Dispatchers.resetMain()
    }

    @Test
    fun `SHOULD produce INITIAL state`() {
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.success).isEmpty()
        assertThat(state.error).isNull()
    }

    @Test
    fun `SHOULD produce NOT_EMPTY search query UPDATE state`() {
        viewModel.handleEvent(SearchImageEvent.QueryChanged("porcupine"))
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isTrue()
        assertThat(state.error).isNull()
        assertThat(state.success).isEmpty()
        assertThat(state.query).isEqualTo("porcupine")
    }

    @Test
    fun `SHOULD produce EMPTY search query UPDATE state`() {
        viewModel.handleEvent(SearchImageEvent.QueryChanged(""))
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isTrue()
        assertThat(state.error).isNull()
        assertThat(state.success).isEmpty()
        assertThat(state.query).isNull()
    }

    @Test
    fun `SHOULD produce LOADING state`() {
        val event = SearchImageEvent.InitiateSearch("porcupine")
        viewModel.handleEvent(event)
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isTrue()
        assertThat(state.error).isNull()
        assertThat(state.success).isEmpty()
    }

    @Test
    fun `SHOULD produce SUCESS state`() = runTest {
        whenever(useCase.execute("porcupine")).thenReturn(SampleFlickrProvider.returnFlickrResponseFlow())
        val event = SearchImageEvent.InitiateSearch("porcupine")
        viewModel.handleEvent(event)
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.success).isNotEmpty()
    }

    @Test
    fun `SHOULD produce ERROR state`() {
        viewModel.handleEvent(SearchImageEvent.OnError("Error Occurred"))
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNotEmpty()
        assertThat(state.success).isEmpty()
    }


}