// vcam-shared.jsx — phone frame, tokens, filter data, mock food photos

const V = {
  // warm coral + beige duo on a warm near-black/near-white system
  coral: '#F27A66',
  coralSoft: '#FBE3DC',
  beige: '#E8D3B8',
  beigeSoft: '#F5EBDD',
  ink: '#15110E',
  ink70: 'rgba(21,17,14,0.7)',
  ink50: 'rgba(21,17,14,0.5)',
  ink30: 'rgba(21,17,14,0.3)',
  ink12: 'rgba(21,17,14,0.12)',
  ink06: 'rgba(21,17,14,0.06)',
  paper: '#FAF7F2',
  paperWarm: '#F2EDE4',
  divider: 'rgba(21,17,14,0.08)',
};

// Web fonts
if (typeof document !== 'undefined' && !document.getElementById('vcam-fonts')) {
  const l = document.createElement('link');
  l.id = 'vcam-fonts';
  l.rel = 'stylesheet';
  l.href = 'https://fonts.googleapis.com/css2?family=Instrument+Serif:ital@0;1&family=DM+Sans:opsz,wght@9..40,400;9..40,500;9..40,600;9..40,700&family=JetBrains+Mono:wght@400;500&display=swap';
  document.head.appendChild(l);
}

const VFonts = {
  serif: '"Instrument Serif", "Times New Roman", serif',
  ui: '"DM Sans", -apple-system, system-ui, sans-serif',
  mono: '"JetBrains Mono", ui-monospace, monospace',
};

// ── Phone frame ─────────────────────────────────────────────
function Phone({ children, width = 360, height = 720, dark = false, style }) {
  return (
    <div style={{
      width, height, borderRadius: 38, overflow: 'hidden',
      background: dark ? '#0a0908' : V.paper,
      border: '1px solid rgba(21,17,14,0.12)',
      boxShadow: '0 1px 0 rgba(255,255,255,0.6) inset, 0 18px 50px rgba(21,17,14,0.18), 0 2px 6px rgba(21,17,14,0.08)',
      position: 'relative', fontFamily: VFonts.ui, color: V.ink,
      display: 'flex', flexDirection: 'column',
      ...style,
    }}>
      {children}
    </div>
  );
}

// ── Status bar (Android, gesture nav included) ──────────────
function StatusBar({ dark = false, time = '9:41' }) {
  const c = dark ? 'rgba(255,255,255,0.92)' : V.ink;
  return (
    <div style={{
      height: 30, display: 'flex', alignItems: 'center',
      justifyContent: 'space-between', padding: '0 18px 0 20px',
      position: 'relative', fontFamily: VFonts.ui, flexShrink: 0,
      color: c, fontSize: 12.5, fontWeight: 600, letterSpacing: 0.2,
    }}>
      <span>{time}</span>
      <div style={{
        position: 'absolute', left: '50%', top: 7,
        transform: 'translateX(-50%)',
        width: 14, height: 14, borderRadius: 7,
        background: dark ? '#0a0908' : '#2a2520',
      }} />
      <div style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
        {/* signal */}
        <svg width="14" height="10" viewBox="0 0 14 10" fill={c}>
          <rect x="0" y="7" width="2.5" height="3" rx="0.5"/>
          <rect x="3.5" y="5" width="2.5" height="5" rx="0.5"/>
          <rect x="7" y="2.5" width="2.5" height="7.5" rx="0.5"/>
          <rect x="10.5" y="0" width="2.5" height="10" rx="0.5"/>
        </svg>
        {/* wifi */}
        <svg width="13" height="10" viewBox="0 0 13 10" fill={c}>
          <path d="M6.5 1C4.2 1 2.1 1.9 0.5 3.4l1.1 1.2C2.9 3.4 4.6 2.7 6.5 2.7s3.6.7 4.9 1.9l1.1-1.2C10.9 1.9 8.8 1 6.5 1z"/>
          <path d="M2.6 5.6 3.7 6.8c.8-.8 1.9-1.2 2.8-1.2s2 .4 2.8 1.2l1.1-1.2c-1-1-2.4-1.6-3.9-1.6s-2.9.6-3.9 1.6z"/>
          <circle cx="6.5" cy="8.5" r="1.3"/>
        </svg>
        {/* battery */}
        <svg width="22" height="11" viewBox="0 0 22 11" fill="none">
          <rect x="0.5" y="0.5" width="19" height="10" rx="2.5" stroke={c} strokeOpacity="0.5"/>
          <rect x="2" y="2" width="14" height="7" rx="1.2" fill={c}/>
          <rect x="20.2" y="3.5" width="1.6" height="4" rx="0.5" fill={c} fillOpacity="0.5"/>
        </svg>
      </div>
    </div>
  );
}

