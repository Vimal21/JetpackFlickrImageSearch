package com.flickrs.imagesearch.domain.usecases

import com.flickrs.imagesearch.domain.entities.MappedImageItemModel
import com.flickrs.imagesearch.domain.entities.repositories.ImageSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/*Image Search use case*/
class ImageSearchUseCase @Inject constructor(
    private val repository: ImageSearchRepository
) {

    /**
     * Executes a image search query asynchronously and emits the result as a Flow.
     *
     * This function executes a image search query asynchronously using the provided [query],
     * fetching image data from the repository. The search results are emitted as a Flow.
     *
     * @param query The search query string.
     * @return A Flow emitting a list of [MappedImageItemModel] representing the search results.
     */
    fun execute(query: String?): Flow<List<MappedImageItemModel>> = flow {
        emit(repository.fetchSearchData(query))
    }.flowOn(Dispatchers.IO)

}