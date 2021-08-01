package ai.sterling.kchat.android.database.di

import ai.sterling.kchat.android.database.DatabaseManager
import ai.sterling.kchat.android.database.KChatDatabaseHelper
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    internal fun provideDatabaseManager(database: KChatDatabaseHelper): DatabaseManager {
        DatabaseManager.initializeInstance(database)
        return DatabaseManager.getInstance()
    }

    @Provides
    @Singleton
    internal fun provideKChatDatabaseHelper(@ApplicationContext context: Context): KChatDatabaseHelper =
        KChatDatabaseHelper(context)
}
