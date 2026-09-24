package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.BudgieTheme
import com.example.ui.WalletViewModel
import com.example.ui.screens.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: WalletViewModel by viewModels {
                WalletViewModel.provideFactory(application)
            }

            val appTheme by viewModel.appTheme.collectAsStateWithLifecycle()
            val language by viewModel.appLanguage.collectAsStateWithLifecycle()
            val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()

            BudgieTheme(
                darkTheme = appTheme == "Scuro"
            ) {
                CompositionLocalProvider(LocalCurrencySymbol provides currencySymbol) {
                    MainAppContainer(viewModel = viewModel, language = language)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(
    viewModel: WalletViewModel,
    language: String
) {
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsStateWithLifecycle()

    if (!onboardingCompleted) {
        OnboardingWizardScreen(viewModel = viewModel)
    } else {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()

        // Screen States
        var currentScreen by remember { mutableStateOf(BudgieScreen.HOME) }
        val notifications by viewModel.notifications.collectAsStateWithLifecycle()
        var showNotificationDialog by remember { mutableStateOf(false) }
        var showSearchTipsDialog by remember { mutableStateOf(false) }
        var showingHistoryMode by remember { mutableStateOf(false) }

        BackHandler(enabled = currentScreen != BudgieScreen.HOME) {
            if (drawerState.isOpen) {
                coroutineScope.launch { drawerState.close() }
            } else {
                currentScreen = BudgieScreen.HOME
            }
        }

        // Module enablement checks
        val budgetOn by viewModel.budgetModuleEnabled.collectAsStateWithLifecycle()
        val goalsOn by viewModel.goalsModuleEnabled.collectAsStateWithLifecycle()
        val reportsOn by viewModel.reportsModuleEnabled.collectAsStateWithLifecycle()
        val catOn by viewModel.categoriesModuleEnabled.collectAsStateWithLifecycle()
        val planningOn by viewModel.planningModuleEnabled.collectAsStateWithLifecycle()
        val exportOn by viewModel.exportModuleEnabled.collectAsStateWithLifecycle()

        // Filter available screens according to Settings switches
        val navigationItems = remember(budgetOn, goalsOn, reportsOn, catOn, planningOn, exportOn, language) {
            mutableListOf<NavigationItem>().apply {
                add(NavigationItem(BudgieScreen.HOME, Icons.Default.GridView, Icons.Outlined.GridView, "Dashboard".t(language)))
                add(NavigationItem(BudgieScreen.ACCOUNTS, Icons.Default.AccountBalance, Icons.Outlined.AccountBalance, "Conti".t(language)))
                add(NavigationItem(BudgieScreen.HISTORY, Icons.Default.ReceiptLong, Icons.Outlined.ReceiptLong, "Cronologia".t(language)))
                
                if (reportsOn) {
                    add(NavigationItem(BudgieScreen.REPORTS, Icons.Default.InsertChart, Icons.Outlined.InsertChart, "Statistiche".t(language)))
                }
                if (budgetOn) {
                    add(NavigationItem(BudgieScreen.BUDGETS, Icons.Default.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "Budget".t(language)))
                }
                if (goalsOn) {
                    add(NavigationItem(BudgieScreen.GOALS, Icons.Default.Savings, Icons.Outlined.Savings, "Obiettivi".t(language)))
                }
                if (planningOn) {
                    add(NavigationItem(BudgieScreen.PLANNING, Icons.Default.Schedule, Icons.Outlined.Schedule, "Pianificazione".t(language)))
                }
                if (catOn) {
                    add(NavigationItem(BudgieScreen.CATEGORIES, Icons.Default.Category, Icons.Outlined.Category, "Categorie".t(language)))
                }
                if (exportOn) {
                    add(NavigationItem(BudgieScreen.EXPORT, Icons.Default.Share, Icons.Outlined.Share, "Export".t(language)))
                }
                
                add(NavigationItem(BudgieScreen.SETTINGS, Icons.Default.Settings, Icons.Outlined.Settings, "Impostazioni".t(language)))
            }
        }

        ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                // Brand Header Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🦜", fontSize = 24.sp)
                    }
                    Column {
                        Text(
                            text = "Budgie",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation drawer items
                navigationItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.filledIcon else item.outlinedIcon,
                                contentDescription = item.label,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            currentScreen = item.screen
                            coroutineScope.launch { drawerState.close() }
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                            .testTag("drawer_item_${item.screen.route}"),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                val coroutineScope = rememberCoroutineScope()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Menu toggle button
                        IconButton(
                            onClick = { coroutineScope.launch { drawerState.open() } },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .testTag("menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Logo and Brand Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🦜", fontSize = 18.sp)
                            }
                            Text(
                                text = when (currentScreen) {
                                    BudgieScreen.HOME -> "Budgie"
                                    BudgieScreen.ACCOUNTS -> "Gestione Conti".t(language)
                                    BudgieScreen.HISTORY -> "Cronologia".t(language)
                                    BudgieScreen.REPORTS -> "Statistiche".t(language)
                                    BudgieScreen.BUDGETS -> "Budget".t(language)
                                    BudgieScreen.GOALS -> "Obiettivi".t(language)
                                    BudgieScreen.PLANNING -> "Pianificazione".t(language)
                                    BudgieScreen.CATEGORIES -> "Categorie".t(language)
                                    BudgieScreen.SETTINGS -> "Impostazioni".t(language)
                                    BudgieScreen.EXPORT -> "Export".t(language)
                                },
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Top bar actions
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentScreen == BudgieScreen.HISTORY) {
                            IconButton(
                                onClick = { showSearchTipsDialog = true },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .testTag("search_tips_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = "Suggerimenti di Ricerca".t(language),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Notification Button
                        Box(modifier = Modifier.wrapContentSize()) {
                            IconButton(
                                onClick = { 
                                    showingHistoryMode = false
                                    showNotificationDialog = true 
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifiche",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            val unreadCount = notifications.count { !it.isRead }
                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(Color.Red),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = unreadCount.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            },
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (currentScreen) {
                    BudgieScreen.HOME -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToHistory = { currentScreen = BudgieScreen.HISTORY }
                    )
                    BudgieScreen.ACCOUNTS -> AccountsScreen(viewModel = viewModel)
                    BudgieScreen.HISTORY -> HistoryScreen(viewModel = viewModel)
                    BudgieScreen.REPORTS -> ReportsScreen(viewModel = viewModel)
                    BudgieScreen.BUDGETS -> BudgetsScreen(viewModel = viewModel)
                    BudgieScreen.GOALS -> GoalsScreen(viewModel = viewModel)
                    BudgieScreen.PLANNING -> PlanningScreen(viewModel = viewModel)
                    BudgieScreen.CATEGORIES -> CategoriesScreen(viewModel = viewModel)
                    BudgieScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    BudgieScreen.EXPORT -> ExportScreen(viewModel = viewModel)
                }
            }
        }

        if (showNotificationDialog) {
            val displayList = if (showingHistoryMode) notifications else notifications.filter { !it.isRead }

            AlertDialog(
                onDismissRequest = { showNotificationDialog = false },
                title = { 
                    Text(
                        if (showingHistoryMode) {
                            "Storico Notifiche".t(language)
                        } else {
                            "Notifiche di Budgie 🦜".t(language)
                        }
                    ) 
                },
                text = {
                    if (displayList.isEmpty()) {
                        Text(
                            if (showingHistoryMode) {
                                "Nessuna notifica nello storico.".t(language)
                            } else {
                                "Nessuna notifica non letta.".t(language)
                            }
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            displayList.forEach { notif ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (notif.isRead) {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        }
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = notif.title, 
                                                fontWeight = FontWeight.Bold, 
                                                style = MaterialTheme.typography.titleMedium,
                                                color = if (notif.isRead) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (!notif.isRead) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(MaterialTheme.colorScheme.primary)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = notif.message, 
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (notif.isRead) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (!notif.isRead) {
                                                TextButton(
                                                    onClick = { viewModel.markNotificationAsRead(notif.id) },
                                                    modifier = Modifier.height(36.dp)
                                                ) {
                                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Letto".t(language), fontSize = 12.sp)
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                            }
                                            TextButton(
                                                onClick = { viewModel.removeNotification(notif.id) },
                                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                                modifier = Modifier.height(36.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Elimina".t(language), fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { 
                        if (showingHistoryMode) {
                            showingHistoryMode = false
                        } else {
                            showNotificationDialog = false 
                        }
                    }) {
                        Text(if (showingHistoryMode) "Indietro".t(language) else "Chiudi".t(language))
                    }
                },
                dismissButton = {
                    if (showingHistoryMode) {
                        if (notifications.isNotEmpty()) {
                            TextButton(
                                onClick = { viewModel.clearAllNotifications() },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Elimina tutte".t(language))
                            }
                        }
                    } else {
                        TextButton(
                            onClick = { showingHistoryMode = true }
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Storico".t(language))
                        }
                    }
                }
            )
        }

        if (showSearchTipsDialog) {
            SearchTipsDialog(
                language = language,
                onDismiss = { showSearchTipsDialog = false }
            )
        }
    }
}
}

// Helper representing formatted navigation items
data class NavigationItem(
    val screen: BudgieScreen,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val label: String
)
