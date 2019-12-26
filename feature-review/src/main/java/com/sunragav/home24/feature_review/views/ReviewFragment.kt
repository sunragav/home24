package com.sunragav.home24.feature_review.views


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.sunragav.feature_review.R
import com.sunragav.feature_review.databinding.FragmentReviewBinding
import com.sunragav.home24.domain.models.RepositoryState
import com.sunragav.home24.domain.models.RepositoryState.Companion.CONNECTED
import com.sunragav.home24.domain.models.RepositoryState.Companion.DB_ERROR
import com.sunragav.home24.domain.models.RepositoryState.Companion.DISCONNECTED
import com.sunragav.home24.domain.models.RepositoryState.Companion.EMPTY
import com.sunragav.home24.domain.models.RepositoryState.Companion.ERROR
import com.sunragav.home24.domain.models.RepositoryState.Companion.LOADED
import com.sunragav.home24.domain.models.RepositoryState.Companion.LOADING
import com.sunragav.home24.domain.models.RepositoryStateRelay
import com.sunragav.home24.feature_review.adapter.PagedArticlesAdapter
import com.sunragav.home24.feature_review.listeners.ClickListener
import com.sunragav.home24.feature_review.mappers.ArticleUIModelMapper
import com.sunragav.home24.presentation.factory.ArticlesViewModelFactory
import com.sunragav.home24.presentation.viewmodels.ArticlesViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ReviewFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ArticlesViewModelFactory

    lateinit var viewModel: ArticlesViewModel

    lateinit var binding:FragmentReviewBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var pagedArticlesAdapter: PagedArticlesAdapter

    @Inject
    lateinit var repositoryStateRelay:RepositoryStateRelay

    @Inject
    lateinit var disposable: CompositeDisposable

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_review,container,false)
        activity?.let {
            viewModel =
                ViewModelProviders.of(it, viewModelFactory).get(ArticlesViewModel::class.java)
        }

        recyclerView = binding.rvArticlesList

        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        binding.rvArticlesList.addItemDecoration(decoration)
        binding.rvArticlesList.setHasFixedSize(true)
        binding.rvArticlesList.layoutManager = GridLayoutManager(activity, 1)
        initViewModel()

        initAdapter()
        startListeningToRepoState()



        binding.clickHandler = ClickListener(viewModel,{
            binding.rvArticlesList.layoutManager = GridLayoutManager(activity, 1)

        }){
            binding.rvArticlesList.layoutManager = GridLayoutManager(activity, 2)
        }
        return binding.root
    }

    private fun initViewModel() {
        repositoryStateRelay.relay.accept(EMPTY)
    }



    private fun initAdapter() {
        pagedArticlesAdapter = PagedArticlesAdapter(
            ArticleUIModelMapper()
        )
        recyclerView.adapter = pagedArticlesAdapter
        viewModel.articlesListSource.observe(this, Observer {
            if (it.size > 0) {
                repositoryStateRelay.relay.accept(RepositoryState.UI_LOADED)
                viewModel.isLoading.set(false)
                pagedArticlesAdapter.submitList(it)
                viewModel.articlesCount.postValue(pagedArticlesAdapter.itemCount)
            }
        })
    }


    private fun startListeningToRepoState() {
        val subscription = repositoryStateRelay.relay.subscribe {
            when (it) {
                LOADING -> viewModel.isLoading.set(true)
                LOADED -> {
                    viewModel.isLoading.set(false)
                }
                DISCONNECTED -> {
                    Toast.makeText(
                        activity,
                        R.string.network_lost,
                        Toast.LENGTH_LONG
                    )
                }
                DB_ERROR -> {
                    Toast.makeText(
                        activity,
                        R.string.network_lost,
                        Toast.LENGTH_LONG
                    )
                }
                CONNECTED -> {
                    if (viewModel.isLoading.get() == true) {
                        viewModel.getModels()
                    }
                }
                ERROR -> {
                    viewModel.isLoading.set(false)
                    Toast.makeText(
                        activity,
                        R.string.network_error,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                EMPTY -> {
                    val count = pagedArticlesAdapter.currentList?.size ?: 0
                    if (count == 0) {
                        viewModel.isLoading.set(true)
                        viewModel.getFavoites()
                    }
                }
            }

        }
        disposable.add(subscription)
    }

    override fun onDetach() {
        super.onDetach()
        viewModel.articlesListSource.removeObservers(this)
        disposable.dispose()
    }

}
