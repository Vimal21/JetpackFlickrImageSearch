package com.flickrs.imagesearch.domain.entities.repositories

import com.flickrs.imagesearch.domain.entities.MappedImageItemModel

interface ImageSearchRepository {
    /**
     * Fetches image search data asynchronously based on the provided query string.
     *This function fetches search data asynchronously from a remote data source based on the provided query string.
     *
     * @param query The search query string.
     * @return A list of [MappedImageItemModel] representing the image search results.
     */
    suspend fun fetchSearchData(query: String?): List<MappedImageItemModel>
}