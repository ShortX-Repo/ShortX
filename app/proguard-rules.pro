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


-keep class tornaco.apps.shortx.services.xposed.hooks.hook** {*;}

# Proto
-keep class tornaco.apps.shortx.core.proto** {*;}
# Exclude protobuf classes
-keep class com.google.protobuf.** { *; }

-keep class ** implements android.os.Parcelable { *; }


-keep,allowobfuscation @interface tornaco.apps.shortx.core.annotations.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @tornaco.apps.shortx.core.annotations.DoNotStrip class *
-keepclassmembers class * {
    @tornaco.apps.shortx.core.annotations.DoNotStrip *;
}