function GestureBar({ dark = false }) {
  return (
    <div style={{
      height: 22, display: 'flex', alignItems: 'center', justifyContent: 'center',
      flexShrink: 0, background: dark ? 'transparent' : 'transparent',
    }}>
      <div style={{
        width: 108, height: 4, borderRadius: 2,
        background: dark ? 'rgba(255,255,255,0.85)' : V.ink,
        opacity: dark ? 0.85 : 0.85,
      }} />
    </div>
  );
}

// ── Filter library: each filter has a tint overlay + LUT-ish color shift ──
// The "live preview" is generated from a base food/lifestyle photo (CSS
// gradient placeholder) with a multiply/overlay tint to simulate the LUT.
const FILTERS = [
  // Food
  { id: 'fd01', cat: 'Food',     name: 'Crisp 01',   code: 'F·01', tint: 'linear-gradient(135deg, rgba(255,210,150,0.18), rgba(255,140,90,0.12))',  shift: 'saturate(1.2) contrast(1.05) brightness(1.03)' },
  { id: 'fd02', cat: 'Food',     name: 'Bake',       code: 'F·02', tint: 'linear-gradient(135deg, rgba(255,180,120,0.22), rgba(190,90,40,0.14))',   shift: 'saturate(1.1) contrast(1.08) brightness(0.98) sepia(0.05)' },
  { id: 'fd03', cat: 'Food',     name: 'Honey',      code: 'F·03', tint: 'linear-gradient(135deg, rgba(255,200,80,0.22), rgba(220,140,40,0.16))',   shift: 'saturate(1.18) contrast(1.04) brightness(1.05) sepia(0.08)' },
  // Cafe
  { id: 'cf01', cat: 'Cafe',     name: 'Latte',      code: 'C·01', tint: 'linear-gradient(135deg, rgba(220,190,150,0.25), rgba(140,100,70,0.18))',  shift: 'saturate(0.95) contrast(1.03) brightness(1.02) sepia(0.1)' },
  { id: 'cf02', cat: 'Cafe',     name: 'Roast',      code: 'C·02', tint: 'linear-gradient(135deg, rgba(180,140,100,0.28), rgba(80,50,30,0.18))',    shift: 'saturate(0.9) contrast(1.12) brightness(0.92) sepia(0.18)' },
  { id: 'cf03', cat: 'Cafe',     name: 'Mocha',      code: 'C·03', tint: 'linear-gradient(135deg, rgba(160,110,70,0.3), rgba(70,40,20,0.2))',       shift: 'saturate(0.92) contrast(1.1) brightness(0.95) sepia(0.15)' },
  // Portrait
  { id: 'pr01', cat: 'Portrait', name: 'Glow',       code: 'P·01', tint: 'linear-gradient(135deg, rgba(255,200,180,0.2), rgba(255,160,140,0.12))',  shift: 'saturate(1.05) contrast(0.98) brightness(1.05)' },
  { id: 'pr02', cat: 'Portrait', name: 'Soft',       code: 'P·02', tint: 'linear-gradient(135deg, rgba(255,220,210,0.22), rgba(240,180,170,0.14))', shift: 'saturate(0.95) contrast(0.95) brightness(1.06)' },
  { id: 'pr03', cat: 'Portrait', name: 'Skin Tone',  code: 'P·03', tint: 'linear-gradient(135deg, rgba(255,210,190,0.2), rgba(220,160,140,0.14))',  shift: 'saturate(1.02) contrast(1.02) brightness(1.04)' },
  // Travel
  { id: 'tr01', cat: 'Travel',   name: 'Coast',      code: 'T·01', tint: 'linear-gradient(135deg, rgba(170,210,220,0.2), rgba(255,210,150,0.14))',  shift: 'saturate(1.15) contrast(1.06) brightness(1.04)' },
  { id: 'tr02', cat: 'Travel',   name: 'Dune',       code: 'T·02', tint: 'linear-gradient(135deg, rgba(240,200,140,0.25), rgba(200,130,80,0.16))',  shift: 'saturate(1.1) contrast(1.08) brightness(1.03) sepia(0.08)' },
  { id: 'tr03', cat: 'Travel',   name: 'Kyoto',      code: 'T·03', tint: 'linear-gradient(135deg, rgba(230,170,160,0.22), rgba(180,140,170,0.14))', shift: 'saturate(1.04) contrast(1.05) brightness(1.0)' },
  // Vintage
  { id: 'vt01', cat: 'Vintage',  name: '1978',       code: 'V·01', tint: 'linear-gradient(135deg, rgba(220,170,90,0.28), rgba(160,90,60,0.2))',     shift: 'saturate(0.85) contrast(1.08) brightness(0.98) sepia(0.25)' },
  { id: 'vt02', cat: 'Vintage',  name: 'Polaroid',   code: 'V·02', tint: 'linear-gradient(135deg, rgba(255,230,180,0.22), rgba(190,150,110,0.14))', shift: 'saturate(0.92) contrast(0.94) brightness(1.06) sepia(0.18)' },
  { id: 'vt03', cat: 'Vintage',  name: 'Faded',      code: 'V·03', tint: 'linear-gradient(135deg, rgba(200,180,160,0.25), rgba(160,140,130,0.16))', shift: 'saturate(0.7) contrast(0.92) brightness(1.04)' },
  // Night
  { id: 'nt01', cat: 'Night',    name: 'Neon',       code: 'N·01', tint: 'linear-gradient(135deg, rgba(120,90,200,0.25), rgba(220,80,120,0.18))',   shift: 'saturate(1.3) contrast(1.18) brightness(0.95)' },
  { id: 'nt02', cat: 'Night',    name: 'Moody',      code: 'N·02', tint: 'linear-gradient(135deg, rgba(60,50,80,0.32), rgba(140,90,90,0.2))',       shift: 'saturate(1.05) contrast(1.22) brightness(0.85)' },
  { id: 'nt03', cat: 'Night',    name: 'Tungsten',   code: 'N·03', tint: 'linear-gradient(135deg, rgba(255,160,80,0.25), rgba(120,60,40,0.2))',     shift: 'saturate(1.15) contrast(1.12) brightness(0.92) sepia(0.12)' },
  // Clean
  { id: 'cl01', cat: 'Clean',    name: 'Bright',     code: 'L·01', tint: 'linear-gradient(135deg, rgba(255,255,255,0.1), rgba(240,240,240,0.06))',  shift: 'saturate(1.05) contrast(1.04) brightness(1.08)' },
  { id: 'cl02', cat: 'Clean',    name: 'Pure',      code: 'L·02', tint: 'linear-gradient(135deg, rgba(250,250,250,0.08), rgba(230,230,230,0.04))', shift: 'saturate(1.02) contrast(1.02) brightness(1.04)' },
  // Warm
  { id: 'wm01', cat: 'Warm',     name: 'Amber',      code: 'W·01', tint: 'linear-gradient(135deg, rgba(255,180,90,0.22), rgba(230,130,60,0.14))',   shift: 'saturate(1.15) contrast(1.05) brightness(1.03) sepia(0.1)' },
  { id: 'wm02', cat: 'Warm',     name: 'Toast',      code: 'W·02', tint: 'linear-gradient(135deg, rgba(240,170,100,0.25), rgba(200,120,70,0.16))',  shift: 'saturate(1.08) contrast(1.06) brightness(1.0) sepia(0.14)' },
  // Cool
  { id: 'co01', cat: 'Cool',     name: 'Mist',       code: 'X·01', tint: 'linear-gradient(135deg, rgba(170,200,220,0.22), rgba(130,170,200,0.14))', shift: 'saturate(0.95) contrast(1.04) brightness(1.04) hue-rotate(-6deg)' },
  { id: 'co02', cat: 'Cool',     name: 'Frost',      code: 'X·02', tint: 'linear-gradient(135deg, rgba(180,210,230,0.25), rgba(140,180,210,0.16))', shift: 'saturate(0.9) contrast(1.06) brightness(1.06) hue-rotate(-10deg)' },
];

