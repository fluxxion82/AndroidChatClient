package ai.sterling.kchat.android.remote.repositories

import ai.sterling.kchat.android.api.KChatApiClient
import ai.sterling.kchat.domain.base.CoroutinesContextFacade
import ai.sterling.kchat.domain.user.LoginUser
import ai.sterling.kchat.domain.user.SignUpUser
import ai.sterling.kchat.domain.user.models.AppUser
import ai.sterling.kchat.domain.user.models.ProfileDetails
import ai.sterling.kchat.domain.user.persistences.UserPreferences
import ai.sterling.kchat.domain.user.persistences.UserRepository
import ai.sterling.kchat.domain.user.persistences.UserStorage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteUserRepository @Inject constructor(
    private val apiClient: KChatApiClient,
    private val userStorage: UserStorage,
    private val userPreferences: UserPreferences,
    private val contextFacade: CoroutinesContextFacade
): UserRepository {

    override fun login(param: LoginUser.LoginData): Flow<AppUser.LoggedIn> = channelFlow {
        userStorage.insertUser(param)
        send(userStorage.getUser(param.username)!!)
    }.flowOn(contextFacade.io)

    override fun signup(param: SignUpUser.SignUpnData): Flow<AppUser.LoggedIn> = channelFlow {
        send(userStorage.getUser(param.username)!!)
    }.flowOn(contextFacade.io)

    override suspend fun disconnect() = coroutineScope {
        withContext(contextFacade.io) {
            apiClient.disconnect()
        }
    }

    override fun getUser(id: Long): Flow<AppUser> = channelFlow {
        send(userStorage.getUser(id)!!)
    }.flowOn(contextFacade.io)

    override fun getUser(username: String): Flow<AppUser> = channelFlow {
        send(userStorage.getUser(username)!!)
    }.flowOn(contextFacade.io)

    override fun getUserProfile(): Flow<ProfileDetails> = channelFlow {
        val user = userStorage.getUser(userPreferences.getServerInfo().username)!!
        send(ProfileDetails(
            user.username
        ))
    }.flowOn(contextFacade.io)

    override suspend fun updateProfileDetails(updated: ProfileDetails) = coroutineScope {
        withContext(contextFacade.io) {
            getUser(userPreferences.getServerInfo().username).collect {
                when (it) {
                    is AppUser.LoggedIn -> userStorage.updateUser(it)
                    else -> Unit
                }
            }
        }
    }
}