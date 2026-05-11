---
name: design-handoff-vcam
description: "Skill for the Design_handoff_vcam area of v-cam. 31 symbols across 2 files."
---

# Design_handoff_vcam

31 symbols | 2 files | Cohesion: 100%

## When to Use

- Working with code in `design_handoff_vcam/`
- Understanding how dcExport, toDataURL, scrapeCss work
- Modifying design_handoff_vcam-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `design_handoff_vcam/design-canvas.jsx` | dcExport, toDataURL, scrapeCss, walk, cloneStyled (+16) |
| `design_handoff_vcam/tweaks-panel.jsx` | move, segAt, onPointerDown, TweakNumber, clamp (+5) |

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `dcExport` | Function | `design_handoff_vcam/design-canvas.jsx` | 528 |
| `toDataURL` | Function | `design_handoff_vcam/design-canvas.jsx` | 530 |
| `scrapeCss` | Function | `design_handoff_vcam/design-canvas.jsx` | 540 |
| `walk` | Function | `design_handoff_vcam/design-canvas.jsx` | 548 |
| `cloneStyled` | Function | `design_handoff_vcam/design-canvas.jsx` | 572 |
| `save` | Function | `design_handoff_vcam/design-canvas.jsx` | 607 |
| `DCArtboardFrame` | Function | `design_handoff_vcam/design-canvas.jsx` | 641 |
| `doExport` | Function | `design_handoff_vcam/design-canvas.jsx` | 659 |
| `move` | Function | `design_handoff_vcam/tweaks-panel.jsx` | 273 |
| `segAt` | Function | `design_handoff_vcam/tweaks-panel.jsx` | 388 |
| `onPointerDown` | Function | `design_handoff_vcam/tweaks-panel.jsx` | 395 |
| `TweakNumber` | Function | `design_handoff_vcam/tweaks-panel.jsx` | 453 |
| `clamp` | Function | `design_handoff_vcam/tweaks-panel.jsx` | 454 |
| `zoomAt` | Function | `design_handoff_vcam/design-canvas.jsx` | 295 |
| `isMouseWheel` | Function | `design_handoff_vcam/design-canvas.jsx` | 313 |
| `onWheel` | Function | `design_handoff_vcam/design-canvas.jsx` | 317 |
| `onGestureChange` | Function | `design_handoff_vcam/design-canvas.jsx` | 343 |
| `onHostMsg` | Function | `design_handoff_vcam/design-canvas.jsx` | 376 |
| `DCFocusOverlay` | Function | `design_handoff_vcam/design-canvas.jsx` | 788 |
| `go` | Function | `design_handoff_vcam/design-canvas.jsx` | 798 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `DCArtboardFrame → ScrapeCss` | intra_community | 5 |
| `DCArtboardFrame → ToDataURL` | intra_community | 5 |

## How to Explore

1. `gitnexus_context({name: "dcExport"})` — see callers and callees
2. `gitnexus_query({query: "design_handoff_vcam"})` — find related execution flows
3. Read key files listed above for implementation details
