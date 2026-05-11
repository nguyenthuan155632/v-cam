# Handoff: V-Cam (Android camera + filters app)

## Overview
V-Cam is a focused Android camera app for taking beautiful photos with real-time filters — similar in spirit to Foodie / VSCO. Scope is intentionally small: capture, filter, preview, settings. **No social, accounts, feeds, comments, sharing, cloud, stickers, or beauty reshaping.**

The design covers four core screens and exposes three visual treatments for the camera screen so the team can pick the direction that best fits the brand.

## About the Design Files
The files in this bundle are **design references created in HTML** — prototypes showing intended look and behavior, not production code to copy directly. The task is to **recreate these HTML designs in the target Android codebase** using its established patterns (e.g. Jetpack Compose with Material 3, or XML + Material Components). If no codebase exists yet, prefer **Jetpack Compose** with Material 3 — it matches the design language used in the mocks.

The HTML simulates filters with CSS gradient tints over gradient "photo" placeholders. In the real app, filters must be applied to the live camera preview using CameraX + a real LUT pipeline (Renderscript replacement: GPU shader / `RenderEffect` / GLSurfaceView + OpenGL, or a library like `GPUImage` / `mediapipe`).

## Fidelity
**High-fidelity.** Exact hex values, type, spacing, and component shapes are final. The 3 camera variations should be discussed with the team — only one should ship. Recommended default: **Classic** (it is the most familiar and lowest-cognitive-load).

## Screens

### 01 · Camera Screen (3 variations)
Full-screen live camera preview with translucent dark controls.

**Common elements** (top to bottom):
- Status bar (system).
- Top control row (padded 14px): settings icon button · 3 chips (flash, timer, ratio) · grid icon button.
- Live viewfinder fills the rest, cropped to the selected aspect ratio with letterbox black above/below.
- Optional rule-of-thirds overlay (white, 50% opacity).
- Filter name + category code centered above the shutter.
- Horizontal filter ribbon (12 visible).
- Aspect ratio strip: `1:1  4:3  16:9  FULL` (mono, 11px, accent for active).
- Bottom row: gallery thumbnail (44×44, 12 radius, 1.5px white border) · shutter · camera flip button.
- Gesture nav bar.

**Variation A — Classic** (`CameraClassic` in `vcam-camera.jsx`)
- 4:3 viewfinder.
- Filter name as serif italic with chevron `< >` arrows hinting horizontal swipe.
- Round-thumb filter ribbon.
- Solid white shutter, 72px, 3px white ring.

**Variation B — Pro** (`CameraPro`)
- 1:1 viewfinder.
- Floating glass card above ribbon with filter category code, name, and a small heart count.
- Square-thumb filter ribbon.
- Ring shutter (76px, 2px accent ring, white fill).

**Variation C — Full-bleed** (`CameraMinimal`)
- 16:9 viewfinder, no chrome on the preview except a top/bottom darkening gradient for legibility.
- Right-side icon column (flash, timer, grid).
- Filter name top-center; ribbon pill-thumbs in a glass dock at the bottom.
- Conic-gradient shutter (coral → beige → coral).

### 02 · Filter Browser (`FilterBrowser`)
Full-screen filter library.

- Back · "FILTERS / Library" title · search icon header.
- Horizontal category pills (9 categories: Food, Cafe, Portrait, Travel, Vintage, Night, Clean, Warm, Cool). Active pill is solid ink with paper text.
- Hero 4:3 preview of the active filter with overlay "HOLD TO COMPARE" pill (mono).
- Intensity slider (0–100, 5 ticks, accent fill, white thumb with accent ring).
- 4-column filter grid; each tile shows tinted thumbnail + filter name + code. Active tile gets a 2px accent outline.

### 02b · Camera with Intensity sheet (`CameraIntensity`)
Camera screen with a translucent intensity sheet docked just above the filter ribbon: section label + value on top row, horizontal slider with tick marks below. Filter name pill is centered up top for context.

### 03 · Photo Preview (`PhotoPreview`)
Post-capture screen.

