package ai.sterling.kchat.android.di

import ai.sterling.kchat.domain.di.DomainModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module(includes = [
    DomainModule::class
])
interface AggregatorModule
