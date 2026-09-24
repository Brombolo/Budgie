<div align="center">

# 🦜 Budgie - Personal Financial Assistant

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg?style=for-the-badge)](https://www.gnu.org/licenses/gpl-3.0)
[![Android SDK](https://img.shields.io/badge/Android_SDK-26%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)

**Budgie** is a modern, privacy-first, and lightning-fast Android application designed to help you effortlessly manage personal finances, track budgets, schedule recurring transactions, and achieve your savings goals.

[**Download Latest APK (v0.9.8)**](./Budgie-0.9.8.apk)

</div>

---

## ✨ Core Features

### 🔒 100% Local & Privacy-First
Your financial data belongs exclusively to you. Budgie operates entirely offline with zero telemetry, cloud tracking, or external servers. All data is securely stored locally using Room Database.

### 💳 Multi-Account Management
- Seamlessly manage bank accounts, credit/debit cards, and cash wallets.
- Real-time aggregation of total and disposable balances.
- Custom account icons and inclusion/exclusion toggles from net worth.

### 📊 Advanced Budgeting & Expense Tracking
- Set daily, weekly, or monthly spending limits per category or overall.
- Real-time progress indicators and automatic alerts when approaching or exceeding budget limits.
- Rich transaction history with powerful keyword search, filters, and CSV export.

### 🎯 Savings Goals & Virtual Piggy Bank
- Dedicate per-account virtual savings pools ("Salvadanaio Virtuale") with clean progress tracking.
- Set individual savings goals linked to specific accounts with strict emoji validation (`🐷`).
- Automatic deadline tracking with smart expiration badges and quick management actions.
- **One-Tap Completion**: Reach 100% of your goal and instantly record a real expense utilizing your saved funds.

### 📅 Planned & Recurring Transactions
- Automate future income, expenses, and internal transfers with flexible recurrence intervals (daily, weekly, monthly).
- Automatic execution tracking and scheduled overview.

### 📈 Reports & Analytics
- Interactive canvas charts showing expense distributions by category, financial trends, and cash flow evolution.
- Persistent historical trend filtering (Week, Month, Year).

### 🌐 Multi-Language Support
Budgie is fully localized in:
- 🇬🇧 English
- 🇮🇹 Italiano
- 🇪🇸 Español
- 🟡🔴 Català
- 🇫🇷 Français
- 🇩🇪 Deutsch

---

## 🛠️ Architecture & Tech Stack

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io) design system and edge-to-edge support.
- **Architecture**: MVVM (Model-View-ViewModel) pattern adhering to Clean Architecture principles.
- **Asynchronous Programming**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [StateFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx.coroutines-core/kotlinx.coroutines.flow/-state-flow/).
- **Database**: [Room Persistence Library](https://developer.android.com/training/data-storage/room) with KSP (Kotlin Symbol Processing).

---

## 🚀 Getting Started & Installation

### Download Ready-to-Use APK
You can download the latest compiled release directly from the repository root:
📥 [**Budgie-0.9.8.apk**](./Budgie-0.9.8.apk)

### Building from Source
1. Clone the repository:
   ```bash
   git clone https://github.com/Brombolo/Budgie.git
   ```
2. Open the project in **Android Studio** (Ladybug or newer).
3. Build and run the app on your emulator or physical Android device (min SDK 26).

---

## 👨‍💻 Developer

- **GitHub**: [@Brombolo](https://github.com/Brombolo)

---

## 📄 License

Budgie is open-source software licensed under the **GNU General Public License v3.0**. See the [LICENSE](LICENSE) file for more details.
