---
name: components
description: "Skill for the Components area of v-cam. 12 symbols across 9 files."
---

# Components

12 symbols | 9 files | Cohesion: 70%

## When to Use

- Working with code in `app/`
- Understanding how filtersInCategory, FilterBrowserScreen, photoKindAt work
- Modifying components-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `app/src/main/java/com/vcam/ui/components/PhotoPlaceholder.kt` | photoKindAt, photoBrush, PhotoPlaceholder |
| `app/src/main/java/com/vcam/ui/components/IntensitySlider.kt` | IntensitySlider, drawTrack |
| `app/src/main/java/com/vcam/data/Filters.kt` | filtersInCategory |
| `app/src/main/java/com/vcam/ui/filters/FilterBrowserScreen.kt` | FilterBrowserScreen |
| `app/src/main/java/com/vcam/ui/filters/components/FilterGrid.kt` | FilterGrid |
| `app/src/main/java/com/vcam/ui/camera/components/FilterRibbon.kt` | FilterRibbon |
| `app/src/main/java/com/vcam/ui/components/FilterThumb.kt` | FilterThumb |
| `app/src/main/java/com/vcam/ui/filters/components/HeroPreview.kt` | HeroPreview |
| `app/src/main/java/com/vcam/ui/camera/components/CameraBottomRow.kt` | CameraBottomRow |

## Entry Points

Start here when exploring this area:

- **`filtersInCategory`** (Function) — `app/src/main/java/com/vcam/data/Filters.kt:90`
- **`FilterBrowserScreen`** (Function) — `app/src/main/java/com/vcam/ui/filters/FilterBrowserScreen.kt:37`
- **`photoKindAt`** (Function) — `app/src/main/java/com/vcam/ui/components/PhotoPlaceholder.kt:72`
- **`FilterGrid`** (Function) — `app/src/main/java/com/vcam/ui/filters/components/FilterGrid.kt:34`
- **`FilterRibbon`** (Function) — `app/src/main/java/com/vcam/ui/camera/components/FilterRibbon.kt:28`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `filtersInCategory` | Function | `app/src/main/java/com/vcam/data/Filters.kt` | 90 |
| `FilterBrowserScreen` | Function | `app/src/main/java/com/vcam/ui/filters/FilterBrowserScreen.kt` | 37 |
| `photoKindAt` | Function | `app/src/main/java/com/vcam/ui/components/PhotoPlaceholder.kt` | 72 |
| `FilterGrid` | Function | `app/src/main/java/com/vcam/ui/filters/components/FilterGrid.kt` | 34 |
| `FilterRibbon` | Function | `app/src/main/java/com/vcam/ui/camera/components/FilterRibbon.kt` | 28 |
| `photoBrush` | Function | `app/src/main/java/com/vcam/ui/components/PhotoPlaceholder.kt` | 11 |
| `PhotoPlaceholder` | Function | `app/src/main/java/com/vcam/ui/components/PhotoPlaceholder.kt` | 74 |
| `FilterThumb` | Function | `app/src/main/java/com/vcam/ui/components/FilterThumb.kt` | 15 |
| `HeroPreview` | Function | `app/src/main/java/com/vcam/ui/filters/components/HeroPreview.kt` | 25 |
| `CameraBottomRow` | Function | `app/src/main/java/com/vcam/ui/camera/components/CameraBottomRow.kt` | 23 |
| `IntensitySlider` | Function | `app/src/main/java/com/vcam/ui/components/IntensitySlider.kt` | 33 |
| `drawTrack` | Function | `app/src/main/java/com/vcam/ui/components/IntensitySlider.kt` | 104 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `OnCreate → PhotoBrush` | cross_community | 5 |
| `OnCreate → PhotoKindAt` | cross_community | 5 |
| `OnCreate → FiltersInCategory` | cross_community | 4 |

## How to Explore

1. `gitnexus_context({name: "filtersInCategory"})` — see callers and callees
2. `gitnexus_query({query: "components"})` — find related execution flows
3. Read key files listed above for implementation details
