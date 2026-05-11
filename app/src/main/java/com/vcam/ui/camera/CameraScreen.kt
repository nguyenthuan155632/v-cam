package com.vcam.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcam.VCamApplication
import com.vcam.camera.CameraController
import com.vcam.camera.CameraGLSurfaceView
import com.vcam.camera.LutRenderer
import com.vcam.camera.parseCubeLutFromAssets
import com.vcam.data.Filters
import com.vcam.theme.VColors
import com.vcam.ui.camera.components.CameraBottomRow
import com.vcam.ui.camera.components.CameraTopBar
import com.vcam.ui.camera.components.FilterNameLabel
import com.vcam.ui.camera.components.FilterRibbon
import com.vcam.ui.camera.components.RatioStrip
import com.vcam.ui.camera.components.RibbonVariant
import com.vcam.ui.camera.components.RuleOfThirdsOverlay
import com.vcam.ui.camera.components.ShutterClassic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CameraScreen(
    onOpenSettings: () -> Unit,
    onOpenFilterBrowser: () -> Unit,
    onPhotoCaptured: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val app = context.applicationContext as VCamApplication
    val vm: CameraViewModel = viewModel(factory = CameraViewModel.Factory(app.settingsRepo))
    val state by vm.state.collectAsState()

    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> permissionGranted = granted }
    LaunchedEffect(Unit) {
        if (!permissionGranted) permLauncher.launch(Manifest.permission.CAMERA)
    }

    val controllerRef = remember { mutableStateOf<CameraController?>(null) }
    val rendererRef = remember { mutableStateOf<LutRenderer?>(null) }
    val surfaceTextureRef = remember { mutableStateOf<SurfaceTexture?>(null) }
    var surfaceWidth by remember { mutableIntStateOf(0) }
    var surfaceHeight by remember { mutableIntStateOf(0) }

    val activeFilter = Filters.getOrElse(state.activeFilterIndex) { Filters.first() }

    // Active filter → LUT swap.
    LaunchedEffect(state.activeFilterIndex) {
        val r = rendererRef.value ?: return@LaunchedEffect
        val lut = withContext(Dispatchers.IO) {
            parseCubeLutFromAssets(context, activeFilter.lutAsset)
        }
        r.submitLut(lut)
    }

    // Intensity → renderer uniform.
    LaunchedEffect(state.intensity) {
        rendererRef.value?.intensity = state.intensity / 100f
    }

    // Flash mode → ImageCapture.
    LaunchedEffect(state.flash) {
        controllerRef.value?.setFlashMode(state.flash.cxFlash)
    }

    // Lens facing → rebind CameraX to the same SurfaceTexture with the new selector.
    LaunchedEffect(state.frontFacing) {
        val controller = controllerRef.value ?: return@LaunchedEffect
        val st = surfaceTextureRef.value ?: return@LaunchedEffect
        controller.flipCamera(st, surfaceWidth.coerceAtLeast(1), surfaceHeight.coerceAtLeast(1))
    }

    DisposableEffect(Unit) {
        onDispose {
            controllerRef.value?.release()
            rendererRef.value?.release()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(VColors.CameraBackground)
    ) {
        // 4:3 viewfinder.
        Box(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 64.dp)
                .aspectRatio(3f / 4f, matchHeightConstraintsFirst = false)
                .align(Alignment.TopCenter)
                .background(VColors.CameraBackground)
        ) {
            if (permissionGranted) {
                AndroidView(
                    factory = { ctx ->
                        CameraGLSurfaceView(ctx).apply {
                            val renderer = LutRenderer { surfaceTexture, w, h ->
                                rendererRef.value = this.renderer
                                surfaceTextureRef.value = surfaceTexture
                                surfaceWidth = if (w > 0) w else 1080
                                surfaceHeight = if (h > 0) h else 1440
                                val controller = CameraController(ctx, lifecycleOwner)
                                    .also { controllerRef.value = it }
                                controller.bindToSurfaceTexture(
                                    surfaceTexture,
                                    width = surfaceWidth,
                                    height = surfaceHeight,
                                    flashMode = state.flash.cxFlash,
                                )
                                // Seed initial LUT + intensity.
                                this@apply.renderer?.intensity = state.intensity / 100f
                                this@apply.queueEvent {
                                    // Cannot do IO here; load on main scope instead.
                                }
                            }
                            bindRenderer(renderer)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            RuleOfThirdsOverlay(show = state.gridOn)
        }

        // Top bar.
        Column(
            Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding(),
        ) {
            CameraTopBar(
                flash = state.flash,
                timer = state.timer,
                ratio = state.aspectRatio,
                onSettings = onOpenSettings,
                onGrid = vm::toggleGrid,
                onFlashClick = vm::cycleFlash,
                onTimerClick = vm::cycleTimer,
                onRatioClick = vm::cycleAspectRatio,
            )
        }

        // Bottom stack.
        Column(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .systemBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FilterNameLabel(
                filter = activeFilter,
                onPrev = { vm.setActiveFilter((state.activeFilterIndex - 1).coerceAtLeast(0)) },
                onNext = { vm.setActiveFilter((state.activeFilterIndex + 1).coerceAtMost(Filters.size - 1)) },
            )
            Spacer(Modifier.height(10.dp))
            FilterRibbon(
                filters = Filters.take(12),
                activeIndex = state.activeFilterIndex.coerceAtMost(11),
                accent = VColors.Coral,
                variant = RibbonVariant.Circle,
                onSelect = vm::setActiveFilter,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            RatioStrip(
                value = state.aspectRatio,
                accent = VColors.Coral,
                onSelect = { vm.cycleAspectRatio() },
            )
            Spacer(Modifier.height(18.dp))
            CameraBottomRow(
                accent = VColors.Coral,
                onGallery = onOpenFilterBrowser,
                onFlip = vm::flipCamera,
                shutter = {
                    ShutterClassic {
                        controllerRef.value?.capture { uri ->
                            onPhotoCaptured(uri ?: "preview")
                        }
                    }
                },
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}
