package com.sunragav.home24.data

import androidx.paging.PagedList
import com.sunragav.home24.data.contract.LocalRepository
import com.sunragav.home24.data.contract.RemoteRepository
import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.domain.models.RepositoryState
import com.sunragav.home24.domain.models.RepositoryStateRelay
import com.sunragav.home24.domain.qualifiers.Background
import com.sunragav.home24.domain.qualifiers.Foreground
import com.sunragav.home24.domain.repository.ArticlesRepository
import com.sunragav.home24.domain.usecases.GetArticlesAction
import com.sunragav.home24.domain.usecases.GetArticlesAction.GetArticlesActionResult
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticlesRepositoryImpl @Inject constructor(
    val disposable: CompositeDisposable,
    val localRepository: LocalRepository,
    val remoteRepository: RemoteRepository,
    val repositoryStateRelay: RepositoryStateRelay,
    @Foreground val foregroundScheduler: Scheduler,
    @Background val backgroundScheduler: Scheduler
) : ArticlesRepository {

    override fun getArticle(sku: String): Observable<ArticleDomainEntity> {
        return localRepository.getArticleById(sku)
    }

    override fun getArticles(query: GetArticlesAction.Params): GetArticlesActionResult {
        return GetArticlesActionResult(
            localRepository.getArticlesDatasourceFactory(query),
            ArticlesBoundaryCallback(query)
        )
    }

    override fun updateArticle(articleDomainEntity: ArticleDomainEntity): Completable {
        return localRepository.update(articleDomainEntity)
    }

    override fun clearAllLikes(): Maybe<Int> {
        return localRepository.clearAllLikes()
    }


    inner class ArticlesBoundaryCallback(
        private val query: GetArticlesAction.Params
    ) : PagedList.BoundaryCallback<ArticleDomainEntity>() {

        private var lastRequestedPage = 0


        private var isRequestInProgress = false

        override fun onZeroItemsLoaded() = requestAndSaveData()

        override fun onItemAtEndLoaded(itemAtEnd: ArticleDomainEntity) =
            requestAndSaveData()

        private fun requestAndSaveData() {

            if (isRequestInProgress || query.flagged || query.reviewed) return
            repositoryStateRelay.relay.accept(RepositoryState.LOADING)
            println("Requesting page$lastRequestedPage")
            isRequestInProgress = true
            updateRepoFromRemote()
        }

        private fun updateRepoFromRemote() {
            disposable.add(
                remoteRepository.getArticles(
                    lastRequestedPage,
                    query.limit
                ).subscribeOn(backgroundScheduler)
                    .observeOn(backgroundScheduler)
                    .doOnSubscribe { disposable.add(it) }
                    .subscribe(
                        { articles ->
                            println("Loaded page$lastRequestedPage offset:${lastRequestedPage * query.limit}")
                            insertDB(articles)
                        },
                        { error ->
                            setRepositoryState(RepositoryState.error(error.localizedMessage))
                        })
            )
        }

        private fun insertDB(articles: List<ArticleDomainEntity>) {
            localRepository.insert(articles)
                .subscribeOn(backgroundScheduler)
                .observeOn(backgroundScheduler)
                .doOnSubscribe { disposable.add(it) }
                .doOnError {
                    setRepositoryState(RepositoryState.DB_ERROR)
                }
                .andThen {
                    lastRequestedPage++
                    setRepositoryState(RepositoryState.DB_LOADED)
                }.subscribe()
        }

        private fun setRepositoryState(repositoryState: RepositoryState) {
            isRequestInProgress = false
            Observable.fromCallable {
                repositoryStateRelay.relay.accept(repositoryState)
            }.doOnSubscribe { disposable.add(it) }.subscribeOn(foregroundScheduler)
                .retry(1)
                .subscribe()
        }
    }
}