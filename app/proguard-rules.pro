# https://github.com/square/retrofit/issues/3751
# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response
# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Proto
-keep class tornaco.apps.shortx.core.proto** {*;}
# Exclude protobuf classes
-keep class com.google.protobuf.** { *; }

-keep class ** implements android.os.Parcelable { *; }
-keep class ** implements android.os.IInterface { *; }

-keep class de.robv.android.xposed.**{*;}
-keep class tornaco.apps.shortx.services.xposed.hooks.hook** {*;}

-keep,allowobfuscation @interface tornaco.apps.shortx.core.annotations.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep,allowobfuscation @interface tornaco.apps.shortx.core.annotations.DoNotStrip
-keep @tornaco.apps.shortx.core.annotations.DoNotStrip class * {*;}

-keepclasseswithmembers class * {
    @tornaco.apps.shortx.core.annotations.DoNotStrip <methods>;
}

-keepclasseswithmembers class * {
    @tornaco.apps.shortx.core.annotations.DoNotStrip <fields>;
}

-keepclasseswithmembers class * {
    @tornaco.apps.shortx.core.annotations.DoNotStrip <init>(...);
}


# This class implements IAccessibilityServiceClient.Stub from hidden api
-keepclassmembers class tornaco.apps.shortx.services.wm.UiAutomationManager {
   public *;
}

-keep class tornaco.apps.shortx.ui.addrule.action.model.** { *; }
-keep class tornaco.apps.shortx.ui.addrule.condition.model.** { *; }
-keep class tornaco.apps.shortx.ui.addrule.fact.model.** { *; }



# java.lang.VerifyError: Verifier rejected class t7.e0: void t7.e0.b(int, int[])
# failed to verify: void t7.e0.b(int, int[]): [0x7] register v5 has type Precise Reference:
# int[] but expected Integer (declaration of 't7.e0' appears in Anonymous-DexFile@3310950862)
-keep class com.squareup.** { *; }

# Keep, just incase.
-keep class com.topjohnwu.superuser.** { *; }

# Keep thanox core.
-keep class github.tornaco.android.thanos.core** {*;}

# Used for mevl
-keep class org.jsoup** {*;}

# Keep all for Rhino JS
-keep class com.faendir.rhino_android** {*;}
-keep class org.mozilla** {*;}

# Code Editor
-keep class io.github.rosemoe** {*;}
-keep class org.eclipse** {*;}
-keep class org.joni** {*;}

-keep class tornaco.apps.shortx.services.util.http.persistentcookiejar** {*;}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# https://github.com/thegrizzlylabs/sardine-android/issues/70
-dontwarn org.xmlpull.v1.**
-keep class org.xmlpull.v1.** { *; }
-dontwarn android.content.res.XmlResourceParser