- Close · "JUST CAPTURED" + timestamp + dimensions · star (favorite).
- 4:3 photo with current filter applied; small dark pill in the top-left showing filter code + intensity %.
- "Edit filter" row — horizontal scroll of filter thumbs, active gets accent outline.
- Intensity slider (same component as browser).
- Bottom action row: "Retake" (paper, ink70 text, `#15110E12` border) · "Save photo" (ink button, paper text — 1.4× wider, default action).

### 04 · Settings (`SettingsScreen`)
Grouped list, paper-warm card backgrounds.

- Header: back · "Settings".
- **CAPTURE** group: Save original photo · Auto-save to gallery · Grid lines · Camera sound. Each row uses a Material-style toggle (38×22, accent-on).
- **DEFAULTS** group: Default aspect ratio · Default filter · Default intensity. Each is a row with value + chev (navigates to a sub-screen — not designed yet).
- **APPEARANCE** group: App theme — segmented control (Light / Dark / System).
- **ABOUT** group: Version.

## Design Tokens

### Color (warm system, exposed via `V` in `vcam-shared.jsx`)
| Token | Hex | Use |
|---|---|---|
| `coral` | `#F27A66` | Primary accent (default) |
| `coralSoft` | `#FBE3DC` | Accent backgrounds |
| `beige` | `#E8D3B8` | Secondary accent (selected chips on dark) |
| `beigeSoft` | `#F5EBDD` | — |
| `ink` | `#15110E` | Primary text, dark surfaces |
| `ink70` | `rgba(21,17,14,0.7)` | Secondary text |
| `ink50` | `rgba(21,17,14,0.5)` | Tertiary text, mono labels |
| `ink30` | `rgba(21,17,14,0.3)` | Disabled, ticks |
| `ink12` | `rgba(21,17,14,0.12)` | Borders, slider tracks |
| `ink06` | `rgba(21,17,14,0.06)` | Inactive chips, segmented bg |
| `paper` | `#FAF7F2` | App background |
| `paperWarm` | `#F2EDE4` | Grouped list cards |
| `divider` | `rgba(21,17,14,0.08)` | Row dividers |
| Camera bg | `#0a0908` | Phone surface in camera mode |

**Accent options** (Tweaks-exposed): `#F27A66` coral · `#E8B583` beige · `#F1A8B5` pastel pink · `#B5A89A` taupe.

### Type
- **Display / filter names**: Instrument Serif, italic.
- **UI / body**: DM Sans (400, 500, 600).
- **Tech labels (codes, EV, ratios)**: JetBrains Mono.
- In the Android codebase, substitute Material 3's `Roboto Flex` for DM Sans if preferred; pick a Google Font serif for the display face if Instrument Serif is unavailable in Compose.

Sizes (px):
- Hero display 30 (camera screen filter name on full-bleed) / 26 (Pro card) / 22 (Classic).
- Section header 22 (Filter library).
- Body 14, secondary 12.5–13.
- Tech mono labels 10–11, letter-spacing 1.2–1.6.

### Spacing
4 / 8 / 10 / 12 / 14 / 16 / 18 / 22 / 28 / 32 px scale.

### Radius
- Buttons / chips: 999 (pill).
- Glass sheets / cards: 14–20.
- Filter thumbs: 12 (square) · 22 (circle) · 12 (pill).
- Phone frame: 38.

### Shadows
- Resting card: `0 1px 3px rgba(21,17,14,0.08), 0 4px 16px rgba(21,17,14,0.06)`.
- Floating glass over preview: `0 12px 40px rgba(0,0,0,0.5)`.
- Slider thumb: `0 0 0 1.5px <accent>, 0 2px 6px rgba(0,0,0,0.35)` (dark) / `0 1px 4px rgba(21,17,14,0.2), 0 0 0 1.5px <accent>` (light).

## Filters
24 filters across 9 categories. Each filter has an `id`, `cat`, `name`, `code` (e.g. `F·01`), `tint` (CSS gradient overlay used for HTML preview), and `shift` (CSS filter string approximating the LUT — saturate/contrast/brightness/sepia/hue-rotate).

