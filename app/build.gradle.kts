plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.marurun66.postingapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.marurun66.postingapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //Commons IO - 파일 및 I/O 작업을 지원하는 유틸리티 라이브러리
    implementation("commons-io:commons-io:2.18.0")
    //Glide - 이미지 로딩 라이브러리
    implementation("com.github.bumptech.glide:glide:4.16.0")
    //Retrofit 라이브러리
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("androidx.cardview:cardview:1.0.0")

}