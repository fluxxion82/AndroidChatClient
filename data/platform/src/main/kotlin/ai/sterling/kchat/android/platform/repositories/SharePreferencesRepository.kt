package ai.sterling.kchat.android.platform.repositories

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext

internal abstract class SharePreferencesRepository(
    @ApplicationContext context: Context,
    prefsName: String
) {
    protected val sharedPreferences: SharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
}
