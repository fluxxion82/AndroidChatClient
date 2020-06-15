package ai.sterling.kchat.android.di

import ai.sterling.kchat.android.ui.main.MainActivity
import ai.sterling.kchat.android.ui.main.di.MainActivityInjectors
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [TestModule::class, TestInitializers::class])
abstract class TestInjectors {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityInjectors::class])
    internal abstract fun mainActivity(): MainActivity
}
