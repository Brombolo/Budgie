package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // "Bank", "Card", "Cash"
    val balance: Double,
    val isIncludedInTotal: Boolean = true
)

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val iconEmoji: String,
    val parentCategoryId: Int? = null, // Null per le categorie principali, Int per le sottocategorie
    val isCustom: Boolean = false,
    val type: String = "Expense", // "Expense" o "Income"
    val isQuickAction: Boolean = false,
    val defaultAccountId: Int? = null
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: String, // "Expense", "Income", "Transfer"
    val timestamp: Long = System.currentTimeMillis(),
    val categoryId: Int? = null,
    val sourceAccountId: Int,
    val destinationAccountId: Int? = null, // Per giroconti o prelievi/depositi
    val savingsGoalId: Int? = null // Facoltativo, per accumulo obiettivi
)

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int? = null, // Se null, si riferisce al budget totale
    val subCategoryId: Int? = null, // Se null, "tutte"
    val accountId: Int? = null, // Se null, si riferisce a "tutti i conti insieme"
    val amountLimit: Double,
    val period: String, // "Daily", "Weekly", "Monthly"
    val notifyOnOverflow: Boolean = true,
    val autoRenew: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val isPinnedToHome: Boolean = false
)

@Entity(tableName = "savings_goals")
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val deadline: String,
    val iconEmoji: String? = null
)

@Entity(tableName = "planned_transactions")
data class PlannedTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String = "",
    val type: String, // "Expense", "Income", "Transfer"
    val isRecurring: Boolean, // true = recurring, false = scheduled/one-time
    val frequencyInterval: Int? = null, // e.g., 3
    val frequencyUnit: String? = null, // "giorni", "settimane", "mesi"
    val startDate: Long, // timestamp
    val amount: Double,
    val categoryId: Int? = null,
    val subCategoryId: Int? = null,
    val sourceAccountId: Int,
    val destinationAccountId: Int? = null, // for Transfer
    val lastExecutedDate: Long? = null, // timestamp of last execution
    val isActive: Boolean = true
)
