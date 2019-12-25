package com.sunragav.home24.localdata.dao

import androidx.paging.DataSource
import androidx.room.*
import com.sunragav.home24.localdata.models.ArticleLocalData
import com.sunragav.home24.localdata.models.Request
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable


@Dao
interface RequestDao {

    @Query(
        "SELECT * FROM request limit 1"
    )
    fun getRequest(): Maybe<Request>

    @Update
    fun update(request: Request): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(request: Request): Completable


    @Query("Delete from request")
    fun clearRequest(): Completable
}