package com.vcam.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcam.VCamApplication
import com.vcam.camera.CameraController
import com.vcam.camera.CameraGLSurfaceView
import com.vcam.camera.LutRenderer
import com.vcam.camera.CubeLut
import com.vcam.camera.parseCubeLutFromAssets
import com.vcam.color.FilterCatalog
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

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
    var focusControlState by remember { mutableStateOf<FocusControlState?>(null) }
    var exposureDragging by remember { mutableStateOf(false) }
    var activeLut by remember { mutableStateOf<CubeLut?>(null) }

    val activeFilter = Filters.getOrElse(state.activeFilterIndex) { Filters.first() }

    // Active filter → LUT swap.
    LaunchedEffect(state.activeFilterIndex) {
        val lut = withContext(Dispatchers.IO) {
            parseCubeLutFromAssets(context, activeFilter.lutAsset)
        }
        activeLut = lut
        rendererRef.value?.submitLut(lut)
    }

    // Intensity → renderer uniform.
    LaunchedEffect(state.intensity) {
        rendererRef.value?.intensity = state.intensity / 100f
    }

    // Flash mode → ImageCapture.
    LaunchedEffect(state.flash) {
        controllerRef.value?.setFlashMode(state.flash.cxFlash)
    }

    LaunchedEffect(focusControlState, exposureDragging) {
        if (focusControlState != null && !exposureDragging) {
            delay(1500)
            focusControlState = null
        }
    }

    LaunchedEffect(state.aspectRatio.cameraAspectRatio) {
        val controller = controllerRef.value ?: return@LaunchedEffect
        val st = surfaceTextureRef.value ?: return@LaunchedEffect
        controller.rebind(
            st,
            surfaceWidth.coerceAtLeast(1),
            surfaceHeight.coerceAtLeast(1),
            state.aspectRatio.cameraAspectRatio,
        ) { w, h -> rendererRef.value?.updateSourceSize(w, h) }
    }

    // Lens facing → rebind CameraX to the same SurfaceTexture with the new selector.
    LaunchedEffect(state.frontFacing) {
        val controller = controllerRef.value ?: return@LaunchedEffect
        val st = surfaceTextureRef.value ?: return@LaunchedEffect
        controller.flipCamera(
            st,
            surfaceWidth.coerceAtLeast(1),
            surfaceHeight.coerceAtLeast(1),
            state.aspectRatio.cameraAspectRatio,
        ) { w, h -> rendererRef.value?.updateSourceSize(w, h) }
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
        val viewfinderModifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 64.dp)
            .then(
                state.aspectRatio.previewAspect?.let { Modifier.aspectRatio(it, matchHeightConstraintsFirst = false) }
                    ?: Modifier.fillMaxHeight(),
            )
            .align(Alignment.TopCenter)
            .background(VColors.CameraBackground)

        Box(viewfinderModifier) {
            if (permissionGranted) {
                AndroidView(
                    factory = { ctx ->
                        CameraGLSurfaceView(ctx).apply {
                            onPreviewTap = { x, y, width, height ->
                                controllerRef.value?.let { controller ->
                                    if (controller.focusAndMeterAt(x, y, width, height)) {
                                        focusControlState = FocusControlState(
                                            offset = Offset(x, y),
                                            exposureState = controller.exposureCompensationState(),
                                        )
                                    }
                                }
                            }
                            val renderer = LutRenderer { surfaceTexture, w, h ->
                                rendererRef.value = this.renderer
                                activeLut?.let { this.renderer?.submitLut(it) }
                                surfaceTextureRef.value = surfaceTexture
                                surfaceWidth = if (w > 0) w else 1080
                                surfaceHeight = if (h > 0) h else 1440
                                this@apply.renderer?.updateSourceSize(surfaceWidth, surfaceHeight)
                                val controller = CameraController(ctx, lifecycleOwner, app.offscreenLutProcessor)
                                    .also { controllerRef.value = it }
                                controller.bindToSurfaceTexture(
                                    surfaceTexture,
                                    width = surfaceWidth,
                                    height = surfaceHeight,
                                    flashMode = state.flash.cxFlash,
                                    targetAspectRatio = state.aspectRatio.cameraAspectRatio,
                                ) { selectedWidth, selectedHeight ->
                                    this@apply.renderer?.updateSourceSize(selectedWidth, selectedHeight)
                                }
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
            FocusControls(
                state = focusControlState,
                onExposureDragStart = { exposureDragging = true },
                onExposureDragEnd = { exposureDragging = false },
                onExposureIndexChange = { index ->
                    controllerRef.value?.let { controller ->
                        if (controller.setExposureCompensationIndex(index)) {
                            focusControlState = focusControlState?.copy(
                                exposureState = controller.exposureCompensationState(),
                            )
                        }
                    }
                },
            )
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
                onSelect = vm::setAspectRatio,
            )
            Spacer(Modifier.height(18.dp))
            CameraBottomRow(
                accent = VColors.Coral,
                onGallery = onOpenFilterBrowser,
                onFlip = vm::flipCamera,
                shutter = {
                    ShutterClassic {
                        val lut = activeLut ?: return@ShutterClassic
                        controllerRef.value?.capture(
                            filter = FilterCatalog.byId(activeFilter.id) ?: return@ShutterClassic,
                            lut = lut,
                            intensity = state.intensity / 100f,
                            saveOriginal = state.saveOriginal,
                        ) { filteredUri, _ ->
                            onPhotoCaptured(filteredUri ?: "preview")
                        }
                    }
                },
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}

private data class FocusControlState(
    val offset: Offset,
    val exposureState: CameraController.ExposureCompensationState,
)

@Composable
private fun BoxScope.FocusControls(
    state: FocusControlState?,
    onExposureDragStart: () -> Unit,
    onExposureDragEnd: () -> Unit,
    onExposureIndexChange: (Int) -> Unit,
) {
    state ?: return
    FocusRing(state.offset)
    val exposureState = state.exposureState as? CameraController.ExposureCompensationState.Supported ?: return
    ExposureBar(
        focusOffset = state.offset,
        exposureState = exposureState,
        onDragStart = onExposureDragStart,
        onDragEnd = onExposureDragEnd,
        onIndexChange = onExposureIndexChange,
    )
}

@Composable
private fun BoxScope.FocusRing(offset: Offset) {
    val density = LocalDensity.current
    Box(
        Modifier
            .offset {
                with(density) {
                    IntOffset(
                        x = (offset.x - 24.dp.toPx()).roundToInt(),
                        y = (offset.y - 24.dp.toPx()).roundToInt(),
                    )
                }
            }
            .size(48.dp)
            .alpha(0.9f)
            .border(2.dp, VColors.Coral, CircleShape)
    )
}

@Composable
private fun BoxScope.ExposureBar(
    focusOffset: Offset,
    exposureState: CameraController.ExposureCompensationState.Supported,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onIndexChange: (Int) -> Unit,
) {
    val density = LocalDensity.current
    val barHeight = 120.dp
    val barWidth = 36.dp
    val range = exposureState.maxIndex - exposureState.minIndex
    val fraction = if (range == 0) {
        0.5f
    } else {
        (exposureState.currentIndex - exposureState.minIndex).toFloat() / range.toFloat()
    }
    Box(
        Modifier
            .offset {
                with(density) {
                    IntOffset(
                        x = (focusOffset.x + 34.dp.toPx()).roundToInt(),
                        y = (focusOffset.y - (barHeight.toPx() / 2f)).roundToInt(),
                    )
                }
            }
            .width(barWidth)
            .height(barHeight)
            .pointerInput(exposureState.minIndex, exposureState.maxIndex) {
                detectVerticalDragGestures(
                    onDragStart = { onDragStart() },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() },
                ) { change, _ ->
                    val y = change.position.y.coerceIn(0f, size.height.toFloat())
                    val draggedFraction = 1f - (y / size.height.toFloat())
                    val nextIndex = (exposureState.minIndex + draggedFraction * range).roundToInt()
                    onIndexChange(nextIndex)
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .width(2.dp)
                .fillMaxHeight()
                .background(VColors.White85)
        )
        Box(
            Modifier
                .offset {
                    with(density) {
                        IntOffset(0, ((0.5f - fraction) * barHeight.toPx()).roundToInt())
                    }
                }
                .size(14.dp)
                .background(VColors.Coral, CircleShape)
        )
    }
}
