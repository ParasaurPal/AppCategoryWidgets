plugins {
 id("com.android.application")
 id("org.jetbrains.kotlin.android")
}
android {
 namespace="com.parasaur.appcategorywidgets"
 compileSdk=35
 defaultConfig {
  applicationId="com.parasaur.appcategorywidgets"
  minSdk=26
  targetSdk=35
  versionCode = 6
  versionName = "1.3.1"
 }
 compileOptions {
  sourceCompatibility=JavaVersion.VERSION_17
  targetCompatibility=JavaVersion.VERSION_17
 }
 kotlinOptions {
  jvmTarget="17"
 }
}
dependencies { implementation("org.jsoup:jsoup:1.18.3") }
