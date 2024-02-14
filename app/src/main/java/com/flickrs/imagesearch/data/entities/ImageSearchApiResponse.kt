package com.flickrs.imagesearch.data.entities

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
data class FlickrResponse(
	@Json(name = "title")
	val title: String?= null,
	@Json(name="items")
	val imageItemList: List<ImageItem> = listOf()
)

@Parcelize
data class ImageItem(
	@Json(name="title")
	val title : String? = null,
	@Json(name="media")
	val media : Media? = null,
	@Json(name="published")
	val publishedTime : String? = null,
	@Json(name="description")
	val description : String? = null,
	@Json(name="author")
	val author : String? = null,
	@Json(name="tags")
	val tags : String? = null,
):Parcelable

@Parcelize
data class Media(
	@Json(name = "m")
	val imageLink : String? = null
): Parcelable
