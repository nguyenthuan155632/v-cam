---
name: camera
description: "Skill for the Camera area of v-cam. 31 symbols across 10 files."
---

# Camera

31 symbols | 10 files | Cohesion: 87%

## When to Use

- Working with code in `app/`
- Understanding how parseCubeLutFromAssets, CameraScreen, ShutterClassic work
- Modifying camera-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `app/src/main/java/com/vcam/camera/LutRenderer.kt` | LutRenderer, attach, submitLut, onSurfaceCreated, onDrawFrame (+5) |
| `app/src/main/java/com/vcam/camera/CameraController.kt` | CameraController, capture, bindToSurfaceTexture, setFlashMode, flipCamera (+1) |
| `app/src/main/java/com/vcam/camera/CubeLutParser.kt` | parseCubeLutFromAssets, CubeLut, identityCubeLut, parseCubeLut |
| `app/src/main/java/com/vcam/ui/camera/CameraViewModel.kt` | cycleAspectRatio, setActiveFilter, CameraViewModel, create |
| `app/src/main/java/com/vcam/camera/CameraGLSurfaceView.kt` | CameraGLSurfaceView, bindRenderer |
| `app/src/main/java/com/vcam/ui/camera/CameraScreen.kt` | CameraScreen |
| `app/src/main/java/com/vcam/ui/camera/components/ShutterClassic.kt` | ShutterClassic |
| `app/src/main/java/com/vcam/ui/camera/components/RuleOfThirdsOverlay.kt` | RuleOfThirdsOverlay |
| `app/src/main/java/com/vcam/ui/camera/components/RatioStrip.kt` | RatioStrip |
| `app/src/main/java/com/vcam/ui/camera/components/FilterNameLabel.kt` | FilterNameLabel |

## Entry Points

Start here when exploring this area:

- **`parseCubeLutFromAssets`** (Function) — `app/src/main/java/com/vcam/camera/CubeLutParser.kt:25`
- **`CameraScreen`** (Function) — `app/src/main/java/com/vcam/ui/camera/CameraScreen.kt:54`
- **`ShutterClassic`** (Function) — `app/src/main/java/com/vcam/ui/camera/components/ShutterClassic.kt:22`
- **`RuleOfThirdsOverlay`** (Function) — `app/src/main/java/com/vcam/ui/camera/components/RuleOfThirdsOverlay.kt:9`
- **`RatioStrip`** (Function) — `app/src/main/java/com/vcam/ui/camera/components/RatioStrip.kt:16`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `LutRenderer` | Class | `app/src/main/java/com/vcam/camera/LutRenderer.kt` | 19 |
| `CameraGLSurfaceView` | Class | `app/src/main/java/com/vcam/camera/CameraGLSurfaceView.kt` | 6 |
| `CameraController` | Class | `app/src/main/java/com/vcam/camera/CameraController.kt` | 25 |
| `CubeLut` | Class | `app/src/main/java/com/vcam/camera/CubeLutParser.kt` | 6 |
| `CameraViewModel` | Class | `app/src/main/java/com/vcam/ui/camera/CameraViewModel.kt` | 13 |
| `parseCubeLutFromAssets` | Function | `app/src/main/java/com/vcam/camera/CubeLutParser.kt` | 25 |
| `CameraScreen` | Function | `app/src/main/java/com/vcam/ui/camera/CameraScreen.kt` | 54 |
| `ShutterClassic` | Function | `app/src/main/java/com/vcam/ui/camera/components/ShutterClassic.kt` | 22 |
| `RuleOfThirdsOverlay` | Function | `app/src/main/java/com/vcam/ui/camera/components/RuleOfThirdsOverlay.kt` | 9 |
| `RatioStrip` | Function | `app/src/main/java/com/vcam/ui/camera/components/RatioStrip.kt` | 16 |
| `FilterNameLabel` | Function | `app/src/main/java/com/vcam/ui/camera/components/FilterNameLabel.kt` | 20 |
| `identityCubeLut` | Function | `app/src/main/java/com/vcam/camera/CubeLutParser.kt` | 9 |
| `parseCubeLut` | Function | `app/src/main/java/com/vcam/camera/CubeLutParser.kt` | 31 |
| `attach` | Method | `app/src/main/java/com/vcam/camera/LutRenderer.kt` | 24 |
| `submitLut` | Method | `app/src/main/java/com/vcam/camera/LutRenderer.kt` | 59 |
| `bindRenderer` | Method | `app/src/main/java/com/vcam/camera/CameraGLSurfaceView.kt` | 14 |
| `capture` | Method | `app/src/main/java/com/vcam/camera/CameraController.kt` | 79 |
| `cycleAspectRatio` | Method | `app/src/main/java/com/vcam/ui/camera/CameraViewModel.kt` | 36 |
| `setActiveFilter` | Method | `app/src/main/java/com/vcam/ui/camera/CameraViewModel.kt` | 43 |
| `onSurfaceCreated` | Method | `app/src/main/java/com/vcam/camera/LutRenderer.kt` | 61 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `OnCreate → Release` | cross_community | 6 |
| `OnCreate → SetFlashMode` | cross_community | 6 |
| `OnCreate → ParseCubeLutFromAssets` | cross_community | 4 |
| `OnCreate → SubmitLut` | cross_community | 4 |
| `OnSurfaceCreated → Compile` | intra_community | 3 |
| `OnSurfaceCreated → UploadLut` | intra_community | 3 |
| `ParseCubeLut → CubeLut` | intra_community | 3 |

## Connected Areas

| Area | Connections |
|------|-------------|
| Components | 2 calls |

## How to Explore

1. `gitnexus_context({name: "parseCubeLutFromAssets"})` — see callers and callees
2. `gitnexus_query({query: "camera"})` — find related execution flows
3. Read key files listed above for implementation details
