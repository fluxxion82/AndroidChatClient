package com.opengarden.test.chat.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.opengarden.test.chat.BuildConfig;
import com.opengarden.test.chat.R;
import com.opengarden.test.chat.database.ChatDatabaseHelper;
import com.opengarden.test.chat.model.ChatMessage;
import com.opengarden.test.chat.utils.Utils;
import com.opengarden.test.chat.utils.Settings;

import androidx.fragment.app.Fragment;

/**
 * Created by salbury on 8/9/15.
 */
public class ChatClientFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ChatClientFragment.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private EditText mMessageBoxView;
    private Button mSendButton;
    private TextView mChatHistoryView;
    private Socket mClient;
    private PrintWriter mPrintWriter;
    private BufferedReader mBufferedReader;
    private boolean mKeepGoing = true;
    private boolean mNeedToFlush = false;
    private ChatDatabaseHelper mDatabaseHelper;
    private Receiver mReceiver;

    //default IP address and port of the chat server
    private String CHAT_SERVER_IP = "10.0.0.63";
    private String USERNAME = "androidClient";
    private int CHAT_SERVER_PORT = 4444;

    public static ChatClientFragment newInstance() {
        ChatClientFragment frag = new ChatClientFragment();

        Bundle args = new Bundle();

        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String history = savedInstanceState.getString("chatHistory");
            if(history != null) {
                mChatHistoryView.setText("");
                mChatHistoryView.append(history);
            }
        }

        return inflater.inflate(R.layout.fragment_chat_client, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setRetainInstance(true);

        Settings settings = Settings.getInstance();
        USERNAME = settings.getUsername();
        if(!settings.getServerAddress().isEmpty()) {
            CHAT_SERVER_IP = settings.getServerAddress();
        }
        if(!settings.getServerPort().isEmpty()) {
            CHAT_SERVER_PORT = Integer.parseInt(settings.getServerPort());
        }

        Log.i(TAG, "username: " + USERNAME + " ip: " + CHAT_SERVER_IP + " port: " + CHAT_SERVER_PORT);

        mMessageBoxView = (EditText) view.findViewById(R.id.editText1);
        mSendButton = (Button) view.findViewById(R.id.button1);
        mChatHistoryView = (TextView) view.findViewById(R.id.textView1);

        mSendButton.setOnClickListener(this);
    }


    // Called after onCreateView
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            String history = savedInstanceState.getString("chatHistory");
            if(history != null) {
                mChatHistoryView.setText("");
                mChatHistoryView.append(history);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        ChatOperator chatOperator = new ChatOperator();
        chatOperator.execute();
    }

    @Override
    public void onPause() {
        super.onPause();

        disconnect();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("chatHistory", mChatHistoryView.getText().toString());
    }

    @Override
    public void onClick(View v) {
        if(mClient == null) {
            ChatOperator chatOperator = new ChatOperator();
            chatOperator.execute();
        }

        // create chat message object
        String message = mMessageBoxView.getText().toString();
        if(message.equalsIgnoreCase("logout")) {
            getActivity().finish(); // exit the app. logout message is sent on disconnect
            return;
        }

        ChatMessage msg = new ChatMessage(0, USERNAME, ChatMessage.MESSAGE, message, Calendar.getInstance().getTimeInMillis());

        List<ChatMessage> messageList = new ArrayList<>(1);
        messageList.add(msg);

        // If online, try clearing any queue, and then send the next message we just tried to send
        if(Utils.isOnline(getActivity()) && mClient != null) {
            final Sender messageSender = new Sender(messageList);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                messageSender.execute();
            }
        } else { // else, print to history and queue message
            mMessageBoxView.setText(""); // Clear the chat box
            mChatHistoryView.append("Not connected right now...message queued" + "\n");

            addMessageToQueue(messageList.get(0));
            mNeedToFlush = true;
        }

    }

    private class ChatOperator extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                mClient = new Socket(CHAT_SERVER_IP, CHAT_SERVER_PORT); // Creating the server socket.

                if (mClient != null) {
                    mKeepGoing = true;
                    mPrintWriter = new PrintWriter(mClient.getOutputStream(), true);
                    InputStreamReader inputStreamReader = new InputStreamReader(mClient.getInputStream());
                    mBufferedReader = new BufferedReader(inputStreamReader);

                    /*
                     * just to send the username to server so we can display connected
                     * I wanted to just give this to the Sender and get the flush of queued
                     * messages, but managing the order is tricky
                     */
                    ChatMessage msg = new ChatMessage(0, USERNAME, ChatMessage.LOGIN, "", Calendar.getInstance().getTimeInMillis());

                    mPrintWriter.write(new Gson().toJson(msg) + "\n");
                    mPrintWriter.flush();
                } else {
                    System.out.println("Server has not bean started on port " + CHAT_SERVER_PORT);
                }
            } catch (UnknownHostException e) {
                System.out.println("Faild to connect server " + CHAT_SERVER_IP + " on port " + CHAT_SERVER_PORT);
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Faild to connect server " + CHAT_SERVER_IP + " on port " + CHAT_SERVER_PORT);
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            if(mClient != null) {
                if(DEBUG) {
                    Log.i(TAG, "onPostExecute, mClient not null");
                }

                final Sender messageSender = new Sender(); // Initialize chat sender AsyncTask.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    messageSender.execute();
                }
            } else {
                mChatHistoryView.append("**Did not connect. Please exit the app and then come back to try to reconnect.**" + "\n");
            }
            Receiver receiver = new Receiver();
            receiver.execute();
        }

    }

    private class Receiver extends AsyncTask<Void, Void, Void> {
        private String message = "";

        @Override
        protected Void doInBackground(Void... params) {
            while (mKeepGoing) {
                try {
                    if(mBufferedReader != null && mBufferedReader.ready()) {
                        message = mBufferedReader.readLine();
                        publishProgress(null);
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            ChatMessage msg = new Gson().fromJson(message, ChatMessage.class);
            mChatHistoryView.append(msg.toString());
        }
    }

    /**
     * This AsyncTask sends the chat message through the output stream.
     */
    private class Sender extends AsyncTask<Void, Void, Void> {
        private StringBuilder mMessageBuilder;
        private List<ChatMessage> mMessageList;

        public Sender(List<ChatMessage> messageList) {
            mMessageList = messageList;
        }

        public Sender() {
            mMessageList= new ArrayList<>();
        }

        @Override
        protected synchronized Void doInBackground(Void... params) {
            List<ChatMessage> messageList = new ArrayList<>();

            if(mNeedToFlush) { // Get messages to flush
                mDatabaseHelper = new ChatDatabaseHelper(getActivity());

                SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
                mDatabaseHelper.startTransaction(db);
                try {
                    //Get messages
                    messageList = mDatabaseHelper.getAllMessages();

                    // clear of queue - will add back messages that don't get sent
                    mDatabaseHelper.deleteAllMessages();

                    mDatabaseHelper.setTransactionSuccessful(db);
                } finally {
                    mDatabaseHelper.endTransaction(db);
                    mDatabaseHelper.closeDB();
                }
            }

            messageList.addAll(mMessageList); //trying to keep order correct
            mMessageList.clear();

            if(messageList.size() > 0) {
                mMessageBuilder = new StringBuilder();

                try {
                    JsonElement element = new Gson().toJsonTree(messageList,
                            new TypeToken<List<ChatMessage>>() {
                            }.getType());
                    mMessageBuilder.append(element.getAsJsonArray());
                    mMessageBuilder.append("\n"); //automatic flushing is enabled on PrintWriter


                    mPrintWriter.write(mMessageBuilder.toString());
                    mPrintWriter.flush();

                } catch (Exception e) {
                    mNeedToFlush = true;
                }
            }

            if(mNeedToFlush) { // queue up messages again if they didn't go through
                if(mDatabaseHelper == null) {
                    mDatabaseHelper = new ChatDatabaseHelper(getActivity());
                }

                SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
                mDatabaseHelper.startTransaction(db);
                try {
                    for (ChatMessage message : mMessageList) {
                        mDatabaseHelper.addMessage(message);
                    }

                    mDatabaseHelper.setTransactionSuccessful(db);
                } finally {
                    mDatabaseHelper.endTransaction(db);
                    mDatabaseHelper.closeDB();
                }


                releaseDatabaseManager();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mMessageBoxView.setText(""); // Clear the chat box
        }
    }

    private synchronized void addMessageToQueue(ChatMessage message) {
        mDatabaseHelper = new ChatDatabaseHelper(getActivity());
        mDatabaseHelper.addMessage(message);
        mDatabaseHelper.closeDB();
    }

    public void releaseDatabaseManager() {
        if (mDatabaseHelper != null) {
            mDatabaseHelper.closeDB();
            mDatabaseHelper = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            disconnect();
        } finally {
            super.finalize();
        }
    }

    private void disconnect() {
        mKeepGoing = false;

        releaseDatabaseManager();

        try {
            if(mPrintWriter != null) {
                // Send logout message
                ChatMessage msg = new ChatMessage(0, USERNAME, ChatMessage.LOGOUT, "", Calendar.getInstance().getTimeInMillis());

                mPrintWriter.write(new Gson().toJson(msg) + "\n");//automatic flushing is enabled on PrintWriter
                mPrintWriter.flush();

                mPrintWriter.close();
            }
        } catch (Exception e) {
            //not much else to do
        }

        try {
            if(mClient != null) {
                mClient.close();
            }
        } catch (Exception e) {
            //not much else to do
        }

        try {
            if(mBufferedReader != null) {
                mBufferedReader.close();
            }
        } catch (Exception e) {
            //not much else to do
        }
    }
}
