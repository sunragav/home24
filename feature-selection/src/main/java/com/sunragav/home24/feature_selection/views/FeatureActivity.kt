package com.sunragav.home24.feature_selection.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ActivityNavigator
import com.sunragav.feature_selection.R
import com.sunragav.home24.domain.models.RepositoryStateRelay
import dagger.android.AndroidInjection
import javax.inject.Inject

class FeatureActivity : AppCompatActivity() {

    @Inject
    lateinit var repositoryStateRelay: RepositoryStateRelay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature)
        AndroidInjection.inject(this)
    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }
}

