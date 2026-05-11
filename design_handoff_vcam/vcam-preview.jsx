// vcam-preview.jsx — Photo Preview (post-capture) + 2 variations

function PhotoPreview({ accent = V.coral, activeFilter = 0, intensity = 100, variant = 'classic' }) {
  const f = FILTERS[activeFilter];
  const photoKeys = Object.keys(PHOTO_SOURCES);

  return (
    <Phone width={PHONE_W} height={PHONE_H}>
      <StatusBar />
      {/* Top bar */}
      <div style={{
        padding: '12px 14px 8px', display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      }}>
        <button style={{ appearance: 'none', border: 0, background: 'transparent', color: V.ink, display: 'flex' }}>
          <Ico.close />
        </button>
        <div style={{ textAlign: 'center' }}>
          <div style={{ fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1.6, color: V.ink50 }}>JUST CAPTURED</div>
          <div style={{ fontFamily: VFonts.ui, fontSize: 12, color: V.ink70, marginTop: 1 }}>10:14 · 4032 × 3024</div>
        </div>
        <button style={{ appearance: 'none', border: 0, background: 'transparent', color: V.ink, display: 'flex' }}>
          <Ico.star style={{ width: 18, height: 18, color: V.ink30 }} />
        </button>
      </div>

      {/* Photo */}
      <div style={{ padding: '6px 14px 12px' }}>
        <div style={{
          width: '100%', aspectRatio: '4/3', borderRadius: 16, overflow: 'hidden',
          position: 'relative', background: PHOTO_SOURCES.pancakes,
          boxShadow: '0 4px 18px rgba(21,17,14,0.12)',
        }}>
          <div style={{ position: 'absolute', inset: 0, background: f.tint, mixBlendMode: 'overlay', opacity: intensity/100 }} />
          <div style={{
            position: 'absolute', top: 12, left: 12,
            padding: '4px 10px', borderRadius: 999,
            background: 'rgba(20,16,12,0.55)', backdropFilter: 'blur(10px)',
            color: '#fff', fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1.2,
          }}>{f.code} · {Math.round(intensity)}%</div>
        </div>
      </div>

      {/* Filter row — quick switch */}
      <div style={{ padding: '0 14px 12px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: 8 }}>
          <span style={{ fontFamily: VFonts.ui, fontSize: 12, fontWeight: 500, color: V.ink70 }}>Edit filter</span>
          <span style={{ fontFamily: VFonts.mono, fontSize: 11, color: V.ink50, letterSpacing: 1 }}>{f.cat.toUpperCase()}</span>
        </div>
        <div style={{ display: 'flex', gap: 10, overflowX: 'hidden', overflowY: 'visible', padding: '4px 0' }}>
          {FILTERS.slice(0, 8).map((flt, i) => {
            const a = i === activeFilter;
            return (
              <div key={flt.id} style={{ flexShrink: 0, textAlign: 'center' }}>
                <div style={{ position: 'relative' }}>
                  <FilterTile filter={flt} size={48} radius={10} photo={photoKeys[i % photoKeys.length]} />
                  {a && <div style={{
                    position: 'absolute', inset: -2, border: `2px solid ${accent}`,
                    borderRadius: 12, pointerEvents: 'none',
                  }} />}
                </div>
                <div style={{
                  fontFamily: VFonts.ui, fontSize: 10, fontWeight: a ? 600 : 500,
                  color: a ? V.ink : V.ink50, marginTop: 5,
                }}>{flt.name}</div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Intensity */}
      <div style={{ padding: '4px 18px 12px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: 8 }}>
          <span style={{ fontFamily: VFonts.ui, fontSize: 12, color: V.ink70, fontWeight: 500 }}>Intensity</span>
          <span style={{ fontFamily: VFonts.mono, fontSize: 13, color: V.ink }}>{intensity}</span>
        </div>
        <div style={{ position: 'relative', height: 22, display: 'flex', alignItems: 'center' }}>
          <div style={{ position: 'absolute', left: 0, right: 0, height: 3, borderRadius: 2, background: V.ink12 }} />
          <div style={{ position: 'absolute', left: 0, width: `${intensity}%`, height: 3, borderRadius: 2, background: accent }} />
          <div style={{
            position: 'absolute', left: `${intensity}%`, width: 22, height: 22,
            borderRadius: 11, background: '#fff', boxShadow: `0 1px 4px rgba(21,17,14,0.2), 0 0 0 1.5px ${accent}`,
            transform: 'translateX(-11px)',
          }} />
        </div>
      </div>

      {/* Action row */}
      <div style={{ flex: 1 }} />
      <div style={{ padding: '0 14px 16px', display: 'flex', gap: 10 }}>
        <button style={{
          appearance: 'none', border: `1px solid ${V.ink12}`, background: V.paper,
          color: V.ink70, padding: '13px 0', borderRadius: 14, flex: 1,
          fontFamily: VFonts.ui, fontWeight: 600, fontSize: 14,
          display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
        }}>
          <Ico.retake style={{ width: 16, height: 16 }} /> Retake
        </button>
        <button style={{
          appearance: 'none', border: 0, background: V.ink, color: V.paper,
          padding: '13px 0', borderRadius: 14, flex: 1.4,
          fontFamily: VFonts.ui, fontWeight: 600, fontSize: 14,
          display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
        }}>
          <Ico.download style={{ width: 16, height: 16 }} /> Save photo
        </button>
      </div>
      <GestureBar />
    </Phone>
  );
}

Object.assign(window, { PhotoPreview });
