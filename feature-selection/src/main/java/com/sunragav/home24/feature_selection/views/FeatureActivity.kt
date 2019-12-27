package com.sunragav.home24.feature_selection.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.ActivityNavigator
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.sunragav.feature_selection.R
import com.sunragav.home24.android_utils.ConnectivityMonitorLiveData
import com.sunragav.home24.domain.models.RepositoryState
import com.sunragav.home24.domain.models.RepositoryStateRelay
import dagger.android.AndroidInjection
import javax.inject.Inject

class FeatureActivity : AppCompatActivity() {

    @Inject
    lateinit var repositoryStateRelay: RepositoryStateRelay

    @Inject
    lateinit var connectivityState: ConnectivityMonitorLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature)
        AndroidInjection.inject(this)
        connectivityState.observe(this, Observer {
            if (it == true) connected() else disconnected()
        })

        Glide.get(this).setMemoryCategory(MemoryCategory.LOW)
    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }

    private fun connected() {
        repositoryStateRelay.relay.accept(RepositoryState.CONNECTED)

    }

    private fun disconnected() {
        repositoryStateRelay.relay.accept(RepositoryState.DISCONNECTED)
    }
}

