# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name in stack traces
-renamesourcefileattribute SourceFile

# Keep generic signatures (needed for Kotlin)
-keepattributes Signature

# Keep annotations
-keepattributes *Annotation*

# ==================== KOTLIN ====================
-dontwarn kotlin.**
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }

# ==================== COROUTINES ====================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ==================== ROOM ====================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ==================== KOIN ====================
-keepnames class * extends android.app.Application
-keep class org.koin.** { *; }
-keep class org.koin.core.** { *; }
-keep class org.koin.dsl.** { *; }

# Keep Koin modules
-keep class com.aaltix.lotto.**.di.** { *; }

# ==================== COMPOSE ====================
-dontwarn androidx.compose.**

# Keep Compose compiler generated classes
-keep class androidx.compose.** { *; }

# ==================== APP SPECIFIC ====================
# Keep domain models
-keep class com.aaltix.lotto.core.domain.model.** { *; }

# Keep database entities
-keep class com.aaltix.lotto.core.database.entity.** { *; }

# Keep data DTOs
-keep class com.aaltix.lotto.core.data.dto.** { *; }

# Keep MVI contracts
-keep class com.aaltix.lotto.**.presentation.**Contract { *; }
-keep class com.aaltix.lotto.**.presentation.**Contract$* { *; }
