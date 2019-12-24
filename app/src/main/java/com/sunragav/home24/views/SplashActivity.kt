package com.sunragav.home24.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.sunragav.home24.R

import kotlinx.android.synthetic.main.activity_main.*

class SplashActivity : AppCompatActivity() {
    private val mHandler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mHandler.postDelayed({
            val i = Intent(applicationContext, FeatureActivity::class.java)
            startActivity(i)
            finish()
        }, 500)
    }
}
