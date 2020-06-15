package ai.sterling.kchat.android.di

import ai.sterling.kchat.android.databinding.adapters.BindingComponentInitializer
import ai.sterling.kchat.domain.initialization.AppInitializer
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
abstract class TestInitializers {

    @Binds
    @IntoSet
    internal abstract fun binding(binding: BindingComponentInitializer): AppInitializer
}
