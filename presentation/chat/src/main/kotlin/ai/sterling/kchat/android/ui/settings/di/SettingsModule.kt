package ai.sterling.kchat.android.ui.settings.di

import ai.sterling.kchat.android.di.ViewModelKey
import ai.sterling.kchat.android.ui.settings.SettingsViewModel
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SettingsModule {

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun settingsViewModel(viewModel: SettingsViewModel): ViewModel
}