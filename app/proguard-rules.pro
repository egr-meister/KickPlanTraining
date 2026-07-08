# ---------------------------------------------------------------------------
# KickPlan Training - R8 / ProGuard rules
# Keep kotlinx.serialization generated serializers and model classes so that
# release (minified) builds never fail to (de)serialize DataStore JSON or the
# football-data.org API response.
# ---------------------------------------------------------------------------

# Keep Kotlin metadata (needed by reflection-free serialization runtime).
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

# kotlinx.serialization core keep rules.
-keepclassmembers class **$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclasseswithmembers class **$$serializer {
    *** descriptor;
}
-keepclassmembers class ** {
    *** Companion;
}

# Keep all @Serializable classes (models + DTOs) and their generated serializers.
-keep,includedescriptorclasses class com.kickplan.training.**$$serializer { *; }
-keep @kotlinx.serialization.Serializable class com.kickplan.training.** { *; }
-keepclassmembers @kotlinx.serialization.Serializable class com.kickplan.training.** {
    *** *;
}

# Retrofit / OkHttp standard keep rules.
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Kotlin coroutines.
-dontwarn kotlinx.coroutines.**

# Compose is handled by the AGP-provided default rules; nothing extra needed.
