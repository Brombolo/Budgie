package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

// --- TRANSLATION HELPER SYSTEM ---
object LocalizedStrings {
    private val en = mapOf(
        "(Tutte le sottocategorie)" to "(All subcategories)",
        "Dashboard" to "Dashboard",
        "Cronologia" to "History",
        "Statistiche" to "Reports",
        "Budget" to "Budgets",
        "Obiettivi" to "Goals",
        "Categorie" to "Categories",
        "Impostazioni" to "Settings",
        "Conti" to "Accounts",
        "Utente Premium" to "Premium User",
        "Gestione Conti" to "Accounts",
        "Notifiche di Budgie 🦜" to "Budgie Notifications 🦜",
        "Nessuna notifica presente al momento." to "No notifications at the moment.",
        "Chiudi" to "Close",
        // Settings Screen
        "IMPOSTAZIONI" to "SETTINGS",
        "ATTIVAZIONE MODULI" to "ENABLE MODULES",
        "Modulo Budget" to "Budget Module",
        "Modulo Obiettivi" to "Goals Module",
        "Modulo Report" to "Reports Module",
        "Modulo Categorie" to "Categories Module",
        "GENERALE" to "GENERAL",
        "Lingua" to "Language",
        "Tema" to "Theme",
        "Valuta" to "Currency",
        "Valuta:" to "Currency:",
        "Valuta del Conto:" to "Account Currency:",
        "Tutte le valute..." to "All currencies...",
        "Valuta dell'applicazione:" to "App currency:",
        "Seleziona la valuta dell'applicazione:" to "Select the app currency:",
        "Complessivo" to "Overall",
        "Tutti i Conti" to "All Accounts",
        "di" to "of",
        "Pianificato" to "Scheduled",
        "Chiaro" to "Light",
        "Scuro" to "Dark",
        "Progettato per la tua serenità finanziaria" to "Designed for your financial peace of mind",
        "Sviluppatore:" to "Developer:",
        "Licenza:" to "License:",
        "Annulla" to "Cancel",
        "Salva" to "Save",
        "Crea" to "Create",
        "Aggiungi" to "Add",
        "Elimina" to "Delete",
        // Dashboard / Home Screen
        "NOTIFICHE" to "NOTIFICATIONS",
        "MESSAGGIO DI BENVENUTO" to "WELCOME MESSAGE",
        "SALDO TOTALE" to "TOTAL BALANCE",
        "Saldo Totale" to "Total Balance",
        "Entrate" to "Income",
        "Spese" to "Expenses",
        "ENTRATE" to "INCOME",
        "SPESE" to "EXPENSES",
        "GIROCONTO" to "TRANSFER",
        "Nuova Transazione" to "New Transaction",
        "Tip del Pappagallino" to "Budgie's Tip",
        "Seleziona, nella sezione Categorie max 8 spese rapide" to "Select up to 8 quick actions in the Categories tab",
        "Inserimento Rapido" to "Quick Action",
        "Importo" to "Amount",
        "Salva Transazione" to "Save Transaction",
        "Valore non valido" to "Invalid value",
        "Conto Sorgente" to "Source Account",
        "Seleziona Conto" to "Select Account",
        "Seleziona Categoria" to "Select Category",
        "Seleziona Sottocategoria" to "Select Subcategory",
        "Aggiungi Transazione" to "Add Transaction",
        "Nessuna Transazione" to "No Transactions",
        "Nessuna transazione registrata." to "No transactions recorded.",
        "Aggiungi transazione rapida" to "Add quick transaction",
        "Inserimento rapido" to "Quick Entry",
        "Importo Spesa" to "Expense Amount",
        "Inserisci l'importo per" to "Enter the amount for",
        "Inserisci un importo valido" to "Enter a valid amount",
        "Conferma" to "Confirm",
        "Spesa" to "Expense",
        "Entrata" to "Income",
        "Giroconto" to "Transfer",
        "I MIEI CONTI" to "MY ACCOUNTS",
        "Tutti i conti" to "All accounts",
        "Nuovo Conto" to "New Account",
        "INSERIMENTI RAPIDI" to "QUICK ENTRIES",
        // Category Screen
        "Aggiungi Categoria" to "Add Category",
        "Nome Categoria" to "Category Name",
        "Emoji Icona" to "Emoji Icon",
        "Inserisci un singolo carattere emoji valido" to "Enter a single valid emoji character",
        "Carattere emoji valido" to "Valid emoji character",
        "Sottocategoria di (Opzionale):" to "Subcategory of (Optional):",
        "Sottocategoria di" to "Subcategory of",
        "Nessuna (Principale)" to "None (Primary)",
        "Modifica Categoria" to "Edit Category",
        "Modifica Sottocategoria" to "Edit Subcategory",
        "Nome" to "Name",
        "Emoji" to "Emoji",
        "Nuovo Budget di Categoria" to "New Category Budget",
        "Nuovo Budget Complessivo" to "New Overall Budget",
        "Seleziona Categoria:" to "Select Category:",
        "Nessuna Categoria" to "No Category",
        "Sottocategoria (Opzionale):" to "Subcategory (Optional):",
        "Tutte (Intera Categoria)" to "All (Whole Category)",
        "Notifica allo sforamento" to "Notify on overflow",
        "Rinnova automaticamente" to "Automatically renew",
        "Mostra nella Home" to "Show on Home",
        "Aggiungi Sottocategoria" to "Add Subcategory",
        "Nuova Sottocategoria per" to "New Subcategory for",
        "Nome Sottocategoria" to "Subcategory Name",
        "ENTRATE E USCITE" to "INCOME AND EXPENSES",
        "GESTIONE" to "MANAGEMENT",
        "Crea Categoria" to "Create Category",
        "Legenda: Clicca sulla stellina per impostarla come inserimento rapido sulla Home (Max 8)." to "Legend: Tap the star to set it as a quick entry on the Home screen (Max 8).",
        "Non puoi selezionare più di 8 inserimenti rapidi!" to "You cannot select more than 8 quick entries!",
        "Budget in Evidenza" to "Featured Budget",
        "Rinnovo auto" to "Auto-renew",
        "Notifica attiva" to "Notification active",
        "SCADUTO ⏱️" to "EXPIRED ⏱️",
        "SFORATO 🚨" to "EXCEEDED 🚨",
        "Cerca categorie..." to "Search categories...",
        "Attenzione" to "Warning",
        // Accounts Screen
        "Nome Conto" to "Account Name",
        "Tipo Conto" to "Account Type",
        "Conto Bancario" to "Bank Account",
        "Carta di Credito" to "Credit Card",
        "Contante" to "Cash",
        "Includi nel calcolo del saldo totale" to "Include in total balance calculation",
        "SALDO ATTUALE" to "CURRENT BALANCE",
        "DETTAGLIO CONTO" to "ACCOUNT DETAIL",
        "Modifica Conto" to "Edit Account",
        "Escluso dal totale" to "Excluded from total",
        "Conto" to "Account",
        "Scegli l'icona:" to "Choose the icon:",
        // Pianificazione
        "Modulo Pianificazione" to "Planning Module",
        "Pianificazione" to "Planning",
        "Imposta transazioni future o ricorrenti. Budgie le registrerà automaticamente." to "Set future or recurring transactions. Budgie will record them automatically.",
        "Transazioni Pianificate" to "Planned Transactions",
        "Nessuna transazione pianificata." to "No planned transactions.",
        "Ogni" to "Every",
        "Conto sorgente:" to "Source account:",
        "Conto destinazione:" to "Destination account:",
        "Categoria:" to "Category:",
        "Data inizio:" to "Start date:",
        "Prossimo:" to "Next:",
        "Aggiungi Pianificazione" to "Add Planning",
        "Nuova Pianificazione" to "New Planning",
        "Pianifica" to "Plan",
        "Errore: la data deve essere futura." to "Error: date must be in the future.",
        "Errore: inserisci un importo valido." to "Error: enter a valid amount.",
        "Errore: inserisci un intervallo valido per la ricorrenza." to "Error: enter a valid interval for recurrence.",
        "Errore: seleziona un conto di destinazione." to "Error: select a destination account.",
        "Errore: inserisci un nome per la pianificazione." to "Error: enter a name for the planning.",
        "Nome Pianificazione" to "Planning Name",
        "Step 1 di 2" to "Step 1 of 2",
        "Step 2 di 2" to "Step 2 of 2",
        "Avanti" to "Next",
        "Indietro" to "Back",
        "Da quale data iniziare" to "From which date to start",
        "Data transazione" to "Transaction date",
        "Seleziona Categoria" to "Select Category",
        "Seleziona Sottocategoria" to "Select Subcategory",
        "Nessuna sottocategoria" to "No subcategory",
        "giorni" to "days",
        "settimane" to "weeks",
        "mesi" to "months",
        // History Screen
        "CRONOLOGIA" to "HISTORY",
        "FILTRA PER CONTO" to "FILTER BY ACCOUNT",
        "Cerca per titolo..." to "Search by title...",
        "Tutti i Tipi" to "All Types",
        "Filtra per Categoria" to "Filter by Category",
        "Modifica Transazione" to "Edit Transaction",
        "Data e Ora" to "Date and Time",
        "Giroconti" to "Transfers",
        "Dettaglio Spese" to "Expense Details",
        "Modifica" to "Edit",
        // Budgets Screen
        "I MIEI LIMITI DI SPESA" to "MY SPENDING LIMITS",
        "Crea Budget" to "Create Budget",
        "Regola Limite Mensile" to "Adjust Monthly Limit",
        "SPESE MENSILI CORRENTI" to "CURRENT MONTHLY EXPENSES",
        "SPESE SETTIMANALI CORRENTI" to "CURRENT WEEKLY EXPENSES",
        "Giroconti esclusi" to "Transfers excluded",
        "Legenda: Clicca sulla stellina per fissare quel budget come \"Budget in Evidenza\" nella Home dell'applicazione." to "Legend: Click the star to pin this budget as the \"Featured Budget\" on the Home screen.",
        "Giornaliero" to "Daily",
        "Settimanale" to "Weekly",
        "Mensile" to "Monthly",
        // Goals Screen
        "OBIETTIVI DI RISPARMIO" to "SAVINGS GOALS",
        "Crea Obiettivo" to "Create Goal",
        "Nuovo Obiettivo di Risparmio" to "New Savings Goal",
        "Nome Obiettivo" to "Goal Name",
        "Cifra da Raggiungere" to "Target Amount",
        "Data Scadenza (es. 31/12/2026)" to "Deadline (e.g. 12/31/2026)",
        "Aggiungi Risparmi" to "Add Savings",
        "Aggiungi Fondi a" to "Add Funds to",
        "Seleziona Conto di Origine" to "Select Source Account",
        "Importo da spostare" to "Amount to transfer",
        "RAGGIUNTO! 🎉" to "REACHED! 🎉",
        "Scadenza:" to "Deadline:",
        "Versa fondi" to "Deposit funds",
        // Reports Screen
        "REPORT FINANZIARI" to "FINANCIAL REPORTS",
        "DISTRIBUZIONE SPESE MENSILI" to "MONTHLY EXPENSES DISTRIBUTION",
        "ENTRATE VS USCITE" to "INCOME VS EXPENSES",
        "Seleziona il Giorno di Inizio Mese Finanziario" to "Select Financial Month Start Day",
        "Giorno d'inizio:" to "Start day:",
        "Cambia" to "Change",
        "Nessun dato di spesa disponibile per questo mese." to "No expense data available for this month.",
        "Nessun dato disponibile." to "No data available.",
        "Bilancio mensile" to "Monthly balance",
        "Inizio Mese Finanziario" to "Financial Month Start",
        "Giorno di partenza del tuo mese finanziario personalizzato:" to "Start day of your custom financial month:",
        "Giorno" to "Day",
        // Wizard & Danger Zone Settings
        "Benvenuto su Budgie!" to "Welcome to Budgie!",
        "Il tuo assistente finanziario personale, sicuro, moderno e 100% offline." to "Your personal financial assistant, secure, modern and 100% offline.",
        "Seleziona la lingua dell'applicazione:" to "Select the application language:",
        "Configura il tuo primo conto" to "Set up your first account",
        "Registra dove tieni i tuoi fondi per tracciare entrate e spese." to "Register where you keep your funds to track income and expenses.",
        "Nome del Conto" to "Account Name",
        "Saldo Iniziale (€)" to "Initial Balance (€)",
        "Tipologia Conto:" to "Account Type:",
        "Conto" to "Account",
        "Contanti" to "Cash",
        "Carta" to "Card",
        "Imposta un Budget Mensile" to "Set a Monthly Budget",
        "Controlla le tue uscite impostando una soglia limite per una categoria di spesa." to "Control your spending by setting a limit threshold for an expense category.",
        "Abilita Budget iniziale" to "Enable initial Budget",
        "Ti aiuta a monitorare le spese mensili" to "Helps you track monthly expenses",
        "Categoria di spesa" to "Expense category",
        "Limite mensile di spesa (€)" to "Monthly spending limit (€)",
        "Tutto pronto!" to "All ready!",
        "Configurazione completata con successo. Sei pronto a prendere il controllo delle tue finanze personali con Budgie!" to "Setup completed successfully. You are ready to take control of your personal finances with Budgie!",
        "La tua privacy è sacra" to "Your privacy is sacred",
        "Tutti i tuoi dati sono salvati esclusivamente in locale sul tuo telefono e non vengono mai trasmessi a server esterni." to "All your data is saved exclusively locally on your phone and is never transmitted to external servers.",
        "Entra in Budgie" to "Enter Budgie",
        "ZONA DI PERICOLO" to "DANGER ZONE",
        "Ripristina Dati" to "Reset Data",
        "Cancella tutti i dati salvati e riavvia l'onboarding." to "Delete all saved data and restart onboarding.",
        "Sei sicuro di voler resettare?" to "Are you sure you want to reset?",
        "Questa operazione cancellerà tutti i conti, transazioni, budget e impostazioni. Non è possibile annullare questa azione." to "This operation will delete all accounts, transactions, budgets and settings. It cannot be undone.",
        "Sì, Resetta" to "Yes, Reset",
        "Inserisci nome" to "Enter name",
        "Nessun conto disponibile" to "No accounts available",
        "Valore non numerico" to "Non-numeric value",
        "Configurazione Calendario" to "Calendar Setup",
        "Storico Notifiche" to "Notification History",
        "Nessuna notifica nello storico." to "No notifications in history.",
        "Nessuna notifica non letta." to "No unread notifications.",
        "Letto" to "Read",
        "Storico" to "History",
        "Elimina tutte" to "Delete all",
        "I tuoi Obiettivi" to "Your Goals",
        "Gestisci i tuoi risparmi virtuali" to "Manage your virtual savings",
        "Nuovo" to "New",
        "SALVADANAIO VIRTUALE" to "VIRTUAL PIGGY BANK",
        "Fondi totali accantonati dai tuoi conti" to "Total funds set aside from your accounts",
        "Vincolati" to "Locked",
        "Non Assegnati" to "Unallocated",
        "Accantona" to "Set Aside",
        "Rilascia" to "Release",
        "OBIETTIVI INDIVIDUALI" to "INDIVIDUAL GOALS",
        "Svincola tutto" to "Unlock all",
        "Nessun obiettivo creato. Creane uno!" to "No goals created. Create one!",
        "su" to "of",
        "completato" to "completed",
        "Assegna" to "Assign",
        "Rimuovi" to "Remove",
        "Completa" to "Complete",
        "Elimina Obiettivo" to "Delete Goal",
        "Cifra Target (€)" to "Target Amount (€)",
        "Scadenza (es: Estate 2024)" to "Deadline (e.g., Summer 2024)",
        "Icona personalizzata (max 1 emoji)" to "Custom icon (max 1 emoji)",
        "Accantona Fondi" to "Set Aside Funds",
        "Preleva fittiziamente del denaro da un tuo conto per metterlo nel Salvadanaio Virtuale." to "Virtually withdraw money from one of your accounts to put it in the Virtual Piggy Bank.",
        "Quota da accantonare (€)" to "Amount to set aside (€)",
        "Seleziona conto di origine:" to "Select source account:",
        "Rilascia Fondi" to "Release Funds",
        "Preleva fondi dal Salvadanaio Virtuale per riallinearli come disponibili nel tuo conto." to "Withdraw funds from the Virtual Piggy Bank to realign them as available in your account.",
        "Seleziona conto di destinazione:" to "Select destination account:",
        "Accantonato:" to "Set aside:",
        "Assegna Fondi a Obiettivo" to "Assign Funds to Goal",
        "Disponibili non assegnati:" to "Unallocated available:",
        "Target rimanente:" to "Remaining target:",
        "Importo da vincolare (€)" to "Amount to lock (€)",
        "Rimuovi Fondi da Obiettivo" to "Remove Funds from Goal",
        "Rilascia parte dei fondi vincolati a questo obiettivo e riportali nello stato non assegnato del Salvadanaio." to "Release part of the funds locked to this goal and return them to the unallocated state of the Piggy Bank.",
        "Attualmente vincolati:" to "Currently locked:",
        "Importo da svincolare (€)" to "Amount to release (€)",
        "Completa Obiettivo" to "Complete Goal",
        "L'obiettivo verrà chiuso. Vuoi registrare l'acquisto reale come una spesa reale in contabilità?" to "The goal will be closed. Do you want to record the actual purchase as a real expense in the accounts?",
        "Registra come spesa reale" to "Record as actual expense",
        "Dettagli Spesa Reale:" to "Actual Expense Details:",
        "Conto di addebito reale:" to "Real debit account:",
        "Nota/Titolo Spesa" to "Expense Note/Title",
        "Tutti i Conti Insieme" to "All Accounts Together",
        "Conto Eliminato" to "Deleted Account",
        "Spesa corrente:" to "Current spending:",
        "di" to "of",
        "Reimposta" to "Reset",
        "Nessun budget complessivo impostato. Puoi impostare un limite di spesa totale cumulato o per un conto specifico." to "No overall budget set. You can set a total cumulative spending limit or for a specific account.",
        "Nessun budget per categoria configurato. Puoi impostare dei limiti dedicati a categorie specifiche." to "No category budget set. You can set dedicated limits for specific categories.",
        "Andamento Spese Personalizzato" to "Custom Expense Trend",
        "N. Periodi" to "N. Periods",
        "Giorni" to "Days",
        "Settimane" to "Weeks",
        "Mesi" to "Months",
        "Set" to "Wk",
        "Nessuna spesa nel periodo selezionato" to "No expenses in the selected period",
        "Gestisci conti, carte e contanti" to "Manage accounts, cards, and cash",
        "Conto Bancario" to "Bank Account",
        "Carta di Credito" to "Credit Card",
        "Contanti" to "Cash",
        "Includi nel totale" to "Include in total",
        "Regola Limite Mensile" to "Adjust Monthly Limit",
        "Limite Mensile (€)" to "Monthly Limit (€)",
        "Modifica Record" to "Edit Record",
        "Conto Origine:" to "Source Account:",
        "Conto Destinazione:" to "Destination Account:",
        "I Tuoi Conti" to "My Accounts",
        "Nuova Categoria Spesa" to "New Expense Category",
        "Nuova Categoria Entrata" to "New Income Category",
        "Emoji Icona" to "Emoji Icon",
        "Inserisci un singolo carattere emoji valido" to "Enter a single valid emoji character",
        "Carattere emoji valido" to "Valid emoji character",
        "Nessuna (Principale)" to "None (Main Category)",
        "Seleziona Sottocategoria:" to "Select Subcategory:",
        "Seleziona Ambito:" to "Select Scope:",
        "Ambito" to "Scope",
        "Ambito (Conto)" to "Scope (Account)",
        "Salva Modifiche" to "Save Changes",
        "Nota: hai dei fondi vincolati ad obiettivi individuali. Per rilasciare più di" to "Note: you have funds locked to individual goals. To release more than",
        "rimuovi prima i fondi dagli obiettivi." to "remove funds from goals first.",
        "Quota da rilasciare (€) - max" to "Amount to release (€) - max",
        "Destina parte dei risparmi non assegnati del Salvadanaio all'obiettivo:" to "Allocate part of the unallocated Piggy Bank savings to the goal: ",
        "Imposta intero importo previsto" to "Set full planned amount",
        "Rimuovi tutto" to "Remove all",
        "Congratulazioni per aver completato l'obiettivo:" to "Congratulations on completing the goal:",
        "L'obiettivo verrà chiuso. Vuoi registrare l'acquisto reale come una spesa reale in contabilità?" to "The goal will be closed. Do you want to record the actual purchase as a real expense in accounting?",
        "Registra come spesa reale" to "Record as a real expense",
        "Dettagli Spesa Reale:" to "Real Expense Details:",
        "Conto di addebito reale:" to "Real Debit Account:",
        "Nota/Titolo Spesa" to "Expense Note/Title",
        "Nessuna" to "None",
        "Completa" to "Complete",
        "Accantonati:" to "Allocated:",
        "Reale:" to "Real:",
        "Attività Recenti" to "Recent Activity",
        "Vedi Tutto" to "View All",
        "Budget Totali" to "Total Budgets",
        "Imposta" to "Set",
        "Nessun budget complessivo impostato. Puoi impostare un limite di spesa totale cumulato o per un conto specifico." to "No overall budget set. You can set a cumulative total spending limit or for a specific account.",
        "A quale conto destinare:" to "To which account to allocate:",
        "A:" to "To:",
        "Da:" to "From:",
        "Data e Ora (gg/mm/aaaa oo:mm)" to "Date and Time (dd/mm/yyyy hh:mm)",
        "Entrate 💰" to "Income 💰",
        "Esporta e Salva su Disco" to "Export and Save to Disk",
        "Importo (€)" to "Amount (€)",
        "Invia via Email come Allegato" to "Send via Email as Attachment",
        "Limite di Spesa (€)" to "Spending Limit (€)",
        "Modifica Budget" to "Edit Budget",
        "Nota (massimo 22 caratteri)" to "Note (maximum 22 characters)",
        "Notifiche Push" to "Push Notifications",
        "Periodo di Validità" to "Validity Period",
        "Periodo di Validità:" to "Validity Period:",
        "Rimani aggiornato sulle tue spese in tempo reale." to "Stay updated on your expenses in real-time.",
        "Seleziona Conto:" to "Select Account:",
        "Spese 💸" to "Expenses 💸",
        "Totale" to "Total",
        "transazioni" to "transactions",
        "Nuova Spesa" to "New Expense",
        "Nuova Entrata" to "New Income",
        "Nuovo Giroconto" to "New Transfer",
        "Budget per Categoria" to "Category Budgets",
        "Categoria" to "Category",
        "Sottocategoria" to "Subcategory",
        "Filtra per Categoria:" to "Filter by Category:",
        "Spesa per Categoria" to "Spending by Category",
        "Esporta Transazioni" to "Export Transactions",
        "Genera un report in formato CSV compatibile con Excel e software di contabilità." to "Generate a CSV report compatible with Excel and accounting software.",
        "PERIODO DI ESPORTAZIONE" to "EXPORT PERIOD",
        "Transazioni reali trovate nel periodo" to "Real transactions found in the period",
        "(I risparmi e gli accantonamenti virtuali degli obiettivi sono automaticamente esclusi dall'esportazione)" to "(Savings and virtual goal allocations are automatically excluded from the export)",
        "Tema Chiaro o Scuro:" to "Light or Dark Theme:",
        "Seleziona le funzioni da abilitare:" to "Select features to enable:",
        "Mese Finanziario (Giorno Inizio)" to "Financial Month (Start Day)",
        "Il ciclo mensile terminerà automaticamente il giorno precedente a quello selezionato." to "The monthly cycle will automatically end the day before the selected one.",
        "Primo giorno della settimana" to "First day of the week",
        "Andamento Finanziario" to "Financial Trend",
        "Settimana" to "Week",
        "Mese" to "Month",
        "Anno" to "Year",
        "Entrate:" to "Income:",
        "Spese:" to "Expenses:",
        "Risparmio netto:" to "Net savings:",
        "Mostra Giroconti Interni" to "Show Internal Transfers",
        "Visualizza movimenti tra i tuoi conti personali." to "View transactions between your personal accounts.",
        "Cerca transazione..." to "Search transaction...",
        "Nessuna transazione trovata." to "No transactions found.",
        "Nuovo Obiettivo" to "New Goal",
        "Tipo:" to "Type:",
        "Modalità:" to "Mode:",
        "Ricorrente" to "Recurring",
        "Programmato" to "Scheduled",
        "Unità" to "Unit",
        "CALENDARIO E PERIODI" to "CALENDAR & PERIODS",
        "COMUNICAZIONE" to "COMMUNICATION",
        "PERSONALIZZAZIONE FUNZIONI" to "FEATURE CUSTOMIZATION",
        "Conto Addebito Predefinito" to "Default Debit Account",
        "Seleziona il conto su cui addebitare di default la spesa rapida per" to "Select the default account to debit for quick expenses of",
        "Salta / Nessuno" to "Skip / None",
        "Inizio Settimana" to "Week Start"
    )

