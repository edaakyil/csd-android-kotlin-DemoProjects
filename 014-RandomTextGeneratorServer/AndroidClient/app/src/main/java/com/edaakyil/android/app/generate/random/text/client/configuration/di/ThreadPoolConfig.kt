package com.edaakyil.android.app.generate.random.text.client.configuration.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ThreadPoolConfig {
    @Provides
    @Singleton
    fun provideThreadPool(): ExecutorService = Executors.newSingleThreadExecutor()
}