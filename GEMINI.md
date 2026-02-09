# SafeBytes Application Context

This document provides a technical overview of the "SafeBytes" (ProductRecommendation) Android application to facilitate future AI-assisted development tasks.

## 📱 Project Overview

**SafeBytes** is a native Android prototype application written in **Java**. Its primary function is to capture images of product labels (specifically ingredient lists) via the device camera and extract the text using Optical Character Recognition (OCR). This text is then processed to display a list of ingredients to the user.

### Key Capabilities
*   **OCR & Text Extraction:** Utilizes `com.google.android.gms:play-services-vision` to detect and recognize text from images.
*   **Camera Integration:** Custom camera implementation (via `OCRActivity`) to capture images.
*   **User Management:** Basic local simulation of user authentication (Login/Register) using `SharedPreferences`.
*   **Data Parsing:** Splits recognized text blocks by commas to format ingredient lists.

## 🛠 Technical Architecture

*   **Language:** Java
*   **Build System:** Gradle
*   **Minimum SDK:** 26 (Android 8.0 Oreo)
*   **Target SDK:** 33 (Android 13 Tiramisu)
*   **Namespace:** `com.nextgen.productrecommendation`

### Key Libraries
*   `com.google.android.gms:play-services-vision:18.0.0` (Legacy Mobile Vision API)
*   `com.google.android.gms:play-services-mlkit-text-recognition-common:18.0.0`
*   `androidx.appcompat:appcompat:1.6.0`
*   `androidx.constraintlayout:constraintlayout:2.1.4`

## 📂 Project Structure

*   **`app/src/main/java/com/nextgen/productrecommendation/`**: Contains all source code.
    *   `OCRActivity.java`: Core logic for camera handling and text recognition.
    *   `IngredientsActivity.java`: Displays the parsed ingredients.
    *   `GlobalPreference.java`: Helper class for `SharedPreferences` (User Session).
    *   `LoginActivity.java` / `RegisterActivity.java`: Auth UI and logic.
*   **`app/src/main/AndroidManifest.xml`**: App manifest, permissions, and activity declarations.
*   **`app/build.gradle`**: Module-level build configuration.

## 🚀 Building and Running

### Commands
This project uses the Gradle wrapper.

*   **Build Debug APK:**
    ```bash
    ./gradlew assembleDebug
    ```
*   **Run Unit Tests:**
    ```bash
    ./gradlew test
    ```
*   **Run Instrumentation Tests:**
    ```bash
    ./gradlew connectedAndroidTest
    ```

### Permissions
The app strictly requires the following permissions (declared in `AndroidManifest.xml`):
*   `android.permission.CAMERA`
*   `android.permission.READ_EXTERNAL_STORAGE`
*   `android.permission.WRITE_EXTERNAL_STORAGE`

## 📝 Development Conventions

*   **UI Pattern:** MVC (Model-View-Controller) where Activities act as Controllers.
*   **Data Persistence:** Currently relies on `SharedPreferences` for user session data. No local database (Room/SQLite) is implemented yet.
*   **Code Style:** Standard Java Android conventions. Resources are stored in `app/src/main/res`.
*   **Future Migration:** There is an intent to migrate from the legacy `play-services-vision` to the newer ML Kit or CameraX libraries.
