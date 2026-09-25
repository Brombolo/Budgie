package com.example.ui.screens

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.drawWithContent
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
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.*
import com.example.ui.translations.TranslationProvider
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round


@Composable
fun ResponsiveText(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = 1,
    targetFontSize: androidx.compose.ui.unit.TextUnit = style.fontSize
) {
    var resizedFontSize by remember(text, targetFontSize) { mutableStateOf(targetFontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        textAlign = textAlign,
        fontSize = resizedFontSize,
        softWrap = false,
        maxLines = maxLines,
        style = style,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow && resizedFontSize > 8.sp) {
                resizedFontSize = (resizedFontSize.value * 0.95f).sp
            } else {
                readyToDraw = true
            }
        }
    )
}

@Composable
fun ResponsiveTextAnnotated(
    text: AnnotatedString,
    style: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = 1,
    targetFontSize: androidx.compose.ui.unit.TextUnit = style.fontSize
) {
    var resizedFontSize by remember(text, targetFontSize) { mutableStateOf(targetFontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        textAlign = textAlign,
        fontSize = resizedFontSize,
        softWrap = false,
        maxLines = maxLines,
        style = style,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow && resizedFontSize > 8.sp) {
                resizedFontSize = (resizedFontSize.value * 0.95f).sp
            } else {
                readyToDraw = true
            }
        }
    )
}

@Composable
fun NoteTextFieldWithAutocomplete(
    value: String,
    onValueChange: (String) -> Unit,
    wordFrequencies: Map<String, Int>,
    label: String,
    language: String,
    modifier: Modifier = Modifier,
    maxChars: Int = 22,
    singleLine: Boolean = true
) {
    // Current active word prefix being typed
    val currentWordPrefix = remember(value) {
        val lastSpaceIndex = value.lastIndexOfAny(charArrayOf(' ', ',', '.', ';', ':', '!', '?', '-'))
        if (lastSpaceIndex == -1) value else value.substring(lastSpaceIndex + 1)
    }

    val candidateWords = remember(currentWordPrefix, wordFrequencies) {
        if (currentWordPrefix.isBlank()) {
            emptyList()
        } else {
            wordFrequencies.entries
                .filter { (word, _) ->
                    word.startsWith(currentWordPrefix, ignoreCase = true) &&
                    !word.equals(currentWordPrefix, ignoreCase = true)
                }
                .sortedWith(
                    compareByDescending<Map.Entry<String, Int>> { it.value }
                        .thenBy { it.key.length }
                        .thenBy { it.key.lowercase() }
                )
                .map { it.key }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val availableWidthDp = this.maxWidth.value
        // Reserve width for "💡" icon (28dp) + margins
        val widthBudgetDp = (availableWidthDp - 32f).coerceAtLeast(80f)

        val fittingSuggestions = remember(candidateWords, widthBudgetDp) {
            var usedWidth = 0f
            val result = mutableListOf<String>()
            for (word in candidateWords) {
                // Estimate chip width: padding (20dp) + approx 7.5dp per character
                val estimatedChipWidth = 20f + (word.length * 7.5f)
                val spacing = if (result.isNotEmpty()) 6f else 0f
                if (usedWidth + estimatedChipWidth + spacing <= widthBudgetDp && result.size < 4) {
                    result.add(word)
                    usedWidth += estimatedChipWidth + spacing
                } else {
                    break // Exceeds available width in single row, stop adding
                }
            }
            result
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    if (newValue.length <= maxChars && !newValue.contains("\n")) {
                        onValueChange(newValue)
                    }
                },
                label = { Text(label) },
                singleLine = singleLine,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            if (fittingSuggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💡",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    fittingSuggestions.forEach { suggestion ->
                        SuggestionChip(
                            onClick = {
                                val lastSpaceIndex = value.lastIndexOfAny(charArrayOf(' ', ',', '.', ';', ':', '!', '?', '-'))
                                val prefixText = if (lastSpaceIndex == -1) "" else value.substring(0, lastSpaceIndex + 1)
                                val completedText = prefixText + suggestion + " "
                                if (completedText.length <= maxChars) {
                                    onValueChange(completedText)
                                } else {
                                    onValueChange((prefixText + suggestion).take(maxChars))
                                }
                            },
                            label = {
                                Text(
                                    text = suggestion,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = null
                        )
                    }
                }
            }
        }
    }
}

