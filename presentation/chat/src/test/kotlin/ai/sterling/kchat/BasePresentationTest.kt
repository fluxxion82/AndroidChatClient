package ai.sterling.kchat

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule

abstract class BasePresentationTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @InternalCoroutinesApi
    @Before
    fun setUpMainDispatcher() {
        Dispatchers.setMain(TestUiContext)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}

@InternalCoroutinesApi
object TestUiContext : CoroutineDispatcher(), Delay {
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        continuation.resume(Unit)
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        block.run()
    }
}
