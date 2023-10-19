# geoARt

This product focuses on solving issues identified within the problem space encompassing the
disconnect between the community and their public spaces, and creating respectful, non-invasive ways
of making these environments interesting, stimulating, and exciting areas. It focuses specifically
on the role of urban art in livening communal areas, and the varying societal perspectives
surrounding its use.

## Installing the app

The built APK to be installed on android phones can be downloaded from
our [website](https://deco3801-goofygoobers.uqcloud.net/).

The website provides detailed instructions on how to install an APK onto an android phone.

## Project Structure

The project is architected using the the Model-View-ViewModel (MVVM) architecture pattern and is structured as follows:

- `firebase/` directory contains the Firebase security rules and indexes.
- `app/` directory contains the main source code for the app.
  - `src/main/kotlin/com/goofygoobers/geoart/` directory contains the Kotlin source code for the
    app.
    - `viewmodel/` contains the view models for the app that are used to store and manage the state of
      the UI.
    - `ui/` contains the UI screens and components for the app.
    - `data/` contains the database data models and repositories for the app.
    - `directions/` contains all code related to querying the Google Directions API to get the directions to an artwork.
    - `artdisplay/` contains all code related to the previewing and displaying of art in AR.
    - `di/` contains the dependency injection modules for the app.
    - `util/` contains utility functions for the app.
  - `src/main/res` directory contains the resources for the app.
  - `src/main/assets` directory contains the assets for the app.
  - `src/main/python` directory contains the Python script for the app that is used to convert 2D images into 3D models.
  - `src/androidTest/kotlin/com/goofygoobers/geoart` directory contains the Android tests for
    the app.
  - `src/test/kotlin/com/goofygoobers/geoart` directory contains the unit tests for the app.

## Building from source

### Prerequisite

#### Android Studio

The main Prerequisite is having [Android Studio](https://developer.android.com/studio) installed.

For detailed instructions of how to install Android Studio, please refer to the official
[Android documentation](https://developer.android.com/codelabs/basic-android-kotlin-training-install-android-studio).

#### API Keys

Next you will need to setup the API keys required to build the project,

##### Google Maps Platform API Key

Since the app uses geolocation and displays dynamic maps a Google Maps Platform API key is required,
please following the official
[Google Maps Platform documentation](https://developers.google.com/maps/get-started)
to setup an API key.
The free tier of Google Maps Platform will suffice as that will give you $200 of free usage monthly.
Once you have created your Google Maps Platform API, go into
the [Google Cloud Console](https://console.cloud.google.com/google/maps-apis/api-list), select
APIs & Services and ensure that at least the following APIs are enabled:

- Maps SDK for Android (Provides dynamic maps used on home page and art page)
- Directions API (Provides the route information shown on the art page)
- Maps Static API (Provides the static mini-maps on the profile page)

Once you have created and configures Google Maps Platform API, locate your key in the
[Google Cloud Console](https://console.cloud.google.com/google/maps-apis/credentials)
and copy it the `local.properties` file,

```shell
MAPS_API_KEY=YOUR_API_KEY
```

##### Firebase

The serverless backend of the app is powered by Firebase and a Firebase API key is required.
The following steps have been extracted from the official
[Firebase documentation](https://firebase.google.com/docs/android/setup)
for clarity and will help you setup a Firebase project and connect it to the app.

**Step 1. Create a Firebase project:**

1. Visit the [Firebase console](https://console.firebase.google.com) and create a Firebase account.
2. You should be now be logged into the Firebase console, if not please revisit the link from Step
3. Click **Add project** and follow the prompts.
4. If prompted, review and accept the Firebase terms, then click **Continue**.
5. Give the project a name and take note of the **Project ID**, and click **Continue**.
6. Keep **Google Analytics** enabled (for future changes in the project) and click **Continue**.
7. Select **Default Account for Firebase** as the **Google Analytics** account and click **Create
   Project**.

**Step 2. Register the app with your Firebase project:**

1. Go to the [Firebase console](https://console.firebase.google.com) and select your newly created
   project.
2. In the center of the project overview page, click the **Android icon** or **Add app** to launch
   the setup workflow.
3. Enter the package name `com.goofygoobers.geoart` in the **Android package name** field.
4. Click **Register app**
5. Click **Download google-services.json** and move the downloaded `google-services.json` file into
   the `app/` directory.
6. You can now exit out of the **Add Firebase to your Android app** page as the Firebase SDK has
   already been added to the app.

**Step 3. Enabled the Firebase services:**

1. Go to the [Firebase console](https://console.firebase.google.com) and select your newly created
   project.
2. On the left hand side, click on **Build** then **Authentication**.
3. Click on **Get started** to setup Authentication.
4. Click on **Sign-in method** then **Email/Password**.
5. Enabled **Email/Password** then click **Save**.
6. Go back the the **Build** menu and select **Firestore Database**.
7. Click on **Create database** to setup Firestore.
8. Select **Start in production mode** and click **Next**.
9. Select the location closest to you, in our case **australia-southeast1 (Sydney)** and click
   **Enable**.
10. Go back the the **Build** menu and select **Storage**.
11. Click on **Get started** to setup Storage.
12. Select **Start in production mode** and click **Next**.
13. If the location has not been already set refer to step 3.9 to set the location, and click
    **Done**.

**Step 4. Configure the Firebase project:**

1. To configure the Firebase project with our security rules and indexes we need to use the Firebase
   CLI. This requires **Node.js** to be installed. Please install **Node.js** by following
   the [official Node.js documentation](https://nodejs.org/en/download)
2. Run the following gradle task in the terminal with your project ID from Step 1.6

    ```shell
    .\gradlew configureFirebase -PprojectId=yourProjectId
    ```

3. When your browser opens requesting permission for **Firebase CLI** to connect to your account,
   click **Allow**.

Congratulations your Firebase project and APIs are setup and configured and you're now ready to
build and run the app.

### Building

1. If you haven't done so already, please open the project in Android Studio.
2. Next you need to setup an Android device to run the app. This can be a physical device connected
   to your computer or an emulator. To setup the device go to the **Tools** menu and select
   **Device Manager**. Please note that the emulators do not support AR.
3. For an emulator select the **Virtual** tab and click **Create Device**.
4. Select **Phone** and **Pixel 7 Pro** and click **Next**.
5. Download the **API 34** image and click **Next** then click **Finish**.
6. If you want to test with a physical device select the **Physical** tab and click
   **Pair using Wi-fi** or connect your device to your computer.
7. Select the `App` configuration and run. The first time you do this it may take several minutes as
   all the build dependencies will be downloaded automatically.

### Testing

To test the program we are going to use the
[Firebase CLI](https://firebase.google.com/docs/emulator-suite/install_and_configure)
you downloaded earlier to emulate the Firestore database, Storage and Authentication.

The following Android Studio configurations can be run to test the app:

- The Android tests can be run with the `Android Tests` configuration.
- The Unit tests can be run by running the `Unit Tests` configuration.
- The Android Studio linter can be run with the `Lint` configuration.

### Style

The code style of the project is enforced using [ktlint](https://pinterest.github.io/ktlint/1.0.0/),
which is a style checker and formatting adhering to the
[Kotlin coding conventions](https://kotlinlang.org/docs/reference/coding-conventions.html)
and [Android Kotlin Style Guide](https://android.github.io/kotlin-guides/style.html).

The following Android Studio configurations can be run to perform various style checks and format
the code:

- The code style can be checked by `ktlint` with the `Style` configuration.
- The code can be formatted by `ktlint` with the `Format` configuration.

## Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Core UI library.
- [Material Design 3](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary) -
  UI theme and design.
- [Firebase](https://github.com/firebase/firebase-android-sdk) - Provides serverless backend
  (Firestore, storage, and authentication).
- [AR Core](https://github.com/google-ar/arcore-android-sdk) - Provides AR capabilities.
- [SceneView](https://github.com/SceneView/sceneview-android) - Integrates AR Core with Jetpack
  Compose and provides open source sample assets for demo purposes.
- [Google Play Services](https://developers.google.com/android/guides/overview) - Used for Google
  Maps, and location services.
- [Google Maps Compose](https://github.com/googlemaps/android-maps-compose) - Integrates Google Maps
  with Jetpack Compose.
- [GeoFirestore](https://github.com/imperiumlabs/GeoFirestore-Android) - Provides realtime
  geo-queries using Firebase Firestore.
- [Hilt](https://github.com/google/dagger) - Provides dependency injection.
- [Coil](https://github.com/coil-kt/coil) - Async image loading and displaying.
- [Chaquopy](https://chaquo.com/chaquopy/doc/15.0/android.html) - Used for Python interop with
  Kotlin.
- [pygltflib](https://gitlab.com/dodgyville/pygltflib) - Python package used to convert 2D images to
  3D models.
- [ktlint](https://pinterest.github.io/ktlint/1.0.0/) - Code style checker and formatter.
- [Github Copilot](https://github.com/features/copilot) - Aided in writing the comments for code documentation.
- [ChatGPT](https://chat.openai.com/) - Aided in creating the terms and conditions and privacy policy.
  Please see the appendix of the report for the full disclosure with prompts.

## Code References

- [1] R. Elizarov and V. Tolstopyatov, "Serializing 3rd Party Classes," Kotlin, 11 August 2020. [Online]. Available: <https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serializers.md#serializing-3rd-party-classes>. [Accessed 26 September 2023].
- [2] "Retrieving File Information," Android Developers, 27 October 2021. [Online]. Available: <https://developer.android.com/training/secure-file-sharing/retrieve-info>. [Accessed 15 September 2023].
- [3] The Android Open Source Project, "LocationUpdateScreen.kt," Android, 16 May 2023. [Online]. Available: <https://github.com/android/platform-samples/blob/main/samples/location/src/main/java/com/example/platform/location/locationupdates/LocationUpdatesScreen.kt>. [Accessed 10 September 2023].
- [4] E. Yulianto, "Geocoder - getFromLocation() Deprecated," Stackoverflow, 25 October 2022. [Online]. Available: <https://stackoverflow.com/a/74160903>. [Accessed 11 October 2023].
- [5] G. Mayani and sergiuteaca, "What ratio scales do Google Maps zoom levels correspond to?," Geographic Information Systems Stack Exchange, 19 August 2020. [Online]. Available: <https://gis.stackexchange.com/a/127949>. [Accessed 10 September 2023].
- [6] B. Hoffmann, "android:autoSizeTextType in Jetpack Compose," Stackoverflow, 7 July 2021. [Online]. Available: <https://stackoverflow.com/a/66090448>. [Accessed 16 October 2023].
- [7] Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: <https://github.com/kahdichienja/JetMapCompose>
- [8] Blizl, “Blizl/sceneview-android,” 21 September 2023. [Online]. Available: <https://github.com/Blizl/sceneview-android/tree/blizl/ecommerce-compose-mvvm-app>.
- [9] GitHub, OpenAI, "Github Copilot," 2023. [Online]. Available: <https://github.com/features/copilot>. [Accessed 18 October 2023].
- [10] OpenAI, "ChatGPT," 2023. [Online]. Available: <https://chat.openai.com/>. [Accessed 15 October 2023].
- [11] Sceneview, "Assets," 2023. [Online]. Available: <https://sceneview.github.io/assets/>. [Accessed 10 October 2023].
- [12] Taochok, "How to Resize a Bitmap in Android?," Stackoverflow, 6 March 2015. [Online]. Available: https://stackoverflow.com/a/28893299. [Accessed 18 October 2023].
- [13] J. Sambells, "Decoding polylines from google maps directions api with java," 27 May 2010. [Online]. Available: https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java. [Accessed 2 October 2023].
