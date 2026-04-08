# Stocksum — Design System & UI/UX Guide

Makesure dont use material3 components, write the ui ux by yourself, dont get anything anywhere from components, make it from scratch. dont use any pre-built components. and no code comments. 

> Everything your AI agent needs to build consistent, premium, and intuitive screens for Stocksum.

---

## 1. Design Philosophy

Stocksum is a **financial tool**, not a social app. Every design decision should serve clarity, speed, and trust. Users open the app to make decisions — give them signal, not noise.

**Four core principles:**

- **Clarity first** — if a user has to think about what a number means, you've already lost them. Labels, colors, and layout should make data self-explanatory.
- **Speed over delight** — animations and transitions should feel snappy (150–250ms), never decorative. No loading spinners where skeleton screens can go.
- **Trust through consistency** — colors mean the same thing everywhere. Green is always gain. Red is always loss. Never flip this.
- **Dark by default** — financial apps are used in all lighting conditions, often on the go. Dark theme reduces eye strain and makes colored data pop.

---

## 2. Color System

### Base Palette

| Token | Hex | Usage |
|---|---|---|
| `color-bg-base` | `#0A0F1E` | App background, screen base |
| `color-bg-card` | `#141929` | Cards, bottom sheets, modals |
| `color-bg-elevated` | `#1C2540` | Nav bars, tab bars, elevated surfaces |
| `color-bg-input` | `#1A2035` | Input fields, search bars |
| `color-border` | `#252E4A` | Card borders, dividers, separators |
| `color-border-subtle` | `#1C2540` | Row dividers inside cards |

### Semantic Colors (never break these rules)

| Token | Hex | Usage |
|---|---|---|
| `color-gain` | `#00E676` | Positive P&L, price up, alert triggered above |
| `color-gain-bg` | `#003D20` | Gain badge backgrounds, gain chip fills |
| `color-loss` | `#FF5252` | Negative P&L, price down, alert triggered below |
| `color-loss-bg` | `#3D0A0A` | Loss badge backgrounds, loss chip fills |
| `color-neutral` | `#FFB300` | Neutral alerts, "Neutral" market mood, pending states |
| `color-neutral-bg` | `#3D2800` | Neutral badge backgrounds |
| `color-accent` | `#4D8EFF` | Primary CTA buttons, links, active tab indicator |
| `color-accent-bg` | `#0D1F3D` | Accent button backgrounds, info banners |

### Text Colors

| Token | Hex | Usage |
|---|---|---|
| `color-text-primary` | `#F0F4FF` | Primary text, prices, names |
| `color-text-secondary` | `#7A86A8` | Labels, subtitles, timestamps, muted metadata |
| `color-text-tertiary` | `#3D4A70` | Placeholder text, disabled states, hints |
| `color-text-gain` | `#00E676` | Positive values |
| `color-text-loss` | `#FF5252` | Negative values |

### Light Mode (optional, Phase 2)

If you add light mode later, flip the palette: base becomes `#F5F7FF`, cards become `#FFFFFF`, text becomes `#0A0F1E`. Gain and loss colors stay identical — they work on both.

---

## 3. Typography

**Font:** Use the system default — `SF Pro` on iOS, `Roboto` on Android. Never import a custom font for a data-heavy app; system fonts render faster and feel native.

### Type Scale

| Role | Size | Weight | Usage |
|---|---|---|---|
| `display` | 28sp | 500 | Portfolio total value, hero numbers |
| `headline` | 20sp | 500 | Screen titles, section headers |
| `title` | 16sp | 500 | Card titles, stock names, ticker symbols |
| `body` | 14sp | 400 | Body text, descriptions, alert conditions |
| `label` | 12sp | 500 | Badges, chips, tab labels, column headers |
| `caption` | 11sp | 400 | Timestamps, secondary metadata, footnotes |

### Rules

- **Never go below 11sp** — financial data must be readable at arm's length.
- Ticker symbols (AAPL, RELIANCE) are always `title` size and uppercase.
- P&L numbers that are positive get `color-text-gain`, negative get `color-text-loss`, zero gets `color-text-secondary`.
- Use tabular/monospace number rendering for prices in lists — this prevents columns from shifting as digits change.

---

## 4. Spacing & Layout

### Base Unit

Everything is built on an **8dp grid**. Internal component spacing uses 4dp steps. Never use arbitrary pixel values.

