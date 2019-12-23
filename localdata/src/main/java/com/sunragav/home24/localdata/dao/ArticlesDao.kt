package com.sunragav.home24.localdata.dao

import androidx.paging.DataSource
import androidx.room.*
import com.sunragav.home24.localdata.models.ArticleLocalData
import io.reactivex.Completable
import io.reactivex.Observable


@Dao
interface ArticlesDao {

    @Query(
        "SELECT * FROM articles"
    )
    fun getArticles(): DataSource.Factory<Int, ArticleLocalData>

    @Query(
        "SELECT * FROM articles where is_flagged = 1"
    )
    fun getFavorites(): DataSource.Factory<Int, ArticleLocalData>


    @Update
    fun update(articleLocalData: ArticleLocalData): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(articlesList: List<ArticleLocalData>): Completable

    @Query("SELECT * FROM articles WHERE sku = :id")
    fun getArticleById(id: String): Observable<ArticleLocalData>

    @Query("DELETE FROM articles")
    fun clearArticlesFromTable(): Completable
}