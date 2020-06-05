package com.opengarden.test.chat.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.opengarden.test.chat.R;
import com.opengarden.test.chat.utils.Settings;

import androidx.fragment.app.DialogFragment;

/**
 * Created by salbury on 8/9/15.
 */
public class EnterSettingsFragment extends DialogFragment {
    private EditText mUsername;
    private EditText mServerAddy;
    private EditText mServerPort;
    private Button mFinish;
    private OnSettingsEntered mListener;

    public interface OnSettingsEntered {
        void onSettingsEntered();
    }

    public EnterSettingsFragment() {
        // Empty constructor required for DialogFragment
    }

    public static EnterSettingsFragment newInstance(String title) {
        EnterSettingsFragment frag = new EnterSettingsFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof OnSettingsEntered) {
            mListener = (OnSettingsEntered)activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_username_dialog, container);

        String title = getArguments().getString("title", "Enter Connection Info");
        getDialog().setTitle(title);


        mUsername = (EditText) view.findViewById(R.id.et_username);
        mUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mUsername.getText().toString().equals("")) {
                    mUsername.setHint(R.string.username_hint);
                } else {
                    mUsername.setHint("");
                }

            }
        });


        mServerAddy = (EditText) view.findViewById(R.id.et_server_ip);
        mServerAddy.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mServerAddy.getText().toString().equals("")) {
                    mServerAddy.setHint(R.string.server_address_hint);
                } else {
                    mServerAddy.setHint("");
                }

            }
        });

        mServerPort = (EditText) view.findViewById(R.id.et_server_port);
        mServerPort.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mServerPort.getText().toString().equals("")) {
                    mServerPort.setHint(R.string.server_port_hint);
                } else {
                    mServerPort.setHint("");
                }

            }
        });


        mFinish = (Button) view.findViewById(R.id.btn_finish);
        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUsername.getText().toString() != "") { //can leave the ip and port blank to use defaults
                    Settings settings = Settings.getInstance();
                    settings.setUsername(mUsername.getText().toString());
                    settings.setServerAddress(mServerAddy.getText().toString());
                    settings.setServerPort(mServerPort.getText().toString());

                    mListener.onSettingsEntered();
                    EnterSettingsFragment.this.dismiss();
                }
            }
        });

        // Show soft keyboard automatically
        mUsername.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }


}
