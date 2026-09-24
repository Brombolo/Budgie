package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Account::class,
        Category::class,
        Transaction::class,
        Budget::class,
        SavingsGoal::class,
        PlannedTransaction::class
    ],
    version = 10,
    exportSchema = false
)
abstract class BudgieDatabase : RoomDatabase() {

    abstract fun budgieDao(): BudgieDao

    companion object {
        @Volatile
        private var INSTANCE: BudgieDatabase? = null

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN isFromPlanned INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE accounts ADD COLUMN savedAmount REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE savings_goals ADD COLUMN accountId INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE savings_goals ADD COLUMN iconEmoji TEXT NOT NULL DEFAULT '🐷'")
            }
        }

        fun getDatabase(context: Context): BudgieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgieDatabase::class.java,
                    "budgie_database"
                )
                .addMigrations(MIGRATION_8_9, MIGRATION_9_10)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
