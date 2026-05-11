# V-Cam

Native Android camera app with real-time LUT filters. Captures photos through a
CameraX pipeline rendered to a GL surface so each filter is applied to the live
preview, not just baked in at save time.

Scope is intentionally narrow — capture, filter, preview, settings. No accounts,
no social, no sharing, no beauty reshaping.

## Stack

| Layer | Tech |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Camera | CameraX (`Preview` + `ImageCapture`) |
| Live filter | OpenGL ES 2.0 / `GLSurfaceView` + tiled 2D LUT |
| Navigation | Navigation Compose |
| Persistence | DataStore Preferences |
| Fonts | Compose Google Fonts (Instrument Serif, DM Sans, JetBrains Mono) |

Min SDK 26, target / compile SDK 34, Kotlin 2.0.20, AGP 8.5.2, Gradle 8.10.2.

## Layout

```
app/src/main/java/com/vcam/
├── MainActivity.kt              entry, theme + nav host
├── VCamApplication.kt           singletons (SettingsRepository)
├── navigation/                  Routes + NavHost
├── theme/                       Color / Type / Shapes / Spacing / Shadows
├── data/
│   ├── Filter.kt                model
│   ├── Filters.kt               24 filters (id, code, category, tint, LUT path)
│   ├── Categories.kt
│   └── settings/                DataStore + UserSettings
├── camera/
│   ├── CameraController.kt      CameraX bind / capture / lens flip
│   ├── CameraGLSurfaceView.kt   GL view + renderer attach
│   ├── LutRenderer.kt           OES camera tex → 2D-tiled LUT shader
│   ├── LutShaders.kt            GLSL vertex + fragment
│   └── CubeLutParser.kt         .cube file → float buffer
├── ui/
│   ├── camera/                  CameraScreen (Classic variation) + components
│   ├── filters/                 FilterBrowserScreen + components
│   ├── preview/                 PhotoPreviewScreen + ViewModel
│   ├── settings/                SettingsScreen + components
│   ├── components/              IntensitySlider, FilterThumb, PhotoPlaceholder
│   └── icons/VIcons.kt          ImageVector icon set
└── ...
app/src/main/assets/luts/        24 placeholder identity .cube files
design_handoff_vcam/             original spec (HTML mocks, screenshots, README)
```

## Prerequisites

- JDK 17 (`brew install openjdk@17`)
- Android SDK with **platforms;android-34** and **build-tools;34.0.0** (or 35)
- Gradle wrapper is bundled — no system Gradle required

## Setup

```bash
# Point Gradle at your local Android SDK
cat > local.properties <<EOF
sdk.dir=$HOME/Library/Android/sdk
EOF

# Accept SDK licences (one-time)
yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses

# Install required SDK components if missing
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager \
  "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

## Build

```bash
./gradlew :app:assembleDebug                      # → app/build/outputs/apk/debug/app-debug.apk
./gradlew :app:installDebug                       # build + install onto connected device
./gradlew :app:assembleRelease                    # unsigned release APK
```

## Run on device

```bash
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

If `adb install` fails with `INSTALL_FAILED_USER_RESTRICTED`, enable **Install via
USB** on the phone (Developer Options) — Xiaomi, Oppo and Vivo gate this by
default. Alternatively push and tap:

```bash
adb push app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/
# then open Files → Downloads → tap the APK on the phone
```

## LUT filters

Each filter in `data/Filters.kt` references a `.cube` file under
`app/src/main/assets/luts/`. The 24 files shipped today are identity LUTs (17³
neutral grades) — replace them with real grades exported from DaVinci Resolve,
Photoshop, or any color tool. File names must match the `lutAsset` in
`Filters.kt`:

```
luts/
├── fd01.cube  Crisp 01    (Food)
├── fd02.cube  Bake        (Food)
├── fd03.cube  Honey       (Food)
├── cf01.cube  Latte       (Cafe)
... 24 total
```

LUTs are loaded lazily — first use of a filter parses the file, uploads it as a
tiled `N×N²` 2D texture, and swaps it into the fragment shader. Switching
filters or intensity does not re-bind the camera.

## Settings

Persisted via `DataStore Preferences` under name `vcam_settings`:

| Key | Type | Default |
|---|---|---|
| `save_original` | Boolean | `true` |
| `auto_save` | Boolean | `true` |
| `grid_lines` | Boolean | `false` |
| `camera_sound` | Boolean | `false` |
| `default_ratio` | String (`1:1` / `4:3` / `16:9` / `FULL`) | `4:3` |
| `default_filter` | String (filter id) | `fd01` |
| `default_intensity` | Int | `80` |
| `theme` | String (`Light` / `Dark` / `System`) | `Light` |

## Design tokens (source of truth: `theme/Color.kt`, `theme/Type.kt`)

Warm coral + beige duotone on warm near-black / near-white:

```
coral        #F27A66    primary accent
coralSoft    #FBE3DC    accent surface
beige        #E8D3B8    secondary accent
ink          #15110E    primary text
paper        #FAF7F2    app background
paperWarm    #F2EDE4    grouped list card
cameraBg     #0A0908    dark surface in camera mode
```

Type — Instrument Serif italic (filter names), DM Sans (UI), JetBrains Mono
(codes, EV, mono labels). All loaded via Compose Google Fonts at first use.

## Camera variations

The handoff includes three camera variations (Classic / Pro / Full-bleed). Only
**Classic** ships — it is the recommended default. Pro and Full-bleed components
(square / pill ribbons, ring & conic-gradient shutters, full-bleed dock) can be
added later by composing the same primitives in `ui/camera/components/`.

## Roadmap

- Real-bitmap photo preview from captured `Uri` — **done**
- Lens-flip rebind without restarting the GL context — **done**
- Pro + Full-bleed camera variations
- Real LUTs (24 files)
- Long-press filter quick-action menu
- Pinch-zoom + double-tap lens flip
- Hold-to-compare on Filter Browser hero

## License

Internal. No license granted yet.