    private val es = mapOf(
        "(Tutte le sottocategorie)" to "(Todas las subcategorías)",
        "Dashboard" to "Panel de Control",
        "Cronologia" to "Historial",
        "Statistiche" to "Estadísticas",
        "Budget" to "Presupuestos",
        "Obiettivi" to "Metas",
        "Categorie" to "Categorías",
        "Impostazioni" to "Ajustes",
        "Conti" to "Cuentas",
        "Utente Premium" to "Usuario Premium",
        "Gestione Conti" to "Gestión de Cuentas",
        "Notifiche di Budgie 🦜" to "Notificaciones de Budgie 🦜",
        "Nessuna notifica presente al momento." to "No hay notificaciones en este momento.",
        "Chiudi" to "Cerrar",
        // Settings Screen
        "IMPOSTAZIONI" to "AJUSTES",
        "ATTIVAZIONE MODULI" to "ACTIVACIÓN DE MÓDULOS",
        "Modulo Budget" to "Módulo de Presupuestos",
        "Modulo Obiettivi" to "Módulo de Metas",
        "Modulo Report" to "Módulo de Informes",
        "Modulo Categorie" to "Módulo de Categorías",
        "GENERALE" to "GENERAL",
        "Lingua" to "Idioma",
        "Tema" to "Tema",
        "Valuta" to "Moneda",
        "Valuta:" to "Moneda:",
        "Valuta del Conto:" to "Moneda de la cuenta:",
        "Tutte le valute..." to "Todas las monedas...",
        "Valuta dell'applicazione:" to "Moneda de la aplicación:",
        "Seleziona la valuta dell'applicazione:" to "Selecciona la moneda de la aplicación:",
        "Complessivo" to "General",
        "Tutti i Conti" to "Todas las cuentas",
        "di" to "de",
        "Pianificato" to "Programado",
        "Chiaro" to "Claro",
        "Scuro" to "Oscuro",
        "Progettato per la tua serenità finanziaria" to "Diseñado para tu tranquilidad financiera",
        "Sviluppatore:" to "Desarrollador:",
        "Licenza:" to "Licencia:",
        "Annulla" to "Cancelar",
        "Salva" to "Guardar",
        "Crea" to "Crear",
        "Aggiungi" to "Añadir",
        "Elimina" to "Eliminar",
        // Dashboard / Home Screen
        "NOTIFICHE" to "NOTIFICACIONES",
        "MESSAGGIO DI BENVENUTO" to "MENSAJE DE BIENVENIDA",
        "SALDO TOTALE" to "SALDO TOTAL",
        "Saldo Totale" to "Saldo Total",
        "Entrate" to "Ingresos",
        "Spese" to "Gastos",
        "ENTRATE" to "INGRESOS",
        "SPESE" to "GASTOS",
        "GIROCONTO" to "TRANSFERENCIA",
        "Nuova Transazione" to "Nueva Transacción",
        "Tip del Pappagallino" to "Consejo de Budgie",
        "Seleziona, nella sezione Categorie max 8 spese rapide" to "Selecciona hasta 8 acciones rápidas en la pestaña Categorías",
        "Inserimento Rapido" to "Acción Rápida",
        "Importo" to "Importe",
        "Salva Transazione" to "Guardar Transacción",
        "Valore non valido" to "Valor no válido",
        "Conto Sorgente" to "Cuenta de Origen",
        "Seleziona Conto" to "Seleccionar Cuenta",
        "Seleziona Categoria" to "Seleccionar Categoría",
        "Seleziona Sottocategoria" to "Seleccionar Subcategoría",
        "Aggiungi Transazione" to "Añadir Transacción",
        "Nessuna Transazione" to "Sin Transacciones",
        "Nessuna transazione registrata." to "No hay transacciones registradas.",
        "Aggiungi transazione rapida" to "Añadir transacción rápida",
        "Inserimento rapido" to "Entrada Rápida",
        "Importo Spesa" to "Importe del Gasto",
        "Inserisci l'importo per" to "Introduce el importe para",
        "Inserisci un importo valido" to "Introduce un importe válido",
        "Conferma" to "Confirmar",
        "Spesa" to "Gasto",
        "Entrata" to "Ingreso",
        "Giroconto" to "Transferencia",
        "I MIEI CONTI" to "MIS CUENTAS",
        "Tutti i conti" to "Todas las cuentas",
        "Nuovo Conto" to "Nueva Cuenta",
        "INSERIMENTI RAPIDI" to "ENTRADAS RÁPIDAS",
        // Category Screen
        "Aggiungi Categoria" to "Añadir Categoría",
        "Nome Categoria" to "Nombre de Categoría",
        "Emoji Icona" to "Icono Emoji",
        "Inserisci un singolo carattere emoji valido" to "Introduce un solo carácter emoji válido",
        "Carattere emoji valido" to "Carácter emoji válido",
        "Sottocategoria di (Opzionale):" to "Subcategoría de (Opcional):",
        "Sottocategoria di" to "Subcategoría de",
        "Nessuna (Principale)" to "Ninguna (Principal)",
        "Modifica Categoria" to "Editar Categoría",
        "Modifica Sottocategoria" to "Editar Subcategoría",
        "Nome" to "Nombre",
        "Emoji" to "Emoji",
        "Nuovo Budget di Categoria" to "Nuevo Presupuesto de Categoría",
        "Nuovo Budget Complessivo" to "Nuevo Presupuesto General",
        "Seleziona Categoria:" to "Seleccionar Categoría:",
        "Nessuna Categoria" to "Sin Categoría",
        "Sottocategoria (Opzionale):" to "Subcategoría (Opcional):",
        "Tutte (Intera Categoria)" to "Todas (Categoría Completa)",
        "Notifica allo sforamento" to "Notificar al exceder",
        "Rinnova automaticamente" to "Renovar automáticamente",
        "Mostra nella Home" to "Mostrar en la Home",
        "Aggiungi Sottocategoria" to "Añadir Subcategoría",
        "Nuova Sottocategoria per" to "Nueva Subcategoría para",
        "Nome Sottocategoria" to "Nombre de Subcategoría",
        "ENTRATE E USCITE" to "INGRESOS Y GASTOS",
        "GESTIONE" to "GESTIÓN",
        "Crea Categoria" to "Crear Categoría",
        "Legenda: Clicca sulla stellina per impostarla come inserimento rapido sulla Home (Max 8)." to "Leyenda: Toca la estrella para establecerla como acción rápida en la pantalla de inicio (Máx 8).",
        "Non puoi selezionare più di 8 inserimenti rapidi!" to "¡No puedes seleccionar más de 8 entradas rápidas!",
        "Budget in Evidenza" to "Presupuesto Destacado",
        "Rinnovo auto" to "Auto-renovación",
        "Notifica attiva" to "Notificación activa",
        "SCADUTO ⏱️" to "EXPIRADO ⏱️",
        "SFORATO 🚨" to "EXCEDIDO 🚨",
        "Cerca categorie..." to "Buscar categorías...",
        "Attenzione" to "Atención",
        // Accounts Screen
        "Nome Conto" to "Nombre de la Cuenta",
        "Tipo Conto" to "Tipo de Cuenta",
        "Conto Bancario" to "Cuenta Bancaria",
        "Carta di Credito" to "Tarjeta de Crédito",
        "Contante" to "Efectivo",
        "Includi nel calcolo del saldo totale" to "Incluir en el cálculo del saldo total",
        "SALDO ATTUALE" to "SALDO ACTUAL",
        "DETTAGLIO CONTO" to "DETALLE DE LA CUENTA",
        "Modifica Conto" to "Editar Cuenta",
        "Escluso dal totale" to "Excluido del total",
        "Conto" to "Cuenta",
        "Scegli l'icona:" to "Elige el icono:",
        // Pianificazione
        "Modulo Pianificazione" to "Módulo de Planificación",
        "Pianificazione" to "Planificación",
        "Imposta transazioni future o ricorrenti. Budgie le registrerà automaticamente." to "Configura transacciones futuras o recurrentes. Budgie las registrará automáticamente.",
        "Transazioni Pianificate" to "Transacciones Planificadas",
        "Nessuna transazione pianificata." to "No hay transacciones planificadas.",
        "Ogni" to "Cada",
        "Conto sorgente:" to "Cuenta de origen:",
        "Conto destinazione:" to "Cuenta de destino:",
        "Categoria:" to "Categoría:",
        "Data inizio:" to "Fecha de inicio:",
        "Prossimo:" to "Próximo:",
        "Aggiungi Pianificazione" to "Añadir Planificación",
        "Nuova Pianificazione" to "Nueva Planificación",
        "Pianifica" to "Planificar",
        "Errore: la data deve essere futura." to "Error: la fecha debe ser futura.",
        "Errore: inserisci un importo valido." to "Error: introduce un importe válido.",
        "Errore: inserisci un intervallo valido per la ricorrenza." to "Error: introduce un intervalo válido para la recurrencia.",
        "Errore: seleziona un conto di destinazione." to "Error: selecciona una cuenta de destino.",
        "Errore: inserisci un nome per la pianificazione." to "Error: introduce un nombre para la planificación.",
        "Nome Pianificazione" to "Nombre de la Planificación",
        "Step 1 di 2" to "Paso 1 de 2",
        "Step 2 di 2" to "Paso 2 de 2",
        "Avanti" to "Siguiente",
        "Indietro" to "Atrás",
        "Da quale data iniziare" to "Desde qué fecha comenzar",
        "Data transazione" to "Fecha de transacción",
        "Seleziona Categoria" to "Seleccionar Categoría",
        "Seleziona Sottocategoria" to "Seleccionar Subcategoría",
        "Nessuna sottocategoria" to "Sin subcategoría",
        "giorni" to "días",
        "settimane" to "semanas",
        "mesi" to "meses",
        // History Screen
        "CRONOLOGIA" to "HISTORIAL",
        "FILTRA PER CONTO" to "FILTRAR POR CUENTA",
        "Cerca per titolo..." to "Buscar por título...",
        "Tutti i Tipi" to "Todos los Tipos",
        "Filtra per Categoria" to "Filtrar por Categoría",
        "Modifica Transazione" to "Editar Transacción",
        "Data e Ora" to "Fecha y Hora",
        "Giroconti" to "Transferencias",
        "Dettaglio Spese" to "Detalle de Gastos",
        "Modifica" to "Editar",
        // Budgets Screen
        "I MIEI LIMITI DI SPESA" to "MIS LÍMITES DE GASTO",
        "Crea Budget" to "Crear Presupuesto",
        "Regola Limite Mensile" to "Ajustar Límite Mensual",
        "SPESE MENSILI CORRENTI" to "GASTOS MENSUALES ACTUALES",
        "SPESE SETTIMANALI CORRENTI" to "GASTOS SEMANALES ACTUALES",
        "Giroconti esclusi" to "Transferencias excluidas",
        "Legenda: Clicca sulla stellina per fissare quel budget come \"Budget in Evidenza\" nella Home dell'applicazione." to "Leyenda: Haz clic en la estrella para fijar ese presupuesto como \"Presupuesto Destacado\" en la pantalla de inicio.",
        "Giornaliero" to "Diario",
        "Settimanale" to "Semanal",
        "Mensile" to "Mensual",
        // Goals Screen
        "OBIETTIVI DI RISPARMIO" to "METAS DE AHORRO",
        "Crea Obiettivo" to "Crear Meta",
        "Nuovo Obiettivo di Risparmio" to "Nueva Meta de Ahorro",
        "Nome Obiettivo" to "Nombre de la Meta",
        "Cifra da Raggiungere" to "Monto a Alcanzar",
        "Data Scadenza (es. 31/12/2026)" to "Fecha de Vencimiento (ej. 31/12/2026)",
        "Aggiungi Risparmi" to "Añadir Ahorros",
        "Aggiungi Fondi a" to "Añadir Fondos a",
        "Seleziona Conto di Origine" to "Seleccionar Cuenta de Origen",
        "Importo da spostare" to "Importe a transferir",
        "RAGGIUNTO! 🎉" to "¡ALCANZADO! 🎉",
        "Scadenza:" to "Vencimiento:",
        "Versa fondi" to "Depositar fondos",
        // Reports Screen
        "REPORT FINANZIARI" to "INFORMES FINANCIEROS",
        "DISTRIBUZIONE SPESE MENSILI" to "DISTRIBUCIÓN DE GASTOS MENSUALES",
        "ENTRATE VS USCITE" to "INGRESOS VS GASTOS",
        "Seleziona il Giorno di Inizio Mese Finanziario" to "Selecciona el Día de Inicio del Mes Financiero",
        "Giorno d'inizio:" to "Día de inicio:",
        "Cambia" to "Cambiar",
        "Nessun dato di spesa disponibile per questo mese." to "No hay datos de gastos disponibles para este mes.",
        "Nessun dato disponibile." to "No hay datos disponibles.",
        "Bilancio mensile" to "Balance mensual",
        "Inizio Mese Finanziario" to "Inicio del Mes Financiero",
        "Giorno di partenza del tuo mese finanziario personalizzato:" to "Día de inicio de tu mes financiero personalizado:",
        "Giorno" to "Día",
        // Wizard & Danger Zone Settings
        "Benvenuto su Budgie!" to "¡Bienvenido a Budgie!",
        "Il tuo assistente finanziario personale, sicuro, moderno e 100% offline." to "Tu asistente financiero personal, seguro, moderno y 100% offline.",
        "Seleziona la lingua dell'applicazione:" to "Selecciona el idioma de la aplicación:",
        "Configura il tuo primo conto" to "Configura tu primera cuenta",
        "Registra dove tieni i tuoi fondi per tracciare entrate e spese." to "Registra dónde guardas tus fondos para rastrear ingresos y gastos.",
        "Nome del Conto" to "Nombre de la cuenta",
        "Saldo Iniziale (€)" to "Saldo inicial (€)",
        "Tipologia Conto:" to "Tipo de cuenta:",
        "Conto" to "Cuenta",
        "Contanti" to "Efectivo",
        "Carta" to "Tarjeta",
        "Imposta un Budget Mensile" to "Establece un Presupuesto Mensual",
        "Controlla le tue uscite impostando una soglia limite per una categoria di spesa." to "Controla tus gastos estableciendo un límite para una categoría de gasto.",
        "Abilita Budget iniziale" to "Habilitar Presupuesto inicial",
        "Ti aiuta a monitorare le spese mensili" to "Te ayuda a monitorear los gastos mensuales",
        "Categoria di spesa" to "Categoría de gasto",
        "Limite mensile di spesa (€)" to "Límite mensual de gasto (€)",
        "Tutto pronto!" to "¡Todo listo!",
        "Configurazione completata con successo. Sei pronto a prendere il controllo delle tue finanze personali con Budgie!" to "Configuración completada con éxito. ¡Estás listo para tomar el control de tus finanzas personales con Budgie!",
        "La tua privacy è sacra" to "Tu privacidad es sagrada",
        "Tutti i tuoi dati sono salvati esclusivamente in locale sul tuo telefono e non vengono mai trasmessi a server esterni." to "Todos tus datos se guardan exclusivamente de forma local en tu teléfono y nunca se transmiten a servidores externos.",
        "Entra in Budgie" to "Entrar en Budgie",
        "ZONA DI PERICOLO" to "ZONA DE PELIGRO",
        "Ripristina Dati" to "Restablecer Datos",
        "Cancella tutti i dati salvati e riavvia l'onboarding." to "Borra todos los datos guardados y reinicia el onboarding.",
        "Sei sicuro di voler resettare?" to "¿Estás seguro de que deseas restablecer?",
        "Questa operazione cancellerà tutti i conti, transazioni, budget e impostazioni. Non è possibile annullare questa acción." to "Esta operación eliminará todas las cuentas, transacciones, presupuestos y ajustes. No se puede deshacer.",
        "Sì, Resetta" to "Sí, restablecer",
        "Inserisci nome" to "Introduce nombre",
        "Nessun conto disponibile" to "No hay cuentas disponibles",
        "Valore non numerico" to "Valor no numérico",
        "Mese Finanziario (Giorno Inizio)" to "Mes financiero (Día de inicio)",
        "Il ciclo mensile terminerà automaticamente il giorno precedente a quello selezionato." to "El ciclo mensual terminará automáticamente el día anterior al seleccionado.",
        "Primo giorno della settimana" to "Primer día de la semana",
        "Seleziona le funzioni da abilitare:" to "Selecciona las funciones para habilitar:",
        "Tema Chiaro o Scuro:" to "Tema Claro u Oscuro:",
        "Chiudi" to "Cerrar",
        "Configurazione Calendario" to "Configuración del Calendario",
        "Storico Notifiche" to "Historial de Notificaciones",
        "Nessuna notifica nello storico." to "No hay notificaciones en el historial.",
        "Nessuna notifica non letta." to "No hay notificaciones sin leer.",
        "Letto" to "Leído",
        "Storico" to "Historial",
        "Elimina tutte" to "Eliminar todas",
        "I tuoi Obiettivi" to "Tus metas",
        "Gestisci i tuoi risparmi virtuali" to "Gestiona tus ahorros virtuales",
        "Nuovo" to "Nuevo",
        "SALVADANAIO VIRTUALE" to "HUCHA VIRTUAL",
        "Fondi totali accantonati dai tuoi conti" to "Fondos totales reservados de tus cuentas",
        "Vincolati" to "Bloqueados",
        "Non Assegnati" to "No asignados",
        "Accantona" to "Reservar",
        "Rilascia" to "Liberar",
        "OBIETTIVI INDIVIDUALI" to "METAS INDIVIDUALES",
        "Svincola tutto" to "Desbloquear todo",
        "Nessun obiettivo creato. Creane uno!" to "No se han creado metas. ¡Crea una!",
        "su" to "de",
        "completato" to "completado",
        "Assegna" to "Asignar",
        "Rimuovi" to "Retirar",
        "Completa" to "Completar",
        "Elimina Obiettivo" to "Eliminar meta",
        "Cifra Target (€)" to "Monto objetivo (€)",
        "Scadenza (es: Estate 2024)" to "Fecha límite (ej. Verano 2024)",
        "Icona personalizzata (max 1 emoji)" to "Icono personalizado (máx. 1 emoji)",
        "Accantona Fondi" to "Reservar fondos",
        "Preleva fittiziamente del denaro da un tuo conto per metterlo nel Salvadanaio Virtuale." to "Retira dinero virtualmente de una de tus cuentas para ponerlo en la hucha virtual.",
        "Quota da accantonare (€)" to "Cantidad a reservar (€)",
        "Seleziona conto di origine:" to "Seleccionar cuenta de origen:",
        "Rilascia Fondi" to "Liberar fondos",
        "Preleva fondi dal Salvadanaio Virtuale per riallinearli come disponibili nel tuo conto." to "Retira fondos de la hucha virtual para realinearlos como disponibles en tu cuenta.",
        "Seleziona conto di destinazione:" to "Seleccionar cuenta de destino:",
        "Accantonato:" to "Reservado:",
        "Assegna Fondi a Obiettivo" to "Asignar fondos a la meta",
        "Disponibili non assegnati:" to "Disponibles no asignados:",
        "Target rimanente:" to "Meta restante:",
        "Importo da vincolare (€)" to "Cantidad a bloquear (€)",
        "Rimuovi Fondi da Obiettivo" to "Retirar fondos de la meta",
        "Rilascia parte dei fondi vincolati a questo obiettivo e riportali nello stato non assegnato del Salvadanaio." to "Libera parte de los fondos bloqueados en esta meta y devuélvelos al estado no asignado de la hucha.",
        "Attualmente vincolati:" to "Actualmente bloqueados:",
        "Importo da svincolare (€)" to "Cantidad a liberar (€)",
        "Completa Obiettivo" to "Completar meta",
        "L'obiettivo verrà chiuso. Vuoi registrare l'acquisto reale come una spesa reale in contabilità?" to "¿La meta se cerrará. ¿Deseas registrar la compra real como un gasto real en la contabilidad?",
        "Registra come spesa reale" to "Registrar como gasto real",
        "Dettagli Spesa Reale:" to "Detalles del gasto real:",
        "Conto di addebito reale:" to "Cuenta de débito real:",
        "Nota/Titolo Spesa" to "Nota/Título del gasto",
        "Tutti i Conti Insieme" to "Todas las cuentas juntas",
        "Conto Eliminato" to "Cuenta eliminada",
        "Spesa corrente:" to "Gasto actual:",
        "di" to "de",
        "Reimposta" to "Restablecer",
        "Nessun budget complessivo impostato. Puoi impostare un limite di spesa totale cumulato o per un conto specifico." to "No se ha establecido ningún presupuesto general. Puedes establecer un límite de gasto total acumulado o para una cuenta específica.",
        "Nessun budget per categoria configurato. Puoi impostare dei limites dedicados para categorías específicas." to "No se ha configurado ningún presupuesto por categoría. Puedes establecer límites dedicados para categorías específicas.",
        "Andamento Spese Personalizzato" to "Tendencia de gastos personalizada",
        "N. Periodi" to "N. Periodos",
        "Giorni" to "Días",
        "Settimane" to "Semanas",
        "Mesi" to "Meses",
        "Set" to "Sem",
        "Nessuna spesa nel periodo selezionato" to "Sin gastos en el período seleccionado",
        "Gestisci conti, carte e contanti" to "Gestionar cuentas, tarjetas y efectivo",
        "Conto Bancario" to "Cuenta bancaria",
        "Carta di Credito" to "Tarjeta de crédito",
        "Contanti" to "Efectivo",
        "Includi nel totale" to "Incluir en el total",
        "Regola Limite Mensile" to "Ajustar límite mensual",
        "Limite Mensile (€)" to "Límite mensual (€)",
        "Modifica Record" to "Editar registro",
        "Conto Origine:" to "Cuenta de origen:",
        "Conto Destinazione:" to "Cuenta de destino:",
        "I Tuoi Conti" to "Mis Cuentas",
        "Nuova Categoria Spesa" to "Nueva categoría de gastos",
        "Nuova Categoria Entrata" to "Nueva categoría de ingresos",
        "Emoji Icona" to "Emoji de icono",
        "Inserisci un singolo carattere emoji valido" to "Introduce un único carácter emoji válido",
        "Carattere emoji valido" to "Carácter emoji válido",
        "Nessuna (Principale)" to "Ninguna (Principal)",
        "Seleziona Sottocategoria:" to "Seleccionar subcategoría:",
        "Seleziona Ambito:" to "Seleccionar ámbito:",
        "Ambito" to "Ámbito",
        "Ambito (Conto)" to "Ámbito (Cuenta)",
        "Salva Modifiche" to "Guardar cambios",
        "Nota: hai dei fondi vincolati ad obiettivi individuali. Per rilasciare più di" to "Nota: tienes fondos bloqueados para metas individuales. Para liberar más de",
        "rimuovi prima i fondi dagli obiettivi." to "retira primero los fondos de las metas.",
        "Quota da rilasciare (€) - max" to "Cantidad a liberar (€) - máx.",
        "Destina parte dei risparmi non assegnati del Salvadanaio all'obiettivo:" to "Destina parte de los ahorros no asignados de la hucha a la meta: ",
        "Imposta intero importo previsto" to "Establecer cantidad prevista total",
        "Rimuovi tutto" to "Retirar todo",
        "Congratulazioni per aver completato l'obiettivo:" to "¡Felicitaciones por completar la meta!",
        "L'obiettivo verrà chiuso. Vuoi registrare l'acquisto reale come una spesa reale in contabilità?" to "La meta se cerrará. ¿Quieres registrar la compra real como un gasto real en la contabilidad?",
        "Registra come spesa reale" to "Registrar como gasto real",
        "Dettagli Spesa Reale:" to "Detalles del gasto real:",
        "Conto di addebito reale:" to "Cuenta de cargo real:",
        "Nota/Titolo Spesa" to "Nota/Título del gasto",
        "Nessuna" to "Ninguno",
        "Completa" to "Completar",
        "Accantonati:" to "Reservados:",
        "Reale:" to "Real:",
        "Attività Recenti" to "Actividad reciente",
        "Vedi Tutto" to "Ver todo",
        "Budget Totali" to "Presupuestos totales",
        "Imposta" to "Establecer",
        "Nessun budget complessivo impostato. Puoi impostare un limite di spesa totale cumulato o per un conto specifico." to "No se ha establecido ningún presupuesto general. Puedes establecer un límite de gasto total acumulado o para una cuenta específica.",
        "A quale conto destinare:" to "¿A qué cuenta destinar?",
        "A:" to "A:",
        "Da:" to "De:",
        "Data e Ora (gg/mm/aaaa oo:mm)" to "Fecha y hora (dd/mm/aaaa hh:mm)",
        "Entrate 💰" to "Ingresos 💰",
        "Esporta e Salva su Disco" to "Exportar y guardar en disco",
        "Importo (€)" to "Importe (€)",
        "Invia via Email come Allegato" to "Enviar por correo electrónico como archivo adjunto",
        "Limite di Spesa (€)" to "Límite de gasto (€)",
        "Modifica Budget" to "Editar presupuesto",
        "Nota (massimo 22 caratteri)" to "Nota (máximo 22 caracteres)",
        "Notifiche Push" to "Notificaciones push",
        "Periodo di Validità" to "Periodo de validez",
        "Periodo di Validità:" to "Periodo de validez:",
        "Rimani aggiornato sulle tue spese in tempo reale." to "Mantente al día con tus gastos en tiempo real.",
        "Seleziona Conto:" to "Seleccionar cuenta:",
        "Spese 💸" to "Gastos 💸",
        "Totale" to "Total",
        "transazioni" to "transacciones",
        "Nuova Spesa" to "Nuevo gasto",
        "Nuova Entrata" to "Nuevo ingreso",
        "Nuovo Giroconto" to "Nueva transferencia",
        "Budget per Categoria" to "Presupuestos por categoría",
        "Categoria" to "Categoría",
        "Sottocategoria" to "Subcategoría",
        "Filtra per Categoria:" to "Filtrar por categoría:",
        "Spesa per Categoria" to "Gasto por categoría",
        "Esporta Transazioni" to "Exportar Transacciones",
        "Genera un report in formato CSV compatibile con Excel e software di contabilidad." to "Genera un informe CSV compatible con Excel y software de contabilidad.",
        "PERIODO DI ESPORTAZIONE" to "PERÍODO DE EXPORTACIÓN",
        "Transazioni reali trovate nel periodo" to "Transacciones reales encontradas en el período",
        "(I risparmi e gli accantonamenti virtuali degli obiettivi sono automaticamente esclusi dall'esportazione)" to "(Los ahorros y asignaciones virtuales de las metas se excluyen automáticamente de la exportación)",
        "Andamento Finanziario" to "Desempeño financiero",
        "Settimana" to "Semana",
        "Mese" to "Mes",
        "Anno" to "Año",
        "Entrate:" to "Ingresos:",
        "Spese:" to "Gastos:",
        "Risparmio netto:" to "Ahorro neto:",
        "Mostra Giroconti Interni" to "Mostrar Transferencias Internas",
        "Visualizza movimenti tra i tuoi conti personali." to "Ver transacciones entre tus cuentas personales.",
        "Cerca transazione..." to "Buscar transacción...",
        "Nessuna transazione trovata." to "No se encontraron transacciones.",
        "Nuovo Obiettivo" to "Nueva Meta",
        "Tipo:" to "Tipo:",
        "Modalità:" to "Modalidad:",
        "Ricorrente" to "Recurrente",
        "Programmato" to "Programado",
        "Unità" to "Unidad",
        "CALENDARIO E PERIODI" to "CALENDARIO Y PERÍODOS",
        "COMUNICAZIONE" to "COMUNICACIÓN",
        "PERSONALIZZAZIONE FUNZIONI" to "PERSONALIZACIÓN DE FUNCIONES",
        "Conto Addebito Predefinito" to "Cuenta de Débito Predeterminada",
        "Seleziona il conto su cui addebitare di default la spesa rapida per" to "Selecciona la cuenta en la que adeudar por defecto el gasto rápido para",
        "Salta / Nessuno" to "Omitir / Ninguno",
        "Inizio Settimana" to "Inicio de la semana"
    )

    private val ca = mapOf(
        "(Tutte le sottocategorie)" to "(Totes les subcategories)",
        "Dashboard" to "Taulell de Control",
        "Cronologia" to "Historial",
        "Statistiche" to "Estadístiques",
        "Budget" to "Pressupostos",
        "Obiettivi" to "Objectius",
        "Categorie" to "Categories",
        "Impostazioni" to "Configuració",
        "Conti" to "Comptes",
        "Utente Premium" to "Usuari Premium",
        "Gestione Conti" to "Gestió de Comptes",
        "Notifiche di Budgie 🦜" to "Notificacions de Budgie 🦜",
        "Nessuna notifica presente al momento." to "Cap notificació de moment.",
        "Chiudi" to "Tancar",
        // Settings Screen
        "IMPOSTAZIONI" to "CONFIGURACIÓ",
        "ATTIVAZIONE MODULI" to "ACTIVACIÓ DE MÒDULS",
        "Modulo Budget" to "Mòdul de Pressupostos",
        "Modulo Obiettivi" to "Mòdul d'Objectius",
        "Modulo Report" to "Mòdul d'Estadístiques",
        "Modulo Categorie" to "Mòdul de Categories",
        "GENERALE" to "GENERAL",
        "Lingua" to "Idioma",
        "Tema" to "Tema",
        "Valuta" to "Moneda",
        "Valuta:" to "Moneda:",
        "Valuta del Conto:" to "Moneda del compte:",
        "Tutte le valute..." to "Totes les monedes...",
        "Valuta dell'applicazione:" to "Moneda de l'aplicació:",
        "Seleziona la valuta dell'applicazione:" to "Selecciona la moneda de l'aplicació:",
        "Complessivo" to "General",
        "Tutti i Conti" to "Tots els comptes",
        "di" to "de",
        "Pianificato" to "Programat",
        "Chiaro" to "Clar",
        "Scuro" to "Fosc",
        "Progettato per la tua serenità finanziaria" to "Dissenyat per a la teva tranquil·litat financera",
        "Sviluppatore:" to "Desenvolupador:",
        "Licenza:" to "Llicència:",
        "Annulla" to "Cancel·lar",
        "Salva" to "Desar",
        "Crea" to "Crear",
        "Aggiungi" to "Afegir",
        "Elimina" to "Eliminar",
        // Dashboard / Home Screen
        "NOTIFICHE" to "NOTIFICACIONS",
        "MESSAGGIO DI BENVENUTO" to "MISSATGE DE BENVINGUDA",
        "SALDO TOTALE" to "SALDO TOTAL",
        "Saldo Totale" to "Saldo Total",
        "Entrate" to "Ingressos",
        "Spese" to "Despeses",
        "ENTRATE" to "INGRESSOS",
        "SPESE" to "DESPESES",
        "GIROCONTO" to "TRANSFERÈNCIA",
        "Nuova Transazione" to "Nova Transacció",
        "Tip del Pappagallino" to "Consell de Budgie",
        "Seleziona, nella sezione Categorie max 8 spese rapide" to "Selecciona fins a 8 accions ràpides a la pestanya Categories",
        "Inserimento Rapido" to "Acció Ràpida",
        "Importo" to "Import",
        "Salva Transazione" to "Desar Transacció",
        "Valore non valido" to "Valor no vàlid",
        "Conto Sorgente" to "Compte d'Origen",
        "Seleziona Conto" to "Seleccionar Compte",
        "Seleziona Categoria" to "Seleccionar Categoria",
        "Seleziona Sottocategoria" to "Seleccionar Subcategoria",
        "Aggiungi Transazione" to "Afegir Transacció",
        "Nessuna Transazione" to "Sense Transaccions",
        "Nessuna transazione registrata." to "No hi ha transaccions registrades.",
        "Aggiungi transazione rapida" to "Afegir transacció ràpida",
        "Inserimento rapido" to "Entrada Ràpida",
        "Importo Spesa" to "Import de la Despesa",
        "Inserisci l'importo per" to "Introdueix l'import per",
        "Inserisci un importo valido" to "Introdueix un import vàlid",
        "Conferma" to "Confirmar",
        "Spesa" to "Despesa",
        "Entrata" to "Ingrés",
        "Giroconto" to "Transferència",
        "I MIEI CONTI" to "ELS MEUS COMPTES",
        "Tutti i conti" to "Tots els comptes",
        "Nuovo Conto" to "Nou Compte",
        "INSERIMENTI RAPIDI" to "ENTRADES RÀPIDES",
        // Category Screen
        "Aggiungi Categoria" to "Afegir Categoria",
        "Nome Categoria" to "Nom de Categoria",
        "Emoji Icona" to "Icona Emoji",
        "Inserisci un singolo carattere emoji valido" to "Introdueix un sol caràcter emoji vàlid",
        "Carattere emoji valido" to "Caràcter emoji vàlid",
        "Sottocategoria di (Opzionale):" to "Subcategoria de (Opcional):",
        "Sottocategoria di" to "Subcategoria de",
        "Nessuna (Principale)" to "Cap (Principal)",
        "Modifica Categoria" to "Editar Categoria",
        "Modifica Sottocategoria" to "Editar Subcategoria",
        "Nome" to "Nom",
        "Emoji" to "Emoji",
        "Nuovo Budget di Categoria" to "Nou Pressupost de Categoria",
        "Nuovo Budget Complessivo" to "Nou Pressupost General",
        "Seleziona Categoria:" to "Seleccionar Categoria:",
        "Nessuna Categoria" to "Sense Categoria",
        "Sottocategoria (Opzionale):" to "Subcategoria (Opcional):",
        "Tutte (Intera Categoria)" to "Totes (Categoria Completa)",
        "Notifica allo sforamento" to "Notificar en superar el límit",
        "Rinnova automaticamente" to "Renovar automàticament",
        "Mostra nella Home" to "Mostrar a la Home",
        "Aggiungi Sottocategoria" to "Afegir Subcategoria",
        "Nuova Sottocategoria per" to "Nova Subcategoria per",
        "Nome Sottocategoria" to "Nom de Subcategoria",
        "ENTRATE E USCITE" to "INGRESSOS I DESPESES",
        "GESTIONE" to "GESTIÓ",
        "Crea Categoria" to "Crear Categoria",
        "Legenda: Clicca sulla stellina per impostarla come inserimento rapido sulla Home (Max 8)." to "Llegenda: Toca l'estrella per definir-la com a acció ràpida a la pantalla d'inici (Màx 8).",
        "Non puoi selezionare più di 8 inserimenti rapidi!" to "No pots seleccionar més de 8 entrades ràpides!",
        "Budget in Evidenza" to "Pressupost Destacat",
        "Rinnovo auto" to "Auto-renovació",
        "Notifica attiva" to "Notificació activa",
        "SCADUTO ⏱️" to "EXPIRAT ⏱️",
        "SFORATO 🚨" to "SUPERAT 🚨",
        "Cerca categorie..." to "Cercar categories...",
        "Attenzione" to "Atenció",
        // Accounts Screen
        "Nome Conto" to "Nom del Compte",
        "Tipo Conto" to "Tipus de Compte",
        "Conto Bancario" to "Compte Bancari",
        "Carta di Credito" to "Targeta de Crèdit",
        "Contante" to "Efectiu",
        "Includi nel calcolo del saldo totale" to "Incloure en el càlcul del saldo total",
        "SALDO ATTUALE" to "SALDO ACTUAL",
        "DETTAGLIO CONTO" to "DETALL DEL COMPTE",
        "Modifica Conto" to "Editar Compte",
        "Escluso dal totale" to "Exclòs del total",
        "Conto" to "Compte",
        "Scegli l'icona:" to "Tria la icona:",
        // Pianificazione
        "Modulo Pianificazione" to "Mòdul de Planificació",
        "Pianificazione" to "Planificació",
        "Imposta transazioni future o ricorrenti. Budgie le registrerà automaticamente." to "Configura transaccions futures o recurrentes. Budgie les registrarà automàticament.",
        "Transazioni Pianificate" to "Transaccions Planificades",
        "Nessuna transazione pianificata." to "No hi ha transaccions planificades.",
        "Ogni" to "Cada",
        "Conto sorgente:" to "Compte d'origen:",
        "Conto destinazione:" to "Compte de destinació:",
        "Categoria:" to "Categoria:",
        "Data inizio:" to "Data d'inici:",
        "Prossimo:" to "Següent:",
        "Aggiungi Pianificazione" to "Afegir Planificació",
        "Nuova Pianificazione" to "Nova Planificació",
        "Pianifica" to "Planificar",
        "Errore: la data deve essere futura." to "Error: la data ha de ser futura.",
        "Errore: inserisci un importo valido." to "Error: introdueix un import vàlid.",
        "Errore: inserisci un intervallo valido per la ricorrenza." to "Error: introdueix un interval vàlid per a la recurrència.",
        "Errore: seleziona un conto di destinazione." to "Error: selecciona un compte de destinació.",
        "Errore: inserisci un nome per la pianificazione." to "Error: introdueix un nom per a la planificació.",
        "Nome Pianificazione" to "Nom de la Planificació",
        "Step 1 di 2" to "Pas 1 de 2",
        "Step 2 di 2" to "Pas 2 de 2",
        "Avanti" to "Següent",
        "Indietro" to "Enrere",
        "Da quale data iniziare" to "Des de quina data començar",
        "Data transazione" to "Data de transacció",
        "Seleziona Categoria" to "Seleccionar Categoria",
        "Seleziona Sottocategoria" to "Seleccionar Subcategoria",
        "Nessuna sottocategoria" to "Sense subcategoria",
        "giorni" to "dies",
        "settimane" to "setmanes",
        "mesi" to "mesos",
        // History Screen
        "CRONOLOGIA" to "HISTORIAL",
        "FILTRA PER CONTO" to "FILTRAR PER COMPTE",
        "Cerca per titolo..." to "Cercar per títol...",
        "Tutti i Tipi" to "Tots els Tipus",
        "Filtra per Categoria" to "Filtrar per Categoria",
        "Modifica Transazione" to "Editar Transacció",
        "Data e Ora" to "Data i Hora",
        "Giroconti" to "Transferències",
        "Dettaglio Spese" to "Detall de Despeses",
        "Modifica" to "Editar",
        // Budgets Screen
        "I MIEI LIMITI DI SPESA" to "ELS MEUS LÍMITS DE DESPESA",
        "Crea Budget" to "Crear Pressupost",
        "Regola Limite Mensile" to "Ajustar Límit Mensual",
        "SPESE MENSILI CORRENTI" to "DESPESES MENSUALS ACTUALS",
        "SPESE SETTIMANALI CORRENTI" to "DESPESES SETMANALS ACTUALS",
        "Giroconti esclusi" to "Transferències excloses",
        "Legenda: Clicca sulla stellina per fissare quel budget come \"Budget in Evidenza\" nella Home dell'applicazione." to "Llegenda: Fes clic a l'estrella per fixar aquest pressupost com a \"Pressupost Destacat\" a la pantalla d'inici.",
        "Giornaliero" to "Diari",
        "Settimanale" to "Setmanal",
        "Mensile" to "Mensual",
        // Goals Screen
        "OBIETTIVI DI RISPARMIO" to "OBJECTIUS D'ESTALVI",
        "Crea Obiettivo" to "Crear Objectiu",
        "Nuovo Obiettivo di Risparmio" to "Nou Objectiu d'Estalvi",
        "Nome Obiettivo" to "Nom de l'Objectiu",
        "Cifra da Raggiungere" to "Quantitat a Assolir",
        "Data Scadenza (es. 31/12/2026)" to "Data de Venciment (ex. 31/12/2026)",
        "Aggiungi Risparmi" to "Afegir Estalvis",
        "Aggiungi Fondi a" to "Afegir Fons a",
        "Seleziona Conto di Origine" to "Seleccionar Compte d'Origen",
        "Importo da spostare" to "Import a transferir",
        "RAGGIUNTO! 🎉" to "ASSOLIT! 🎉",
        "Scadenza:" to "Venciment:",
        "Versa fondi" to "Dipositar fons",
        // Reports Screen
        "REPORT FINANZIARI" to "INFORMES FINANCERS",
        "DISTRIBUZIONE SPESE MENSILI" to "DISTRIBUCIÓ DE DESPESES MENSUALS",
        "ENTRATE VS USCITE" to "INGRESSOS VS DESPESES",
        "Seleziona il Giorno di Inizio Mese Finanziario" to "Selecciona el Dia d'Inici del Mes Financer",
        "Giorno d'inizio:" to "Dia d'inici:",
        "Cambia" to "Canviar",
        "Nessun dato di spesa disponibile per questo mese." to "No hi ha dades de despeses disponibles per a aquest mes.",
        "Nessun dato disponibile." to "No hi ha dades disponibles.",
        "Bilancio mensile" to "Balanç mensual",
        "Inizio Mese Finanziario" to "Inici del Mes Financer",
        "Giorno di partenza del tuo mese finanziario personalizzato:" to "Dia de partida del teu mes financer personalitzat:",
        "Giorno" to "Dia",
        // Wizard & Danger Zone Settings
        "Benvenuto su Budgie!" to "Benvingut a Budgie!",
        "Il tuo assistente finanziario personale, sicuro, moderno i 100% offline." to "El teu assistent financer personal, segur, modern i 100% offline.",
        "Seleziona la lingua dell'applicazione:" to "Selecciona l'idioma de l'aplicació:",
        "Configura il tuo primo conto" to "Configura el teu primer compte",
        "Registra dove tieni i tuoi fondi per tracciare entrate e spese." to "Registra on guardes els teus fons per fer un seguiment d'ingressos i despeses.",
        "Nome del Conto" to "Nom del compte",
        "Saldo Iniziale (€)" to "Saldo inicial (€)",
        "Tipologia Conto:" to "Tipus de compte:",
        "Conto" to "Compte",
        "Contanti" to "Efectiu",
        "Carta" to "Targeta",
        "Imposta un Budget Mensile" to "Estableix un Pressupost Mensual",
        "Controlla le tue uscite impostando una soglia limite per una categoria di spesa." to "Controla les teves despeses establint un límit per a una categoria de despesa.",
        "Abilita Budget iniziale" to "Habilitar Pressupost inicial",
        "Ti aiuta a monitorare le spese mensili" to "T'ajuda a fer un seguiment de les despeses mensuals",
        "Categoria di spesa" to "Categoria de despesa",
        "Limite mensile di spesa (€)" to "Límit mensual de despesa (€)",
        "Tutto pronto!" to "Tot a punt!",
        "Configurazione completata con successo. Sei pronto a prendere il controllo delle tue finanze personali con Budgie!" to "Configuració completada amb èxit. Ja estàs a punt per prendre el control de les teves finances personals amb Budgie!",
        "La tua privacy è sacra" to "La teva privadesa és sagrada",
        "Tutti i tuoi dati sono salvati esclusivamente in locale sul tuo telefono e non vengono mai trasmessi a server esterni." to "Totes les teves dades es guarden exclusivament en local al teu telèfon i mai es transmeten a servidors externs.",
        "Entra in Budgie" to "Entra a Budgie",
        "ZONA DI PERICOLO" to "ZONA DE PERILL",
        "Ripristina Dati" to "Restablir Dades",
        "Cancella tutti i dati salvati e riavvia l'onboarding." to "Esborra totes les dades desades i reinicia l'onboarding.",
        "Sei sicuro di voler resettare?" to "Estàs segur que vols restablir?",
        "Questa operazione cancellerà tutti i conti, transazioni, budget e impostazioni. No es pot desfer aquesta acció." to "Aquesta operació suprimirà tots els comptes, transaccions, pressupostos i configuracions. No es pot desfer.",
        "Sì, Resetta" to "Sí, restablir",
        "Inserisci nome" to "Introdueix nom",
        "Nessun conto disponibile" to "No hi ha comptes disponibles",
        "Valore non numerico" to "Valor no numèric",
        "Mese Finanziario (Giorno Inizio)" to "Mes financer (Dia d'inici)",
        "Il ciclo mensile terminerà automaticamente il giorno precedente a quello selezionato." to "El cicle mensual acabarà automàticament el dia anterior al seleccionat.",
        "Primo giorno della settimana" to "Primer dia de la setmana",
        "Seleziona le funzioni da abilitare:" to "Selecciona les funcions a habilitar:",
        "Tema Chiaro o Scuro:" to "Tema Clar o Fosc:",
        "Chiudi" to "Tancar",
        "Configurazione Calendario" to "Configuració del Calendari",
        "Storico Notifiche" to "Historial de Notificacions",
        "Nessuna notifica nello storico." to "Cap notificació a l'historial.",
        "Nessuna notifica non letta." to "Cap notificació sense llegir.",
        "Letto" to "Llegit",
        "Storico" to "Historial",
        "Elimina tutte" to "Eliminar totes",
        "I tuoi Obiettivi" to "Els teus objectius",
        "Gestisci i tuoi risparmi virtuali" to "Gestiona els teus estalvis virtuals",
        "Nuovo" to "Nou",
        "SALVADANAIO VIRTUALE" to "FARMACIOLA VIRTUAL",
        "Fondi totali accantonati dai tuoi conti" to "Fons totals reservats dels teus comptes",
        "Vincolati" to "Bloqueats",
        "Non Assegnati" to "No assignats",
        "Accantona" to "Reservar",
        "Rilascia" to "Alliberar",
        "OBIETTIVI INDIVIDUALI" to "OBJECTIUS INDIVIDUALS",
        "Svincola tutto" to "Desbloquejar-ho tot",
        "Nessun obiettivo creato. Creane uno!" to "No s'ha creat cap objectiu. Crea'n un!",
        "su" to "de",
        "completato" to "completat",
        "Assegna" to "Assignar",
        "Rimuovi" to "Retirar",
        "Completa" to "Completar",
        "Elimina Obiettivo" to "Eliminar objectiu",
        "Cifra Target (€)" to "Import de la meta (€)",
        "Scadenza (es: Estate 2024)" to "Data límit (ex. Estiu 2024)",
        "Icona personalizzata (max 1 emoji)" to "Icona personalitzada (màx. 1 emoji)",
        "Accantona Fondi" to "Reservar fons",
        "Preleva fittiziamente del denaro da un tuo conto per metterlo nel Salvadanaio Virtuale." to "Retira diners virtualment d'un dels teus comptes per posar-los a la farmaciola virtual.",
        "Quota da accantonare (€)" to "Quantitat a reservar (€)",
        "Seleziona conto di origine:" to "Seleccionar compte d'origen:",
        "Rilascia Fondi" to "Alliberar fons",
        "Preleva fittiziamente del denaro da un tuo conto per metterlo nel Salvadanaio Virtuale." to "Retira diners virtualment d'un dels teus comptes per posar-los a la farmaciola virtual.",
        "Seleziona conto di destinazione:" to "Seleccionar compte de destí:",
        "Accantonato:" to "Reservat:",
        "Assegna Fondi a Obiettivo" to "Assignar fons a l'objectiu",
        "Disponibili non assegnati:" to "Disponibles no assignats:",
        "Target rimanente:" to "Objectiu restant:",
        "Importo da vincolare (€)" to "Quantitat a bloquejar (€)",
        "Rimuovi Fondi da Obiettivo" to "Retirar fons de l'objectiu",
        "Rilascia parte dei fondi vincolati a questo obiettivo e riportali nello stato non assegnato del Salvadanaio." to "Allibera part dels fons bloquejats en aquest objectiu i torna'ls a l'estat no assignat de la farmaciola.",
        "Attualmente vincolati:" to "Actualment bloquejats:",
        "Importo da svincolare (€)" to "Quantitat a alliberar (€)",
        "Completa Obiettivo" to "Completar objectiu",
        "L'obiettivo verrà chiuso. Vuoi registrare l'acquisto reale come una spesa reale in contabilità?" to "L'objectiu es tancarà. Vols registrar la compra real com una despesa real a la comptabilitat?",
        "Registra come spesa reale" to "Registrar com a despesa real",
        "Dettagli Spesa Reale:" to "Detalls de la despesa real:",
        "Conto di addebito reale:" to "Compte de dèbit real:",
        "Nota/Titolo Spesa" to "Nota/Títol de la despesa",
        "Tutti i Conti Insieme" to "Tots els comptes junts",
        "Conto Eliminato" to "Compte eliminat",
        "Spesa corrente:" to "Despesa actual:",
        "di" to "de",
        "Reimposta" to "Restablir",
        "Nessun budget complessivo impostato. Puoi impostare un limite di spesa totale cumulato o per un conto specifico." to "No s'ha establit cap pressupost general. Pots establir un límit de despesa total acumulat o per a un compte específic.",
        "Nessun budget per categoria configurato. Puoi impostare dei limiti dedicati a categorie specifiche." to "No s'ha configurat cap pressupost per categoria. Pots establir límits dedicats per a categories específiques.",
        "Andamento Spese Personalizzato" to "Tendència de despeses personalitzada",
        "N. Periodi" to "N. Períodes",
        "Giorni" to "Dies",
        "Settimane" to "Setmanes",
        "Mesi" to "Mesos",
        "Set" to "Set",
        "Nessuna spesa nel periodo selezionato" to "Sense despeses en el període seleccionat",
        "Gestisci conti, carte e contanti" to "Gestionar comptes, targetes i efectiu",
        "Conto Bancario" to "Compte bancari",
        "Carta di Credito" to "Targeta de crèdit",
        "Contanti" to "Efectiu",
        "Includi nel totale" to "Inclou al total",
        "Regola Limite Mensile" to "Ajustar límit menstrual",
        "Limite Mensile (€)" to "Límit menstrual (€)",
        "Modifica Record" to "Editar registre",
        "Conto Origine:" to "Compte d'origen:",
        "Conto Destinazione:" to "Compte de destí:",
        "I Tuoi Conti" to "Els meus Comptes",
        "Nuova Categoria Spesa" to "Nova categoria de despeses",
        "Nuova Categoria Entrata" to "Nova categoria d'ingressos",
        "Emoji Icona" to "Emoji d'icona",
        "Inserisci un singolo carattere emoji valido" to "Introdueix un únic caràcter emoji vàlid",
        "Carattere emoji valido" to "Caràcter emoji vàlid",
        "Nessuna (Principale)" to "Cap (Principal)",
        "Seleziona Sottocategoria:" to "Seleccionar subcategoria:",
        "Seleziona Ambito:" to "Seleccionar àmbit:",
        "Ambito" to "Àmbit",
        "Ambito (Conto)" to "Àmbit (Compte)",
        "Salva Modifiche" to "Desar els canvis",
        "Nota: hai dei fondi vincolati ad obiettivi individuali. Per rilasciare più di" to "Nota: tens fons bloquejats per a objectius individuals. Per alliberar més de",
        "rimuovi prima i fondi dagli obiettivi." to "retira primer els fons dels objectius.",
        "Quota da rilasciare (€) - max" to "Quantitat a alliberar (€) - màx.",
        "Destina parte dei risparmi non assegnati del Salvadanaio all'obiettivo:" to "Destina part dels estalvis no assignats de la farmaciola a l'objectiu: ",
        "Imposta intero importo previsto" to "Establir quantitat prevista total",
        "Rimuovi tutto" to "Retirar-ho tot",
        "Congratulazioni per aver completato l'obiettivo:" to "Felicitats per completar l'objectiu!",
        "L'obiettivo verrà chiuso. Vuoi registrare l'acquisto reale come una spesa reale in contabilità?" to "L'objectiu es tancarà. Vols registrar la compra real com una despesa real en la comptabilitat?",
        "Registra come spesa reale" to "Registrar com a despesa real",
        "Dettagli Spesa Reale:" to "Detalls de la despesa real:",
        "Conto di addebito reale:" to "Compte de càrrec real:",
        "Nota/Titolo Spesa" to "Nota/Títol de la despesa",
        "Nessuna" to "Cap",
        "Completa" to "Completar",
        "Accantonati:" to "Reservats:",
        "Reale:" to "Real:",
        "Attività Recenti" to "Activitat recent",
        "Vedi Tutto" to "Veure-ho tot",
        "Budget Totali" to "Pressupostos totals",
        "Imposta" to "Establir",
        "Nessun budget complessivo impostato. Puoi impostare un limite di spesa totale cumulato o per un conto specifico." to "No s'ha establit cap pressupost general. Pots establir un límit de despesa total acumulat o per a un compte específic.",
        "A quale conto destinare:" to "A quin compte destinar:",
        "A:" to "A:",
        "Da:" to "De:",
        "Data e Ora (gg/mm/aaaa oo:mm)" to "Data i hora (dd/mm/aaaa hh:mm)",
        "Entrate 💰" to "Ingressos 💰",
        "Esporta e Salva su Disco" to "Exportar i desar al disc",
        "Importo (€)" to "Import (€)",
        "Invia via Email come Allegato" to "Enviar per correu electrònic com a adjunt",
        "Limite di Spesa (€)" to "Límit de despesa (€)",
        "Modifica Budget" to "Editar pressupost",
        "Nota (massimo 22 caratteri)" to "Nota (màxim 22 caràcters)",
        "Notifiche Push" to "Notificacions push",
        "Periodo di Validità" to "Període de validesa",
        "Periodo di Validità:" to "Període de validesa:",
        "Rimani aggiornato sulle tue spese in tempo reale." to "Mantingues-te al dia de les teves despeses en temps real.",
        "Seleziona Conto:" to "Selecciona compte:",
        "Spese 💸" to "Despeses 💸",
        "Totale" to "Total",
        "transazioni" to "transaccions",
        "Nuova Spesa" to "Nova despesa",
        "Nuova Entrata" to "Nou ingrés",
        "Nuovo Giroconto" to "Nova transferència",
        "Budget per Categoria" to "Pressupostos per categoria",
        "Categoria" to "Categoria",
        "Sottocategoria" to "Subcategoria",
        "Filtra per Categoria:" to "Filtrar per categoria:",
        "Spesa per Categoria" to "Despesa per categoria",
        "Esporta Transazioni" to "Exportar Transaccions",
        "Genera un report in formato CSV compatibile con Excel e software di contabilità." to "Genera un informe CSV compatible amb Excel i programari de comptabilitat.",
        "PERIODO DI ESPORTAZIONE" to "PERÍODE D'EXPORTACIÓ",
        "Transazioni reali trovate nel periodo" to "Transaccions reals trobades en el període",
        "(I risparmi e gli accantonamenti virtuali degli obiettivi sono automaticamente esclusi dall'esportazione)" to "(Els estalvis i assignacions virtuals de les fites s'exclouen automàticament de l'exportació)",
        "Andamento Finanziario" to "Evolució Financera",
        "Settimana" to "Setmana",
        "Mese" to "Mes",
        "Anno" to "Any",
        "Entrate:" to "Ingressos:",
        "Spese:" to "Despeses:",
        "Risparmio netto:" to "Estalvi net:",
        "Mostra Giroconti Interni" to "Mostrar Transferències Internes",
        "Visualizza movimenti tra i tuoi conti personali." to "Mostrar les transaccions entre els teus comptes personals.",
        "Cerca transazione..." to "Cercar transacció...",
        "Nessuna transazione trovata." to "No s'han trobat transaccions.",
        "Nuovo Obiettivo" to "Nou Objectiu",
        "Tipo:" to "Tipus:",
        "Modalità:" to "Modalitat:",
        "Ricorrente" to "Recurrent",
        "Programmato" to "Programat",
        "Unità" to "Unitat",
        "CALENDARIO E PERIODI" to "CALENDARIS I PERÍODES",
        "COMUNICAZIONE" to "COMUNICACIÓ",
        "PERSONALIZZAZIONE FUNZIONI" to "PERSONALITZACIÓ DE FUNCIONS",
        "Conto Addebito Predefinito" to "Compte de Dèbit Predeterminat",
        "Seleziona il conto su cui addebitare di default la spesa rapida per" to "Selecciona el compte on carregar per defecte la despesa ràpida per a",
        "Salta / Nessuno" to "Ometre / Cap",
        "Inizio Settimana" to "Inici de la setmana"
    )

