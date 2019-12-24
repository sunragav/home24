package com.sunragav.home24.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sunragav.home24.di.qualifiers.ViewModelKey
import com.sunragav.home24.presentation.factory.ArticlesViewModelFactory
import com.sunragav.home24.presentation.viewmodels.ArticlesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [PresentationModule.Binders::class])
class PresentationModule {
    @Module
    interface Binders {

        @Binds
        fun bindsViewModelFactory(
            factory: ArticlesViewModelFactory
        ): ViewModelProvider.Factory

        @Binds
        @IntoMap
        @ViewModelKey(ArticlesViewModel::class)
        fun bindsArticlesViewModel(articlesViewModel: ArticlesViewModel): ViewModel
    }
}