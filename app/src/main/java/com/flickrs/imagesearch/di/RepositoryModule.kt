package com.flickrs.imagesearch.di

import com.flickrs.imagesearch.data.repository.ImageSearchRepositoryImpl
import com.flickrs.imagesearch.domain.entities.repositories.ImageSearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindScheduleRepository(albumRepositoryImpl: ImageSearchRepositoryImpl): ImageSearchRepository
}