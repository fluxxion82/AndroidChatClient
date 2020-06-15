package ai.sterling.kchat.android.database.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ai.sterling.kchat.android.database.DatabaseManager
import ai.sterling.kchat.android.database.KChatDatabaseHelper
import javax.inject.Singleton

@Module(includes = [StorageModule::class])
class DatabaseModule {

    @Provides
    @Singleton
    internal fun provideDatabaseManager(database: KChatDatabaseHelper): DatabaseManager {
        DatabaseManager.initializeInstance(database)
        return DatabaseManager.getInstance()
    }

    @Provides
    @Singleton
    internal fun provideKChatDatabaseHelper(context: Context): KChatDatabaseHelper =
        KChatDatabaseHelper(context)
}
