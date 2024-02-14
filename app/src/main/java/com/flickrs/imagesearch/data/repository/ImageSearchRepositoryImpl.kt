package com.flickrs.imagesearch.data.repository

import com.flickrs.imagesearch.data.entities.toImageModel
import com.flickrs.imagesearch.domain.entities.MappedImageItemModel
import com.flickrs.imagesearch.domain.entities.repositories.ImageSearchRepository
import javax.inject.Inject

class ImageSearchRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource
) : ImageSearchRepository {

    /**
     * Fetches image search data asynchronously based on the query string.
     *
     * @param query The search query string.
     * @return A list of [MappedImageItemModel] representing the search results.
     * @throws IllegalStateException if the fetched image item list is empty.
     * @throws Exception if an error occurs during the fetching process.
     */
    override suspend fun fetchSearchData(query: String?): List<MappedImageItemModel> {
        return try {
            val result = networkDataSource.fetchSearchData(query = query)

            if(result.imageItemList.isEmpty()){
                throw IllegalStateException("Empty product list")
            }
           result.imageItemList.map {
                 it.toImageModel()
             }
        } catch (e: Exception) {
            throw e
        }
    }
}