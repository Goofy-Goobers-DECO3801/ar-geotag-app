import org.gradle.internal.os.OperatingSystem
import java.io.ByteArrayInputStream
import java.nio.file.Paths

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.chaquo.python")
    id("org.jlleitschuh.gradle.ktlint")
    kotlin("plugin.serialization") version "1.9.0"
}

android {
    namespace = "com.example.deco3801"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.deco3801"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.example.deco3801.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

chaquopy {
    defaultConfig {
        pip {
            install("pygltflib")
        }
    }
}

val npx =
    if (OperatingSystem.current().isWindows) {
        "npx.cmd"
    } else {
        "npx"
    }

val npm =
    if (OperatingSystem.current().isWindows) {
        "npm.cmd"
    } else {
        "npm"
    }

val installFirebaseEmulators by tasks.registering {
    doLast {
        exec {
            commandLine(npx, "firebase", "setup:emulators:firestore")
        }
        exec {
            commandLine(npx, "firebase", "setup:emulators:storage")
        }
    }
}

installFirebaseEmulators {
    onlyIf {
        val emulatorDir =
            file(Paths.get(System.getProperty("user.home")).resolve(".cache/firebase/emulators"))
        !emulatorDir.exists()
    }
}

val startFirebaseEmulators by tasks.registering {
    dependsOn(installFirebaseEmulators)
    doLast {
        val process =
            ProcessBuilder()
                .directory(projectDir)
                .command(npx, "firebase", "emulators:start")
                .start()
        project.extensions.extraProperties.set("firebaseEmulators", process)
    }
}

val stopFirebaseEmulators by tasks.registering {
    doLast {
        // Retrieve the process reference from the watch task
        val process = project.extensions.extraProperties.get("firebaseEmulators") as? Process
        process?.destroy()
        exec { // Firebase doesnt cleanup nicely on its own...
            commandLine(npx, "kill-port", "4400", "8080", "9099", "9199", "9150")
        }
    }
}
val configureFirebase by tasks.registering {
    doLast {
        val projectId =
            properties["projectId"] as? String
                ?: error("Please rerun with the project id, ./gradlew configureFirebase -PprojectId=yourProjectId")

        println("Installing NPM packages...")
        exec {
            commandLine(npm, "install", "--no-fund", "--no-audit", "--loglevel", "error")
        }

        exec {
            commandLine(npx, "firebase", "login", "--interactive")
            standardInput = ByteArrayInputStream("n\n".toByteArray())
        }

        exec {
            commandLine(npx, "firebase", "use", "--add", projectId)
        }

        exec {
            commandLine(npx, "firebase", "deploy")
        }
    }
}

gradle.projectsEvaluated {
    tasks.getByName("preDebugAndroidTestBuild") {
        dependsOn(startFirebaseEmulators)
    }
    tasks.getByName("connectedDebugAndroidTest") {
        finalizedBy(stopFirebaseEmulators)
    }
}

dependencies {
    // Compose
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.navigation:navigation-compose:2.7.3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Theme
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.material:material-icons-extended")

    // Google services
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.maps.android:maps-compose:2.14.0")
    implementation("com.google.accompanist:accompanist-permissions:0.25.0")

    // AR dependencies
    implementation("com.google.ar:core:1.39.0")
    implementation("io.github.sceneview:arsceneview:0.10.2")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")

    // Saving objects as key-value pairs to app storage.
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Async Images for Compose
    implementation("io.coil-kt:coil:2.4.0")
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.github.imperiumlabs:GeoFirestore-Android:v1.5.0")

    // Hilt Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48.1")
    ksp("com.google.dagger:hilt-compiler:2.48.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("com.google.truth:truth:1.1.4")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48.1")
    kspAndroidTest("com.google.dagger:hilt-compiler:2.48.1")

    // Compose Testing
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    ktlintRuleset("io.nlopez.compose.rules:ktlint:0.3.0")
}

ktlint {
    version.set("1.0.0")
    android.set(true)
}
