---
name: settings
description: "Skill for the Settings area of v-cam. 18 symbols across 8 files."
---

# Settings

18 symbols | 8 files | Cohesion: 92%

## When to Use

- Working with code in `app/`
- Understanding how VCamNavGraph, SettingsScreen, ValueChev work
- Modifying settings-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `app/src/main/java/com/vcam/ui/settings/SettingsViewModel.kt` | SettingsViewModel, create, toggleSaveOriginal, toggleAutoSave, toggleGridLines (+1) |
| `app/src/main/java/com/vcam/data/settings/SettingsRepository.kt` | setSaveOriginal, setAutoSave, setGridLines, setCameraSound |
| `app/src/main/java/com/vcam/ui/settings/SettingsScreen.kt` | SettingsScreen, SectionLabel, GroupedCard |
| `app/src/main/java/com/vcam/MainActivity.kt` | onCreate |
| `app/src/main/java/com/vcam/navigation/VCamNavGraph.kt` | VCamNavGraph |
| `app/src/main/java/com/vcam/navigation/Routes.kt` | photoPreview |
| `app/src/main/java/com/vcam/ui/settings/components/ValueChev.kt` | ValueChev |
| `app/src/main/java/com/vcam/ui/settings/components/ThemeSegmented.kt` | ThemeSegmented |

## Entry Points

Start here when exploring this area:

- **`VCamNavGraph`** (Function) — `app/src/main/java/com/vcam/navigation/VCamNavGraph.kt:13`
- **`SettingsScreen`** (Function) — `app/src/main/java/com/vcam/ui/settings/SettingsScreen.kt:40`
- **`ValueChev`** (Function) — `app/src/main/java/com/vcam/ui/settings/components/ValueChev.kt:15`
- **`ThemeSegmented`** (Function) — `app/src/main/java/com/vcam/ui/settings/components/ThemeSegmented.kt:22`
- **`SettingsViewModel`** (Class) — `app/src/main/java/com/vcam/ui/settings/SettingsViewModel.kt:14`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `SettingsViewModel` | Class | `app/src/main/java/com/vcam/ui/settings/SettingsViewModel.kt` | 14 |
| `VCamNavGraph` | Function | `app/src/main/java/com/vcam/navigation/VCamNavGraph.kt` | 13 |
| `SettingsScreen` | Function | `app/src/main/java/com/vcam/ui/settings/SettingsScreen.kt` | 40 |
| `ValueChev` | Function | `app/src/main/java/com/vcam/ui/settings/components/ValueChev.kt` | 15 |
| `ThemeSegmented` | Function | `app/src/main/java/com/vcam/ui/settings/components/ThemeSegmented.kt` | 22 |
| `onCreate` | Method | `app/src/main/java/com/vcam/MainActivity.kt` | 14 |
| `photoPreview` | Method | `app/src/main/java/com/vcam/navigation/Routes.kt` | 11 |
| `create` | Method | `app/src/main/java/com/vcam/ui/settings/SettingsViewModel.kt` | 32 |
| `toggleSaveOriginal` | Method | `app/src/main/java/com/vcam/ui/settings/SettingsViewModel.kt` | 22 |
| `setSaveOriginal` | Method | `app/src/main/java/com/vcam/data/settings/SettingsRepository.kt` | 41 |
| `toggleAutoSave` | Method | `app/src/main/java/com/vcam/ui/settings/SettingsViewModel.kt` | 23 |
| `setAutoSave` | Method | `app/src/main/java/com/vcam/data/settings/SettingsRepository.kt` | 42 |
| `toggleGridLines` | Method | `app/src/main/java/com/vcam/ui/settings/SettingsViewModel.kt` | 24 |
| `setGridLines` | Method | `app/src/main/java/com/vcam/data/settings/SettingsRepository.kt` | 43 |
| `toggleCameraSound` | Method | `app/src/main/java/com/vcam/ui/settings/SettingsViewModel.kt` | 25 |
| `setCameraSound` | Method | `app/src/main/java/com/vcam/data/settings/SettingsRepository.kt` | 44 |
| `SectionLabel` | Function | `app/src/main/java/com/vcam/ui/settings/SettingsScreen.kt` | 142 |
| `GroupedCard` | Function | `app/src/main/java/com/vcam/ui/settings/SettingsScreen.kt` | 152 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `OnCreate → Release` | cross_community | 6 |
| `OnCreate → SetFlashMode` | cross_community | 6 |
| `OnCreate → PhotoBrush` | cross_community | 5 |
| `OnCreate → PhotoKindAt` | cross_community | 5 |
| `VCamNavGraph → ApplyOrientation` | cross_community | 5 |
| `OnCreate → ParseCubeLutFromAssets` | cross_community | 4 |
| `OnCreate → SubmitLut` | cross_community | 4 |
| `OnCreate → FiltersInCategory` | cross_community | 4 |
| `OnCreate → ToggleStar` | cross_community | 4 |
| `OnCreate → SetFilter` | cross_community | 4 |

## Connected Areas

| Area | Connections |
|------|-------------|
| Camera | 1 calls |
| Components | 1 calls |
| Preview | 1 calls |

## How to Explore

1. `gitnexus_context({name: "VCamNavGraph"})` — see callers and callees
2. `gitnexus_query({query: "settings"})` — find related execution flows
3. Read key files listed above for implementation details
