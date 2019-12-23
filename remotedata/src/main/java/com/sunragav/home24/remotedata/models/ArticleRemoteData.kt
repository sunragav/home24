package com.sunragav.home24.remotedata.models

data class DataWrapper<out T>(
    val embedded: Embedded<T>
)


data class Embedded<out T>(
    val articles: T
)

data class ArticleRemoteData(
    val sku:String,
    val media: List<Media?>?
)

data class Media(
    val uri:String
)


