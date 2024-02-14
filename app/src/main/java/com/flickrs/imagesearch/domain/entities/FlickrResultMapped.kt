package com.flickrs.imagesearch.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MappedImageItemModel(
    val imageTitle : String,
    val imageLink : String,
    val publishedTime : String,
    val description : String,
    val author : String,
    val tags : String
): Parcelable