| Token | Value | Usage |
|---|---|---|
| `space-xs` | 4dp | Icon-to-label gap, badge padding |
| `space-sm` | 8dp | Row internal spacing, chip padding |
| `space-md` | 12dp | Card internal padding (compact) |
| `space-lg` | 16dp | Standard card padding, section gap |
| `space-xl` | 24dp | Screen horizontal margin, section header spacing |
| `space-2xl` | 32dp | Top of screen to first card |

### Screen Layout

- Horizontal screen margin: `20dp` on both sides — gives breathing room without wasting space.
- Safe area: always respect top notch/status bar and bottom home indicator.
- Scrollable screens: use `LazyColumn` (Android) or `ScrollView` with pull-to-refresh.

### Corner Radius

| Token | Value | Usage |
|---|---|---|
| `radius-sm` | 8dp | Chips, badges, input fields, tags |
| `radius-md` | 12dp | Small cards, bottom nav items, mini cards |
| `radius-lg` | 16dp | Standard cards, modals, bottom sheets |
| `radius-xl` | 24dp | Large hero cards, full-width banners |
| `radius-full` | 999dp | Pills, circular avatars, FAB |

---

## 5. Component Library

### 5.1 Stock Row (used in Portfolio, Watchlist, Search results)

The most repeated component in the app. Must be scannable in under 1 second.

```
[Logo] [Ticker / Company name]    [Current price]
       [Exchange · Shares owned]  [P&L value  +%]
```

**Specs:**
- Height: 64dp
- Logo circle: 36dp diameter, initials fallback (2 letters) with colored background
- Ticker: `title` size, `color-text-primary`
- Company name: `caption` size, `color-text-secondary`
- Price: `title` size, `color-text-primary`, right-aligned, tabular numbers
- P&L: `label` size, `color-text-gain` or `color-text-loss`, right-aligned
- Divider: 0.5dp `color-border-subtle` between rows (not a full card per row)
- On tap: navigate to Stock Detail screen with a shared element transition on the logo

**Logo color assignment:** Derive background color from ticker hash so it's consistent across sessions. Never use random colors.

### 5.2 Price Badge / Chip

Used for % change in market movers, search results, and portfolio rows.

```
[ +3.24% ]   ← green badge
[ -1.10% ]   ← red badge
```

**Specs:**
- Padding: `4dp` vertical, `8dp` horizontal
- Corner radius: `radius-sm` (8dp)
- Font: `label` (12sp, 500 weight)
- Positive: text `color-gain`, background `color-gain-bg`
- Negative: text `color-loss`, background `color-loss-bg`
- Neutral (0.00%): text `color-text-secondary`, background `color-bg-elevated`
- Always include sign (+/-) explicitly — never omit for positive values

### 5.3 Portfolio Hero Card

The top card on the Home screen. Most important element in the app.

```
┌─────────────────────────────────────┐
│  Portfolio value                    │
│  $24,812.50              (display)  │
│  +$348.20 today  [+1.42%]           │
│  ─────────────────────────────────  │
│  [Sparkline chart — 7 days]         │
└─────────────────────────────────────┘
```

**Specs:**
- Background: slightly lighter than base — use `#0F1E3D` to create subtle depth
- Corner radius: `radius-xl` (24dp)
- Padding: `20dp`
- "Portfolio value" label: `caption`, `color-text-secondary`
- Total value: `display` (28sp, 500), `color-text-primary`
- Today's change: `body`, colored by direction
- Badge next to change: `Price Badge` component
- Sparkline: 48dp tall, line weight 1.5dp, no Y-axis labels, gradient fill below line
- Sparkline color: `color-gain` if today is positive, `color-loss` if negative

### 5.4 Market Mover Chip (horizontal scroll row)

3–5 chips in a horizontal scroll, no snap. Used on Home screen under Portfolio card.

```
┌────────────┐  ┌────────────┐  ┌────────────┐
│  NVDA      │  │  AAPL      │  │  RELIANCE  │
│  $892.40   │  │  $178.90   │  │  ₹2,940    │
│  [+3.2%]   │  │  [-1.1%]   │  │  [+2.8%]   │
└────────────┘  └────────────┘  └────────────┘
```

**Specs:**
- Width: 100dp, fixed (do not stretch to fill)
- Height: 76dp
- Background: `color-bg-card`
- Corner radius: `radius-md`
- Ticker: `label`, `color-text-primary`
- Price: `body`, `color-text-primary`
- Badge: `Price Badge` component
- Spacing between chips: `8dp`
- On tap: navigate to Stock Detail

### 5.5 Section Header

