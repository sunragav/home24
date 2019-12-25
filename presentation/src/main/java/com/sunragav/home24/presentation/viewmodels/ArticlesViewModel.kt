package com.sunragav.home24.presentation.viewmodels

import androidx.databinding.ObservableField
import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.domain.models.RepositoryState
import com.sunragav.home24.domain.models.RepositoryState.Companion.DB_CLEARED
import com.sunragav.home24.domain.models.RepositoryState.Companion.DB_ERROR
import com.sunragav.home24.domain.models.RepositoryState.Companion.EMPTY
import com.sunragav.home24.domain.models.RepositoryStateRelay
import com.sunragav.home24.domain.qualifiers.Background
import com.sunragav.home24.domain.qualifiers.ReviewCount
import com.sunragav.home24.domain.usecases.ClearAllLikesAction
import com.sunragav.home24.domain.usecases.GetArticleAction
import com.sunragav.home24.domain.usecases.GetArticlesAction
import com.sunragav.home24.domain.usecases.UpdateArticleAction
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

typealias Callback = () -> Unit

open class ArticlesViewModel @Inject internal constructor(
    private val getArticlesAction: GetArticlesAction,
    private val updateArticleAction: UpdateArticleAction,
    private val clearAllLikesAction: ClearAllLikesAction,
    private val compositeDisposable: CompositeDisposable,
    private val repositoryStateRelay: RepositoryStateRelay,
    @Background private val background: Scheduler,
    @ReviewCount private val reviewCountStr: String
) : ViewModel(), CoroutineScope {
    companion object {
        private const val LIMIT = 40
    }

    val isLoading = ObservableField<Boolean>()
    val isReadyToReview = ObservableField<Boolean>()
    val canShowUndo = ObservableField<Boolean>()
    private val likesCount = ObservableField<Int>()
    val reviewText = ObservableField<String>()

    private val reviewCount: Int = reviewCountStr.toInt()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + viewModelJob
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val bgScope = Dispatchers.IO


    private var normalRequestParam = GetArticlesAction.Params(limit = LIMIT)
    private var favoritesRequestParam = GetArticlesAction.Params(limit = LIMIT, flagged = true)

    private val pagingConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(true)
        .setInitialLoadSizeHint(LIMIT)
        .setPageSize(LIMIT)
        .build()


    val articlesCount = MutableLiveData<Int>()

    val currentItem = MutableLiveData<Int>()

    val canNavigate = MutableLiveData<Boolean>()


    val articlesListSource: LiveData<PagedList<ArticleDomainEntity>>
        get() = Transformations.switchMap(pagedListMediator) { it }

    private val pagedListMediator = MediatorLiveData<LiveData<PagedList<ArticleDomainEntity>>>()
    private val filterRequestLiveData = MutableLiveData<GetArticlesAction.Params>()
    private lateinit var dataSourceFactory: DataSource.Factory<Int, ArticleDomainEntity>

    init {
        init()
        pagedListMediator.addSource(filterRequestLiveData) { param ->
            uiScope.launch {
                withContext(bgScope) {
                    with(getArticlesAction.buildUseCase(param)) {
                        dataSourceFactory = dataSource
                        pagedListMediator.postValue(
                            LivePagedListBuilder(dataSource, pagingConfig)
                                .setBoundaryCallback(boundaryCallback)
                                .build()
                        )
                    }
                }
            }
        }
        getModels()
    }


    fun init(){
        isReadyToReview.set(false)
        isLoading.set(true)
        canShowUndo.set(false)
        articlesCount.value = 0
        likesCount.set(0)
        currentItem.value = 0
        reviewText.set("0/$reviewCount")
        clearAllLikes {
            canNavigate.value = true
            repositoryStateRelay.relay.accept(EMPTY)
        }
    }


    fun getModels() {
        filterRequestLiveData.postValue(normalRequestParam)
    }

    fun getFavoites() {
        filterRequestLiveData.postValue(favoritesRequestParam)
    }

    fun clearAllLikes(postExecute: Callback? = null) {
        canNavigate.value = false
        clearAllLikesAction.buildUseCase()
            .doOnError {
                reportDBState(DB_ERROR)
                postExecute?.invoke()
            }
            .doOnComplete{
                reportDBState(DB_CLEARED)
                postExecute?.invoke()
            }
            .doOnSubscribe { compositeDisposable.add(it) }
            .doOnSuccess {
                reportDBState(DB_CLEARED)
                postExecute?.invoke()
            }.subscribe()
    }

    fun handleLikeDislike(
        articleDomainEntity: ArticleDomainEntity,
        liked: Boolean,
        postExecute: Callback? = null

    ) {
        if (isReadyToReview.get() == false) {
            handleIfAlreadyLiked(articleDomainEntity, liked) {
                val itemCount = articlesCount.value ?: 0
                val currentIndex = currentItem.value ?: 0
                if (currentIndex == reviewCount - 1 || currentIndex >= itemCount) {
                    isReadyToReview.set(true)
                    canShowUndo.set(false)
                }
                if (currentIndex < itemCount) {
                    canShowUndo.set(true)
                    postExecute?.invoke()
                }
            }

        }
    }


    private fun handleIfAlreadyLiked(
        articleDomainEntity: ArticleDomainEntity,
        liked: Boolean,
        executePostUpdate: Callback
    ) {
        val count = likesCount.get() ?: 0

        //If the item is already liked in db, the like count should not be incremented on like
        //In other words if the current like is already liked article don't increment the like count
        if (liked && articleDomainEntity.flagged.not()) {
            likesCount.set(count + 1)
        }
        //if the item is already liked in db, the like count should be decremented on dislike
        else if (count > 0 && liked.not() && articleDomainEntity.flagged) {
            likesCount.set(count - 1)
        }
        reviewText.set("${likesCount.get()}/$reviewCount")
        //update db only if the current like state and the db like state are different
        //navigate to next item only after db update
        if (liked != articleDomainEntity.flagged) {
            update(articleDomainEntity.copy(flagged = liked)) {
                executePostUpdate.invoke()
            }
        } else {
            executePostUpdate.invoke()
        }
    }

    private fun update(
        articleDomainEntity: ArticleDomainEntity,
        postExecute: Callback
    ) {
        updateArticleAction.buildUseCase(articleDomainEntity)
            .doOnError { reportDBState(DB_ERROR) }
            .doOnSubscribe { compositeDisposable.add(it) }
            .andThen {
                reportDBState(RepositoryState.DB_UPDATED)
                postExecute.invoke()
            }.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    private fun reportDBState(repositoryState: RepositoryState) {
        Observable.fromCallable {
            repositoryStateRelay.relay.accept(repositoryState)
        }.doOnSubscribe { compositeDisposable.add(it) }
            .retry(1)
            .subscribe()
    }


}