Real implementation should ship a LUT (`.cube` or PNG 3D-LUT) per filter and apply via a GLSL fragment shader or `RenderEffect` (API 31+). The CSS `shift` values are a hint for the LUT character — convert to LUT files in a color-grading tool (Davinci, Photoshop) using a neutral plate.

Filter list is the source of truth in `vcam-shared.jsx` → `FILTERS`.

## Interactions
- Tap shutter → freeze preview → navigate to Photo Preview.
- Long-press shutter → burst (not designed; standard Android pattern).
- Swipe horizontally on viewfinder or ribbon → switch filter.
- Long-press a filter thumb in the ribbon → quick-action menu (not in MVP).
- Pinch on viewfinder → zoom.
- Double-tap viewfinder → flip camera.
- Hold-to-compare on Filter Browser hero → reveals original.
- Settings rows → standard Material navigation.

Transitions: 150–200ms cubic-bezier(0.3, 0.7, 0.4, 1) for chips, segmented thumbs, toggles. Shutter press: scale down to 0.92 over 80ms then bounce back.

## State (per-screen)
**Camera**: `flash` (auto/on/off), `timer` (0/3/10), `aspectRatio` (1:1, 4:3, 16:9, full), `gridOn`, `activeFilterIdx`, `intensity`, `cameraFacing` (front/back).
**Filter Browser**: `activeCategory`, `activeFilterIdx`, `intensity`.
**Photo Preview**: `capturedBitmap`, `activeFilterIdx`, `intensity`, `originalKeptOnDisk` (from Settings).
**Settings**: persisted booleans + enum for theme & default ratio.

## Assets
- No bitmap assets shipped. All icons are inline SVG strokes (`vcam-icons.jsx`) — port to Material Icons / custom SVG drawables.
- Gradient "photo" placeholders in `PHOTO_SOURCES` are not for production.
- Fonts loaded via Google Fonts in the HTML — bundle equivalents (or substitutes) in the app.

## Screenshots
See `screenshots/` for per-screen PNGs:
- `01-camera-classic.png` — Camera, Classic variation
- `02-camera-pro.png` — Camera, Pro variation
- `03-camera-fullbleed.png` — Camera, Full-bleed variation
- `04-filter-library-food.png` — Filter Browser, Food category
- `05-filter-library-cafe.png` — Filter Browser, Cafe category
- `06-camera-intensity.png` — Camera with intensity sheet docked
- `07-photo-preview.png` — Photo Preview, default
- `08-photo-preview-edit.png` — Photo Preview, after switching filter to 70%
- `09-settings.png` — Settings

## Files in this bundle
- `V-Cam.html` — entry point that mounts the canvas with all artboards.
- `vcam-shared.jsx` — design tokens, fonts, phone frame, status/gesture bars, filter data, filter tile + hero photo primitives.
- `vcam-icons.jsx` — inline SVG icon set.
- `vcam-camera.jsx` — `CameraClassic`, `CameraPro`, `CameraMinimal`, plus shared chip/icon-button/shutter/ribbon primitives.
- `vcam-filters.jsx` — `FilterBrowser`, `CameraIntensity` (intensity-in-action).
- `vcam-preview.jsx` — `PhotoPreview`.
- `vcam-settings.jsx` — `SettingsScreen`.
- `design-canvas.jsx`, `tweaks-panel.jsx` — presentation scaffolding (not part of the app).

## Notes for the developer
- Build the camera pipeline on **CameraX** (`PreviewView` + `ImageCapture` + `ImageAnalysis`). Apply the live filter via a custom `SurfaceProcessor` or by rendering the camera SurfaceTexture to a GLSurfaceView with a LUT shader.
- Use **Jetpack Compose + Material 3** for the UI layer. Map the color tokens above to a `lightColorScheme` / `darkColorScheme`; the camera screen forces dark surfaces regardless of system theme.
- Honor the Settings flags: `Save original photo` writes the unfiltered frame to gallery alongside the rendered output; `Auto-save` skips Photo Preview's "Save photo" affirmation.
- Respect Android edge-to-edge: status bar over the camera should be translucent; gesture nav inset should be honored on every screen.
