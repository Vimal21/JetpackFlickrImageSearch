package com.flickrs.imagesearch.data.repository

import com.flickrs.imagesearch.data.remote.ApiService
import com.flickrs.imagesearch.data.entities.FlickrResponse
import com.flickrs.imagesearch.domain.entities.MappedImageItemModel
import javax.inject.Inject

/*Network data source to fetch data from server using api service client*/
class NetworkDataSource @Inject constructor(
    private val apiService: ApiService
) {

    /**
     * Fetches image search data asynchronously from the Flickr API based on the provided query string.
     * If no query is provided, the default search term "porcupine" is used.
     *
     * @param query The search query string. If null, the default search term "porcupine" is used.
     * @return A [FlickrResponse] object representing the response from the Flickr API.
     */
    suspend fun fetchSearchData(query: String?): FlickrResponse = apiService.getSearchResponse(query?: "porcupine")
}