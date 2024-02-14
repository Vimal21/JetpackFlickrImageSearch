package com.flickrs.imagesearch.data.entities

import com.flickrs.imagesearch.domain.entities.MappedImageItemModel


fun ImageItem.toImageModel() = MappedImageItemModel(
    imageTitle = title ?: "",
    imageLink = media?.imageLink ?: "",
    publishedTime = publishedTime ?: "",
    description = description ?: "",
    author = author ?: "",
    tags = tags ?: ""
)