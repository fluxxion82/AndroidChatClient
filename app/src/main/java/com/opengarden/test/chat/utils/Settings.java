package com.opengarden.test.chat.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by salbury on 8/9/15.
 */
public class Settings {
    private final static String Username = "chat_username";
    private final static String ServerAddress = "server_addy";
    private final static String ServerPort = "server_port";

    private SharedPreferences mPreferences;
    private static Settings mChatSettings = null;

    private Settings(Context context) {
        mPreferences = context.getSharedPreferences(
                "com.opengarden_chat_test_prefs", Context.MODE_PRIVATE);
    }

    public static final Settings getInstance() {
        return mChatSettings;
    }

    public static final Settings create(Application app) {
        if (mChatSettings == null) {
            mChatSettings = new Settings(app);
        }

        return mChatSettings;
    }

    public String getUsername() {
        return mPreferences.getString(Username, "");
    }

    public String getServerAddress() {
        return mPreferences.getString(ServerAddress, "");
    }

    public String getServerPort() {
        return mPreferences.getString(ServerPort, "");
    }

    public void setServerPort(String address) {
        commitPrefs(ServerPort, address);
    }

    public void setServerAddress(String address) {
        commitPrefs(ServerAddress, address);
    }

    public void setUsername(String username) {
        commitPrefs(Username, username);
    }

    private void commitPrefs(String prefName, String prefValue) {
        mPreferences.edit().putString(prefName, prefValue).commit();
    }
}
