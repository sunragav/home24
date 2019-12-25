package com.sunragav.home24.feature_selection.views


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.ActivityNavigator
import androidx.navigation.findNavController
import com.sunragav.feature_selection.R
import com.sunragav.home24.domain.models.RepositoryState
import com.sunragav.home24.domain.models.RepositoryStateRelay
import com.sunragav.home24.presentation.factory.ArticlesViewModelFactory
import com.sunragav.home24.presentation.viewmodels.ArticlesViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_start.view.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class StartFragment : Fragment() {
    @Inject
    lateinit var repositoryStateRelay: RepositoryStateRelay

    @Inject
    lateinit var viewModelFactory: ArticlesViewModelFactory



    lateinit var viewModel: ArticlesViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ArticlesViewModel::class.java)
        view.btn_start.setOnClickListener {
          //  viewModel.clearAllLikes{
                val action = StartFragmentDirections.actionStartFragmentToSelectionFragment()
                val navController =view.findNavController()
                navController.navigate(action)
               // repositoryStateRelay.relay.accept(RepositoryState.EMPTY)
                //navController?.popBackStack(R.id.startFragment,true)
           // }
        }
        return view
    }
}
