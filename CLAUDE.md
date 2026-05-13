# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

V-Cam is a native Android camera app focused on capture, live LUT-filter preview, photo preview, and settings. It is intentionally scoped away from accounts, social features, sharing, and beauty reshaping.

Stack:
- Kotlin 2.0.20, AGP 8.5.2, Gradle wrapper 8.10.2
- Android min SDK 26, compile/target SDK 34
- Jetpack Compose + Material 3 UI
- Navigation Compose for screens
- CameraX `Preview` + `ImageCapture`
- OpenGL ES 2.0 via `GLSurfaceView` for live LUT filtering
- DataStore Preferences for settings

## Common commands

Prerequisites from README:
- JDK 17
- Android SDK with `platforms;android-34` and `build-tools;34.0.0` or newer
- `local.properties` must point to Android SDK, e.g. `sdk.dir=$HOME/Library/Android/sdk`

Build and install:
```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
./gradlew :app:assembleRelease
```

Verification:
```bash
./gradlew :app:build
./gradlew :app:check
./gradlew :app:lint
./gradlew :app:test
./gradlew :app:testDebugUnitTest
./gradlew :app:connectedAndroidTest
```

Run a single unit test when tests exist:
```bash
./gradlew :app:testDebugUnitTest --tests 'com.vcam.package.ClassName.testName'
```

Device install helpers:
```bash
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb push app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/
```

## Architecture

Single Android app module `:app` contains all runtime code under `app/src/main/java/com/vcam`.

Entry and dependency access:
- `VCamApplication` constructs app-level `SettingsRepository`.
- `MainActivity` enables edge-to-edge Compose, observes theme from settings, wraps content in `VCamTheme`, then starts `VCamNavGraph`.

Navigation:
- `navigation/Routes.kt` defines route strings.
- `navigation/VCamNavGraph.kt` owns the `NavHost` and connects Camera, Filter Browser, Photo Preview, and Settings screens.
- Photo preview receives a URI-like `photoId` route argument and decodes it before showing `PhotoPreviewScreen`.

Settings/data flow:
- `data/settings/SettingsRepository.kt` maps DataStore key/value preferences to `UserSettings` and exposes update methods.
- ViewModels collect `SettingsRepository.settings` and expose Compose-friendly state flows.
- Filter metadata lives in `data/Filters.kt`; each filter references a `.cube` LUT asset under `app/src/main/assets/luts/`.

Camera/rendering flow:
- `CameraScreen` requests camera permission, creates `CameraGLSurfaceView`, and wires `LutRenderer` to `CameraController`.
- `LutRenderer` owns the OpenGL external camera texture and LUT texture. It renders CameraX preview frames through GLSL in `LutShaders.kt`, applying current LUT and intensity.
- `CameraController` binds CameraX `Preview` to the renderer-owned `SurfaceTexture` and uses `ImageCapture` to save JPEGs into `MediaStore` under `Pictures/VCam`.
- Filter changes parse `.cube` assets with `CubeLutParser` on `Dispatchers.IO`, then submit the LUT to the renderer. Intensity changes update renderer uniform without rebinding camera.
- Lens flip rebinds CameraX to the same `SurfaceTexture`; GL context is not restarted.

UI organization:
- `ui/camera` contains the shipped Classic camera screen and component primitives.
- `ui/filters`, `ui/preview`, and `ui/settings` contain their screens and ViewModels.
- Shared UI components live in `ui/components`; custom icons live in `ui/icons/VIcons.kt`.
- Theme source of truth is in `theme/Color.kt`, `theme/Type.kt`, `theme/Shapes.kt`, `theme/Spacing.kt`, and `theme/Shadows.kt`.

## Assets and design notes

`app/src/main/assets/luts/` contains 24 placeholder identity `.cube` LUT files. Replace with real grades by keeping file names aligned with each filter's `lutAsset` in `data/Filters.kt`.

`design_handoff_vcam/` contains original design handoff material. README says only the Classic camera variation currently ships; Pro and Full-bleed are future work built from existing camera UI primitives.

<!-- gitnexus:start -->
# GitNexus — Code Intelligence

This project is indexed by GitNexus as **v-cam** (1821 symbols, 3588 relationships, 99 execution flows). Use the GitNexus MCP tools to understand code, assess impact, and navigate safely.

> If any GitNexus tool warns the index is stale, run `npx gitnexus analyze` in terminal first.

## Always Do

- **MUST run impact analysis before editing any symbol.** Before modifying a function, class, or method, run `gitnexus_impact({target: "symbolName", direction: "upstream"})` and report the blast radius (direct callers, affected processes, risk level) to the user.
- **MUST run `gitnexus_detect_changes()` before committing** to verify your changes only affect expected symbols and execution flows.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- When exploring unfamiliar code, use `gitnexus_query({query: "concept"})` to find execution flows instead of grepping. It returns process-grouped results ranked by relevance.
- When you need full context on a specific symbol — callers, callees, which execution flows it participates in — use `gitnexus_context({name: "symbolName"})`.

## Never Do

- NEVER edit a function, class, or method without first running `gitnexus_impact` on it.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis.
- NEVER rename symbols with find-and-replace — use `gitnexus_rename` which understands the call graph.
- NEVER commit changes without running `gitnexus_detect_changes()` to check affected scope.

## Resources

| Resource | Use for |
|----------|---------|
| `gitnexus://repo/v-cam/context` | Codebase overview, check index freshness |
| `gitnexus://repo/v-cam/clusters` | All functional areas |
| `gitnexus://repo/v-cam/processes` | All execution flows |
| `gitnexus://repo/v-cam/process/{name}` | Step-by-step execution trace |

## CLI

| Task | Read this skill file |
|------|---------------------|
| Understand architecture / "How does X work?" | `.claude/skills/gitnexus/gitnexus-exploring/SKILL.md` |
| Blast radius / "What breaks if I change X?" | `.claude/skills/gitnexus/gitnexus-impact-analysis/SKILL.md` |
| Trace bugs / "Why is X failing?" | `.claude/skills/gitnexus/gitnexus-debugging/SKILL.md` |
| Rename / extract / split / refactor | `.claude/skills/gitnexus/gitnexus-refactoring/SKILL.md` |
| Tools, resources, schema reference | `.claude/skills/gitnexus/gitnexus-guide/SKILL.md` |
| Index, status, clean, wiki CLI commands | `.claude/skills/gitnexus/gitnexus-cli/SKILL.md` |

<!-- gitnexus:end -->
