package com.opengarden.test.chat;

import android.app.Application;
import android.util.Log;

import com.opengarden.test.chat.database.ChatDatabaseHelper;
import com.opengarden.test.chat.utils.Settings;

/**
 * Created by salbury on 8/9/15.
 */
public class ChatApplication extends Application {
    private final static String TAG = "ChatApplication";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static ChatApplication mSingleton;
    private Settings mSettings;

    public static ChatApplication getInstance() {
        return mSingleton;
    }

    @Override
    public void onCreate() {
        if (DEBUG)
            Log.i(TAG, "Application onCreate");
        super.onCreate();
        mSingleton = this;
        mSettings = Settings.create(this);

        // I want to make sure the db is created before we start trying to do
        // anything with it
        ChatDatabaseHelper db = new ChatDatabaseHelper(this);
    }

    @Override
    public void onTerminate() {
        if (DEBUG)
            Log.i(TAG, "Application onTerminate");
        super.onTerminate();
    }

    public Settings getSettings() {
        return mSettings;
    }

}
