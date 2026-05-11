// vcam-camera.jsx — Camera screens (3 variations)

const PHONE_W = 360;
const PHONE_H = 720;

// Translucent chip used over the dark camera preview
function Chip({ children, active, onClick, style }) {
  return (
    <button onClick={onClick} style={{
      appearance: 'none', border: 0, padding: '6px 12px',
      borderRadius: 999, fontFamily: VFonts.ui, fontSize: 12,
      fontWeight: active ? 600 : 500,
      color: active ? V.ink : 'rgba(255,255,255,0.95)',
      background: active ? V.beige : 'rgba(255,255,255,0.14)',
      backdropFilter: 'blur(12px)', WebkitBackdropFilter: 'blur(12px)',
      letterSpacing: 0.2,
      ...style,
    }}>{children}</button>
  );
}

function IconBtn({ children, onClick, active, style }) {
  return (
    <button onClick={onClick} style={{
      appearance: 'none', border: 0, padding: 0,
      width: 38, height: 38, borderRadius: 19,
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: active ? V.coral : 'rgba(255,255,255,0.14)',
      backdropFilter: 'blur(12px)', WebkitBackdropFilter: 'blur(12px)',
      color: active ? '#fff' : 'rgba(255,255,255,0.95)',
      cursor: 'pointer',
      ...style,
    }}>{children}</button>
  );
}

// Grid overlay
function RuleOfThirds({ show }) {
  if (!show) return null;
  return (
    <svg viewBox="0 0 3 3" preserveAspectRatio="none" style={{
      position: 'absolute', inset: 0, width: '100%', height: '100%',
      pointerEvents: 'none', opacity: 0.5,
    }}>
      <path d="M1 0v3M2 0v3M0 1h3M0 2h3" stroke="white" strokeWidth="0.012" fill="none"/>
    </svg>
  );
}

// Shutter buttons — three different treatments
function ShutterClassic({ accent }) {
  return (
    <div style={{
      width: 72, height: 72, borderRadius: 36,
      border: '3px solid rgba(255,255,255,0.95)',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: 'transparent',
    }}>
      <div style={{ width: 58, height: 58, borderRadius: 29, background: '#fff' }} />
    </div>
  );
}

function ShutterRing({ accent }) {
  return (
    <div style={{
      width: 76, height: 76, borderRadius: 38,
      border: `2px solid ${accent}`,
      padding: 4, boxSizing: 'border-box',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
    }}>
      <div style={{
        width: '100%', height: '100%', borderRadius: '50%',
        background: '#fff',
      }} />
    </div>
  );
}

function ShutterGradient({ accent }) {
  return (
    <div style={{
      width: 76, height: 76, borderRadius: 38,
      background: `conic-gradient(from 200deg, ${accent}, ${V.beige}, ${accent})`,
      padding: 4, boxSizing: 'border-box',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
    }}>
      <div style={{ width: '100%', height: '100%', borderRadius: '50%', background: '#fff' }} />
    </div>
  );
}

// ── Filter ribbon at the bottom (small thumbs) ────────────
function FilterRibbon({ active, accent, variant = 'circle' }) {
  const list = FILTERS.slice(0, 12);
  const r = variant === 'circle' ? 100 : variant === 'pill' ? 12 : 8;
  return (
    <div style={{ display: 'flex', gap: 10, alignItems: 'center', overflowX: 'hidden', overflowY: 'visible', padding: '6px 16px' }}>
      {list.map((f, i) => {
        const isActive = i === active;
        const photoKeys = Object.keys(PHOTO_SOURCES);
        return (
          <div key={f.id} style={{
            position: 'relative', flexShrink: 0,
            transform: isActive ? 'scale(1.12)' : 'scale(1)',
            transition: 'transform .2s',
          }}>
            <FilterTile filter={f} size={variant === 'pill' ? 52 : 44}
              radius={variant === 'circle' ? 22 : variant === 'pill' ? 12 : 8}
              photo={photoKeys[i % photoKeys.length]} />
            {isActive && (
              <div style={{
                position: 'absolute', inset: -2,
                borderRadius: variant === 'circle' ? 24 : variant === 'pill' ? 14 : 10,
                border: `2px solid ${accent}`,
                pointerEvents: 'none',
              }} />
            )}
          </div>
        );
      })}
    </div>
  );
}

