package com.sunragav.home24.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.sunragav.home24.R
import com.sunragav.home24.feature_selection.views.FeatureActivity

class SplashActivity : AppCompatActivity() {
    private val mHandler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mHandler.postDelayed({
            val i = Intent(applicationContext, FeatureActivity::class.java)
            startActivity(i)
            finish()
        }, 1000)
    }
}
