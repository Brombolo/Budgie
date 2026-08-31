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
    version = 9,
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

        fun getDatabase(context: Context): BudgieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgieDatabase::class.java,
                    "budgie_database"
                )
                .addMigrations(MIGRATION_8_9)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
