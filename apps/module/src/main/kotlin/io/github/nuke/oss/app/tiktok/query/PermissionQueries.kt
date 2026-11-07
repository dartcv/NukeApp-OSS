package io.github.nuke.oss.app.tiktok.query

import io.github.nuke.oss.app.shared.dex.DexQueries
import io.github.nuke.oss.app.shared.dex.method

// Dex queries for the notification-permission prompts, taken from the legacy module and located the same way as
// FeedQueries.
@DexQueries
internal object PermissionQueries {
    // The loader of the push-permission guide: it logs its scene and the "notification has enable" check, and reports
    // back through the listener it is given.
    val LOAD_GUIDE = method {
        matcher {
            returnType = "void"
            paramTypes(
                "com.ss.android.ugc.awemepushapi.PermissionScene", "com.ss.android.ugc.awemepushapi.PermissionType",
                "com.ss.android.ugc.awemepushapi.IPushPermissionUIGuideLoadListener", "org.json.JSONObject",
                "org.json.JSONObject", "java.lang.String",
            )
            usingEqStrings("tryLoadPushPermissionGuideData， permissionScene=", "notification has enable")
        }
    }

    // The custom alert shown before the system dialog. 40.7.0 declares it in PermissionBootServiceProvider under its
    // own name with eight parameters; 41.0.0 moved it to an obfuscated class and method with seven. Its log line is
    // the one stable identity, and the callback parameter is found by type where the feature installs it.
    val GOOGLE_ALERT = method(version = 2) {
        matcher {
            returnType = "void"
            usingEqStrings("[requestNotificationPermissionByGoogleAlert]customDialogView:")
        }
    }
}
