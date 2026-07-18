package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class BudgieRepository(private val dao: BudgieDao) {

    val allAccounts: Flow<List<Account>> = dao.getAllAccounts()
    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()
    val allCategories: Flow<List<Category>> = dao.getAllCategories()
    val allBudgets: Flow<List<Budget>> = dao.getAllBudgets()
    val allSavingsGoals: Flow<List<SavingsGoal>> = dao.getAllSavingsGoals()
    val allPlannedTransactions: Flow<List<PlannedTransaction>> = dao.getAllPlannedTransactions()

    suspend fun getAccountById(id: Int): Account? = dao.getAccountById(id)

    suspend fun insertAccount(account: Account): Long = dao.insertAccount(account)
    suspend fun updateAccount(account: Account) = dao.updateAccount(account)
    suspend fun deleteAccount(account: Account) = dao.deleteAccount(account)

    suspend fun insertTransaction(transaction: Transaction): Long = dao.insertTransaction(transaction)
    suspend fun updateTransaction(transaction: Transaction) = dao.updateTransaction(transaction)
    suspend fun deleteTransaction(transaction: Transaction) = dao.deleteTransaction(transaction)

    suspend fun insertCategory(category: Category): Long = dao.insertCategory(category)
    suspend fun updateCategory(category: Category) = dao.updateCategory(category)
    suspend fun deleteCategory(category: Category) = dao.deleteCategory(category)

    suspend fun insertBudget(budget: Budget): Long = dao.insertBudget(budget)
    suspend fun updateBudget(budget: Budget) = dao.updateBudget(budget)
    suspend fun deleteBudget(budget: Budget) = dao.deleteBudget(budget)

    suspend fun insertSavingsGoal(goal: SavingsGoal): Long = dao.insertSavingsGoal(goal)
    suspend fun updateSavingsGoal(goal: SavingsGoal) = dao.updateSavingsGoal(goal)
    suspend fun deleteSavingsGoal(goal: SavingsGoal) = dao.deleteSavingsGoal(goal)

    suspend fun insertPlannedTransaction(planned: PlannedTransaction): Long = dao.insertPlannedTransaction(planned)
    suspend fun updatePlannedTransaction(planned: PlannedTransaction) = dao.updatePlannedTransaction(planned)
    suspend fun deletePlannedTransaction(planned: PlannedTransaction) = dao.deletePlannedTransaction(planned)

    suspend fun clearAllUserData() {
        dao.clearAccounts()
        dao.clearTransactions()
        dao.clearBudgets()
        dao.clearSavingsGoals()
        dao.clearPlannedTransactions()
        dao.clearCategories()
    }

    suspend fun seedInitialDataIfEmpty(language: String = "Italiano") {
        val categories = dao.getAllCategories().first()
        if (categories.isEmpty()) {
            val (cibo, rist, spesa, bar) = when (language) {
                "English" -> listOf("Food", "Restaurants", "Groceries", "Bar")
                "Español" -> listOf("Comida", "Restaurantes", "Supermercado", "Bar")
                "Català" -> listOf("Menjar", "Restaurants", "Supermercat", "Bar")
                "Français" -> listOf("Nourriture", "Restaurants", "Courses", "Bar")
                "Deutsch" -> listOf("Essen", "Restaurants", "Lebensmittel", "Bar")
                else -> listOf("Cibo", "Ristoranti", "Spesa", "Bar")
            }

            val (trasporti, carb, mezzi) = when (language) {
                "English" -> listOf("Transportation", "Fuel", "Public Transport")
                "Español" -> listOf("Transporte", "Combustible", "Transporte Público")
                "Català" -> listOf("Transport", "Combustible", "Transport Públic")
                "Français" -> listOf("Transports", "Carburant", "Transports en Commun")
                "Deutsch" -> listOf("Transport", "Kraftstoff", "Öffentliche Verkehrsmittel")
                else -> listOf("Trasporti", "Carburante", "Mezzi Pubblici")
            }

            val (casa, affitto, bollette) = when (language) {
                "English" -> listOf("Home", "Rent/Mortgage", "Bills")
                "Español" -> listOf("Casa", "Alquiler/Hipoteca", "Facturas")
                "Català" -> listOf("Casa", "Lloguer/Hipoteca", "Factures")
                "Français" -> listOf("Maison", "Loyer/Prêt", "Factures")
                "Deutsch" -> listOf("Zuhause", "Miete/Hypothek", "Rechnungen")
                else -> listOf("Casa", "Affitto/Mutuo", "Bollette")
            }

            val (svago, cinema, abbo) = when (language) {
                "English" -> listOf("Leisure", "Cinema", "Subscriptions")
                "Español" -> listOf("Ocio", "Cine", "Suscripciones")
                "Català" -> listOf("Oci", "Cinema", "Subscripcions")
                "Français" -> listOf("Loisirs", "Cinéma", "Abonnements")
                "Deutsch" -> listOf("Freizeit", "Kino", "Abonnements")
                else -> listOf("Svago", "Cinema", "Abbonamenti")
            }

            val (stipendio, lav, free) = when (language) {
                "English" -> listOf("Salary", "Main Job", "Freelance")
                "Español" -> listOf("Salario", "Trabajo Principal", "Freelance")
                "Català" -> listOf("Salari", "Feina Principal", "Freelance")
                "Français" -> listOf("Salaire", "Travail Principal", "Freelance")
                "Deutsch" -> listOf("Gehalt", "Hauptjob", "Freelance")
                else -> listOf("Stipendio", "Lavoro Principale", "Freelance")
            }

            val (invest, div) = when (language) {
                "English" -> listOf("Investments", "Dividends")
                "Español" -> listOf("Inversiones", "Dividendos")
                "Català" -> listOf("Inversions", "Dividends")
                "Français" -> listOf("Investissements", "Dividendes")
                "Deutsch" -> listOf("Investitionen", "Dividenden")
                else -> listOf("Investimenti", "Dividendi")
            }

            // Seed Categories
            val ciboId = dao.insertCategory(Category(name = cibo, iconEmoji = "🍕", parentCategoryId = null, type = "Expense")).toInt()
            dao.insertCategory(Category(name = rist, iconEmoji = "🍕", parentCategoryId = ciboId, type = "Expense"))
            dao.insertCategory(Category(name = spesa, iconEmoji = "🛒", parentCategoryId = ciboId, type = "Expense"))
            dao.insertCategory(Category(name = bar, iconEmoji = "☕", parentCategoryId = ciboId, type = "Expense"))

            val trasportiId = dao.insertCategory(Category(name = trasporti, iconEmoji = "🚗", parentCategoryId = null, type = "Expense")).toInt()
            dao.insertCategory(Category(name = carb, iconEmoji = "⛽", parentCategoryId = trasportiId, type = "Expense"))
            dao.insertCategory(Category(name = mezzi, iconEmoji = "🚌", parentCategoryId = trasportiId, type = "Expense"))

            val casaId = dao.insertCategory(Category(name = casa, iconEmoji = "🏠", parentCategoryId = null, type = "Expense")).toInt()
            dao.insertCategory(Category(name = affitto, iconEmoji = "🔑", parentCategoryId = casaId, type = "Expense"))
            dao.insertCategory(Category(name = bollette, iconEmoji = "⚡", parentCategoryId = casaId, type = "Expense"))

            val svagoId = dao.insertCategory(Category(name = svago, iconEmoji = "🎭", parentCategoryId = null, type = "Expense")).toInt()
            dao.insertCategory(Category(name = cinema, iconEmoji = "🎬", parentCategoryId = svagoId, type = "Expense"))
            dao.insertCategory(Category(name = abbo, iconEmoji = "📱", parentCategoryId = svagoId, type = "Expense"))

            // Seed Income Categories
            val stipendioId = dao.insertCategory(Category(name = stipendio, iconEmoji = "💼", parentCategoryId = null, type = "Income")).toInt()
            dao.insertCategory(Category(name = lav, iconEmoji = "🏢", parentCategoryId = stipendioId, type = "Income"))
            dao.insertCategory(Category(name = free, iconEmoji = "💻", parentCategoryId = stipendioId, type = "Income"))

            val investimentiId = dao.insertCategory(Category(name = invest, iconEmoji = "📈", parentCategoryId = null, type = "Income")).toInt()
            dao.insertCategory(Category(name = div, iconEmoji = "💰", parentCategoryId = investimentiId, type = "Income"))
        }
    }
}
