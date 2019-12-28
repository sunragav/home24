package com.sunragav.home24.feature_selection.views


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
import androidx.viewpager2.widget.ViewPager2
import com.sunragav.feature_selection.R
import com.sunragav.feature_selection.databinding.FragmentSelectionBinding
import com.sunragav.home24.android_utils.animation.Viewpager2CustomDepthTransformation
import com.sunragav.home24.domain.models.RepositoryState
import com.sunragav.home24.domain.models.RepositoryState.Companion.CONNECTED
import com.sunragav.home24.domain.models.RepositoryState.Companion.DB_ERROR
import com.sunragav.home24.domain.models.RepositoryState.Companion.DISCONNECTED
import com.sunragav.home24.domain.models.RepositoryState.Companion.EMPTY
import com.sunragav.home24.domain.models.RepositoryState.Companion.ERROR
import com.sunragav.home24.domain.models.RepositoryState.Companion.LOADED
import com.sunragav.home24.domain.models.RepositoryState.Companion.LOADING
import com.sunragav.home24.domain.models.RepositoryState.Companion.UI_LOADED
import com.sunragav.home24.domain.models.RepositoryStateRelay
import com.sunragav.home24.feature_selection.mappers.ArticleUIModelMapper
import com.sunragav.home24.feature_selection.viewpager.adapter.PagedArticlesAdapter
import com.sunragav.home24.feature_selection.views.listeners.ClickListener
import com.sunragav.home24.presentation.factory.ArticlesViewModelFactory
import com.sunragav.home24.presentation.viewmodels.ArticlesViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import java.lang.ref.WeakReference
import javax.inject.Inject


class SelectionFragment : Fragment() {
    private var prevState: RepositoryState = EMPTY
    @Inject
    lateinit var viewModelFactory: ArticlesViewModelFactory

    @Inject
    lateinit var repositoryStateRelay: RepositoryStateRelay

    @Inject
    lateinit var disposable: CompositeDisposable

    private lateinit var viewModel: ArticlesViewModel

    private lateinit var pagedArticlesAdapter: PagedArticlesAdapter

    private lateinit var viewPager: ViewPager2

    private lateinit var binding: FragmentSelectionBinding

    @Inject
    lateinit var customDepthTransformation: Viewpager2CustomDepthTransformation

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            viewModel =
                ViewModelProviders.of(it, viewModelFactory).get(ArticlesViewModel::class.java)
        }
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_selection,
            container,
            false
        )
        viewPager = binding.vpArticles

        binding.viewModel = viewModel

        initViewPager()

        initAdapter()
        binding.clickListener = ClickListener(
            viewPagerRef = WeakReference(viewPager),
            viewModel = viewModel
        )

        initViewModel()

        startListeningToRepoState()


        return binding.root
    }

    private fun startListeningToRepoState() {
        val subscription = repositoryStateRelay.relay.subscribe {
            when (it) {
                LOADING -> viewModel.isLoading.set(true)
                LOADED -> {
                    viewModel.isLoading.set(false)
                }
                DISCONNECTED -> {
                    val count = pagedArticlesAdapter.currentList?.size ?: 0
                    if (count == 0) {
                        viewModel.canNavigate.value = false
                    }
                    Toast.makeText(
                        activity,
                        R.string.network_lost,
                        Toast.LENGTH_LONG
                    ).show()
                }
                RepositoryState.DB_CLEARED -> {
                    println("DB cleared successfully!!")
                }
                DB_ERROR -> {
                    println("DB error!!")
                    Toast.makeText(
                        activity,
                        R.string.db_error,
                        Toast.LENGTH_LONG
                    ).show()
                }
                CONNECTED -> {
                    if (prevState == DISCONNECTED) {
                        Toast.makeText(
                            activity,
                            R.string.network_regained,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    if (viewModel.isLoading.get() == true) {
                        viewModel.getModels()
                        viewModel.canNavigate.value = true
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
                    val count = pagedArticlesAdapter.currentList?.size ?: 0
                    if (count == 0) {
                        viewModel.isLoading.set(true)
                        viewModel.getModels()
                    }
                }
            }
            prevState = it

        }

        disposable.add(subscription)
    }

    private fun initViewModel() {

        viewModel.currentItem.observe(this, Observer {
            if (it == 0) viewModel.isUndoShowable.set(false)
        })

    }

    private fun initViewPager() {
        viewPager.isUserInputEnabled = false
        viewPager.currentItem = 0
        viewPager.setPageTransformer(customDepthTransformation)
    }

    private fun initAdapter() {
        pagedArticlesAdapter = PagedArticlesAdapter(
            ArticleUIModelMapper()
        )
        viewPager.adapter = pagedArticlesAdapter
        viewModel.articlesListSource.observe(this, Observer {
            if (it.size > 0) {
                repositoryStateRelay.relay.accept(UI_LOADED)
                viewModel.isLoading.set(false)
                pagedArticlesAdapter.submitList(it)
                viewModel.articlesCount.postValue(pagedArticlesAdapter.itemCount)
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
        viewModel.articlesListSource.removeObservers(this)
        viewModel.currentItem.removeObservers(this)
        disposable.dispose()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.clickListener = null
        binding.unbind()
    }

}
