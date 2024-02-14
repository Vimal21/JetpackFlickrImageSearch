package com.flickrs.imagesearch.data

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.flickrs.imagesearch.data.entities.ImageItem
import com.flickrs.imagesearch.data.entities.FlickrResponse
import com.flickrs.imagesearch.data.entities.Media
import com.flickrs.imagesearch.data.entities.toImageModel
import com.flickrs.imagesearch.domain.entities.MappedImageItemModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object SampleFlickrProvider : PreviewParameterProvider<ImageItem> {
    override val values: Sequence<ImageItem> = sequenceOf(
        ImageItem(
            title = "Bad Hair Day",
            media = Media(imageLink = "https://live.staticflickr.com/65535/53527900056_d746ce2d50_m.jpg"),
            publishedTime = "2024-02-14T02:06:56Z",
            description = " <p><a href=\"https:/www.flickr.com/people/nikondigifan\">Mike Gass</a> posted a photo:</p> <p><a href=\"https://www.flickr.com/photos/nikondigifan/53527900056/\" title=\"Bad Hair Day\"><img src=\"https://live.staticflickr.com/65535/53527900056_d746ce2d50_m.jpg\" width=\"240\" height=\"192\" alt=\"Bad Hair Day\" /></a></p> <p>Porcupine in eastern Washington.</p> ",
            author = "nobody@flickr.com (\"Mike Gass\")",
            tags = "porcupine animal mammal nature naturesfinest wildlife nikonz8 nikkor180600 nikon mikegassphotography",
        ),
        ImageItem(
            title = "Snack Time",
            media = Media(imageLink = "https://live.staticflickr.com/65535/53528101189_f87920b18d_m.jpg"),
            publishedTime = "2024-01-21T14:17:38-08:00",
            description = " <p><a href=\"https://www.flickr.com/people/airboy123/\">airboy123</a> posted a photo:</p> <p><a href=\"https://www.flickr.com/photos/airboy123/53528101189/\" title=\"Snack Time\"><img src=\"https://live.staticflickr.com/65535/53528101189_f87920b18d_m.jpg\" width=\"240\" height=\"240\" alt=\"Snack Time\" /></a></p> <p>Porcupine</p>",
            author = "nobody@flickr.com (\"airboy123\")",
            tags = "porcupine",
        )
    )

    /**
     * Returns a Flow emitting a list of [MappedImageItemModel] objects representing Flickr response items.
     *
     * This function transforms the values in the provided [values] collection into [MappedImageItemModel] objects
     * using the [toImageModel] extension function and emits them as a Flow.
     *
     * @return A Flow emitting a list of [MappedImageItemModel] objects.
     */
    fun returnFlickrResponseFlow(): Flow<List<MappedImageItemModel>> {
        return flowOf(values.toList().map { imageItem -> imageItem.toImageModel() })
    }

    /**
     * Returns a list of [MappedImageItemModel] objects representing mapped image items.
     *
     * This function transforms the values in the provided [values] collection into [MappedImageItemModel] objects
     * using the [toImageModel] extension function and returns them as a list.
     *
     * @return A list of [MappedImageItemModel] objects.
     */
    fun returnMappedResponse(): List<MappedImageItemModel> {
        return values.toList().map { imageItem -> imageItem.toImageModel() }
    }

    val jsonResponse= """
    {
  "title": "Recent Uploads tagged porcupine",
  "link": "https://www.flickr.com/photos/tags/porcupine/",
  "description": "",
  "modified": "2024-02-14T02:06:56Z",
  "generator": "https://www.flickr.com",
  "items": [
    {
      "title": "Bad Hair Day",
      "link": "https://www.flickr.com/photos/nikondigifan/53527900056/",
      "media": {
        "m": "https://live.staticflickr.com/65535/53527900056_d746ce2d50_m.jpg"
      },
      "date_taken": "2024-02-13T14:43:24-08:00",
      "description": " <p><a href=\"https://www.flickr.com/people/nikondigifan/\">Mike Gass</a> posted a photo:</p> <p><a href=\"https://www.flickr.com/photos/nikondigifan/53527900056/\" title=\"Bad Hair Day\"><img src=\"https://live.staticflickr.com/65535/53527900056_d746ce2d50_m.jpg\" width=\"240\" height=\"192\" alt=\"Bad Hair Day\" /></a></p> <p>Porcupine in eastern Washington.</p> ",
      "published": "2024-02-14T02:06:56Z",
      "author": "nobody@flickr.com (\"Mike Gass\")",
      "author_id": "12274241@N02",
      "tags": "porcupine animal mammal nature naturesfinest wildlife nikonz8 nikkor180600 nikon mikegassphotography"
    },
    {
      "title": "Snack Time",
      "link": "https://www.flickr.com/photos/airboy123/53528101189/",
      "media": {
        "m": "https://live.staticflickr.com/65535/53528101189_f87920b18d_m.jpg"
      },
      "date_taken": "2024-01-21T14:17:38-08:00",
      "description": " <p><a href=\"https://www.flickr.com/people/airboy123/\">airboy123</a> posted a photo:</p> <p><a href=\"https://www.flickr.com/photos/airboy123/53528101189/\" title=\"Snack Time\"><img src=\"https://live.staticflickr.com/65535/53528101189_f87920b18d_m.jpg\" width=\"240\" height=\"240\" alt=\"Snack Time\" /></a></p> <p>Porcupine</p> ",
      "published": "2024-02-14T00:38:53Z",
      "author": "nobody@flickr.com (\"airboy123\")",
      "author_id": "42478987@N07",
      "tags": "porcupine"
    }
  ]
}
""".trimIndent()

    val emptyListResponse = """
        {
    "total": 34713,
    "totalHits": 500,
    "hits": []
    }
    """.trimIndent()

    val failureResponse = """ {} """.trimIndent()

    /**
     * Converts a JSON string response to a [FlickrResponse] model object.
     *
     * @param response The JSON string response to be converted.
     * @return A [FlickrResponse] object parsed from the JSON string.
     * @throws JsonDataException if the JSON data is malformed or cannot be parsed.
     */
    fun convertJsonToModel(response:String): FlickrResponse {
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter: JsonAdapter<FlickrResponse> = moshi.adapter(FlickrResponse::class.java)
        return adapter.fromJson(response)!!
    }
}