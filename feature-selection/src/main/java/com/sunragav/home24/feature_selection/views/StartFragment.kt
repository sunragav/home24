package com.sunragav.home24.feature_selection.views


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.sunragav.feature_selection.R
import com.sunragav.home24.presentation.factory.ArticlesViewModelFactory
import com.sunragav.home24.presentation.viewmodels.ArticlesViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_start.view.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class StartFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ArticlesViewModelFactory

    private lateinit var viewModel: ArticlesViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_start, container, false)

        activity?.let {
            viewModel =
                ViewModelProviders.of(it, viewModelFactory).get(ArticlesViewModel::class.java)
        }

        view.btn_start.setOnClickListener {
            val action = StartFragmentDirections.actionStartFragmentToSelectionFragment()
            val navController = view.findNavController()
            viewModel.init() {
                navController.navigate(action)
            }

        }
        return view
    }
}
