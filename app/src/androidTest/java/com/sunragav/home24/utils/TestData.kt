package com.sunragav.home24.utils

import com.sunragav.home24.remotedata.models.ArticleRemoteData
import com.sunragav.home24.remotedata.models.DataWrapper
import com.sunragav.home24.remotedata.models.Embedded
import com.sunragav.home24.remotedata.models.Media


const val image1 = "file:///android_asset/mattress.jpeg"
const val image2 = "file:///android_asset/bed.jpeg"


val media1 = Media(
    uri = image1
)

val media2 = Media(
    uri = image2
)
val article1 = ArticleRemoteData(
    sku = "123",
    media = listOf(media1),
    title = "Fake title 1"
)

val article2 = ArticleRemoteData(
    sku = "456",
    media = listOf(media2),
    title = "Fake title 2"
)


val embedded = Embedded(
    articles = listOf(article1, article2)
)

val dataWrapper = DataWrapper(
    _embedded = embedded
)