# Plugin
-keep class shortx.plugin.** { *; }

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn android.accessibilityservice.IAccessibilityServiceClient$Stub
-dontwarn android.accessibilityservice.IAccessibilityServiceClient
-dontwarn android.accessibilityservice.IAccessibilityServiceConnection
-dontwarn android.app.ActivityThread
-dontwarn android.app.UiAutomationConnection
-dontwarn android.content.IOnPrimaryClipChangedListener$Stub
-dontwarn android.content.IOnPrimaryClipChangedListener
-dontwarn android.content.pm.IPackageManager$Stub
-dontwarn android.content.pm.IPackageManager
-dontwarn android.content.pm.PackageParser$PackageLite
-dontwarn android.content.pm.PackageParser$PackageParserException
-dontwarn android.content.pm.PackageParser
-dontwarn android.content.pm.ParceledListSlice
-dontwarn android.content.pm.SuspendDialogInfo
-dontwarn android.content.pm.UserInfo
-dontwarn android.net.TetheringManager$StartTetheringCallback
-dontwarn android.net.TetheringManager$TetheringRequest$Builder
-dontwarn android.net.TetheringManager$TetheringRequest
-dontwarn android.net.TetheringManager
-dontwarn android.os.ServiceManager
-dontwarn android.util.Slog
-dontwarn android.view.IRotationWatcher$Stub
-dontwarn android.view.IRotationWatcher
-dontwarn android.view.accessibility.AccessibilityInteractionClient
-dontwarn com.android.internal.annotations.GuardedBy
-dontwarn com.android.internal.annotations.VisibleForTesting
-dontwarn com.android.internal.appwidget.IAppWidgetHost
-dontwarn com.android.internal.appwidget.IAppWidgetService$Stub
-dontwarn com.android.internal.appwidget.IAppWidgetService
-dontwarn com.android.internal.inputmethod.InputConnectionCommandHeader
-dontwarn com.android.internal.view.IInputContext
-dontwarn de.robv.android.xposed.IXposedHookLoadPackage
-dontwarn de.robv.android.xposed.IXposedHookZygoteInit$StartupParam
-dontwarn de.robv.android.xposed.IXposedHookZygoteInit
-dontwarn de.robv.android.xposed.XC_MethodHook$MethodHookParam
-dontwarn de.robv.android.xposed.XC_MethodHook
-dontwarn de.robv.android.xposed.XC_MethodHook$Unhook
-dontwarn de.robv.android.xposed.XposedBridge
-dontwarn de.robv.android.xposed.XposedHelpers
-dontwarn de.robv.android.xposed.callbacks.XC_LoadPackage$LoadPackageParam
-dontwarn javax.script.AbstractScriptEngine
-dontwarn javax.script.Compilable
-dontwarn javax.script.ScriptEngine
-dontwarn javax.script.ScriptEngineFactory
-dontwarn android.app.usage.IUsageStatsManager$Stub
-dontwarn android.app.usage.IUsageStatsManager
-dontwarn android.app.ActivityManagerInternal
-dontwarn android.app.IActivityManager$Stub
-dontwarn android.app.IActivityManager
-dontwarn android.net.INetworkScoreCache
-dontwarn android.net.NetworkKey
-dontwarn android.net.NetworkScoreManager
-dontwarn android.net.ScoredNetwork
-dontwarn android.net.wifi.WifiNetworkScoreCache$CacheListener
-dontwarn android.net.wifi.WifiNetworkScoreCache
-dontwarn android.app.INotificationManager$Stub
-dontwarn android.app.INotificationManager
-dontwarn android.app.ActivityTaskManager
-dontwarn android.app.IActivityTaskManager
-dontwarn android.media.projection.IMediaProjection$Stub
-dontwarn android.media.projection.IMediaProjection
-dontwarn android.media.projection.IMediaProjectionManager$Stub
-dontwarn android.media.projection.IMediaProjectionManager
-dontwarn android.util.MathUtils
-dontwarn android.bluetooth.BluetoothA2dpSink
-dontwarn android.bluetooth.BluetoothHeadsetClient
-dontwarn android.bluetooth.BluetoothHidHost
-dontwarn android.bluetooth.BluetoothMap
-dontwarn android.bluetooth.BluetoothMapClient
-dontwarn android.bluetooth.BluetoothPan
-dontwarn android.bluetooth.BluetoothPbapClient
-dontwarn android.bluetooth.BluetoothUuid
-dontwarn android.os.SystemProperties
-dontwarn android.view.VelocityTracker$Estimator
-dontwarn android.view.WindowManagerPolicyConstants$PointerEventListener
-dontwarn android.provider.Settings$Config
-dontwarn android.view.IInputFilter$Stub
-dontwarn android.view.IInputFilterHost
-dontwarn android.os.ShellCommand
-dontwarn android.view.ISystemGestureExclusionListener$Stub
-dontwarn android.view.ISystemGestureExclusionListener
-dontwarn android.view.IWindowManager
-dontwarn android.view.WindowManagerGlobal
-dontwarn android.view.IWindowManager$Stub
-dontwarn com.android.internal.inputmethod.IRemoteInputConnection
-dontwarn android.net.wifi.WifiManager$SoftApCallback
-dontwarn android.os.HandlerExecutor
-dontwarn android.net.wifi.WifiManager$ActionListener
-dontwarn org.jspecify.annotations.NullMarked
-dontwarn android.app.IUidObserver$Stub
-dontwarn android.app.IUidObserver
-dontwarn android.app.ITaskStackListener
-dontwarn android.app.TaskStackListener
-dontwarn android.hardware.input.InputManagerGlobal
-dontwarn com.android.internal.os.BaseCommand

# Inroduced by a lib update, not sure if it 55d1baecdbfe02c40a147d696d8a805f43906890
-dontwarn java.beans.BeanDescriptor
-dontwarn java.beans.BeanInfo
-dontwarn java.beans.IntrospectionException
-dontwarn java.beans.Introspector
-dontwarn java.beans.PropertyDescriptor
-dontwarn javax.lang.model.SourceVersion
-dontwarn java.beans.FeatureDescriptor
-dontwarn android.app.AndroidAppHelper
-dontwarn android.util.IntArray
-dontwarn android.util.LongSparseLongArray
-dontwarn android.util.TimingsTraceLog
-dontwarn android.view.InputChannel
-dontwarn android.view.InputEventReceiver
-dontwarn android.view.InputMonitor
-dontwarn com.android.internal.util.Preconditions
-dontwarn com.android.internal.util.ToBooleanFunction
-dontwarn android.app.ActivityClient
-dontwarn com.android.internal.widget.LinearLayoutManager
-dontwarn com.android.internal.widget.RecyclerView$Adapter
-dontwarn com.android.internal.widget.RecyclerView$LayoutManager
-dontwarn com.android.internal.widget.RecyclerView

-dontwarn com.android.internal.telephony.ITelephony$Stub
-dontwarn com.android.internal.telephony.ITelephony
-dontwarn com.google.auto.value.AutoValue$Builder
-dontwarn com.google.auto.value.AutoValue
-dontwarn com.android.internal.statusbar.IStatusBar