    private val fr = mapOf(
        "(Tutte le sottocategorie)" to "(Toutes les sous-catégories)",
        "Dashboard" to "Tableau de Bord",
        "Cronologia" to "Historique",
        "Statistiche" to "Rapports",
        "Budget" to "Budgets",
        "Obiettivi" to "Objectifs",
        "Categorie" to "Catégories",
        "Impostazioni" to "Paramètres",
        "Conti" to "Comptes",
        "Utente Premium" to "Utilisateur Premium",
        "Gestione Conti" to "Gestion des Comptes",
        "Notifiche di Budgie 🦜" to "Notifications de Budgie 🦜",
        "Nessuna notifica presente al momento." to "Aucune notification pour le moment.",
        "Chiudi" to "Fermer",
        // Settings Screen
        "IMPOSTAZIONI" to "PARAMÈTRES",
        "ATTIVAZIONE MODULI" to "ACTIVATION DES MODULES",
        "Modulo Budget" to "Module Budgets",
        "Modulo Obiettivi" to "Module Objectifs",
        "Modulo Report" to "Module Rapports",
        "Modulo Categorie" to "Module Catégories",
        "GENERALE" to "GÉNÉRAL",
        "Lingua" to "Langue",
        "Tema" to "Thème",
        "Valuta" to "Devise",
        "Valuta:" to "Devise :",
        "Valuta del Conto:" to "Devise du compte :",
        "Tutte le valute..." to "Toutes les devises...",
        "Valuta dell'applicazione:" to "Devise de l'application :",
        "Seleziona la valuta dell'applicazione:" to "Sélectionnez la devise de l'application :",
        "Complessivo" to "Général",
        "Tutti i Conti" to "Tous les comptes",
        "di" to "sur",
        "Pianificato" to "Planifié",
        "Chiaro" to "Clair",
        "Scuro" to "Sombre",
        "Progettato per la tua serenità finanziaria" to "Conçu pour votre sérénité financière",
        "Sviluppatore:" to "Développeur :",
        "Licenza:" to "Licence :",
        "Annulla" to "Annuler",
        "Salva" to "Enregistrer",
        "Crea" to "Créer",
        "Aggiungi" to "Ajouter",
        "Elimina" to "Supprimer",
        // Dashboard / Home Screen
        "NOTIFICHE" to "NOTIFICATIONS",
        "MESSAGGIO DI BENVENUTO" to "MESSAGE DE BIENVENUE",
        "SALDO TOTALE" to "SOLDE TOTAL",
        "Saldo Totale" to "Solde Total",
        "Entrate" to "Revenus",
        "Spese" to "Dépenses",
        "ENTRATE" to "REVENUS",
        "SPESE" to "DÉPENSES",
        "GIROCONTO" to "TRANSFERT",
        "Nuova Transazione" to "Nouvelle Transaction",
        "Tip del Pappagallino" to "Conseil de Budgie",
        "Seleziona, nella sezione Categorie max 8 spese rapide" to "Sélectionnez jusqu'à 8 actions rapides dans l'onglet Catégories",
        "Inserimento Rapido" to "Action Rapide",
        "Importo" to "Montant",
        "Salva Transazione" to "Enregistrer Transaction",
        "Valore non valido" to "Valeur non valide",
        "Conto Sorgente" to "Compte Source",
        "Seleziona Conto" to "Sélectionner Compte",
        "Seleziona Categoria" to "Sélectionner Catégorie",
        "Seleziona Sottocategoria" to "Sélectionner Sous-catégorie",
        "Aggiungi Transazione" to "Ajouter Transaction",
        "Nessuna Transazione" to "Aucune Transaction",
        "Nessuna transazione registrata." to "Aucune transaction enregistrée.",
        "Aggiungi transazione rapida" to "Ajouter une transaction rapide",
        "Inserimento rapido" to "Saisie Rapide",
        "Importo Spesa" to "Montant de la Dépense",
        "Inserisci l'importo per" to "Saisissez le montant pour",
        "Inserisci un importo valido" to "Saisissez un montant valide",
        "Conferma" to "Confirmer",
        "Spesa" to "Dépense",
        "Entrata" to "Revenu",
        "Giroconto" to "Transfert",
        "I MIEI CONTI" to "MES COMPTES",
        "Tutti i conti" to "Tous les comptes",
        "Nuovo Conto" to "Nouveau Compte",
        "INSERIMENTI RAPIDI" to "SAISIES RAPIDES",
        // Category Screen
        "Aggiungi Categoria" to "Ajouter Catégorie",
        "Nome Categoria" to "Nom de la Catégorie",
        "Emoji Icona" to "Icône Emoji",
        "Inserisci un singolo carattere emoji valido" to "Saisissez un seul caractère emoji valide",
        "Carattere emoji valido" to "Caractère emoji valide",
        "Sottocategoria di (Opzionale):" to "Sous-catégorie de (Optionnel) :",
        "Sottocategoria di" to "Sous-catégorie de",
        "Nessuna (Principale)" to "Aucune (Principale)",
        "Modifica Categoria" to "Modifier Catégorie",
        "Modifica Sottocategoria" to "Modifier Sous-catégorie",
        "Nome" to "Nom",
        "Emoji" to "Emoji",
        "Nuovo Budget di Categoria" to "Nouveau Budget de Catégorie",
        "Nuovo Budget Complessivo" to "Nouveau Budget Général",
        "Seleziona Categoria:" to "Sélectionner Catégorie :",
        "Nessuna Categoria" to "Aucune Catégorie",
        "Sottocategoria (Opzionale):" to "Sous-catégorie (Optionnel) :",
        "Tutte (Intera Categoria)" to "Toutes (Catégorie Entière)",
        "Notifica allo sforamento" to "Notifier en cas de dépassement",
        "Rinnova automaticamente" to "Renouveler automatiquement",
        "Mostra nella Home" to "Afficher sur la Home",
        "Aggiungi Sottocategoria" to "Ajouter Sous-catégorie",
        "Nuova Sottocategoria per" to "Nouvelle Sous-catégorie pour",
        "Nome Sottocategoria" to "Nom de la Sous-catégorie",
        "ENTRATE E USCITE" to "REVENUS ET DÉPENSES",
        "GESTIONE" to "GESTION",
        "Crea Categoria" to "Créer Catégorie",
        "Legenda: Clicca sulla stellina per impostarla come inserimento rapido sulla Home (Max 8)." to "Légende: Appuyez sur l'étoile pour la définir comme action rapide sur l'écran d'accueil (Max 8).",
        "Non puoi selezionare più di 8 inserimenti rapidi!" to "Vous ne pouvez pas sélectionner plus de 8 saisies rapides !",
        "Budget in Evidenza" to "Budget à la Une",
        "Rinnovo auto" to "Auto-renouvellement",
        "Notifica attiva" to "Notification active",
        "SCADUTO ⏱️" to "EXPIRÉ ⏱️",
        "SFORATO 🚨" to "DÉPASSÉ 🚨",
        "Cerca categorie..." to "Chercher catégories...",
        "Attenzione" to "Attention",
        // Accounts Screen
        "Nome Conto" to "Nom du Compte",
        "Tipo Conto" to "Type de Compte",
        "Conto Bancario" to "Compte Bancaire",
        "Carta di Credito" to "Carte de Crédit",
        "Contante" to "Espèces",
        "Includi nel calcolo del saldo totale" to "Inclure dans le calcul du solde total",
        "SALDO ATTUALE" to "SOLDE ACTUEL",
        "DETTAGLIO CONTO" to "DÉTAIL DU COMPTE",
        "Modifica Conto" to "Modifier Compte",
        "Escluso dal totale" to "Exclu du total",
        "Conto" to "Compte",
        "Scegli l'icona:" to "Choisissez l'icône :",
        // Pianificazione
        "Modulo Pianificazione" to "Module de Planification",
        "Pianificazione" to "Planification",
        "Imposta transazioni future o ricorrenti. Budgie le registrerà automaticamente." to "Configurez des transactions futures ou récurrentes. Budgie les enregistrera automatiquement.",
        "Transazioni Pianificate" to "Transactions Planifiées",
        "Nessuna transazione pianificata." to "Aucune transaction planifiée.",
        "Ogni" to "Chaque",
        "Conto sorgente:" to "Compte source :",
        "Conto destinazione:" to "Compte de destination :",
        "Categoria:" to "Catégorie :",
        "Data inizio:" to "Date de début :",
        "Prossimo:" to "Prochain :",
        "Aggiungi Pianificazione" to "Ajouter Planification",
        "Nuova Pianificazione" to "Nouvelle Planification",
        "Pianifica" to "Planifier",
        "Errore: la data deve essere futura." to "Erreur: la date doit être dans le futur.",
        "Errore: inserisci un importo valido." to "Erreur: saisissez un montant valide.",
        "Errore: inserisci un intervallo valido per la ricorrenza." to "Erreur: saisissez un intervalle valide pour la récurrence.",
        "Errore: seleziona un conto di destinazione." to "Erreur: sélectionnez un compte de destination.",
        "Errore: inserisci un nome per la pianificazione." to "Erreur: saisissez un nom pour la planification.",
        "Nome Pianificazione" to "Nom de la Planification",
        "Step 1 di 2" to "Étape 1 sur 2",
        "Step 2 di 2" to "Étape 2 sur 2",
        "Avanti" to "Suivant",
        "Indietro" to "Retour",
        "Da quale data iniziare" to "À partir de quelle date commencer",
        "Data transazione" to "Date de transaction",
        "Seleziona Categoria" to "Sélectionner Catégorie",
        "Seleziona Sottocategoria" to "Sélectionner Sous-catégorie",
        "Nessuna sottocategoria" to "Aucune sous-catégorie",
        "giorni" to "jours",
        "settimane" to "semaines",
        "mesi" to "mois",
        // History Screen
        "CRONOLOGIA" to "HISTORIQUE",
        "FILTRA PER CONTO" to "FILTRER PAR COMPTE",
        "Cerca per titolo..." to "Rechercher par titre...",
        "Tutti i Tipi" to "Tous les Types",
        "Filtra per Categoria" to "Filtrer par Catégorie",
        "Modifica Transazione" to "Modifier Transaction",
        "Data e Ora" to "Date et Heure",
        "Giroconti" to "Transferts",
        "Dettaglio Spese" to "Détail des Dépenses",
        "Modifica" to "Modifier",
        // Budgets Screen
        "I MIEI LIMITI DI SPESA" to "MES LIMITES DE DÉPENSES",
        "Crea Budget" to "Créer Budget",
        "Regola Limite Mensile" to "Ajuster la Limite Mensuelle",
        "SPESE MENSILI CORRENTI" to "DÉPENSES MENSUELLES ACTUELLES",
        "SPESE SETTIMANALI CORRENTI" to "DÉPENSES HEBDOMADAIRES ACTUELLES",
        "Giroconti esclusi" to "Transferts exclus",
        "Legenda: Clicca sulla stellina per fissare quel budget come \"Budget in Evidenza\" nella Home dell'applicazione." to "Légende: Cliquez sur l'étoile pour épingler ce budget comme \"Budget à la Une\" sur l'écran d'accueil.",
        "Giornaliero" to "Journalier",
        "Settimanale" to "Hebdomadaire",
        "Mensile" to "Mensuel",
        // Goals Screen
        "OBIETTIVI DI RISPARMIO" to "OBJECTIFS D'ÉPARGNE",
        "Crea Obiettivo" to "Créer Objectif",
        "Nuovo Obiettivo di Risparmio" to "Nouvel Objectif d'Épargne",
        "Nome Obiettivo" to "Nom de l'Objectif",
        "Cifra da Raggiungere" to "Montant Cible",
        "Data Scadenza (es. 31/12/2026)" to "Date d'Échéance (ex. 31/12/2026)",
        "Aggiungi Risparmi" to "Ajouter Épargne",
        "Aggiungi Fondi a" to "Ajouter des Fonds à",
        "Seleziona Conto di Origine" to "Sélectionner Compte d'Origine",
        "Importo da spostare" to "Montant à transférer",
        "RAGGIUNTO! 🎉" to "REACHENT! 🎉",
        "Scadenza:" to "Échéance :",
        "Versa fondi" to "Déposer des fonds",
        // Reports Screen
        "REPORT FINANZIARI" to "RAPPORTS FINANCIERS",
        "DISTRIBUZIONE SPESE MENSILI" to "DISTRIBUTION DES DÉPENSES MENSUELLES",
        "ENTRATE VS USCITE" to "REVENUS VS DÉPENSES",
        "Seleziona il Giorno di Inizio Mese Finanziario" to "Sélectionnez le Jour de Début du Mois Financier",
        "Giorno d'inizio:" to "Jour de début :",
        "Cambia" to "Modifier",
        "Nessun dato di spesa disponibile per questo mese." to "Aucune donnée de dépenses disponible pour ce mois.",
        "Nessun dato disponibile." to "Aucune donnée disponible.",
        "Bilancio mensile" to "Bilan mensuel",
        "Inizio Mese Finanziario" to "Début du Mois Financier",
        "Giorno di partenza del tuo mese finanziario personalizzato:" to "Jour de début de votre mois financier personnalisé :",
        "Giorno" to "Jour",
        // Wizard & Danger Zone Settings
        "Benvenuto su Budgie!" to "Bienvenue sur Budgie !",
        "Il tuo assistente finanziario personale, sicuro, moderno e 100% offline." to "Votre assistant financier personnel, sécurisé, moderne et 100% hors ligne.",
        "Seleziona la lingua dell'applicazione:" to "Sélectionnez la langue de l'application :",
        "Configura il tuo primo conto" to "Configurez votre premier compte",
        "Registra dove tieni i tuoi fondi per tracciare entrate e spese." to "Enregistrez l'endroit où vous gardez vos fons pour suivre les revenus et les dépenses.",
        "Nome del Conto" to "Nom du compte",
        "Saldo Iniziale (€)" to "Solde initial (€)",
        "Tipologia Conto:" to "Type de compte :",
        "Conto" to "Compte",
        "Contanti" to "Espèces",
        "Carta" to "Carte",
        "Imposta un Budget Mensile" to "Définissez un Budget Mensuel",
        "Controlla le tue uscite impostando una soglia limite per una categoria di spesa." to "Contrôlez vos dépenses en définissant un seuil limite pour une catégorie de dépenses.",
        "Abilita Budget iniziale" to "Activer le Budget initial",
        "Ti aiuta a monitorare le spese mensili" to "Vous aide à suivre vos dépenses mensuelles",
        "Categoria di spesa" to "Catégorie de dépenses",
        "Limite mensile di spesa (€)" to "Limite mensuelle de dépenses (€)",
        "Tutto pronto!" to "Tout est prêt !",
        "Configurazione completata con successo. Sei pronto a prendere il controllo delle tue finanze personali con Budgie!" to "Configuration réussie. Vous êtes prêt à prendre le contrôle de vos finances personnelles avec Budgie !",
        "La tua privacy è sacra" to "Votre vie privée est sacrée",
        "Tutti i tuoi dati sono salvati esclusivamente in locale sul tuo telefono e non vengono mai trasmessi a server esterni." to "Toutes vos données sont enregistrées exclusivement en local sur votre téléphone et ne sont jamais transmises à des serveurs externes.",
        "Entra in Budgie" to "Entrer dans Budgie",
        "ZONA DI PERICOLO" to "ZONE DE DANGER",
        "Ripristina Dati" to "Réinitialiser les Données",
        "Cancella tutti i dati salvati e riavvia l'onboarding." to "Effacer toutes vos données et recommencer l'onboarding.",
        "Sei sicuro di voler resettare?" to "Êtes-vous sûr de vouloir réinitialiser ?",
        "Questa operazione cancellerà tutti i conti, transazioni, budget e impostazioni. Non è possibile annullare questa azione." to "Cette opération supprimera tous les comptes, transactions, budgets et paramètres. Cette action est irréversible.",
        "Sì, Resetta" to "Oui, réinitialiser",
        "Inserisci nome" to "Saisissez le nom",
        "Nessun conto disponibile" to "Aucun compte disponible",
        "Valore non valido" to "Valeur non valide",
        "Valore non numerico" to "Valeur non numérique",
        "Mese Finanziario (Giorno Inizio)" to "Mois financier (Jour de début)",
        "Il ciclo mensile terminerà automaticamente il giorno precedente a quello selezionato." to "Le cycle mensuel se terminera automatiquement la veille du jour sélectionné.",
        "Primo giorno della settimana" to "Premier jour de la semaine",
        "Seleziona le funzioni da abilitare:" to "Sélectionnez les fonctionnalités à activer :",
        "Tema Chiaro o Scuro:" to "Thème Clair ou Sombre :",
        "Chiudi" to "Fermer",
        "Configurazione Calendario" to "Configuration du Calendrier",
        "Storico Notifiche" to "Historique des Notifications",
        "Nessuna notifica nello storico." to "Aucune notification dans l'historique.",
        "Nessuna notifica non letta." to "Aucune notification non lue.",
        "Letto" to "Lu",
        "Storico" to "Historique",
        "Elimina tutte" to "Tout supprimer",
        "I tuoi Obiettivi" to "Vos objectifs",
        "Gestisci i tuoi risparmi virtuali" to "Gérez votre épargne virtuelle",
        "Nuovo" to "Nouveau",
        "SALVADANAIO VIRTUALE" to "TIRELIRE VIRTUELLE",
        "Fondi totali accantonati dai tuoi conti" to "Fonds totaux mis de côté de vos comptes",
        "Vincolati" to "Bloqués",
        "Non Assegnati" to "Non alloués",
        "Accantona" to "Mettre de côté",
        "Rilascia" to "Libérer",
        "OBIETTIVI INDIVIDUALI" to "OBJECTIFS INDIVIDUELS",
        "Svincola tutto" to "Tout débloquer",
        "Nessun obiettivo creato. Creane uno!" to "Aucun objectif créé. Créez-en un !",
        "su" to "sur",
        "completato" to "complété",
        "Assegna" to "Attribuer",
        "Rimuovi" to "Retirer",
        "Completa" to "Terminer",
        "Elimina Obiettivo" to "Supprimer l'objectif",
        "Cifra Target (€)" to "Montant cible (€)",
        "Scadenza (es: Estate 2024)" to "Échéance (ex. Été 2024)",
        "Icona personalizzata (max 1 emoji)" to "Icône personnalisée (max 1 émoji)",
        "Accantona Fondi" to "Mettre des fonds de côté",
        "Preleva fittiziamente del denaro da un tuo conto per metterlo nel Salvadanaio Virtuale." to "Retirez virtuellement de l'argent de l'un de vos comptes pour le mettre dans la tirelire virtuelle.",
        "Quota da accantonare (€)" to "Montant à mettre de côté (€)",
        "Seleziona conto di origine:" to "Sélectionner le compte d'origine :",
        "Rilascia Fondi" to "Libérer des fonds",
        "Preleva fondi dal Salvadanaio Virtuale per riallinearli come disponibili nel tuo conto." to "Retirez des fonds de la tirelire virtuelle pour les réaligner comme disponibles sur votre compte.",
        "Seleziona conto di destinazione:" to "Sélectionner le compte de destination :",
        "Accantonato:" to "Mis de côté :",
        "Assegna Fondi a Obiettivo" to "Attribuer des fonds à l'objectif",
        "Disponibili non assegnati:" to "Disponibles non alloués :",
        "Target rimanente:" to "Cible restante :",
        "Importo da vincolare (€)" to "Montant à bloquer (€)",
        "Rimuovi Fondi da Obiettivo" to "Retirer des fonds de l'objectif",
        "Rilascia parte dei fondi vincolati a questo obiettivo e riportali nello stato non assegnato del Salvadanaio." to "Libérez une partie des fonds bloqués sur cet objectif et ramenez-les à l'état non alloué de la tirelire.",
        "Attualmente vincolati:" to "Actuellement bloqués :",
        "Importo da svincolare (€)" to "Montant à débloquer (€)",
        "Completa Obiettivo" to "Compléter l'objectif",
        "L'obiettivo verrà chiuso. Vuoi registrare l'acquisto reale come una spesa reale in contabilità?" to "L'objectif sera clôturé. Voulez-vous enregistrer l'achat réel comme une dépense réelle dans la comptabilité ?",
        "Registra come spesa reale" to "Enregistrer comme dépense réelle",
        "Dettagli Spesa Reale:" to "Détails de la dépense réelle :",
        "Conto di addebito reale:" to "Compte de débit réel :",
        "Nota/Titolo Spesa" to "Note/Titre de la dépense",
        "Tutti i Conti Insieme" to "Tous les comptes ensemble",
        "Conto Eliminato" to "Compte supprimé",
        "Spesa corrente:" to "Dépense courante :",
        "di" to "sur",
        "Reimposta" to "Réinitialiser",
        "Nessun budget complessivo impostato. Puoi impostare un limite di spesa totale cumulato o per un conto specifico." to "Aucun budget général défini. Vous pouvez définir une limite de dépenses totale cumulée ou pour un compte spécifique.",
        "Nessun budget per categoria configurato. Puoi impostare dei limites dédiés à des catégories spécifiques." to "Aucun budget par catégorie configuré. Vous pouvez définir des limites dédiées pour des catégories spécifiques.",
        "Andamento Spese Personalizzato" to "Tendance des dépenses personnalisée",
        "N. Periodi" to "Nb. Périodes",
        "Giorni" to "Jours",
        "Settimane" to "Semaines",
        "Mesi" to "Mois",
        "Set" to "Sem",
        "Nessuna spesa nel periodo selezionato" to "Aucune dépense sur la période sélectionnée",
        "Gestisci conti, carte e contanti" to "Gérer les comptes, les cartes et l'espèces",
        "Conto Bancario" to "Compte bancaire",
        "Carta di Credito" to "Carte de crédit",
        "Contanti" to "Espèces",
        "Includi nel totale" to "Inclure dans le total",
        "Regola Limite Mensile" to "Ajuster la limite mensuelle",
        "Limite Mensile (€)" to "Limite mensuelle (€)",
        "Modifica Record" to "Modifier l'enregistrement",
        "Conto Origine:" to "Compte d'origine :",
        "Conto Destinazione:" to "Compte de destination :",
        "I Tuoi Conti" to "Mes Comptes",
        "Nuova Categoria Spesa" to "Nouvelle catégorie de dépenses",
        "Nuova Categoria Entrata" to "Nouvelle catégorie de revenus",
        "Emoji Icona" to "Icône Émoji",
        "Inserisci un singolo carattere emoji valido" to "Saisissez un seul caractère émoji valide",
        "Carattere emoji valido" to "Caractère émoji valide",
        "Nessuna (Principale)" to "Aucune (Principale)",
        "Seleziona Sottocategoria:" to "Sélectionner la sous-catégorie :",
        "Seleziona Ambito:" to "Sélectionner la portée :",
        "Ambito" to "Portée",
        "Ambito (Conto)" to "Portée (Compte)",
        "Salva Modifiche" to "Enregistrer les modifications",
        "Nota: hai dei fondi vincolati ad obiettivi individuali. Per rilasciare più di" to "Note : vous avez des fonds bloqués sur des objectifs individuels. Pour libérer plus de",
        "rimuovi prima i fondi dagli obiettivi." to "retirez d'abord les fonds des objectifs.",
        "Quota da rilasciare (€) - max" to "Montant à libérer (€) - max",
        "Destina parte dei risparmi non assegnati del Salvadanaio all'obiettivo:" to "Allouez une partie de l'épargne non allouée de la tirelire à l'objectif : ",
        "Imposta intero importo previsto" to "Définir le montant total prévu",
        "Rimuovi tutto" to "Tout retirer",
        "Congratulazioni per aver completato l'obiettivo:" to "Félicitations pour avoir atteint votre objectif !",
        "L'obiettivo verrà chiuso. Vuoi registrare l'acquisto reale come una spesa reale in contabilità?" to "L'objectif sera clôturé. Voulez-vous enregistrer l'achat réel comme une dépense réelle en comptabilité ?",
        "Registra come spesa reale" to "Enregistrer comme dépense réelle",
        "Dettagli Spesa Reale:" to "Détails de la dépense réelle :",
        "Conto di addebito reale:" to "Compte de débit réel :",
        "Nota/Titolo Spesa" to "Note/Titre de la dépense",
        "Nessuna" to "Aucun",
        "Completa" to "Compléter",
        "Accantonati:" to "Alloués :",
        "Reale:" to "Réel :",
        "Attività Recenti" to "Activité récente",
        "Vedi Tutto" to "Tout voir",
        "Budget Totali" to "Budgets globaux",
        "Imposta" to "Définir",
        "Nessun budget complessivo impostato. Puoi impostare un limite di spesa totale cumulato o per un conto specifico." to "Aucun budget global défini. Vous pouvez définir une limite de dépenses totale cumulée ou pour un compte spécifique.",
        "A quale conto destinare:" to "À quel compte allouer :",
        "A:" to "À :",
        "Da:" to "De :",
        "Data e Ora (gg/mm/aaaa oo:mm)" to "Date et heure (jj/mm/aaaa hh:mm)",
        "Entrate 💰" to "Revenus 💰",
        "Esporta e Salva su Disco" to "Exporter et enregistrer sur le disque",
        "Importo (€)" to "Montant (€)",
        "Invia via Email come Allegato" to "Envoyer par e-mail en pièce jointe",
        "Limite di Spesa (€)" to "Limite de dépenses (€)",
        "Modifica Budget" to "Modifier le budget",
        "Nota (massimo 22 caratteri)" to "Note (maximum 22 caractères)",
        "Notifiche Push" to "Notifications push",
        "Periodo di Validità" to "Période de validité",
        "Periodo di Validità:" to "Période de validité :",
        "Rimani aggiornato sulle tue spese in tempo reale." to "Restez informé de vos dépenses en temps réel.",
        "Seleziona Conto:" to "Sélectionner un compte :",
        "Spese 💸" to "Dépenses 💸",
        "Totale" to "Total",
        "transazioni" to "transactions",
        "Nuova Spesa" to "Nouvelle dépense",
        "Nuova Entrata" to "Nouveau revenu",
        "Nuovo Giroconto" to "Nouveau transfert",
        "Budget per Categoria" to "Budgets par catégorie",
        "Categoria" to "Catégorie",
        "Sottocategoria" to "Sous-catégorie",
        "Filtra per Categoria:" to "Filtrer par catégorie :",
        "Spesa per Categoria" to "Dépense par catégorie",
        "Esporta Transazioni" to "Exporter les Transactions",
        "Genera un report in formato CSV compatibile con Excel e software di contabilità." to "Générez un rapport CSV compatible avec Excel et les logiciels de comptabilité.",
        "PERIODO DI ESPORTAZIONE" to "PÉRIODE D'EXPORTATION",
        "Transazioni reali trovate nel periodo" to "Transactions réelles trouvées sur la période",
        "(I risparmi e gli accantonamenti virtuali degli obiettivi sono automaticamente esclusi dall'esportazione)" to "(Les épargnes et allocations virtuelles des objectifs sont automatiquement exclues de l'exportation)",
        "Andamento Finanziario" to "Évolution Financière",
        "Settimana" to "Semaine",
        "Mese" to "Mois",
        "Anno" to "Année",
        "Entrate:" to "Revenus:",
        "Spese:" to "Dépenses:",
        "Risparmio netto:" to "Épargne nette:",
        "Mostra Giroconti Interni" to "Afficher les Transferts Internes",
        "Visualizza movimenti tra i tuoi conti personali." to "Afficher les transactions entre vos comptes personnels.",
        "Cerca transazione..." to "Rechercher une transaction...",
        "Nessuna transaction trouvée." to "Aucune transaction trouvée.",
        "Nessuna transazione trovata." to "Aucune transaction trouvée.",
        "Nuovo Obiettivo" to "Nouvel Objectif",
        "Tipo:" to "Type :",
        "Modalità:" to "Mode :",
        "Ricorrente" to "Récurrent",
        "Programmato" to "Planifié",
        "Unità" to "Unité",
        "CALENDARIO E PERIODI" to "CALENDRIER & PÉRIODES",
        "COMUNICAZIONE" to "COMMUNICATION",
        "PERSONALIZZAZIONE FUNZIONI" to "PERSONNALISATION DES FONCTIONNALITÉS",
        "Conto Addebito Predefinito" to "Compte de Débit par Défaut",
        "Seleziona il conto su cui addebitare di default la spesa rapida per" to "Sélectionnez le compte à débiter par défaut pour les dépenses rapides de",
        "Salta / Nessuno" to "Ignorer / Aucun",
        "Inizio Settimana" to "Début de la semaine"
    )