Reusable header for sections like "Market Movers", "My Holdings", "Active Alerts".

```
MY HOLDINGS                          See all →
```

**Specs:**
- Label: `caption` (11sp), uppercase, `color-text-secondary`, letter-spacing 0.08em
- "See all" link: `caption`, `color-accent`, right-aligned
- Margin: `16dp` top, `8dp` bottom
- Never use a heavy font weight here — this is a label, not a headline

### 5.6 Market Mood Bar

Fear/Greed index displayed as a gradient progress bar. Unique to Stocksum.

```
Fear ─────────────●──────── Greed
                 52 · Neutral
```

**Specs:**
- Bar height: 6dp, full card width
- Gradient: left `color-loss` → center `color-neutral` → right `color-gain`
- Indicator dot: 12dp circle, `color-text-primary`, with a 2dp `color-bg-base` ring
- Label below center: `label`, colored to match current zone
- Zones: 0–30 Fear, 31–49 Slight Fear, 50 Neutral, 51–70 Slight Greed, 71–100 Greed
- Source the value from a Fear & Greed API (CNN, Alternative.me, or calculate yourself)

### 5.7 Alert Row

Used in the Alerts screen list.

```
[●] AAPL · Above $200.00            [Active]
    Current: $178.90 · Set 3d ago
```

**Specs:**
- Left dot: 8dp circle, `color-accent` for active, `color-text-secondary` for triggered/paused
- Condition line: `title`, `color-text-primary`
- Metadata line: `caption`, `color-text-secondary`
- Status badge: right-aligned — Active (accent), Triggered (gain green), Paused (neutral)
- Swipe left to delete (native gesture, red background with trash icon)
- Swipe right to toggle pause/resume

### 5.8 Input Fields

All inputs share the same base style.

**Specs:**
- Height: 48dp
- Background: `color-bg-input`
- Border: 1dp `color-border`, on focus changes to `color-accent`
- Corner radius: `radius-sm`
- Placeholder: `color-text-tertiary`
- Text: `color-text-primary`, `body` size
- Currency/unit label inside field: pinned to right, `color-text-secondary`
- Error state: border becomes `color-loss`, error message below in `caption` + `color-loss`

### 5.9 Primary Button

Used for CTAs: "Add Stock", "Set Alert", "Save Changes".

**Specs:**
- Height: 52dp
- Background: `color-accent`
- Text: `color-text-primary` (white), `title` (16sp, 500)
- Corner radius: `radius-lg`
- Width: full width inside its container (avoid narrow buttons)
- Pressed state: 90% opacity + scale 0.98
- Loading state: replace text with a small circular spinner, same color
- Disabled state: `color-bg-elevated` background, `color-text-tertiary` text

### 5.10 FAB (Floating Action Button)

The `+` button for adding stocks or alerts.

