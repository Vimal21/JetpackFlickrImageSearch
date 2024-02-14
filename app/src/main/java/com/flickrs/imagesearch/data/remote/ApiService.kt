package com.flickrs.imagesearch.data.remote

import com.flickrs.imagesearch.data.entities.FlickrResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    /**
     * Get Image search response from the server
     */
    @GET("services/feeds/photos_public.gne")
    suspend fun getSearchResponse(
        @Query("tags") query: String,
        @Query("format") formatType: String? = "json",
        @Query("nojsoncallback") jsonCallback: Int? = 1
    ): FlickrResponse
}
