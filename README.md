# BookYourPlace

Simple Hotel Booking Application on Android Studio

## Features

- **Hotel Booking:** Browse and book hotels based on your preferences.
- **Hotel Renting:** Rent your property to potential guests.
- **User Authentication:** Secure user authentication using Firebase Authentication.
- **Firebase Database:** Store and retrieve hotel data using Firebase Realtime Database.
- **Firebase Storage:** Store images and other media files using Firebase Storage.

## Technologies Used

- Java
- Android Studio
- Firebase Authentication
- Firebase Realtime Database
- Firebase Storage

## Installation

1. Clone the repository: `git clone https://github.com/your-username/your-repository.git`
2. Open the project in Android Studio.
3. Configure your Firebase project:
   - Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app to the project and follow the setup instructions.
   - Download the `google-services.json` file and place it in the `app/` directory of your project.
   - Enable Firebase Authentication and Realtime Database in the Firebase console.
4. Build and run the app on an Android emulator or a physical device.

## Configuration

In the `app/build.gradle` file, replace the placeholder with your Firebase API Key:

```gradle
android {
    // ...
    buildTypes {
        release {
            // ...
            buildConfigField "String", "FIREBASE_API_KEY", '"[Your Firebase API Key]"'
        }
    }
}
