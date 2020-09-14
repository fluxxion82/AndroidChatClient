package ai.sterling.kchat.domain.initialization

import ai.sterling.kchat.domain.base.CoroutinesContextFacade
import ai.sterling.kchat.domain.base.Usecase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.JvmSuppressWildcards

class InitializeApplication @Inject constructor(
    private val initializers: Set<@JvmSuppressWildcards AppInitializer>,
    private val contextFacade: CoroutinesContextFacade
) : Usecase<Unit, Unit> {

    override suspend fun invoke(param: Unit) = coroutineScope {
        initializers.forEach {
            launch(contextFacade.default) { it.initialize() }
        }
    }
}
