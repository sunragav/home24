package com.sunragav.home24.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.sunragav.home24.R
import kotlinx.android.synthetic.main.activity_feature.*

class FeatureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature)
        findNavController(R.id.nav_container).navigate(R.id.startFragment)
    }
}
