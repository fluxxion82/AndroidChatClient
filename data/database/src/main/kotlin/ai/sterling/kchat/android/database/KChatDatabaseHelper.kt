package ai.sterling.kchat.android.database

import ai.sterling.kchat.android.database.dao.AppUserDao
import ai.sterling.kchat.android.database.dao.ChatMessageDao
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.CallSuper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class KChatDatabaseHelper @Inject constructor(
    @ApplicationContext val context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private var database: SQLiteDatabase? = null
    private var openCounter = AtomicInteger()

    override fun onCreate(db: SQLiteDatabase) {
        AppUserDao.Table.onCreate(db)
        ChatMessageDao.Table.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        AppUserDao.Table.onUpgrade(db, oldVersion, newVersion)
        ChatMessageDao.Table.onUpgrade(db, oldVersion, newVersion)
    }

    @CallSuper
    fun dropDatabase(): Boolean {
        database?.close()
        val result: Boolean = context.deleteDatabase(DATABASE_NAME)
        database = writableDatabase
        openCounter = AtomicInteger()
        return result
    }

    @Synchronized
    fun openDatabase(): SQLiteDatabase? {
        if (openCounter.incrementAndGet() == 1) {
            // Opening new database
            database = writableDatabase
        }
        return database
    }

    @Synchronized
    fun closeDatabase() {
        if (openCounter.decrementAndGet() == 0) {
            // Closing database
            database?.close()
        }
    }

    companion object {
        const val DATABASE_NAME = "kchat_db"
        const val DATABASE_VERSION = 1

        private const val FALSE = 0
        private const val TRUE = 1
    }
}
