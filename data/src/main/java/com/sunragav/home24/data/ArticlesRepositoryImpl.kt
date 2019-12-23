package com.sunragav.home24.data

import androidx.paging.PagedList
import com.sunragav.home24.data.contract.LocalRepository
import com.sunragav.home24.data.contract.RemoteRepository
import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.domain.models.NetworkState
import com.sunragav.home24.domain.models.NetworkStateRelay
import com.sunragav.home24.domain.qualifiers.Background
import com.sunragav.home24.domain.qualifiers.Foreground
import com.sunragav.home24.domain.repository.ArticlesRepository
import com.sunragav.home24.domain.usecases.GetArticlesAction
import com.sunragav.home24.domain.usecases.GetArticlesAction.GetArticlesActionResult
import io.reactivex.Completable
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
    val networkStateRelay: NetworkStateRelay,
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

    inner class ArticlesBoundaryCallback(
        private val query: GetArticlesAction.Params
    ) : PagedList.BoundaryCallback<ArticleDomainEntity>() {

        private var lastRequestedPage = 0


        private var isRequestInProgress = false

        override fun onZeroItemsLoaded() = requestAndSaveData(query)

        override fun onItemAtEndLoaded(itemAtEnd: ArticleDomainEntity) =
            requestAndSaveData(query)

        private fun requestAndSaveData(query: GetArticlesAction.Params) {

            if (isRequestInProgress) {
                return
            }
            networkStateRelay.relay.accept(NetworkState.LOADING)
            println("Requesting page$lastRequestedPage")
            isRequestInProgress = true
            disposable.add(
                remoteRepository.getArticles(
                    lastRequestedPage,
                    query.limit
                ).subscribeOn(backgroundScheduler)
                    .observeOn(backgroundScheduler)
                    .doOnSubscribe{disposable.add(it)}
                    .retry(1)
                    .subscribe(
                    { articles ->
                        println("Loaded page$lastRequestedPage offset:${lastRequestedPage * query.limit}")
                        localRepository.insert(articles)
                            .subscribeOn(backgroundScheduler)
                            .observeOn(backgroundScheduler)
                            .subscribe {
                                lastRequestedPage++
                                isRequestInProgress = false
                                Observable.fromCallable {
                                    networkStateRelay.relay.accept(NetworkState.LOADED)
                                }.doOnSubscribe{disposable.add(it)}.subscribeOn(foregroundScheduler)
                                    .retry(1)
                                    .subscribe()
                            }
                    },
                    { error ->
                        Observable.fromCallable {
                            networkStateRelay.relay.accept(NetworkState.error(error.localizedMessage))
                        }.subscribeOn(foregroundScheduler)
                            .subscribe()
                        isRequestInProgress = false
                    })
            )

        }
    }
}