    private val de = mapOf(
        "(Tutte le sottocategorie)" to "(Alle Unterkategorien)",
        "Dashboard" to "Dashboard",
        "Cronologia" to "Verlauf",
        "Statistiche" to "Berichte",
        "Budget" to "Budgets",
        "Obiettivi" to "Ziele",
        "Categorie" to "Kategorien",
        "Impostazioni" to "Einstellungen",
        "Conti" to "Konten",
        "Utente Premium" to "Premium-Benutzer",
        "Gestione Conti" to "Kontoverwaltung",
        "Notifiche di Budgie 🦜" to "Budgie Benachrichtigungen 🦜",
        "Nessuna notifica presente al momento." to "Derzeit keine Benachrichtigungen.",
        "Chiudi" to "Schließen",
        // Settings Screen
        "IMPOSTAZIONI" to "EINSTELLUNGEN",
        "ATTIVAZIONE MODULI" to "MODULE AKTIVIEREN",
        "Modulo Budget" to "Budgetmodul",
        "Modulo Obiettivi" to "Zielmodul",
        "Modulo Report" to "Berichtsmodul",
        "Modulo Categorie" to "Kategoriemodul",
        "GENERALE" to "ALLGEMEIN",
        "Lingua" to "Sprache",
        "Tema" to "Design",
        "Valuta" to "Währung",
        "Valuta:" to "Währung:",
        "Valuta del Conto:" to "Kontowährung:",
        "Tutte le valute..." to "Alle Währungen...",
        "Valuta dell'applicazione:" to "App-Währung:",
        "Seleziona la valuta dell'applicazione:" to "Wähle die App-Währung:",
        "Complessivo" to "Gesamt",
        "Tutti i Conti" to "Alle Konten",
        "di" to "von",
        "Pianificato" to "Geplant",
        "Chiaro" to "Hell",
        "Scuro" to "Dunkel",
        "Progettato per la tua serenità finanziaria" to "Entwickelt für Ihre finanzielle Seelenruhe",
        "Sviluppatore:" to "Entwickler:",
        "Licenza:" to "Lizenz:",
        "Annulla" to "Abbrechen",
        "Salva" to "Speichern",
        "Crea" to "Erstellen",
        "Aggiungi" to "Hinzufügen",
        "Elimina" to "Löschen",
        // Dashboard / Home Screen
        "NOTIFICHE" to "BENACHRICHTIGUNGEN",
        "MESSAGGIO DI BENVENUTO" to "WILLKOMMENSNACHRICHT",
        "SALDO TOTALE" to "GESAMTSALDO",
        "Saldo Totale" to "Gesamtsaldo",
        "Entrate" to "Einnahmen",
        "Spese" to "Ausgaben",
        "ENTRATE" to "EINNAHMEN",
        "SPESE" to "AUSGABEN",
        "GIROCONTO" to "UMBUCHUNG",
        "Nuova Transazione" to "Neue Transaktion",
        "Tip del Pappagallino" to "Budgies Tipp",
        "Seleziona, nella sezione Categorie max 8 spese rapide" to "Wählen Sie in der Rubrik Kategorien bis zu 8 Schnellaktionen aus",
        "Inserimento Rapido" to "Schnellaktion",
        "Importo" to "Betrag",
        "Salva Transazione" to "Transaktion Speichern",
        "Valore non valido" to "Ungültiger Wert",
        "Conto Sorgente" to "Quellkonto",
        "Seleziona Conto" to "Konto Auswählen",
        "Seleziona Categoria" to "Kategorie Auswählen",
        "Seleziona Sottocategoria" to "Unterkategorie Auswählen",
        "Aggiungi Transazione" to "Transaktion Hinzufügen",
        "Nessuna Transazione" to "Keine Transaktionen",
        "Nessuna transazione registrata." to "Keine Transaktionen erfasst.",
        "Aggiungi transazione rapida" to "Schnelltransaktion hinzufügen",
        "Inserimento rapido" to "Schnelleingabe",
        "Importo Spesa" to "Ausgabenbetrag",
        "Inserisci l'importo per" to "Geben Sie den Betrag ein für",
        "Inserisci un importo valido" to "Geben Sie einen gültigen Betrag ein",
        "Conferma" to "Bestätigen",
        "Spesa" to "Ausgabe",
        "Entrata" to "Einnahme",
        "Giroconto" to "Umbuchung",
        "I MIEI CONTI" to "MEINE KONTEN",
        "Tutti i conti" to "Alle Konten",
        "Nuovo Conto" to "Neues Konto",
        "INSERIMENTI RAPIDI" to "SCHNELLEINGABEN",
        // Category Screen
        "Aggiungi Categoria" to "Kategorie Hinzufügen",
        "Nome Categoria" to "Kategoriename",
        "Emoji Icona" to "Emoji-Symbol",
        "Inserisci un singolo carattere emoji valido" to "Geben Sie ein einzelnes gültiges Emoji-Zeichen ein",
        "Carattere emoji valido" to "Gültiges Emoji-Zeichen",
        "Sottocategoria di (Opzionale):" to "Unterkategorie von (Optional):",
        "Sottocategoria di" to "Unterkategorie von",
        "Nessuna (Principale)" to "Keine (Hauptkategorie)",
        "Modifica Categoria" to "Kategorie Bearbeiten",
        "Modifica Sottocategoria" to "Unterkategorie Bearbeiten",
        "Nome" to "Name",
        "Emoji" to "Emoji",
        "Nuovo Budget di Categoria" to "Neues Kategoriebudget",
        "Nuovo Budget Complessivo" to "Neues Gesamtbudget",
        "Seleziona Categoria:" to "Kategorie Auswählen:",
        "Nessuna Categoria" to "Keine Kategorie",
        "Sottocategoria (Opzionale):" to "Unterkategorie (Optional):",
        "Tutte (Intera Categoria)" to "Alle (Ganze Kategorie)",
        "Notifica allo sforamento" to "Bei Überschreitung benachrichtigen",
        "Rinnova automaticamente" to "Automatisch verlängern",
        "Mostra nella Home" to "Auf der Startseite anzeigen",
        "Aggiungi Sottocategoria" to "Unterkategorie Hinzufügen",
        "Nuova Sottocategoria per" to "Neue Unterkategorie für",
        "Nome Sottocategoria" to "Unterkategoriename",
        "ENTRATE E USCITE" to "EINNAHMEN UND AUSGABEN",
        "GESTIONE" to "VERWALTUNG",
        "Crea Categoria" to "Kategorie Erstellen",
        "Legenda: Clicca sulla stellina per impostarla come inserimento rapido sulla Home (Max 8)." to "Legende: Tippen Sie auf den Stern, um es als Schnellaktion auf der Startseite festzulegen (Max. 8).",
        "Non puoi selezionare più di 8 inserimenti rapidi!" to "Sie können nicht mehr als 8 Schnelleingaben auswählen!",
        "Budget in Evidenza" to "Hervorgehobenes Budget",
        "Rinnovo auto" to "Auto-Verlängerung",
        "Notifica attiva" to "Benachrichtigung aktiv",
        "SCADUTO ⏱️" to "ABGELAUFEN ⏱️",
        "SFORATO 🚨" to "ÜBERSCHRITTEN 🚨",
        "Cerca categorie..." to "Kategorien suchen...",
        "Attenzione" to "Achtung",
        // Accounts Screen
        "Nome Conto" to "Kontoname",
        "Tipo Conto" to "Kontotyp",
        "Conto Bancario" to "Bankkonto",
        "Carta di Credito" to "Kreditkarte",
        "Contante" to "Bargeld",
        "Includi nel calcolo del saldo totale" to "In Gesamtsaldoberechnung einbeziehen",
        "SALDO ATTUALE" to "AKTUELLER SALDO",
        "DETTAGLIO CONTO" to "KONTODETAIL",
        "Modifica Conto" to "Konto Bearbeiten",
        "Escluso dal totale" to "Vom Gesamtsaldo ausgeschlossen",
        "Conto" to "Konto",
        "Scegli l'icona:" to "Symbol auswählen:",
        // Pianificazione
        "Modulo Pianificazione" to "Planungsmodul",
        "Pianificazione" to "Planung",
        "Imposta transazioni future o ricorrenti. Budgie le registrerà automaticamente." to "Richten Sie zukünftige oder wiederkehrende Transaktionen ein. Budgie wird sie automatisch erfassen.",
        "Transazioni Pianificate" to "Geplante Transaktionen",
        "Nessuna transazione pianificata." to "Keine geplanten Transaktionen.",
        "Ogni" to "Alle",
        "Conto sorgente:" to "Quellkonto:",
        "Conto destinazione:" to "Zielkonto:",
        "Categoria:" to "Kategorie:",
        "Data inizio:" to "Startdatum:",
        "Prossimo:" to "Nächstes Mal:",
        "Aggiungi Pianificazione" to "Planung Hinzufügen",
        "Nuova Pianificazione" to "Neue Planung",
        "Pianifica" to "Planen",
        "Errore: la data deve essere futura." to "Fehler: Das Datum muss in der Zukunft liegen.",
        "Errore: inserisci un importo valido." to "Fehler: Geben Sie einen gültigen Betrag ein.",
        "Errore: inserisci un intervallo valido per la ricorrenza." to "Fehler: Geben Sie ein gültiges Intervall für die Wiederholung ein.",
        "Errore: seleziona un conto di destinazione." to "Fehler: Wählen Sie ein Zielkonto aus.",
        "Errore: inserisci un nome per la pianificazione." to "Fehler: Geben Sie einen Namen für die Planung ein.",
        "Nome Pianificazione" to "Planungsname",
        "Step 1 di 2" to "Schritt 1 von 2",
        "Step 2 di 2" to "Schritt 2 von 2",
        "Avanti" to "Weiter",
        "Indietro" to "Zurück",
        "Da quale data iniziare" to "Ab welchem Datum starten",
        "Data transazione" to "Transaktionsdatum",
        "Seleziona Categoria" to "Kategorie Auswählen",
        "Seleziona Sottocategoria" to "Unterkategorie Auswählen",
        "Nessuna sottocategoria" to "Keine Unterkategorie",
        "giorni" to "Tage",
        "settimane" to "Wochen",
        "mesi" to "Monate",
        // History Screen
        "CRONOLOGIA" to "VERLAUF",
        "FILTRA PER CONTO" to "NACH KONTO FILTERN",
        "Cerca per titolo..." to "Nach Titel suchen...",
        "Tutti i Tipi" to "Alle Typen",
        "Filtra per Categoria" to "Nach Kategorie Filtern",
        "Modifica Transazione" to "Transaktion Bearbeiten",
        "Data e Ora" to "Datum und Uhrzeit",
        "Giroconti" to "Umbuchungen",
        "Dettaglio Spese" to "Ausgabendetails",
        "Modifica" to "Bearbeiten",
        // Budgets Screen
        "I MIEI LIMITI DI SPESA" to "MEINE AUSGABENLIMITS",
        "Crea Budget" to "Budget Erstellen",
        "Regola Limite Mensile" to "Monatliches Limit Anpassen",
        "SPESE MENSILI CORRENTI" to "AKTUELLE MONATLICHE AUSGABEN",
        "SPESE SETTIMANALI CORRENTI" to "AKTUELLE WÖCHENTLICHE AUSGABEN",
        "Giroconti esclusi" to "Umbuchungen ausgeschlossen",
        "Legenda: Clicca sulla stellina per fissare quel budget come \"Budget in Evidenza\" nella Home dell'applicazione." to "Legende: Klicken Sie auf das Sternchen, um dieses Budget als „Hervorgehobenes Budget“ auf der Startseite festzulegen.",
        "Giornaliero" to "Täglich",
        "Settimanale" to "Wöchentlich",
        "Mensile" to "Monatlich",
        // Goals Screen
        "OBIETTIVI DI RISPARMIO" to "SPARZIELE",
        "Crea Obiettivo" to "Ziel Erstellen",
        "Nuovo Obiettivo di Risparmio" to "Neues Sparziel",
        "Nome Obiettivo" to "Zielname",
        "Cifra da Raggiungere" to "Zielbetrag",
        "Data Scadenza (es. 31/12/2026)" to "Ablaufdatum (z.B. 31/12/2026)",
        "Aggiungi Risparmi" to "Ersparnisse Hinzufügen",
        "Aggiungi Fondi a" to "Mittel Hinzufügen zu",
        "Seleziona Conto di Origine" to "Quellkonto Auswählen",
        "Importo da spostare" to "Zu übertragender Betrag",
        "RAGGIUNTO! 🎉" to "ERREICHT! 🎉",
        "Scadenza:" to "Frist:",
        "Versa fondi" to "Guthaben einzahlen",
        // Reports Screen
        "REPORT FINANZIARI" to "FINANZBERICHTE",
        "DISTRIBUZIONE SPESE MENSILI" to "MONATLICHE AUSGABENVERTEILUNG",
        "ENTRATE VS USCITE" to "EINNAHMEN VS AUSGABEN",
        "Seleziona il Giorno di Inizio Mese Finanziario" to "Wählen Sie den Starttag des Finanzmonats",
        "Giorno d'inizio:" to "Starttag:",
        "Cambia" to "Ändern",
        "Nessun dato di spesa disponibile per questo mese." to "Keine Ausgabendaten für diesen Monat verfügbar.",
        "Nessun dato disponibile." to "Keine Daten verfügbar.",
        "Bilancio mensile" to "Monatliche Bilanz",
        "Inizio Mese Finanziario" to "Beginn des Finanzmonats",
        "Giorno di partenza del tuo mese finanziario personalizzato:" to "Starttag Ihres benutzerdefinierten Finanzmonats:",
        "Giorno" to "Tag",
        // Wizard & Danger Zone Settings
        "Benvenuto su Budgie!" to "Willkommen bei Budgie!",
        "Il tuo assistente finanziario personale, sicuro, moderno und 100% offline." to "Ihr persönlicher Finanzassistent, sicher, modern und 100% offline.",
        "Seleziona la lingua dell'applicazione:" to "Wählen Sie die Sprache der Anwendung:",
        "Configura il tuo primo conto" to "Richten Sie Ihr erstes Konto ein",
        "Registra dove tieni i tuoi fondi per tracciare entrate e spese." to "Registrieren Sie, wo Sie Ihr Geld aufbewahren, um Einnahmen und Ausgaben zu verfolgen.",
        "Nome del Conto" to "Kontoname",
        "Saldo Iniziale (€)" to "Anfangssaldo (€)",
        "Tipologia Conto:" to "Kontotyp:",
        "Conto" to "Konto",
        "Contanti" to "Bargeld",
        "Carta" to "Karte",
        "Imposta un Budget Mensile" to "Richten Sie ein monatliches Budget ein",
        "Controlla le tue uscite impostando una soglia limite per una categoria di spesa." to "Kontrollieren Sie Ihre Ausgaben, indem Sie einen Grenzwert für eine Ausgabenkategorie festlegen.",
        "Abilita Budget iniziale" to "Anfängliches Budget aktivieren",
        "Ti aiuta a monitorare le spese mensili" to "Hilft Ihnen, monatliche Ausgaben zu überwachen",
        "Categoria di spesa" to "Ausgabenkategorie",
        "Limite mensile di spesa (€)" to "Monatliches Ausgabenlimit (€)",
        "Tutto pronto!" to "Alles bereit!",
        "Configurazione completata con successo. Sei pronto a prendere il controllo delle tue finanze personali con Budgie!" to "Einrichtung erfolgreich abgeschlossen. Sie sind bereit, die Kontrolle über Ihre persönlichen Finanzen mit Budgie zu übernehmen!",
        "La tua privacy è sacra" to "Ihre Privatsphäre ist heilig",
        "Tutti i tuoi dati sono salvati esclusivamente in locale sul tuo telefono e non vengono mai trasmessi a server esterni." to "Alle Ihre Daten werden ausschließlich lokal auf Ihrem Telefon gespeichert und niemals an externe Server übertragen.",
        "Entra in Budgie" to "Budgie starten",
        "ZONA DI PERICOLO" to "GEFAHRENBEREICH",
        "Ripristina Dati" to "Daten Zurücksetzen",
        "Cancella tutti i dati salvati e riavvia l'onboarding." to "Löschen Sie alle gespeicherten Daten und starten Sie das Onboarding neu.",
        "Sei sicuro di voler resettare?" to "Sind Sie sicher, dass Sie zurücksetzen möchten?",
        "Questa operazione cancellerà tutti i conti, transazioni, budget e impostazioni. Non è possibile annullare questa azione." to "Dieser Vorgang löscht alle Konten, Transaktionen, Budgets und Einstellungen. Dies kann nicht rückgängig gemacht werden.",
        "Sì, Resetta" to "Ja, zurücksetzen",
        "Inserisci nome" to "Name eingeben",
        "Nessun conto disponibile" to "Keine Konten verfügbar",
        "Valore non valido" to "Ungültiger Wert",
        "Valore non numerico" to "Nicht-numerischer Wert",
        "Mese Finanziario (Giorno Inizio)" to "Finanzmonat (Starttag)",
        "Il ciclo mensile terminerà automaticamente il giorno precedente a quello selezionato." to "Der monatliche Zyklus endet automatisch am Tag vor dem ausgewählten Tag.",
        "Primo giorno della settimana" to "Erster Wochentag",
        "Seleziona le funzioni da abilitare:" to "Wählen Sie die zu aktivierenden Funktionen:",
        "Tema Chiaro o Scuro:" to "Helles oder dunkles Thema:",
        "Chiudi" to "Schließen",
        "Configurazione Calendario" to "Kalender-Einstellung",
        "Storico Notifiche" to "Benachrichtigungungsverlauf",
        "Nessuna notifica nello storico." to "Keine Benachrichtigungen im Verlauf.",
        "Nessuna notifica non letta." to "Keine ungelesenen Benachrichtigungen.",
        "Letto" to "Gelesen",
        "Storico" to "Verlauf",
        "Elimina tutte" to "Alle löschen",
        "I tuoi Obiettivi" to "Ihre Ziele",
        "Gestisci i tuoi risparmi virtuali" to "Verwalten Sie Ihre virtuellen Ersparnisse",
        "Nuovo" to "Neu",
        "SALVADANAIO VIRTUALE" to "VIRTUELLES SPARSCHWEIN",
        "Fondi totali accantonati dai tuoi conti" to "Gesamte von Ihren Konten zurückgelegte Gelder",
        "Vincolati" to "Gesperrt",
        "Non Assegnati" to "Nicht zugewiesen",
        "Accantona" to "Zurücklegen",
        "Rilascia" to "Freigeben",
        "OBIETTIVI INDIVIDUALI" to "INDIVIDUELLE ZIELE",
        "Svincola tutto" to "Alles freigeben",
        "Nessun obiettivo creato. Creane uno!" to "Keine Ziele erstellt. Erstellen Sie eines!",
        "su" to "von",
        "completato" to "abgeschlossen",
        "Assegna" to "Zuweisen",
        "Rimuovi" to "Entfernen",
        "Completa" to "Abschließen",
        "Elimina Obiettivo" to "Ziel löschen",
        "Cifra Target (€)" to "Zielbetrag (€)",
        "Scadenza (es: Estate 2024)" to "Frist (z. B. Sommer 2024)",
        "Icona personalizzata (max 1 emoji)" to "Benutzerdefiniertes Symbol (max. 1 Emoji)",
        "Accantona Fondi" to "Gelder zurücklegen",
        "Preleva fittiziamente del denaro da un tuo conto per metterlo nel Salvadanaio Virtuale." to "Heben Sie virtuell Geld von einem Ihrer Konten ab, um es in das virtuelle Sparschwein einzuzahlen.",
        "Quota da accantonare (€)" to "Zurückzulegender Betrag (€)",
        "Seleziona conto di origine:" to "Herkunftskonto auswählen:",
        "Rilascia Fondi" to "Geld freigeben",
        "Preleva fondi dal Salvadanaio Virtuale per riallinearli come disponibili nel tuo conto." to "Heben Sie Geld vom virtuellen Sparschwein ab, um es wieder als verfügbar auf Ihrem Konto auszurichten.",
        "Seleziona conto di destinazione:" to "Zielkonto auswählen:",
        "Accantonato:" to "Zurückgelegt:",
        "Assegna Fondi a Obiettivo" to "Gelder dem Ziel zuweisen",
        "Disponibili non assegnati:" to "Nicht zugewiesenes Guthaben:",
        "Target rimanente:" to "Verbleibendes Ziel:",
        "Importo da vincolare (€)" to "Zu sperrender Betrag (€)",
        "Rimuovi Fondi da Obiettivo" to "Gelder vom Ziel entfernen",
        "Rilascia parte dei fondi vincolati a questo obiettivo e riportali nello stato non assegnato del Salvadanaio." to "Geben Sie einen Teil der für dieses Ziel gesperrten Gelder frei und führen Sie sie in das nicht zugewiesene Sparschwein zurück.",
        "Attualmente vincolati:" to "Derzeit gesperrt:",
        "Importo da svincolare (€)" to "Freizugebender Betrag (€)",
        "Completa Obiettivo" to "Ziel abschließen",
        "L'obiettivo verrà chiuso. Vuoi registrare l'acquisto reale come una spesa reale in contabilità?" to "Das Ziel wird geschlossen. Möchten Sie den tatsächlichen Kauf als echte Ausgabe in der Buchhaltung erfassen?",
        "Registra come spesa reale" to "Als echte Ausgabe erfassen",
        "Dettagli Spesa Reale:" to "Details der tatsächlichen Ausgabe:",
        "Conto di addebito reale:" to "Echtes Belastungskonto:",
        "Nota/Titolo Spesa" to "Notiz/Ausgabentitel",
        "Tutti i Conti Insieme" to "Alle Konten zusammen",
        "Conto Eliminato" to "Gelöschtes Konto",
        "Spesa corrente:" to "Aktuelle Ausgaben:",
        "di" to "von",
        "Reimposta" to "Zurücksetzen",
        "Nessun budget complessivo impostato. Puoi impostare un limite di spesa totale cumulato o per un conto specifico." to "Kein Gesamtbudget festgelegt. Sie können ein kumuliertes Gesamtausgabenlimit oder für ein bestimmtes Konto festlegen.",
        "Nessun budget per categoria configurato. Puoi impostare dei limiti dedicati a kategorie specifiche." to "Kein Kategoriebudget eingerichtet. Sie können dedizierte Limits für bestimmte Kategorien festlegen.",
        "Andamento Spese Personalizzato" to "Benutzerdefinierter Ausgabentrend",
        "N. Periodi" to "Anz. Zeiträume",
        "Giorni" to "Tage",
        "Settimane" to "Wochen",
        "Mesi" to "Monate",
        "Set" to "Woch",
        "Nessuna spesa nel periodo selezionato" to "Keine Ausgaben im ausgewählten Zeitraum",
        "Gestisci conti, carte e contanti" to "Konten, Karten und Bargeld verwalten",
        "Conto Bancario" to "Bankkonto",
        "Carta di Credito" to "Kreditkarte",
        "Contanti" to "Bargeld",
        "Includi nel totale" to "In Gesamtsumme einschließen",
        "Regola Limite Mensile" to "Monatliches Limit anpassen",
        "Limite Mensile (€)" to "Monatliches Limit (€)",
        "Modifica Record" to "Datensatz bearbeiten",
        "Conto Origine:" to "Herkunftskonto:",
        "Conto Destinazione:" to "Zielkonto:",
        "I Tuoi Conti" to "Meine Konten",
        "Nuova Categoria Spesa" to "Neue Ausgabenkategorie",
        "Nuova Categoria Entrata" to "Neue Einnahmenkategorie",
        "Emoji Icona" to "Emoji-Symbol",
        "Inserisci un singolo carattere emoji valido" to "Geben Sie ein einzelnes gültiges Emoji-Zeichen ein",
        "Carattere emoji valido" to "Gültiges Emoji-Zeichen",
        "Nessuna (Principale)" to "Keine (Hauptkategorie)",
        "Seleziona Sottocategoria:" to "Unterkategorie auswählen:",
        "Seleziona Ambito:" to "Bereich auswählen:",
        "Ambito" to "Bereich",
        "Ambito (Conto)" to "Bereich (Konto)",
        "Salva Modifiche" to "Änderungen speichern",
        "Nota: hai dei fondi vincolati ad obiettivi individuali. Per rilasciare più di" to "Hinweis: Sie haben Gelder für einzelne Ziele gesperrt. Um mehr als freizugeben",
        "rimuovi prima i fondi dagli obiettivi." to "entfernen Sie zuerst die Gelder von den Zielen.",
        "Quota da rilasciare (€) - max" to "Freizugebender Betrag (€) - max",
        "Destina parte dei risparmi non assegnati del Salvadanaio all'obiettivo:" to "Weisen Sie einen Teil der nicht zugewiesenen Ersparnisse des Sparschweins dem Ziel zu: ",
        "Imposta intero importo previsto" to "Gesamten geplanten Betrag festlegen",
        "Rimuovi tutto" to "Alles entfernen",
        "Congratulazioni per aver completato l'obiettivo:" to "Herzlichen Glückwunsch zum Erreichen deines Ziels!",
        "L'obiettivo verrà chiuso. Vuoi registrare l'acquisto reale come una spesa reale in contabilità?" to "Das Ziel wird geschlossen. Möchten Sie den tatsächlichen Kauf als echte Ausgabe in der Buchhaltung erfassen?",
        "Registra come spesa reale" to "Als reale Ausgabe erfassen",
        "Dettagli Spesa Reale:" to "Details zur realen Ausgabe:",
        "Conto di addebito reale:" to "Reales Belastungskonto:",
        "Nota/Titolo Spesa" to "Ausgabennotiz/Titel",
        "Nessuna" to "Keine",
        "Completa" to "Abschließen",
        "Accantonati:" to "Zurückgelegt:",
        "Reale:" to "Echt:",
        "Attività Recenti" to "Kürzliche Aktivität",
        "Vedi Tutto" to "Alles ansehen",
        "Budget Totali" to "Gesamtbudgets",
        "Imposta" to "Festlegen",
        "Nessun budget complessivo impostato. Puoi impostare un limite di spesa totale cumulato o per un conto specifico." to "Kein Gesamtbudget festgelegt. Sie können ein kumuliertes Gesamtausgabenlimit oder ein Limit für ein bestimmtes Konto festlegen.",
        "A quale conto destinare:" to "Auf welches Konto überweisen:",
        "A:" to "An:",
        "Da:" to "Von:",
        "Data e Ora (gg/mm/aaaa oo:mm)" to "Datum und Uhrzeit (TT/MM/JJJJ hh:mm)",
        "Entrate 💰" to "Einnahmen 💰",
        "Esporta e Salva su Disco" to "Exportieren und auf Festplatte speichern",
        "Importo (€)" to "Betrag (€)",
        "Invia via Email come Allegato" to "Per E-Mail als Anhang senden",
        "Limite di Spesa (€)" to "Ausgabenlimit (€)",
        "Modifica Budget" to "Budget bearbeiten",
        "Nota (massimo 22 caratteri)" to "Notiz (maximal 22 Zeichen)",
        "Notifiche Push" to "Push-Benachrichtigungen",
        "Periodo di Validità" to "Gültigkeitszeitraum",
        "Periodo di Validità:" to "Gültigkeitszeitraum:",
        "Rimani aggiornato sulle tue spese in tempo reale." to "Bleiben Sie in Echtzeit über Ihre Ausgaben auf dem Laufenden.",
        "Seleziona Conto:" to "Konto auswählen:",
        "Spese 💸" to "Ausgaben 💸",
        "Totale" to "Gesamt",
        "transazioni" to "Transaktionen",
        "Nuova Spesa" to "Neue Ausgabe",
        "Nuova Entrata" to "Neue Einnahme",
        "Nuovo Giroconto" to "Neue Überweisung",
        "Budget per Categoria" to "Kategorie-Budgets",
        "Categoria" to "Kategorie",
        "Sottocategoria" to "Unterkategorie",
        "Filtra per Categoria:" to "Nach Kategorie filtern:",
        "Spesa per Categoria" to "Ausgaben nach Kategorie",
        "Esporta Transazioni" to "Transaktionen exportieren",
        "Genera un report in formato CSV compatibile con Excel e software di contabilità." to "Erstellen Sie einen CSV-Bericht, der mit Excel und Buchhaltungssoftware kompatibel ist.",
        "PERIODO DI ESPORTAZIONE" to "EXPORTZEITRAUM",
        "Transazioni reali trovate nel periodo" to "Echte Transaktionen im Zeitraum gefunden",
        "(I risparmi e gli accantonamenti virtuali degli obiettivi sono automaticamente esclusi dall'esportazione)" to "(Ersparnisse und virtuelle Zielzuweisungen werden automatisch vom Export ausgeschlossen)",
        "Andamento Finanziario" to "Finanzielle Entwicklung",
        "Settimana" to "Woche",
        "Mese" to "Monat",
        "Anno" to "Jahr",
        "Entrate:" to "Einnahmen:",
        "Spese:" to "Ausgaben:",
        "Risparmio netto:" to "Nettoersparnis:",
        "Mostra Giroconti Interni" to "Interne Überweisungen anzeigen",
        "Visualizza movimenti tra i tuoi conti personali." to "Transaktionen zwischen Ihren persönlichen Konten anzeigen.",
        "Cerca transazione..." to "Transaktion suchen...",
        "Nessuna transazione trovata." to "Keine Transaktionen gefunden.",
        "Nuovo Obiettivo" to "Neues Ziel",
        "Tipo:" to "Typ:",
        "Modalità:" to "Modus:",
        "Ricorrente" to "Wiederkehrend",
        "Programmato" to "Geplant",
        "Unità" to "Einheit",
        "CALENDARIO E PERIODI" to "KALENDER & ZEITRÄUME",
        "COMUNICAZIONE" to "KOMMUNIKATION",
        "PERSONALIZZAZIONE FUNZIONI" to "FUNKTIONSANPASSUNG",
        "Conto Addebito Predefinito" to "Standard-Belastungskonto",
        "Seleziona il conto su cui addebitare di default la spesa rapida per" to "Wählen Sie das Standardkonto aus, das für die Schnellausgabe belastet werden soll für",
        "Salta / Nessuno" to "Überspringen / Keines",
        "Inizio Settimana" to "Wochenstart"
    )

    fun translate(text: String, language: String): String {
        if (language == "Italiano") return text
        return when (language) {
            "English" -> en[text] ?: text
            "Español" -> es[text] ?: en[text] ?: text
            "Català" -> ca[text] ?: en[text] ?: text
            "Français" -> fr[text] ?: en[text] ?: text
            "Deutsch" -> de[text] ?: en[text] ?: text
            else -> text
        }
    }
}

fun String.t(language: String): String {
    return LocalizedStrings.translate(this, language)
}

// --- MAIN ENUM FOR NAVIGATION ---
enum class BudgieScreen(val route: String, val title: String) {
    HOME("home", "Dashboard"),
    HISTORY("history", "Cronologia"),
    REPORTS("reports", "Statistiche"),
    BUDGETS("budgets", "Budget"),
    GOALS("goals", "Obiettivi"),
    PLANNING("planning", "Pianificazione"),
    CATEGORIES("categories", "Categorie"),
    SETTINGS("settings", "Impostazioni"),
    ACCOUNTS("accounts", "Conti"),
    EXPORT("export", "Esportazione")
}

