package ai.sterling.kchat.android.platform.repositories

import android.content.Context
import android.content.SharedPreferences

internal abstract class SharePreferencesRepository(
    context: Context,
    prefsName: String
) {
    protected val sharedPreferences: SharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
}
