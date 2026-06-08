# ================================
# GENERAL CONFIGURATION
# ================================
#-dontshrink
-keepattributes *Annotation*,InnerClasses,EnclosingMethod,Signature
-keepattributes SourceFile,LineNumberTable

# ================================
# KOTLIN
# ================================
-keep,allowoptimization,allowshrinking class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-keepclassmembers class ** {
    ** MODULE$;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkNotNull(...);
    static void checkParameterIsNotNull(...);
    static void checkNotNullParameter(...);
    static void checkExpressionValueIsNotNull(...);
}

# ================================
# KOTLINX SERIALIZATION
# ================================
-keepclassmembers class * {
    *** Companion;
}
-keep class kotlinx.serialization.** { *; }
-keep class ** extends kotlinx.serialization.KSerializer { *; }
-keepclassmembers class **SerializersKt {
    *** get*Serializer(...);
}
-keepclasseswithmembers class * implements kotlinx.serialization.Serializable {
    <methods>;
}

# ================================
# KOIN DEPENDENCY INJECTION
# ================================
-keep,allowshrinking,allowoptimization class org.koin.** { *; }
-keep class * extends org.koin.core.module.Module { *; }
-keep class * implements org.koin.core.qualifier.Qualifier { *; }
-keepclasseswithmembers class * {
    @org.koin.core.annotation.* <methods>;
}
-keepclassmembers class ** {
    @org.koin.core.annotation.* <methods>;
}
-keep @interface org.koin.core.annotation.*

# ================================
# JETPACK COMPOSE
# ================================
-keep,allowoptimization,allowshrinking class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }
-keepclasseswithmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-keepclassmembers class ** {
    @androidx.compose.runtime.Composable <methods>;
}

# ================================
# FIREBASE
# ================================
-keep,allowoptimization,allowshrinking class com.google.firebase.** { *; }
-keep,allowoptimization,allowshrinking interface com.google.firebase.** { *; }
-keep,allowoptimization,allowshrinking class com.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keep interface com.google.android.gms.** { *; }
-keepclassmembers class ** {
    @com.google.firebase.crashlytics.ktx.FirebaseTracingTask <methods>;
}

# ================================
# ANDROIDX NAVIGATION 3
# ================================
-keep class androidx.navigation.** { *; }
-keep interface androidx.navigation.** { *; }
-keep class * extends androidx.navigation.NavBackStackEntry { *; }

# ================================
# ANDROIDX LIFECYCLE & VIEWMODEL
# ================================
-keep class androidx.lifecycle.** { *; }
-keep interface androidx.lifecycle.** { *; }
-keepclassmembers class ** extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ================================
# ANDROIDX DATASTORE & ROOM
# ================================
-keep class androidx.datastore.** { *; }
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Database class * { *; }
-keepclassmembers @androidx.room.Entity class * { *; }
-keepclassmembers @androidx.room.Dao class * { *; }

# ================================
# KTOR CLIENT
# ================================
-keep class io.ktor.client.** { *; }
-keep interface io.ktor.client.** { *; }
-keep class io.ktor.** { *; }
-keep class io.ktor.http.** { *; }
-keep class io.ktor.utils.** { *; }
-keep class io.ktor.util.debug.** { *; }

# ================================
# JAVA MANAGEMENT & LANG API
# ================================
-keep class java.lang.management.** { *; }
-keep interface java.lang.management.** { *; }
-keep class java.lang.reflect.** { *; }
-keep interface java.lang.reflect.** { *; }
-dontwarn java.lang.management.**

# ================================
# OKHTTP & NETWORKING
# ================================
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class java.net.** { *; }
-keep interface java.net.** { *; }
-dontwarn okhttp3.**
-dontwarn javax.annotation.**

# ================================
# SLF4J & LOGBACK
# ================================
-keep class org.slf4j.** { *; }
-keep interface org.slf4j.** { *; }
-keep class ch.qos.logback.** { *; }
-keep interface ch.qos.logback.** { *; }

# ================================
# REFLECTION & LAMBDAS
# ================================
-keepclasseswithmembers class * {
    public static <clinit>();
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ================================
# CALLBACKS & EVENT HANDLERS
# ================================
-keepclasseswithmembers class * {
    <init>(android.content.Context);
    <init>(android.content.Context, android.util.AttributeSet);
    <init>(android.content.Context, android.util.AttributeSet, int);
    public void on*(...);
    public boolean on*(...);
}

# ================================
# NATIVE METHODS
# ================================
-keepclasseswithmembers class * {
    native <methods>;
}

# ================================
# APPLICATION LAYER (Project Specific)
# ================================
-keep,allowshrinking,allowoptimization class ru.kima.sonar.** { *; }
-keep,allowshrinking,allowoptimization class ru.kima.sonar.common.** { *; }
-keep,allowshrinking,allowoptimization interface ru.kima.sonar.** { *; }
-keep,allowshrinking,allowoptimization @interface ru.kima.sonar.** { *; }
-keepclasseswithmembers class ru.kima.sonar.** {
    public <init>(...);
}

# ================================
# ANNOTATIONS
# ================================
-keepattributes *Annotation*
-keep class java.lang.Deprecated { *; }
-keepclassmembers class ** {
    @java.lang.Override <methods>;
}

# ================================
# PRESERVING DEBUG INFO
# ================================
-renamesourcefileattribute SourceFile

# ================================
# WARNINGS SUPPRESSION
# ================================
-dontwarn sun.misc.**
-dontwarn android.content.pm.**
-dontwarn java.nio.file.**
-dontwarn java.lang.invoke.**
