package com.vcam.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vcam.ui.camera.CameraScreen
import com.vcam.ui.filters.FilterBrowserScreen
import com.vcam.ui.preview.PhotoPreviewScreen
import com.vcam.ui.settings.SettingsScreen

@Composable
fun VCamNavGraph() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.Camera) {
        composable(Routes.Camera) {
            CameraScreen(
                onOpenSettings = { nav.navigate(Routes.Settings) },
                onOpenFilterBrowser = { nav.navigate(Routes.FilterBrowser) },
                onPhotoCaptured = { photoId -> nav.navigate(Routes.photoPreview(photoId)) },
            )
        }
        composable(Routes.FilterBrowser) {
            FilterBrowserScreen(onBack = { nav.popBackStack() })
        }
        composable(
            Routes.PhotoPreview,
            arguments = listOf(navArgument("photoId") { type = NavType.StringType }),
        ) { entry ->
            val raw = entry.arguments?.getString("photoId").orEmpty()
            val photoId = java.net.URLDecoder.decode(raw, java.nio.charset.StandardCharsets.UTF_8.name())
            PhotoPreviewScreen(
                photoId = photoId,
                onClose = { nav.popBackStack() },
                onRetake = { nav.popBackStack() },
            )
        }
        composable(Routes.Settings) {
            SettingsScreen(onBack = { nav.popBackStack() })
        }
    }
}
