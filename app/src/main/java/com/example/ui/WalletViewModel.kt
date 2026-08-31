package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import java.util.Calendar
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WalletViewModel(application: Application, private val repository: BudgieRepository) : AndroidViewModel(application) {

    // --- BASE DATA STATE ---
    val accounts = repository.allAccounts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val transactions = repository.allTransactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val categories = repository.allCategories.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val budgets = repository.allBudgets.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val savingsGoals = repository.allSavingsGoals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val plannedTransactions = repository.allPlannedTransactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val prefs = application.getSharedPreferences("BudgiePrefs", android.content.Context.MODE_PRIVATE)

    // --- UI FILTERS STATE ---
    private val _selectedAccountId = MutableStateFlow<Int?>(null) // null = All Accounts
    val selectedAccountId = _selectedAccountId.asStateFlow()

    private val _showInternalTransfers = MutableStateFlow(true)
    val showInternalTransfers = _showInternalTransfers.asStateFlow()

    private val _selectedTimeRange = MutableStateFlow("Month") // "Today", "Week", "Month", "Custom"
    val selectedTimeRange = _selectedTimeRange.asStateFlow()

    // --- SETTINGS STATE (ITALIAN & ENGLISH SUPPORT) ---
    val startOfWeek = MutableStateFlow("Lunedì") // "Lunedì", "Domenica"
    val financialMonthStartDay = MutableStateFlow(1) // 1 to 31
    val pushNotificationsEnabled = MutableStateFlow(true)
    val budgetModuleEnabled = MutableStateFlow(prefs.getBoolean("budget_module_enabled", true))
    val goalsModuleEnabled = MutableStateFlow(prefs.getBoolean("goals_module_enabled", true))
    val reportsModuleEnabled = MutableStateFlow(prefs.getBoolean("reports_module_enabled", true))
    val categoriesModuleEnabled = MutableStateFlow(true)
    val planningModuleEnabled = MutableStateFlow(prefs.getBoolean("planning_module_enabled", true))
    val exportModuleEnabled = MutableStateFlow(prefs.getBoolean("export_module_enabled", true))
    val appLanguage = MutableStateFlow(prefs.getString("app_language", "Italiano") ?: "Italiano") // "Italiano", "English", "Español", "Català", "Français", "Deutsch"
    val appTheme = MutableStateFlow(prefs.getString("app_theme", "Chiaro") ?: "Chiaro") // "Chiaro", "Scuro"
    val currencyCode = MutableStateFlow(prefs.getString("app_currency_code", "EUR") ?: "EUR")
    val currencySymbol = MutableStateFlow(prefs.getString("app_currency_symbol", "€") ?: "€")
    val notifications = MutableStateFlow<List<InAppNotification>>(emptyList())
    val showBudgieTip = MutableStateFlow(prefs.getBoolean("show_budgie_tip", true))
    val budgetViewPeriod = MutableStateFlow(prefs.getString("budget_view_period", "Monthly") ?: "Monthly")
    
    // Persistent settings for Custom Expense Trend Chart
    val trendRangeType = MutableStateFlow(prefs.getString("trend_range_type", "Days") ?: "Days")
    val trendRangeCount = MutableStateFlow(prefs.getInt("trend_range_count", 3))
    val trendCategoryFilterId = MutableStateFlow(if (prefs.contains("trend_category_filter_id")) prefs.getInt("trend_category_filter_id", -1).let { if (it == -1) null else it } else null)

    fun setBudgetViewPeriod(period: String) {
        budgetViewPeriod.value = period
        prefs.edit().putString("budget_view_period", period).apply()
    }

    fun setTrendRangeType(type: String) {
        trendRangeType.value = type
        prefs.edit().putString("trend_range_type", type).apply()
    }

    fun setTrendRangeCount(count: Int) {
        trendRangeCount.value = count
        prefs.edit().putInt("trend_range_count", count).apply()
    }

    fun setTrendCategoryFilterId(categoryId: Int?) {
        trendCategoryFilterId.value = categoryId
        if (categoryId == null) {
            prefs.edit().remove("trend_category_filter_id").apply()
        } else {
            prefs.edit().putInt("trend_category_filter_id", categoryId).apply()
        }
    }

    fun setShowBudgieTip(show: Boolean) {
        showBudgieTip.value = show
        prefs.edit().putBoolean("show_budgie_tip", show).apply()
    }

    fun setPlanningModuleEnabled(enabled: Boolean) {
        togglePlanningModule(enabled)
    }

    fun markNotificationAsRead(id: String) {
        notifications.value = notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun removeNotification(id: String) {
        notifications.value = notifications.value.filter { it.id != id }
    }

    fun removeNotificationsForBudget(budgetId: Int) {
        notifications.value = notifications.value.filter { it.budgetId != budgetId }
    }

    fun clearAllNotifications() {
        notifications.value = emptyList()
    }

    private fun showSystemNotification(title: String, message: String) {
        val context = getApplication<Application>().applicationContext
        val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        
        val channelId = "budgie_notifications"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "Notifiche Budgie",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifiche per scadenza e sforamento budget"
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        val builder = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        if (intent != null) {
            val pendingIntent = android.app.PendingIntent.getActivity(
                context,
                0,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )
            builder.setContentIntent(pendingIntent)
        }
        
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private data class BudgetCheckState(
        val budgets: List<Budget>,
        val transactions: List<Transaction>,
        val weekStart: String,
        val monthStart: Int
    )

    val onboardingCompleted = MutableStateFlow(prefs.getBoolean("onboarding_completed", false))

    fun setStartOfWeek(day: String) {
        startOfWeek.value = day
        prefs.edit().putString("start_of_week", day).apply()
    }

    fun setFinancialMonthStartDay(day: Int) {
        financialMonthStartDay.value = day
        prefs.edit().putInt("financial_month_start_day", day).apply()
    }

    fun setAppLanguage(lang: String) {
        appLanguage.value = lang
        prefs.edit().putString("app_language", lang).apply()
    }

    fun setAppTheme(theme: String) {
        appTheme.value = theme
        prefs.edit().putString("app_theme", theme).apply()
    }

    fun setCurrency(code: String, symbol: String) {
        currencyCode.value = code
        currencySymbol.value = symbol
        prefs.edit().putString("app_currency_code", code).putString("app_currency_symbol", symbol).apply()
    }

    fun completeOnboarding(
        firstAccountName: String,
        firstAccountBalance: Double,
        firstAccountType: String,
        language: String,
        theme: String,
        currencyCodeVal: String = "EUR",
        currencySymbolVal: String = "€",
        weekStart: String,
        monthStart: Int,
        budgetEnabled: Boolean,
        goalsEnabled: Boolean,
        reportsEnabled: Boolean,
        planningEnabled: Boolean,
        exportEnabled: Boolean
    ) {
        viewModelScope.launch {
            // Clear any user data just in case
            repository.clearAllUserData()
            repository.seedInitialDataIfEmpty(language)
            
            // Insert the first account
            repository.insertAccount(Account(
                name = firstAccountName,
                type = firstAccountType,
                balance = firstAccountBalance,
                isIncludedInTotal = true
            ))
            
            // Save settings to SharedPreferences & Flows
            val editor = prefs.edit()
            editor.putString("app_language", language)
            editor.putString("app_theme", theme)
            editor.putString("app_currency_code", currencyCodeVal)
            editor.putString("app_currency_symbol", currencySymbolVal)
            editor.putString("start_of_week", weekStart)
            editor.putInt("financial_month_start_day", monthStart)
            editor.putBoolean("budget_module_enabled", budgetEnabled)
            editor.putBoolean("goals_module_enabled", goalsEnabled)
            editor.putBoolean("reports_module_enabled", reportsEnabled)
            editor.putBoolean("planning_module_enabled", planningEnabled)
            editor.putBoolean("export_module_enabled", exportEnabled)
            editor.putBoolean("onboarding_completed", true)
            editor.apply()
            
            appLanguage.value = language
            appTheme.value = theme
            currencyCode.value = currencyCodeVal
            currencySymbol.value = currencySymbolVal
            startOfWeek.value = weekStart
            financialMonthStartDay.value = monthStart
            budgetModuleEnabled.value = budgetEnabled
            goalsModuleEnabled.value = goalsEnabled
            reportsModuleEnabled.value = reportsEnabled
            planningModuleEnabled.value = planningEnabled
            exportModuleEnabled.value = exportEnabled
            onboardingCompleted.value = true
        }
    }

    fun resetAllUserData() {
        viewModelScope.launch {
            repository.clearAllUserData()
            repository.seedInitialDataIfEmpty("Italiano")
            repository.insertAccount(
                Account(
                    name = "Conto Principale",
                    type = "Conto Corrente",
                    balance = 0.0,
                    isIncludedInTotal = true
                )
            )
            
            // Reset all user customization preferences to default
            prefs.edit().clear().apply()
            
            // Reload StateFlows to default
            onboardingCompleted.value = false
            budgetModuleEnabled.value = true
            goalsModuleEnabled.value = true
            reportsModuleEnabled.value = true
            planningModuleEnabled.value = true
            exportModuleEnabled.value = true
            startOfWeek.value = "Lunedì"
            financialMonthStartDay.value = 1
            appLanguage.value = "Italiano"
            appTheme.value = "Chiaro"
            showBudgieTip.value = true
            budgetViewPeriod.value = "Monthly"
            trendRangeType.value = "Days"
            trendRangeCount.value = 3
            trendCategoryFilterId.value = null
            
            clearAllNotifications()
        }
    }

    init {
        val lastSelected = prefs.getInt("last_selected_account_id", -2)
        if (lastSelected != -2) {
            _selectedAccountId.value = if (lastSelected == -1) null else lastSelected
        }

        // Popola categorie se vuote e azzera dati se onboarding non completato
        viewModelScope.launch {
            val currentLang = prefs.getString("app_language", "Italiano") ?: "Italiano"
            repository.seedInitialDataIfEmpty(currentLang)
            val completed = prefs.getBoolean("onboarding_completed", false)
            if (!completed) {
                repository.clearAllUserData()
            }
        }

        // Controllo automatico budget scaduti e sforati
        viewModelScope.launch {
            combine(budgets, transactions, startOfWeek, financialMonthStartDay) { b, t, w, m ->
                BudgetCheckState(b, t, w, m)
            }.collect { state ->
                val now = System.currentTimeMillis()
                val newNotifications = mutableListOf<InAppNotification>()
                
                state.budgets.forEach { budget ->
                    val bounds = getBudgetBounds(budget, now, state.weekStart, state.monthStart)
                    
                    // 1. Controllo scadenza budget (se non auto-rinnovato)
                    if (!budget.autoRenew && now > bounds.second) {
                        val notifId = "expired_${budget.id}_${budget.createdAt}"
                        val alreadyNotified = notifications.value.any { it.id == notifId }
                        if (!alreadyNotified) {
                            val budgetName = when {
                                budget.categoryId != null -> {
                                    categories.value.find { it.id == budget.categoryId }?.name ?: "Categoria"
                                }
                                else -> "Complessivo"
                            }
                            newNotifications.add(
                                InAppNotification(
                                    id = notifId,
                                    title = "Budget Scaduto ⏱️",
                                    message = "Il tuo budget $budgetName da ${String.format(java.util.Locale.ITALIAN, "%.2f €", budget.amountLimit)} è scaduto. Clicca per reimpostarlo, modificarlo o eliminarlo.",
                                    budgetId = budget.id
                                )
                            )
                        }
                    }
                    
                    // 2. Controllo sforamento budget (se abilitato notifyOnOverflow)
                    if (budget.notifyOnOverflow) {
                        val spending = getBudgetSpending(budget, state.transactions)
                        if (spending > budget.amountLimit) {
                            val notifId = "overflow_${budget.id}_${bounds.first}"
                            val alreadyNotified = notifications.value.any { it.id == notifId }
                            if (!alreadyNotified) {
                                val budgetName = when {
                                    budget.categoryId != null -> {
                                        categories.value.find { it.id == budget.categoryId }?.name ?: "Categoria"
                                    }
                                    else -> "Complessivo"
                                }
                                val limitStr = String.format(java.util.Locale.ITALIAN, "%.2f €", budget.amountLimit)
                                val spendingStr = String.format(java.util.Locale.ITALIAN, "%.2f €", spending)
                                
                                val title = "Budget Superato! ⚠️"
                                val msg = "Il tuo budget $budgetName da $limitStr è stato superato! Spesa corrente: $spendingStr."
                                
                                newNotifications.add(
                                    InAppNotification(
                                        id = notifId,
                                        title = title,
                                        message = msg,
                                        budgetId = budget.id
                                    )
                                )
                                
                                // Mostra notifica reale di sistema
                                showSystemNotification(title, msg)
                            }
                        }
                    }
                }
                
                if (newNotifications.isNotEmpty()) {
                    notifications.value = notifications.value + newNotifications
                }
            }
        }

        // Controllo ed esecuzione automatica transazioni pianificate (Pianificazione)
        viewModelScope.launch {
            repository.allPlannedTransactions.collect { plannedList ->
                val now = System.currentTimeMillis()
                plannedList.forEach { planned ->
                    // Se una transazione una-tantum (non ricorrente) è già stata eseguita in precedenza o non è attiva, rimuovila automaticamente
                    if (!planned.isRecurring && (!planned.isActive || (planned.lastExecutedDate != null && planned.lastExecutedDate <= now))) {
                        repository.deletePlannedTransaction(planned)
                        return@forEach
                    }

                    if (planned.isActive) {
                        var currentPlanned = planned
                        var tempLastExecuted = planned.lastExecutedDate
                        var tempIsActive = planned.isActive
                        
                        while (tempIsActive) {
                            val nextExecution = if (tempLastExecuted == null) {
                                currentPlanned.startDate
                            } else {
                                if (!currentPlanned.isRecurring) {
                                    tempIsActive = false
                                    break
                                } else {
                                    calculateNextScheduledDate(
                                        currentPlanned.startDate,
                                        tempLastExecuted,
                                        currentPlanned.frequencyInterval ?: 1,
                                        currentPlanned.frequencyUnit ?: "giorni"
                                    )
                                }
                            }
                            
                            if (nextExecution <= now) {
                                executePlannedTransaction(currentPlanned, nextExecution)
                                
                                tempLastExecuted = nextExecution
                                if (!currentPlanned.isRecurring) {
                                    tempIsActive = false
                                    // Una volta scaduta/eseguita, la voce programmata singola viene rimossa automaticamente dall'elenco
                                    repository.deletePlannedTransaction(currentPlanned)
                                    break
                                } else {
                                    val updated = currentPlanned.copy(
                                        lastExecutedDate = tempLastExecuted,
                                        isActive = tempIsActive
                                    )
                                    repository.updatePlannedTransaction(updated)
                                    currentPlanned = updated
                                }
                            } else {
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    // --- TRANSITION MUTATORS ---
    fun selectAccount(accountId: Int?) {
        _selectedAccountId.value = accountId
        prefs.edit().putInt("last_selected_account_id", accountId ?: -1).apply()
    }

    fun toggleAccountInclusion(account: Account) {
        viewModelScope.launch {
            repository.updateAccount(account.copy(isIncludedInTotal = !account.isIncludedInTotal))
        }
    }

    fun toggleInternalTransfers() {
        _showInternalTransfers.value = !_showInternalTransfers.value
    }

    fun setTimeRange(range: String) {
        _selectedTimeRange.value = range
    }

    // --- DATABASE WRITERS ---
    fun addAccount(name: String, type: String, initialBalance: Double) {
        viewModelScope.launch {
            repository.insertAccount(Account(name = name, type = type, balance = initialBalance, isIncludedInTotal = true))
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            repository.deleteAccount(account)
        }
    }

    fun updateAccount(account: Account) {
        viewModelScope.launch {
            repository.updateAccount(account)
        }
    }

    fun addPlannedTransaction(planned: PlannedTransaction) {
        viewModelScope.launch {
            repository.insertPlannedTransaction(planned)
        }
    }

    fun deletePlannedTransaction(planned: PlannedTransaction) {
        viewModelScope.launch {
            repository.deletePlannedTransaction(planned)
        }
    }

    private suspend fun executePlannedTransaction(planned: PlannedTransaction, timestamp: Long) {
        val src = repository.getAccountById(planned.sourceAccountId)
        if (src != null) {
            when (planned.type) {
                "Expense" -> {
                    repository.updateAccount(src.copy(balance = src.balance - planned.amount))
                }
                "Income" -> {
                    repository.updateAccount(src.copy(balance = src.balance + planned.amount))
                }
                "Transfer" -> {
                    if (planned.destinationAccountId != null) {
                        val dest = repository.getAccountById(planned.destinationAccountId)
                        if (dest != null) {
                            repository.updateAccount(src.copy(balance = src.balance - planned.amount))
                            repository.updateAccount(dest.copy(balance = dest.balance + planned.amount))
                        }
                    }
                }
            }
        }
        
        val targetCategoryId = planned.subCategoryId ?: planned.categoryId
        
        val categoryName = if (planned.type != "Transfer" && targetCategoryId != null) {
            val allCats = repository.allCategories.first()
            val catObj = allCats.find { it.id == targetCategoryId }
            if (catObj != null) {
                if (catObj.parentCategoryId != null) {
                    val parentObj = allCats.find { it.id == catObj.parentCategoryId }
                    if (parentObj != null) "${parentObj.name} > ${catObj.name}" else catObj.name
                } else {
                    catObj.name
                }
            } else ""
        } else ""
        
        val defaultTitle = if (planned.title.isNotBlank()) {
            planned.title
        } else {
            val titlePrefix = if (planned.isRecurring) "Pianificato Ricorrente" else "Pianificato Programmato"
            val label = if (planned.type == "Transfer") "Giroconto" else if (planned.type == "Expense") "Spesa" else "Entrata"
            "$titlePrefix - $label" + (if (categoryName.isNotEmpty()) " ($categoryName)" else "")
        }

        repository.insertTransaction(
            Transaction(
                title = defaultTitle,
                amount = planned.amount,
                type = planned.type,
                timestamp = timestamp,
                categoryId = targetCategoryId,
                sourceAccountId = planned.sourceAccountId,
                destinationAccountId = planned.destinationAccountId,
                isFromPlanned = true
            )
        )
    }

    fun calculateNextScheduledDate(startDate: Long, lastExecuted: Long?, interval: Int, unit: String): Long {
        val baseTime = lastExecuted ?: startDate
        val cal = Calendar.getInstance().apply {
            timeInMillis = baseTime
        }
        val safeInterval = if (interval <= 0) 1 else interval
        when (unit.lowercase()) {
            "giorni", "days", "giorno", "day" -> cal.add(Calendar.DAY_OF_YEAR, safeInterval)
            "settimane", "weeks", "settimana", "week" -> cal.add(Calendar.WEEK_OF_YEAR, safeInterval)
            "mesi", "months", "mese", "month" -> cal.add(Calendar.MONTH, safeInterval)
            else -> cal.add(Calendar.DAY_OF_YEAR, safeInterval)
        }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun addCategory(name: String, iconEmoji: String, parentCategoryId: Int?, type: String = "Expense") {
        viewModelScope.launch {
            repository.insertCategory(Category(name = name, iconEmoji = iconEmoji, parentCategoryId = parentCategoryId, isCustom = true, type = type))
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun addTransaction(title: String, amount: Double, type: String, categoryId: Int?, sourceAccountId: Int, destinationAccountId: Int? = null, customTimestamp: Long? = null) {
        viewModelScope.launch {
            // Aggiorna saldo conti
            val src = repository.getAccountById(sourceAccountId)
            if (src != null) {
                when (type) {
                    "Expense" -> {
                        repository.updateAccount(src.copy(balance = src.balance - amount))
                    }
                    "Income" -> {
                        repository.updateAccount(src.copy(balance = src.balance + amount))
                    }
                    "Transfer" -> {
                        if (destinationAccountId != null) {
                            val dest = repository.getAccountById(destinationAccountId)
                            if (dest != null) {
                                repository.updateAccount(src.copy(balance = src.balance - amount))
                                repository.updateAccount(dest.copy(balance = dest.balance + amount))
                            }
                        }
                    }
                }
            }
            // Inserisci transazione
            repository.insertTransaction(
                Transaction(
                    title = title,
                    amount = amount,
                    type = type,
                    timestamp = customTimestamp ?: System.currentTimeMillis(),
                    categoryId = categoryId,
                    sourceAccountId = sourceAccountId,
                    destinationAccountId = destinationAccountId
                )
            )
        }
    }

    fun updateTransaction(newTx: Transaction, oldTx: Transaction) {
        viewModelScope.launch {
            // 1. Ripristina saldo conti della vecchia transazione
            val oldSrc = repository.getAccountById(oldTx.sourceAccountId)
            if (oldSrc != null) {
                when (oldTx.type) {
                    "Expense" -> {
                        repository.updateAccount(oldSrc.copy(balance = oldSrc.balance + oldTx.amount))
                    }
                    "Income" -> {
                        repository.updateAccount(oldSrc.copy(balance = oldSrc.balance - oldTx.amount))
                    }
                    "Transfer" -> {
                        if (oldTx.destinationAccountId != null) {
                            val oldDest = repository.getAccountById(oldTx.destinationAccountId)
                            if (oldDest != null) {
                                repository.updateAccount(oldSrc.copy(balance = oldSrc.balance + oldTx.amount))
                                repository.updateAccount(oldDest.copy(balance = oldDest.balance - oldTx.amount))
                            }
                        }
                    }
                }
            }

            // 2. Applica saldo conti della nuova transazione
            val newSrc = repository.getAccountById(newTx.sourceAccountId)
            if (newSrc != null) {
                when (newTx.type) {
                    "Expense" -> {
                        repository.updateAccount(newSrc.copy(balance = newSrc.balance - newTx.amount))
                    }
                    "Income" -> {
                        repository.updateAccount(newSrc.copy(balance = newSrc.balance + newTx.amount))
                    }
                    "Transfer" -> {
                        if (newTx.destinationAccountId != null) {
                            val newDest = repository.getAccountById(newTx.destinationAccountId)
                            if (newDest != null) {
                                repository.updateAccount(newSrc.copy(balance = newSrc.balance - newTx.amount))
                                repository.updateAccount(newDest.copy(balance = newDest.balance + newTx.amount))
                            }
                        }
                    }
                }
            }

            // 3. Salva nel database
            repository.updateTransaction(newTx)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            // Ripristina saldo conti
            val src = repository.getAccountById(transaction.sourceAccountId)
            if (src != null) {
                when (transaction.type) {
                    "Expense" -> {
                        repository.updateAccount(src.copy(balance = src.balance + transaction.amount))
                    }
                    "Income" -> {
                        repository.updateAccount(src.copy(balance = src.balance - transaction.amount))
                    }
                    "Transfer" -> {
                        if (transaction.destinationAccountId != null) {
                            val dest = repository.getAccountById(transaction.destinationAccountId)
                            if (dest != null) {
                                repository.updateAccount(src.copy(balance = src.balance + transaction.amount))
                                repository.updateAccount(dest.copy(balance = dest.balance - transaction.amount))
                            }
                        }
                    }
                }
            }
            repository.deleteTransaction(transaction)
        }
    }

    fun toggleGoalsModule(enabled: Boolean) {
        viewModelScope.launch {
            if (!enabled) {
                // Cancella tutti gli obiettivi (uncompleted)
                savingsGoals.value.forEach { goal ->
                    repository.deleteSavingsGoal(goal)
                }
                // Cancella tutte le transazioni di accantonamento / rilascio virtuali
                transactions.value.forEach { tx ->
                    if (tx.type == "VirtualSaving" || tx.type == "VirtualWithdrawal") {
                        repository.deleteTransaction(tx)
                    }
                }
            }
            goalsModuleEnabled.value = enabled
            prefs.edit().putBoolean("goals_module_enabled", enabled).apply()
        }
    }

    fun toggleExportModule(enabled: Boolean) {
        exportModuleEnabled.value = enabled
        prefs.edit().putBoolean("export_module_enabled", enabled).apply()
    }

    fun toggleBudgetModule(enabled: Boolean) {
        viewModelScope.launch {
            if (!enabled) {
                // Cancella tutti i budget
                budgets.value.forEach { budget ->
                    repository.deleteBudget(budget)
                }
            }
            budgetModuleEnabled.value = enabled
            prefs.edit().putBoolean("budget_module_enabled", enabled).apply()
        }
    }

    fun toggleReportsModule(enabled: Boolean) {
        reportsModuleEnabled.value = enabled
        prefs.edit().putBoolean("reports_module_enabled", enabled).apply()
    }

    fun togglePlanningModule(enabled: Boolean) {
        viewModelScope.launch {
            if (!enabled) {
                // Cancella tutte le transazioni pianificate attive
                plannedTransactions.value.forEach { planned ->
                    repository.deletePlannedTransaction(planned)
                }
            }
            planningModuleEnabled.value = enabled
            prefs.edit().putBoolean("planning_module_enabled", enabled).apply()
        }
    }

    fun clearAllGoalsAllocations() {
        viewModelScope.launch {
            savingsGoals.value.forEach { goal ->
                if (goal.currentAmount > 0.0) {
                    repository.updateSavingsGoal(goal.copy(currentAmount = 0.0))
                }
            }
        }
    }

    fun addSavingsGoal(name: String, targetAmount: Double, deadline: String, iconEmoji: String? = null) {
        viewModelScope.launch {
            repository.insertSavingsGoal(SavingsGoal(name = name, targetAmount = targetAmount, currentAmount = 0.0, deadline = deadline, iconEmoji = iconEmoji))
        }
    }

    fun addVirtualSaving(amount: Double, fromAccountId: Int) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    title = "Accantonamento obiettivi",
                    amount = amount,
                    type = "VirtualSaving",
                    timestamp = System.currentTimeMillis(),
                    sourceAccountId = fromAccountId
                )
            )
        }
    }

    fun addVirtualWithdrawal(amount: Double, toAccountId: Int) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    title = "Rilascio da obiettivi",
                    amount = amount,
                    type = "VirtualWithdrawal",
                    timestamp = System.currentTimeMillis(),
                    sourceAccountId = toAccountId
                )
            )
        }
    }

    fun allocateSavingsToGoal(goal: SavingsGoal, amount: Double) {
        viewModelScope.launch {
            val updatedGoal = goal.copy(currentAmount = goal.currentAmount + amount)
            repository.updateSavingsGoal(updatedGoal)
        }
    }

    fun deallocateSavingsFromGoal(goal: SavingsGoal, amount: Double) {
        viewModelScope.launch {
            val updatedGoal = goal.copy(currentAmount = (goal.currentAmount - amount).coerceAtLeast(0.0))
            repository.updateSavingsGoal(updatedGoal)
        }
    }

    fun completeSavingsGoal(
        goal: SavingsGoal,
        recordAsExpense: Boolean,
        amount: Double,
        fromAccountId: Int,
        categoryId: Int?,
        subCategoryId: Int?,
        note: String,
        timestamp: Long
    ) {
        viewModelScope.launch {
            if (recordAsExpense) {
                // 1. Detrai dal conto reale
                val account = repository.getAccountById(fromAccountId)
                if (account != null) {
                    repository.updateAccount(account.copy(balance = account.balance - amount))
                }
                // Inserisci spesa reale
                repository.insertTransaction(
                    Transaction(
                        title = note.ifBlank { goal.name },
                        amount = amount,
                        type = "Expense",
                        timestamp = timestamp,
                        categoryId = subCategoryId ?: categoryId,
                        sourceAccountId = fromAccountId
                    )
                )
                // 2. Rilascia corrispondente importo salvato virtuale così il saldo disponibile si riallinea correttamente
                repository.insertTransaction(
                    Transaction(
                        title = "Svincolato per acquisto: ${goal.name}",
                        amount = amount,
                        type = "VirtualWithdrawal",
                        timestamp = timestamp,
                        sourceAccountId = fromAccountId
                    )
                )
            }
            // Elimina l'obiettivo
            repository.deleteSavingsGoal(goal)
        }
    }

    fun deleteSavingsGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            repository.deleteSavingsGoal(goal)
        }
    }

    fun addOrUpdateBudget(
        categoryId: Int?,
        subCategoryId: Int? = null,
        accountId: Int?,
        amountLimit: Double,
        period: String,
        notifyOnOverflow: Boolean,
        autoRenew: Boolean = false,
        isPinnedToHome: Boolean = false
    ) {
        viewModelScope.launch {
            val currentBudgets = budgets.value
            val existing = currentBudgets.find {
                it.categoryId == categoryId && it.subCategoryId == subCategoryId && it.accountId == accountId && it.period == period
            }
            if (isPinnedToHome) {
                currentBudgets.forEach { b ->
                    if (b.isPinnedToHome && (existing == null || b.id != existing.id)) {
                        repository.updateBudget(b.copy(isPinnedToHome = false))
                    }
                }
            }
            if (existing != null) {
                repository.updateBudget(
                    existing.copy(
                        amountLimit = amountLimit,
                        notifyOnOverflow = notifyOnOverflow,
                        autoRenew = autoRenew,
                        isPinnedToHome = isPinnedToHome
                    )
                )
                removeNotificationsForBudget(existing.id)
            } else {
                repository.insertBudget(
                    Budget(
                        categoryId = categoryId,
                        subCategoryId = subCategoryId,
                        accountId = accountId,
                        amountLimit = amountLimit,
                        period = period,
                        notifyOnOverflow = notifyOnOverflow,
                        autoRenew = autoRenew,
                        createdAt = System.currentTimeMillis(),
                        isPinnedToHome = isPinnedToHome
                    )
                )
            }
        }
    }

    fun updateBudget(budget: Budget) {
        viewModelScope.launch {
            if (budget.isPinnedToHome) {
                val currentBudgets = budgets.value
                currentBudgets.forEach { b ->
                    if (b.isPinnedToHome && b.id != budget.id) {
                        repository.updateBudget(b.copy(isPinnedToHome = false))
                    }
                }
            }
            repository.updateBudget(budget)
            removeNotificationsForBudget(budget.id)
        }
    }

    fun togglePinBudget(budget: Budget) {
        viewModelScope.launch {
            val nextPinned = !budget.isPinnedToHome
            if (nextPinned) {
                val currentBudgets = budgets.value
                currentBudgets.forEach { b ->
                    if (b.isPinnedToHome && b.id != budget.id) {
                        repository.updateBudget(b.copy(isPinnedToHome = false))
                    }
                }
            }
            repository.updateBudget(budget.copy(isPinnedToHome = nextPinned))
        }
    }

    fun getBudgetSpending(budget: Budget, allTransactions: List<Transaction>): Double {
        val bounds = getBudgetBounds(
            budget = budget,
            currentTime = System.currentTimeMillis(),
            startOfWeek = startOfWeek.value,
            financialMonthStartDay = financialMonthStartDay.value
        )
        val start = bounds.first
        val end = bounds.second
        
        val expenses = allTransactions.filter { it.timestamp in start..end && it.type == "Expense" }
        
        val withAccount = if (budget.accountId != null) {
            expenses.filter { it.sourceAccountId == budget.accountId }
        } else {
            expenses
        }
        
        return if (budget.categoryId != null) {
            if (budget.subCategoryId != null) {
                withAccount.filter { it.categoryId == budget.subCategoryId }.sumOf { it.amount }
            } else {
                val allowedCategoryIds = mutableSetOf(budget.categoryId)
                categories.value.filter { it.parentCategoryId == budget.categoryId }.forEach {
                    allowedCategoryIds.add(it.id)
                }
                withAccount.filter { it.categoryId in allowedCategoryIds }.sumOf { it.amount }
            }
        } else {
            withAccount.sumOf { it.amount }
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
            removeNotificationsForBudget(budget.id)
        }
    }

    fun updateBudgetLimit(categoryId: Int, limit: Double, period: String) {
        addOrUpdateBudget(categoryId = categoryId, accountId = null, amountLimit = limit, period = period, notifyOnOverflow = true)
    }

    // --- COMPUTED READABLE STATES (EXCLUDING INTERNAL TRANSFERS ACCORDINGLY) ---

    // Saldo Totale Aggregato dei conti selezionati per l'inclusione
    val aggregateBalance: StateFlow<Double> = accounts.map { list ->
        list.filter { it.isIncludedInTotal }.sumOf { it.balance }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Mappa dei risparmi virtuali accantonati per ciascun conto (accountId -> importo)
    val totalSavedForGoalsByAccount: StateFlow<Map<Int, Double>> = transactions.map { list ->
        val map = mutableMapOf<Int, Double>()
        list.forEach { tx ->
            if (tx.type == "VirtualSaving") {
                map[tx.sourceAccountId] = (map[tx.sourceAccountId] ?: 0.0) + tx.amount
            } else if (tx.type == "VirtualWithdrawal") {
                map[tx.sourceAccountId] = (map[tx.sourceAccountId] ?: 0.0) - tx.amount
            }
        }
        map
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Totale accantonato complessivo (somma dei risparmi virtuali di tutti i conti attivi)
    val totalAccantonato: StateFlow<Double> = combine(
        totalSavedForGoalsByAccount,
        accounts
    ) { savedMap, list ->
        list.filter { it.isIncludedInTotal }.sumOf { savedMap[it.id] ?: 0.0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Totale allocato agli obiettivi (somma dei currentAmount dei vari obiettivi di risparmio)
    val totalAllocatedToGoals: StateFlow<Double> = savingsGoals.map { list ->
        list.sumOf { it.currentAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Risparmi nel salvadanaio liberi (non ancora assegnati ad alcun obiettivo)
    val unallocatedSavings: StateFlow<Double> = combine(
        totalAccantonato,
        totalAllocatedToGoals
    ) { accantonato, allocated ->
        (accantonato - allocated).coerceAtLeast(0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Lista delle transazioni filtrate in base a:
    // 1. Conto selezionato
    // 2. Giroconti visualizzati o nascosti
    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        transactions,
        _selectedAccountId,
        _showInternalTransfers
    ) { allTx, selectedId, showTransfers ->
        allTx.filter { tx ->
            val matchesAccount = (selectedId == null || tx.sourceAccountId == selectedId || tx.destinationAccountId == selectedId)
            val matchesTransfers = showTransfers || tx.type != "Transfer"
            matchesAccount && matchesTransfers
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calcolo spese complessive per categoria nel periodo selezionato
    val categoryBreakdown: StateFlow<List<CategorySpent>> = combine(
        transactions,
        categories,
        _selectedTimeRange,
        startOfWeek,
        financialMonthStartDay
    ) { allTx, allCat, timeRange, weekStart, monthStartDay ->
        val nowTime = System.currentTimeMillis()
        val bounds: Pair<Long, Long> = when (timeRange) {
            "Today" -> getDailyPeriodBounds(nowTime)
            "Week" -> getWeeklyPeriodBounds(nowTime, weekStart)
            "Month" -> getMonthlyPeriodBounds(nowTime, monthStartDay)
            "Year" -> {
                val currentCal = Calendar.getInstance().apply { timeInMillis = nowTime }
                val currentYear = currentCal.get(Calendar.YEAR)
                val startOfYear = Calendar.getInstance().apply {
                    set(currentYear, Calendar.JANUARY, 1, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val endOfYear = Calendar.getInstance().apply {
                    set(currentYear, Calendar.DECEMBER, 31, 23, 59, 59)
                    set(Calendar.MILLISECOND, 999)
                }.timeInMillis
                Pair(startOfYear, endOfYear)
            }
            else -> Pair(0L, Long.MAX_VALUE)
        }
        val expenses = allTx.filter { it.type == "Expense" && it.timestamp in bounds.first..bounds.second && it.categoryId != null }
        
        val totalExpense = expenses.sumOf { it.amount }
        val primaryCategories = allCat.filter { it.parentCategoryId == null }
        
        primaryCategories.map { cat ->
            // Includi transazioni della categoria principale o delle sue sottocategorie
            val subCategoryIds = allCat.filter { it.parentCategoryId == cat.id }.map { it.id }
            val relevantIds = listOf(cat.id) + subCategoryIds
            
            val catExpenses = expenses.filter { it.categoryId in relevantIds }
            val spentSum = catExpenses.sumOf { it.amount }
            val pct = if (totalExpense > 0) ((spentSum / totalExpense) * 100).toInt() else 0
            
            CategorySpent(
                category = cat,
                spentAmount = spentSum,
                percentage = pct,
                transactionCount = catExpenses.size
            )
        }.filter { it.spentAmount > 0 }.sortedByDescending { it.spentAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dati per il grafico della cronologia delle spese negli ultimi 7 giorni
    val last7DaysTrend: StateFlow<List<DailyTrend>> = transactions.map { allTx ->
        val calendar = Calendar.getInstance()
        val result = mutableListOf<DailyTrend>()
        
        // Ultime 7 date
        val daysOfWeek = listOf("D", "L", "M", "M", "G", "V", "S") // Italian days initials
        for (i in 6 downTo 0) {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            
            val startOfDay = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            val endOfDay = startOfDay + 24 * 60 * 60 * 1000L - 1
            
            val dailyExpense = allTx.filter {
                it.type == "Expense" && it.timestamp in startOfDay..endOfDay
            }.sumOf { it.amount }
            
            val dayOfWeek = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1]
            result.add(DailyTrend(dayName = dayOfWeek, amount = dailyExpense))
        }
        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- BUDGIE MASCOT MOOD ENGINE ---
    // Pappagallino reacts to daily budget consumption
    val dailyBudgetProgress: StateFlow<BudgetProgress> = combine(
        transactions,
        budgets
    ) { allTx, allBudgets ->
        val calendar = Calendar.getInstance()
        val startOfToday = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val todayExpenses = allTx.filter {
            it.type == "Expense" && it.timestamp >= startOfToday
        }.sumOf { it.amount }

        // Trova il limite mensile totale aggregato e dividilo per 30 giorni come rateo giornaliero stimato
        val totalMonthlyLimit = allBudgets.filter { it.period == "Monthly" }.sumOf { it.amountLimit }
        val dailyLimit = if (totalMonthlyLimit > 0) totalMonthlyLimit / 30.0 else 30.0 // Default €30 limit if none is set

        val percent = if (dailyLimit > 0) (todayExpenses / dailyLimit) * 100 else 0.0
        val mood = when {
            percent < 70.0 -> "Felice/Celebrativo" // Pappagallino sorride
            percent in 70.0..99.9 -> "Attento/Pensieroso" // Pappagallino attento
            else -> "Allerta" // Ala sulla fronte, limite superato!
        }

        BudgetProgress(
            spent = todayExpenses,
            limit = dailyLimit,
            percentage = percent,
            mascotMood = mood
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BudgetProgress(0.0, 30.0, 0.0, "Felice/Celebrativo"))

    fun getWeeklyPeriodBounds(currentTime: Long, startDayString: String): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.timeInMillis = currentTime
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        
        val targetDay = when (startDayString) {
            "Domenica" -> Calendar.SUNDAY
            "Lunedì" -> Calendar.MONDAY
            "Martedì" -> Calendar.TUESDAY
            "Mercoledì" -> Calendar.WEDNESDAY
            "Giovedì" -> Calendar.THURSDAY
            "Venerdì" -> Calendar.FRIDAY
            "Sabato" -> Calendar.SATURDAY
            else -> Calendar.MONDAY
        }
        
        while (cal.get(Calendar.DAY_OF_WEEK) != targetDay) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        val start = cal.timeInMillis
        
        cal.add(Calendar.DAY_OF_YEAR, 7)
        val end = cal.timeInMillis - 1
        
        return Pair(start, end)
    }

    fun getMonthlyPeriodBounds(currentTime: Long, financialStartDay: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.timeInMillis = currentTime
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        
        if (currentDay >= financialStartDay) {
            cal.set(Calendar.DAY_OF_MONTH, financialStartDay.coerceAtMost(cal.getActualMaximum(Calendar.DAY_OF_MONTH)))
        } else {
            cal.add(Calendar.MONTH, -1)
            cal.set(Calendar.DAY_OF_MONTH, financialStartDay.coerceAtMost(cal.getActualMaximum(Calendar.DAY_OF_MONTH)))
        }
        val start = cal.timeInMillis
        
        cal.add(Calendar.MONTH, 1)
        cal.set(Calendar.DAY_OF_MONTH, financialStartDay.coerceAtMost(cal.getActualMaximum(Calendar.DAY_OF_MONTH)))
        val end = cal.timeInMillis - 1
        
        return Pair(start, end)
    }

    fun getDailyPeriodBounds(currentTime: Long): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.timeInMillis = currentTime
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val end = cal.timeInMillis - 1
        return Pair(start, end)
    }

    fun getBudgetBounds(
        budget: Budget,
        currentTime: Long,
        startOfWeek: String,
        financialMonthStartDay: Int
    ): Pair<Long, Long> {
        val referenceTime = if (budget.autoRenew) currentTime else budget.createdAt
        return when (budget.period) {
            "Daily" -> getDailyPeriodBounds(referenceTime)
            "Weekly" -> getWeeklyPeriodBounds(referenceTime, startOfWeek)
            "Monthly" -> getMonthlyPeriodBounds(referenceTime, financialMonthStartDay)
            else -> getMonthlyPeriodBounds(referenceTime, financialMonthStartDay)
        }
    }

    private fun getCutoffTimestamp(range: String): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return when (range) {
            "Today" -> cal.timeInMillis
            "Week" -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.timeInMillis
            }
            "Month" -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.timeInMillis
            }
            else -> 0L // Custom or infinity
        }
    }

    // --- VIEWMODEL PROVIDER FACTORY ---
    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val database = BudgieDatabase.getDatabase(application)
                val repository = BudgieRepository(database.budgieDao())
                return WalletViewModel(application, repository) as T
            }
        }
    }
}

// --- TRANSITION MODELS FOR STATISTICS ---
data class CategorySpent(
    val category: Category,
    val spentAmount: Double,
    val percentage: Int,
    val transactionCount: Int
)

data class DailyTrend(
    val dayName: String,
    val amount: Double
)

data class BudgetProgress(
    val spent: Double,
    val limit: Double,
    val percentage: Double,
    val mascotMood: String
)

data class InAppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val budgetId: Int,
    val isRead: Boolean = false
)
