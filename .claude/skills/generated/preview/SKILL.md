---
name: preview
description: "Skill for the Preview area of v-cam. 8 symbols across 2 files."
---

# Preview

8 symbols | 2 files | Cohesion: 83%

## When to Use

- Working with code in `app/`
- Understanding how PhotoPreviewScreen, PhotoPreviewViewModel, loadPhoto work
- Modifying preview-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt` | loadPhoto, decode, applyOrientation, setFilter, toggleStar (+2) |
| `app/src/main/java/com/vcam/ui/preview/PhotoPreviewScreen.kt` | PhotoPreviewScreen |

## Entry Points

Start here when exploring this area:

- **`PhotoPreviewScreen`** (Function) — `app/src/main/java/com/vcam/ui/preview/PhotoPreviewScreen.kt:52`
- **`PhotoPreviewViewModel`** (Class) — `app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt:33`
- **`loadPhoto`** (Method) — `app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt:52`
- **`setFilter`** (Method) — `app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt:114`
- **`toggleStar`** (Method) — `app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt:116`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `PhotoPreviewViewModel` | Class | `app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt` | 33 |
| `PhotoPreviewScreen` | Function | `app/src/main/java/com/vcam/ui/preview/PhotoPreviewScreen.kt` | 52 |
| `loadPhoto` | Method | `app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt` | 52 |
| `setFilter` | Method | `app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt` | 114 |
| `toggleStar` | Method | `app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt` | 116 |
| `create` | Method | `app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt` | 122 |
| `decode` | Method | `app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt` | 68 |
| `applyOrientation` | Method | `app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt` | 99 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `VCamNavGraph → ApplyOrientation` | cross_community | 5 |
| `OnCreate → ToggleStar` | cross_community | 4 |
| `OnCreate → SetFilter` | cross_community | 4 |

## Connected Areas

| Area | Connections |
|------|-------------|
| Components | 2 calls |

## How to Explore

1. `gitnexus_context({name: "PhotoPreviewScreen"})` — see callers and callees
2. `gitnexus_query({query: "preview"})` — find related execution flows
3. Read key files listed above for implementation details
