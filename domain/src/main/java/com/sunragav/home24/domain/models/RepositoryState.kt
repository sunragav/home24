package com.sunragav.home24.domain.models

import com.jakewharton.rxrelay2.BehaviorRelay
import javax.inject.Inject
import javax.inject.Singleton

data class RepositoryState(
    val status: Status,
    val msg: String? = null
) {

    enum class Status {
        RUNNING,
        SUCCESS_LOADED, // New
        SUCCESS_EMPTY, // New
        FAILED,
        DISCONNECTED,
        CONNECTED,
        DB_LOADED,
        UI_LOADED,
        DB_UPDATE,
        DB_ERROR,
        DB_CLEARED
    }

    companion object {

        val EMPTY = RepositoryState(Status.SUCCESS_EMPTY) // New
        val LOADED = RepositoryState(Status.SUCCESS_LOADED) // New
        val LOADING = RepositoryState(Status.RUNNING)
        val ERROR = RepositoryState(Status.FAILED)
        val CONNECTED = RepositoryState(Status.CONNECTED)
        val DISCONNECTED = RepositoryState(Status.DISCONNECTED)
        val DB_LOADED = RepositoryState(Status.DB_LOADED)
        val UI_LOADED = RepositoryState(Status.UI_LOADED)
        val DB_UPDATED = RepositoryState(Status.DB_UPDATE)
        val DB_CLEARED = RepositoryState(Status.DB_CLEARED)
        val DB_ERROR = RepositoryState(Status.DB_ERROR)
        fun error(msg: String?) = RepositoryState(Status.FAILED, msg)
    }
}

@Singleton
class RepositoryStateRelay @Inject constructor() {
    val relay: BehaviorRelay<RepositoryState> = BehaviorRelay.create()
}