-keep class dev.imhamid.moodlink.data.model.** { *; }

# kotlinx.serialization (Supabase rows are decoded through generated serializers)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class dev.imhamid.moodlink.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class dev.imhamid.moodlink.**$$serializer { *; }

# Ktor / OkHttp
-dontwarn org.slf4j.**
-dontwarn io.ktor.**