// ── Aspect ratio strip (16:9, 4:3, 1:1, FULL) ─────────────
function RatioStrip({ value, accent }) {
  const opts = ['1:1', '4:3', '16:9', 'FULL'];
  return (
    <div style={{ display: 'flex', gap: 18, justifyContent: 'center', fontFamily: VFonts.mono, fontSize: 11, fontWeight: 500, letterSpacing: 0.4, color: 'rgba(255,255,255,0.6)' }}>
      {opts.map((o) => (
        <span key={o} style={{ color: o === value ? accent : 'rgba(255,255,255,0.6)' }}>{o}</span>
      ))}
    </div>
  );
}

// Top bar over camera
function CameraTopBar({ flash, timer, ratio, accent }) {
  return (
    <div style={{
      position: 'absolute', top: 30, left: 0, right: 0,
      padding: '8px 14px',
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      color: 'rgba(255,255,255,0.95)',
    }}>
      <IconBtn><Ico.settings /></IconBtn>
      <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
        <Chip>{flash}</Chip>
        <Chip>{timer === 0 ? 'OFF' : `${timer}s`}</Chip>
        <Chip>{ratio}</Chip>
      </div>
      <IconBtn><Ico.grid /></IconBtn>
    </div>
  );
}

// Bottom area — gallery thumb · shutter · flip
function BottomRow({ shutter, accent, variant = 'circle' }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      padding: '0 24px',
    }}>
      {/* Gallery thumb */}
      <div style={{
        width: 44, height: 44, borderRadius: 12, overflow: 'hidden',
        background: PHOTO_SOURCES.sushi, border: '1.5px solid rgba(255,255,255,0.8)',
      }}>
        <div style={{ width: '100%', height: '100%',
          background: 'linear-gradient(135deg, rgba(255,200,150,0.2), rgba(120,60,30,0.1))',
          mixBlendMode: 'overlay',
        }} />
      </div>
      {shutter}
      <IconBtn style={{ width: 44, height: 44, borderRadius: 22 }}>
        <Ico.flip />
      </IconBtn>
    </div>
  );
}

// ─────────────────────────────────────────────────────────
// Variation A — Classic: minimal, filter name above shutter
// ─────────────────────────────────────────────────────────
function CameraClassic({ accent = V.coral, activeFilter = 0, ratio = '4:3', grid = true }) {
  const f = FILTERS[activeFilter];
  return (
    <Phone dark width={PHONE_W} height={PHONE_H}>
      <StatusBar dark />
      <div style={{
        flex: 1, position: 'relative', overflow: 'hidden',
        background: '#0a0908',
      }}>
        {/* Viewfinder area — 4:3 aspect */}
        <div style={{
          position: 'absolute', top: 70, left: 0, right: 0,
          height: PHONE_W * 4/3, overflow: 'hidden',
          background: HERO_PHOTO,
        }}>
          <div style={{ position: 'absolute', inset: 0, background: f.tint, mixBlendMode: 'overlay' }} />
          <RuleOfThirds show={grid} />
          {/* Focus ring */}
          <div style={{
            position: 'absolute', top: '38%', left: '42%',
            width: 56, height: 56, border: `1.4px solid ${accent}`,
            borderRadius: 4, opacity: 0.9,
          }} />
        </div>

        {/* Top bar */}
        <CameraTopBar flash="AUTO" timer={0} ratio={ratio} accent={accent} />

        {/* Filter name pill above shutter */}
        <div style={{
          position: 'absolute', left: 0, right: 0, bottom: 198,
          display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 10,
          color: '#fff',
        }}>
          <Ico.chev style={{ transform: 'rotate(180deg)', opacity: 0.5 }} />
          <div style={{ textAlign: 'center' }}>
            <div style={{ fontFamily: VFonts.serif, fontSize: 22, lineHeight: 1.05, fontStyle: 'italic' }}>{f.name}</div>
            <div style={{ fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1.2, opacity: 0.65, marginTop: 3 }}>
              {f.cat.toUpperCase()} · {f.code}
            </div>
          </div>
          <Ico.chev style={{ opacity: 0.5 }} />
        </div>

        {/* Filter ribbon */}
        <div style={{ position: 'absolute', left: 0, right: 0, bottom: 130 }}>
          <FilterRibbon active={activeFilter} accent={accent} variant="circle" />
        </div>

        {/* Aspect ratio */}
        <div style={{ position: 'absolute', left: 0, right: 0, bottom: 102 }}>
          <RatioStrip value={ratio} accent={accent} />
        </div>

        {/* Bottom row */}
        <div style={{ position: 'absolute', left: 0, right: 0, bottom: 28 }}>
          <BottomRow shutter={<ShutterClassic accent={accent} />} accent={accent} />
        </div>
      </div>
      <div style={{ background: '#0a0908' }}><GestureBar dark /></div>
    </Phone>
  );
}

