package ai.sterling.kchat.android.databinding.adapters

import ai.sterling.kchat.android.databinding.di.DaggerTestBindingComponent
import ai.sterling.kchat.domain.initialization.AppInitializer
import androidx.databinding.DataBindingUtil
import javax.inject.Inject

internal class BindingComponentInitializer @Inject constructor() : AppInitializer {

    override suspend fun initialize() {
        DataBindingUtil.setDefaultComponent(DaggerTestBindingComponent.create())
    }
}
