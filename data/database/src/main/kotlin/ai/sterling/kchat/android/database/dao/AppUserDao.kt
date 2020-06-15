package ai.sterling.kchat.android.database.dao

import ai.sterling.kchat.android.database.DatabaseManager
import ai.sterling.kchat.domain.user.LoginUser
import ai.sterling.kchat.domain.user.models.AppUser
import ai.sterling.kchat.domain.user.persistences.UserDao
import ai.sterling.logger.KLogger
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.soywiz.klock.DateTime
import javax.inject.Inject

class AppUserDao @Inject constructor(
    databaseManager: DatabaseManager
): UserDao {
    private var database: SQLiteDatabase = databaseManager.openDatabase()
    interface Table {
        companion object {
            const val TABLE_NAME = "User"

            const val COLUMN_ID = "_id"
            const val COLUMN_USERNAME = "username"
            const val COLUMN_CREATED_AT = "created_at"

            private const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + '(' +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_USERNAME + " TEXT NOT NULL," +
                    COLUMN_CREATED_AT + " LONG," +
                    "UNIQUE(" + COLUMN_ID + ')'+ ");"

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

    override fun insertOrUpdateUser(user: AppUser.LoggedIn): Boolean {
        var userRow: Long = -1

        database.beginTransactionNonExclusive()

        try {
            val values = ContentValues()

            if (user.username.isNullOrEmpty()) {
                values.put(Table.COLUMN_USERNAME, user.username)
            }

            values.put(Table.COLUMN_CREATED_AT, DateTime.now().unixMillisLong)

            try {
                userRow = database.insertWithOnConflict(
                    Table.TABLE_NAME,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_IGNORE
                )

                if (userRow.toInt() == -1) {
                    userRow = database.update(
                        Table.TABLE_NAME,
                        values,
                        Table.COLUMN_ID + "=?",
                        arrayOf(user.id .toString())
                    ).toLong()
                }

                if (userRow.toInt() > 0) {
                    database.setTransactionSuccessful()
                }
            } catch (exception: SQLiteException) {
                KLogger.e(exception) {
                    "SQLiteException while inserting or updating user"
                }
            }
        } catch (exception: SQLiteException) {
            KLogger.e(exception) {
                "SQLiteException while inserting or updating user"
            }
        } finally {
            database.endTransaction()
        }

        return userRow > 0
    }

    override fun selectUser(id: Long): AppUser.LoggedIn? {
        val selectionArgs = arrayOf(id.toString())
        val query = "SELECT * FROM ${Table.TABLE_NAME} WHERE ${Table.COLUMN_ID} = ?"

        val cursor = database.rawQuery(query, selectionArgs)

        val user = manageCursor(cursor)

        cursor?.close()

        return user
    }

    override fun selectUser(username: String): AppUser.LoggedIn? {
        val selectionArgs = arrayOf(username)
        val query = "SELECT * FROM ${Table.TABLE_NAME} WHERE ${Table.COLUMN_USERNAME} = ?"

        val cursor = database.rawQuery(query, selectionArgs)

        val user = manageCursor(cursor)

        cursor?.close()

        return user
    }

    override fun getUserIdFromUsername(username: String): Long? {
        val selectionArgs = arrayOf(username)
        val query = "SELECT * FROM ${Table.TABLE_NAME} WHERE ${Table.COLUMN_USERNAME} = ?"

        val cursor = database.rawQuery(query, selectionArgs)

        val user = manageCursor(cursor)

        cursor?.close()

        return user?.id
    }

    override fun insertUser(user: LoginUser.LoginData): Boolean {
        var userRow: Long = -1

        database.beginTransactionNonExclusive()

        try {
            val values = ContentValues()

            if (!user.username.isNullOrEmpty()) {
                values.put(Table.COLUMN_USERNAME, user.username)
            }

            try {
                userRow = database.insertWithOnConflict(
                    Table.TABLE_NAME,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_IGNORE
                )

                if (userRow.toInt() > 0) {
                    database.setTransactionSuccessful()
                }
            } catch (exception: SQLiteException) {
                KLogger.e(exception) {
                    "SQLiteException while inserting or updating user"
                }
            }
        } catch (exception: SQLiteException) {
            KLogger.e(exception) {
                "SQLiteException while inserting or updating user"
            }
        } finally {
            database.endTransaction()
        }

        return userRow > 0
    }

    override fun deleteUser() {
        TODO("Not yet implemented")
    }

    private fun cursorToData(cursor: Cursor): AppUser.LoggedIn {
        val idIndex = cursor.getColumnIndex(Table.COLUMN_ID)
        val usernameIndex = cursor.getColumnIndex(Table.COLUMN_USERNAME)
        val createdAt = cursor.getColumnIndex(Table.COLUMN_CREATED_AT)

        return AppUser.LoggedIn(cursor.getLong(idIndex), cursor.getString(usernameIndex), cursor.getLong(createdAt))
    }

    private fun manageCursor(cursor: Cursor?): AppUser.LoggedIn? {
        var user: AppUser.LoggedIn? = null

        if (cursor != null) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                user = cursorToData(cursor)
                cursor.moveToNext()
            }
        }
        return user
    }
}
