// vcam-settings.jsx — Settings screen

function SettingsRow({ label, sub, control, last }) {
  return (
    <div style={{
      padding: '14px 18px', display: 'flex', alignItems: 'center', gap: 14,
      borderBottom: last ? 'none' : `0.5px solid ${V.divider}`,
    }}>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontFamily: VFonts.ui, fontSize: 14, color: V.ink, fontWeight: 500 }}>{label}</div>
        {sub && <div style={{ fontFamily: VFonts.ui, fontSize: 11.5, color: V.ink50, marginTop: 2 }}>{sub}</div>}
      </div>
      {control}
    </div>
  );
}

function Toggle({ on, accent = V.coral }) {
  return (
    <div style={{
      width: 38, height: 22, borderRadius: 11, position: 'relative',
      background: on ? accent : V.ink12,
      transition: 'background .2s',
    }}>
      <div style={{
        position: 'absolute', top: 2, left: on ? 18 : 2,
        width: 18, height: 18, borderRadius: 9, background: '#fff',
        boxShadow: '0 1px 3px rgba(21,17,14,0.18)',
        transition: 'left .2s',
      }} />
    </div>
  );
}

function ValueChev({ value }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 6, color: V.ink50 }}>
      <span style={{ fontFamily: VFonts.ui, fontSize: 13 }}>{value}</span>
      <Ico.chev style={{ color: V.ink30 }} />
    </div>
  );
}

function ThemePicker({ value, accent = V.coral }) {
  const opts = ['Light', 'Dark', 'System'];
  return (
    <div style={{ display: 'flex', padding: 2, borderRadius: 10, background: V.ink06, gap: 2 }}>
      {opts.map(o => {
        const a = o === value;
        return (
          <div key={o} style={{
            padding: '5px 10px', borderRadius: 8, fontSize: 11.5,
            fontFamily: VFonts.ui, fontWeight: a ? 600 : 500,
            background: a ? V.paper : 'transparent',
            color: a ? V.ink : V.ink70,
            boxShadow: a ? '0 1px 2px rgba(21,17,14,0.08)' : 'none',
          }}>{o}</div>
        );
      })}
    </div>
  );
}

function SettingsScreen({ accent = V.coral }) {
  return (
    <Phone width={PHONE_W} height={PHONE_H}>
      <StatusBar />
      <div style={{ padding: '14px 18px 14px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <button style={{ appearance: 'none', border: 0, background: 'transparent', color: V.ink, display: 'flex' }}>
          <Ico.back />
        </button>
        <div style={{ fontFamily: VFonts.ui, fontWeight: 600, fontSize: 16, color: V.ink }}>Settings</div>
        <div style={{ width: 22 }} />
      </div>

      <div style={{ flex: 1, overflow: 'hidden' }}>
        {/* Capture section */}
        <div style={{ padding: '18px 18px 6px', fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1.5, color: V.ink50 }}>CAPTURE</div>
        <div style={{ background: V.paperWarm, borderTop: `0.5px solid ${V.divider}`, borderBottom: `0.5px solid ${V.divider}` }}>
          <SettingsRow label="Save original photo" sub="Keep an unfiltered copy alongside" control={<Toggle on accent={accent} />} />
          <SettingsRow label="Auto-save to gallery" control={<Toggle on accent={accent} />} />
          <SettingsRow label="Grid lines" sub="Rule-of-thirds overlay" control={<Toggle accent={accent} />} />
          <SettingsRow label="Camera sound" control={<Toggle accent={accent} />} last />
        </div>

        {/* Defaults */}
        <div style={{ padding: '18px 18px 6px', fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1.5, color: V.ink50 }}>DEFAULTS</div>
        <div style={{ background: V.paperWarm, borderTop: `0.5px solid ${V.divider}`, borderBottom: `0.5px solid ${V.divider}` }}>
          <SettingsRow label="Default aspect ratio" control={<ValueChev value="4:3" />} />
          <SettingsRow label="Default filter" control={<ValueChev value="Crisp 01" />} />
          <SettingsRow label="Default intensity" control={<ValueChev value="80" />} last />
        </div>

        {/* Appearance */}
        <div style={{ padding: '18px 18px 6px', fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1.5, color: V.ink50 }}>APPEARANCE</div>
        <div style={{ background: V.paperWarm, borderTop: `0.5px solid ${V.divider}`, borderBottom: `0.5px solid ${V.divider}` }}>
          <SettingsRow label="App theme" control={<ThemePicker value="Light" accent={accent} />} last />
        </div>

        <div style={{ padding: '22px 18px 6px', fontFamily: VFonts.mono, fontSize: 10, letterSpacing: 1.5, color: V.ink50 }}>ABOUT</div>
        <div style={{ background: V.paperWarm, borderTop: `0.5px solid ${V.divider}`, borderBottom: `0.5px solid ${V.divider}` }}>
          <SettingsRow label="Version" control={<span style={{ fontFamily: VFonts.mono, fontSize: 12, color: V.ink50 }}>2.4.1</span>} last />
        </div>
      </div>
      <GestureBar />
    </Phone>
  );
}

Object.assign(window, { SettingsScreen });
