# Preserve Line Numbers for Crash Reporting (Firebase/Play Console)
-keepattributes SourceFile,LineNumberTable

# Kotlin Serialization (if used)
-keep class kotlinx.serialization.json.** { *; }

# Retrofit & OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Moshi
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.RoomDatabase { *; }

# Firebase
-keep class com.google.firebase.** { *; }

# Coil
-keep class coil.** { *; }

# Compose
-keep class androidx.compose.** { *; }

# Project specific: Keep Data Entities
-keep class com.example.data.local.** { *; }
-keep class com.example.data.remote.** { *; }