const CATEGORIES = ['Food', 'Cafe', 'Portrait', 'Travel', 'Vintage', 'Night', 'Clean', 'Warm', 'Cool'];

// Base "photos" — pure CSS gradient placeholders that look food/lifestyle-y.
// Used as the source image; FilterTile overlays the filter tint on top.
const PHOTO_SOURCES = {
  pancakes: 'radial-gradient(ellipse at 30% 25%, #f4d28a 0%, #d99a52 35%, #8a4f24 70%, #3a1c0c 100%)',
  pasta:    'radial-gradient(ellipse at 55% 50%, #f6cf85 0%, #d97f47 30%, #a64224 60%, #4a1408 100%)',
  coffee:   'radial-gradient(ellipse at 50% 45%, #c08a5a 0%, #6e3e22 50%, #2a1308 100%)',
  salad:    'radial-gradient(ellipse at 50% 40%, #c3d97a 0%, #6ea14a 45%, #2d4e1c 100%)',
  sushi:    'radial-gradient(ellipse at 45% 50%, #f4e3b8 0%, #c98259 35%, #6b2a18 70%, #1a0805 100%)',
  croissant:'radial-gradient(ellipse at 40% 35%, #f6d68a 0%, #d8995a 40%, #7a4022 75%, #2c1208 100%)',
  cocktail: 'radial-gradient(ellipse at 50% 45%, #ffc36a 0%, #d96245 40%, #7d2230 75%, #1a0510 100%)',
  street:   'linear-gradient(180deg, #1a1626 0%, #3a2030 40%, #c0594a 75%, #f4a566 100%)',
  portrait: 'radial-gradient(ellipse at 50% 40%, #f7d4b2 0%, #d99a72 40%, #8a4f3a 75%, #2a140a 100%)',
};

