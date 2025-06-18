package com.example.cryptotide.model.service.module

import com.example.cryptotide.model.service.AccountService
import com.example.cryptotide.model.service.impl.AccountServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindAccountService(impl: AccountServiceImpl): AccountService
}