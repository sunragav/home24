package com.sunragav.home24.presentation.viewmodels

import androidx.databinding.ObservableField
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.domain.models.RepositoryState
import com.sunragav.home24.domain.models.RepositoryState.Companion.DB_CLEARED
import com.sunragav.home24.domain.models.RepositoryState.Companion.DB_ERROR
import com.sunragav.home24.domain.models.RepositoryState.Companion.DB_UPDATED
import com.sunragav.home24.domain.models.RepositoryState.Companion.EMPTY
import com.sunragav.home24.domain.models.RepositoryStateRelay
import com.sunragav.home24.domain.qualifiers.ReviewCount
import com.sunragav.home24.domain.usecases.CleanAction
import com.sunragav.home24.domain.usecases.ClearAllLikesAction
import com.sunragav.home24.domain.usecases.GetArticlesAction
import com.sunragav.home24.domain.usecases.UpdateArticleAction
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

typealias Callback = () -> Unit

open class ArticlesViewModel @Inject internal constructor(
    private val getArticlesAction: GetArticlesAction,
    private val updateArticleAction: UpdateArticleAction,
    private val clearAllLikesAction: ClearAllLikesAction,
    private val cleanAction: CleanAction,
    private val repositoryStateRelay: RepositoryStateRelay,
    @ReviewCount private val reviewCountStr: String
) : ViewModel(), CoroutineScope {
    companion object {
        private const val LIMIT = 40
    }

    val isLoading = ObservableField<Boolean>()
    val isReadyToReview = ObservableField<Boolean>()
    val isUndoShowable = ObservableField<Boolean>()
    val reviewText = ObservableField<String>()
    val isListView = ObservableField<Boolean>()


    val articlesCount = MutableLiveData<Int>()

    val currentItem = MutableLiveData<Int>()

    val canNavigate = MutableLiveData<Boolean>()

    val articlesListSource: LiveData<PagedList<ArticleDomainEntity>>
        get() = Transformations.switchMap(pagedListMediator) { it }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + viewModelJob

    private lateinit var reviewedArticlesListSource: LiveData<PagedList<ArticleDomainEntity>>

    private var compositeDisposable: CompositeDisposable? = null

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val bgScope = Dispatchers.IO

    private val likesCount = ObservableField<Int>()
    private val reviewCount: Int = reviewCountStr.toInt()

    private var normalRequestParam = GetArticlesAction.Params(limit = LIMIT, reviewed = false)
    private var reviewedRequestParam = GetArticlesAction.Params(reviewed = true)
    private val filterRequestLiveData = MutableLiveData<GetArticlesAction.Params>()

    private val pagingConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(true)
        .setInitialLoadSizeHint(LIMIT)
        .setPageSize(LIMIT)
        .build()

    private val pagedListMediator = MediatorLiveData<LiveData<PagedList<ArticleDomainEntity>>>()

    init {
        pagedListMediator.addSource(filterRequestLiveData) { param ->
            uiScope.launch {
                withContext(bgScope) {
                    with(getArticlesAction.buildUseCase(param)) {
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

    fun init(postInitExecute: () -> Unit) {
        compositeDisposable = CompositeDisposable()
        isReadyToReview.set(false)
        isLoading.set(true)
        isUndoShowable.set(false)
        articlesCount.value = 0
        likesCount.set(0)
        currentItem.value = 0
        reviewText.set("0/$reviewCount")
        canNavigate.value = false
        isListView.set(true)
        clearAllLikes {
            canNavigate.value = true
            repositoryStateRelay.relay.accept(EMPTY)
            postInitExecute.invoke()
        }
    }


    fun getModels() {
        filterRequestLiveData.postValue(normalRequestParam)
    }

    fun getReviewed(): LiveData<PagedList<ArticleDomainEntity>> {
            reviewedArticlesListSource =
                with(getArticlesAction.buildUseCase(reviewedRequestParam)) {
                    LivePagedListBuilder(dataSource, pagingConfig)
                        .setBoundaryCallback(boundaryCallback)
                        .build()

                }
        return reviewedArticlesListSource
    }

    private fun clearAllLikes(executeTaskAfterLikesCleared: Callback? = null) {
        canNavigate.value = false
        clearAllLikesAction.buildUseCase()
            .doOnError { reportRepoState(DB_ERROR) }
            .doOnComplete { reportRepoState(DB_CLEARED) }
            .doFinally {
                reportRepoState(EMPTY)
                executeTaskAfterLikesCleared?.invoke()
            }
            .doOnSubscribe { compositeDisposable?.add(it) }
            .subscribe()
    }

    fun handleLikeDislike(
        articleDomainEntity: ArticleDomainEntity,
        liked: Boolean,
        executeAfterDBUpdate: Callback? = null

    ) {
        if (isReadyToReview.get() == false) {
            handleIfAlreadyLiked(articleDomainEntity, liked) {
                val itemCount = articlesCount.value ?: 0
                val currentIndex = currentItem.value ?: 0
                if (currentIndex == reviewCount - 1 || currentIndex >= itemCount) {
                    isReadyToReview.set(true)
                    isUndoShowable.set(false)
                } else if (currentIndex < itemCount) {
                    isUndoShowable.set(true)
                    executeAfterDBUpdate?.invoke()
                }
            }

        }
    }


    private fun handleIfAlreadyLiked(
        articleDomainEntity: ArticleDomainEntity,
        liked: Boolean,
        executeAfterDBUpdate: Callback
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

        update(articleDomainEntity.copy(flagged = liked, reviewed = true)) {
            executeAfterDBUpdate.invoke()
        }
    }

    private fun update(
        articleDomainEntity: ArticleDomainEntity,
        postExecute: Callback
    ) {
        updateArticleAction.buildUseCase(articleDomainEntity)
            .doOnSubscribe {
                compositeDisposable?.add(it)
            }
            .doFinally { postExecute.invoke() }
            .doOnError { reportRepoState(DB_ERROR) }
            .doOnComplete {
                reportRepoState(DB_UPDATED)
            }.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        clean()
    }


    fun clean() {
        cleanAction.execute()
        compositeDisposable?.dispose()
    }

    private fun reportRepoState(repositoryState: RepositoryState) {
        repositoryStateRelay.relay.accept(repositoryState)
    }
}