**Specs:**
- Size: 56dp × 56dp
- Background: `color-gain` (#00E676)
- Icon: `+` in `color-bg-base` (dark), 24sp
- Corner radius: `radius-full`
- Position: bottom-right, 16dp from edge, 16dp above bottom nav
- Shadow: not a real shadow — use a subtle 1dp ring of `color-gain` at 30% opacity
- On tap: animate with a spring, slight scale bounce

---

## 6. Navigation

### Bottom Navigation Bar

5 tabs. Always visible (never hide on scroll for a financial app — users need instant access).

| Tab | Icon | Screen |
|---|---|---|
| Home | House | Market summary + portfolio snapshot |
| Portfolio | Grid | Full holdings list |
| Discover | Search / Compass | Search + top movers |
| Alerts | Bell | Alert management |
| Profile | Person circle | Settings + preferences |

**Specs:**
- Height: 56dp + safe area bottom
- Background: `color-bg-elevated`
- Top border: 0.5dp `color-border`
- Active tab: icon and label use `color-gain`
- Inactive tab: `color-text-secondary`
- Active indicator: 3dp pill under icon, `color-gain`
- Label: `caption` (11sp)
- Badge on Alerts tab: red dot with count when alerts trigger

### Screen Transitions

- Home → Stock Detail: shared element transition (logo circle expands)
- Tab switches: fade (150ms), not slide — slides feel sluggish for tabs
- Bottom sheet open: slide up (250ms), spring easing
- Back: slide right (200ms)

---

## 7. Screen-by-Screen UX Breakdown

### 7.1 Home Screen

**Goal:** Answer "what's the market doing and how am I doing?" in under 10 seconds.

**Layout (top to bottom):**
1. Greeting + date (top left), Avatar/initials (top right)
2. Portfolio Hero Card (full width)
3. Section header: "Market Movers" + "See all"
4. Horizontal scroll of Market Mover Chips (3–5 chips)
5. Market Mood Bar Card (full width)
6. Section header: "My Holdings" + FAB (+)
7. Top 3–4 holdings as Stock Rows (inside a card)
8. If portfolio is empty: empty state illustration + "Add your first stock" CTA

**Behaviors:**
- Pull-to-refresh updates prices and mood index
- Prices auto-refresh every 5 minutes (show a subtle "Updated 2m ago" timestamp)
- Tapping a holding → Stock Detail
- Tapping "See all" on holdings → Portfolio screen

### 7.2 Portfolio Screen

**Goal:** See your full position, understand performance at a glance.

**Layout:**
1. Summary bar at top: Total value | Today's change | Total gain/loss (3 metric chips in a row)
2. Time filter tabs: 1D · 1W · 1M · 3M · 1Y (changes the portfolio chart below)
3. Portfolio performance chart (area chart, 160dp tall)
4. Holdings list header row: Stock | Shares | Avg Price | Current | P&L
5. Full holdings list (Stock Rows with extra columns)

**Behaviors:**
- Sort options: by value, by gain %, by name, by exchange — accessible via a sort icon in header
- Long press on a holding → context menu: Edit, Set Alert, Remove
- Swipe left on a row → quick delete with confirmation dialog
- Empty state: illustration + "Start building your portfolio" + Add button

### 7.3 Discover Screen

**Goal:** Browse market activity, search for any stock quickly.

**Layout:**
1. Search bar (pinned at top, always visible)
2. Market filter tabs: All · USA · India
3. Horizontal section pills: Gainers | Losers | Active | Sectors
4. Stock list filtered by selected section (Stock Rows with mini sparkline)

**Search behavior:**
- Instant results as user types (debounce 300ms)
- Results show: ticker, full name, exchange flag (🇺🇸 / 🇮🇳), current price, today's % change
- Recent searches shown when search bar is focused but empty
- No results state: "No results for 'XYZ'" + suggested alternatives

### 7.4 Alerts Screen

**Goal:** Manage price alerts, see which have triggered.

**Layout:**
1. Tab bar: Active | Triggered | Paused
2. Alert rows for the selected tab
3. FAB (+) to create new alert
4. Empty state per tab with helpful copy

**Create Alert flow (bottom sheet):**
1. Search / pick stock
2. Set condition: `Above` or `Below` (segmented control)
3. Set target price (input with current price shown as reference)
4. Set repeat: One-time or Repeating (toggle)
5. "Create Alert" primary button

**Notification format:** `"AAPL hit $200 — your target price"` with a deep link back into the alert detail.

### 7.5 Stock Detail Screen

**Goal:** Deep-dive into one stock. Used from any screen.

**Layout:**
1. Header: logo + ticker + company name + exchange badge
2. Current price (display size) + % change badge
3. Price chart with time filter (1D | 1W | 1M | 3M | 1Y)
4. Your position card (if owned): shares, avg cost, current value, P&L
5. "Add to Portfolio" or "Edit Position" button
6. "Set Alert" button (secondary)
7. Key stats: Market Cap, 52W High, 52W Low, Volume, P/E ratio
8. About section: 2–3 line company description

### 7.6 Profile / Settings Screen

**Layout:**
1. User avatar + name (editable)
2. Preferences section: Default currency (USD/INR), Default market (All/USA/India)
3. Notifications section: Alert notifications toggle, Market open/close reminders
4. Appearance section: Theme (Dark/Light/System)
5. Data section: Refresh interval (1m/5m/15m)
6. Premium card (future): "Upgrade to Pro — real-time data, unlimited alerts"
7. About: version number, privacy policy, feedback link

---

## 8. Data Visualization

### Price Charts

- Library recommendation: MPAndroidChart (Android) or Charts (iOS)
- Chart type: Area chart (line with gradient fill below)
- Line color: `color-gain` if today's close ≥ open, `color-loss` if below
- Fill gradient: line color at 20% opacity → transparent at bottom
- No grid lines — financial charts should breathe
- Y-axis: right side only, 3–4 labels max
- X-axis: time labels appropriate to selected range (e.g., "9AM 11AM 1PM" for 1D)
- Touch interaction: vertical crosshair line + price tooltip at touch point

### Portfolio Donut Chart (optional, Portfolio screen)

- Show allocation by stock or by sector
- Each slice uses a consistent color per stock (derived from ticker hash)
- Center label: total value
- Tap a slice → highlight that stock in the list below

### Sparklines (mini charts)

- Used in Stock Rows and Market Mover Chips
- Width: fills available space, Height: 28–36dp
- No axes, no labels — pure visual trend indicator
- 7-day or 5-day data
- Same color rules as full charts

---

## 9. Empty States

Never leave a blank screen. Every empty state needs:
1. A simple illustration (line art, not colorful)
2. A short headline (what's missing)
3. One CTA button

| Screen | Headline | CTA |
|---|---|---|
| Portfolio empty | "Your portfolio is empty" | Add your first stock |
| Alerts empty | "No alerts set" | Create an alert |
| Discover no results | "No results for '[query]'" | Clear search |
| Triggered alerts empty | "No alerts have triggered yet" | — |

---

## 10. Loading & Skeleton States

Never show a spinner for a full screen load. Use skeleton screens instead.

**Skeleton specs:**
- Background: `color-bg-elevated`
- Shimmer animation: horizontal gradient sweep, 1.2s loop
- Match the exact layout of the real content (same row heights, same card sizes)
- Skeleton disappears when data arrives — no fade, instant swap

**When to use a spinner:**
- Inline actions only: button loading state, pull-to-refresh indicator, search results appearing

---

## 11. Error States

**Network error (no connection):**
- Banner at top of screen: "No internet connection" in `color-neutral` with a retry button
- Do not clear cached data — show last-known prices with a "last updated X ago" label

**API error (data unavailable):**
- In-card error: "Couldn't load prices. Tap to retry."
- Never show a raw error code to the user

**Price data delayed:**
- Small badge near price: "Delayed 15m" in `color-neutral` — common for Indian markets

---

## 12. Micro-interactions & Animation

| Interaction | Animation | Duration |
|---|---|---|
| Tab switch | Fade | 150ms |
| Card tap | Scale to 0.97, release | 100ms |
| FAB tap | Spring scale bounce | 200ms |
| Bottom sheet open | Slide up + spring | 250ms |
| Price badge update | Pulse flash (green or red) | 400ms |
| Gain/Loss value update | Number roll animation | 300ms |
| Alert triggered | Pulsing dot on bell icon | Loop until dismissed |

Keep animations purposeful. If removing an animation doesn't break comprehension, remove it.

---

## 13. Accessibility

- Minimum touch target: 48dp × 48dp (even for small icons)
- Color is never the only indicator — always pair with a `+/-` sign or icon
- All prices must have content descriptions for screen readers (e.g., "Apple, up 1.42%")
- Support dynamic text size — test at 150% scale, nothing should clip
- Dark mode is default but respect system preference if Light mode is added

---

## 14. Localization Notes

- Currency: detect device locale for formatting. ₹ for Indian stocks, $ for US.
- Numbers: use locale-appropriate separators (1,00,000 vs 100,000)
- Dates: use relative time ("2 hours ago", "Yesterday") for timestamps; absolute only in detail views
- Exchange labels: show "NSE" / "BSE" / "NYSE" / "NASDAQ" as small caps badges

---

## 15. Design Tokens Summary (Quick Reference)

```
COLORS
  bg-base:       #0A0F1E
  bg-card:       #141929
  bg-elevated:   #1C2540
  bg-input:      #1A2035
  border:        #252E4A

  gain:          #00E676   gain-bg:    #003D20
  loss:          #FF5252   loss-bg:    #3D0A0A
  neutral:       #FFB300   neutral-bg: #3D2800
  accent:        #4D8EFF   accent-bg:  #0D1F3D

  text-primary:  #F0F4FF
  text-secondary:#7A86A8
  text-tertiary: #3D4A70

SPACING
  xs: 4dp  sm: 8dp  md: 12dp  lg: 16dp  xl: 24dp  2xl: 32dp

RADIUS
  sm: 8dp  md: 12dp  lg: 16dp  xl: 24dp  full: 999dp

TYPOGRAPHY
  display:  28sp / 500    headline: 20sp / 500
  title:    16sp / 500    body:     14sp / 400
  label:    12sp / 500    caption:  11sp / 400

ANIMATION
  fast:   150ms    standard: 250ms    slow: 400ms
  easing: spring for entrances, ease-out for exits
```

---

*Last updated: April 2026 · Stocksum v1.0 Design System*