package com.example.ui.screens

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
                                                        .height(50.dp)
                                                        .testTag("lang_${lang.lowercase()}"),
                                                    colors = CardDefaults.outlinedCardColors(
                                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent
                                                    ),
                                                    border = BorderStroke(
                                                        width = if (isSelected) 2.dp else 1.dp,
                                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                                    )
                                                ) {
                                                    Box(
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                        ) {
                                                            if (lang == "Català") {
                                                                Image(
                                                                    painter = painterResource(id = R.drawable.img_flag_catalonia),
                                                                    contentDescription = "Catalonia Flag",
                                                                    modifier = Modifier
                                                                        .size(22.dp)
                                                                        .clip(RoundedCornerShape(3.dp)),
                                                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                                                )
                                                            } else {
                                                                Text(flag, fontSize = 18.sp)
                                                            }
                                                            Text(lang, style = MaterialTheme.typography.bodyMedium, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Theme Select
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
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    listOf("Chiaro", "Scuro").forEach { themeMode ->
                                        val isSelected = selectedTheme == themeMode
                                        val icon = if (themeMode == "Chiaro") Icons.Outlined.LightMode else Icons.Outlined.DarkMode
                                        val label = if (themeMode == "Chiaro") "Chiaro".t(languageToUse) else "Scuro".t(languageToUse)
                                        
                                        OutlinedCard(
                                            onClick = { selectedTheme = themeMode },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(60.dp)
                                                .testTag("theme_$themeMode"),
                                            colors = CardDefaults.outlinedCardColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent
                                            ),
                                            border = BorderStroke(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(icon, contentDescription = null, tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = label,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    1 -> {
                        // FEATURES TO ENABLE (WITH EXPLANATIONS)
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Seleziona le funzioni da abilitare:".t(languageToUse),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Toggle module lists
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val modules = listOf(
                                Triple(
                                    "Budget".t(languageToUse),
                                    budgetModule,
                                    if (selectedLanguage == "English") "Set custom spending limits for categories."
                                    else if (selectedLanguage == "Español") "Establece límites de gastos para categorías."
                                    else if (selectedLanguage == "Català") "Estableix límits de despeses per a categories."
                                    else if (selectedLanguage == "Français") "Fixez des limites de dépenses pour les catégories."
                                    else if (selectedLanguage == "Deutsch") "Legen Sie Ausgabenlimits für Kategorien fest."
                                    else "Consente di impostare tetti di spesa per categoria."
                                ) to { v: Boolean -> budgetModule = v },
                                Triple(
                                    "Obiettivi".t(languageToUse),
                                    goalsModule,
                                    if (selectedLanguage == "English") "Set savings targets with specific deadlines."
                                    else if (selectedLanguage == "Español") "Establece metas de ahorro con plazos específicos."
                                    else if (selectedLanguage == "Català") "Estableix objectius d'estalvi amb terminis concrets."
                                    else if (selectedLanguage == "Français") "Fixez des objectifs d'épargne avec des échéances."
                                    else if (selectedLanguage == "Deutsch") "Legen Sie Sparziele mit Fristen fest."
                                    else "Ti aiuta a risparmiare denaro per obiettivi specifici."
                                ) to { v: Boolean -> goalsModule = v },
                                Triple(
                                    "Statistiche".t(languageToUse),
                                    reportsModule,
                                    if (selectedLanguage == "English") "Visual reports and charts of your money flow."
                                    else if (selectedLanguage == "Español") "Informes visuales y gráficos de tu flujo de dinero."
                                    else if (selectedLanguage == "Català") "Informes visuals i gràfics del teu flux de diners."
                                    else if (selectedLanguage == "Français") "Rapports visuels et graphiques de vos flux d'argent."
                                    else if (selectedLanguage == "Deutsch") "Visuelle Berichte und Diagramme Ihres Geldflusses."
                                    else "Grafici avanzati per analizzare distribuzioni e andamenti."
                                ) to { v: Boolean -> reportsModule = v },
                                Triple(
                                    "Pianificazione".t(languageToUse),
                                    planningModule,
                                    if (selectedLanguage == "English") "Manage future and automatically recurring transactions."
                                    else if (selectedLanguage == "Español") "Gestiona transacciones futuras y recurrentes automáticas."
                                    else if (selectedLanguage == "Català") "Gestiona transaccions futures i recurrents automàtiques."
                                    else if (selectedLanguage == "Français") "Gérez les transactions futures et récurrentes."
                                    else if (selectedLanguage == "Deutsch") "Verwalten Sie zukünftige und wiederkehrende Buchungen."
                                    else "Pianifica transazioni future o ricorrenti automatiche."
                                ) to { v: Boolean -> planningModule = v },
                                Triple(
                                    "Export".t(languageToUse),
                                    exportModule,
                                    if (selectedLanguage == "English") "Export all data locally in CSV or JSON format."
                                    else if (selectedLanguage == "Español") "Exporta todos tus datos localmente en CSV o JSON."
                                    else if (selectedLanguage == "Català") "Exporta totes les teves dades localment en CSV o JSON."
                                    else if (selectedLanguage == "Français") "Exporter toutes vos données localement en CSV ou JSON."
                                    else if (selectedLanguage == "Deutsch") "Exportieren Sie Daten lokal im CSV- oder JSON-Format."
                                    else "Consente di esportare i tuoi dati in locale per backup."
                                ) to { v: Boolean -> exportModule = v }
                            )
                            
                            modules.forEach { (moduleInfo, onToggle) ->
                                val (title, isEnabled, desc) = moduleInfo
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Switch(
                                            checked = isEnabled,
                                            onCheckedChange = onToggle,
                                            modifier = Modifier.testTag("module_switch_$title")
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    2 -> {
                        // CALENDAR CONFIGURATION
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Configurazione Calendario".t(languageToUse),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // First day of week
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Primo giorno della settimana".t(languageToUse),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.align(Alignment.Start)
                                )
                                
                                val daysOfWeek = listOf(
                                    "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    daysOfWeek.forEach { day ->
                                        val isSelected = selectedWeekStart == day
                                        val initial = when (day) {
                                            "Lunedì" -> when (selectedLanguage) {
                                                "English" -> "M"
                                                "Español" -> "L"
                                                "Català" -> "Dl"
                                                "Français" -> "L"
                                                "Deutsch" -> "M"
                                                else -> "L"
                                            }
                                            "Martedì" -> when (selectedLanguage) {
                                                "English" -> "T"
                                                "Español" -> "M"
                                                "Català" -> "Dt"
                                                "Français" -> "M"
                                                "Deutsch" -> "D"
                                                else -> "M"
                                            }
                                            "Mercoledì" -> when (selectedLanguage) {
                                                "English" -> "W"
                                                "Español" -> "X"
                                                "Català" -> "Dc"
                                                "Français" -> "M"
                                                "Deutsch" -> "M"
                                                else -> "M"
                                            }
                                            "Giovedì" -> when (selectedLanguage) {
                                                "English" -> "T"
                                                "Español" -> "J"
                                                "Català" -> "Dj"
                                                "Français" -> "J"
                                                "Deutsch" -> "D"
                                                else -> "G"
                                            }
                                            "Venerdì" -> when (selectedLanguage) {
                                                "English" -> "F"
                                                "Español" -> "V"
                                                "Català" -> "Dv"
                                                "Français" -> "V"
                                                "Deutsch" -> "F"
                                                else -> "V"
                                            }
                                            "Sabato" -> when (selectedLanguage) {
                                                "English" -> "S"
                                                "Español" -> "S"
                                                "Català" -> "Ds"
                                                "Français" -> "S"
                                                "Deutsch" -> "S"
                                                else -> "S"
                                            }
                                            "Domenica" -> when (selectedLanguage) {
                                                "English" -> "S"
                                                "Español" -> "D"
                                                "Català" -> "Dg"
                                                "Français" -> "D"
                                                "Deutsch" -> "S"
                                                else -> "D"
                                            }
                                            else -> ""
                                        }
                                        
                                        OutlinedCard(
                                            onClick = { selectedWeekStart = day },
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .testTag("week_start_$day"),
                                            colors = CardDefaults.outlinedCardColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent
                                            ),
                                            border = BorderStroke(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            ),
                                            shape = CircleShape
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = initial,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                val selectedDayLabel = when (selectedWeekStart) {
                                    "Lunedì" -> {
                                        if (selectedLanguage == "English") "Monday"
                                        else if (selectedLanguage == "Español") "Lunes"
                                        else if (selectedLanguage == "Català") "Dilluns"
                                        else if (selectedLanguage == "Français") "Lundi"
                                        else if (selectedLanguage == "Deutsch") "Montag"
                                        else "Lunedì"
                                    }
                                    "Martedì" -> {
                                        if (selectedLanguage == "English") "Tuesday"
                                        else if (selectedLanguage == "Español") "Martes"
                                        else if (selectedLanguage == "Català") "Dimarts"
                                        else if (selectedLanguage == "Français") "Mardi"
                                        else if (selectedLanguage == "Deutsch") "Dienstag"
                                        else "Martedì"
                                    }
                                    "Mercoledì" -> {
                                        if (selectedLanguage == "English") "Wednesday"
                                        else if (selectedLanguage == "Español") "Miércoles"
                                        else if (selectedLanguage == "Català") "Dimecres"
                                        else if (selectedLanguage == "Français") "Mercredi"
                                        else if (selectedLanguage == "Deutsch") "Mittwoch"
                                        else "Mercoledì"
                                    }
                                    "Giovedì" -> {
                                        if (selectedLanguage == "English") "Thursday"
                                        else if (selectedLanguage == "Español") "Jueves"
                                        else if (selectedLanguage == "Català") "Dijous"
                                        else if (selectedLanguage == "Français") "Jeudi"
                                        else if (selectedLanguage == "Deutsch") "Donnerstag"
                                        else "Giovedì"
                                    }
                                    "Venerdì" -> {
                                        if (selectedLanguage == "English") "Friday"
                                        else if (selectedLanguage == "Español") "Viernes"
                                        else if (selectedLanguage == "Català") "Divendres"
                                        else if (selectedLanguage == "Français") "Vendredi"
                                        else if (selectedLanguage == "Deutsch") "Freitag"
                                        else "Venerdì"
                                    }
                                    "Sabato" -> {
                                        if (selectedLanguage == "English") "Saturday"
                                        else if (selectedLanguage == "Español") "Sábado"
                                        else if (selectedLanguage == "Català") "Dissabte"
                                        else if (selectedLanguage == "Français") "Samedi"
                                        else if (selectedLanguage == "Deutsch") "Samstag"
                                        else "Sabato"
                                    }
                                    "Domenica" -> {
                                        if (selectedLanguage == "English") "Sunday"
                                        else if (selectedLanguage == "Español") "Domingo"
                                        else if (selectedLanguage == "Català") "Diumenge"
                                        else if (selectedLanguage == "Français") "Dimanche"
                                        else if (selectedLanguage == "Deutsch") "Sonntag"
                                        else "Domenica"
                                    }
                                    else -> selectedWeekStart
                                }
                                
                                val selectedDayPrefix = when (selectedLanguage) {
                                    "English" -> "Selected"
                                    "Español" -> "Seleccionado"
                                    "Català" -> "Seleccionat"
                                    "Français" -> "Sélectionné"
                                    "Deutsch" -> "Ausgewählt"
                                    else -> "Selezionato"
                                }
                                
                                Text(
                                    text = "$selectedDayPrefix: $selectedDayLabel",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // First day of month
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Mese Finanziario (Giorno Inizio)".t(languageToUse),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = selectedMonthStart.toString(),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                                
                                Slider(
                                    value = selectedMonthStart.toFloat(),
                                    onValueChange = { selectedMonthStart = it.toInt() },
                                    valueRange = 1f..31f,
                                    steps = 30,
                                    modifier = Modifier.testTag("month_start_slider")
                                )
                                
                                Text(
                                    text = "Il ciclo mensile terminerà automaticamente il giorno precedente a quello selezionato.".t(languageToUse),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    
                    3 -> {
                        // FIRST ACCOUNT SETUP
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏦", fontSize = 36.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Configura il tuo primo conto".t(languageToUse),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = "Registra dove tieni i tuoi fondi per tracciare entrate e spese.".t(languageToUse),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        OutlinedTextField(
                            value = accountName,
                            onValueChange = { accountName = it },
                            label = { Text("Nome del Conto".t(languageToUse)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("account_name_input")
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = accountBalanceStr,
                            onValueChange = { accountBalanceStr = it },
                            label = { Text("Saldo Iniziale (€)".t(languageToUse)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("account_balance_input")
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Tipologia Conto:".t(languageToUse),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Triple("Bank", "🏦", "Conto"),
                                Triple("Cash", "💵", "Contanti"),
                                Triple("Card", "💳", "Carta")
                            ).forEach { (type, emoji, label) ->
                                val isSelected = accountType == type
                                OutlinedCard(
                                    onClick = { accountType = type },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(72.dp)
                                        .testTag("account_type_$type"),
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent
                                    ),
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(emoji, fontSize = 24.sp)
                                        Text(
                                            text = label.t(languageToUse),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    4 -> {
                        // SUMMARY AND COMPLETION
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🚀", fontSize = 56.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "Tutto pronto!".t(languageToUse),
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Configurazione completata con successo. Sei pronto a prendere il controllo delle tue finanze personali con Budgie!".t(languageToUse),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                              ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "Security",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "La tua privacy è sacra".t(languageToUse),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Tutti i tuoi dati sono salvati esclusivamente in locale sul tuo telefono e non vengono mai trasmessi a server esterni.".t(languageToUse),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier
                            .height(50.dp)
                            .weight(1f)
                            .padding(end = 8.dp)
                            .testTag("wizard_prev_button")
                    ) {
                        Text("Indietro".t(languageToUse))
                    }
                }
                
                Button(
                    onClick = {
                        if (currentStep < 4) {
                            // Validation
                            if (currentStep == 3) {
                                if (accountName.isBlank()) {
                                    return@Button
                                }
                            }
                            currentStep++
                        } else {
                            val bal = accountBalanceStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                            viewModel.completeOnboarding(
                                firstAccountName = accountName,
                                firstAccountBalance = bal,
                                firstAccountType = accountType,
                                language = selectedLanguage,
                                theme = selectedTheme,
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
                    modifier = Modifier
                        .height(50.dp)
                        .weight(1f)
                        .padding(start = if (currentStep > 0) 8.dp else 0.dp)
                        .testTag("wizard_next_button"),
                    enabled = currentStep != 3 || accountName.isNotBlank()
                ) {
                    Text(
                        if (currentStep == 4) {
                            "Entra in Budgie".t(languageToUse)
                        } else {
                            "Avanti".t(languageToUse)
                        }
                    )
                }
            }
        }
    }
}
