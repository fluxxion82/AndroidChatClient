package ai.sterling.kchat.domain.user.persistences

import ai.sterling.kchat.domain.user.LoginUser
import ai.sterling.kchat.domain.user.SignUpUser
import ai.sterling.kchat.domain.user.models.AppUser
import ai.sterling.kchat.domain.user.models.ProfileDetails
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun login(param: LoginUser.LoginData): Flow<AppUser>

    fun signup(param: SignUpUser.SignUpnData): Flow<AppUser>

    suspend fun disconnect()

    fun getUser(username: String): Flow<AppUser>

    fun getUser(id: Long): Flow<AppUser>

    fun getUserProfile(): Flow<ProfileDetails>

    suspend fun updateProfileDetails(updated: ProfileDetails)
}
