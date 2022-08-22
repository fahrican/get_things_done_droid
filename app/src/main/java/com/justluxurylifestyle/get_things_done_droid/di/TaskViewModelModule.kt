package com.justluxurylifestyle.get_things_done_droid.di

import com.justluxurylifestyle.get_things_done_droid.ui.open_task.OpenTaskRepository
import com.justluxurylifestyle.get_things_done_droid.ui.open_task.OpenTaskRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
abstract class TaskViewModelModule {

    @Binds
    @ViewModelScoped
    abstract fun bindRepository(repo: OpenTaskRepositoryImpl): OpenTaskRepository
}