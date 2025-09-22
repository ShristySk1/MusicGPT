package com.lalas.musicgpt.di

import com.lalas.musicgpt.data.datasource.LocalMusicDataSource
import com.lalas.musicgpt.data.datasource.MusicDataSource
import com.lalas.musicgpt.data.repository.MusicRepository
import com.lalas.musicgpt.data.repository.MusicRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMusicDataSource(
        localMusicDataSource: LocalMusicDataSource
    ): MusicDataSource

    @Binds
    @Singleton
    abstract fun bindMusicRepository(
        musicRepositoryImpl: MusicRepositoryImpl
    ): MusicRepository
}
