# geoARt

This product focuses on solving issues identified within the problem space encompassing the
disconnect between the community and their public spaces, and creating respectful, non-invasive ways
of making these environments interesting, stimulating, and exciting areas. It focuses specifically
on the role of urban art in livening communal areas, and the varying societal perspectives
surrounding its use.

## Installation

The APK to install the app on android phones can be downloaded from
our [website](https://deco3801-goofygoobers.uqcloud.net/).

The website provides further instructions on how to install an APK onto an android phone.

## Building from source

### Prerequisite

The main Prerequisite is having [Android Studio](https://developer.android.com/studio) installed.

For detailed instructions of how to install Android Studio, please refer to
the [official documentation](https://developer.android.com/codelabs/basic-android-kotlin-training-install-android-studio).

### Building

1. Clone the repository (or use the submitted zip file)
    ```shell
    git clone https://github.com/Goofy-Goobers-DECO3801/ar-geotag-app.git
    cd ar-geotag-app
     ```
2. Open the repository in Android Studio
    ```shell
    studio .
    ```
3. Add the API key for google maps to `local.properties`
    ```shell
   echo MAPS_API_KEY=AIzaSyDjU0kEDQBI_5zZ9R3qJ5T88mRmyW5MXPQ >> local.properties
    ```
4. Select the `App` configuration and run. The first time you do this it may take several minutes as
   all the build dependencies will be downloaded automatically.

### Testing

To test the program you require [Node.js](https://nodejs.org/en/download) version 16.0 or higher as
it is required
for [Firebase CLI](https://firebase.google.com/docs/emulator-suite/install_and_configure) to emulate
the database, storage, and authentication.

Once you have Node.js installed you can install the test dependencies

```shell script
npm install
```

The following Android Studio configurations can be run to test the app:

- The Android tests can be run with the `Android Tests` configuration.
- The Unit tests can be run by running the `Unit Tests` configuration.

### Style

The code style of the project is enforced using [ktlint](https://pinterest.github.io/ktlint/1.0.0/),
which is a style checker and formatting adhering to the
[Kotlin coding conventions](https://kotlinlang.org/docs/reference/coding-conventions.html)
and [Android Kotlin Style Guide](https://android.github.io/kotlin-guides/style.html).

The following Android Studio configurations can be run to perform various style checks and format
the code:

- The Android Studio linter can be run with the `Lint` configuration.
- The code style can be checked by `ktlint` with the `Style` configuration.
- The code can be formatted by `ktlint` with the `Format` configuration.

## Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Core UI library.
- [Material Design 3](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary) -
  UI theme and design.
- [Firebase](https://github.com/firebase/firebase-android-sdk) - Provides backend database (
  Firestore), storage, and authentication.
- [AR Core](https://github.com/google-ar/arcore-android-sdk) - Provides AR capabilities.
- [SceneView](https://github.com/SceneView/sceneview-android) - Integrates AR Core with Jetpack
  Compose.
- [Google Play Services](https://developers.google.com/android/guides/overview) - Used for Google
  Maps, and location services.
- [Google Maps Compose](https://github.com/googlemaps/android-maps-compose) - Integrates Google Maps
  with Jetpack Compose.
- [GeoFirestore](https://github.com/imperiumlabs/GeoFirestore-Android) - Provides realtime
  geo-queries using Firebase Firestore.
- [Hilt](https://github.com/google/dagger) - Provides depedency injection.
- [Coil](https://github.com/coil-kt/coil) - Async image loading and displaying.
- [ktlint](https://pinterest.github.io/ktlint/1.0.0/) - Code style checker and formatter