// Used as the "main" camera viewfinder image — a hero food shot.
const HERO_PHOTO = PHOTO_SOURCES.pancakes;

// FilterTile — tiny preview thumbnail. Renders a photo with the filter's
// tint overlay so each filter looks distinctly different.
function FilterTile({ filter, size = 56, radius = 12, photo = 'pancakes', children }) {
  return (
    <div style={{
      width: size, height: size, borderRadius: radius, overflow: 'hidden',
      position: 'relative', flexShrink: 0,
      background: PHOTO_SOURCES[photo] || PHOTO_SOURCES.pancakes,
      filter: filter?.shift,
    }}>
      <div style={{
        position: 'absolute', inset: 0,
        background: filter?.tint,
        mixBlendMode: 'overlay',
      }} />
      {children}
    </div>
  );
}

// HeroPhoto — full camera viewfinder photo with filter applied at intensity.
function HeroPhoto({ filter, intensity = 100, photo = HERO_PHOTO, style }) {
  const k = intensity / 100;
  return (
    <div style={{
      position: 'absolute', inset: 0,
      background: photo,
      filter: filter ? `saturate(${1 + (parseFloat((filter.shift.match(/saturate\(([^)]+)\)/) || [0,1])[1]) - 1) * k})` : 'none',
      ...style,
    }}>
      {filter && (
        <div style={{
          position: 'absolute', inset: 0,
          background: filter.tint,
          opacity: k,
          mixBlendMode: 'overlay',
        }} />
      )}
    </div>
  );
}

Object.assign(window, {
  V, VFonts, Phone, StatusBar, GestureBar,
  FILTERS, CATEGORIES, PHOTO_SOURCES, HERO_PHOTO,
  FilterTile, HeroPhoto,
});
