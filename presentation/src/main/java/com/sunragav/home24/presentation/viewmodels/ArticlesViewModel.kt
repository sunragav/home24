package com.sunragav.home24.presentation.viewmodels

import androidx.databinding.ObservableField
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.domain.usecases.GetArticlesAction

import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

open class HomeVM @Inject internal constructor(
    private val getArticlesAction: GetArticlesAction
) : ViewModel(), CoroutineScope {
    companion object {
        private const val LIMIT = 20
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + viewModelJob
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val bgScope = Dispatchers.IO


    private var requestParam = GetArticlesAction.Params(limit = LIMIT)
    val isLoading = ObservableField<Boolean>()


    private val pagingConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(true)
        .setInitialLoadSizeHint(LIMIT)
        .setPageSize(LIMIT)
        .build()


    val articlesListSource: LiveData<PagedList<ArticleDomainEntity>>
        get() = Transformations.switchMap(pagedListMediator) { it }

    private val pagedListMediator = MutableLiveData<LiveData<PagedList<ArticleDomainEntity>>>()


    init {
        load()
    }

    fun load() {
        uiScope.launch {
            withContext(bgScope) {
                with(getArticlesAction.buildUseCase(requestParam)) {
                    pagedListMediator.postValue(
                        LivePagedListBuilder(dataSource, pagingConfig)
                            .setBoundaryCallback(boundaryCallback)
                            .build()
                    )
                }
            }
        }
    }

}