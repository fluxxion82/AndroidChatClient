package ai.sterling.kchat.android.database.di

import ai.sterling.kchat.android.database.dao.AppUserDao
import ai.sterling.kchat.android.database.dao.ChatMessageDao
import ai.sterling.kchat.android.database.storage.AppUserStorage
import dagger.Binds
import dagger.Module
import ai.sterling.kchat.android.database.storage.ChatMessageStorage
import ai.sterling.kchat.domain.chat.persistence.ChatDao
import ai.sterling.kchat.domain.chat.persistence.ChatStorage
import ai.sterling.kchat.domain.user.persistences.UserDao
import ai.sterling.kchat.domain.user.persistences.UserStorage

@Module
abstract class StorageModule {
    @Binds
    internal abstract fun chatStorage(implementation: ChatMessageStorage): ChatStorage

    @Binds
    internal abstract fun userStorage(implementation: AppUserStorage): UserStorage

    @Binds
    internal abstract fun chatDao(implementation: ChatMessageDao): ChatDao

    @Binds
    internal abstract fun userDao(implementation: AppUserDao): UserDao
}
