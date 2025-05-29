# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 基本保留规则
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View

# 保留注解
-keepattributes *Annotation*, Signature, InnerClasses

# 保留R资源
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 保留Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepclasseswithmembers class * {
    @com.google.errorprone.annotations.* <methods>;
    @javax.annotation.* <methods>;
    @javax.annotation.concurrent.* <methods>;
}

# 保留 Google HTTP Client 相关类
-keep class com.google.api.client.http.** { *; }
-keep class com.google.api.client.util.** { *; }
-keep class com.google.api.client.json.** { *; }

# 保留 Joda-Time 相关类
-keep class org.joda.time.** { *; }

# 保留 Tink 的 KeysDownloader 功能
-keep class com.google.crypto.tink.util.KeysDownloader { *; }
-keep class com.google.crypto.tink.util.KeysDownloader$** { *; }

# 保留所有注解
-keepattributes *Annotation*

# 保留序列化相关的类和方法
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 完全移除 KeysDownloader 相关类
-assumenosideeffects class com.google.crypto.tink.util.KeysDownloader { *; }
-dontwarn com.google.crypto.tink.util.KeysDownloader
-dontwarn com.google.api.client.http.**
-dontwarn org.joda.time.**

# 保留 Tink 核心功能
-keep class com.google.crypto.tink.** { *; }
-dontwarn com.google.crypto.tink.**

# 处理注解问题
-keepattributes *Annotation*, Signature
-keep class com.google.errorprone.annotations.** { *; }
-keep class javax.annotation.** { *; }
-keep class javax.annotation.concurrent.** { *; }
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**

# 保留数据类和接口实现
-keep class * implements com.google.crypto.tink.proto.KeyData
-keep class * implements com.google.crypto.tink.shaded.protobuf.MessageLite