package com.justluxurylifestyle.get_things_done_droid.di

import com.justluxurylifestyle.get_things_done_droid.remote_datasource.http_client.TaskApi
import com.justluxurylifestyle.get_things_done_droid.remote_datasource.http_client.TaskWebService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideTaskApiWebService(): TaskApi = TaskWebService.getTaskApiClient()
}