fun String.t(language: String): String {
    return TranslationProvider.translate(this, language)
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

fun Double.formatForInput(): String = String.format(Locale.US, "%.2f", this)

fun getPeriodColor(period: String): Color {
    return when (period) {
        "Daily" -> Color(0xFF00897B)   // Teal / Cyan
        "Weekly" -> Color(0xFFE65100)  // Amber / Orange
        "Monthly" -> Color(0xFF3F51B5) // Indigo / Purple
        else -> Color(0xFF3F51B5)
    }
}

fun getAccountEmoji(account: Account?): String {
    if (account == null) return "🌍"
    val t = account.type.trim()
    return if (t.isNotBlank() && isValidSingleEmoji(t)) {
        t
    } else {
        when (t.lowercase(Locale.ROOT)) {
            "bank", "conto corrente", "banca" -> "🏦"
            "card", "carta", "carta di credito" -> "💳"
            "cash", "contanti", "contante" -> "💵"
            "savings", "risparmi", "salvadanaio" -> "💰"
            else -> if (t.isNotBlank()) t else "💳"
        }
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

                    // Balance text (disposable) - Punto 9 (Responsive font)
                    ResponsiveTextAnnotated(
                        text = disposableBalance.formatEuroAnnotated(),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1.5).sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
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
            
            val account = if (pinnedBudget.accountId != null) accounts.find { it.id == pinnedBudget.accountId } else null
            val cat = if (pinnedBudget.categoryId != null) categories.find { it.id == pinnedBudget.categoryId } else null
            val sub = if (pinnedBudget.subCategoryId != null) categories.find { it.id == pinnedBudget.subCategoryId } else null

            val (emojiSymbol, displayName) = if (pinnedBudget.categoryId != null) {
                val emoji = sub?.iconEmoji ?: cat?.iconEmoji ?: "🏷️"
                val name = if (cat != null && sub != null) {
                    "${cat.name} > ${sub.name}"
                } else {
                    cat?.name ?: "Categoria".t(language)
                }
                Pair(emoji, name)
            } else {
                val emoji = if (account != null) account.type.toAccountEmoji() else "🌍"
                val name = if (account != null) account.name else "Tutti i Conti".t(language)
                Pair(emoji, name)
            }

            val periodLabel = when (pinnedBudget.period) {
                "Daily" -> "Giornaliero".t(language)
                "Weekly" -> "Settimanale".t(language)
                "Monthly" -> "Mensile".t(language)
                else -> "Mensile".t(language)
            }
            val periodColor = getPeriodColor(pinnedBudget.period)

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
                                .background(periodColor.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = periodLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = periodColor
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "$emojiSymbol $displayName",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f, fill = false)
                        )
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

                        val timeRangeCode by viewModel.selectedTimeRange.collectAsStateWithLifecycle()
                        val trendPeriod = when (timeRangeCode) {
                            "Week" -> "Settimana"
                            "Month" -> "Mese"
                            "Year" -> "Anno"
                            else -> "Settimana"
                        }

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
                                        .clickable {
                                            val code = when (period) {
                                                "Settimana" -> "Week"
                                                "Mese" -> "Month"
                                                "Anno" -> "Year"
                                                else -> "Week"
                                            }
                                            viewModel.setTimeRange(code)
                                        }
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

                            val locale = remember(language) {
                                when (language) {
                                    "English" -> Locale.ENGLISH
                                    "Español" -> Locale("es")
                                    "Català" -> Locale("ca")
                                    "Français" -> Locale.FRENCH
                                    "Deutsch" -> Locale.GERMAN
                                    else -> Locale.ITALIAN
                                }
                            }

                            val isNotMonday = !startOfWeek.equals("Lunedì", ignoreCase = true) && !startOfWeek.equals("Monday", ignoreCase = true)
                            if (trendPeriod == "Settimana" && isNotMonday) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${"Inizio settimana impostato come:".t(language)} ${startOfWeek.t(language)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            } else if (trendPeriod == "Mese" && finMonthDay != 1) {
                                val monthStartDateStr = remember(bounds.first, locale) {
                                    SimpleDateFormat("d MMMM yyyy", locale).format(Date(bounds.first))
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${"Inizio mese fiscale corrente:".t(language)} $monthStartDateStr",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 2.dp)
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
    // Inizializzato sempre al mese solare corrente
    var selectedMonthCalendar by remember { mutableStateOf(Calendar.getInstance()) }

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

    // Limiti del mese solare selezionato (dal 1° del mese alle 00:00:00 all'ultimo giorno del mese alle 23:59:59.999)
    val (startLimit, endLimit) = remember(selectedMonthCalendar) {
        val startCal = (selectedMonthCalendar.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = (startCal.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
        }
        Pair(startCal.timeInMillis, endCal.timeInMillis - 1)
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

                val txCal = java.util.Calendar.getInstance().apply { timeInMillis = tx.timestamp }

                val sdfDay = SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN)
                val txDateStr = sdfDay.format(Date(tx.timestamp))

                // Formato senza zero iniziale per facilitare ricerche come "5/9/2026" o "9/2026"
                val txDateStrNoZero = "${txCal.get(Calendar.DAY_OF_MONTH)}/${txCal.get(Calendar.MONTH) + 1}/${txCal.get(Calendar.YEAR)}"

                tx.title.contains(searchQuery, ignoreCase = true) ||
                categoryName.contains(searchQuery, ignoreCase = true) ||
                parentCategoryName.contains(searchQuery, ignoreCase = true) ||
                sourceAccName.contains(searchQuery, ignoreCase = true) ||
                destAccName.contains(searchQuery, ignoreCase = true) ||
                txDateStr.contains(searchQuery, ignoreCase = true) ||
                txDateStrNoZero.contains(searchQuery, ignoreCase = true)
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

        // Info text showing visible date range (Mese Solare)
        val currentMonthName = monthYearFormatter.format(selectedMonthCalendar.time).replaceFirstChar { it.uppercaseChar() }
        val infoText = when (language) {
            "English" -> "Showing transactions for $currentMonthName"
            "Español" -> "Mostrando transacciones de $currentMonthName"
            "Català" -> "Mostrant transaccions de $currentMonthName"
            "Français" -> "Affichage des transactions de $currentMonthName"
            "Deutsch" -> "Transaktionen von $currentMonthName anzeigen"
            else -> "Mostrate transazioni di $currentMonthName"
        }

        Text(
            text = infoText,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Month pagination navigation (legato esclusivamente al mese solare con limite massimo al mese corrente)
        val nowCal = java.util.Calendar.getInstance()
        val isCurrentCalendarMonth = (selectedMonthCalendar.get(java.util.Calendar.YEAR) == nowCal.get(
            Calendar.YEAR)) &&
                (selectedMonthCalendar.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val prevMonthTarget = remember(selectedMonthCalendar) {
                (selectedMonthCalendar.clone() as java.util.Calendar).apply {
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

            // Centered Calendar Today Icon Button
            if (!isCurrentCalendarMonth) {
                IconButton(
                    onClick = {
                        selectedMonthCalendar = Calendar.getInstance()
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .testTag("current_month_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Mese Corrente".t(language),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            // Next Month Button (ONLY if next month is <= current calendar month)
            val nextMonthTarget = remember(selectedMonthCalendar) {
                (selectedMonthCalendar.clone() as Calendar).apply {
                    add(Calendar.MONTH, 1)
                }
            }

            val canGoNext = (nextMonthTarget.get(Calendar.YEAR) < nowCal.get(Calendar.YEAR)) ||
                    (nextMonthTarget.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
                     nextMonthTarget.get(Calendar.MONTH) <= nowCal.get(Calendar.MONTH))

            if (canGoNext) {
                val nextMonthLabel = monthYearFormatter.format(nextMonthTarget.time).replaceFirstChar { it.uppercaseChar() }
                Button(
                    onClick = {
                        selectedMonthCalendar = (nextMonthTarget.clone() as Calendar)
                    },
                    modifier = Modifier.testTag("next_month_button")
                ) {
                    Text(nextMonthLabel, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
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

                    // Centered Text - Punto 9 (Responsive font)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Totale".t(language), style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        ResponsiveText(
                            text = totalSpent.formatEuro(),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(100.dp),
                            textAlign = TextAlign.Center
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
        val locale = remember(language) {
            when (language) {
                "English" -> Locale.ENGLISH
                "Español" -> Locale("es")
                "Català" -> Locale("ca")
                "Français" -> Locale.FRENCH
                "Deutsch" -> Locale.GERMAN
                else -> Locale.ITALIAN
            }
        }

        // ⭐ Legenda star button
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
                    text = "Clicca sulla stellina per fissare quel budget in evidenza nella schermata Home dell'applicazione.".t(language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // 📅 Legenda Calcolo Periodi Personalizzati (compatta e salvaspazio)
        val isNotMonday = !startOfWeek.equals("Lunedì", ignoreCase = true) && !startOfWeek.equals("Monday", ignoreCase = true)
        if (isNotMonday || finMonthDay != 1) {
            val monthlyBounds = viewModel.getMonthlyPeriodBounds(System.currentTimeMillis(), finMonthDay)
            val sdfDate = SimpleDateFormat("d MMM", locale)
            val startDateStr = sdfDate.format(Date(monthlyBounds.first))
            val endDateStr = sdfDate.format(Date(monthlyBounds.second))
            val prevDay = when (startOfWeek) {
                "Domenica", "Sunday" -> "Sabato".t(language)
                "Sabato", "Saturday" -> "Venerdì".t(language)
                "Venerdì", "Friday" -> "Giovedì".t(language)
                else -> "Domenica".t(language)
            }

            val legendText = buildString {
                if (isNotMonday) {
                    append("${"Settimana:".t(language)} ${startOfWeek.t(language)}–$prevDay")
                }
                if (isNotMonday && finMonthDay != 1) append(" • ")
                if (finMonthDay != 1) {
                    append("${"Mese fiscale:".t(language)} $startDateStr–$endDateStr")
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("📅", fontSize = 14.sp)
                    Text(
                        text = legendText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // B. BUDGET PER CONTO
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Budget per Conto".t(language),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            TextButton(onClick = { showAddTotalBudgetDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nuovo".t(language))
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

                val acc = if (budget.accountId != null) accounts.find { it.id == budget.accountId } else null
                val accountName = if (budget.accountId == null) {
                    "Tutti i Conti".t(language)
                } else {
                    acc?.name ?: "Conto Eliminato".t(language)
                }
                val accountEmoji = getAccountEmoji(acc)

                val periodText = when (budget.period) {
                    "Daily" -> "Giornaliero".t(language)
                    "Weekly" -> "Settimanale".t(language)
                    "Monthly" -> "Mensile".t(language)
                    else -> "Mensile".t(language)
                }
                val periodColor = getPeriodColor(budget.period)

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
                                        text = accountEmoji,
                                        fontSize = 18.sp
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = accountName,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = periodText,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = periodColor,
                                        maxLines = 1
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
                    val periodColor = getPeriodColor(budget.period)

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
                                            text = displayName,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = periodText,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = periodColor,
                                            maxLines = 1
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

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAccantonaDialog by remember { mutableStateOf(false) }
    var showRilasciaDialog by remember { mutableStateOf(false) }

    var editingGoal by remember { mutableStateOf<SavingsGoal?>(null) }
    var completingGoal by remember { mutableStateOf<SavingsGoal?>(null) }
    var expandedGoalId by remember { mutableStateOf<Int?>(null) }
    var isPiggyExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
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
                            text = "Saldo Totale Accantonato".t(language),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                Text(
                    text = totalSavedAll.formatEuro(),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.tertiary
                )

                // Breakdown per account (collapsible)
                val activeSavedAccounts = accounts.filter { it.savedAmount > 0.0 }
                if (activeSavedAccounts.isNotEmpty()) {
                    Divider(color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPiggyExpanded = !isPiggyExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Accantonamenti per conto".t(language) + if (activeSavedAccounts.size > 1) " (${activeSavedAccounts.size})" else "",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            imageVector = if (isPiggyExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Espandi",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isPiggyExpanded) {
                        activeSavedAccounts.forEach { acc ->
                            val accEmoji = getAccountEmoji(acc)
                            val ratio = if (totalSavedAll > 0) acc.savedAmount / totalSavedAll else 0.0
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$accEmoji ${acc.name}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                    )
                                    Text(
                                        text = "${acc.savedAmount.formatEuro()} (${(ratio * 100).toInt()}%)",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { ratio.toFloat().coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = MaterialTheme.colorScheme.tertiary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
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
                        Text("Svincola".t(language))
                    }
                }
            }
        }

        // A. Title Header (Moved below Salvadanaio card)
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

        // C. Individual Goals List Header
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
        }

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
                val linkedAcc = accounts.find { it.id == goal.accountId }
                val currentSavedForAccount = linkedAcc?.savedAmount ?: 0.0
                val progress = if (goal.targetAmount > 0) currentSavedForAccount / goal.targetAmount else 0.0
                val progressPercent = (progress * 100).toInt()
                val isCompleted = currentSavedForAccount >= goal.targetAmount
                val isExpanded = expandedGoalId == goal.id

                val sdfGoalDate = remember { SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN) }
                val isGoalExpired = remember(goal.deadline) {
                    if (goal.deadline.isBlank()) false else {
                        try {
                            val parsed = sdfGoalDate.parse(goal.deadline)
                            parsed != null && System.currentTimeMillis() >= parsed.time
                        } catch (_: Exception) {
                            false
                        }
                    }
                }

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
                        color = if (isGoalExpired) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else if (isCompleted) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f) else if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                    )
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
                                    Text(goal.iconEmoji.ifBlank { "🐷" }, fontSize = 18.sp)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            goal.name,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (isGoalExpired) {
                                            Text(
                                                "SCADUTO ⏱️".t(language),
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                    val accName = linkedAcc?.name ?: "Conto non associato".t(language)
                                    Text(
                                        text = if (goal.deadline.isBlank()) accName else "$accName • ${goal.deadline}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isGoalExpired) MaterialTheme.colorScheme.error else Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        goal.targetAmount.formatEuro(),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge.copy(color = if (isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary)
                                    )
                                    Text(
                                        "Target".t(language),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }

                                if (isCompleted) {
                                    IconButton(
                                        onClick = { completingGoal = goal },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape)
                                    ) {
                                        Text("🪙", fontSize = 20.sp)
                                    }
                                }
                            }
                        }

                        if (!isExpanded) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LinearProgressIndicator(
                                    progress = { progress.toFloat().coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = if (isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "$progressPercent%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            LinearProgressIndicator(
                                progress = { progress.toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = if (isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            if (isGoalExpired) {
                                Surface(
                                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "Obiettivo Scaduto! ⏱️ La data limite è stata superata.".t(language),
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                            text = "Suggerimento: puoi modificare o rimuovere la data di scadenza oppure eliminare l'obiettivo.".t(language),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedButton(
                                                onClick = { editingGoal = goal },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text("Modifica Data".t(language), fontSize = 11.sp)
                                            }
                                            OutlinedButton(
                                                onClick = { viewModel.updateSavingsGoal(goal.copy(deadline = "")) },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text("Rimuovi Scadenza".t(language), fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { editingGoal = goal },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Modifica".t(language), fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = { viewModel.deleteSavingsGoal(goal) },
                                    modifier = Modifier
                                        .size(36.dp)
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
        AddOrEditSavingsGoalDialog(
            accounts = accounts,
            viewModel = viewModel,
            onDismiss = { showAddGoalDialog = false }
        )
    }

    if (editingGoal != null) {
        AddOrEditSavingsGoalDialog(
            goal = editingGoal,
            accounts = accounts,
            viewModel = viewModel,
            onDismiss = { editingGoal = null }
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
            viewModel = viewModel,
            onDismiss = { showRilasciaDialog = false }
        )
    }

    if (completingGoal != null) {
        CompleteGoalExpenseDialog(
            goal = completingGoal!!,
            accounts = accounts,
            viewModel = viewModel,
            onDismiss = { completingGoal = null }
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
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setPushNotificationsEnabled(isGranted)
    }

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
                Switch(
                    checked = pushNotif,
                    onCheckedChange = { checked ->
                        if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.setPushNotificationsEnabled(checked)
                        }
                    }
                )
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
            Text("BUDGIE v0.9.8", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.Gray)
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
    val context = LocalContext.current
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val wordFrequencies by viewModel.noteWordFrequencies.collectAsStateWithLifecycle()

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
                            // Punto 2: Impedisci inserimento date successive a oggi
                            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
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
                            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                            datePickerDialog.show()
                        }
                )

                NoteTextFieldWithAutocomplete(
                    value = title,
                    onValueChange = { title = it },
                    wordFrequencies = wordFrequencies,
                    label = "Nota (massimo 22 caratteri)".t(language),
                    language = language,
                    maxChars = 22
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
                            limitStr = if (limitVal > 0.0) limitVal.formatForInput() else ""
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
fun AddOrEditSavingsGoalDialog(
    goal: SavingsGoal? = null,
    accounts: List<Account>,
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN) }

    var name by remember { mutableStateOf(goal?.name ?: "") }
    var targetStr by remember { mutableStateOf(goal?.targetAmount?.formatForInput() ?: "") }
    var deadline by remember { mutableStateOf(goal?.deadline ?: "") }
    var iconEmoji by remember { mutableStateOf(goal?.iconEmoji?.ifBlank { "🐷" } ?: "🐷") }
    var selectedAccountId by remember { mutableStateOf(goal?.accountId ?: accounts.firstOrNull()?.id ?: 0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val openDatePicker = {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                deadline = dateFormatter.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val finalEmoji = if (iconEmoji.isBlank()) "🐷" else iconEmoji.trim()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (goal == null) "Nuovo Obiettivo".t(language) else "Modifica Obiettivo".t(language)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
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
                    value = name,
                    onValueChange = { 
                        if (it.length <= 30 && !it.contains("\n")) name = it
                        errorMessage = null
                    },
                    label = { Text("Nome Obiettivo".t(language)) },
                    singleLine = true,
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

                // Date picker for deadline
                Box(modifier = Modifier.fillMaxWidth().clickable { openDatePicker() }) {
                    OutlinedTextField(
                        value = deadline,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        label = { Text("Scadenza (opzionale)".t(language)) },
                        trailingIcon = {
                            IconButton(onClick = { openDatePicker() }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Seleziona Data")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (deadline.isNotBlank()) {
                    TextButton(
                        onClick = { deadline = "" },
                        modifier = Modifier.align(Alignment.End),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Rimuovi scadenza (indefinita)".t(language), fontSize = 12.sp)
                    }
                }

                OutlinedTextField(
                    value = iconEmoji,
                    onValueChange = { 
                        if (it.length <= 4) iconEmoji = it
                        errorMessage = null
                    },
                    label = { Text("Simbolo Emoji (lascia vuoto per 🐷)".t(language)) },
                    isError = iconEmoji.isNotBlank() && !isValidSingleEmoji(finalEmoji),
                    supportingText = {
                        if (iconEmoji.isNotBlank() && !isValidSingleEmoji(finalEmoji)) {
                            Text("Inserisci un singolo carattere emoji valido".t(language), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (accounts.size > 1) {
                    Text("Conto da monitorare:".t(language))
                    val selectedAcc = accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()
                    BudgieDropdown(
                        label = "Seleziona Conto".t(language),
                        options = accounts,
                        selectedOption = selectedAcc,
                        optionToString = { acc -> acc?.let { "${getAccountEmoji(it)} ${it.name}" } ?: "" },
                        onOptionSelected = { acc ->
                            if (acc != null) selectedAccountId = acc.id
                        }
                    )
                } else {
                    selectedAccountId = accounts.firstOrNull()?.id ?: 0
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                    when {
                        name.isBlank() -> errorMessage = "Inserisci il nome dell'obiettivo.".t(language)
                        target <= 0.0 -> errorMessage = "Inserisci una cifra target valida maggiore di zero.".t(language)
                        !isValidSingleEmoji(finalEmoji) -> errorMessage = "Inserisci un'emoji valida (singolo carattere).".t(language)
                        selectedAccountId <= 0 -> errorMessage = "Seleziona un conto da monitorare.".t(language)
                        else -> {
                            try {
                                if (goal == null) {
                                    viewModel.addSavingsGoal(name, target, deadline, finalEmoji, selectedAccountId)
                                } else {
                                    viewModel.updateSavingsGoal(goal.copy(
                                        name = name,
                                        targetAmount = target,
                                        deadline = deadline,
                                        iconEmoji = finalEmoji,
                                        accountId = selectedAccountId
                                    ))
                                }
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                },
                enabled = name.isNotBlank() && targetStr.isNotBlank()
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
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val selectedAcc = accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()
    val maxAvailable = maxOf(0.0, (selectedAcc?.balance ?: 0.0) - (selectedAcc?.savedAmount ?: 0.0))

    var sliderPercent by remember(selectedAccountId) { mutableStateOf(0f) }
    var amountStr by remember(selectedAccountId) { mutableStateOf("") }

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

                Text("Accantona una quota di denaro dal tuo conto nel Salvadanaio Virtuale.".t(language))

                if (accounts.size > 1) {
                    Text("Seleziona conto di origine:".t(language))
                    BudgieDropdown(
                        label = "Seleziona Conto".t(language),
                        options = accounts,
                        selectedOption = selectedAcc,
                        optionToString = { acc -> 
                            if (acc == null) "" else "${getAccountEmoji(acc)} ${acc.name}"
                        },
                        onOptionSelected = { acc ->
                            if (acc != null) {
                                selectedAccountId = acc.id
                                sliderPercent = 0f
                                amountStr = ""
                                errorMessage = null
                            }
                        }
                    )
                } else {
                    selectedAccountId = accounts.firstOrNull()?.id ?: 0
                }

                // Dettaglio disponibilità subito sotto la selezione del conto
                Text(
                    text = "${"Disponibile sul conto:".t(language)} ${maxAvailable.formatEuro()}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                // Slider per accantonamento (0% a 100%) con valori interi
                if (maxAvailable > 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Percentuale:".t(language), style = MaterialTheme.typography.bodySmall)
                            Text("${sliderPercent.toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = sliderPercent,
                            onValueChange = { newVal ->
                                val percentInt = round(newVal).toInt()
                                sliderPercent = percentInt.toFloat()
                                val calculatedAmount = round(maxAvailable * percentInt / 100.0).toInt()
                                amountStr = if (calculatedAmount > 0) calculatedAmount.toString() else ""
                                errorMessage = null
                            },
                            valueRange = 0f..100f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { newVal ->
                        amountStr = newVal
                        errorMessage = null
                        val parsed = newVal.replace(',', '.').toDoubleOrNull() ?: 0.0
                        if (maxAvailable > 0) {
                            sliderPercent = ((parsed / maxAvailable) * 100.0).toFloat().coerceIn(0f, 100f)
                        }
                    },
                    label = { Text("Quota da accantonare (€)".t(language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                    when {
                        amount <= 0.0 -> errorMessage = "Inserisci una quota valida maggiore di zero.".t(language)
                        amount > maxAvailable -> errorMessage = "L'importo supera il saldo disponibile del conto (pari a ${maxAvailable.formatAmount()}, dedotti gli accantonamenti già effettuati).".t(language)
                        else -> {
                            try {
                                viewModel.accantonaToSavings(selectedAccountId, amount)
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
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val savedAccounts = accounts.filter { it.savedAmount > 0.0 }
    var selectedAccountId by remember { mutableStateOf(savedAccounts.firstOrNull()?.id ?: accounts.firstOrNull()?.id ?: 0) }
    
    val selectedAcc = accounts.find { it.id == selectedAccountId } ?: savedAccounts.firstOrNull()
    val maxSaved = selectedAcc?.savedAmount ?: 0.0

    var sliderPercent by remember(selectedAccountId) { mutableStateOf(100f) }
    var amountStr by remember(selectedAccountId) { mutableStateOf(maxSaved.toInt().toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Svincola Fondi".t(language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                if (savedAccounts.isEmpty()) {
                    Text("Nessun fondo accantonato disponibile per lo svincolo.".t(language), color = Color.Gray)
                } else {
                    if (savedAccounts.size > 1) {
                        Text("Seleziona il conto da cui svincolare i fondi:".t(language))
                        BudgieDropdown(
                            label = "Seleziona Conto".t(language),
                            options = savedAccounts,
                            selectedOption = selectedAcc,
                            optionToString = { acc -> 
                                if (acc == null) "" else "${getAccountEmoji(acc)} ${acc.name}"
                            },
                            onOptionSelected = { acc ->
                                if (acc != null) {
                                    selectedAccountId = acc.id
                                    sliderPercent = 100f
                                    amountStr = acc.savedAmount.toInt().toString()
                                    errorMessage = null
                                }
                            }
                        )
                    } else {
                        selectedAccountId = savedAccounts.first().id
                    }

                    Text("${"Accantonato su questo conto:".t(language)} ${maxSaved.formatEuro()}", fontWeight = FontWeight.Bold)

                    // Slider (0% to 100%) - integer amounts
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Percentuale:".t(language), style = MaterialTheme.typography.bodySmall)
                            Text("${sliderPercent.toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = sliderPercent,
                            onValueChange = { newVal ->
                                val percentInt = round(newVal).toInt()
                                sliderPercent = percentInt.toFloat()
                                val calculatedAmount = round(maxSaved * percentInt / 100.0).toInt()
                                amountStr = calculatedAmount.toString()
                                errorMessage = null
                            },
                            valueRange = 0f..100f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { newVal ->
                            amountStr = newVal
                            errorMessage = null
                            val parsed = newVal.replace(',', '.').toDoubleOrNull() ?: 0.0
                            if (maxSaved > 0) {
                                sliderPercent = ((parsed / maxSaved) * 100.0).toFloat().coerceIn(0f, 100f)
                            }
                        },
                        label = { Text("Importo esatto (€)".t(language)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (savedAccounts.isNotEmpty()) {
                Button(
                    onClick = {
                        val amount = amountStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                        when {
                            amount <= 0.0 -> errorMessage = "Inserisci un importo valido maggiore di zero.".t(language)
                            amount > maxSaved -> errorMessage = "L'importo supera i fondi accantonati su questo conto.".t(language)
                            else -> {
                                try {
                                    viewModel.releaseSavingsFromAccount(selectedAccountId, amount)
                                    onDismiss()
                                } catch (e: Exception) {
                                    errorMessage = "Errore durante lo svincolo: ${e.localizedMessage}".t(language)
                                }
                            }
                        }
                    }
                ) {
                    Text("Svincola".t(language))
                }
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
fun CompleteGoalExpenseDialog(
    goal: SavingsGoal,
    accounts: List<Account>,
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val expenseCats = categories.filter { it.parentCategoryId == null && it.type == "Expense" }

    val linkedAcc = accounts.find { it.id == goal.accountId }
    val linkedAccName = linkedAcc?.name ?: "Conto".t(language)
    val defaultAmount = minOf(goal.targetAmount, linkedAcc?.savedAmount ?: goal.targetAmount)

    var amountStr by remember { mutableStateOf(defaultAmount.formatForInput()) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(expenseCats.firstOrNull()?.id) }
    var selectedSubCategoryId by remember { mutableStateOf<Int?>(null) }
    var note by remember { mutableStateOf(goal.name) }
    var selectedTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val calendar = remember { Calendar.getInstance() }
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALIAN) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🎉 Obiettivo Raggiunto!".t(language)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${goal.iconEmoji} ${goal.name}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Complimenti! Puoi registrare l'acquisto reale usando i fondi accantonati sul conto '$linkedAccName'.".t(language), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it; errorMessage = null },
                    label = { Text("Importo Spesa (€)".t(language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Nota / Titolo Spesa".t(language)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Date selector (restricted to past dates up to now)
                val formattedDateTime = sdf.format(selectedTimestamp)
                OutlinedTextField(
                    value = formattedDateTime,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Data e Ora (Passata)".t(language)) },
                    trailingIcon = {
                        IconButton(onClick = {
                            val dpd = DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    calendar.set(Calendar.YEAR, year)
                                    calendar.set(Calendar.MONTH, month)
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                    
                                    TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                            calendar.set(Calendar.MINUTE, minute)
                                            val chosenTime = calendar.timeInMillis
                                            if (chosenTime <= System.currentTimeMillis()) {
                                                selectedTimestamp = chosenTime
                                                errorMessage = null
                                            } else {
                                                errorMessage = "Seleziona una data e ora passata o presente.".t(language)
                                            }
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            dpd.datePicker.maxDate = System.currentTimeMillis()
                            dpd.show()
                        }) {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Categories selection
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
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.replace(',', '.').toDoubleOrNull() ?: 0.0
                    when {
                        amount <= 0.0 -> errorMessage = "Inserisci un importo valido maggiore di zero.".t(language)
                        selectedTimestamp > System.currentTimeMillis() -> errorMessage = "Non puoi selezionare una data futura.".t(language)
                        else -> {
                            try {
                                viewModel.completeGoalAsExpense(
                                    goal = goal,
                                    amount = amount,
                                    categoryId = selectedCategoryId ?: expenseCats.firstOrNull()?.id,
                                    subCategoryId = selectedSubCategoryId,
                                    note = note,
                                    timestamp = selectedTimestamp
                                )
                                onDismiss()
                            } catch (e: Exception) {
                                errorMessage = "Errore: ${e.localizedMessage}".t(language)
                            }
                        }
                    }
                }
            ) {
                Text("Conferma Spesa".t(language))
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

        // G. Fondi Complessivi Section - Punto 6
        val totalGlobalBalance by viewModel.totalGlobalBalance.collectAsStateWithLifecycle()
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fondi Complessivi".t(language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = totalGlobalBalance.formatEuro(),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End
                )
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
fun String.toAccountEmoji(): String {
    val t = this.trim()
    return if (t.isNotBlank() && isValidSingleEmoji(t)) {
        t
    } else {
        when (t.lowercase(Locale.ROOT)) {
            "bank", "conto corrente", "banca" -> "🏦"
            "card", "carta", "carta di credito" -> "💳"
            "cash", "contanti", "contante" -> "💵"
            "savings", "risparmi", "salvadanaio" -> "💰"
            else -> if (t.isNotBlank()) t else "💳"
        }
    }
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
    var balanceStr by remember { mutableStateOf(account.balance.formatForInput()) }
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
    val wordFrequencies by viewModel.noteWordFrequencies.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf(transaction.title) }
    var amountStr by remember { mutableStateOf(transaction.amount.formatForInput()) }
    var selectedCategoryId by remember { mutableStateOf(transaction.categoryId) }
    var selectedSourceAccountId by remember { mutableStateOf(transaction.sourceAccountId) }
    var selectedDestinationAccountId by remember { mutableStateOf(transaction.destinationAccountId) }
    
    // Punto 7: Identificazione transazione "Saldo iniziale"
    val isInitialBalance = transaction.title.equals("Saldo iniziale", ignoreCase = true) || 
                         transaction.title.equals("Initial Balance", ignoreCase = true) ||
                         transaction.title.equals("Initialer Kontostand", ignoreCase = true) ||
                         transaction.title.equals("Solde initial", ignoreCase = true)

    // Formatting timestamp
    val sdf = remember { java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.ITALY) }
    var dateStr by remember { mutableStateOf(sdf.format(java.util.Date(transaction.timestamp))) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

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

                val openDatePickerAndPicker = {
                    calendar.timeInMillis = try { sdf.parse(dateStr)?.time ?: transaction.timestamp } catch(e: Exception) { transaction.timestamp }
                    val datePickerDialog = DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(Calendar.YEAR, year)
                            calendar.set(Calendar.MONTH, month)
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                                    calendar.set(Calendar.MINUTE, minute)
                                    calendar.set(Calendar.SECOND, 0)
                                    calendar.set(Calendar.MILLISECOND, 0)
                                    dateStr = sdf.format(calendar.time)
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                    datePickerDialog.show()
                }

                OutlinedTextField(
                    value = dateStr,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Data e Ora".t(language)) },
                    enabled = !isInitialBalance,
                    trailingIcon = {
                        if (!isInitialBalance) {
                            IconButton(onClick = { openDatePickerAndPicker() }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Seleziona Data e Ora".t(language))
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isInitialBalance) { openDatePickerAndPicker() }
                )

                NoteTextFieldWithAutocomplete(
                    value = title,
                    onValueChange = { title = it },
                    wordFrequencies = wordFrequencies,
                    label = "Nota (massimo 22 caratteri)".t(language),
                    language = language,
                    maxChars = 22
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
                    val currentCat = categories.find { it.id == selectedCategoryId } ?: relevantCategories.firstOrNull()
                    
                    if (isInitialBalance) {
                        OutlinedTextField(
                            value = currentCat?.let { "${it.iconEmoji} ${it.name}" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        BudgieDropdown(
                            label = "Seleziona Categoria".t(language),
                            options = relevantCategories,
                            selectedOption = currentCat ?: relevantCategories.first(),
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
                        !isInitialBalance && parsedDate > System.currentTimeMillis() -> {
                            errorMessage = "Errore: la data non può essere futura.".t(language)
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
                if (!isInitialBalance) {
                    TextButton(
                        onClick = {
                            viewModel.deleteTransaction(transaction)
                            onDismiss()
                        }
                    ) {
                        Text("Elimina".t(language), color = Color.Red)
                    }
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
        title = { Text("Nuovo Budget per Categoria".t(language)) },
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
        title = { Text("Nuovo Budget per Conto".t(language)) },
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

                Text("Seleziona Conto:".t(language), style = MaterialTheme.typography.labelLarge)
                val scopeOptions = listOf(null) + accounts
                BudgieDropdown(
                    label = "Conto".t(language),
                    options = scopeOptions,
                    selectedOption = accounts.find { it.id == selectedAccountId },
                    optionToString = { it?.let { "${it.type.toAccountEmoji()} ${it.name}" } ?: "🌍 ${"Tutti i Conti".t(language)}" },
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

    var amountStr by remember { mutableStateOf(budget.amountLimit.formatForInput()) }
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
                    Text("Conto".t(language), style = MaterialTheme.typography.labelLarge)
                    BudgieDropdown(
                        label = "Conto".t(language),
                        options = scopeOptions,
                        selectedOption = accounts.find { it.id == selectedAccountId },
                        optionToString = { it?.let { "${it.type.toAccountEmoji()} ${it.name}" } ?: "🌍 ${"Tutti i Conti".t(language)}" },
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
    var editingPlanned by remember { mutableStateOf<PlannedTransaction?>(null) }

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

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(16.dp)
                                ) {
                                    // Punto 10: Riprogettazione layout item
                                    // Riga superiore: Titolo a tutta riga
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
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Riga inferiore: Icona, Tipo/Frequenza e Importo
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(bgColor),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = emojiIcon, fontSize = 16.sp)
                                            }
                                            
                                            Column {
                                                val typeLabel = if (planned.isRecurring) {
                                                    "${"Ogni".t(language)} ${planned.frequencyInterval} ${planned.frequencyUnit.toString().t(language)}"
                                                } else {
                                                    "Programmato".t(language)
                                                }
                                                Text(
                                                    text = typeLabel,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray,
                                                    softWrap = true
                                                )
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
                                                },
                                                textAlign = TextAlign.End
                                            )
                                            
                                            Icon(
                                                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = if (expanded) "Collassa".t(language) else "Espandi".t(language),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                modifier = Modifier.clickable { expanded = !expanded }
                                            )
                                        }
                                    }

                                    // Expanded details section
                                    if (expanded) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                                        Spacer(modifier = Modifier.height(12.dp))

                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                            modifier = Modifier.padding(top = 4.dp)
                                                        ) {
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
                                                            if (subCategoryName != null) {
                                                                Text(">", fontSize = 10.sp, color = Color.Gray)
                                                                Box(
                                                                    modifier = Modifier
                                                                        .clip(RoundedCornerShape(8.dp))
                                                                        .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f))
                                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                                ) {
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

                                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Punto 1: Modifica Pianificazione
                                                TextButton(
                                                    onClick = { editingPlanned = planned },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Modifica".t(language), fontSize = 12.sp)
                                                }
                                                
                                                Spacer(modifier = Modifier.width(8.dp))

                                                TextButton(
                                                    onClick = { viewModel.deletePlannedTransaction(planned) },
                                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
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

    if (editingPlanned != null) {
        AddPlannedTransactionDialog(
            viewModel = viewModel,
            onDismiss = { editingPlanned = null },
            existingPlanned = editingPlanned
        )
    }
}

@Composable
fun AddPlannedTransactionDialog(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit,
    existingPlanned: PlannedTransaction? = null
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val wordFrequencies by viewModel.noteWordFrequencies.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    var step by remember { mutableStateOf(1) } // Step 1 or Step 2
    var title by remember { mutableStateOf(existingPlanned?.title ?: "") }
    var type by remember { mutableStateOf(existingPlanned?.type ?: "Expense") } // "Expense", "Income", "Transfer"
    var mode by remember { mutableStateOf(if (existingPlanned?.isRecurring == false) "Programmato" else "Ricorrente") } // "Ricorrente", "Programmato"
    var frequencyIntervalStr by remember { mutableStateOf(existingPlanned?.frequencyInterval?.toString() ?: "1") }
    var frequencyUnit by remember { mutableStateOf(existingPlanned?.frequencyUnit ?: "giorni") } // "giorni", "settimane", "mesi"
    
    // Default to tomorrow at the beginning of the day (00:00:00.000)
    val todayCal = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    val tomorrowTime = todayCal.timeInMillis + 24 * 60 * 60 * 1000
    
    val defaultCalendar = java.util.Calendar.getInstance().apply {
        timeInMillis = existingPlanned?.startDate ?: tomorrowTime
    }
    var startDate by remember { mutableStateOf(defaultCalendar.timeInMillis) }
    var amountStr by remember { mutableStateOf(existingPlanned?.amount?.formatForInput() ?: "") }
    
    var sourceAccountId by remember { mutableStateOf(existingPlanned?.sourceAccountId ?: accounts.firstOrNull()?.id ?: 0) }
    var destinationAccountId by remember { 
        mutableStateOf(existingPlanned?.destinationAccountId ?: accounts.filter { it.id != sourceAccountId }.firstOrNull()?.id) 
    }
    
    var selectedCategoryId by remember { mutableStateOf(existingPlanned?.categoryId) }
    var selectedSubCategoryId by remember { mutableStateOf(existingPlanned?.subCategoryId) }
    var validationError by remember { mutableStateOf<String?>(null) }

    // If destination is same as source, change it
    LaunchedEffect(sourceAccountId) {
        if (destinationAccountId == sourceAccountId) {
            destinationAccountId = accounts.filter { it.id != sourceAccountId }.firstOrNull()?.id
        }
    }

    // Set default category when type changes (preserving existing planned category on first load)
    var isFirstTypeEffect by remember { mutableStateOf(true) }
    LaunchedEffect(type) {
        if (isFirstTypeEffect) {
            isFirstTypeEffect = false
            if (existingPlanned != null && existingPlanned.categoryId != null) {
                selectedCategoryId = existingPlanned.categoryId
                selectedSubCategoryId = existingPlanned.subCategoryId
                return@LaunchedEffect
            }
        }
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
                    NoteTextFieldWithAutocomplete(
                        value = title,
                        onValueChange = { title = it },
                        wordFrequencies = wordFrequencies,
                        label = "Nome Pianificazione".t(language),
                        language = language,
                        maxChars = 22
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
                            
                            val updatedPlanned = (existingPlanned ?: PlannedTransaction(
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
                            )).copy(
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
                            
                            if (existingPlanned != null) {
                                viewModel.updatePlannedTransaction(updatedPlanned)
                            } else {
                                viewModel.addPlannedTransaction(updatedPlanned)
                            }
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
        FileProvider.getUriForFile(context, "com.aistudio.budgie.v2.fileprovider", file)
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

@Composable
fun SearchTipsDialog(
    language: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Suggerimenti di Ricerca".t(language),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Come cercare nella cronologia:".t(language),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val tips = listOf(
                    Triple(
                        "Data Esatta (gg/mm/aaaa)".t(language),
                        "15/09/2026",
                        "Trova le transazioni eseguite in un giorno specifico.".t(language)
                    ),
                    Triple(
                        "Mese e Anno (mm/aaaa)".t(language),
                        "09/2026",
                        "Filtra tutte le transazioni di un mese e anno specifici.".t(language)
                    ),
                    Triple(
                        "Nota o Descrizione".t(language),
                        "Spesa, Pizza, Stipendio",
                        "Cerca parole chiave nella nota o titolo della transazione.".t(language)
                    ),
                    Triple(
                        "Categoria o Sottocategoria".t(language),
                        "Alimentari, Ristoranti, Svago",
                        "Filtra per nome della categoria principale o sottocategoria.".t(language)
                    ),
                    Triple(
                        "Conto".t(language),
                        "Conto Principale, Carta",
                        "Mostra le transazioni collegate a uno specifico conto.".t(language)
                    )
                )

                tips.forEach { (title, examples, description) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${"Esempi:".t(language)} $examples",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Capito".t(language))
            }
        }
    )
}
