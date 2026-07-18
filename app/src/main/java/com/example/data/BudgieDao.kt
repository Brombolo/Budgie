package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgieDao {

    // --- ACCOUNTS ---
    @Query("SELECT * FROM accounts ORDER BY id ASC")
    fun getAllAccounts(): Flow<List<Account>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account): Long

    @Update
    suspend fun updateAccount(account: Account)

    @Delete
    suspend fun deleteAccount(account: Account)

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: Int): Account?


    // --- TRANSACTIONS ---
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type != 'Transfer' ORDER BY timestamp DESC")
    fun getAllNonTransferTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)


    // --- CATEGORIES ---
    @Query("SELECT * FROM categories ORDER BY id ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)


    // --- BUDGETS ---
    @Query("SELECT * FROM budgets ORDER BY id ASC")
    fun getAllBudgets(): Flow<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget): Long

    @Update
    suspend fun updateBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)


    // --- SAVINGS GOALS ---
    @Query("SELECT * FROM savings_goals ORDER BY id ASC")
    fun getAllSavingsGoals(): Flow<List<SavingsGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoal(goal: SavingsGoal): Long

    @Update
    suspend fun updateSavingsGoal(goal: SavingsGoal)

    @Delete
    suspend fun deleteSavingsGoal(goal: SavingsGoal)


    // --- PLANNED TRANSACTIONS ---
    @Query("SELECT * FROM planned_transactions ORDER BY startDate ASC")
    fun getAllPlannedTransactions(): Flow<List<PlannedTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlannedTransaction(planned: PlannedTransaction): Long

    @Update
    suspend fun updatePlannedTransaction(planned: PlannedTransaction)

    @Delete
    suspend fun deletePlannedTransaction(planned: PlannedTransaction)

    // --- CLEAR OPERATIONS ---
    @Query("DELETE FROM accounts")
    suspend fun clearAccounts()

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM budgets")
    suspend fun clearBudgets()

    @Query("DELETE FROM savings_goals")
    suspend fun clearSavingsGoals()

    @Query("DELETE FROM planned_transactions")
    suspend fun clearPlannedTransactions()

    @Query("DELETE FROM categories")
    suspend fun clearCategories()
}
