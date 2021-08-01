package ai.sterling.kchat.android.ui.main

import ai.sterling.kchat.android.ui.base.BaseViewModel
import ai.sterling.kchat.domain.app.ExitApp
import ai.sterling.kchat.domain.base.invoke
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val exitApp: ExitApp
) : BaseViewModel() {

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        launch {
            exitApp()
        }
    }
}