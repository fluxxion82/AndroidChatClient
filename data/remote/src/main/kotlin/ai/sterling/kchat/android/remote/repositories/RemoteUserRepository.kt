package ai.sterling.kchat.android.remote.repositories

import ai.sterling.kchat.android.api.KChatApiClient
import ai.sterling.kchat.domain.base.CoroutinesContextFacade
import ai.sterling.kchat.domain.base.model.Outcome
import ai.sterling.kchat.domain.exception.Failure
import ai.sterling.kchat.domain.user.LoginUser
import ai.sterling.kchat.domain.user.SignUpUser
import ai.sterling.kchat.domain.user.models.AppUser
import ai.sterling.kchat.domain.user.models.ProfileDetails
import ai.sterling.kchat.domain.user.persistences.UserPreferences
import ai.sterling.kchat.domain.user.persistences.UserRepository
import ai.sterling.kchat.domain.user.persistences.UserStorage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteUserRepository @Inject constructor(
    private val apiClient: KChatApiClient,
    private val userStorage: UserStorage,
    private val userPreferences: UserPreferences,
    private val contextFacade: CoroutinesContextFacade
): UserRepository {

    override suspend fun login(param: LoginUser.LoginData): Outcome<AppUser> = coroutineScope {
        withContext(contextFacade.io) {
            when (val outcome = apiClient.connect()) {
                is Outcome.Success -> {
                    println("connect success")
                    userStorage.insertUser(param)
                    Outcome.Success(userStorage.getUser(param.username!!)!!)
                }
                is Outcome.Error -> {
                    when (outcome.cause) {
                        else -> {
                            disconnect()
                            Outcome.Error("", Failure.ServerError(300))
                        }
                    }
                }
            }
        }
    }

    override suspend fun signup(param: SignUpUser.SignUpnData): Outcome<AppUser> = coroutineScope {
        withContext(contextFacade.io) {
            Outcome.Success(userStorage.getUser(param.username)!!)
        }
    }

    override suspend fun disconnect() = coroutineScope {
        withContext(contextFacade.io) {
            apiClient.disconnect()
        }
    }

    override suspend fun getUser(id: Long): Outcome<AppUser> = coroutineScope {
        withContext(contextFacade.io) {
            Outcome.Success(userStorage.getUser(id)!!)
        }
    }

    override suspend fun getUser(username: String): Outcome<AppUser> = coroutineScope {
        withContext(contextFacade.io) {
            Outcome.Success(userStorage.getUser(username)!!)
        }
    }

    override suspend fun getUserProfile(): Outcome<ProfileDetails> = coroutineScope {
        withContext(contextFacade.io) {
            val user = userStorage.getUser(userPreferences.getServerInfo().username!!)!!
            Outcome.Success(
                ProfileDetails(
                    user.username
                )
            )
        }
    }

    override suspend fun updateProfileDetails(updated: ProfileDetails) = coroutineScope {
        withContext(contextFacade.io) {
            when (val outcome = getUser(userPreferences.getServerInfo().username!!)) {
                is Outcome.Success -> {
                    when (val appUser = outcome.value) {
                        is AppUser.LoggedIn -> userStorage.updateUser(appUser)
                        else -> Unit
                    }
                }
                is Outcome.Error -> Unit
            }
            Unit
        }
    }
}