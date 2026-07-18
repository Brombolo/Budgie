package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Account::class,
        Category::class,
        Transaction::class,
        Budget::class,
        SavingsGoal::class,
        PlannedTransaction::class
    ],
    version = 8,
    exportSchema = false
)
abstract class BudgieDatabase : RoomDatabase() {

    abstract fun budgieDao(): BudgieDao

    companion object {
        @Volatile
        private var INSTANCE: BudgieDatabase? = null

        fun getDatabase(context: Context): BudgieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgieDatabase::class.java,
                    "budgie_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
