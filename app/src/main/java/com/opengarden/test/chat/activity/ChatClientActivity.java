package com.opengarden.test.chat.activity;

import android.os.Bundle;

import com.opengarden.test.chat.R;
import com.opengarden.test.chat.fragment.ChatClientFragment;
import com.opengarden.test.chat.fragment.EnterSettingsFragment;
import com.opengarden.test.chat.utils.Settings;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by salbury on 8/9/15.
 */
public class ChatClientActivity extends FragmentActivity implements EnterSettingsFragment.OnSettingsEntered {
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_client);

        Settings settings = Settings.getInstance();
        if(settings.getUsername().isEmpty()) {
            showEditDialog();
        } else {
            showChatConsole(savedInstanceState);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mContent != null) {
            getSupportFragmentManager().putFragment(outState, "mContent", mContent);
        }
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EnterSettingsFragment editNameDialog = EnterSettingsFragment.newInstance("Enter Username");
        editNameDialog.show(fm, "fragment_enter_username");
    }

    private void showChatConsole(Bundle savedInstanceState) {
        FragmentManager fm = getSupportFragmentManager();
        if(savedInstanceState != null) {
            mContent = fm.getFragment(savedInstanceState, "mContent");
        } else {
            mContent = ChatClientFragment.newInstance();
        }

        FragmentTransaction t = fm.beginTransaction();
        t.add(R.id.container, mContent);
        t.commitAllowingStateLoss();
    }

    @Override
    public void onSettingsEntered() {
        showChatConsole(null);
    }
}
