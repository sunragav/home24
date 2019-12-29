package com.sunragav.home24.feature_review.views


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunragav.feature_review.R
import com.sunragav.feature_review.databinding.FragmentReviewBinding
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

    private var binding: FragmentReviewBinding? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var listPagedArticlesAdapter: PagedArticlesAdapter

    @Inject
    lateinit var repositoryStateRelay: RepositoryStateRelay

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


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_review, container, false)
        activity?.let {
            viewModel =
                ViewModelProviders.of(it, viewModelFactory).get(ArticlesViewModel::class.java)
        }

        recyclerView = binding?.rvArticlesList!!
        val gridLayout = GridLayoutManager(activity, 2)
        val listLayout = GridLayoutManager(activity, 1)

        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        with(recyclerView) {
            addItemDecoration(decoration)
            setHasFixedSize(true)
            layoutManager = listLayout
            //  itemAnimator = null
        }
        startListeningToRepoState()

        initAdapter()
        initListPagedListObserver()

        binding?.clickHandler = ClickListener(viewModel, {
            with(recyclerView) {
                recycledViewPool.clear()
                scrollToPosition(0)
                layoutManager = listLayout
            }
        }) {
            with(recyclerView) {
                recycledViewPool.clear()
                scrollToPosition(0)
                layoutManager = gridLayout
            }
        }
        return binding?.root
    }

    private fun initAdapter() {
        val articleUIModelMapper = ArticleUIModelMapper()
        listPagedArticlesAdapter = PagedArticlesAdapter(
            articleUIModelMapper, viewModel
        )

        //By default adapter is List view layout adapter
        recyclerView.adapter = listPagedArticlesAdapter
    }

    private fun initListPagedListObserver() {
        viewModel.getReviewed().observe(this, Observer { pagedList ->
            if (pagedList.size > 0) {
                viewModel.isLoading.set(false)
                listPagedArticlesAdapter.submitList(pagedList)
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
                    println("DB error!")
                    Toast.makeText(
                        activity,
                        R.string.db_error,
                        Toast.LENGTH_LONG
                    ).show()
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
                    ).show()
                }
                EMPTY -> {
                    viewModel.isLoading.set(true)
                }
            }

        }
        disposable.add(subscription)
    }

    override fun onDestroyView() {
        recyclerView.addOnAttachStateChangeListener(object :
            View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) { // no-op
            }

            override fun onViewDetachedFromWindow(v: View) {
                listPagedArticlesAdapter.submitList(null)
                recyclerView.adapter = null
            }
        })
        binding?.clickHandler = null
        binding?.unbind()
        binding = null
        super.onDestroyView()
    }


    override fun onDetach() {
        super.onDetach()
        viewModel.getReviewed().removeObservers(this)
        disposable.dispose()
        viewModel.clean()

    }

}
