package com.hearthappy.route.service

import android.net.Uri
import com.hearthappy.common_api.RouterPath
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.service.PathReplaceService

/**
 * Created Date: 2025/6/19
 * @author ChenRui
 * ClassDescription：Release the comment code and verify the Path replacement service
 *
 *
 */
@Route(RouterPath.SERVICE_PATH_REPLACE) class PathReplaceImpl : PathReplaceService {
    override fun forString(path: String): String {
        //        if (path == RouterPath.USER_PROFILE_ACTIVITY) {
        //            return RouterPath.MODEL2_UI
        //        }
        return path
    }

    /**
     * test uri：hearthappy://kotlin.ksp.com/model2/ui?name=KSP Router!&age=18
     * @param uri Uri
     * @return Uri
     */
    override fun forUri(uri: Uri): Uri {
        //        if (uri.path == RouterPath.MODEL2_UI) {
        //            return uri.buildUpon().apply {
        //                clearQuery()
        //                appendQueryParameter("name", "KSP Router!")
        //                appendQueryParameter("age", "40")
        //            }.build()
        //        }
        return uri
    }
}