package ai.sterling.kchat.domain.user.persistences

import ai.sterling.kchat.domain.user.LoginUser
import ai.sterling.kchat.domain.user.models.AppUser

interface UserDao {
    fun insertOrUpdateUser(user: AppUser.LoggedIn): Boolean
    fun insertUser(user: LoginUser.LoginData): Boolean
    fun selectUser(id: Long): AppUser.LoggedIn?
    fun selectUser(username: String): AppUser.LoggedIn?
    fun getUserIdFromUsername(username: String): Long?
    fun deleteUser()
}