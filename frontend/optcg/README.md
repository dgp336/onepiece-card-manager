# 🃏 One Piece Card Browser

A fast, client-side card browser for the **One Piece Card Game**, built with Vanilla JS and Vite.  
Search and filter your entire card database locally — no server required.

---

## 🚀 Getting Started

### Prerequisites

- [Node.js](https://nodejs.org/) (v18 or later recommended)
- npm

### Install & Run

```bash
# Install dependencies
npm install

# Start the development server
npm run dev
```

Then open the URL shown in your terminal (usually `http://localhost:5173`) in your browser.

### Build for Production

```bash
npm run build
```

The output will be in the `dist/` folder.

---

## 🖥️ Interface Overview

The app has two main areas:

| Area | Description |
|------|-------------|
| **Top Bar** | Global text search input + "Search Filters" toggle button |
| **Filter Sidebar** | Sticky left panel with all advanced filters |
| **Card Grid** | Results area — scrollable, fills the remaining width |

---

## 🔍 How to Search

### Global Search Bar

The search bar at the top searches **simultaneously** across the following card fields as you type:

- Card Number
- Card Name
- Card Type
- Card Set
- Card Color
- Card Feature / Traits
- Card Cost

> Results update automatically with a short debounce — no need to press Enter.

**Example searches:**
- `Sanji` → all cards with "Sanji" in any field
- `OP01` → all cards belonging to sets starting with OP01
- `CHARACTER` → all Character-type cards

---

## 🗂️ Advanced Filters (Sidebar)

Click the **☰ Search Filters** button in the top bar to show or hide the filter sidebar.

All filters work **simultaneously** — you can combine any number of them to narrow down results.

---

### 📦 Card Set
**Type:** Text input  
**Placeholder:** `example: OP012`

Filters cards whose set code contains the text you type (case-insensitive, partial match).

---

### 🏷️ Card Name
**Type:** Text input  
**Placeholder:** `example: Sanji`

Filters by card name. Partial matches are supported.

---

### ⚔️ Card Type
**Type:** Text input  
**Placeholder:** `example: CHARACTER`

Filters by card type (e.g. `CHARACTER`, `EVENT`, `STAGE`, `LEADER`). Partial match.

---

### 🎨 Card Color
**Type:** Interactive SVG hexagon

A hexagon divided into 6 colored slices — click one or multiple slices to filter by that color:

```
         🟡 Yellow
  ⚫ Black     🔴 Red
  🟣 Purple    🟢 Green
         🔵 Blue
```

> Click the **same slice again** to deselect it. The selected color name is shown below the hexagon.

---

### 💰 Card Cost
**Type:** Button grid (`0` – `10`)

Click a number button to filter by exact cost. The selected button is highlighted in purple.

> Click the **same button again** to deselect.

---

### 🌀 Card Feature / Traits
**Type:** Text input  
**Placeholder:** `example: Straw Hat Crew`

Filters cards by their trait/feature line (partial, case-insensitive match).

---

### 🛡️ Counter
**Type:** Button group

Filter cards by their counter value:

| Button | Matches |
|--------|---------|
| `0`    | Cards with no counter |
| `+1000` | Cards with a 1000 counter |
| `+2000` | Cards with a 2000 counter |

> Click the **same button again** to deselect.

---

### ✕ Clear Filters

The **✕ Clear Filters** button at the bottom of the sidebar resets **all** filters at once (including the global search bar).

---

## 🃏 Card Detail View (Modal)

Click on any card in the grid to open a full-screen detail popup showing:

| Field | Description |
|-------|-------------|
| **Card Image** | Full card artwork |
| **Number** | Unique card identifier |
| **Set** | The set this card belongs to |
| **Type** | Card type (CHARACTER, EVENT, etc.) |
| **Color** | Card color |
| **Cost** | Play cost |
| **Power** | Battle power value |
| **Counter** | Counter bonus value |
| **Traits** | Crew / faction affiliations |
| **Effect** | Full card effect text |

### Closing the Modal

- Click the **×** button (top right)
- Click anywhere on the **dark backdrop** outside the card detail

---

## 📁 Project Structure

```
data_fetch_project/
├── src/
│   ├── main.js          # App logic: rendering, filtering, modal
│   ├── style.css        # All styles (layout, sidebar, cards, modal)
│   └── card_data.json   # Full card database (loaded locally)
├── public/              # Static assets
├── index.html           # App entry point
└── package.json
```

---

## 🛠️ Tech Stack

| Technology | Role |
|------------|------|
| **Vanilla JS (ES Modules)** | App logic, DOM manipulation |
| **Vite** | Dev server & bundler |
| **CSS (custom)** | Glassmorphism dark UI, sticky sidebar, modal |
| **JSON** | Local card database (no API calls) |

---

*Built by David Granados Pérez*