// ─────────────────────────────────────────────────────────
// Variation B — Pro: dense bottom toolbar with chips
// ─────────────────────────────────────────────────────────
function CameraPro({ accent = V.coral, activeFilter = 6, ratio = '1:1' }) {
  const f = FILTERS[activeFilter];
  return (
    <Phone dark width={PHONE_W} height={PHONE_H}>
      <StatusBar dark />
      <div style={{
        flex: 1, position: 'relative', overflow: 'hidden', background: '#0a0908',
      }}>
        {/* 1:1 viewfinder, centered */}
        <div style={{
          position: 'absolute', top: 92, left: 0, right: 0,
          height: PHONE_W, overflow: 'hidden',
          background: PHOTO_SOURCES.portrait,
        }}>
          <div style={{ position: 'absolute', inset: 0, background: f.tint, mixBlendMode: 'overlay' }} />
          {/* Letterbox lines */}
        </div>

        {/* Top bar */}
        <div style={{
          position: 'absolute', top: 30, left: 0, right: 0,
          padding: '8px 14px',
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          color: 'rgba(255,255,255,0.95)',
        }}>
          <IconBtn><Ico.settings /></IconBtn>
          <div style={{ fontFamily: VFonts.mono, fontSize: 11, letterSpacing: 1.6, color: 'rgba(255,255,255,0.7)' }}>
            V·CAM
          </div>
          <IconBtn><Ico.grid /></IconBtn>
        </div>

        {/* Quick controls strip below status */}
        <div style={{
          position: 'absolute', top: 76, left: 14, right: 14,
          display: 'flex', justifyContent: 'space-between', alignItems: 'center',
          fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1, color: 'rgba(255,255,255,0.78)',
        }}>
          <span style={{ display: 'flex', alignItems: 'center', gap: 5 }}><Ico.flashAuto style={{ width: 14, height: 14 }}/> AUTO</span>
          <span style={{ display: 'flex', alignItems: 'center', gap: 5 }}><Ico.timer style={{ width: 14, height: 14 }}/> OFF</span>
          <span style={{ color: accent }}>{ratio}</span>
          <span>EV 0.0</span>
        </div>

        {/* Filter card above ribbon */}
        <div style={{
          position: 'absolute', left: 14, right: 14, bottom: 220,
          padding: '12px 16px',
          borderRadius: 16,
          background: 'rgba(20,16,12,0.55)',
          backdropFilter: 'blur(20px)', WebkitBackdropFilter: 'blur(20px)',
          color: '#fff',
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        }}>
          <div>
            <div style={{ fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1.5, color: 'rgba(255,255,255,0.55)' }}>
              {f.cat.toUpperCase()} · {f.code}
            </div>
            <div style={{ fontFamily: VFonts.serif, fontSize: 26, lineHeight: 1.1, fontStyle: 'italic', marginTop: 1 }}>{f.name}</div>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Ico.heart style={{ color: accent, width: 16, height: 16 }} />
            <span style={{ fontFamily: VFonts.mono, fontSize: 12, color: accent }}>72</span>
          </div>
        </div>

        {/* Filter ribbon — squares */}
        <div style={{ position: 'absolute', left: 0, right: 0, bottom: 132 }}>
          <FilterRibbon active={activeFilter} accent={accent} variant="square" />
        </div>

        {/* Bottom row */}
        <div style={{ position: 'absolute', left: 0, right: 0, bottom: 28 }}>
          <BottomRow shutter={<ShutterRing accent={accent} />} accent={accent} />
        </div>
      </div>
      <div style={{ background: '#0a0908' }}><GestureBar dark /></div>
    </Phone>
  );
}

