-keep class tornaco.apps.shortx.services.xposed.hooks.hook** {*;}

# Proto
-keep class tornaco.apps.shortx.core.proto** {*;}
# Exclude protobuf classes
-keep class com.google.protobuf.** { *; }

-keep class ** implements android.os.Parcelable { *; }
-keep class ** implements android.os.IInterface { *; }


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