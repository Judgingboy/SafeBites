# SafeBytes Application

A native Android application designed to scan product labels and extract ingredient information using Optical Character Recognition (OCR). This project serves as a prototype for a product analysis and recommendation system.

## 📱 Project Overview

This application allows users to capture images of product packaging (specifically ingredient lists) and automatically extracts the text using Google's Mobile Vision API. It is built to help users quickly digitize and view product ingredients.

This Android application works in conjunction with a backend service that handles
authentication, allergy data, and ingredient management.

## 🎥 Demo Video

A walkthrough of the SafeBites Android application in action, demonstrating:
- User authentication
- OCR-based ingredient scanning
- Ingredient extraction and display
- Backend integration

▶️ **Watch the demo:**  
https://youtu.be/S6CkRNoJhXI


🔗 **Backend Repository:**  
https://github.com/Judgingboy/SafeBitesBackend


## ✨ Features

*   **User Authentication:** Login and registration flow integrated with a PHP + MySQL backend via REST-style APIs.
*   **Camera Integration:** In-app camera functionality to capture high-quality images of product labels.
*   **OCR Technology:** distinct text recognition using `com.google.android.gms:play-services-vision`.
*   **Ingredient Extraction:** Parses scanned text (specifically comma-separated values) to present a readable list of ingredients.
*   **User Session Management:** Stores basic session data locally using SharedPreferences
after successful authentication via the backend.

## 🛠 Tech Stack

*   **Language:** Java
*   **Platform:** Android (Min SDK 26, Target SDK 33)
*   **UI Framework:** Android XML Layouts, Material Design Components
*   **Libraries:**
    *   `androidx.appcompat:appcompat`
    *   `androidx.constraintlayout:constraintlayout`
    *   `com.google.android.gms:play-services-vision` (Text Detection)
    *   `com.google.android.gms:play-services-mlkit-text-recognition-common`

## 🧩 System Architecture

The SafeBites system follows a **client–server architecture**:

- **Android App (This Repository):**
  - Handles UI, camera input, OCR processing
  - Sends requests to backend APIs
- **Backend Service:**
  - Manages authentication, user data, allergies, and ingredients
  - Stores persistent data in MySQL
  - Returns JSON responses to the mobile app




## 🏗 APP Architecture (Android)

The app follows a standard **MVC (Model-View-Controller)** pattern where Activities serve as the controllers managing the UI and business logic.

*   **Activities:**
    *   `SplashActivity`: App entry point.
    *   `LoginActivity` / `RegisterActivity`: User onboarding.
    *   `HomeActivity`: Main dashboard.
    *   `OCRActivity`: Handles Camera interaction and OCR processing.
    *   `IngredientsActivity`: Displays the extracted data.
*   **Data Management:** `GlobalPreference` class handles simple session persistence (e.g., storing the logged-in user's name) via `SharedPreferences`.

## 🔐 Permissions

The app requires the following permissions to function:

*   `android.permission.CAMERA`: To capture product images.
*   `android.permission.READ_EXTERNAL_STORAGE` / `WRITE_EXTERNAL_STORAGE`: To handle image files.

## 🚀 How to Build and Run

1.  **Prerequisites:**
    *   Android Studio Flamingo or newer.
    *   JDK 1.8 or newer.
2.  **Clone the Repository:**
    ```bash
    git clone <repository-url>
    ```
3.  **Open in Android Studio:**
    *   Select `File > Open` and choose the project directory.
4.  **Sync Gradle:**
    *   Allow Android Studio to download dependencies and sync the project.
5.  **Run:**
    *   Connect an Android device or start an Emulator.
    *   Click the **Run** (Play) button.

## 📂 Folder Structure

```
app/src/main/
├── java/com/nextgen/productrecommendation/
│   ├── GlobalPreference.java   # SharedPreference Helper
│   ├── HomeActivity.java       # Dashboard
│   ├── IngredientsActivity.java# Result Display
│   ├── LoginActivity.java      # Auth
│   ├── OCRActivity.java        # Camera & OCR Logic
│   └── ...
├── res/
│   ├── layout/                 # XML UI Definitions
│   ├── mipmap/                 # Icons
│   └── values/                 # Strings, Colors, Themes
└── AndroidManifest.xml         # App Configuration
```

## 🔮 Future Improvements

*   **Recommendation Engine:** Implement logic to analyze extracted ingredients and recommend healthier alternatives or flag allergens.
*   **Backend Enhancements:** Improve API security, validation, and scalability for production use.
*   **Live Camera Preview:** Implement real-time text detection overlay.
*   **Migrate to CameraX:** Update camera logic to the modern Jetpack CameraX library.
