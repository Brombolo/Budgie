# Budgie 🦜 - Assistente Finanziario Personale / Personal Financial Assistant

**Budgie** è un'applicazione Android moderna, intuitiva e focalizzata sulla privacy per la gestione delle finanze personali, il controllo dei budget di spesa, la pianificazione delle transazioni e il tracciamento degli obiettivi di risparmio.

---

## 🌟 Caratteristiche Principali / Main Features

- 🔒 **100% Locale & Privata**: Nessun server esterno, nessun tracciamento. Tutti i tuoi dati finanziari rimangono esclusivamente sul tuo dispositivo.
- 💳 **Gestione Multi-Conto**: Supporto per Conti Bancari, Carte di Credito e Contanti con calcolo automatico del saldo totale e del salvadanaio.
- 📊 **Budget & Limiti di Spesa**: Imposta soglie mensili o per categoria con avvisi automatici allo sforamento.
- 🎯 **Obiettivi di Risparmio & Salvadanaio Virtuale**: Accantona fondi, assegna risparmi agli obiettivi e monitora la barra di avanzamento verso i tuoi traguardi.
- 📈 **Report & Statistiche Visive**: Grafici interattivi di distribuzione delle spese ed evoluzione del patrimonio.
- 📅 **Transazioni Pianificate e Recurrenti**: Automatizza entrate, uscite e giroconti periodici.
- 🌐 **Supporto Multilingua / Multi-language**:
  - 🇮🇹 Italiano
  - 🇬🇧 English
  - 🇪🇸 Español
  - 🟡🔴 Català
  - 🇫🇷 Français
  - 🇩🇪 Deutsch

---

## 🛠️ Architettura e Tecnologie / Tech Stack

- **Linguaggio**: Kotlin
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Database**: Room Database con KSP (Kotlin Symbol Processing)
- **Asincronia**: Kotlin Coroutines & StateFlow
- **Grafici**: Vico / Custom Canvas Charts
- **Architettura**: MVVM (Model-View-ViewModel) + Clean Architecture pattern

---

## 🚀 Come Compilare il Progetto / How to Build

### Requisiti
- Android Studio Ladybug (o successivo) / JDK 17+
- Android SDK 26+ (Android 8.0+)

### Comandi Gradle

Compilare l'applicazione ed eseguire la verifica dei file:
```bash
# Compilazione debug
gradle assembleDebug

# Esecuzione unit test
gradle testDebugUnitTest
```

L'APK generato sarà disponibile nel percorso:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 Licenza / License

Questo progetto è distribuito sotto licenza **GNU General Public License v3.0 (GPL-3.0)**. Consulta il file [LICENSE](LICENSE) per i dettagli completi.
