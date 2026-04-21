<p align="center">
  <h1 align="center">📈 TradeTrack - Trading Journal & Analytics App</h1>
</p><p align="center">
  <img src="https://img.shields.io/badge/version-1.0-blue" />
  <img src="https://img.shields.io/badge/API-24%2B-green" />
  <img src="https://img.shields.io/badge/license-MIT-brightgreen" />
  <img src="https://img.shields.io/badge/platform-Android-brightgreen" />
</p><p align="center">
<strong>TradeTrack</strong> is a professional trading journal and analytics application for Android. Track your trades, analyze performance patterns, and improve your trading discipline with data-driven insights. Perfect for day traders, swing traders, and forex traders.
</p>---

📱 Features

📊 Core Trading Features

- Trade logging with entry, exit, stop-loss, and take-profit
- Full trade history with filtering, editing, and deletion
- Performance analytics (win rate, profit/loss charts, stats)
- Detailed trade breakdown (timeframes, prices, outcomes)
- Image attachments for trade setups and screenshots

🧠 Smart Insights & Analysis

- Overtrading detection (alerts on excessive trading days)
- Risk-reward ratio analysis (<1.5:1 warnings)
- Losing streak detection (consecutive losses tracking)
- Trading time optimization (best hours analysis)
- Performance pattern recognition with color-coded insights

🎨 Dynamic Dashboard

- Daily P&L summary at a glance
- Quick stats cards (win rate, avg win/loss, best trade)
- Gamified XP progress system
- Motivational trading messages
- Smooth animated UI components

⚡ Micro-Interactions & Polish

- Button press animations (scale + fade effects)
- Material 3 ripple feedback
- Floating action button pulse animation
- Screen transition animations
- Haptic feedback on key actions
- Loading indicators & skeleton states

🔐 User Management

- Firebase authentication (email/password + anonymous)
- User profile customization
- Dark & light theme support
- Persistent settings storage
- Secure logout/session handling

---

🛠️ Tech Stack

Languages & Frameworks

- Kotlin (primary language)
- Jetpack Compose (modern UI)
- Material Design 3

Architecture & Data

- MVVM architecture
- Room database (local storage)
- Firebase (Auth + Firestore)
- Kotlin Coroutines
- LiveData / StateFlow

UI & Animation

- Jetpack Compose UI toolkit
- Lottie animations
- Material Icons Extended
- Coil image loading

Charts & Tools

- MPAndroidChart
- GSON serialization
- Navigation Compose

---

📋 Requirements

- Android 7.0+ (API 24+)
- JDK 11+
- Android Studio Flamingo or newer
- Gradle 8.0+

---

🚀 Getting Started

Prerequisites

- Android Studio installed
- Firebase project created

---

Installation

1. Clone the repository

git clone https://github.com/yourusername/TradeTrack.git
cd TradeTrack

2. Setup Firebase

- Go to Firebase Console
- Create a project
- Add Android app
- Download "google-services.json"
- Place it inside "app/" folder

3. Build the project

./gradlew build

4. Run on device/emulator

./gradlew installDebug

---

📁 Project Structure

TradeTrack/
├── app/
│   ├── src/main/java/com/example/tradetrack/
│   │   ├── data/
│   │   │   ├── db/
│   │   │   ├── repository/
│   │   │   └── model/
│   │   ├── ui/
│   │   │   ├── screens/
│   │   │   ├── components/
│   │   │   └── animations/
│   │   ├── viewmodel/
│   │   ├── navigation/
│   │   ├── util/
│   │   └── MainActivity.kt
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties

---

📖 Documentation

- IMPLEMENTATION_SUMMARY.md — Feature overview
- MICRO_INTERACTIONS_GUIDE.md — Animation system
- README_ANIMATIONS.md — Animation reference
- QUICK_REFERENCE.md — Code snippets
- CHANGELOG.md — Version history

---

🎨 UI/UX Highlights

- Smart insights dashboard with trading patterns
- Daily performance summary cards
- XP-based gamification system
- Material Design 3 animations
- Smooth navigation transitions
- Real-time performance feedback

---

💡 Smart Insights Engine

Overtrading Detection

Tracks daily trades and alerts when limits are exceeded.

Risk-Reward Analysis

Ensures disciplined trading by flagging poor RR setups.

Losing Streak Detection

Helps prevent emotional trading decisions.

Time Analysis

Identifies your most profitable trading hours.

---

🧑‍💻 Development

Build Debug APK

./gradlew assembleDebug

Build Release APK

./gradlew assembleRelease

Run Tests

./gradlew test

---

🐛 Troubleshooting

App crashes on startup

- Verify Firebase setup
- Clear app data
- Check API level (24+)

Animations not working

- Ensure Compose dependencies are enabled
- Check system animation settings

Firebase issues

- Confirm google-services.json location
- Verify internet permission

---

📊 Performance

Metric| Target| Status
FPS| 60| ✅
Startup| <2s| ✅
Memory| <150MB| ✅
Response| <200ms| ✅

---

🤝 Contributing

1. Fork repo
2. Create branch
3. Commit changes
4. Push branch
5. Open Pull Request

---

📝 License

MIT License — free to use and modify.

---

🗺️ Roadmap

- CSV/PDF export
- Trading strategy templates
- Advanced indicators
- Multi-account support
- Broker API integration
- AI trading insights

---

🔐 Privacy

- All trades stored locally
- Optional Firebase sync
- No third-party tracking
- Full user data control

---

⭐ Support

If you like this project, star the repo ⭐

<p align="center">
<strong>TradeTrack — Build discipline. Track performance. Improve trading.</strong>
</p>
