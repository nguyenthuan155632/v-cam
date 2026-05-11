// vcam-filters.jsx — Filter Browser & Intensity-in-action

function FilterBrowser({ accent = V.coral, activeCat = 'Food', activeFilter = 0, intensity = 80 }) {
  const cats = CATEGORIES;
  const inCat = FILTERS.filter(f => f.cat === activeCat);
  const f = inCat[activeFilter] || inCat[0];
  const photoKeys = Object.keys(PHOTO_SOURCES);

  return (
    <Phone width={PHONE_W} height={PHONE_H}>
      <StatusBar />
      {/* Header */}
      <div style={{
        padding: '14px 18px 10px', display: 'flex', alignItems: 'center',
        justifyContent: 'space-between',
      }}>
        <button style={{ appearance: 'none', border: 0, background: 'transparent', display: 'flex', alignItems: 'center', color: V.ink }}>
          <Ico.back />
        </button>
        <div style={{ textAlign: 'center' }}>
          <div style={{ fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1.6, color: V.ink50 }}>FILTERS</div>
          <div style={{ fontFamily: VFonts.serif, fontStyle: 'italic', fontSize: 22, lineHeight: 1.1 }}>Library</div>
        </div>
        <button style={{ appearance: 'none', border: 0, background: 'transparent', display: 'flex', alignItems: 'center', color: V.ink }}>
          <Ico.search />
        </button>
      </div>

      {/* Category pills */}
      <div style={{ padding: '0 14px 12px', display: 'flex', gap: 8, overflowX: 'hidden' }}>
        {cats.map((c) => {
          const a = c === activeCat;
          return (
            <span key={c} style={{
              padding: '6px 12px', borderRadius: 999, fontSize: 12,
              fontWeight: a ? 600 : 500, flexShrink: 0,
              background: a ? V.ink : V.ink06,
              color: a ? V.paper : V.ink70,
              fontFamily: VFonts.ui,
            }}>{c}</span>
          );
        })}
      </div>

      {/* Hero preview */}
      <div style={{ padding: '0 14px' }}>
        <div style={{
          width: '100%', aspectRatio: '4/3', borderRadius: 16, overflow: 'hidden',
          position: 'relative', background: PHOTO_SOURCES.pancakes,
        }}>
          <div style={{ position: 'absolute', inset: 0, background: f.tint, mixBlendMode: 'overlay', opacity: intensity/100 }} />
          <div style={{
            position: 'absolute', left: 14, bottom: 14,
            color: '#fff', textShadow: '0 1px 8px rgba(0,0,0,0.4)',
          }}>
            <div style={{ fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1.4, opacity: 0.85 }}>{f.cat.toUpperCase()} · {f.code}</div>
            <div style={{ fontFamily: VFonts.serif, fontStyle: 'italic', fontSize: 26, lineHeight: 1.1, marginTop: 2 }}>{f.name}</div>
          </div>
          {/* compare A/B handle */}
          <div style={{
            position: 'absolute', top: 14, right: 14, padding: '4px 10px',
            borderRadius: 999, background: 'rgba(20,16,12,0.5)', color: '#fff',
            fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1.2,
            backdropFilter: 'blur(10px)',
          }}>HOLD TO COMPARE</div>
        </div>
      </div>

      {/* Intensity slider */}
      <div style={{ padding: '14px 18px 6px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: 8 }}>
          <span style={{ fontFamily: VFonts.ui, fontSize: 12, color: V.ink70, fontWeight: 500 }}>Intensity</span>
          <span style={{ fontFamily: VFonts.mono, fontSize: 13, color: V.ink }}>{intensity}</span>
        </div>
        <div style={{ position: 'relative', height: 22, display: 'flex', alignItems: 'center' }}>
          <div style={{ position: 'absolute', left: 0, right: 0, height: 3, borderRadius: 2, background: V.ink12 }} />
          <div style={{ position: 'absolute', left: 0, width: `${intensity}%`, height: 3, borderRadius: 2, background: accent }} />
          {/* ticks */}
          {[0, 25, 50, 75, 100].map(t => (
            <div key={t} style={{ position: 'absolute', left: `${t}%`, width: 1, height: 8, top: 7, background: V.ink30, transform: 'translateX(-0.5px)' }} />
          ))}
          <div style={{
            position: 'absolute', left: `${intensity}%`, width: 22, height: 22,
            borderRadius: 11, background: '#fff', boxShadow: '0 1px 4px rgba(21,17,14,0.2), 0 0 0 1.5px ' + accent,
            transform: 'translateX(-11px)',
          }} />
        </div>
      </div>

      {/* Filter grid */}
      <div style={{ padding: '6px 14px 14px', overflowY: 'hidden', flex: 1 }}>
        <div style={{
          display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 10,
        }}>
          {FILTERS.filter(x => x.cat === activeCat).map((flt, i) => {
            const a = i === activeFilter;
            return (
              <div key={flt.id} style={{ textAlign: 'center' }}>
                <div style={{ position: 'relative' }}>
                  <FilterTile filter={flt} size="100%" radius={12} photo={photoKeys[i % photoKeys.length]}>
                    {/* spacer so aspect square */}
                    <div style={{ paddingTop: '100%' }} />
                  </FilterTile>
                  {a && (
                    <div style={{
                      position: 'absolute', inset: -2, border: `2px solid ${accent}`,
                      borderRadius: 14, pointerEvents: 'none',
                    }} />
                  )}
                </div>
                <div style={{ fontFamily: VFonts.ui, fontSize: 11, fontWeight: a ? 600 : 500, color: a ? V.ink : V.ink70, marginTop: 6 }}>{flt.name}</div>
                <div style={{ fontFamily: VFonts.mono, fontSize: 9, color: V.ink30, letterSpacing: 0.6 }}>{flt.code}</div>
              </div>
            );
          })}
          {/* spacer cards */}
          {Array.from({ length: Math.max(0, 8 - FILTERS.filter(x => x.cat === activeCat).length) }).map((_,i) => (
            <div key={i} style={{ aspectRatio: '1', borderRadius: 12, background: V.ink06, display: 'flex', alignItems: 'center', justifyContent: 'center', color: V.ink30, fontSize: 18 }}>+</div>
          ))}
        </div>
      </div>
      <GestureBar />
    </Phone>
  );
}

// ── Intensity in action: horizontal slider sheet above filter ribbon ───
function CameraIntensity({ accent = V.coral, activeFilter = 1, intensity = 65 }) {
  const f = FILTERS[activeFilter];
  return (
    <Phone dark width={PHONE_W} height={PHONE_H}>
      <StatusBar dark />
      <div style={{ flex: 1, position: 'relative', overflow: 'hidden', background: '#0a0908' }}>
        <div style={{ position: 'absolute', inset: 0, background: PHOTO_SOURCES.pasta }}>
          <div style={{ position: 'absolute', inset: 0, background: f.tint, mixBlendMode: 'overlay', opacity: intensity/100 }} />
        </div>

        {/* Top bar */}
        <div style={{ position: 'absolute', top: 30, left: 0, right: 0, padding: '8px 14px', display: 'flex', justifyContent: 'space-between' }}>
          <IconBtn><Ico.settings /></IconBtn>
          <div style={{ display: 'flex', gap: 8 }}>
            <Chip>AUTO</Chip><Chip>OFF</Chip><Chip>4:3</Chip>
          </div>
          <IconBtn><Ico.grid /></IconBtn>
        </div>

        {/* Centered intensity readout (small, unobtrusive) */}
        <div style={{
          position: 'absolute', left: '50%', top: 90, transform: 'translateX(-50%)',
          padding: '6px 14px', borderRadius: 999,
          background: 'rgba(20,16,12,0.5)',
          backdropFilter: 'blur(16px)', WebkitBackdropFilter: 'blur(16px)',
          color: '#fff', display: 'flex', alignItems: 'center', gap: 8,
        }}>
          <span style={{ fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1.4, color: 'rgba(255,255,255,0.65)' }}>{f.code}</span>
          <span style={{ width: 1, height: 10, background: 'rgba(255,255,255,0.25)' }} />
          <span style={{ fontFamily: VFonts.serif, fontStyle: 'italic', fontSize: 16, lineHeight: 1 }}>{f.name}</span>
        </div>

        {/* Intensity sheet — sits just above filter ribbon */}
        <div style={{
          position: 'absolute', left: 16, right: 16, bottom: 218,
          padding: '14px 16px 14px',
          borderRadius: 20,
          background: 'rgba(20,16,12,0.55)',
          backdropFilter: 'blur(20px)', WebkitBackdropFilter: 'blur(20px)',
          border: '0.5px solid rgba(255,255,255,0.08)',
        }}>
          <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 10 }}>
            <span style={{ fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1.4, color: 'rgba(255,255,255,0.6)' }}>INTENSITY</span>
            <span style={{ display: 'flex', alignItems: 'baseline', gap: 3, color: '#fff' }}>
              <span style={{ fontFamily: VFonts.serif, fontStyle: 'italic', fontSize: 22, lineHeight: 1, color: accent }}>{intensity}</span>
              <span style={{ fontFamily: VFonts.mono, fontSize: 10, color: 'rgba(255,255,255,0.5)' }}>/ 100</span>
            </span>
          </div>
          {/* horizontal slider */}
          <div style={{ position: 'relative', height: 20, display: 'flex', alignItems: 'center' }}>
            <div style={{ position: 'absolute', left: 0, right: 0, height: 3, borderRadius: 2, background: 'rgba(255,255,255,0.18)' }} />
            <div style={{ position: 'absolute', left: 0, width: `${intensity}%`, height: 3, borderRadius: 2, background: accent }} />
            {[0, 25, 50, 75, 100].map(t => (
              <div key={t} style={{ position: 'absolute', left: `${t}%`, width: 1, height: 7, top: 6.5, background: 'rgba(255,255,255,0.35)', transform: 'translateX(-0.5px)' }} />
            ))}
            <div style={{
              position: 'absolute', left: `${intensity}%`, width: 20, height: 20,
              borderRadius: 10, background: '#fff',
              boxShadow: `0 0 0 1.5px ${accent}, 0 2px 6px rgba(0,0,0,0.35)`,
              transform: 'translateX(-10px)',
            }} />
          </div>
        </div>

        {/* Filter ribbon */}
        <div style={{ position: 'absolute', left: 0, right: 0, bottom: 132 }}>
          <FilterRibbon active={activeFilter} accent={accent} variant="circle" />
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

// ── Long-press favorite menu (radial-ish bubble) ─────────────────
function LongPressFavorite({ accent = V.coral, activeFilter = 4 }) {
  const f = FILTERS[activeFilter];
  return (
    <Phone dark width={PHONE_W} height={PHONE_H}>
      <StatusBar dark />
      <div style={{ flex: 1, position: 'relative', overflow: 'hidden', background: '#0a0908' }}>
        <div style={{ position: 'absolute', inset: 0, background: PHOTO_SOURCES.coffee }}>
          <div style={{ position: 'absolute', inset: 0, background: f.tint, mixBlendMode: 'overlay' }} />
          <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.4)' }} />
        </div>

        {/* Top bar */}
        <div style={{ position: 'absolute', top: 30, left: 0, right: 0, padding: '8px 14px', display: 'flex', justifyContent: 'space-between' }}>
          <IconBtn><Ico.settings /></IconBtn>
          <div style={{ display: 'flex', gap: 8 }}>
            <Chip>AUTO</Chip><Chip>OFF</Chip><Chip>4:3</Chip>
          </div>
          <IconBtn><Ico.grid /></IconBtn>
        </div>

        {/* Floating popover above pressed filter thumb */}
        <div style={{
          position: 'absolute', left: '50%', top: '46%', transform: 'translate(-50%,-50%)',
          width: 240, padding: '14px 16px 12px',
          borderRadius: 18,
          background: 'rgba(28,22,18,0.78)',
          backdropFilter: 'blur(24px)',
          border: '0.5px solid rgba(255,255,255,0.08)',
          color: '#fff',
          boxShadow: '0 12px 40px rgba(0,0,0,0.5)',
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 14 }}>
            <FilterTile filter={f} size={56} radius={12} photo="croissant" />
            <div style={{ flex: 1 }}>
              <div style={{ fontFamily: VFonts.serif, fontStyle: 'italic', fontSize: 22, lineHeight: 1.05 }}>{f.name}</div>
              <div style={{ fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1.4, color: 'rgba(255,255,255,0.6)' }}>{f.cat.toUpperCase()} · {f.code}</div>
            </div>
          </div>
          {[
            { icon: <Ico.star style={{ color: accent }} />, label: 'Add to Favorites' },
            { icon: <Ico.heart style={{ color: 'rgba(255,255,255,0.85)' }} />, label: 'Pin to camera' },
            { icon: <Ico.search style={{ color: 'rgba(255,255,255,0.85)' }} />, label: 'Compare original' },
            { icon: <Ico.edit style={{ color: 'rgba(255,255,255,0.85)' }} />, label: 'Adjust intensity' },
          ].map((it, i) => (
            <div key={i} style={{
              padding: '9px 4px', display: 'flex', alignItems: 'center', gap: 12,
              borderTop: i ? '0.5px solid rgba(255,255,255,0.08)' : 'none',
              fontFamily: VFonts.ui, fontSize: 13.5, fontWeight: 500,
            }}>
              <div style={{ width: 18, display: 'flex', justifyContent: 'center' }}>{it.icon}</div>
              <span>{it.label}</span>
              <span style={{ flex: 1 }} />
              <Ico.chev style={{ opacity: 0.4 }} />
            </div>
          ))}
        </div>

        {/* Filter ribbon (shown beneath, slightly dim) */}
        <div style={{ position: 'absolute', left: 0, right: 0, bottom: 132, opacity: 0.55 }}>
          <FilterRibbon active={activeFilter} accent={accent} variant="circle" />
        </div>
        {/* visual finger-press */}
        <div style={{
          position: 'absolute', left: '50%', bottom: 140,
          width: 64, height: 64, borderRadius: 32,
          border: `2px solid ${accent}`, opacity: 0.7,
          transform: 'translateX(-50%)',
        }} />
        <div style={{
          position: 'absolute', left: '50%', bottom: 158,
          width: 28, height: 28, borderRadius: 14,
          background: 'rgba(255,255,255,0.35)',
          transform: 'translateX(-50%)',
        }} />

        {/* Bottom row */}
        <div style={{ position: 'absolute', left: 0, right: 0, bottom: 28, opacity: 0.55 }}>
          <BottomRow shutter={<ShutterClassic accent={accent} />} accent={accent} />
        </div>
      </div>
      <div style={{ background: '#0a0908' }}><GestureBar dark /></div>
    </Phone>
  );
}

Object.assign(window, { FilterBrowser, CameraIntensity, LongPressFavorite });
