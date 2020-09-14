package ai.sterling.kchat.domain.user.persistences

import ai.sterling.kchat.domain.user.LoginUser
import ai.sterling.kchat.domain.user.models.AppUser

interface UserStorage {
    suspend fun insertUser(user: LoginUser.LoginData)
    suspend fun getUser(id: Long): AppUser.LoggedIn?
    suspend fun getUser(username: String): AppUser.LoggedIn?
    suspend fun updateUser(user: AppUser.LoggedIn): Boolean
    suspend fun deleteUser()
}