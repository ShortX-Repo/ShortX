import tornaco.project.android.shortx.Configs
import tornaco.project.android.shortx.Configs.sxPackageName
import tornaco.project.android.shortx.addAidlTask
import java.util.UUID

plugins {
    alias(libs.plugins.gmazzo.buildconfig)

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.protobuf.gradle.plugin)
    alias(libs.plugins.kover)
    id("shortxmvnpub")
}

buildConfig {
    packageName("${sxPackageName}.core")
    className("BuildPropInternal")
    useKotlinOutput { internalVisibility = false }

    buildConfigField("String", "APP_PACKAGE_NAME", provider { "\"${sxPackageName}\"" })
    buildConfigField("String", "APP_VERSION_NAME", provider { "\"${Configs.sxVersionName}\"" })
    buildConfigField(
        "String",
        "APP_VERSION_NAME_SUFFIX",
        provider { "\"${Configs.sxVersionNameSuffix}\"" })
    buildConfigField("int", "APP_VERSION_CODE", provider { "${Configs.sxVersionCode}" })
    buildConfigField("boolean", "APP_IS_DEBUG", provider { "${Configs.sxIsDebugBuild}" })
    buildConfigField("String", "BUILD_FLAVOR", provider { "\"${Configs.sxFlavor}\"" })
    buildConfigField("long", "BUILD_TIME", "${System.currentTimeMillis()}L")
    buildConfigField("String", "BUILD_FINGERPRINT", "\"${UUID.randomUUID()}\"")

    buildConfigField("String", "SHORTX_EXT_APP_PKG_NAME", "\"tornaco.apps.shortx.ext\"")

    buildConfigField("String", "CUSTOM_CONTEXT_MENU_CLASSNAME_BASE", "\"tornaco.apps.shortx.ui.contextmenu.CustomContextMenuActivity\"")

    buildConfigField(
        "String",
        "SHORTCUT_STUB_KEYSTORE_URL",
        "\"https://github.com/ShortX-Repo/ShortX-Files/raw/main/shortcut/keystore\""
    )
    buildConfigField(
        "String",
        "SHORTCUT_STUB_APK_URL",
        "\"https://github.com/ShortX-Repo/ShortX-Files/raw/main/shortcut/shortcut-stub.apk\""
    )

    buildConfigField(
        "String",
        "PLAY_PUBLIC_KEY",
        "\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmRCHlj+Ht08vWkDq77KiPNjwqsXhxvOJr8mb0yZ0DTMKFBlE6Jhzb1EhUu4AQBq0LnRSvWoQBnQwvAijTSRiiuOcAHhvyEXZh4k7kTh/uQQRVaODsbWTxj3d+Yu8m6FWgAaHrzoyelozOkxOX5j8NC4k2JtwOXtNZ6vr2qhmONfL/cC00jXvfbgLU5qMp9rEe/W6a/8rRHUnbikQFu8d5zfGVpnbGCcl18ttnFmsOaE+t5Uyk5+bNMlYSYfDcgia9shJGvIASSvwjxWo3UMR/EMN9IbzSeJpzBlUyS/b4BYCADo9TGpP3UDKZeRFKkuJnCAOHNeWui+kN3/pRyBFmwIDAQAB\""
    )

    buildConfigField("String", "SIGNATURES", "\"A92E8A9A743FE6648E2E4743FDAC89E9EB4A568F\"")
    buildConfigField("String", "SIGNATURES_MD5", "\"25cc0926b09a6e73798de05977c420f7\"")
    // Play.
    buildConfigField("String", "SIGNATURES2", "\"66999CBE10A8E546E869F16B68C356280FA60BE6\"")
    buildConfigField("String", "SIGNATURES2_MD5", "\"77d65c17c9cda7b74551a5699969cbce\"")

    buildConfigField("String", "API_BASE_URL", "\"http://shortx.emui.tech/api/\"")
    buildConfigField("String", "EXT_DOWNLOAD_URL", "\"https://github.com/ShortX-Repo/ShortX-Extensions/releases\"")
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    api(libs.protobuf.java)
    api(libs.protobuf.java.util)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.stdlib.jdk8)

    implementation(libs.kotlin.reflect)

    api(libs.retrofit)
    api(libs.converter.gson)
    api(libs.okhttp)
    api(libs.logging.interceptor)

    implementation(project(":core-api"))
    implementation(project(":external:utils"))
}

addAidlTask()

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:4.26.1"
    }
}
