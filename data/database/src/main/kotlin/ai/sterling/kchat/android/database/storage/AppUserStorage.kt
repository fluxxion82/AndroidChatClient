package ai.sterling.kchat.android.database.storage

import ai.sterling.kchat.android.database.DatabaseManager
import ai.sterling.kchat.domain.base.CoroutinesContextFacade
import ai.sterling.kchat.domain.user.LoginUser
import ai.sterling.kchat.domain.user.models.AppUser
import ai.sterling.kchat.domain.user.persistences.UserDao
import ai.sterling.kchat.domain.user.persistences.UserStorage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppUserStorage @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val userDao: UserDao,
    private val contextFacade: CoroutinesContextFacade
) : UserStorage {

    override suspend fun insertUser(user: LoginUser.LoginData) = coroutineScope {
        withContext(contextFacade.io) {
            databaseManager.openDatabase()
            userDao.insertUser(user)
            databaseManager.closeDatabase()
        }
    }

    override suspend fun getUser(id: Long): AppUser.LoggedIn? = coroutineScope {
        withContext(contextFacade.io) {
            databaseManager.openDatabase()
            val chatMessage = userDao.selectUser(id)
            databaseManager.closeDatabase()

            return@withContext chatMessage
        }
    }

    override suspend fun getUser(username: String): AppUser.LoggedIn? = coroutineScope {
        withContext(contextFacade.io) {
            databaseManager.openDatabase()
            val user = userDao.selectUser(username)
            databaseManager.closeDatabase()

            return@withContext user
        }
    }

    override suspend fun updateUser(user: AppUser.LoggedIn): Boolean = coroutineScope {
        withContext(contextFacade.io) {
            databaseManager.openDatabase()
            val result = userDao.insertOrUpdateUser(user)
            databaseManager.closeDatabase()
            return@withContext result
        }
    }

    override suspend fun deleteUser() = coroutineScope {
        withContext(contextFacade.io) {
            databaseManager.openDatabase()
            userDao.deleteUser()
            databaseManager.closeDatabase()
        }
    }
}