// --- DYNAMIC VECTOR BUDGET MASCOT (BUDGIE) ---
@Composable
fun BudgieMascot(
    mood: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(130.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2.2f

            // 1. Corpo del Pappagallino (Pastel Green)
            drawCircle(
                color = Color(0xFF7ADAA3),
                radius = radius,
                center = center
            )

            // 2. Cresta Gialla sulla testa (Budgie characteristic)
            drawArc(
                color = Color(0xFFFCD664),
                startAngle = -135f,
                sweepAngle = 90f,
                useCenter = true,
                size = Size(radius * 2f, radius * 2f),
                topLeft = Offset(center.x - radius, center.y - radius)
            )

            // 3. Pance panciuta (Light soft yellow)
            drawCircle(
                color = Color(0xFFFFF1AA),
                radius = radius * 0.6f,
                center = Offset(center.x, center.y + radius * 0.3f)
            )

            // 4. Guancette rosee basate sullo stato d'animo
            val cheekColor = when (mood) {
                "Felice/Celebrativo" -> Color(0xFFFFA6C9) // Rosa felice
                "Attento/Pensieroso" -> Color(0xFFFFD480) // Giallo dubbioso
                else -> Color(0xFFFF6B6B) // Rosso allerta
            }
            drawCircle(
                color = cheekColor,
                radius = radius * 0.15f,
                center = Offset(center.x - radius * 0.45f, center.y + radius * 0.1f)
            )
            drawCircle(
                color = cheekColor,
                radius = radius * 0.15f,
                center = Offset(center.x + radius * 0.45f, center.y + radius * 0.1f)
            )

            // 5. Occhietti lucidi
            val eyeRadius = radius * 0.1f
            val eyeLeft = Offset(center.x - radius * 0.28f, center.y - radius * 0.12f)
            val eyeRight = Offset(center.x + radius * 0.28f, center.y - radius * 0.12f)

            when (mood) {
                "Allerta" -> {
                    // Occhi sbarrati di preoccupazione
                    drawCircle(color = Color.White, radius = eyeRadius * 1.4f, center = eyeLeft)
                    drawCircle(color = Color.Black, radius = eyeRadius * 0.8f, center = eyeLeft)
                    drawCircle(color = Color.White, radius = eyeRadius * 1.4f, center = eyeRight)
                    drawCircle(color = Color.Black, radius = eyeRadius * 0.8f, center = eyeRight)
                }
                "Attento/Pensieroso" -> {
                    // Guarda di lato con curiosità
                    drawCircle(color = Color.Black, radius = eyeRadius, center = eyeLeft)
                    drawCircle(color = Color.White, radius = eyeRadius * 0.3f, center = eyeLeft - Offset(eyeRadius * 0.3f, eyeRadius * 0.3f))
                    drawCircle(color = Color.Black, radius = eyeRadius, center = eyeRight)
                    drawCircle(color = Color.White, radius = eyeRadius * 0.3f, center = eyeRight - Offset(eyeRadius * 0.3f, eyeRadius * 0.3f))
                }
                else -> {
                    // Felice/Soridente (archi a mezzaluna all'insù)
                    drawCircle(color = Color.Black, radius = eyeRadius, center = eyeLeft)
                    drawCircle(color = Color.White, radius = eyeRadius * 0.35f, center = Offset(eyeLeft.x + 2f, eyeLeft.y - 2f))
                    drawCircle(color = Color.Black, radius = eyeRadius, center = eyeRight)
                    drawCircle(color = Color.White, radius = eyeRadius * 0.35f, center = Offset(eyeRight.x + 2f, eyeRight.y - 2f))
                }
            }

            // 6. Becco ricurvo carino (Arancione)
            val beakTopLeft = Offset(center.x - radius * 0.12f, center.y - radius * 0.05f)
            val beakSize = Size(radius * 0.24f, radius * 0.35f)
            drawArc(
                color = Color(0xFFFF9233),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = true,
                size = beakSize,
                topLeft = beakTopLeft
            )

            // 7. Segno distintivo dell'allerta o della gioia (Ala o stelline esterne)
            if (mood == "Allerta") {
                // Manina sulla fronte stilizzata (Ala piegata in alto)
                drawCircle(
                    color = Color(0xFF7ADAA3),
                    radius = radius * 0.25f,
                    center = Offset(center.x - radius * 0.3f, center.y - radius * 0.45f)
                )
            }
        }

        // Simbolo fluttuante basato sullo stato d'animo
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 4.dp, y = (-4).dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when (mood) {
                        "Felice/Celebrativo" -> Color(0xFF76D6A0)
                        "Attento/Pensieroso" -> Color(0xFFFCD664)
                        else -> Color(0xFFFF6B6B)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (mood) {
                    "Felice/Celebrativo" -> Icons.Default.Favorite
                    "Attento/Pensieroso" -> Icons.Default.Info
                    else -> Icons.Default.Warning
                },
                contentDescription = mood,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// --- UTILS ---
val LocalCurrencySymbol = staticCompositionLocalOf { "€" }

data class AppCurrency(
    val code: String,
    val symbol: String,
    val name: String,
    val flag: String
)

val supportedCurrencies = listOf(
    AppCurrency("EUR", "€", "Euro", "🇪🇺"),
    AppCurrency("USD", "$", "US Dollar", "🇺🇸"),
    AppCurrency("GBP", "£", "British Pound", "🇬🇧"),
    AppCurrency("CHF", "CHF", "Swiss Franc", "🇨🇭"),
    AppCurrency("JPY", "¥", "Japanese Yen", "🇯🇵"),
    AppCurrency("CAD", "CA$", "Canadian Dollar", "🇨🇦"),
    AppCurrency("AUD", "AU$", "Australian Dollar", "🇦🇺"),
    AppCurrency("BRL", "R$", "Brazilian Real", "🇧🇷"),
    AppCurrency("CNY", "¥", "Chinese Yuan", "🇨🇳"),
    AppCurrency("INR", "₹", "Indian Rupee", "🇮🇳"),
    AppCurrency("MXN", "MX$", "Mexican Peso", "🇲🇽"),
    AppCurrency("PLN", "zł", "Polish Zloty", "🇵🇱"),
    AppCurrency("SEK", "kr", "Swedish Krona", "🇸🇪"),
    AppCurrency("NOK", "kr", "Norwegian Krone", "🇳🇴"),
    AppCurrency("TRY", "₺", "Turkish Lira", "🇹🇷"),
    AppCurrency("RUB", "₽", "Russian Ruble", "🇷🇺")
)

fun Double.formatAmount(symbol: String = "€"): String {
    val symbols = java.text.DecimalFormatSymbols(Locale.ITALY)
    val df = DecimalFormat("#,##0.00", symbols)
    val formatted = df.format(this)
    return if (symbol.length > 1 && !symbol.startsWith("CA") && !symbol.startsWith("AU") && !symbol.startsWith("MX") && !symbol.startsWith("R")) {
        "$symbol $formatted"
    } else {
        "$symbol$formatted"
    }
}

@Composable
fun Double.formatEuro(): String {
    val symbol = LocalCurrencySymbol.current
    return formatAmount(symbol)
}

@Composable
fun Double.formatEuroAnnotated(
    prefix: String = "",
    suffix: String = ""
): AnnotatedString {
    val symbol = LocalCurrencySymbol.current
    val symbols = java.text.DecimalFormatSymbols(Locale.ITALY)
    val df = DecimalFormat("#,##0.00", symbols)
    val absVal = kotlin.math.abs(this)
    val formattedNum = df.format(absVal)
    val isMultiLetter = symbol.length > 1

    return buildAnnotatedString {
        if (prefix.isNotEmpty()) {
            append(prefix)
        } else if (this@formatEuroAnnotated < 0) {
            append("-")
        }

        if (isMultiLetter) {
            withStyle(SpanStyle(fontSize = 0.48.em, fontWeight = FontWeight.SemiBold)) {
                append(symbol)
                append(" ")
            }
        } else {
            append(symbol)
        }

        append(formattedNum)

        if (suffix.isNotEmpty()) {
            append(suffix)
        }
    }
}

fun formatTimestamp(timestamp: Long, language: String = "Italiano"): String {
    val locale = when (language) {
        "English" -> Locale.ENGLISH
        "Español" -> Locale("es")
        "Català" -> Locale("ca")
        "Français" -> Locale.FRENCH
        "Deutsch" -> Locale.GERMAN
        else -> Locale.ITALIAN
    }
    val sdf = SimpleDateFormat("dd MMM, HH:mm", locale)
    return sdf.format(Date(timestamp))
}

fun formatFullDateTime(timestamp: Long, language: String = "Italiano"): String {
    val locale = when (language) {
        "English" -> Locale.ENGLISH
        "Español" -> Locale("es")
        "Català" -> Locale("ca")
        "Français" -> Locale.FRENCH
        "Deutsch" -> Locale.GERMAN
        else -> Locale.ITALIAN
    }
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", locale)
    return sdf.format(Date(timestamp))
}

fun isValidSingleEmoji(s: String): Boolean {
    val clean = s.trim()
    if (clean.isEmpty()) return false
    
    // Convert to code points
    val codePoints = clean.codePoints().toArray()
    if (codePoints.isEmpty()) return false
    
    // Verify each code point belongs to an emoji or auxiliary formatting character
    for (cp in codePoints) {
        val isEmoji = (cp in 0x1F300..0x1F9FF) || // Miscellaneous Symbols and Pictographs, Food, etc.
                      (cp in 0x1F600..0x1F64F) || // Emoticons
                      (cp in 0x1F680..0x1F6FF) || // Transport and Map
                      (cp in 0x2600..0x27BF) ||   // Misc Symbols, Dingbats
                      (cp in 0x1F1E6..0x1F1FF) || // Regional Indicator Symbols (flags)
                      (cp in 0x1F900..0x1F9FF) || // Supplemental Symbols
                      (cp in 0x1FA00..0x1FAFF) || // Symbols and Pictographs Extended-A
                      cp == 0x200D ||             // Zero Width Joiner
                      cp == 0xFE0F                // Variation Selector-16
        
        if (!isEmoji) return false
    }
    
    // Count grapheme clusters (an emoji is a single grapheme cluster)
    val boundary = java.text.BreakIterator.getCharacterInstance()
    boundary.setText(clean)
    var count = 0
    while (boundary.next() != java.text.BreakIterator.DONE) {
        count++
    }
    
    return count == 1
}

fun suggestEmoji(name: String): String {
    val clean = name.lowercase(java.util.Locale.ITALIAN).trim()
    if (clean.isEmpty()) return "🍕" // default
    
    val mappings = mapOf(
        "stipendio" to "💰", "lavoro" to "💼", "fattura" to "📄", "guadagno" to "📈", "bonus" to "💵",
        "cibo" to "🍕", "ristorante" to "🍔", "pizza" to "🍕", "pasta" to "🍝", "cena" to "🍽️", "pranzo" to "🍱", "spesa" to "🛒", "alimentari" to "🍎",
        "casa" to "🏠", "affitto" to "🔑", "bollette" to "💡", "luce" to "⚡", "gas" to "🔥", "arredamento" to "🪑",
        "auto" to "🚗", "benzina" to "⛽", "carburante" to "⛽", "trasporti" to "🚌", "treno" to "🚆", "viaggi" to "✈️", "viaggio" to "✈️", "volo" to "✈️",
        "salute" to "💊", "farmacia" to "⚕️", "medico" to "🩺", "dentista" to "🦷", "visita" to "🏥",
        "sport" to "⚽", "palestra" to "🏋️", "fitness" to "💪", "calcio" to "⚽", "corsa" to "🏃",
        "cinema" to "🎬", "film" to "🎥", "teatro" to "🎭", "concerti" to "🎵", "musica" to "🎧", "svago" to "🕹️", "giochi" to "🎮",
        "regali" to "🎁", "regalo" to "🎁", "compleanno" to "🎂", "festa" to "🎉",
        "abbigliamento" to "👕", "vestiti" to "👗", "scarpe" to "👟", "shopping" to "🛍️",
        "tecnologia" to "💻", "telefono" to "📱", "computer" to "🖥️", "abbonamenti" to "📅", "internet" to "🌐",
        "scuola" to "🎒", "università" to "🎓", "libri" to "📚", "corso" to "✏️",
        "animali" to "🐶", "cane" to "🐕", "gatto" to "🐈", "veterinario" to "🐾",
        "tasse" to "🏦", "banca" to "🏛️", "interessi" to "🪙", "investimenti" to "📊", "mutuo" to "🏠"
    )
    
    for ((keyword, emoji) in mappings) {
        if (clean.contains(keyword)) {
            return emoji
        }
    }
    
    return "🌍" // Default fallback
}

// --- SCREEN 1: DASHBOARD (HOME) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: WalletViewModel,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val aggregateBalance by viewModel.aggregateBalance.collectAsStateWithLifecycle()
    val selectedAccountId by viewModel.selectedAccountId.collectAsStateWithLifecycle()
    val dailyBudget by viewModel.dailyBudgetProgress.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val budgets by viewModel.budgets.collectAsStateWithLifecycle()
    val startOfWeek by viewModel.startOfWeek.collectAsStateWithLifecycle()
    val finMonthDay by viewModel.financialMonthStartDay.collectAsStateWithLifecycle()
    val budgetOn by viewModel.budgetModuleEnabled.collectAsStateWithLifecycle()
    val goalsOn by viewModel.goalsModuleEnabled.collectAsStateWithLifecycle()
    val totalAccantonato by viewModel.totalAccantonato.collectAsStateWithLifecycle()
    val totalSavedForGoalsByAccount by viewModel.totalSavedForGoalsByAccount.collectAsStateWithLifecycle()
    val showBudgieTip by viewModel.showBudgieTip.collectAsStateWithLifecycle()

    var showAddTxDialog by remember { mutableStateOf(false) }
    var addTxType by remember { mutableStateOf("Expense") } // "Expense", "Income", "Transfer"
    var showAddAccountDialog by remember { mutableStateOf(false) }

    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    var showQuickAddDialog by remember { mutableStateOf<Category?>(null) }
    var quickAddAmount by remember { mutableStateOf("") }

    val pagerState = rememberPagerState(
        initialPage = if (selectedAccountId == null) 0 else {
            val index = accounts.indexOfFirst { it.id == selectedAccountId }
            if (index != -1) index + 1 else 0
        },
        pageCount = { 1 + accounts.size }
    )

    LaunchedEffect(pagerState.currentPage, accounts) {
        val page = pagerState.currentPage
        val currentTargetId = if (page == 0) null else accounts.getOrNull(page - 1)?.id
        if (selectedAccountId != currentTargetId) {
            viewModel.selectAccount(currentTargetId)
        }
    }

    LaunchedEffect(selectedAccountId, accounts) {
        val targetPage = if (selectedAccountId == null) 0 else {
            val index = accounts.indexOfFirst { it.id == selectedAccountId }
            if (index != -1) index + 1 else 0
        }
        if (pagerState.currentPage != targetPage && targetPage < pagerState.pageCount) {
            pagerState.scrollToPage(targetPage)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // A. Dynamic Balance Hero Card (Slide Carousel)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val displayTitle = if (page == 0) {
                "Saldo Totale".t(language)
            } else {
                val acc = accounts.getOrNull(page - 1)
                acc?.let { "${it.type.toAccountEmoji()} ${it.name}" } ?: ""
            }

            val isPageZero = (page == 0)
            val realBalance = if (isPageZero) {
                aggregateBalance
            } else {
                accounts.getOrNull(page - 1)?.balance ?: 0.0
            }

            val savedAmount = if (goalsOn) {
                if (isPageZero) {
                    totalAccantonato
                } else {
                    val accId = accounts.getOrNull(page - 1)?.id
                    if (accId != null) totalSavedForGoalsByAccount[accId] ?: 0.0 else 0.0
                }
            } else {
                0.0
            }

            val disposableBalance = realBalance - savedAmount

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(vertical = 28.dp, horizontal = 16.dp)
                    .testTag("aggregate_balance_card"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Subtitle filled badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = displayTitle,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Balance text (disposable)
                    Text(
                        text = disposableBalance.formatEuroAnnotated(),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1.5).sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    // Small detail for accantonati if active
                    if (goalsOn && savedAmount > 0.0) {
                        Text(
                            text = buildAnnotatedString {
                                append("${"Accantonati:".t(language)} ")
                                append(savedAmount.formatEuroAnnotated())
                                append(" (${"Reale:".t(language)} ")
                                append(realBalance.formatEuroAnnotated())
                                append(")")
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Swipe Indicator dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        repeat(1 + accounts.size) { i ->
                            val isSelected = (i == page)
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary 
                                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }
                }
            }
        }        // ⭐ B. INSERIMENTI RAPIDI (Quick Add Section)
        val quickCategories = categories.filter { it.isQuickAction }
        
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "INSERIMENTI RAPIDI".t(language),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            if (quickCategories.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Seleziona, nella sezione Categorie max 8 spese rapide".t(language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Arrange in 2 rows, up to 4 items per row
                val rows = quickCategories.chunked(4)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { cat ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1.2f)
                                        .clickable {
                                            showQuickAddDialog = cat
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(cat.iconEmoji, fontSize = 26.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = cat.name,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                            // Fill remaining slots in row if it has less than 4 items to keep spacing balanced
                            val emptySlots = 4 - rowItems.size
                            for (i in 0 until emptySlots) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Quick Add Amount input dialog
        if (showQuickAddDialog != null) {
            val targetCat = showQuickAddDialog!!
            var selectedAccountForQuickAdd by remember(targetCat) {
                mutableStateOf(
                    accounts.find { it.id == targetCat.defaultAccountId }
                    ?: accounts.find { it.id == selectedAccountId }
                    ?: accounts.firstOrNull()
                )
            }

            AlertDialog(
                onDismissRequest = { showQuickAddDialog = null; quickAddAmount = "" },
                title = { Text("Inserimento rapido".t(language)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("${"Inserisci l'importo per".t(language)}: ${targetCat.iconEmoji} ${targetCat.name}")
                        OutlinedTextField(
                            value = quickAddAmount,
                            onValueChange = { quickAddAmount = it },
                            label = { Text("Importo".t(language)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        if (accounts.isNotEmpty()) {
                            BudgieDropdown(
                                label = "Addebita su".t(language),
                                options = accounts,
                                selectedOption = selectedAccountForQuickAdd ?: accounts.first(),
                                optionToString = { it.name },
                                onOptionSelected = { selectedAccountForQuickAdd = it }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amountVal = quickAddAmount.replace(',', '.').toDoubleOrNull() ?: 0.0
                            if (amountVal > 0) {
                                val activeAccountId = selectedAccountForQuickAdd?.id ?: accounts.firstOrNull()?.id ?: 1
                                viewModel.addTransaction(
                                    title = targetCat.name,
                                    amount = amountVal,
                                    type = "Expense",
                                    categoryId = targetCat.id,
                                    sourceAccountId = activeAccountId,
                                    destinationAccountId = null
                                )
                                showQuickAddDialog = null
                                quickAddAmount = ""
                            }
                        }
                    ) {
                        Text("Conferma".t(language))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showQuickAddDialog = null
                            quickAddAmount = ""
                        }
                    ) {
                        Text("Annulla".t(language))
                    }
                }
            )
        }


        // C. Bottoni Rapidi Bento (Replication of Quick Actions Bento from Stitch)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Spesa (Expense)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                    .clickable {
                        addTxType = "Expense"
                        showAddTxDialog = true
                    }
                    .padding(vertical = 16.dp, horizontal = 12.dp)
                    .testTag("quick_expense_btn"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "Spesa".t(language),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Giroconto (Transfer) - Solo se ci sono più conti
            if (accounts.size > 1) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f))
                        .clickable {
                            addTxType = "Transfer"
                            showAddTxDialog = true
                        }
                        .padding(vertical = 16.dp, horizontal = 12.dp)
                        .testTag("quick_transfer_btn"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text = "Giroconto".t(language),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            // Entrata (Income)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f))
                    .clickable {
                        addTxType = "Income"
                        showAddTxDialog = true
                    }
                    .padding(vertical = 16.dp, horizontal = 12.dp)
                    .testTag("quick_income_btn"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "Entrata".t(language),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // D. Mascotte (Budgie) Feedback & Tip Area (Replication of Tip Card from Stitch) OR Pinned Budget
        val pinnedBudget = if (budgetOn) budgets.find { it.isPinnedToHome } else null
        if (pinnedBudget != null) {
            val spending = viewModel.getBudgetSpending(pinnedBudget, transactions)
            val percentage = if (pinnedBudget.amountLimit > 0) (spending / pinnedBudget.amountLimit) else 0.0
            val progressColor = when {
                percentage < 0.7 -> Color(0xFF76D6A0) // Green
                percentage < 1.0 -> Color(0xFFFCD664) // Yellow
                else -> Color(0xFFBA1A1A)             // Red
            }
            
            val accountName = if (pinnedBudget.accountId == null) {
                "Tutti i Conti".t(language)
            } else {
                accounts.find { it.id == pinnedBudget.accountId }?.name ?: "Conto".t(language)
            }
            
            val cat = if (pinnedBudget.categoryId != null) categories.find { it.id == pinnedBudget.categoryId } else null
            val sub = if (pinnedBudget.subCategoryId != null) categories.find { it.id == pinnedBudget.subCategoryId } else null
            
            val categoryMainName = if (cat != null) {
                "${cat.iconEmoji} ${cat.name}"
            } else {
                "🌍 " + "Complessivo".t(language) + " ($accountName)"
            }
            
            val isAllSubcategories = pinnedBudget.categoryId != null && pinnedBudget.subCategoryId == null
            val subCategoryName = sub?.name ?: ""

            val periodLabel = when (pinnedBudget.period) {
                "Daily" -> "Giornaliero".t(language)
                "Weekly" -> "Settimanale".t(language)
                "Monthly" -> "Mensile".t(language)
                else -> "Mensile".t(language)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFCD664),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Budget in Evidenza".t(language).uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = periodLabel.t(language),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = categoryMainName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (isAllSubcategories) {
                            Text(
                                text = "(Tutte le sottocategorie)".t(language),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else if (subCategoryName.isNotEmpty()) {
                            Text(
                                text = "($subCategoryName)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        LinearProgressIndicator(
                            progress = { percentage.toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = progressColor,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = spending.formatEuroAnnotated(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        append("${"di".t(language)} ")
                                        append(pinnedBudget.amountLimit.formatEuroAnnotated())
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            Text(
                                text = "${(percentage * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = progressColor
                            )
                        }
                    }
                }
            }
        }

        if (showBudgieTip) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(16.dp)
            ) {
                // Close 'x' button in the top-right corner
                IconButton(
                    onClick = { viewModel.setShowBudgieTip(false) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Nascondi Tip",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // App icon/logo
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.budgie_logo),
                        contentDescription = "Budgie Logo",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Andamento Finanziario".t(language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        var trendPeriod by remember { mutableStateOf("Settimana") } // "Settimana" | "Mese" | "Anno"

                        // Toggle Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("Settimana", "Mese", "Anno").forEach { period ->
                                val isSelected = trendPeriod == period
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary 
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        )
                                        .clickable { trendPeriod = period }
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = period.t(language),
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Calculations
                        val nowTime = System.currentTimeMillis()
                        val bounds = when (trendPeriod) {
                            "Settimana" -> viewModel.getWeeklyPeriodBounds(nowTime, startOfWeek)
                            "Mese" -> viewModel.getMonthlyPeriodBounds(nowTime, finMonthDay)
                            else -> {
                                val currentCal = java.util.Calendar.getInstance().apply { timeInMillis = nowTime }
                                val currentYear = currentCal.get(Calendar.YEAR)
                                val startOfYear = java.util.Calendar.getInstance().apply {
                                    set(currentYear, java.util.Calendar.JANUARY, 1, 0, 0, 0)
                                    set(java.util.Calendar.MILLISECOND, 0)
                                }.timeInMillis
                                val endOfYear = java.util.Calendar.getInstance().apply {
                                    set(currentYear, java.util.Calendar.DECEMBER, 31, 23, 59, 59)
                                    set(java.util.Calendar.MILLISECOND, 999)
                                }.timeInMillis
                                Pair(startOfYear, endOfYear)
                            }
                        }

                        val filteredTx = transactions.filter { it.timestamp in bounds.first..bounds.second }
                        val totalExp = filteredTx.filter { it.type == "Expense" }.sumOf { it.amount }
                        val totalInc = filteredTx.filter { it.type == "Income" }.sumOf { it.amount }
                        val netBalance = totalInc - totalExp

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Entrate:".t(language), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                Text(
                                    text = totalInc.formatEuroAnnotated(prefix = "+"),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF006D43)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Spese:".t(language), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                Text(
                                    text = totalExp.formatEuroAnnotated(prefix = "-"),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFBA1A1A)
                                )
                            }
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Risparmio netto:".t(language), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text(
                                    text = kotlin.math.abs(netBalance).formatEuroAnnotated(prefix = if (netBalance >= 0) "+" else "-"),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (netBalance >= 0) Color(0xFF006D43) else Color(0xFFBA1A1A)
                                )
                            }
                        }
                    }
                }
            }
        }

        // E. Attività Recenti Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Attività Recenti".t(language),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            TextButton(onClick = onNavigateToHistory) {
                Text(
                    text = "Vedi Tutto".t(language),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // F. Recent transactions list items (Max 4)
        val recentTx = transactions.take(4)
        if (recentTx.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Nessuna transazione registrata.".t(language), color = Color.Gray)
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                recentTx.forEach { tx ->
                    TransactionItemRow(tx = tx, viewModel = viewModel)
                }
            }
        }
    }

    // --- POPUP DIALOGS ---
    if (showAddTxDialog) {
        AddTransactionDialog(
            type = addTxType,
            viewModel = viewModel,
            onDismiss = { showAddTxDialog = false }
        )
    }

    if (showAddAccountDialog) {
        AddAccountDialog(
            viewModel = viewModel,
            onDismiss = { showAddAccountDialog = false }
        )
    }
}

