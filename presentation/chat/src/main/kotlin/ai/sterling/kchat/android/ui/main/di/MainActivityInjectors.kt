package ai.sterling.kchat.android.ui.main.di

import ai.sterling.kchat.android.di.FragmentScope
import ai.sterling.kchat.android.ui.chat.ChatFragment
import ai.sterling.kchat.android.ui.chat.di.ChatModule
import ai.sterling.kchat.android.ui.settings.EnterSettingsFragment
import ai.sterling.kchat.android.ui.settings.di.SettingsModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityInjectors {

    @FragmentScope
    @ContributesAndroidInjector(modules = [ChatModule::class])
    abstract fun chat(): ChatFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [SettingsModule::class])
    abstract fun serverInfo(): EnterSettingsFragment
}
