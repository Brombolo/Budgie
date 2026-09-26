package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.*
import com.example.ui.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingWizardScreen(
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    // Current App Language
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    
    // Onboarding Form States
    var currentStep by remember { mutableIntStateOf(0) }
    
    // State values modified during wizard
    var selectedLanguage by remember { mutableStateOf(language) }
    var selectedTheme by remember { mutableStateOf("Chiaro") }
    var selectedCurrencyCode by remember { mutableStateOf("EUR") }
    var selectedCurrencySymbol by remember { mutableStateOf("€") }
    
    // Modules enablement
    var budgetModule by remember { mutableStateOf(true) }
    var goalsModule by remember { mutableStateOf(true) }
    var reportsModule by remember { mutableStateOf(true) }
    var planningModule by remember { mutableStateOf(true) }
    var exportModule by remember { mutableStateOf(true) }
    
    // Calendar settings
    var selectedWeekStart by remember { mutableStateOf("Lunedì") }
    var selectedMonthStart by remember { mutableStateOf(1) }
    
    // Step 3 states (Account Setup)
    var accountName by remember { mutableStateOf("") }
    var accountBalanceStr by remember { mutableStateOf("1000") }
    var accountType by remember { mutableStateOf("Bank") } // "Bank", "Cash", "Card"

    val context = LocalContext.current
    val isOldAppInstalled = remember {
        try {
            context.packageManager.getPackageInfo("com.aistudio.budgie.fhnskp", 0)
            true
        } catch (_: Exception) {
            false
        }
    }

    var importMessage by remember { mutableStateOf<String?>(null) }
    val csvPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.importDataFromCSV(context, uri) { success, msg ->
                importMessage = msg
                if (success) {
                    viewModel.completeOnboarding(
                        firstAccountName = "Conto Importato",
                        firstAccountBalance = 0.0,
                        firstAccountType = "Bank",
                        language = selectedLanguage,
                        theme = selectedTheme,
                        currencyCodeVal = selectedCurrencyCode,
                        currencySymbolVal = selectedCurrencySymbol,
                        weekStart = selectedWeekStart,
                        monthStart = selectedMonthStart,
                        budgetEnabled = budgetModule,
                        goalsEnabled = goalsModule,
                        reportsEnabled = reportsModule,
                        planningEnabled = planningModule,
                        exportEnabled = exportModule
                    )
                }
            }
        }
    }

    // Set default account name according to language
    LaunchedEffect(selectedLanguage) {
        if (accountName.isEmpty() || accountName == "Conto Principale" || accountName == "Main Account" || accountName == "Cuenta Principal" || accountName == "Compte Principal" || accountName == "Hauptkonto") {
            accountName = when (selectedLanguage) {
                "English" -> "Main Account"
                "Español" -> "Cuenta Principal"
                "Català" -> "Compte Principal"
                "Français" -> "Compte Principal"
                "Deutsch" -> "Hauptkonto"
                else -> "Conto Principale"
            }
        }
    }
    
    // Use selected language for in-wizard translation
    val languageToUse = selectedLanguage
    
    Scaffold(
        modifier = modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Step indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val totalSteps = 5
                for (i in 0 until totalSteps) {
                    val isPast = i < currentStep
                    val isCurrent = i == currentStep
                    val color = if (isCurrent) {
                        MaterialTheme.colorScheme.primary
                    } else if (isPast) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Main Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (currentStep) {
                    0 -> {
                        // WELCOME, LANGUAGE AND THEME SELECTION
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🦜", fontSize = 48.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Benvenuto su Budgie!".t(languageToUse),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = "Il tuo assistente finanziario personale, sicuro, moderno e 100% offline.".t(languageToUse),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )

                        if (isOldAppInstalled) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Rilevata versione precedente!", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                                    Text("È stata trovata una versione precedente di Budgie sul dispositivo. Puoi importare direttamente i tuoi conti, transazioni reali e categorie caricando il file CSV esportato dalla vecchia app.", style = MaterialTheme.typography.bodySmall)
                                    Button(
                                        onClick = { csvPickerLauncher.launch("text/comma-separated-values") },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                                    ) {
                                        Text("Importa da Backup CSV")
                                    }
                                    if (importMessage != null) {
                                        Text(importMessage!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Languages grid
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Seleziona la lingua dell'applicazione:".t(languageToUse),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                
                                val languages = listOf(
                                    "Italiano" to "🇮🇹",
                                    "English" to "🇬🇧",
                                    "Español" to "🇪🇸",
                                    "Català" to "🟡🔴",
                                    "Français" to "🇫🇷",
                                    "Deutsch" to "🇩🇪"
                                )
                                
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    for (chunk in languages.chunked(2)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            chunk.forEach { (lang, flag) ->
                                                val isSelected = selectedLanguage == lang
                                                OutlinedCard(
                                                    onClick = { selectedLanguage = lang },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(50.dp),
                                                    colors = CardDefaults.outlinedCardColors(
                                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                                                    ),
                                                    border = BorderStroke(
                                                        width = if (isSelected) 2.dp else 1.dp,
                                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                                    )
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .padding(horizontal = 12.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Text(flag, fontSize = 20.sp)
                                                        Text(
                                                            text = lang,
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Theme Selection Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Tema Chiaro o Scuro:".t(languageToUse),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val themes = listOf("Chiaro", "Scuro")
                                    themes.forEach { tName ->
                                        val isSelected = selectedTheme == tName
                                        OutlinedCard(
                                            onClick = { selectedTheme = tName },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(50.dp),
                                            colors = CardDefaults.outlinedCardColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                                            ),
                                            border = BorderStroke(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(if (tName == "Chiaro") "☀️" else "🌙", fontSize = 20.sp)
                                                Text(
                                                    text = tName.t(languageToUse),
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // CURRENCY & FORMAT
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💱", fontSize = 40.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Valuta dell'applicazione:".t(languageToUse),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                val currencies = listOf(
                                    "EUR" to "€",
                                    "USD" to "$",
                                    "GBP" to "£",
                                    "CHF" to "CHF",
                                    "JPY" to "¥"
                                )
                                
                                currencies.forEach { (code, symbol) ->
                                    val isSelected = selectedCurrencyCode == code
                                    OutlinedCard(
                                        onClick = {
                                            selectedCurrencyCode = code
                                            selectedCurrencySymbol = symbol
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.outlinedCardColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                                        ),
                                        border = BorderStroke(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Text(symbol, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                                Column {
                                                    Text(code, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                                    Text(
                                                        text = when(code) {
                                                            "EUR" -> "Euro"
                                                            "USD" -> "US Dollar"
                                                            "GBP" -> "British Pound"
                                                            "CHF" -> "Swiss Franc"
                                                            "JPY" -> "Japanese Yen"
                                                            else -> code
                                                        },
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.Gray
                                                    )
                                                }
                                            }
                                            if (isSelected) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // MODULES ENABLEMENT
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🧩", fontSize = 40.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Seleziona le funzioni da abilitare:".t(languageToUse),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                WizardModuleToggle("Modulo Budget".t(languageToUse), "Monitora i limiti di spesa per categoria o totali".t(languageToUse), budgetModule) { budgetModule = it }
                                Divider()
                                WizardModuleToggle("Modulo Obiettivi".t(languageToUse), "Traccia i tuoi traguardi di risparmio e salvadanaio".t(languageToUse), goalsModule) { goalsModule = it }
                                Divider()
                                WizardModuleToggle("Modulo Report".t(languageToUse), "Analizza statistiche e distribuzioni di spesa".t(languageToUse), reportsModule) { reportsModule = it }
                                Divider()
                                WizardModuleToggle("Modulo Pianificazione".t(languageToUse), "Automatizza transazioni ricorrenti e future".t(languageToUse), planningModule) { planningModule = it }
                                Divider()
                                WizardModuleToggle("Modulo Esportazione".t(languageToUse), "Esporta report e dati contabili in CSV".t(languageToUse), exportModule) { exportModule = it }
                            }
                        }
                    }
                    3 -> {
                        // FIRST ACCOUNT SETUP
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💳", fontSize = 40.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Configura il tuo primo conto".t(languageToUse),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = "Registra dove tieni i tuoi fondi per tracciare entrate e spese.".t(languageToUse),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                OutlinedTextField(
                                    value = accountName,
                                    onValueChange = { accountName = it },
                                    label = { Text("Nome del Conto".t(languageToUse)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                
                                OutlinedTextField(
                                    value = accountBalanceStr,
                                    onValueChange = { accountBalanceStr = it },
                                    label = { Text("Saldo Iniziale (€)".t(languageToUse)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                
                                Text(text = "Tipologia Conto:".t(languageToUse), style = MaterialTheme.typography.labelLarge)
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val types = listOf(
                                        "Bank" to "Conto".t(languageToUse),
                                        "Cash" to "Contanti".t(languageToUse),
                                        "Card" to "Carta".t(languageToUse)
                                    )
                                    types.forEach { (typeKey, typeLabel) ->
                                        val isSelected = accountType == typeKey
                                        OutlinedCard(
                                            onClick = { accountType = typeKey },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(45.dp),
                                            colors = CardDefaults.outlinedCardColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                                            ),
                                            border = BorderStroke(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            )
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = typeLabel,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    4 -> {
                        // CALENDAR & FINAL STEP
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🗓️", fontSize = 40.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Configurazione Calendario".t(languageToUse),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Primo giorno della settimana".t(languageToUse),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val days = listOf("Lunedì", "Domenica", "Sabato")
                                    days.forEach { day ->
                                        val isSelected = selectedWeekStart == day
                                        OutlinedCard(
                                            onClick = { selectedWeekStart = day },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(45.dp),
                                            colors = CardDefaults.outlinedCardColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                                            ),
                                            border = BorderStroke(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            )
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = day.t(languageToUse),
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                Divider()
                                
                                Text(
                                    text = "Mese Finanziario (Giorno Inizio)".t(languageToUse),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                
                                OutlinedTextField(
                                    value = selectedMonthStart.toString(),
                                    onValueChange = { selectedMonthStart = it.toIntOrNull()?.coerceIn(1, 28) ?: 1 },
                                    label = { Text("Giorno d'inizio:".t(languageToUse)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Navigation / Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Indietro".t(languageToUse))
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Button(
                    onClick = {
                        if (currentStep < 4) {
                            currentStep++
                        } else {
                            val initialBal = accountBalanceStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                            viewModel.completeOnboarding(
                                firstAccountName = accountName.ifBlank { "Conto Principale" },
                                firstAccountBalance = initialBal,
                                firstAccountType = accountType,
                                language = selectedLanguage,
                                theme = selectedTheme,
                                currencyCodeVal = selectedCurrencyCode,
                                currencySymbolVal = selectedCurrencySymbol,
                                weekStart = selectedWeekStart,
                                monthStart = selectedMonthStart,
                                budgetEnabled = budgetModule,
                                goalsEnabled = goalsModule,
                                reportsEnabled = reportsModule,
                                planningEnabled = planningModule,
                                exportEnabled = exportModule
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp).weight(1f).padding(start = if (currentStep > 0) 12.dp else 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (currentStep < 4) "Avanti".t(languageToUse) else "Entra in Budgie".t(languageToUse))
                    if (currentStep < 4) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun WizardModuleToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
