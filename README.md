# 🎂 My BirthdayTracker (Android)

A premium, native Android application built with Kotlin to help you track, manage, and never forget a birthday again. The app features automated SMS greetings, voice call reminders, and system notifications using modern Android architecture.

---

## 🌟 Features

- **Multi-Channel Reminders:** Receive local notifications, automated SMS greetings, and **Automated Voice Calls** via Twilio on the morning of a birthday.
- **Intimation Alerts:** Get notified **one day before** a birthday so you can prepare in advance.
- **Premium UI/UX:** Built with **Material Design 3**, featuring dynamic cards, smooth transitions, and a clean, modern aesthetic.
- **Offline First:** All birthday data is stored locally using **Room Database**, ensuring the app works perfectly without an internet connection.
- **Smart Background Tasks:** Uses **WorkManager** to schedule daily checks that persist even after device reboots.
- **Pre-populated Data:** Automatically seeds 35+ records from a JSON source on the first installation.
- **Missed Birthdays:** A dedicated section to see birthdays you might have missed recently.

---

## 🛠️ Technology Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Database:** [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- **Background Scheduling:** [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- **Networking:** [Retrofit](https://square.github.io/retrofit/) & [Gson](https://github.com/google/gson)
- **Communications API:** [Twilio Messaging & Voice API](https://www.twilio.com/docs)
- **Image Loading:** [Glide](https://github.com/bumptech/glide)
- **UI Components:** [Material Components for Android](https://material.io/develop/android)

---

## 📂 Project Structure

```text
app/src/main/java/com/example/mybirthdaytracker/
├── adapters/          # RecyclerView Adapters for Home and Missed lists
├── data/              # Room DB: Entities, DAOs, and Database configuration
├── network/           # Retrofit Client and Twilio API (SMS + Voice)
├── repository/        # Repository pattern for data abstraction
├── ui/                # UI Fragments (Home, Add, Profile)
├── utils/             # Date processing and Notification helpers
├── viewmodel/         # ViewModels and ViewModelFactories
└── workers/           # BirthdayReminderWorker (SMS, Voice, and Notification logic)
```

---

## ⚙️ How It Works

### 1. Data Layer (Room)
The app uses a `BirthdayEntity` to store name, date of birth, tags, and image paths. The `BirthdayDatabase` includes a callback that reads `birthdays.json` from the assets folder during the first run to seed initial data.

### 2. Logic Layer (MVVM)
- **Repository:** Fetches data from Room and provides it as a `Flow`.
- **ViewModel:** Collects data flows and prepares it for the UI.
- **Fragments:** Observe the ViewModel and update the RecyclerViews using `ListAdapter` for efficient updates.

### 3. Communication Layer (WorkManager + Twilio)
The `BirthdayReminderWorker` is scheduled to run every 24 hours.
1. **Intimation (1 Day Before):** It triggers a local notification to alert the user about tomorrow's birthdays.
2. **On the Day:** It triggers:
    - A **Local Notification** with high priority.
    - An **Automated SMS** with a personalized birthday message.
    - An **Automated Voice Call** that announces the birthday person's name via Text-to-Speech.

---

## 🚀 Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/rbsk-05/MAD-MyBirthdayTracker-App.git
   ```

2. **Configure Twilio:**
   Create a `local.properties` file in the root directory and add your credentials:
   ```properties
   TWILIO_ACCOUNT_SID=your_sid_here
   TWILIO_AUTH_TOKEN=your_token_here
   TWILIO_FROM_PHONE=+1234567890
   TWILIO_TO_PHONE=+0987654321
   ```

3. **Build & Run:**
   - Open the project in **Android Studio**.
   - Sync Gradle.
   - Run on an emulator or physical device (API 24+).
   - **Important:** On Android 13+, ensure you grant the "Notification Permission" on first launch.

---

## 📝 Author
**Darshan M**  
Roll No: 230701061  
Rajalakshmi Engineering College
