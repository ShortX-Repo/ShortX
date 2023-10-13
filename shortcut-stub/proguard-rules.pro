# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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



-keep class ** implements android.os.Parcelable { *; }
-keep class ** implements android.os.IInterface { *; }

-dontwarn android.content.pm.ParceledListSlice
-dontwarn android.os.ServiceManager
-dontwarn com.android.internal.appwidget.IAppWidgetHost
-dontwarn com.android.internal.appwidget.IAppWidgetService$Stub
-dontwarn com.android.internal.appwidget.IAppWidgetService
-dontwarn android.content.pm.UserInfo
-dontwarn com.google.protobuf.AbstractMessageLite$Builder
-dontwarn com.google.protobuf.AbstractParser
-dontwarn com.google.protobuf.ByteString
-dontwarn com.google.protobuf.Descriptors$Descriptor
-dontwarn com.google.protobuf.Descriptors$FileDescriptor
-dontwarn com.google.protobuf.GeneratedMessageV3$Builder
-dontwarn com.google.protobuf.GeneratedMessageV3$FieldAccessorTable
-dontwarn com.google.protobuf.GeneratedMessageV3
-dontwarn com.google.protobuf.LazyStringArrayList
-dontwarn com.google.protobuf.LazyStringList
-dontwarn com.google.protobuf.Message
-dontwarn com.google.protobuf.MessageOrBuilder
-dontwarn com.google.protobuf.Parser
-dontwarn com.google.protobuf.ProtocolStringList
-dontwarn com.google.protobuf.UninitializedMessageException
-dontwarn com.google.protobuf.UnknownFieldSet
-dontwarn com.jaredrummler.ktsh.Shell$Command$Config$Builder
-dontwarn com.jaredrummler.ktsh.Shell$Command$Config
-dontwarn com.jaredrummler.ktsh.Shell$Command$Result$Details
-dontwarn com.jaredrummler.ktsh.Shell$Command$Result
-dontwarn com.jaredrummler.ktsh.Shell$Companion
-dontwarn com.jaredrummler.ktsh.Shell$Timeout
-dontwarn com.jaredrummler.ktsh.Shell
-dontwarn com.topjohnwu.superuser.Shell$Builder
-dontwarn com.topjohnwu.superuser.Shell$Job
-dontwarn com.topjohnwu.superuser.Shell$Result
-dontwarn com.topjohnwu.superuser.Shell$ResultCallback
-dontwarn com.topjohnwu.superuser.Shell
-dontwarn eu.chainfire.libsuperuser.Debug$OnLogListener
-dontwarn eu.chainfire.libsuperuser.Debug
-dontwarn eu.chainfire.libsuperuser.Shell$Pool
-dontwarn eu.chainfire.libsuperuser.Shell$PoolWrapper
-dontwarn eu.chainfire.libsuperuser.Shell$SU
-dontwarn kotlinx.coroutines.BuildersKt
-dontwarn kotlinx.coroutines.CoroutineDispatcher
-dontwarn kotlinx.coroutines.Dispatchers