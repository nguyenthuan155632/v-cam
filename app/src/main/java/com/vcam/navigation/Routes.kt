package com.vcam.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Routes {
    const val Camera = "camera"
    const val FilterBrowser = "filters"
    const val PhotoPreview = "preview/{photoId}"
    const val Settings = "settings"

    fun photoPreview(photoId: String): String {
        val encoded = URLEncoder.encode(photoId, StandardCharsets.UTF_8.name())
        return "preview/$encoded"
    }
}
