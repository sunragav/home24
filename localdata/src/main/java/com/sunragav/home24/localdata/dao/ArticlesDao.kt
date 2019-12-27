package com.sunragav.home24.localdata.dao

import androidx.paging.DataSource
import androidx.room.*
import com.sunragav.home24.localdata.models.ArticleLocalData
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable


@Dao
interface ArticlesDao {

    @Query(
        "SELECT * FROM articles"
    )
    fun getArticles(): DataSource.Factory<Int, ArticleLocalData>

    @Query(
        "SELECT * FROM articles where is_reviewed = 1"
    )
    fun getReviewedArticles(): DataSource.Factory<Int, ArticleLocalData>


    @Query(
        "SELECT * FROM articles where is_flagged = 1 and is_reviewed=1"
    )
    fun getFavorites(): DataSource.Factory<Int, ArticleLocalData>


    @Update
    fun update(articleLocalData: ArticleLocalData): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(articlesList: List<ArticleLocalData>): Completable

    @Query("SELECT * FROM articles WHERE sku = :id limit 1")
    fun getArticleById(id: String): Observable<ArticleLocalData>

    @Query("UPDATE articles set is_flagged = 0, is_reviewed =0")
    //@Query("DELETE FROM articles")
    fun clearLikesFromArticles():  Maybe<Int>
}