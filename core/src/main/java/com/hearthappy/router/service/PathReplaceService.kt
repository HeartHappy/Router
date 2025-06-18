package com.hearthappy.router.service

import android.net.Uri

interface PathReplaceService {
    /**
     * For normal path.
     *
     * @param path raw path
     */
    fun forString(path: String): String

    /**
     * For uri type.
     *
     * @param uri raw uri
     */
    fun forUri(uri: Uri): Uri
}