// ─────────────────────────────────────────────────────────
// Variation C — Minimal: floating shutter, no chrome
// ─────────────────────────────────────────────────────────
function CameraMinimal({ accent = V.coral, activeFilter = 2, ratio = '16:9' }) {
  const f = FILTERS[activeFilter];
  return (
    <Phone dark width={PHONE_W} height={PHONE_H}>
      <StatusBar dark />
      <div style={{ flex: 1, position: 'relative', overflow: 'hidden', background: '#0a0908' }}>
        {/* Full-bleed viewfinder */}
        <div style={{ position: 'absolute', inset: 0, background: PHOTO_SOURCES.cocktail }}>
          <div style={{ position: 'absolute', inset: 0, background: f.tint, mixBlendMode: 'overlay' }} />
          {/* dark gradient overlays for legibility */}
          <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: 180,
            background: 'linear-gradient(180deg, rgba(0,0,0,0.5), transparent)' }} />
          <div style={{ position: 'absolute', bottom: 0, left: 0, right: 0, height: 280,
            background: 'linear-gradient(0deg, rgba(0,0,0,0.7), transparent)' }} />
        </div>

        {/* Single top control column on right */}
        <div style={{
          position: 'absolute', top: 50, right: 14, display: 'flex', flexDirection: 'column', gap: 12,
        }}>
          <IconBtn><Ico.flashAuto /></IconBtn>
          <IconBtn><Ico.timer /></IconBtn>
          <IconBtn><Ico.grid /></IconBtn>
        </div>

        {/* Filter name centered top */}
        <div style={{
          position: 'absolute', top: 64, left: 0, right: 0, textAlign: 'center', color: '#fff',
        }}>
          <div style={{ fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1.6, opacity: 0.65 }}>
            {ratio} · {f.cat.toUpperCase()}
          </div>
          <div style={{ fontFamily: VFonts.serif, fontStyle: 'italic', fontSize: 30, lineHeight: 1.1, marginTop: 2 }}>{f.name}</div>
        </div>

        {/* Bottom dock — pill */}
        <div style={{
          position: 'absolute', left: 16, right: 16, bottom: 32,
          display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 16,
        }}>
          <div style={{
            width: '100%',
            background: 'rgba(20,16,12,0.45)',
            backdropFilter: 'blur(20px)', WebkitBackdropFilter: 'blur(20px)',
            borderRadius: 26, padding: '12px 0',
          }}>
            <FilterRibbon active={activeFilter} accent={accent} variant="pill" />
          </div>
          <div style={{
            width: '100%',
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            padding: '0 8px',
          }}>
            <div style={{ width: 44, height: 44, borderRadius: 12, overflow: 'hidden',
              background: PHOTO_SOURCES.salad, border: '1.5px solid rgba(255,255,255,0.8)' }} />
            <ShutterGradient accent={accent} />
            <IconBtn style={{ width: 44, height: 44, borderRadius: 22 }}>
              <Ico.flip />
            </IconBtn>
          </div>
        </div>
      </div>
      <div style={{ background: '#0a0908' }}><GestureBar dark /></div>
    </Phone>
  );
}

Object.assign(window, { CameraClassic, CameraPro, CameraMinimal, Chip, IconBtn, FilterRibbon, ShutterClassic, ShutterRing, ShutterGradient, PHONE_W, PHONE_H });