@Composable
fun TransactionItemRow(
    tx: Transaction,
    viewModel: WalletViewModel
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()

    val cat = categories.find { it.id == tx.categoryId }
    val parentCat = if (cat?.parentCategoryId != null) categories.find { it.id == cat.parentCategoryId } else null

    val emoji = when {
        tx.type == "Transfer" -> "💸"
        cat != null -> cat.iconEmoji
        else -> "💼"
    }

    val sourceAccName = accounts.find { it.id == tx.sourceAccountId }?.name ?: "Conto".t(language)
    val destAccName = if (tx.type == "Transfer" && tx.destinationAccountId != null) {
        accounts.find { it.id == tx.destinationAccountId }?.name
    } else null

    val isFromScheduled = tx.isFromPlanned || tx.title.startsWith("Pianificato Ricorrente") || tx.title.startsWith("Pianificato Programmato")

    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showEditDialog = true }
            .padding(vertical = 3.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Circular icon with scheduled badge overlay
                val tintColor = when (tx.type) {
                    "Expense" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    "Income" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    else -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                }
                Box(
                    modifier = Modifier.size(46.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(tintColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 22.sp)
                    }
                    if (isFromScheduled) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Pianificato".t(language),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }

                // Text details with clean line wrapping and high legibility
                Column(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val rawTitle = tx.title.trim()
                    val isAutoPlannedTitle = rawTitle.startsWith("Pianificato Ricorrente") || rawTitle.startsWith("Pianificato Programmato")
                    val hasCustomNote = rawTitle.isNotBlank() && !isAutoPlannedTitle

                    val mainTitleText = when {
                        hasCustomNote -> rawTitle
                        tx.type == "Transfer" -> "Giroconto".t(language)
                        parentCat != null -> parentCat.name
                        cat != null -> cat.name
                        else -> "Transazione".t(language)
                    }

                    Text(
                        text = mainTitleText,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Subcategory line if applicable
                    if (!hasCustomNote && parentCat != null && cat != null) {
                        Text(
                            text = "> ${cat.name}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else if (hasCustomNote && cat != null) {
                        val catDisplay = if (parentCat != null) "${parentCat.name} > ${cat.name}" else cat.name
                        Text(
                            text = catDisplay,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Account line
                    val accountText = if (destAccName != null) {
                        "${"Da".t(language)} $sourceAccName ${"a".t(language)} $destAccName"
                    } else {
                        sourceAccName
                    }

                    Text(
                        text = accountText,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Timestamp line
                    Text(
                        text = formatFullDateTime(tx.timestamp, language),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            val (amountPrefix, amountColor) = when (tx.type) {
                "Expense" -> {
                    "- " to MaterialTheme.colorScheme.error
                }
                "Income" -> {
                    "+ " to MaterialTheme.colorScheme.primary
                }
                else -> {
                    "" to MaterialTheme.colorScheme.tertiary
                }
            }
            Text(
                text = tx.amount.formatEuroAnnotated(prefix = amountPrefix),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = amountColor,
                modifier = Modifier.padding(start = 4.dp),
                softWrap = false
            )
        }
    }

    if (showEditDialog) {
        EditTransactionDialog(
            transaction = tx,
            viewModel = viewModel,
            onDismiss = { showEditDialog = false }
        )
    }
}

// --- SCREEN 2: HISTORY SCREEN ---
@Composable
fun HistoryScreen(
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val filteredTx by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val showInternalTransfers by viewModel.showInternalTransfers.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedMonthCalendar by remember { mutableStateOf<java.util.Calendar?>(null) }

    val locale = remember(language) {
        when (language) {
            "English" -> java.util.Locale.ENGLISH
            "Español" -> java.util.Locale("es")
            "Català" -> java.util.Locale("ca")
            "Français" -> java.util.Locale.FRENCH
            "Deutsch" -> java.util.Locale.GERMAN
            else -> java.util.Locale.ITALIAN
        }
    }
    val monthNameFormatter = remember(locale) { java.text.SimpleDateFormat("MMMM", locale) }
    val monthYearFormatter = remember(locale) { java.text.SimpleDateFormat("MMMM yyyy", locale) }

    // Start limit of previous calendar month
    val startOfPrevLimit = remember {
        java.util.Calendar.getInstance().apply {
            add(java.util.Calendar.MONTH, -1)
            set(java.util.Calendar.DAY_OF_MONTH, 1)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val (startLimit, endLimit) = remember(selectedMonthCalendar) {
        if (selectedMonthCalendar == null) {
            Pair(startOfPrevLimit, Long.MAX_VALUE)
        } else {
            val startOfSelected = (selectedMonthCalendar!!.clone() as java.util.Calendar).apply {
                set(java.util.Calendar.DAY_OF_MONTH, 1)
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis
            val endOfSelected = (selectedMonthCalendar!!.clone() as java.util.Calendar).apply {
                set(java.util.Calendar.DAY_OF_MONTH, getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
                set(java.util.Calendar.HOUR_OF_DAY, 23)
                set(java.util.Calendar.MINUTE, 59)
                set(java.util.Calendar.SECOND, 59)
                set(java.util.Calendar.MILLISECOND, 999)
            }.timeInMillis
            Pair(startOfSelected, endOfSelected)
        }
    }

    val txInPeriod = remember(filteredTx, startLimit, endLimit) {
        filteredTx.filter { it.timestamp in startLimit..endLimit }
    }

    val finalTx = remember(filteredTx, txInPeriod, searchQuery, categories, accounts) {
        val baseTxs = if (searchQuery.isNotBlank()) filteredTx else txInPeriod
        
        if (searchQuery.isBlank()) {
            baseTxs
        } else {
            baseTxs.filter { tx ->
                val catObj = categories.find { it.id == tx.categoryId }
                val parentCatObj = catObj?.parentCategoryId?.let { pid -> categories.find { it.id == pid } }
                
                val categoryName = catObj?.name ?: ""
                val parentCategoryName = parentCatObj?.name ?: ""
                
                val sourceAccName = accounts.find { it.id == tx.sourceAccountId }?.name ?: ""
                val destAccName = tx.destinationAccountId?.let { did -> accounts.find { it.id == did }?.name } ?: ""
                
                val sdfDay = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ITALIAN)
                val txDateStr = sdfDay.format(java.util.Date(tx.timestamp))
                val txDateAlt = txDateStr.replace("/", "-")
                
                val monthNameIt = java.text.SimpleDateFormat("MMMM", java.util.Locale.ITALIAN).format(java.util.Date(tx.timestamp))
                val monthNameEn = java.text.SimpleDateFormat("MMMM", java.util.Locale.ENGLISH).format(java.util.Date(tx.timestamp))
                val yearStr = java.text.SimpleDateFormat("yyyy", java.util.Locale.ITALIAN).format(java.util.Date(tx.timestamp))
                
                val fullMonthYearIt = "$monthNameIt $yearStr"
                val fullMonthYearEn = "$monthNameEn $yearStr"
                
                tx.title.contains(searchQuery, ignoreCase = true) ||
                categoryName.contains(searchQuery, ignoreCase = true) ||
                parentCategoryName.contains(searchQuery, ignoreCase = true) ||
                sourceAccName.contains(searchQuery, ignoreCase = true) ||
                destAccName.contains(searchQuery, ignoreCase = true) ||
                txDateStr.contains(searchQuery, ignoreCase = true) ||
                txDateAlt.contains(searchQuery, ignoreCase = true) ||
                monthNameIt.contains(searchQuery, ignoreCase = true) ||
                monthNameEn.contains(searchQuery, ignoreCase = true) ||
                fullMonthYearIt.contains(searchQuery, ignoreCase = true) ||
                fullMonthYearEn.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // A. Toggle Interno Giroconti Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mostra Giroconti Interni".t(language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Visualizza movimenti tra i tuoi conti personali.".t(language),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Switch(
                    checked = showInternalTransfers,
                    onCheckedChange = { viewModel.toggleInternalTransfers() },
                    modifier = Modifier.testTag("internal_transfers_switch")
                )
            }
        }

        // B. Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_bar"),
            placeholder = { Text("Cerca transazione...".t(language)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Cancella")
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            maxLines = 1
        )

        // Info text showing visible date range / active period
        val infoText = if (selectedMonthCalendar == null) {
            val startOfPrev = java.util.Calendar.getInstance().apply {
                add(java.util.Calendar.MONTH, -1)
            }.time
            val prevMonthName = monthNameFormatter.format(startOfPrev).replaceFirstChar { it.uppercaseChar() }
            when (language) {
                "English" -> "Showing transactions from $prevMonthName 1st to today"
                "Español" -> "Mostrando transacciones desde el 1 de $prevMonthName hasta hoy"
                "Català" -> "Mostrant transaccions des de l'1 de $prevMonthName fins a avui"
                "Français" -> "Affichage des transactions du 1er $prevMonthName à aujourd'hui"
                "Deutsch" -> "Transaktionen vom 1. $prevMonthName bis heute anzeigen"
                else -> "Mostrate transazioni dal 1° $prevMonthName ad oggi"
            }
        } else {
            val currentMonthName = monthYearFormatter.format(selectedMonthCalendar!!.time).replaceFirstChar { it.uppercaseChar() }
            when (language) {
                "English" -> "Showing transactions of $currentMonthName"
                "Español" -> "Mostrando transacciones de $currentMonthName"
                "Català" -> "Mostrant transaccions de $currentMonthName"
                "Français" -> "Affichage des transactions de $currentMonthName"
                "Deutsch" -> "Transaktionen von $currentMonthName anzeigen"
                else -> "Mostrate transazioni di $currentMonthName"
            }
        }

        Text(
            text = infoText,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Month pagination navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val prevMonthTarget = remember(selectedMonthCalendar) {
                java.util.Calendar.getInstance().apply {
                    if (selectedMonthCalendar != null) {
                        time = selectedMonthCalendar!!.time
                    }
                    add(java.util.Calendar.MONTH, -1)
                }
            }
            val prevMonthLabel = monthYearFormatter.format(prevMonthTarget.time).replaceFirstChar { it.uppercaseChar() }

            Button(
                onClick = {
                    selectedMonthCalendar = (prevMonthTarget.clone() as java.util.Calendar)
                },
                modifier = Modifier.testTag("prev_month_button")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(prevMonthLabel, style = MaterialTheme.typography.labelMedium)
            }

            if (selectedMonthCalendar != null) {
                val nextMonthTarget = remember(selectedMonthCalendar) {
                    java.util.Calendar.getInstance().apply {
                        time = selectedMonthCalendar!!.time
                        add(java.util.Calendar.MONTH, 1)
                    }
                }
                
                val currentCal = java.util.Calendar.getInstance()
                val isNextCurrentOrFuture = (nextMonthTarget.get(java.util.Calendar.YEAR) > currentCal.get(java.util.Calendar.YEAR)) ||
                        (nextMonthTarget.get(java.util.Calendar.YEAR) == currentCal.get(java.util.Calendar.YEAR) &&
                         nextMonthTarget.get(java.util.Calendar.MONTH) >= currentCal.get(java.util.Calendar.MONTH))

                val nextMonthLabel = if (isNextCurrentOrFuture) {
                    when (language) {
                        "English" -> "Default View"
                        "Español" -> "Vista predeterminada"
                        "Català" -> "Vista predeterminada"
                        "Français" -> "Vue par défaut"
                        "Deutsch" -> "Standardansicht"
                        else -> "Vista Predefinita"
                    }
                } else {
                    monthYearFormatter.format(nextMonthTarget.time).replaceFirstChar { it.uppercaseChar() }
                }

                Button(
                    onClick = {
                        if (isNextCurrentOrFuture) {
                            selectedMonthCalendar = null
                        } else {
                            selectedMonthCalendar = (nextMonthTarget.clone() as java.util.Calendar)
                        }
                    },
                    modifier = Modifier.testTag("next_month_button")
                ) {
                    Text(nextMonthLabel, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            } else {
                Box(modifier = Modifier.size(1.dp))
            }
        }

        if (finalTx.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Nessuna transazione trovata.".t(language), color = Color.Gray)
            }
        } else {
            // Group transactions by date
            val dayMs = 24 * 60 * 60 * 1000L
            val now = System.currentTimeMillis()
            val startOfToday = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val todayGroup = finalTx.filter { it.timestamp >= startOfToday }
            val yesterdayGroup = finalTx.filter { it.timestamp in (startOfToday - dayMs)..<startOfToday }
            val olderGroup = finalTx.filter { it.timestamp < (startOfToday - dayMs) }

            val sdfGroup = remember { java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ITALIAN) }
            val olderGrouped = remember(olderGroup) {
                olderGroup.groupBy { sdfGroup.format(java.util.Date(it.timestamp)) }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (todayGroup.isNotEmpty()) {
                    item {
                        Text(
                            text = "OGGI".t(language),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                    items(todayGroup.size) { index ->
                        TransactionItemRow(tx = todayGroup[index], viewModel = viewModel)
                    }
                }

                if (yesterdayGroup.isNotEmpty()) {
                    item {
                        Text(
                            text = "IERI".t(language),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                    items(yesterdayGroup.size) { index ->
                        TransactionItemRow(tx = yesterdayGroup[index], viewModel = viewModel)
                    }
                }

                if (olderGrouped.isNotEmpty()) {
                    olderGrouped.forEach { (dateStr, txsForDate) ->
                        item {
                            Text(
                                text = dateStr,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        items(txsForDate.size) { index ->
                            TransactionItemRow(tx = txsForDate[index], viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 3: REPORTS SCREEN (STATISTICHE) ---
@Composable
@kotlin.OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
fun ReportsScreen(
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val categoryBreakdown by viewModel.categoryBreakdown.collectAsStateWithLifecycle()
    val timeRange by viewModel.selectedTimeRange.collectAsStateWithLifecycle()
    val dailyTrend by viewModel.last7DaysTrend.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // A. Selettore Periodo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Week", "Month", "Year").forEach { r ->
                val text = when (r) {
                    "Week" -> "Settimana".t(language)
                    "Month" -> "Mese".t(language)
                    else -> "Anno".t(language)
                }
                Button(
                    onClick = { viewModel.setTimeRange(r) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (timeRange == r) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (timeRange == r) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // B. Pie/Doughnut Chart Card (Canvas centered donut drawing)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Spesa per Categoria".t(language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(16.dp))

                val totalSpent = categoryBreakdown.sumOf { it.spentAmount }

                Box(
                    modifier = Modifier.size(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 32.dp.toPx()
                        val diameter = size.width - strokeWidth
                        val rectSize = Size(diameter, diameter)
                        val offset = strokeWidth / 2f
                        val rectOffset = Offset(offset, offset)

                        if (totalSpent == 0.0) {
                            // Draw empty grey circle
                            drawArc(
                                color = Color.LightGray.copy(alpha = 0.4f),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = rectOffset,
                                size = rectSize,
                                style = Stroke(width = strokeWidth)
                            )
                        } else {
                            var startAngle = -90f
                            val colors = listOf(
                                Color(0xFFFF8B8B), // Food - Red-pink
                                Color(0xFF8BA4FF), // Housing - Indigo
                                Color(0xFF76D6A0), // Fun - Green
                                Color(0xFFFCD664), // Transport - Yellow
                                Color(0xFF86CCED)  // Other - Blue
                            )

                            categoryBreakdown.forEachIndexed { index, item ->
                                val sweepAngle = ((item.spentAmount / totalSpent) * 360f).toFloat()
                                drawArc(
                                    color = colors[index % colors.size],
                                    startAngle = startAngle,
                                    sweepAngle = sweepAngle,
                                    useCenter = false,
                                    topLeft = rectOffset,
                                    size = rectSize,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                startAngle += sweepAngle
                            }
                        }
                    }

                    // Centered Text
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Totale".t(language), style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Text(
                            text = totalSpent.formatEuro(),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Color Legend Row Wrap
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val colors = listOf(Color(0xFFFF8B8B), Color(0xFF8BA4FF), Color(0xFF76D6A0), Color(0xFFFCD664), Color(0xFF86CCED))
                    categoryBreakdown.forEachIndexed { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(colors[index % colors.size])
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(item.category.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // D. Category Breakdown details list
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Dettaglio Spese".t(language),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            categoryBreakdown.forEach { breakdown ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                              ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(breakdown.category.iconEmoji, fontSize = 18.sp)
                                }
                                Column {
                                    Text(breakdown.category.name, fontWeight = FontWeight.Bold)
                                    Text("${breakdown.transactionCount} " + "transazioni".t(language), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(breakdown.spentAmount.formatEuro(), fontWeight = FontWeight.Bold)
                                Text("${breakdown.percentage}%", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        // Linear Progress Bar representing category spent percentage
                        LinearProgressIndicator(
                            progress = { breakdown.percentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }

        // C. Grafico Personalizzabile sull'Andamento delle Spese
        val startOfWeek by viewModel.startOfWeek.collectAsStateWithLifecycle()
        val finMonthDay by viewModel.financialMonthStartDay.collectAsStateWithLifecycle()
        val categories by viewModel.categories.collectAsStateWithLifecycle()
        val transactions by viewModel.transactions.collectAsStateWithLifecycle()

        val trendRangeType by viewModel.trendRangeType.collectAsStateWithLifecycle()
        val trendRangeCount by viewModel.trendRangeCount.collectAsStateWithLifecycle()
        val trendCategoryFilterId by viewModel.trendCategoryFilterId.collectAsStateWithLifecycle()

        val selectedCategoryFilter = categories.find { it.id == trendCategoryFilterId }
        var textVal by remember(trendRangeCount) { mutableStateOf(trendRangeCount.toString()) }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Andamento Spese Personalizzato".t(language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                // 1. Selector for Period Count (Input with max 2 digits) & Period Type (Giorni, Settimane, Mesi)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textVal,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }
                            if (digits.length <= 2) {
                                textVal = digits
                                val parsed = digits.toIntOrNull()
                                if (parsed != null && parsed > 0) {
                                    viewModel.setTrendRangeCount(parsed)
                                }
                            }
                        },
                        label = { Text("N. Periodi".t(language)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier.width(90.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        listOf("Days" to "Giorni", "Weeks" to "Settimane", "Months" to "Mesi").forEach { (typeCode, label) ->
                            val isSelected = trendRangeType == typeCode
                            ElevatedFilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setTrendRangeType(typeCode) },
                                label = { Text(label.t(language), style = MaterialTheme.typography.bodySmall) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 2. Filter by Category Dropdown Menu
                var expandedCategoryDropdown by remember { mutableStateOf(false) }
                val categoryLabel = selectedCategoryFilter?.let { "${it.iconEmoji} ${it.name}" } ?: "Totale".t(language)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Filtra per Categoria:".t(language),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expandedCategoryDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = categoryLabel, style = MaterialTheme.typography.bodyMedium)
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Seleziona Categoria"
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = expandedCategoryDropdown,
                            onDismissRequest = { expandedCategoryDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Totale".t(language)) },
                                onClick = {
                                    viewModel.setTrendCategoryFilterId(null)
                                    expandedCategoryDropdown = false
                                }
                            )
                            categories.filter { it.parentCategoryId == null && it.type == "Expense" }.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text("${category.iconEmoji} ${category.name}") },
                                    onClick = {
                                        viewModel.setTrendCategoryFilterId(category.id)
                                        expandedCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calculations
                val locale = when (language) {
                    "English" -> Locale.ENGLISH
                    "Español" -> Locale("es")
                    "Català" -> Locale("ca")
                    "Français" -> Locale.FRENCH
                    "Deutsch" -> Locale.GERMAN
                    else -> Locale.ITALIAN
                }
                val sdfDays = remember(locale) { SimpleDateFormat("dd/MM", locale) }
                val sdfMonths = remember(locale) { SimpleDateFormat("MMM", locale) }

                val matchesCategory = { transactionCategoryId: Int? ->
                    if (selectedCategoryFilter == null) {
                        true
                    } else {
                        transactionCategoryId == selectedCategoryFilter.id ||
                        categories.find { c -> c.id == transactionCategoryId }?.parentCategoryId == selectedCategoryFilter.id
                    }
                }

                val trendPoints = (0 until trendRangeCount).map { i ->
                    val cal = Calendar.getInstance()
                    val label = when (trendRangeType) {
                        "Days" -> {
                            cal.add(Calendar.DAY_OF_YEAR, -i)
                            val startOfDay = cal.apply {
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.timeInMillis
                            val endOfDay = cal.apply {
                                set(Calendar.HOUR_OF_DAY, 23)
                                set(Calendar.MINUTE, 59)
                                set(Calendar.SECOND, 59)
                                set(Calendar.MILLISECOND, 999)
                            }.timeInMillis
                            val l = sdfDays.format(cal.time)
                            val amt = transactions.filter {
                                it.type == "Expense" &&
                                it.timestamp in startOfDay..endOfDay &&
                                matchesCategory(it.categoryId)
                            }.sumOf { it.amount }
                            l to amt
                        }
                        "Weeks" -> {
                            cal.add(Calendar.WEEK_OF_YEAR, -i)
                            // Standardize start and end based on user start of week setting
                            val startOfWeekTime = viewModel.getWeeklyPeriodBounds(cal.timeInMillis, startOfWeek).first
                            val endOfWeekTime = viewModel.getWeeklyPeriodBounds(cal.timeInMillis, startOfWeek).second
                            val l = "Set".t(language) + " " + cal.get(Calendar.WEEK_OF_YEAR)
                            val amt = transactions.filter {
                                it.type == "Expense" &&
                                it.timestamp in startOfWeekTime..endOfWeekTime &&
                                matchesCategory(it.categoryId)
                            }.sumOf { it.amount }
                            l to amt
                        }
                        else -> { // Months
                            cal.add(Calendar.MONTH, -i)
                            val startOfMonthTime = viewModel.getMonthlyPeriodBounds(cal.timeInMillis, finMonthDay).first
                            val endOfMonthTime = viewModel.getMonthlyPeriodBounds(cal.timeInMillis, finMonthDay).second
                            val l = sdfMonths.format(cal.time)
                            val amt = transactions.filter {
                                it.type == "Expense" &&
                                it.timestamp in startOfMonthTime..endOfMonthTime &&
                                matchesCategory(it.categoryId)
                            }.sumOf { it.amount }
                            l to amt
                        }
                    }
                    label
                }.reversed()

                // Render horizontal scrollable bar chart
                val maxAmount = trendPoints.maxOfOrNull { it.second } ?: 1.0
                val maxVal = if (maxAmount == 0.0) 1.0 else maxAmount

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    if (trendPoints.all { it.second == 0.0 }) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nessuna spesa nel periodo selezionato".t(language),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            trendPoints.forEach { point ->
                                val barHeightFactor = (point.second / maxVal).toFloat().coerceIn(0.02f, 1.0f)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier
                                        .width(52.dp)
                                        .fillMaxHeight()
                                ) {
                                    // Price above bar
                                    if (point.second > 0.0) {
                                        Text(
                                            text = point.second.formatEuro(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }

                                    // Bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight(barHeightFactor * 0.75f) // Scale slightly to fit the price label
                                            .width(22.dp)
                                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                            .background(
                                                if (point.second == maxAmount && point.second > 0.0) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.primaryContainer
                                                }
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = point.first,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (point.second == maxAmount && point.second > 0.0) MaterialTheme.colorScheme.primary else Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 4: BUDGETS LIMITS SCREEN ---
@Composable
fun BudgetsScreen(
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val budgets by viewModel.budgets.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val startOfWeek by viewModel.startOfWeek.collectAsStateWithLifecycle()
    val finMonthDay by viewModel.financialMonthStartDay.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()

    var showAddCategoryBudgetDialog by remember { mutableStateOf(false) }
    var showAddTotalBudgetDialog by remember { mutableStateOf(false) }
    var editingBudget by remember { mutableStateOf<Budget?>(null) }
    var expandedBudgetId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // A. Header section for Total spending (switchable Weekly vs Monthly)
        val budgetViewPeriod by viewModel.budgetViewPeriod.collectAsStateWithLifecycle()

        val bounds = if (budgetViewPeriod == "Weekly") {
            viewModel.getWeeklyPeriodBounds(System.currentTimeMillis(), startOfWeek)
        } else {
            viewModel.getMonthlyPeriodBounds(System.currentTimeMillis(), finMonthDay)
        }

        // Spesa totale corrente nel periodo selezionato (escludendo i giroconti)
        val currentExpensesOnly = transactions.filter {
            it.type == "Expense" && it.timestamp in bounds.first..bounds.second
        }.sumOf { it.amount }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(vertical = 16.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val nextPeriod = if (budgetViewPeriod == "Monthly") "Weekly" else "Monthly"
                        viewModel.setBudgetViewPeriod(nextPeriod)
                    },
                    modifier = Modifier.testTag("budget_prev_period_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Precedente",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Subtitle filled badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (budgetViewPeriod == "Weekly") "SPESE SETTIMANALI CORRENTI".t(language) else "SPESE MENSILI CORRENTI".t(language),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Amount text
                    Text(
                        text = currentExpensesOnly.formatEuro(),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1.5).sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    // Subtext / description
                    Text(
                        text = "Giroconti esclusi".t(language),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                IconButton(
                    onClick = {
                        val nextPeriod = if (budgetViewPeriod == "Monthly") "Weekly" else "Monthly"
                        viewModel.setBudgetViewPeriod(nextPeriod)
                    },
                    modifier = Modifier.testTag("budget_next_period_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Successivo",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // ⭐ Legenda star button (Point 9)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("⭐", fontSize = 24.sp)
                Text(
                    text = "Legenda: Clicca sulla stellina per fissare quel budget come \"Budget in Evidenza\" nella Home dell'applicazione.".t(language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // B. BUDGET TOTALI (per conto o cumulativo)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Budget Totali".t(language),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            TextButton(onClick = { showAddTotalBudgetDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Imposta".t(language))
            }
        }

        val totalBudgets = budgets.filter { it.categoryId == null }
        if (totalBudgets.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Nessun budget complessivo impostato. Puoi impostare un limite di spesa totale cumulato o per un conto specifico.".t(language),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            totalBudgets.forEach { budget ->
                val spending = viewModel.getBudgetSpending(budget, transactions)
                val bounds = viewModel.getBudgetBounds(budget, System.currentTimeMillis(), startOfWeek, finMonthDay)
                val isExpired = !budget.autoRenew && System.currentTimeMillis() > bounds.second

                val accountName = if (budget.accountId == null) {
                    "Tutti i Conti Insieme".t(language)
                } else {
                    accounts.find { it.id == budget.accountId }?.name ?: "Conto Eliminato".t(language)
                }

                val periodText = when (budget.period) {
                    "Daily" -> "Giornaliero".t(language)
                    "Weekly" -> "Settimanale".t(language)
                    "Monthly" -> "Mensile".t(language)
                    else -> "Mensile".t(language)
                }

                val percentage = if (budget.amountLimit > 0) (spending / budget.amountLimit) else 0.0
                val progressColor = when {
                    isExpired -> Color.Gray
                    percentage < 0.7 -> Color(0xFF76D6A0) // Green
                    percentage < 1.0 -> Color(0xFFFCD664) // Yellow
                    else -> Color(0xFFBA1A1A)             // Red
                }

                val isExpanded = expandedBudgetId == budget.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedBudgetId = if (isExpanded) null else budget.id },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isExpanded) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Compact Top Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (budget.accountId == null) "🌍" else "💳",
                                        fontSize = 18.sp
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "$accountName ($periodText)",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (isExpired) {
                                        Text(
                                            text = "SCADUTO ⏱️",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "${(percentage * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = progressColor
                                )
                                IconButton(
                                    onClick = { viewModel.togglePinBudget(budget) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (budget.isPinnedToHome) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Fissa in evidenza",
                                        tint = if (budget.isPinnedToHome) Color(0xFFFCD664) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Expanded Section
                        if (isExpanded) {
                            Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Spesa corrente:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "${spending.formatEuro()} di ${budget.amountLimit.formatEuro()}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            LinearProgressIndicator(
                                progress = { percentage.toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = progressColor,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            if (percentage >= 1.0 && budget.notifyOnOverflow) {
                                Text(
                                    text = "Sforato! Limite superato di ${(spending - budget.amountLimit).formatEuro()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFBA1A1A),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (budget.notifyOnOverflow) {
                                        Icon(
                                            Icons.Default.NotificationsActive,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            "Notifica attiva",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    if (budget.notifyOnOverflow && budget.autoRenew) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    if (budget.autoRenew) {
                                        Icon(
                                            Icons.Default.Autorenew,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            "Rinnovo auto",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isExpired) {
                                        Button(
                                            onClick = { viewModel.updateBudget(budget.copy(createdAt = System.currentTimeMillis())) },
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary)
                                        ) {
                                            Text("Reimposta", fontSize = 11.sp)
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { editingBudget = budget },
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Modifica", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Modifica", fontSize = 11.sp)
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteBudget(budget) },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f), CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Elimina",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // C. BUDGET CATEGORIE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Budget per Categoria".t(language),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            TextButton(onClick = { showAddCategoryBudgetDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nuovo".t(language))
            }
        }

        val categoryBudgets = budgets.filter { it.categoryId != null }
        if (categoryBudgets.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Nessun budget per categoria configurato. Puoi impostare dei limiti dedicati a categorie specifiche.".t(language),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            categoryBudgets.forEach { budget ->
                val cat = categories.find { it.id == budget.categoryId }
                if (cat != null) {
                    val subCat = budget.subCategoryId?.let { subId -> categories.find { it.id == subId } }
                    val spending = viewModel.getBudgetSpending(budget, transactions)
                    val bounds = viewModel.getBudgetBounds(budget, System.currentTimeMillis(), startOfWeek, finMonthDay)
                    val isExpired = !budget.autoRenew && System.currentTimeMillis() > bounds.second

                    val percentage = if (budget.amountLimit > 0) (spending / budget.amountLimit) else 0.0
                    val progressColor = when {
                        isExpired -> Color.Gray
                        percentage < 0.7 -> Color(0xFF76D6A0) // Green
                        percentage < 1.0 -> Color(0xFFFCD664) // Yellow
                        else -> Color(0xFFBA1A1A)             // Red
                    }

                    val periodText = when (budget.period) {
                        "Daily" -> "Giornaliero".t(language)
                        "Weekly" -> "Settimanale".t(language)
                        "Monthly" -> "Mensile".t(language)
                        else -> "Mensile".t(language)
                    }

                    val isExpanded = expandedBudgetId == budget.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedBudgetId = if (isExpanded) null else budget.id },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpanded) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Compact Top Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cat.iconEmoji,
                                            fontSize = 18.sp
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        val displayName = if (subCat != null) "${cat.name} > ${subCat.name}" else cat.name
                                        Text(
                                            text = "$displayName ($periodText)",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (isExpired) {
                                            Text(
                                                text = "SCADUTO ⏱️",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "${(percentage * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = progressColor
                                    )
                                    IconButton(
                                        onClick = { viewModel.togglePinBudget(budget) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (budget.isPinnedToHome) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "Fissa in evidenza",
                                            tint = if (budget.isPinnedToHome) Color(0xFFFCD664) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            // Expanded Section
                            if (isExpanded) {
                                Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Spesa corrente:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "${spending.formatEuro()} di ${budget.amountLimit.formatEuro()}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { percentage.toFloat().coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(CircleShape),
                                    color = progressColor,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                if (percentage >= 1.0 && budget.notifyOnOverflow) {
                                    Text(
                                        text = "Sforato! Limite superato di ${(spending - budget.amountLimit).formatEuro()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFBA1A1A),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        if (budget.notifyOnOverflow) {
                                            Icon(
                                                Icons.Default.NotificationsActive,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                "Notifica attiva",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        if (budget.notifyOnOverflow && budget.autoRenew) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        if (budget.autoRenew) {
                                            Icon(
                                                Icons.Default.Autorenew,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                "Rinnovo auto",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isExpired) {
                                            Button(
                                                onClick = { viewModel.updateBudget(budget.copy(createdAt = System.currentTimeMillis())) },
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.height(32.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary)
                                            ) {
                                                Text("Reimposta", fontSize = 11.sp)
                                            }
                                        }

                                        OutlinedButton(
                                            onClick = { editingBudget = budget },
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Modifica", modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Modifica", fontSize = 11.sp)
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteBudget(budget) },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f), CircleShape)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Elimina",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddCategoryBudgetDialog) {
        AddCategoryBudgetDialog(
            viewModel = viewModel,
            onDismiss = { showAddCategoryBudgetDialog = false }
        )
    }

    if (showAddTotalBudgetDialog) {
        AddTotalBudgetDialog(
            viewModel = viewModel,
            onDismiss = { showAddTotalBudgetDialog = false }
        )
    }

    if (editingBudget != null) {
        EditBudgetDialog(
            budget = editingBudget!!,
            viewModel = viewModel,
            onDismiss = { editingBudget = null }
        )
    }
}

// --- SCREEN 5: GOALS SAVINGS SCREEN (OBIETTIVI) ---
@Composable
fun GoalsScreen(
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val goals by viewModel.savingsGoals.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()

    val totalSavedAll by viewModel.totalAccantonato.collectAsStateWithLifecycle()
    val totalAllocatedToGoals by viewModel.totalAllocatedToGoals.collectAsStateWithLifecycle()
    val unallocatedSavings by viewModel.unallocatedSavings.collectAsStateWithLifecycle()
    val totalSavedForGoalsByAccount by viewModel.totalSavedForGoalsByAccount.collectAsStateWithLifecycle()

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAccantonaDialog by remember { mutableStateOf(false) }
    var showRilasciaDialog by remember { mutableStateOf(false) }

    var selectedGoalForAllocating by remember { mutableStateOf<SavingsGoal?>(null) }
    var selectedGoalForDeallocating by remember { mutableStateOf<SavingsGoal?>(null) }
    var selectedGoalForCompleting by remember { mutableStateOf<SavingsGoal?>(null) }
    var expandedGoalId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // A. Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "I tuoi Obiettivi".t(language),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Gestisci i tuoi risparmi virtuali".t(language),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }
            Button(
                onClick = { showAddGoalDialog = true },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Nuovo".t(language))
            }
        }

        // B. SALVADANAIO VIRTUALE Card (Top Section)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🐖", fontSize = 24.sp)
                    }
                    Column {
                        Text(
                            text = "SALVADANAIO VIRTUALE".t(language),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "Fondi totali accantonati dai tuoi conti".t(language),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = totalSavedAll.formatEuro(),
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "Saldo Totale".t(language),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = totalAllocatedToGoals.formatEuro(),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Vincolati".t(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = unallocatedSavings.formatEuro(),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "Non Assegnati".t(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showAccantonaDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Accantona".t(language))
                    }

                    Button(
                        onClick = { showRilasciaDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rilascia".t(language))
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OBIETTIVI INDIVIDUALI".t(language),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.outline
            )
            
            if (goals.any { it.currentAmount > 0.0 }) {
                TextButton(
                    onClick = { viewModel.clearAllGoalsAllocations() },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Svincola tutti i fondi",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Svincola tutto".t(language),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // C. Individual Goals List
        if (goals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Nessun obiettivo creato. Creane uno!".t(language), color = Color.Gray)
            }
        } else {
            goals.forEach { goal ->
                val progress = if (goal.targetAmount > 0) goal.currentAmount / goal.targetAmount else 0.0
                val progressPercent = (progress * 100).toInt()
                val isExpanded = expandedGoalId == goal.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedGoalId = if (isExpanded) null else goal.id },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isExpanded) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Header info row (Compact)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!goal.iconEmoji.isNullOrBlank()) {
                                        Text(goal.iconEmoji, fontSize = 18.sp)
                                    } else {
                                        Icon(
                                            Icons.Default.Savings,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.tertiary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        goal.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    if (!isExpanded) {
                                        Text(
                                            goal.deadline,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    goal.currentAmount.formatEuro(),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                                )
                                Text(
                                    "${"su".t(language)} ${goal.targetAmount.formatEuro()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        if (!isExpanded) {
                            // Thin progress line when collapsed
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LinearProgressIndicator(
                                    progress = { progress.toFloat().coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(CircleShape),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "$progressPercent%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            // Expanded view details
                            Text(
                                text = "${"Scadenza:".t(language)} ${goal.deadline}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$progressPercent% ${"completato".t(language)}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            // Standard progress bar
                            LinearProgressIndicator(
                                progress = { progress.toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                            // Actions Row (Assegna, Rimuovi, Completa, Elimina)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Outlined "+" button for assigning funds
                                OutlinedButton(
                                    onClick = { selectedGoalForAllocating = goal },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Assegna fondi", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Assegna".t(language), fontSize = 11.sp)
                                }

                                // Outlined "-" button for removing funds
                                if (goal.currentAmount > 0.0) {
                                    OutlinedButton(
                                        onClick = { selectedGoalForDeallocating = goal },
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Rimuovi fondi", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Rimuovi".t(language), fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // "Completa" Button
                                Button(
                                    onClick = { selectedGoalForCompleting = goal },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Completa", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Completa".t(language), fontSize = 11.sp)
                                }

                                // "Elimina" Button (trash icon button)
                                IconButton(
                                    onClick = { viewModel.deleteSavingsGoal(goal) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Elimina Obiettivo",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddGoalDialog) {
        AddSavingsGoalDialog(
            viewModel = viewModel,
            onDismiss = { showAddGoalDialog = false }
        )
    }

    if (showAccantonaDialog) {
        AccantonaFondiDialog(
            accounts = accounts,
            viewModel = viewModel,
            onDismiss = { showAccantonaDialog = false }
        )
    }

    if (showRilasciaDialog) {
        RilasciaFondiDialog(
            accounts = accounts,
            savedForGoalsMap = totalSavedForGoalsByAccount,
            unallocatedAmount = unallocatedSavings,
            viewModel = viewModel,
            onDismiss = { showRilasciaDialog = false }
        )
    }

    if (selectedGoalForAllocating != null) {
        AssegnaFondiAObiettivoDialog(
            goal = selectedGoalForAllocating!!,
            unallocatedAmount = unallocatedSavings,
            viewModel = viewModel,
            onDismiss = { selectedGoalForAllocating = null }
        )
    }

    if (selectedGoalForDeallocating != null) {
        RimuoviFondiDaObiettivoDialog(
            goal = selectedGoalForDeallocating!!,
            viewModel = viewModel,
            onDismiss = { selectedGoalForDeallocating = null }
        )
    }

    if (selectedGoalForCompleting != null) {
        CompletaObiettivoDialog(
            goal = selectedGoalForCompleting!!,
            accounts = accounts,
            viewModel = viewModel,
            onDismiss = { selectedGoalForCompleting = null }
        )
    }
}

// --- SCREEN 6: CATEGORIES HIERARCHY SCREEN ---
@Composable
fun CategoriesScreen(
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var selectedTabByTypeName by remember { mutableStateOf("Expense") } // "Expense" or "Income"

    var searchQuery by remember { mutableStateOf("") }

    // Dialog state for editing a category or subcategory
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    
    // Dialog state for adding a subcategory to a specific parent category
    var parentCategoryForNewSub by remember { mutableStateOf<Category?>(null) }
    
    var categoryForDefaultAccountSetup by remember { mutableStateOf<Category?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ⭐ Legend Card (Point 9)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("⭐", fontSize = 22.sp)
                Text(
                    text = "Legenda: Clicca sulla stellina per impostarla come inserimento rapido sulla Home (Max 8).".t(language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Macro Categories Tab Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { selectedTabByTypeName = "Expense" },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTabByTypeName == "Expense") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedTabByTypeName == "Expense") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Spese 💸".t(language))
            }
            Button(
                onClick = { selectedTabByTypeName = "Income" },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTabByTypeName == "Income") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedTabByTypeName == "Income") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Entrate 💰".t(language))
            }
        }

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cerca categorie...".t(language)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(24.dp)
        )

        // Filter primary categories of the selected type
        val primaryCategories = categories.filter {
            it.parentCategoryId == null && 
            it.type == selectedTabByTypeName &&
            it.name.contains(searchQuery, ignoreCase = true)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(primaryCategories.size) { index ->
                val cat = primaryCategories[index]
                val subCats = categories.filter { it.parentCategoryId == cat.id }

                var isExpanded by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = !isExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(cat.iconEmoji, fontSize = 22.sp)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        cat.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        "${subCats.size} sottocategorie",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val isQuick = cat.isQuickAction
                                IconButton(
                                    onClick = {
                                        if (isQuick) {
                                            viewModel.updateCategory(cat.copy(isQuickAction = false))
                                        } else {
                                            val starredCount = categories.count { it.isQuickAction }
                                            if (starredCount >= 8) {
                                                android.widget.Toast.makeText(
                                                    context,
                                                    "Non puoi selezionare più di 8 inserimenti rapidi!".t(language),
                                                    android.widget.Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                categoryForDefaultAccountSetup = cat
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isQuick) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Starred",
                                        tint = if (isQuick) Color(0xFFFCD664) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { categoryToEdit = cat },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Modifica",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deleteCategory(cat) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Elimina",
                                        tint = Color.Red,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            }
                        }

                        // Expanded dropdown showing subcategories list
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                                subCats.forEach { sub ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(sub.iconEmoji, fontSize = 18.sp)
                                            Text(sub.name, style = MaterialTheme.typography.bodyLarge)
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            val isSubQuick = sub.isQuickAction
                                            IconButton(
                                                onClick = {
                                                    if (isSubQuick) {
                                                        viewModel.updateCategory(sub.copy(isQuickAction = false))
                                                    } else {
                                                        val starredCount = categories.count { it.isQuickAction }
                                                        if (starredCount >= 8) {
                                                            android.widget.Toast.makeText(
                                                                context,
                                                                "Non puoi selezionare più di 8 inserimenti rapidi!".t(language),
                                                                android.widget.Toast.LENGTH_SHORT
                                                            ).show()
                                                        } else {
                                                            categoryForDefaultAccountSetup = sub
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isSubQuick) Icons.Default.Star else Icons.Default.StarBorder,
                                                    contentDescription = "Starred",
                                                    tint = if (isSubQuick) Color(0xFFFCD664) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { categoryToEdit = sub },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Modifica Sottocategoria",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { viewModel.deleteCategory(sub) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Elimina Sottocategoria",
                                                    tint = Color.Red,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                if (subCats.isEmpty()) {
                                    Text(
                                        text = "Nessuna sottocategoria",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }

                                // Quick button to add a subcategory
                                TextButton(
                                    onClick = { parentCategoryForNewSub = cat },
                                    modifier = Modifier.align(Alignment.Start),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Aggiungi Sottocategoria", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Add category button dotted card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showAddCategoryDialog = true }
                        .drawBehind {
                            // Dotted border
                            drawRoundRect(
                                color = Color.LightGray,
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                        floatArrayOf(10f, 10f),
                                        0f
                                    )
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = Color.Gray)
                        Text(
                            text = (if (selectedTabByTypeName == "Expense") "Nuova Categoria Spesa" else "Nuova Categoria Entrata").t(language),
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            type = selectedTabByTypeName,
            viewModel = viewModel,
            onDismiss = { showAddCategoryDialog = false }
        )
    }

    if (parentCategoryForNewSub != null) {
        AddCategorySubcategoryDialog(
            parentCategory = parentCategoryForNewSub!!,
            viewModel = viewModel,
            onDismiss = { parentCategoryForNewSub = null }
        )
    }

    if (categoryToEdit != null) {
        EditCategoryDialog(
            category = categoryToEdit!!,
            viewModel = viewModel,
            onDismiss = { categoryToEdit = null }
        )
    }

    if (categoryForDefaultAccountSetup != null) {
        val cat = categoryForDefaultAccountSetup!!
        var selectedAccountIdSetup by remember { mutableStateOf(accounts.firstOrNull()?.id) }
        
        AlertDialog(
            onDismissRequest = { categoryForDefaultAccountSetup = null },
            title = { Text("Conto Addebito Predefinito".t(language)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "${"Seleziona il conto su cui addebitare di default la spesa rapida per".t(language)}: ${cat.iconEmoji} ${cat.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    accounts.forEach { account ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedAccountIdSetup = account.id }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RadioButton(
                                selected = selectedAccountIdSetup == account.id,
                                onClick = { selectedAccountIdSetup = account.id }
                            )
                            Text(text = account.name, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateCategory(cat.copy(isQuickAction = true, defaultAccountId = selectedAccountIdSetup))
                        categoryForDefaultAccountSetup = null
                    }
                ) {
                    Text("Conferma".t(language))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.updateCategory(cat.copy(isQuickAction = true, defaultAccountId = null))
                        categoryForDefaultAccountSetup = null
                    }
                ) {
                    Text("Salta / Nessuno".t(language))
                }
            }
        )
    }
}

// --- SCREEN 7: SETTINGS SCREEN (IMPOSTAZIONI) ---
@Composable
fun SettingsScreen(
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val startOfWeek by viewModel.startOfWeek.collectAsStateWithLifecycle()
    val finMonthDay by viewModel.financialMonthStartDay.collectAsStateWithLifecycle()
    val pushNotif by viewModel.pushNotificationsEnabled.collectAsStateWithLifecycle()
    val budgetOn by viewModel.budgetModuleEnabled.collectAsStateWithLifecycle()
    val goalsOn by viewModel.goalsModuleEnabled.collectAsStateWithLifecycle()
    val reportsOn by viewModel.reportsModuleEnabled.collectAsStateWithLifecycle()
    val catOn by viewModel.categoriesModuleEnabled.collectAsStateWithLifecycle()
    val planningOn by viewModel.planningModuleEnabled.collectAsStateWithLifecycle()
    val exportOn by viewModel.exportModuleEnabled.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val theme by viewModel.appTheme.collectAsStateWithLifecycle()
    val showBudgieTip by viewModel.showBudgieTip.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // A. CALENDARIO E PERIODI
        Text(
            text = "CALENDARIO E PERIODI".t(language),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.Gray
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Inizio settimana
                Column {
                    Text("Inizio Settimana".t(language), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val daysList = listOf("Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica")
                        daysList.forEach { fullName ->
                            val initial = when (fullName) {
                                "Lunedì" -> when (language) {
                                    "English" -> "M"
                                    "Español" -> "L"
                                    "Català" -> "Dl"
                                    "Français" -> "L"
                                    "Deutsch" -> "M"
                                    else -> "L"
                                }
                                "Martedì" -> when (language) {
                                    "English" -> "T"
                                    "Español" -> "M"
                                    "Català" -> "Dt"
                                    "Français" -> "M"
                                    "Deutsch" -> "D"
                                    else -> "M"
                                }
                                "Mercoledì" -> when (language) {
                                    "English" -> "W"
                                    "Español" -> "X"
                                    "Català" -> "Dc"
                                    "Français" -> "M"
                                    "Deutsch" -> "M"
                                    else -> "M"
                                }
                                "Giovedì" -> when (language) {
                                    "English" -> "T"
                                    "Español" -> "J"
                                    "Català" -> "Dj"
                                    "Français" -> "J"
                                    "Deutsch" -> "D"
                                    else -> "G"
                                }
                                "Venerdì" -> when (language) {
                                    "English" -> "F"
                                    "Español" -> "V"
                                    "Català" -> "Dv"
                                    "Français" -> "V"
                                    "Deutsch" -> "F"
                                    else -> "V"
                                }
                                "Sabato" -> when (language) {
                                    "English" -> "S"
                                    "Español" -> "S"
                                    "Català" -> "Ds"
                                    "Français" -> "S"
                                    "Deutsch" -> "S"
                                    else -> "S"
                                }
                                "Domenica" -> when (language) {
                                    "English" -> "S"
                                    "Español" -> "D"
                                    "Català" -> "Dg"
                                    "Français" -> "D"
                                    "Deutsch" -> "S"
                                    else -> "D"
                                }
                                else -> ""
                            }
                            val isSelected = startOfWeek == fullName
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary 
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { viewModel.setStartOfWeek(fullName) }
                                    .testTag("day_button_" + fullName),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initial,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }

                Divider()

                // Mese Finanziario
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mese Finanziario (Giorno Inizio)".t(language), fontWeight = FontWeight.Bold)
                        Text(finMonthDay.toString(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = finMonthDay.toFloat(),
                        onValueChange = { viewModel.setFinancialMonthStartDay(it.toInt()) },
                        valueRange = 1f..31f,
                        steps = 30
                    )
                    Text(
                        "Il ciclo mensile terminerà automaticamente il giorno precedente a quello selezionato.".t(language),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }
            }
        }

        // B. COMUNICAZIONE
        Text(
            text = "COMUNICAZIONE".t(language),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.Gray
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Notifiche Push".t(language), fontWeight = FontWeight.Bold)
                    Text("Rimani aggiornato sulle tue spese in tempo reale.".t(language), style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                }
                Switch(checked = pushNotif, onCheckedChange = { viewModel.pushNotificationsEnabled.value = it })
            }
        }

        // C. PERSONALIZZAZIONE FUNZIONI
        Text(
            text = "PERSONALIZZAZIONE FUNZIONI".t(language),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FunctionSettingRow("Modulo Budget".t(language), budgetOn) { viewModel.toggleBudgetModule(it) }
                Divider()
                FunctionSettingRow("Modulo Obiettivi".t(language), goalsOn) { viewModel.toggleGoalsModule(it) }
                Divider()
                FunctionSettingRow("Modulo Report".t(language), reportsOn) { viewModel.toggleReportsModule(it) }
                Divider()
                FunctionSettingRow("Modulo Pianificazione".t(language), planningOn) { viewModel.togglePlanningModule(it) }
                Divider()
                FunctionSettingRow("Modulo Esportazione".t(language), exportOn) { viewModel.toggleExportModule(it) }
                Divider()
                FunctionSettingRow("Andamento Finanziario".t(language), showBudgieTip) { viewModel.setShowBudgieTip(it) }
            }
        }

        // D. GENERALE
        Text(
            text = "GENERALE".t(language),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Lingua
                var showLanguageDialog by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLanguageDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Lingua".t(language), fontWeight = FontWeight.Bold)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (language == "Català") {
                            Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_flag_catalonia),
                                contentDescription = "Catalonia Flag",
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            val flag = when (language) {
                                "Italiano" -> "🇮🇹"
                                "English" -> "🇬🇧"
                                "Español" -> "🇪🇸"
                                "Français" -> "🇫🇷"
                                "Deutsch" -> "🇩🇪"
                                else -> ""
                            }
                            if (flag.isNotEmpty()) Text(flag, fontSize = 16.sp)
                        }
                        Text(language, color = Color.Gray)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }

                if (showLanguageDialog) {
                    AlertDialog(
                        onDismissRequest = { showLanguageDialog = false },
                        title = { Text("Seleziona la lingua dell'applicazione:".t(language)) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Italiano", "English", "Español", "Català", "Français", "Deutsch").forEach { lang ->
                                    val isSelected = language == lang
                                    TextButton(
                                        onClick = {
                                            viewModel.setAppLanguage(lang)
                                            showLanguageDialog = false
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.textButtonColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else Color.Transparent
                                        )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            if (lang == "Català") {
                                                Image(
                                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_flag_catalonia),
                                                    contentDescription = "Catalonia Flag",
                                                    modifier = Modifier
                                                        .size(22.dp)
                                                        .clip(RoundedCornerShape(3.dp)),
                                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                                )
                                            } else {
                                                val flag = when(lang) {
                                                    "Italiano" -> "🇮🇹"
                                                    "English" -> "🇬🇧"
                                                    "Español" -> "🇪🇸"
                                                    "Français" -> "🇫🇷"
                                                    "Deutsch" -> "🇩🇪"
                                                    else -> ""
                                                }
                                                Text(flag, fontSize = 18.sp)
                                            }
                                            Text(
                                                text = lang,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showLanguageDialog = false }) {
                                Text("Chiudi".t(language))
                            }
                        }
                    )
                }

                Divider()

                // Valuta
                val currencyCode by viewModel.currencyCode.collectAsStateWithLifecycle()
                val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
                var showCurrencyDialog by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCurrencyDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Valuta".t(language), fontWeight = FontWeight.Bold)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val currentCurr = supportedCurrencies.find { it.code == currencyCode }
                        if (currentCurr != null) {
                            Text(currentCurr.flag, fontSize = 16.sp)
                            Text("${currentCurr.symbol} (${currentCurr.code})", color = Color.Gray)
                        } else {
                            Text("$currencySymbol ($currencyCode)", color = Color.Gray)
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }

                if (showCurrencyDialog) {
                    AlertDialog(
                        onDismissRequest = { showCurrencyDialog = false },
                        title = { Text("Seleziona la valuta dell'applicazione:".t(language)) },
                        text = {
                            Column(
                                modifier = Modifier
                                    .heightIn(max = 360.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                supportedCurrencies.forEach { curr ->
                                    val isSelected = currencyCode == curr.code
                                    TextButton(
                                        onClick = {
                                            viewModel.setCurrency(curr.code, curr.symbol)
                                            showCurrencyDialog = false
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.textButtonColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else Color.Transparent
                                        )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(curr.flag, fontSize = 18.sp)
                                            Text(
                                                text = "${curr.symbol} - ${curr.name} (${curr.code})",
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showCurrencyDialog = false }) {
                                Text("Chiudi".t(language))
                            }
                        }
                    )
                }

                Divider()

                // Tema
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setAppTheme(if (theme == "Chiaro") "Scuro" else "Chiaro")
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tema".t(language), fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val themeLabel = if (theme == "Chiaro") "Chiaro".t(language) else "Scuro".t(language)
                        Text(themeLabel, color = Color.Gray)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // E. ZONA DI PERICOLO
        var showResetConfirmationDialog by remember { mutableStateOf(false) }

        Text(
            text = "ZONA DI PERICOLO".t(language),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.error
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showResetConfirmationDialog = true }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ripristina Dati".t(language),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Cancella tutti i dati salvati e riavvia l'onboarding.".t(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Reset",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (showResetConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showResetConfirmationDialog = false },
                title = { Text("Sei sicuro di voler resettare?".t(language)) },
                text = { Text("Questa operazione cancellerà tutti i conti, transazioni, budget e impostazioni. Non è possibile annullare questa azione.".t(language)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showResetConfirmationDialog = false
                            viewModel.resetAllUserData()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Sì, Resetta".t(language))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetConfirmationDialog = false }) {
                        Text("Annulla".t(language))
                    }
                }
            )
        }

        // Footer version representation and developer credits
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("BUDGIE v0.9.7", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text("Progettato per la tua serenità finanziaria".t(language), style = MaterialTheme.typography.labelSmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${"Sviluppatore:".t(language)} Brombolo",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse("https://github.com/Brombolo/Budgie")
                        )
                        context.startActivity(intent)
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = "GitHub",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "https://github.com/Brombolo/Budgie",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                )
            }

            Text(
                text = "${"Licenza:".t(language)} GNU General Public License v3.0 (GPL-3.0)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun FunctionSettingRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.SemiBold)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}


// --- SCREEN DIALOGS IMPLEMENTATIONS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    type: String,
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var selectedSubCategoryId by remember { mutableStateOf<Int?>(null) }
    var sourceAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 0) }
    var destinationAccountId by remember { mutableStateOf(accounts.getOrNull(1)?.id ?: 0) }

    LaunchedEffect(type, categories) {
        val mainCats = categories.filter { it.parentCategoryId == null && it.type == type }
        if (selectedCategoryId == null || !mainCats.any { it.id == selectedCategoryId }) {
            selectedCategoryId = mainCats.firstOrNull()?.id
            selectedSubCategoryId = null
        }
    }

    var selectedTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }
    val calendar = remember { java.util.Calendar.getInstance() }
    val sdf = remember { java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.ITALIAN) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            val titleText = when (type) {
                "Expense" -> "Nuova Spesa".t(language)
                "Income" -> "Nuova Entrata".t(language)
                else -> "Nuovo Giroconto".t(language)
            }
            Text(text = titleText)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { 
                        amountStr = it 
                        errorMessage = null
                    },
                    label = { Text("Importo (€)".t(language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Data e Ora Corrente (Modificabile dall'utente)
                val formattedDateTime = sdf.format(selectedTimestamp)
                OutlinedTextField(
                    value = formattedDateTime,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Data e Ora".t(language)) },
                    trailingIcon = {
                        IconButton(onClick = {
                            val datePickerDialog = android.app.DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    calendar.set(java.util.Calendar.YEAR, year)
                                    calendar.set(java.util.Calendar.MONTH, month)
                                    calendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth)
                                    
                                    android.app.TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            calendar.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay)
                                            calendar.set(java.util.Calendar.MINUTE, minute)
                                            calendar.set(java.util.Calendar.SECOND, 0)
                                            calendar.set(java.util.Calendar.MILLISECOND, 0)
                                            selectedTimestamp = calendar.timeInMillis
                                        },
                                        calendar.get(java.util.Calendar.HOUR_OF_DAY),
                                        calendar.get(java.util.Calendar.MINUTE),
                                        true
                                    ).show()
                                },
                                calendar.get(java.util.Calendar.YEAR),
                                calendar.get(java.util.Calendar.MONTH),
                                calendar.get(java.util.Calendar.DAY_OF_MONTH)
                            )
                            datePickerDialog.show()
                        }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleziona Data e Ora".t(language))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val datePickerDialog = android.app.DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    calendar.set(java.util.Calendar.YEAR, year)
                                    calendar.set(java.util.Calendar.MONTH, month)
                                    calendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth)
                                    
                                    android.app.TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            calendar.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay)
                                            calendar.set(java.util.Calendar.MINUTE, minute)
                                            calendar.set(java.util.Calendar.SECOND, 0)
                                            calendar.set(java.util.Calendar.MILLISECOND, 0)
                                            selectedTimestamp = calendar.timeInMillis
                                        },
                                        calendar.get(java.util.Calendar.HOUR_OF_DAY),
                                        calendar.get(java.util.Calendar.MINUTE),
                                        true
                                    ).show()
                                },
                                calendar.get(java.util.Calendar.YEAR),
                                calendar.get(java.util.Calendar.MONTH),
                                calendar.get(java.util.Calendar.DAY_OF_MONTH)
                            )
                            datePickerDialog.show()
                        }
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 22) title = it },
                    label = { Text("Nota (massimo 22 caratteri)".t(language)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Account sorgente
                Text("Seleziona Conto:".t(language))
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    accounts.forEach { acc ->
                        FilterChip(
                            selected = sourceAccountId == acc.id,
                            onClick = { sourceAccountId = acc.id },
                            label = { Text(acc.name) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                // Account destinazione (Solo per Giroconto)
                if (type == "Transfer") {
                    Text("A quale conto destinare:".t(language))
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        accounts.filter { it.id != sourceAccountId }.forEach { acc ->
                            FilterChip(
                                selected = destinationAccountId == acc.id,
                                onClick = { destinationAccountId = acc.id },
                                label = { Text(acc.name) },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                // Categoria (Solo per Spesa o Entrata)
                if (type != "Transfer") {
                    val mainCats = categories.filter { it.parentCategoryId == null && it.type == type }
                    val currentCatId = selectedCategoryId ?: mainCats.firstOrNull()?.id
                    
                    Text("Categoria".t(language) + ":", style = MaterialTheme.typography.labelLarge)
                    BudgieDropdown(
                        label = "Seleziona Categoria".t(language),
                        options = mainCats,
                        selectedOption = categories.find { it.id == currentCatId } ?: mainCats.firstOrNull(),
                        optionToString = { it?.let { "${it.iconEmoji} ${it.name}" } ?: "Nessuna Categoria".t(language) },
                        onOptionSelected = { cat ->
                            selectedCategoryId = cat?.id
                            selectedSubCategoryId = null
                        }
                    )

                    if (currentCatId != null) {
                        val subCats = categories.filter { it.parentCategoryId == currentCatId }
                        if (subCats.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Sottocategoria (Opzionale):".t(language), style = MaterialTheme.typography.labelLarge)
                            val subOptions = listOf(null) + subCats
                            BudgieDropdown(
                                label = "Seleziona Sottocategoria".t(language),
                                options = subOptions,
                                selectedOption = categories.find { it.id == selectedSubCategoryId },
                                optionToString = { it?.let { "${it.iconEmoji} ${it.name}" } ?: "Tutte (Intera Categoria)".t(language) },
                                onOptionSelected = { subCat ->
                                    selectedSubCategoryId = subCat?.id
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                    val mainCats = categories.filter { it.parentCategoryId == null && it.type == type }
                    val effectiveCatId = if (type == "Transfer") null else (selectedSubCategoryId ?: selectedCategoryId ?: mainCats.firstOrNull()?.id)
                    val effectiveSourceAccountId = if (accounts.any { it.id == sourceAccountId }) sourceAccountId else (accounts.firstOrNull()?.id ?: 0)

                    when {
                        amount <= 0.0 -> {
                            errorMessage = "Inserisci un importo valido maggiore di zero.".t(language)
                        }
                        effectiveSourceAccountId == 0 -> {
                            errorMessage = "Nessun conto selezionato. Crea o seleziona un conto.".t(language)
                        }
                        type == "Transfer" && destinationAccountId == effectiveSourceAccountId -> {
                            errorMessage = "Seleziona un conto di destinazione diverso dal conto di origine.".t(language)
                        }
                        type != "Transfer" && effectiveCatId == null -> {
                            errorMessage = "Seleziona una categoria.".t(language)
                        }
                        else -> {
                            try {
                                viewModel.addTransaction(
                                    title = title,
                                    amount = amount,
                                    type = type,
                                    categoryId = effectiveCatId,
                                    sourceAccountId = effectiveSourceAccountId,
                                    destinationAccountId = if (type == "Transfer") destinationAccountId else null,
                                    customTimestamp = selectedTimestamp
                                )
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore durante il salvataggio: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                }
            ) {
                Text("Salva".t(language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla".t(language))
            }
        }
    )
}

@Composable
fun EmojiSelector(
    selectedEmoji: String,
    onEmojiSelected: (String) -> Unit
) {
    val emojis = listOf(
        "💳", "💰", "💸", "🏦", "📈", "📅", "🔁", "💵", "👛", "📊", 
        "💲", "🪙", "💎", "🧾", "💹", "🏧", "💱", "⚖️", "🤝", "📦", 
        "💴", "💶", "💷", "🛍️", "🛒", "📉", "📋", "🃏", "🔐"
    )
    
    val chunkedEmojis = emojis.chunked(6)
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        chunkedEmojis.forEach { rowEmojis ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowEmojis.forEach { emoji ->
                    val isSelected = selectedEmoji == emoji
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else Color.Transparent
                            )
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onEmojiSelected(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 20.sp)
                    }
                }
                if (rowEmojis.size < 6) {
                    repeat(6 - rowEmojis.size) {
                        Spacer(modifier = Modifier.size(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AddAccountDialog(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var balanceStr by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("💳") } // Holds the chosen emoji
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuovo Conto".t(language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        errorMessage = null
                    },
                    label = { Text("Nome Conto".t(language)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = balanceStr,
                    onValueChange = { 
                        balanceStr = it
                        errorMessage = null
                    },
                    label = { Text("Saldo Iniziale (€)".t(language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Scegli l'icona:".t(language), style = MaterialTheme.typography.labelLarge)
                EmojiSelector(
                    selectedEmoji = type,
                    onEmojiSelected = { type = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val balance = balanceStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                    when {
                        name.isBlank() -> {
                            errorMessage = "Inserisci un nome per il conto.".t(language)
                        }
                        else -> {
                            try {
                                viewModel.addAccount(name, type, balance)
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore durante la creazione: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                }
            ) {
                Text("Crea".t(language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla".t(language))
            }
        }
    )
}

@Composable
fun AddCategoryDialog(
    type: String,
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("🍕") }
    var parentId by remember { mutableStateOf<Int?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val mainCategories = categories.filter { it.parentCategoryId == null && it.type == type }
    val isEmojiValid = isValidSingleEmoji(emoji)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text((if (type == "Expense") "Nuova Categoria Spesa" else "Nuova Categoria Entrata").t(language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        errorMessage = null
                        val suggested = suggestEmoji(it)
                        if (suggested != "🌍" || emoji == "🍕") {
                            emoji = suggested
                        }
                    },
                    label = { Text("Nome Categoria".t(language)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = emoji,
                    onValueChange = { 
                        emoji = it
                        errorMessage = null
                    },
                    label = { Text("Emoji Icona".t(language)) },
                    isError = !isEmojiValid,
                    supportingText = {
                        if (!isEmojiValid) {
                            Text("Inserisci un singolo carattere emoji valido".t(language), color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("Carattere emoji valido".t(language))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Sottocategoria di (Opzionale):".t(language), style = MaterialTheme.typography.labelLarge)
                val parentOptions = listOf(null) + mainCategories
                BudgieDropdown(
                    label = "Sottocategoria di".t(language),
                    options = parentOptions,
                    selectedOption = mainCategories.find { it.id == parentId },
                    optionToString = { it?.let { "${it.iconEmoji} ${it.name}" } ?: "Nessuna (Principale)".t(language) },
                    onOptionSelected = { parentId = it?.id }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        name.isBlank() -> errorMessage = "Inserisci un nome per la categoria.".t(language)
                        !isEmojiValid -> errorMessage = "Inserisci un'emoji valida.".t(language)
                        else -> {
                            try {
                                viewModel.addCategory(name, emoji, parentId, type)
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore durante la creazione: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                }
            ) {
                Text("Aggiungi".t(language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla".t(language))
            }
        }
    )
}

@Composable
fun AdjustBudgetDialog(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val budgets by viewModel.budgets.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()

    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull { it.parentCategoryId == null }?.id ?: 0) }
    var limitStr by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val mainCategories = categories.filter { it.parentCategoryId == null }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Regola Limite Mensile".t(language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Text("Seleziona Categoria:".t(language), style = MaterialTheme.typography.labelLarge)
                BudgieDropdown(
                    label = "Categoria".t(language),
                    options = mainCategories,
                    selectedOption = mainCategories.find { it.id == selectedCategoryId } ?: mainCategories.firstOrNull(),
                    optionToString = { it?.let { "${it.iconEmoji} ${it.name}" } ?: "Nessuna Categoria".t(language) },
                    onOptionSelected = { cat ->
                        if (cat != null) {
                            selectedCategoryId = cat.id
                            val limitVal = budgets.find { it.categoryId == cat.id }?.amountLimit ?: 0.0
                            limitStr = if (limitVal > 0.0) limitVal.toString() else ""
                        }
                    }
                )

                OutlinedTextField(
                    value = limitStr,
                    onValueChange = { 
                        limitStr = it
                        errorMessage = null
                    },
                    label = { Text("Limite Mensile (€)".t(language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val limit = limitStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                    when {
                        selectedCategoryId <= 0 -> errorMessage = "Seleziona una categoria.".t(language)
                        limit <= 0.0 -> errorMessage = "Inserisci un limite mensile valido maggiore di zero.".t(language)
                        else -> {
                            try {
                                viewModel.updateBudgetLimit(selectedCategoryId, limit, "Monthly")
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore durante il salvataggio: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                }
            ) {
                Text("Salva".t(language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla".t(language))
            }
        }
    )
}

@Composable
fun AddSavingsGoalDialog(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var targetStr by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var iconEmoji by remember { mutableStateOf("🎯") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isEmojiValid = isValidSingleEmoji(iconEmoji)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuovo Obiettivo".t(language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        if (it.length <= 22 && !it.contains("\n")) name = it
                        errorMessage = null
                    },
                    label = { Text("Nome Obiettivo".t(language)) },
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { 
                        targetStr = it
                        errorMessage = null
                    },
                    label = { Text("Cifra Target (€)".t(language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Scadenza (es: Estate 2024)".t(language)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = iconEmoji,
                    onValueChange = { 
                        iconEmoji = it
                        errorMessage = null
                    },
                    label = { Text("Emoji Icona".t(language)) },
                    isError = !isEmojiValid,
                    supportingText = {
                        if (!isEmojiValid) {
                            Text("Inserisci un singolo carattere emoji valido".t(language), color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("Carattere emoji valido".t(language))
                        }
                    },
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                    when {
                        name.isBlank() -> errorMessage = "Inserisci il nome dell'obiettivo.".t(language)
                        target <= 0.0 -> errorMessage = "Inserisci una cifra target valida maggiore di zero.".t(language)
                        !isEmojiValid -> errorMessage = "Inserisci un'emoji valida.".t(language)
                        else -> {
                            try {
                                viewModel.addSavingsGoal(name, target, deadline, iconEmoji)
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore durante la creazione: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                },
                enabled = name.isNotBlank() && targetStr.isNotBlank() && isEmojiValid
            ) {
                Text("Salva".t(language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla".t(language))
            }
        }
    )
}

@Composable
fun AccantonaFondiDialog(
    accounts: List<Account>,
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    var amountStr by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Accantona Fondi".t(language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Text("Preleva fittiziamente del denaro da un tuo conto per metterlo nel Salvadanaio Virtuale.".t(language))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { 
                        amountStr = it 
                        errorMessage = null
                    },
                    label = { Text("Quota da accantonare (€)".t(language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Seleziona conto di origine:".t(language))
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    accounts.forEach { acc ->
                        FilterChip(
                            selected = selectedAccountId == acc.id,
                            onClick = { selectedAccountId = acc.id },
                            label = { Text("${acc.name} (${acc.balance.formatEuro()})") },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                    val activeAccId = if (accounts.any { it.id == selectedAccountId }) selectedAccountId else (accounts.firstOrNull()?.id ?: 0)
                    when {
                        amount <= 0.0 -> errorMessage = "Inserisci una quota valida maggiore di zero.".t(language)
                        activeAccId <= 0 -> errorMessage = "Seleziona un conto valido.".t(language)
                        else -> {
                            try {
                                viewModel.addVirtualSaving(amount, activeAccId)
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore durante l'accantonamento: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                }
            ) {
                Text("Accantona".t(language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla".t(language))
            }
        }
    )
}

@Composable
fun RilasciaFondiDialog(
    accounts: List<Account>,
    savedForGoalsMap: Map<Int, Double>,
    unallocatedAmount: Double,
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    var amountStr by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val savedInSelectedAcc = savedForGoalsMap[selectedAccountId] ?: 0.0
    val maxReleaseLimit = minOf(savedInSelectedAcc, unallocatedAmount)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rilascia Fondi".t(language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Text("Preleva fondi dal Salvadanaio Virtuale per riallinearli come disponibili nel tuo conto.".t(language))

                if (maxReleaseLimit < savedInSelectedAcc) {
                    Text(
                        text = "Nota: hai dei fondi vincolati ad obiettivi individuali. Per rilasciare più di".t(language) + " ${maxReleaseLimit.formatEuro()}, " + "rimuovi prima i fondi dagli obiettivi.".t(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                val parsedAmount = amountStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                val isError = parsedAmount > maxReleaseLimit

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { 
                        amountStr = it 
                        errorMessage = null
                    },
                    label = { Text("Quota da rilasciare (€) - max".t(language) + " ${maxReleaseLimit.formatEuro()}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = isError,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Seleziona conto di destinazione:".t(language))
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    accounts.forEach { acc ->
                        val savedAmount = savedForGoalsMap[acc.id] ?: 0.0
                        FilterChip(
                            selected = selectedAccountId == acc.id,
                            onClick = { selectedAccountId = acc.id },
                            label = { Text("${acc.name} (${"Accantonato:".t(language)} ${savedAmount.formatEuro()})") },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                    val activeAccId = if (accounts.any { it.id == selectedAccountId }) selectedAccountId else (accounts.firstOrNull()?.id ?: 0)
                    when {
                        amount <= 0.0 -> errorMessage = "Inserisci una quota valida maggiore di zero.".t(language)
                        activeAccId <= 0 -> errorMessage = "Seleziona un conto valido.".t(language)
                        amount > maxReleaseLimit -> errorMessage = "L'importo supera il limite massimo rilasciabile.".t(language)
                        else -> {
                            try {
                                viewModel.addVirtualWithdrawal(amount, activeAccId)
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore durante il rilascio: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                }
            ) {
                Text("Rilascia".t(language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla".t(language))
            }
        }
    )
}

@Composable
fun AssegnaFondiAObiettivoDialog(
    goal: SavingsGoal,
    unallocatedAmount: Double,
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    var amountStr by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val remainingTarget = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assegna Fondi a Obiettivo".t(language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Text("Destina parte dei risparmi non assegnati del Salvadanaio all'obiettivo:".t(language) + " ${goal.name}")
                Text("${"Disponibili non assegnati:".t(language)} ${unallocatedAmount.formatEuro()}", fontWeight = FontWeight.Bold)
                Text("${"Target rimanente:".t(language)} ${remainingTarget.formatEuro()}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { 
                        amountStr = it 
                        errorMessage = null
                    },
                    label = { Text("Importo da vincolare (€)".t(language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                if (remainingTarget > 0.0) {
                    TextButton(
                        onClick = { amountStr = remainingTarget.toString() },
                        enabled = unallocatedAmount > 0.0,
                        modifier = Modifier.align(Alignment.End),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("${"Imposta intero importo previsto".t(language)} (${remainingTarget.formatEuro()})")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                    when {
                        amount <= 0.0 -> errorMessage = "Inserisci un importo valido maggiore di zero.".t(language)
                        amount > unallocatedAmount -> errorMessage = "L'importo supera i fondi non assegnati disponibili.".t(language)
                        else -> {
                            try {
                                viewModel.allocateSavingsToGoal(goal, amount)
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore durante l'assegnazione: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                }
            ) {
                Text("Assegna".t(language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla".t(language))
            }
        }
    )
}

@Composable
fun RimuoviFondiDaObiettivoDialog(
    goal: SavingsGoal,
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    var amountStr by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rimuovi Fondi da Obiettivo".t(language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Text("Rilascia parte dei fondi vincolati a questo obiettivo e riportali nello stato non assegnato del Salvadanaio.".t(language))
                Text("${"Attualmente vincolati:".t(language)} ${goal.currentAmount.formatEuro()}", fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { 
                        amountStr = it 
                        errorMessage = null
                    },
                    label = { Text("Importo da svincolare (€)".t(language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                if (goal.currentAmount > 0.0) {
                    TextButton(
                        onClick = { amountStr = goal.currentAmount.toString() },
                        modifier = Modifier.align(Alignment.End),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("${"Rimuovi tutto".t(language)} (${goal.currentAmount.formatEuro()})")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                    when {
                        amount <= 0.0 -> errorMessage = "Inserisci un importo valido maggiore di zero.".t(language)
                        amount > goal.currentAmount -> errorMessage = "L'importo supera i fondi attualmente vincolati.".t(language)
                        else -> {
                            try {
                                viewModel.deallocateSavingsFromGoal(goal, amount)
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore durante il rilascio: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                }
            ) {
                Text("Rimuovi".t(language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla".t(language))
            }
        }
    )
}

@Composable
fun CompletaObiettivoDialog(
    goal: SavingsGoal,
    accounts: List<Account>,
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    var recordAsExpense by remember { mutableStateOf(false) }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 0) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var selectedSubCategoryId by remember { mutableStateOf<Int?>(null) }
    var note by remember { mutableStateOf(goal.name) }
    var selectedTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }

    val calendar = remember { java.util.Calendar.getInstance() }
    val sdf = remember { java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.ITALIAN) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Completa Obiettivo".t(language)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("${"Congratulazioni per aver completato l'obiettivo:".t(language)} ${goal.name}!", fontWeight = FontWeight.Bold)
                Text("L'obiettivo verrà chiuso. Vuoi registrare l'acquisto reale come una spesa reale in contabilità?".t(language))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable { recordAsExpense = !recordAsExpense }
                ) {
                    Checkbox(checked = recordAsExpense, onCheckedChange = { recordAsExpense = it })
                    Text("Registra come spesa reale".t(language))
                }

                if (recordAsExpense) {
                    Divider()
                    Text("Dettagli Spesa Reale:".t(language), fontWeight = FontWeight.Bold)

                    Text("Conto di addebito reale:".t(language))
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        accounts.forEach { acc ->
                            FilterChip(
                                selected = selectedAccountId == acc.id,
                                onClick = { selectedAccountId = acc.id },
                                label = { Text("${acc.name} (${acc.balance.formatEuro()})") },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Nota/Titolo Spesa".t(language)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Date selector
                    val formattedDateTime = sdf.format(selectedTimestamp)
                    OutlinedTextField(
                        value = formattedDateTime,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Data e Ora".t(language)) },
                        trailingIcon = {
                            IconButton(onClick = {
                                android.app.DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        calendar.set(java.util.Calendar.YEAR, year)
                                        calendar.set(java.util.Calendar.MONTH, month)
                                        calendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth)
                                        
                                        android.app.TimePickerDialog(
                                            context,
                                            { _, hourOfDay, minute ->
                                                calendar.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay)
                                                calendar.set(java.util.Calendar.MINUTE, minute)
                                                selectedTimestamp = calendar.timeInMillis
                                            },
                                            calendar.get(java.util.Calendar.HOUR_OF_DAY),
                                            calendar.get(java.util.Calendar.MINUTE),
                                            true
                                        ).show()
                                    },
                                    calendar.get(java.util.Calendar.YEAR),
                                    calendar.get(java.util.Calendar.MONTH),
                                    calendar.get(java.util.Calendar.DAY_OF_MONTH)
                                ).show()
                            }) {
                                Icon(Icons.Default.DateRange, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Categories selection
                    val expenseCats = categories.filter { it.parentCategoryId == null && it.type == "Expense" }
                    Text("Categoria:".t(language))
                    BudgieDropdown(
                        label = "Seleziona Categoria".t(language),
                        options = expenseCats,
                        selectedOption = categories.find { it.id == selectedCategoryId } ?: expenseCats.firstOrNull(),
                        optionToString = { it?.let { "${it.iconEmoji} ${it.name}" } ?: "Nessuna".t(language) },
                        onOptionSelected = { cat ->
                            selectedCategoryId = cat?.id
                            selectedSubCategoryId = null
                        }
                    )

                    if (selectedCategoryId != null) {
                        val subCats = categories.filter { it.parentCategoryId == selectedCategoryId }
                        if (subCats.isNotEmpty()) {
                            Text("Sottocategoria (Opzionale):".t(language))
                            val subOptions = listOf(null) + subCats
                            BudgieDropdown(
                                label = "Seleziona Sottocategoria".t(language),
                                options = subOptions,
                                selectedOption = categories.find { it.id == selectedSubCategoryId },
                                optionToString = { it?.let { "${it.iconEmoji} ${it.name}" } ?: "Nessuna".t(language) },
                                onOptionSelected = { subCat ->
                                    selectedSubCategoryId = subCat?.id
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.completeSavingsGoal(
                        goal = goal,
                        recordAsExpense = recordAsExpense,
                        amount = goal.currentAmount,
                        fromAccountId = selectedAccountId,
                        categoryId = selectedCategoryId ?: categories.filter { it.parentCategoryId == null && it.type == "Expense" }.firstOrNull()?.id,
                        subCategoryId = selectedSubCategoryId,
                        note = note,
                        timestamp = selectedTimestamp
                    )
                    onDismiss()
                }
            ) {
                Text("Completa".t(language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla".t(language))
            }
        }
    )
}

// --- SCREEN 8: ACCOUNTS SCREEN (GESTIONE CONTI) ---
@Composable
fun AccountsScreen(
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    var showAddAccountDialog by remember { mutableStateOf(false) }
    var accountToEdit by remember { mutableStateOf<Account?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mascot header or Title Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "I Tuoi Conti".t(language) + " 💳",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Gestisci conti, carte e contanti".t(language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Button(
                onClick = { showAddAccountDialog = true },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nuovo".t(language))
            }
        }

        // Account list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(accounts.size) { index ->
                val acc = accounts[index]
                val emoji = acc.type.toAccountEmoji()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { accountToEdit = acc },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 28.sp)
                            }
                            Column {
                                Text(
                                    text = acc.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = acc.balance.formatEuroAnnotated(),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (acc.balance >= 0) MaterialTheme.colorScheme.primary else Color(0xFFBA1A1A)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Switch(
                                    checked = acc.isIncludedInTotal,
                                    onCheckedChange = { viewModel.toggleAccountInclusion(acc) },
                                    modifier = Modifier.scale(0.7f)
                                )
                                Text(
                                    text = "Includi nel totale".t(language),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddAccountDialog) {
        AddAccountDialog(
            viewModel = viewModel,
            onDismiss = { showAddAccountDialog = false }
        )
    }

    if (accountToEdit != null) {
        EditAccountDialog(
            account = accountToEdit!!,
            viewModel = viewModel,
            onDismiss = { accountToEdit = null }
        )
    }
}

// --- UTILITY EXTENSION ---
fun String.toAccountEmoji(): String = when (this) {
    "Bank" -> "🏦"
    "Card" -> "💳"
    "Cash" -> "💵"
    "Conto Corrente" -> "🏦"
    else -> if (this.isNotBlank()) this else "💰"
}

// --- EDIT ACCOUNT DIALOG ---
@Composable
fun EditAccountDialog(
    account: Account,
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf(account.name) }
    var balanceStr by remember { mutableStateOf(account.balance.toString()) }
    var type by remember {
        mutableStateOf(
            when (account.type) {
                "Bank" -> "🏦"
                "Card" -> "💳"
                "Cash" -> "💵"
                else -> if (account.type.isNotEmpty()) account.type else "💰"
            }
        )
    }
    var isIncluded by remember { mutableStateOf(account.isIncludedInTotal) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifica Conto".t(language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        errorMessage = null
                    },
                    label = { Text("Nome Conto".t(language)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = balanceStr,
                    onValueChange = { 
                        balanceStr = it
                        errorMessage = null
                    },
                    label = { Text("Saldo (€)".t(language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Scegli l'icona:".t(language), style = MaterialTheme.typography.labelLarge)
                EmojiSelector(
                    selectedEmoji = type,
                    onEmojiSelected = { type = it }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = isIncluded,
                        onCheckedChange = { isIncluded = it }
                    )
                    Text("Includi nei calcoli totali".t(language))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val balance = balanceStr.replace(',', '.').toDoubleOrNull() ?: account.balance
                    when {
                        name.isBlank() -> {
                            errorMessage = "Inserisci un nome per il conto.".t(language)
                        }
                        else -> {
                            try {
                                viewModel.updateAccount(account.copy(name = name, type = type, balance = balance, isIncludedInTotal = isIncluded))
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore durante il salvataggio: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                }
            ) {
                Text("Salva".t(language))
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = {
                        viewModel.deleteAccount(account)
                        onDismiss()
                    }
                ) {
                    Text("Elimina".t(language), color = Color.Red)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss) {
                    Text("Annulla".t(language))
                }
            }
        }
    )
}

// --- EDIT TRANSACTION DIALOG (PER CRONOLOGIA RECORD) ---
@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf(transaction.title) }
    var amountStr by remember { mutableStateOf(transaction.amount.toString()) }
    var selectedCategoryId by remember { mutableStateOf(transaction.categoryId) }
    var selectedSourceAccountId by remember { mutableStateOf(transaction.sourceAccountId) }
    var selectedDestinationAccountId by remember { mutableStateOf(transaction.destinationAccountId) }
    
    // Formatting timestamp
    val sdf = remember { java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.ITALY) }
    var dateStr by remember { mutableStateOf(sdf.format(java.util.Date(transaction.timestamp))) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifica Record".t(language)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { 
                        amountStr = it 
                        errorMessage = null
                    },
                    label = { Text("Importo (€)".t(language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dateStr,
                    onValueChange = { dateStr = it },
                    label = { Text("Data e Ora (gg/mm/aaaa oo:mm)".t(language)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 22) title = it },
                    label = { Text("Nota (massimo 22 caratteri)".t(language)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Select account
                Text("Conto Origine:".t(language), style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    accounts.forEach { acc ->
                        FilterChip(
                            selected = selectedSourceAccountId == acc.id,
                            onClick = { selectedSourceAccountId = acc.id },
                            label = { Text(acc.name) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                if (transaction.type == "Transfer") {
                    Text("Conto Destinazione:".t(language), style = MaterialTheme.typography.labelLarge)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        accounts.forEach { acc ->
                            FilterChip(
                                selected = selectedDestinationAccountId == acc.id,
                                onClick = { selectedDestinationAccountId = acc.id },
                                label = { Text(acc.name) },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                } else {
                    // Category dropdown
                    Text("Categoria:".t(language), style = MaterialTheme.typography.labelLarge)
                    val relevantCategories = categories.filter { it.type == transaction.type }
                    BudgieDropdown(
                        label = "Seleziona Categoria".t(language),
                        options = relevantCategories,
                        selectedOption = categories.find { it.id == selectedCategoryId } ?: relevantCategories.firstOrNull(),
                        optionToString = { cat ->
                            if (cat != null) {
                                if (cat.parentCategoryId != null) {
                                    val parentName = categories.find { it.id == cat.parentCategoryId }?.name ?: ""
                                    "${cat.iconEmoji} $parentName > ${cat.name}"
                                } else {
                                    "${cat.iconEmoji} ${cat.name}"
                                }
                            } else {
                                "Nessuna Categoria".t(language)
                            }
                        },
                        onOptionSelected = { cat ->
                            selectedCategoryId = cat?.id
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                    val parsedDate = try {
                        sdf.parse(dateStr)?.time
                    } catch (e: Exception) {
                        null
                    }

                    when {
                        amount <= 0.0 -> {
                            errorMessage = "Inserisci un importo valido maggiore di zero.".t(language)
                        }
                        parsedDate == null -> {
                            errorMessage = "Formato data non valido. Usa il formato gg/mm/aaaa oo:mm".t(language)
                        }
                        transaction.type == "Transfer" && selectedDestinationAccountId == selectedSourceAccountId -> {
                            errorMessage = "Seleziona un conto di destinazione diverso dal conto di origine.".t(language)
                        }
                        else -> {
                            try {
                                val updatedTx = transaction.copy(
                                    title = title,
                                    amount = amount,
                                    timestamp = parsedDate,
                                    categoryId = selectedCategoryId,
                                    sourceAccountId = selectedSourceAccountId,
                                    destinationAccountId = selectedDestinationAccountId
                                )
                                viewModel.updateTransaction(updatedTx, transaction)
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore durante il salvataggio: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                }
            ) {
                Text("Salva".t(language))
            }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        viewModel.deleteTransaction(transaction)
                        onDismiss()
                    }
                ) {
                    Text("Elimina".t(language), color = Color.Red)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss) {
                    Text("Annulla".t(language))
                }
            }
        }
    )
}

// --- ADD CATEGORY BUDGET DIALOG ---
@Composable
fun AddCategoryBudgetDialog(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val primaryCategories = categories.filter { it.parentCategoryId == null }

    var selectedCategoryId by remember { mutableStateOf(primaryCategories.firstOrNull()?.id) }
    var selectedSubCategoryId by remember { mutableStateOf<Int?>(null) }
    var amountStr by remember { mutableStateOf("") }
    var notifyOnOverflow by remember { mutableStateOf(true) }
    var autoRenew by remember { mutableStateOf(true) }
    var isPinnedToHome by remember { mutableStateOf(false) }
    var period by remember { mutableStateOf("Monthly") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuovo Budget di Categoria".t(language)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Text("Seleziona Categoria:".t(language), style = MaterialTheme.typography.labelLarge)
                if (primaryCategories.isNotEmpty()) {
                    val selectedCategory = categories.find { it.id == selectedCategoryId } ?: primaryCategories.first()
                    BudgieDropdown(
                        label = "Categoria".t(language),
                        options = primaryCategories,
                        selectedOption = selectedCategory,
                        optionToString = { "${it.iconEmoji} ${it.name}" },
                        onOptionSelected = {
                            selectedCategoryId = it.id
                            selectedSubCategoryId = null
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text("Seleziona Sottocategoria:".t(language), style = MaterialTheme.typography.labelLarge)
                val subCategories = categories.filter { it.parentCategoryId == selectedCategoryId }
                val subOptions = listOf(null) + subCategories
                BudgieDropdown(
                    label = "Sottocategoria".t(language),
                    options = subOptions,
                    selectedOption = categories.find { it.id == selectedSubCategoryId },
                    optionToString = { it?.name ?: "Tutte (Intera Categoria)".t(language) },
                    onOptionSelected = { selectedSubCategoryId = it?.id }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text("Periodo di Validità:".t(language), style = MaterialTheme.typography.labelLarge)
                val periodOptions = listOf("Daily", "Weekly", "Monthly")
                BudgieDropdown(
                    label = "Periodo".t(language),
                    options = periodOptions,
                    selectedOption = period,
                    optionToString = {
                        when (it) {
                            "Daily" -> "Giornaliero".t(language)
                            "Weekly" -> "Settimanale".t(language)
                            "Monthly" -> "Mensile".t(language)
                            else -> "Mensile".t(language)
                        }
                    },
                    onOptionSelected = { period = it }
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { 
                        amountStr = it 
                        errorMessage = null
                    },
                    label = { Text("Limite di Spesa (€)".t(language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { notifyOnOverflow = !notifyOnOverflow }
                        .padding(vertical = 6.dp)
                ) {
                    Checkbox(checked = notifyOnOverflow, onCheckedChange = null)
                    Text("Notifica allo sforamento".t(language), style = MaterialTheme.typography.bodyMedium)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { autoRenew = !autoRenew }
                        .padding(vertical = 6.dp)
                ) {
                    Checkbox(checked = autoRenew, onCheckedChange = null)
                    Text("Rinnova automaticamente".t(language), style = MaterialTheme.typography.bodyMedium)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isPinnedToHome = !isPinnedToHome }
                        .padding(vertical = 6.dp)
                ) {
                    Checkbox(checked = isPinnedToHome, onCheckedChange = null)
                    Text("Mostra nella Home".t(language), style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                    val effectiveCategoryId = selectedCategoryId ?: primaryCategories.firstOrNull()?.id
                    when {
                        effectiveCategoryId == null -> errorMessage = "Seleziona una categoria.".t(language)
                        amount <= 0.0 -> errorMessage = "Inserisci un limite di spesa valido maggiore di zero.".t(language)
                        else -> {
                            try {
                                viewModel.addOrUpdateBudget(
                                    categoryId = effectiveCategoryId,
                                    subCategoryId = selectedSubCategoryId,
                                    accountId = null,
                                    amountLimit = amount,
                                    period = period,
                                    notifyOnOverflow = notifyOnOverflow,
                                    autoRenew = autoRenew,
                                    isPinnedToHome = isPinnedToHome
                                )
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore durante il salvataggio: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                }
            ) {
                Text("Salva".t(language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla".t(language))
            }
        }
    )
}

// --- ADD TOTAL BUDGET DIALOG ---
@Composable
fun AddTotalBudgetDialog(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()

    var selectedAccountId by remember { mutableStateOf<Int?>(null) }
    var amountStr by remember { mutableStateOf("") }
    var notifyOnOverflow by remember { mutableStateOf(true) }
    var autoRenew by remember { mutableStateOf(true) }
    var isPinnedToHome by remember { mutableStateOf(false) }
    var period by remember { mutableStateOf("Monthly") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuovo Budget Complessivo".t(language)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Text("Seleziona Ambito:".t(language), style = MaterialTheme.typography.labelLarge)
                val scopeOptions = listOf(null) + accounts
                BudgieDropdown(
                    label = "Ambito".t(language),
                    options = scopeOptions,
                    selectedOption = accounts.find { it.id == selectedAccountId },
                    optionToString = { it?.let { "${it.type.toAccountEmoji()} ${it.name}" } ?: "🌍 ${"Tutti i Conti Insieme".t(language)}" },
                    onOptionSelected = { selectedAccountId = it?.id }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text("Periodo di Validità:".t(language), style = MaterialTheme.typography.labelLarge)
                val periodOptions = listOf("Daily", "Weekly", "Monthly")
                BudgieDropdown(
                    label = "Periodo".t(language),
                    options = periodOptions,
                    selectedOption = period,
                    optionToString = {
                        when (it) {
                            "Daily" -> "Giornaliero".t(language)
                            "Weekly" -> "Settimanale".t(language)
                            "Monthly" -> "Mensile".t(language)
                            else -> "Mensile".t(language)
                        }
                    },
                    onOptionSelected = { period = it }
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { 
                        amountStr = it 
                        errorMessage = null
                    },
                    label = { Text("Limite di Spesa (€)".t(language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { notifyOnOverflow = !notifyOnOverflow }
                        .padding(vertical = 6.dp)
                ) {
                    Checkbox(checked = notifyOnOverflow, onCheckedChange = null)
                    Text("Notifica allo sforamento".t(language), style = MaterialTheme.typography.bodyMedium)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { autoRenew = !autoRenew }
                        .padding(vertical = 6.dp)
                ) {
                    Checkbox(checked = autoRenew, onCheckedChange = null)
                    Text("Rinnova automaticamente".t(language), style = MaterialTheme.typography.bodyMedium)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isPinnedToHome = !isPinnedToHome }
                        .padding(vertical = 6.dp)
                ) {
                    Checkbox(checked = isPinnedToHome, onCheckedChange = null)
                    Text("Mostra nella Home".t(language), style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                    when {
                        amount <= 0.0 -> errorMessage = "Inserisci un limite di spesa valido maggiore di zero.".t(language)
                        else -> {
                            try {
                                viewModel.addOrUpdateBudget(
                                    categoryId = null,
                                    subCategoryId = null,
                                    accountId = selectedAccountId,
                                    amountLimit = amount,
                                    period = period,
                                    notifyOnOverflow = notifyOnOverflow,
                                    autoRenew = autoRenew,
                                    isPinnedToHome = isPinnedToHome
                                )
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore durante il salvataggio: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                }
            ) {
                Text("Salva".t(language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla".t(language))
            }
        }
    )
}

// --- EDIT BUDGET DIALOG ---
@Composable
fun EditBudgetDialog(
    budget: Budget,
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()

    var amountStr by remember { mutableStateOf(budget.amountLimit.toString()) }
    var notifyOnOverflow by remember { mutableStateOf(budget.notifyOnOverflow) }
    var autoRenew by remember { mutableStateOf(budget.autoRenew) }
    var isPinnedToHome by remember { mutableStateOf(budget.isPinnedToHome) }
    var period by remember { mutableStateOf(budget.period) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Category specific
    val parentCategories = categories.filter { it.parentCategoryId == null }
    var selectedCategoryId by remember { mutableStateOf(budget.categoryId) }
    var selectedSubCategoryId by remember { mutableStateOf(budget.subCategoryId) }

    // Account specific
    var selectedAccountId by remember { mutableStateOf(budget.accountId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifica Budget".t(language)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // If Category Budget
                if (budget.categoryId != null) {
                    Text("Categoria".t(language), style = MaterialTheme.typography.labelLarge)
                    if (parentCategories.isNotEmpty()) {
                        BudgieDropdown(
                            label = "Categoria".t(language),
                            options = parentCategories,
                            selectedOption = categories.find { it.id == selectedCategoryId } ?: parentCategories.first(),
                            optionToString = { "${it.iconEmoji} ${it.name}" },
                            onOptionSelected = {
                                selectedCategoryId = it.id
                                selectedSubCategoryId = null
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Sottocategoria".t(language), style = MaterialTheme.typography.labelLarge)
                    val subCategories = categories.filter { it.parentCategoryId == selectedCategoryId }
                    val subOptions = listOf(null) + subCategories
                    BudgieDropdown(
                        label = "Sottocategoria".t(language),
                        options = subOptions,
                        selectedOption = categories.find { it.id == selectedSubCategoryId },
                        optionToString = { it?.name ?: "Tutte (Intera Categoria)".t(language) },
                        onOptionSelected = { selectedSubCategoryId = it?.id }
                    )
                } else {
                    val scopeOptions = listOf(null) + accounts
                    Text("Ambito (Conto)".t(language), style = MaterialTheme.typography.labelLarge)
                    BudgieDropdown(
                        label = "Ambito".t(language),
                        options = scopeOptions,
                        selectedOption = accounts.find { it.id == selectedAccountId },
                        optionToString = { it?.let { "${it.type.toAccountEmoji()} ${it.name}" } ?: "🌍 ${"Tutti i Conti Insieme".t(language)}" },
                        onOptionSelected = { selectedAccountId = it?.id }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                val periodOptions = listOf("Daily", "Weekly", "Monthly")
                Text("Periodo di Validità".t(language), style = MaterialTheme.typography.labelLarge)
                BudgieDropdown(
                    label = "Periodo".t(language),
                    options = periodOptions,
                    selectedOption = period,
                    optionToString = {
                        when (it) {
                            "Daily" -> "Giornaliero".t(language)
                            "Weekly" -> "Settimanale".t(language)
                            "Monthly" -> "Mensile".t(language)
                            else -> "Mensile".t(language)
                        }
                    },
                    onOptionSelected = { period = it }
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { 
                        amountStr = it 
                        errorMessage = null
                    },
                    label = { Text("Limite di Spesa (€)".t(language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(checked = notifyOnOverflow, onCheckedChange = { notifyOnOverflow = it })
                    Text("Notifica allo sforamento".t(language))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(checked = autoRenew, onCheckedChange = { autoRenew = it })
                    Text("Rinnova automaticamente".t(language))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(checked = isPinnedToHome, onCheckedChange = { isPinnedToHome = it })
                    Text("Mostra nella Home".t(language))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.replace(',', '.').toDoubleOrNull() ?: budget.amountLimit
                    when {
                        amount <= 0.0 -> errorMessage = "Inserisci un limite di spesa valido maggiore di zero.".t(language)
                        else -> {
                            try {
                                viewModel.updateBudget(
                                    budget.copy(
                                        categoryId = selectedCategoryId,
                                        subCategoryId = selectedSubCategoryId,
                                        accountId = selectedAccountId,
                                        amountLimit = amount,
                                        period = period,
                                        notifyOnOverflow = notifyOnOverflow,
                                        autoRenew = autoRenew,
                                        isPinnedToHome = isPinnedToHome
                                    )
                                )
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore durante il salvataggio: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                }
            ) {
                Text("Salva Modifiche".t(language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla".t(language))
            }
        }
    )
}

// --- BUDGIE CUSTOM DROPDOWN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> BudgieDropdown(
    label: String,
    options: List<T>,
    selectedOption: T,
    optionToString: (T) -> String,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = optionToString(selectedOption),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionToString(option)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// --- ADD CATEGORY SUBCATEGORY DIALOG ---
@Composable
fun AddCategorySubcategoryDialog(
    parentCategory: Category,
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("🔹") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isEmojiValid = isValidSingleEmoji(emoji)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${"Nuova Sottocategoria per".t(language)} ${parentCategory.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        errorMessage = null
                    },
                    label = { Text("Nome Sottocategoria".t(language)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = emoji,
                    onValueChange = { 
                        emoji = it
                        errorMessage = null
                    },
                    label = { Text("Emoji Icona".t(language)) },
                    isError = !isEmojiValid,
                    supportingText = {
                        if (!isEmojiValid) {
                            Text("Inserisci un singolo carattere emoji valido".t(language), color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("Carattere emoji valido".t(language))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        name.isBlank() -> errorMessage = "Inserisci un nome per la sottocategoria.".t(language)
                        !isEmojiValid -> errorMessage = "Inserisci un'emoji valida.".t(language)
                        else -> {
                            try {
                                viewModel.addCategory(name, emoji, parentCategory.id, parentCategory.type)
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore durante la creazione: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                },
                enabled = name.isNotBlank() && isEmojiValid
            ) {
                Text("Crea".t(language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla".t(language))
            }
        }
    )
}

// --- EDIT CATEGORY DIALOG ---
@Composable
fun EditCategoryDialog(
    category: Category,
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf(category.name) }
    var emoji by remember { mutableStateOf(category.iconEmoji) }

    val isEmojiValid = isValidSingleEmoji(emoji)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category.parentCategoryId == null) "Modifica Categoria".t(language) else "Modifica Sottocategoria".t(language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome".t(language)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = emoji,
                    onValueChange = { emoji = it },
                    label = { Text("Emoji".t(language)) },
                    isError = !isEmojiValid,
                    supportingText = {
                        if (!isEmojiValid) {
                            Text("Inserisci un singolo carattere emoji valido".t(language), color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("Carattere emoji valido".t(language))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && isEmojiValid) {
                        viewModel.updateCategory(category.copy(name = name, iconEmoji = emoji))
                        onDismiss()
                    }
                },
                enabled = name.isNotEmpty() && isEmojiValid
            ) {
                Text("Salva".t(language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla".t(language))
            }
        }
    )
}


@Composable
fun PlanningScreen(
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val plannedTransactions by viewModel.plannedTransactions.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()

    var showAddPlannedDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero card summary or explanation
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Pianificazione".t(language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Imposta transazioni future o ricorrenti. Budgie le registrerà automaticamente.".t(language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = "Transazioni Pianificate".t(language),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (plannedTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🦜", fontSize = 48.sp)
                        Text(
                            text = "Nessuna transazione pianificata.".t(language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(plannedTransactions) { planned ->
                        var expanded by remember { mutableStateOf(false) }
                        
                        val sourceAcc = accounts.find { it.id == planned.sourceAccountId }?.name ?: "Conto sconosciuto"
                        val destAcc = if (planned.type == "Transfer") {
                            accounts.find { it.id == planned.destinationAccountId }?.name ?: "Conto sconosciuto"
                        } else null
                        
                        val tempCategoryObj = categories.find { it.id == planned.categoryId }
                        val categoryObj = if (tempCategoryObj?.parentCategoryId != null) {
                            categories.find { it.id == tempCategoryObj.parentCategoryId }
                        } else {
                            tempCategoryObj
                        }
                        val categoryName = categoryObj?.name

                        val subCategoryObj = if (tempCategoryObj?.parentCategoryId != null) {
                            tempCategoryObj
                        } else {
                            categories.find { it.id == planned.subCategoryId }
                        }
                        val subCategoryName = if (subCategoryObj != null && subCategoryObj.id != categoryObj?.id) {
                            subCategoryObj.name
                        } else {
                            null
                        }
                        
                        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ITALIAN)
                        val startDateStr = sdf.format(planned.startDate)
                        
                        val nextExecDate = if (planned.isRecurring) {
                            val nextTs = viewModel.calculateNextScheduledDate(
                                planned.startDate,
                                planned.lastExecutedDate,
                                planned.frequencyInterval ?: 1,
                                planned.frequencyUnit ?: "giorni"
                            )
                            sdf.format(nextTs)
                        } else {
                            if (planned.isActive) startDateStr else "Eseguito".t(language)
                        }

                        val emojiIcon = subCategoryObj?.iconEmoji
                            ?: categoryObj?.iconEmoji
                            ?: when (planned.type) {
                                "Income" -> "📈"
                                "Expense" -> "📉"
                                else -> "🔄"
                            }

                        val itemColor = when (planned.type) {
                            "Income" -> Color(0xFF2E7D32)
                            "Expense" -> Color(0xFFD32F2F)
                            else -> Color(0xFF1976D2)
                        }
                        val bgColor = itemColor.copy(alpha = 0.08f)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("planned_item_${planned.id}"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (planned.isActive) {
                                    MaterialTheme.colorScheme.surface
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                }
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = itemColor.copy(alpha = 0.25f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min),
                                verticalAlignment = Alignment.CenterVertically
                              ) {
                                // Left Accent Bar for visual type distinction (Income=Green, Expense=Red, Transfer=Blue)
                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .fillMaxHeight()
                                        .background(itemColor)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    // Header Row (Always visible and clickable to expand/collapse)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { expanded = !expanded }
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .clip(CircleShape)
                                                    .background(bgColor),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = emojiIcon,
                                                    fontSize = 20.sp
                                                )
                                            }
                                            
                                            Column {
                                                Text(
                                                    text = if (planned.title.isNotBlank()) {
                                                        planned.title
                                                    } else {
                                                        when (planned.type) {
                                                            "Income" -> "Entrata".t(language)
                                                            "Expense" -> "Spesa".t(language)
                                                            else -> "Giroconto".t(language)
                                                        }
                                                    },
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                                )
                                                if (planned.isRecurring) {
                                                    Text(
                                                        text = "${"Ogni".t(language)} ${planned.frequencyInterval} ${planned.frequencyUnit.toString().t(language)}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.Gray
                                                    )
                                                } else {
                                                    Text(
                                                        text = "Programmato".t(language),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.Gray
                                                    )
                                                }
                                            }
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = planned.amount.formatEuro(),
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = when (planned.type) {
                                                    "Income" -> Color(0xFF006D43)
                                                    "Expense" -> Color(0xFFBA1A1A)
                                                    else -> MaterialTheme.colorScheme.primary
                                                }
                                            )
                                            
                                            if (expanded) {
                                                IconButton(
                                                    onClick = { viewModel.deletePlannedTransaction(planned) }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Elimina".t(language),
                                                        tint = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            }
                                            
                                            Icon(
                                                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = if (expanded) "Collassa".t(language) else "Espandi".t(language),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                            )
                                        }
                                    }

                                    // Expanded details section
                                    if (expanded) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                        ) {
                                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                                            Spacer(modifier = Modifier.height(12.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(
                                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(
                                                        text = "${"Conto sorgente:".t(language)} $sourceAcc",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    if (destAcc != null) {
                                                        Text(
                                                            text = "${"Conto destinazione:".t(language)} $destAcc",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                    if (categoryName != null) {
                                                        Column(
                                                            verticalArrangement = Arrangement.spacedBy(4.dp),
                                                            modifier = Modifier.padding(top = 4.dp)
                                                        ) {
                                                            // Category Badge
                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(8.dp))
                                                                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                                            ) {
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                                ) {
                                                                    Text(categoryObj?.iconEmoji ?: "", fontSize = 12.sp)
                                                                    Text(
                                                                        text = categoryName,
                                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                                                    )
                                                                }
                                                            }

                                                            // Subcategory Badge if exists
                                                            if (subCategoryName != null) {
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                                    modifier = Modifier.padding(start = 8.dp)
                                                                ) {
                                                                    Text(
                                                                        text = "↳",
                                                                        style = MaterialTheme.typography.labelSmall,
                                                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                                                    )
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .clip(RoundedCornerShape(8.dp))
                                                                            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f))
                                                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                                                    ) {
                                                                        Row(
                                                                            verticalAlignment = Alignment.CenterVertically,
                                                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                                        ) {
                                                                            Text(subCategoryObj?.iconEmoji ?: "", fontSize = 12.sp)
                                                                            Text(
                                                                                text = subCategoryName,
                                                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                                                color = MaterialTheme.colorScheme.onTertiaryContainer
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                Column(
                                                    horizontalAlignment = Alignment.End,
                                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    Text(
                                                        text = "${"Data inizio:".t(language)} $startDateStr",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.Gray
                                                    )
                                                    Text(
                                                        text = if (planned.isActive) {
                                                            "${"Prossimo:".t(language)} $nextExecDate"
                                                        } else {
                                                            "Eseguito/Inattivo".t(language)
                                                        },
                                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                        color = if (planned.isActive) MaterialTheme.colorScheme.primary else Color.Gray
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB to add new planned transaction
        FloatingActionButton(
            onClick = { showAddPlannedDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_planned_btn"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Aggiungi Pianificazione".t(language))
        }
    }

    if (showAddPlannedDialog) {
        AddPlannedTransactionDialog(
            viewModel = viewModel,
            onDismiss = { showAddPlannedDialog = false }
        )
    }
}

@Composable
fun AddPlannedTransactionDialog(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    var step by remember { mutableStateOf(1) } // Step 1 or Step 2
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Expense") } // "Expense", "Income", "Transfer"
    var mode by remember { mutableStateOf("Ricorrente") } // "Ricorrente", "Programmato"
    var frequencyIntervalStr by remember { mutableStateOf("1") }
    var frequencyUnit by remember { mutableStateOf("giorni") } // "giorni", "settimane", "mesi"
    
    // Default to tomorrow at the beginning of the day (00:00:00.000)
    val todayCal = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    val tomorrowTime = todayCal.timeInMillis + 24 * 60 * 60 * 1000
    
    val defaultCalendar = java.util.Calendar.getInstance().apply {
        timeInMillis = tomorrowTime
    }
    var startDate by remember { mutableStateOf(defaultCalendar.timeInMillis) }
    var amountStr by remember { mutableStateOf("") }
    
    var sourceAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 0) }
    var destinationAccountId by remember { 
        mutableStateOf(accounts.filter { it.id != sourceAccountId }.firstOrNull()?.id) 
    }
    
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var selectedSubCategoryId by remember { mutableStateOf<Int?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    // If destination is same as source, change it
    LaunchedEffect(sourceAccountId) {
        if (destinationAccountId == sourceAccountId) {
            destinationAccountId = accounts.filter { it.id != sourceAccountId }.firstOrNull()?.id
        }
    }

    // Set default category when type or category changes
    LaunchedEffect(type) {
        val mainCats = categories.filter { it.parentCategoryId == null && it.type == type }
        selectedCategoryId = mainCats.firstOrNull()?.id
        selectedSubCategoryId = null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pianifica".t(language), fontWeight = FontWeight.Bold)
                Text(
                    text = if (step == 1) "Step 1 di 2".t(language) else "Step 2 di 2".t(language),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (validationError != null) {
                    Text(
                        text = validationError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                if (step == 1) {
                    // STEP 1 CONTENT:
                    // 1. Nome della pianificazione
                    OutlinedTextField(
                        value = title,
                        onValueChange = { if (it.length <= 22 && !it.contains("\n")) title = it },
                        label = { Text("Nome Pianificazione".t(language)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        maxLines = 1
                    )

                    // 2. Tipo Trasferimento (Spesa / Entrata / Giroconto)
                    Text("Tipo:".t(language), style = MaterialTheme.typography.labelLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Expense" to "Spesa", "Income" to "Entrata", "Transfer" to "Giroconto").forEach { (tCode, tLabel) ->
                            if (tCode != "Transfer" || accounts.size > 1) {
                                val isSelected = type == tCode
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary 
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        )
                                        .clickable { type = tCode }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tLabel.t(language),
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // 3. Modalità (Ricorrente / Programmato)
                    Text("Modalità:".t(language), style = MaterialTheme.typography.labelLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Ricorrente", "Programmato").forEach { mLabel ->
                            val isSelected = mode == mLabel
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary 
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .clickable { mode = mLabel }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mLabel.t(language),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Se Ricorrente, chiedi frequenza
                    if (mode == "Ricorrente") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Ogni".t(language), style = MaterialTheme.typography.bodyMedium)
                            OutlinedTextField(
                                value = frequencyIntervalStr,
                                onValueChange = { frequencyIntervalStr = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.width(60.dp),
                                singleLine = true
                            )
                            Box(modifier = Modifier.weight(1f)) {
                                BudgieDropdown(
                                    label = "Unità".t(language),
                                    options = listOf("giorni", "settimane", "mesi"),
                                    selectedOption = frequencyUnit,
                                    optionToString = { it.t(language) },
                                    onOptionSelected = { frequencyUnit = it }
                                )
                            }
                        }
                    }

                    // Data d'inizio / programmata
                    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ITALIAN)
                    val dateLabel = if (mode == "Ricorrente") "Da quale data iniziare".t(language) else "Data transazione".t(language)
                    
                    OutlinedTextField(
                        value = sdf.format(startDate),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(dateLabel) },
                        trailingIcon = {
                            IconButton(onClick = {
                                val picker = android.app.DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val calendarPick = java.util.Calendar.getInstance().apply {
                                            set(java.util.Calendar.YEAR, year)
                                            set(java.util.Calendar.MONTH, month)
                                            set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth)
                                            set(java.util.Calendar.HOUR_OF_DAY, 0)
                                            set(java.util.Calendar.MINUTE, 0)
                                            set(java.util.Calendar.SECOND, 0)
                                            set(java.util.Calendar.MILLISECOND, 0)
                                        }
                                        startDate = calendarPick.timeInMillis
                                    },
                                    defaultCalendar.get(java.util.Calendar.YEAR),
                                    defaultCalendar.get(java.util.Calendar.MONTH),
                                    defaultCalendar.get(java.util.Calendar.DAY_OF_MONTH)
                                )
                                picker.datePicker.minDate = tomorrowTime
                                picker.show()
                            }) {
                                Icon(Icons.Default.DateRange, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                } else {
                    // STEP 2 CONTENT:
                    // 1. Importo
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Importo (€)".t(language)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // 2. Account Sorgente
                    Text("Conto sorgente:".t(language), style = MaterialTheme.typography.labelLarge)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        accounts.forEach { acc ->
                            FilterChip(
                                selected = sourceAccountId == acc.id,
                                onClick = { sourceAccountId = acc.id },
                                label = { Text(acc.name) },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    // 3. Account Destinazione (Solo per Giroconto)
                    if (type == "Transfer") {
                        Text("Conto destinazione:".t(language), style = MaterialTheme.typography.labelLarge)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            accounts.filter { it.id != sourceAccountId }.forEach { acc ->
                                FilterChip(
                                    selected = destinationAccountId == acc.id,
                                    onClick = { destinationAccountId = acc.id },
                                    label = { Text(acc.name) },
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }

                    // 4. Categoria e Sottocategoria (Solo per Spesa e Entrata)
                    if (type != "Transfer") {
                        val mainCats = categories.filter { it.parentCategoryId == null && it.type == type }
                        if (mainCats.isNotEmpty()) {
                            Text("Categoria:".t(language), style = MaterialTheme.typography.labelLarge)
                            BudgieDropdown(
                                label = "Seleziona Categoria".t(language),
                                options = mainCats,
                                selectedOption = categories.find { it.id == selectedCategoryId } ?: mainCats.first(),
                                optionToString = { "${it.iconEmoji} ${it.name}" },
                                onOptionSelected = { cat ->
                                    selectedCategoryId = cat.id
                                    selectedSubCategoryId = null
                                }
                            )

                            val currentCatId = selectedCategoryId ?: mainCats.first().id
                            val subCats = categories.filter { it.parentCategoryId == currentCatId }
                            if (subCats.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Sottocategoria (Opzionale):".t(language), style = MaterialTheme.typography.labelLarge)
                                val subOptions = listOf(null) + subCats
                                BudgieDropdown(
                                    label = "Seleziona Sottocategoria".t(language),
                                    options = subOptions,
                                    selectedOption = categories.find { it.id == selectedSubCategoryId },
                                    optionToString = { it?.let { "${it.iconEmoji} ${it.name}" } ?: "Nessuna sottocategoria".t(language) },
                                    onOptionSelected = { subCat ->
                                        selectedSubCategoryId = subCat?.id
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (step == 1) {
                Button(
                    onClick = {
                        val interval = frequencyIntervalStr.toIntOrNull() ?: 0
                        
                        if (title.isBlank()) {
                            validationError = "Errore: inserisci un nome per la pianificazione.".t(language)
                        } else if (startDate < tomorrowTime) {
                            validationError = "Errore: la data deve essere futura.".t(language)
                        } else if (mode == "Ricorrente" && interval <= 0) {
                            validationError = "Errore: inserisci un intervallo valido per la ricorrenza.".t(language)
                        } else {
                            validationError = null
                            step = 2
                        }
                    }
                ) {
                    Text("Avanti".t(language))
                }
            } else {
                Button(
                    onClick = {
                        val amount = amountStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                        val interval = frequencyIntervalStr.toIntOrNull() ?: 0
                        
                        if (amount <= 0.0) {
                            validationError = "Errore: inserisci un importo valido.".t(language)
                        } else if (type == "Transfer" && destinationAccountId == null) {
                            validationError = "Errore: seleziona un conto di destinazione.".t(language)
                        } else {
                            // All checks passed! Save!
                            val mainCats = categories.filter { it.parentCategoryId == null && it.type == type }
                            val catId = if (type == "Transfer") null else {
                                selectedCategoryId ?: mainCats.firstOrNull()?.id
                            }
                            
                            val newPlanned = PlannedTransaction(
                                title = title,
                                type = type,
                                isRecurring = (mode == "Ricorrente"),
                                frequencyInterval = if (mode == "Ricorrente") interval else null,
                                frequencyUnit = if (mode == "Ricorrente") frequencyUnit else null,
                                startDate = startDate,
                                amount = amount,
                                categoryId = catId,
                                subCategoryId = if (type == "Transfer") null else selectedSubCategoryId,
                                sourceAccountId = sourceAccountId,
                                destinationAccountId = if (type == "Transfer") destinationAccountId else null
                            )
                            
                            viewModel.addPlannedTransaction(newPlanned)
                            onDismiss()
                        }
                    }
                ) {
                    Text("Conferma".t(language))
                }
            }
        },
        dismissButton = {
            if (step == 1) {
                TextButton(onClick = onDismiss) {
                    Text("Annulla".t(language))
                }
            } else {
                TextButton(onClick = { step = 1 }) {
                    Text("Indietro".t(language))
                }
            }
        }
    )
}

@Composable
fun ExportScreen(
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()

    // Default Start Date: first day of current month
    val defaultStartDate = remember {
        java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.DAY_OF_MONTH, 1)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    
    // Default End Date: last day of current month
    val defaultEndDate = remember {
        java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.DAY_OF_MONTH, getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
            set(java.util.Calendar.HOUR_OF_DAY, 23)
            set(java.util.Calendar.MINUTE, 59)
            set(java.util.Calendar.SECOND, 59)
            set(java.util.Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    var startDate by remember { mutableStateOf(defaultStartDate) }
    var endDate by remember { mutableStateOf(defaultEndDate) }

    val sdfDisplay = remember { java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ITALIAN) }

    // Count matching transactions
    val matchingRealTxs = remember(transactions, startDate, endDate) {
        transactions.filter {
            it.timestamp in startDate..endDate &&
            it.type != "VirtualSaving" &&
            it.type != "VirtualWithdrawal"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Card header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("📤", fontSize = 32.sp)
                Column {
                    Text(
                        text = "Esporta Transazioni".t(language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Genera un report in formato CSV compatibile con Excel e software di contabilità.".t(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }

        Text(
            text = "PERIODO DI ESPORTAZIONE".t(language),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.outline
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Start Date picker button
            OutlinedButton(
                onClick = {
                    val calTemp = java.util.Calendar.getInstance().apply { timeInMillis = startDate }
                    android.app.DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calTemp.set(java.util.Calendar.YEAR, year)
                            calTemp.set(java.util.Calendar.MONTH, month)
                            calTemp.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth)
                            calTemp.set(java.util.Calendar.HOUR_OF_DAY, 0)
                            calTemp.set(java.util.Calendar.MINUTE, 0)
                            calTemp.set(java.util.Calendar.SECOND, 0)
                            startDate = calTemp.timeInMillis
                        },
                        calTemp.get(java.util.Calendar.YEAR),
                        calTemp.get(java.util.Calendar.MONTH),
                        calTemp.get(java.util.Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Da:".t(language), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(sdfDisplay.format(java.util.Date(startDate)), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            }

            // End Date picker button
            OutlinedButton(
                onClick = {
                    val calTemp = java.util.Calendar.getInstance().apply { timeInMillis = endDate }
                    android.app.DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calTemp.set(java.util.Calendar.YEAR, year)
                            calTemp.set(java.util.Calendar.MONTH, month)
                            calTemp.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth)
                            calTemp.set(java.util.Calendar.HOUR_OF_DAY, 23)
                            calTemp.set(java.util.Calendar.MINUTE, 59)
                            calTemp.set(java.util.Calendar.SECOND, 59)
                            endDate = calTemp.timeInMillis
                        },
                        calTemp.get(java.util.Calendar.YEAR),
                        calTemp.get(java.util.Calendar.MONTH),
                        calTemp.get(java.util.Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("A:".t(language), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(sdfDisplay.format(java.util.Date(endDate)), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Preview count
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${matchingRealTxs.size}",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Transazioni reali trovate nel periodo".t(language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "(I risparmi e gli accantonamenti virtuali degli obiettivi sono automaticamente esclusi dall'esportazione)".t(language),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Export Actions
        Button(
            onClick = {
                exportTransactionsToCSV(
                    context = context,
                    transactionsList = transactions,
                    categoriesList = categories,
                    accountsList = accounts,
                    startDate = startDate,
                    endDate = endDate,
                    shareAsEmail = false,
                    language = language
                )
            },
            enabled = matchingRealTxs.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Esporta e Salva su Disco".t(language))
        }

        Button(
            onClick = {
                exportTransactionsToCSV(
                    context = context,
                    transactionsList = transactions,
                    categoriesList = categories,
                    accountsList = accounts,
                    startDate = startDate,
                    endDate = endDate,
                    shareAsEmail = true,
                    language = language
                )
            },
            enabled = matchingRealTxs.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Default.Email, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Invia via Email come Allegato".t(language))
        }
    }
}

fun exportTransactionsToCSV(
    context: android.content.Context,
    transactionsList: List<Transaction>,
    categoriesList: List<Category>,
    accountsList: List<Account>,
    startDate: Long,
    endDate: Long,
    shareAsEmail: Boolean,
    language: String = "en"
) {
    val realTxs = transactionsList.filter {
        it.timestamp in startDate..endDate &&
        it.type != "VirtualSaving" &&
        it.type != "VirtualWithdrawal"
    }.sortedByDescending { it.timestamp }

    val sdfDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.ITALIAN)
    val csvHeader = when (language) {
        "es" -> "Fecha,Tipo,Importe,Título/Nota,Categoría,Subcategoría,Cuenta origen,Cuenta destino\n"
        "ca" -> "Data,Tipus,Import,Títol/Nota,Categoria,Subcategoria,Compte origen,Compte destí\n"
        "fr" -> "Date,Type,Montant,Titre/Note,Catégorie,Sous-catégorie,Compte source,Compte destination\n"
        "de" -> "Datum,Typ,Betrag,Titel/Notiz,Kategorie,Unterkategorie,Herkunftskonto,Zielkonto\n"
        else -> "Date,Type,Amount,Title/Note,Category,Subcategory,Source Account,Destination Account\n"
    }
    val csvBody = java.lang.StringBuilder()
    
    realTxs.forEach { tx ->
        val dateStr = sdfDate.format(java.util.Date(tx.timestamp))
        val typeStr = when (tx.type) {
            "Expense" -> when (language) {
                "es" -> "Gasto"
                "ca" -> "Despesa"
                "fr" -> "Dépense"
                "de" -> "Ausgabe"
                else -> "Expense"
            }
            "Income" -> when (language) {
                "es" -> "Ingreso"
                "ca" -> "Ingrés"
                "fr" -> "Revenu"
                "de" -> "Einnahme"
                else -> "Income"
            }
            "Transfer" -> when (language) {
                "es" -> "Transferencia"
                "ca" -> "Transferència"
                "fr" -> "Transfert"
                "de" -> "Überweisung"
                else -> "Transfer"
            }
            else -> tx.type
        }
        val amountStr = tx.amount.toString()
        val titleStr = tx.title.replace(",", " ").replace("\n", " ")
        
        val catObj = categoriesList.find { it.id == tx.categoryId }
        var catName = ""
        var subCatName = ""
        if (catObj != null) {
            if (catObj.parentCategoryId != null) {
                subCatName = catObj.name
                val parentCat = categoriesList.find { it.id == catObj.parentCategoryId }
                catName = parentCat?.name ?: ""
            } else {
                catName = catObj.name
            }
        }
        
        val sourceAcc = accountsList.find { it.id == tx.sourceAccountId }?.name ?: ""
        val destAcc = if (tx.type == "Transfer" && tx.destinationAccountId != null) {
            accountsList.find { it.id == tx.destinationAccountId }?.name ?: ""
        } else ""

        csvBody.append("\"$dateStr\",\"$typeStr\",$amountStr,\"$titleStr\",\"$catName\",\"$subCatName\",\"$sourceAcc\",\"$destAcc\"\n")
    }

    val csvContent = csvHeader + csvBody.toString()

    val cacheDir = context.cacheDir
    val sdfRange = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.ITALIAN)
    val fromStr = sdfRange.format(java.util.Date(startDate))
    val toStr = sdfRange.format(java.util.Date(endDate))
    val fileName = "budgie_export_${fromStr}_to_${toStr}.csv"
    val file = java.io.File(cacheDir, fileName)
    file.writeText(csvContent, Charsets.UTF_8)

    val uri = try {
        androidx.core.content.FileProvider.getUriForFile(context, "com.example.fileprovider", file)
    } catch (e: Exception) {
        null
    }

    if (uri == null) return

    if (shareAsEmail) {
        val subjectStr = when (language) {
            "es" -> "Budgie - Exportación de transacciones de $fromStr a $toStr"
            "ca" -> "Budgie - Exportació de transaccions de $fromStr a $toStr"
            "fr" -> "Budgie - Exportation des transactions de $fromStr à $toStr"
            "de" -> "Budgie - Transaktionsexport von $fromStr bis $toStr"
            else -> "Budgie - Transaction export from $fromStr to $toStr"
        }
        val bodyStr = when (language) {
            "es" -> "Adjunto encontrarás el informe de transacciones desde $fromStr hasta $toStr exportado de la aplicación Budgie."
            "ca" -> "Adjunt trobaràs l'informe de transaccions des de $fromStr fins a $toStr exportat de l'aplicació Budgie."
            "fr" -> "Veuillez trouver ci-joint le rapport des transactions du $fromStr au $toStr exporté de l'application Budgie."
            "de" -> "Anbei finden Sie den Transaktionsbericht vom $fromStr bis $toStr, der aus der Budgie-App exportiert wurde."
            else -> "Attached you will find the transaction report from $fromStr to $toStr exported from the Budgie app."
        }
        val chooserTitle = when (language) {
            "es" -> "Enviar correo con exportación"
            "ca" -> "Enviar correu amb exportació"
            "fr" -> "Envoyer l'e-mail avec l'exportation"
            "de" -> "E-Mail mit Export senden"
            else -> "Send Email with Export"
        }

        val emailIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(android.content.Intent.EXTRA_SUBJECT, subjectStr)
            putExtra(android.content.Intent.EXTRA_TEXT, bodyStr)
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(android.content.Intent.createChooser(emailIntent, chooserTitle))
    } else {
        val chooserTitle = when (language) {
            "es" -> "Exportar y guardar transacciones"
            "ca" -> "Exportar i desar transaccions"
            "fr" -> "Exporter et sauvegarder les transactions"
            "de" -> "Transaktionen exportieren und speichern"
            else -> "Export and Save Transactions"
        }

        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(android.content.Intent.createChooser(shareIntent, chooserTitle))
    }
}
