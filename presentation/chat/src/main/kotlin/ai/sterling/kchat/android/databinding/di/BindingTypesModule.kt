package ai.sterling.kchat.android.databinding.di

import dagger.Binds
import dagger.Module
import ai.sterling.kchat.android.databinding.adapters.typecomponents.DateTimeComponent
import ai.sterling.kchat.android.databinding.adapters.typecomponents.JavaDateFormatter

@Module
abstract class BindingTypesModule {

    @Binds
    internal abstract fun date(implementation: JavaDateFormatter): DateTimeComponent
}
