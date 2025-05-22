plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.studycard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.studycard"
        minSdk = 24
        targetSdk = 34
        versionCode = 6
        versionName = "2.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // Основные зависимости
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.annotation)

    // Тестирование//
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Библиотеки сторонних разработчиков
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // Markwon для работы с Markdown
    implementation("io.noties.markwon:core:4.6.2") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }

    implementation("io.noties.markwon:html:4.6.2") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }
    implementation("io.noties.markwon:syntax-highlight:4.6.2") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }
    implementation("io.noties.markwon:ext-latex:4.6.2") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }
    implementation("io.noties.markwon:image:4.6.2") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }
    implementation("io.noties.markwon:image-glide:4.6.2") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }
    implementation("io.noties.markwon:image-picasso:4.6.2") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }

    implementation ("com.google.firebase:firebase-messaging:24.1.1")
    implementation ("com.google.firebase:firebase-analytics:22.4.0")

    implementation("com.yandex.android:mobileads:7.12.2")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("io.noties:prism4j:1.0.0") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }

    implementation("io.noties.markwon:recycler-table:4.6.2") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }
    
    implementation("io.noties.markwon:ext-latex:4.6.2") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }

    implementation("io.noties.markwon:ext-tables:4.6.2") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }


    implementation("org.jetbrains:annotations:24.0.0") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }

    implementation("com.google.android.gms:play-services-auth:20.7.0");


    // Стандартная тема для Prism4j, если она будет использоваться
    // implementation("io.noties:prism4j-theme-default:2.0.0") {
    //    exclude(group = "org.jetbrains", module = "annotations-java5")
    // }
}
