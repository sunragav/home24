package com.sunragav.home24.utils

import androidx.test.espresso.IdlingResource
import com.sunragav.home24.domain.models.RepositoryState
import com.sunragav.home24.domain.models.RepositoryStateRelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.atomic.AtomicBoolean

class RepositoryStateIdlingResource(private val repositoryStateRelay: RepositoryStateRelay) :
    IdlingResource {
    private val isIdle = AtomicBoolean()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var resourceCallback: IdlingResource.ResourceCallback

    override fun getName() =
        "NetworkStateRelayIdlingResource"


    override fun isIdleNow(): Boolean {
        if (isIdle.get()) compositeDisposable.dispose()
        return isIdle.get()
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        resourceCallback = callback
        isIdle.getAndSet(false)
        compositeDisposable.add(repositoryStateRelay.relay.observeOn(AndroidSchedulers.mainThread()).subscribe {
            if (it == RepositoryState.UI_LOADED) {
                resourceCallback.onTransitionToIdle()
                isIdle.getAndSet(true)
            }
        })
    }
}