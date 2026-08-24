package com.evalorithm.di

import com.evalorithm.data.repository.AuthRepository
import com.evalorithm.data.repository.AuthRepositoryImpl
import com.evalorithm.data.repository.MainRepository
import com.evalorithm.data.repository.MainRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindMainRepository(impl: MainRepositoryImpl): MainRepository
}
