package com.opengarden.test.chat.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.opengarden.test.chat.BuildConfig;
import com.opengarden.test.chat.model.ChatMessage;
import com.opengarden.test.chat.utils.Settings;
import com.opengarden.test.chat.utils.Utils;

/**
 * Created by salbury on 8/10/15.
 */
public class ChatDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = ChatDatabaseHelper.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String DATABASE_NAME = "internal.db";

    private static final int DATABASE_VERSION = 1;

    private Context mContext;

    // Table Names
    private static final String TABLE_OFFLINE_CHAT = "offline_chat_table";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DATE = "date";


    public ChatDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        internalCreate(db);
    }

    private void internalCreate(SQLiteDatabase db) {
        if (DEBUG)
            Log.d(TAG, "Creating internal database");
        db.execSQL("PRAGMA cache_size = 50000;");

        //using text for dates will allow sorting by the date column
        db.execSQL("CREATE TABLE " + TABLE_OFFLINE_CHAT + '(' + KEY_ID
                + " INTEGER PRIMARY KEY NOT NULL DEFAULT 0," + KEY_TYPE
                + " INTEGER DEFAULT 0," + KEY_MESSAGE + " TEXT NOT NULL,"
                + KEY_DATE + " TEXT," + "UNIQUE(" + KEY_ID + ')' + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINE_CHAT);

        // Create tables again
        onCreate(db);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        try {
            return super.getReadableDatabase();
        } catch (SQLiteException e) {
            File path = mContext.getDatabasePath(DATABASE_NAME);
            if (isDatabaseCorrupt(path)) {
                Utils.delete(path);
            }
            return super.getReadableDatabase();
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        try {
            return super.getWritableDatabase();
        } catch (SQLiteException e) {
            File path = mContext.getDatabasePath(DATABASE_NAME);
            if (isDatabaseCorrupt(path)) {
                if (Utils.delete(path)) {
                    Log.e(TAG, "Database failed to delete.");
                }
            }
            return super.getWritableDatabase();
        }
    }

    public boolean isDatabaseCorrupt(File path) {
        if (path.isDirectory()) {
            return true;
        }

        if (Utils.isDataParitionSpaceLow()) {
            return false;
        }

        File file = new File(mContext.getFilesDir(), "fscheck");
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            // definitely a file system issue
            return false;
        }
        file.delete();

        return true;
    }

    public void startTransaction(SQLiteDatabase db) {
        db.beginTransaction();
    }

    public void setTransactionSuccessful(SQLiteDatabase db) {
        db.setTransactionSuccessful();
    }

    public void endTransaction(SQLiteDatabase db) {
        db.endTransaction();
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    // Adding new message
    public void addMessage(ChatMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, message.getType());
        values.put(KEY_MESSAGE, message.getMessage());
        values.put(KEY_DATE, Utils.formatDateTimeForPersist(message.getDate()) );

        // Inserting Row
        db.insert(TABLE_OFFLINE_CHAT, null, values);
    }

    // Getting single message
    public ChatMessage getMessage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OFFLINE_CHAT, new String[] { KEY_ID,
                        KEY_TYPE, KEY_MESSAGE, KEY_DATE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ChatMessage message = new ChatMessage(Integer.parseInt( cursor.getString(0) ), Settings.getInstance().getUsername(), Integer.parseInt( cursor.getString(1) ),
                cursor.getString(2), Utils.getDateFromString(cursor.getString(3)) );

        // return message
        return message;
    }

    // Getting All ChatMessages
    public List<ChatMessage> getAllMessages() {
        List<ChatMessage> messageList = new ArrayList<ChatMessage>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_OFFLINE_CHAT + " ORDER BY +" + KEY_DATE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ChatMessage message = new ChatMessage();
                message.setID( Integer.parseInt( cursor.getString(0) ) );
                message.setUsername(Settings.getInstance().getUsername());
                message.setType(Integer.parseInt(cursor.getString(1)));
                message.setMessage(cursor.getString(2));
                message.setDate(Utils.getDateFromString(cursor.getString(3)));
                // Adding message to list
                messageList.add(message);
            } while (cursor.moveToNext());
        }

        // return message list
        return messageList;
    }

    // Updating single message
    public int updateMessage(ChatMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, message.getType());
        values.put(KEY_MESSAGE, message.getMessage());
        values.put(KEY_DATE, message.getDate());

        // updating row
        return db.update(TABLE_OFFLINE_CHAT, values, KEY_ID + " = ?",
                new String[] { String.valueOf(message.getID()) });
    }

    // Deleting single message
    public void deleteMessage(ChatMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OFFLINE_CHAT, KEY_ID + " = ?",
                new String[] { String.valueOf(message.getID()) });

    }

    // Deleting all messages
    public void deleteAllMessages() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OFFLINE_CHAT, null, null);
    }

    // Getting message Count
    public int getMessageCount() {
        String countQuery = "SELECT  * FROM " + TABLE_OFFLINE_CHAT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        // return count
        return cursor.getCount();
    }
}