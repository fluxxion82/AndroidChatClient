package ai.sterling.kchat.android.database.dao

import ai.sterling.kchat.android.database.DatabaseManager
import ai.sterling.kchat.domain.chat.model.ChatMessage
import ai.sterling.kchat.domain.chat.persistence.ChatDao
import ai.sterling.kchat.domain.user.LoginUser
import ai.sterling.kchat.domain.user.persistences.UserDao
import ai.sterling.logger.KLogger
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import java.util.ArrayList
import javax.inject.Inject

class ChatMessageDao @Inject constructor(
    databaseManager: DatabaseManager,
    private val userDao: UserDao
): ChatDao {
    private var database: SQLiteDatabase = databaseManager.openDatabase()

    interface Table {
        companion object {
            const val TABLE_NAME = "ChatMessage"

            const val COLUMN_ID = "_id"
            const val COLUMN_USER_ID = "user_id"
            const val COLUMN_TYPE = "type"
            const val COLUMN_MESSAGE = "message"
            const val COLUMN_DATE = "date"

            const val COLUMN_USERNAME = "username"

            private const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + '(' +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_USER_ID + " LONG NOT NULL," +
                    COLUMN_TYPE + " INTEGER DEFAULT 0," +
                    COLUMN_MESSAGE + " TEXT NOT NULL," +
                    COLUMN_DATE + " TEXT," +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + AppUserDao.Table.TABLE_NAME + "(" + AppUserDao.Table.COLUMN_ID + ")" +
                    "UNIQUE(" + COLUMN_USER_ID + "," + COLUMN_MESSAGE + "," + COLUMN_DATE + ')'+ ");"

            fun onCreate(database: SQLiteDatabase) {
                database.execSQL(CREATE_TABLE)
            }

            @Suppress("MagicNumber")
            fun onUpgrade(
                database: SQLiteDatabase,
                oldVersion: Int,
                newVersion: Int
            ) {
                for (i in oldVersion until newVersion) {
                    when (i) {

                    }
                }
            }
        }
    }

    @Synchronized
    private fun cursorToData(cursor: Cursor): ChatMessage {
        return ChatMessage(
            cursor.getInt(cursor.getColumnIndex(Table.COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(Table.COLUMN_USER_ID)),
            cursor.getInt(cursor.getColumnIndex(Table.COLUMN_TYPE)),
            cursor.getString(cursor.getColumnIndex(Table.COLUMN_MESSAGE)),
            cursor.getLong(cursor.getColumnIndex(Table.COLUMN_DATE))
        )
    }

    @Synchronized
    private fun manageCursor(cursor: Cursor?): List<ChatMessage>? {
        val messageList = ArrayList<ChatMessage>()

        if (cursor != null) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val device = cursorToData(cursor)
                messageList.add(device)
                cursor.moveToNext()
            }
        }
        return messageList
    }

    override fun getAllMessages(): List<ChatMessage> {
        val query = "SELECT messages.*, user.${Table.COLUMN_USERNAME} FROM ${Table.TABLE_NAME} messages, ${AppUserDao.Table.TABLE_NAME} user " +
                "WHERE messages.${Table.COLUMN_USER_ID} = user.${AppUserDao.Table.COLUMN_ID};"

        val cursor = database.rawQuery(query, null)

        val deviceList = manageCursor(cursor)

        cursor?.close()

        return deviceList!!
    }

    override fun getChatMessage(id: Int): ChatMessage? {
        var message: ChatMessage? = null

        val query = "SELECT messages.*, user.${Table.COLUMN_USERNAME} FROM ${Table.TABLE_NAME} messages, ${AppUserDao.Table.TABLE_NAME} user " +
                "WHERE messages.${Table.COLUMN_USER_ID} = user.${AppUserDao.Table.COLUMN_ID} AND ${Table.COLUMN_ID}=?;"

        val bindArgs = arrayOf(id.toString())
        val cursor = database.rawQuery(query, bindArgs)

        val deviceList = manageCursor(cursor)
        if (deviceList?.size!! > 0) {
            message = deviceList[0]
        }

        cursor?.close()

        return message
    }

    override fun insertChatMessage(message: ChatMessage): Boolean {
        if (message.id < 0) {
            return false
        }
        var messageRow: Long = -1

        database.beginTransactionNonExclusive()

        try {
            val values = ContentValues()

            var user = userDao.getUserIdFromUsername(message.username)
            if (user == null) {
                userDao.insertUser(LoginUser.LoginData(message.username))
                user = userDao.getUserIdFromUsername(message.username)
            }

            values.put(Table.COLUMN_USER_ID, user)

            if (message.type >= 0) {
                values.put(Table.COLUMN_TYPE, message.type)
            }
            if (!message.message.isNullOrEmpty()) {
                values.put(Table.COLUMN_MESSAGE, message.message)
            }
            values.put(Table.COLUMN_DATE, message.date)

            try {
                messageRow = database.insertWithOnConflict(
                    Table.TABLE_NAME,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_IGNORE
                )

                if (messageRow.toInt() == -1) {
                    messageRow = database.update(
                        Table.TABLE_NAME,
                        values,
                        Table.COLUMN_ID + "=?",
                        arrayOf(message.id.toString())
                    ).toLong()
                }

                if (messageRow.toInt() > 0) {
                    database.setTransactionSuccessful()
                }
            } catch (exception: SQLiteException) {
                KLogger.w {
                    "SQLiteException while inserting Device:" + exception.message
                }
            }
        } catch (exception: SQLiteException) {
            KLogger.e(exception) {
                "SQLiteException while insert or update Device"
            }
        } finally {
            database.endTransaction()
        }

        return messageRow >= 0
    }

    override fun insertAllMessages(messageList: List<ChatMessage>): Boolean {
        messageList.forEach {
            val insert = insertChatMessage(it)

            if (!insert) {
                return false
            }
        }

        return true
    }

    override fun deleteAllMessages(): Boolean {
        database.beginTransactionNonExclusive()

        val result = database.delete(
            Table.TABLE_NAME,
            null,
            null
        )

        database.setTransactionSuccessful()
        database.endTransaction()

        return result > 0
    }
}
