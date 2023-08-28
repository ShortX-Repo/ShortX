import tornaco.project.android.shortx.Configs.sxIsDebugBuild
import tornaco.project.android.shortx.Configs.sxPackageName
import tornaco.project.android.shortx.Configs.sxVersionCode
import tornaco.project.android.shortx.Configs.sxVersionName
import tornaco.project.android.shortx.Configs.sxVersionNameSuffix
import tornaco.project.android.shortx.addAidlTask
import java.util.UUID

plugins {
    alias(libs.plugins.gmazzo.buildconfig)

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.protobuf.gradle.plugin)
    alias(libs.plugins.kover)
}

buildConfig {
    packageName("${sxPackageName}.core")
    className("BuildProp")
    useKotlinOutput { internalVisibility = false }

    buildConfigField("String", "APP_PACKAGE_NAME", provider { "\"${sxPackageName}\"" })
    buildConfigField("String", "APP_VERSION_NAME", provider { "\"${sxVersionName}\"" })
    buildConfigField("String", "APP_VERSION_NAME_SUFFIX", provider { "\"${sxVersionNameSuffix}\"" })
    buildConfigField("int", "APP_VERSION_CODE", provider { "$sxVersionCode" })
    buildConfigField("boolean", "APP_IS_DEBUG", provider { "$sxIsDebugBuild" })
    buildConfigField("long", "BUILD_TIME", "${System.currentTimeMillis()}L")
    buildConfigField("String", "BUILD_FINGERPRINT", "\"${UUID.randomUUID()}\"")
    buildConfigField("String", "TG_GROUP", "\"https://t.me/shortxmod\"")
    buildConfigField("String", "WIKI_URL", "\"https://shortx-repo.github.io/ShortX-Pages/zh/\"")
    buildConfigField(
        "String",
        "WIKI_URL_MVEL",
        "\"https://shortx-repo.github.io/ShortX-Pages/zh/2023/01/05/shortx-usefull-mvel.html\""
    )

    buildConfigField(
        "String",
        "APP_SHELL_SERVICE_NAME",
        "\"tornaco.apps.shortx.ui.service.AppShellService\""
    )
    buildConfigField("String", "DEFAULT_OVERLAY_BUTTON_ICON", "\"\"")
    buildConfigField(
        "String",
        "OPEN_SOURCE_LICENSE_URL",
        "\"https://raw.githubusercontent.com/ShortX-Repo/ShortX-Release/main/open_source_license.md\""
    )
    buildConfigField(
        "String",
        "PLAY_PUBLIC_KEY",
        "\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmRCHlj+Ht08vWkDq77KiPNjwqsXhxvOJr8mb0yZ0DTMKFBlE6Jhzb1EhUu4AQBq0LnRSvWoQBnQwvAijTSRiiuOcAHhvyEXZh4k7kTh/uQQRVaODsbWTxj3d+Yu8m6FWgAaHrzoyelozOkxOX5j8NC4k2JtwOXtNZ6vr2qhmONfL/cC00jXvfbgLU5qMp9rEe/W6a/8rRHUnbikQFu8d5zfGVpnbGCcl18ttnFmsOaE+t5Uyk5+bNMlYSYfDcgia9shJGvIASSvwjxWo3UMR/EMN9IbzSeJpzBlUyS/b4BYCADo9TGpP3UDKZeRFKkuJnCAOHNeWui+kN3/pRyBFmwIDAQAB\""
    )

    buildConfigField("String", "API_BASE_URL", "\"http://shortx.emui.tech/api/\"")
    buildConfigField("String", "SHORTX_GITHIB_REPO_URL", "\"https://github.com/ShortX-Repo\"")
    buildConfigField(
        "String",
        "SHORTX_DISCOVERY_DEFAULT_ONLINE_REPO_URL",
        "\"https://raw.githubusercontent.com/ShortX-Repo/Files/main/index.json\""
    )

    buildConfigField("String", "SIGNATURES", "\"A92E8A9A743FE6648E2E4743FDAC89E9EB4A568F\"")
    buildConfigField("String", "SIGNATURES_MD5", "\"25cc0926b09a6e73798de05977c420f7\"")
    // Play.
    buildConfigField("String", "SIGNATURES2", "\"66999CBE10A8E546E869F16B68C356280FA60BE6\"")
    buildConfigField("String", "SIGNATURES2_MD5", "\"77d65c17c9cda7b74551a5699969cbce\"")
}

dependencies {
    compileOnly(files("${rootProject.rootDir.path}/android-sdk/31/android-31.jar"))
    api(libs.protobuf.java)
    api(libs.protobuf.java.util)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.stdlib.jdk8)

    implementation(libs.kotlin.reflect)

    implementation(project(":external:utils"))
}

addAidlTask()

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.24.2"
    }
}
