package ai.sterling.kchat.android.database

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseManager {
    private var database: SQLiteDatabase? = null

    companion object {
        var dmInstance: DatabaseManager? = null
            private set
        private var databaseHelper: SQLiteOpenHelper? = null

        @Synchronized
        fun initializeInstance(helper: SQLiteOpenHelper) {
            if (dmInstance == null) {
                dmInstance = DatabaseManager()
                databaseHelper = helper
            }
        }

        @Synchronized
        fun getInstance(): DatabaseManager {
            if (dmInstance == null) {
                throw IllegalStateException(
                    DatabaseManager::class.java.simpleName + " is not initialized, call initializeInstance(..) method first."
                )
            }

            return dmInstance!!
        }
    }

    fun openDatabase(): SQLiteDatabase {
        return (databaseHelper as KChatDatabaseHelper).openDatabase().also {
            database = it
        }!!
    }

    fun closeDatabase() = (databaseHelper as KChatDatabaseHelper).closeDatabase()
}
