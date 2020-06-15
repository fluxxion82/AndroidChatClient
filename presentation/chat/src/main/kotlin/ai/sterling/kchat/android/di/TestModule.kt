package ai.sterling.kchat.android.di

import ai.sterling.kchat.R
import android.content.Context
import android.content.SharedPreferences
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class TestModule {
    @Provides
    @Singleton
    internal fun provideSharedPreferences(app: Context): SharedPreferences {
        return app.getSharedPreferences(
            app.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    internal fun provideBroadcastManager(context: Context): LocalBroadcastManager = LocalBroadcastManager.getInstance(context)
}
