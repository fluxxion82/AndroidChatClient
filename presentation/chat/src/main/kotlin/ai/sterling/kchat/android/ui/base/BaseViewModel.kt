package ai.sterling.kchat.android.ui.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ai.sterling.kchat.domain.base.Usecase
import android.util.Log
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

abstract class BaseViewModel(parentJob: Job? = null) : ViewModel(), LifecycleObserver, CoroutineScope {

    protected val job = SupervisorJob(parentJob)

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    protected fun <TIn, TOut> Usecase<TIn, ReceiveChannel<TOut>>.toLiveData(param: TIn): LiveData<TOut?> {
        val liveData = MutableLiveData<TOut?>()
        launch {
            runCatching {
                invoke(param).consumeEach {
                    liveData.value = it
                }
            }.onFailure { exception ->
                Log.e(BaseViewModel::class.java.simpleName, exception.message)
            }
        }

        return liveData
    }

    protected fun <T> Usecase<Unit, ReceiveChannel<T>>.toLiveData(): LiveData<T?> =
        toLiveData(Unit)
}
