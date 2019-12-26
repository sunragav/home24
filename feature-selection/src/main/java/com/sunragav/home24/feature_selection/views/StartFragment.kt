package com.sunragav.home24.feature_selection.views


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.sunragav.feature_selection.R
import kotlinx.android.synthetic.main.fragment_start.view.*


/**
 * A simple [Fragment] subclass.
 */
class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        view.btn_start.setOnClickListener {
            val action = StartFragmentDirections.actionStartFragmentToSelectionFragment()
            val navController = view.findNavController()
            navController.navigate(action)
        }
        return view
    }
}
