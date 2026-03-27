import "./style.css";

const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

document.querySelector("#app").innerHTML = `
  <div class="app-container">
    <header class="top-bar">
      <div class="top-bar-content">
        <button id="toggleFiltersBtn" class="filter-btn">☰ Search Filters</button>
        <input type="text" id="searchInput" placeholder="Search by number, name, type, set, color, or cost..." autocomplete="off">
      </div>
    </header>

    <div class="main-layout">
      <!-- LEFT STICKY SIDEBAR -->
      <aside id="filterSidebar" class="filter-sidebar">
        <div class="filters-grid">

          <div class="filter-group">
            <label>Card Set</label>
            <input type="text" id="filterSet" placeholder="example: OP12">
          </div>

          <div class="filter-group">
            <label>Card Name</label>
            <input type="text" id="filterName" placeholder="example: Sanji">
          </div>

          <div class="filter-group">
            <label>Card Type</label>
            <div class="type-buttons">
              <button class="type-btn" data-type="LEADER">LEADER</button>
              <button class="type-btn" data-type="CHARACTER">CHARACTER</button>
              <button class="type-btn" data-type="EVENT">EVENT</button>
              <button class="type-btn" data-type="STAGE">STAGE</button>
            </div>
          </div>

          <div class="filter-group">
            <label>Card Color</label>
            <div class="hexagon-container">
              <svg width="130" height="130" viewBox="0 0 100 100" class="color-hexagon">
                <!-- clockwise from top-right: Red, Green, Blue, Purple, Black, Yellow -->
                <polygon points="50,50 50,0 93.3,25"  fill="#ef4444" data-color="Red"    class="color-slice"/>
                <polygon points="50,50 93.3,25 93.3,75" fill="#22c55e" data-color="Green"  class="color-slice"/>
                <polygon points="50,50 93.3,75 50,100" fill="#3b82f6" data-color="Blue"   class="color-slice"/>
                <polygon points="50,50 50,100 6.7,75"  fill="#a855f7" data-color="Purple" class="color-slice"/>
                <polygon points="50,50 6.7,75 6.7,25"  fill="#1a1a1a" data-color="Black"  class="color-slice"/>
                <polygon points="50,50 6.7,25 50,0"    fill="#eab308" data-color="Yellow" class="color-slice"/>
              </svg>
            </div>
            <div id="selectedColorLabel" class="selected-label"></div>
          </div>

          <div class="filter-group">
            <label>Card Cost</label>
            <div class="cost-buttons">
              ${[0, 1, 2, 3, 4, 5].map((c) => `<button class="cost-btn" data-cost="${c}">${c}</button>`).join("")}
            </div>
            <div class="cost-buttons">
              ${[6, 7, 8, 9, 10].map((c) => `<button class="cost-btn" data-cost="${c}">${c}</button>`).join("")}
            </div>
          </div>

          <div class="filter-group">
            <label>Card Feature / Traits</label>
            <input type="text" id="filterFeature" placeholder="example: Straw Hat Crew">
          </div>

          <div class="filter-group">
            <label>Counter</label>
            <div class="counter-buttons">
              <button class="counter-btn" data-counter="0">0</button>
              <button class="counter-btn" data-counter="1000">+1000</button>
              <button class="counter-btn" data-counter="2000">+2000</button>
            </div>
          </div>

          <button id="clearFiltersBtn" class="clear-btn">✕ Clear Filters</button>
        </div>
      </aside>

      <!-- MAIN CONTENT / CARD GRID -->
      <main class="content">
        <p id="message" class="message">Loading cards...</p>
        <div id="grid" class="card-grid hidden"></div>
      </main>
    </div>
  </div>

  <!-- CARD ZOOM MODAL -->
  <div id="cardModal" class="modal hidden">
    <div class="modal-backdrop"></div>
    <div class="modal-content">
      <button class="close-modal" id="closeModal">&times;</button>
      <div id="modalDetails"></div>
    </div>
  </div>
`;

const toggleFiltersBtn = document.getElementById("toggleFiltersBtn");
const filterSidebar = document.getElementById("filterSidebar");
const clearFiltersBtn = document.getElementById("clearFiltersBtn");
const message = document.getElementById("message");
const grid = document.getElementById("grid");
const modal = document.getElementById("cardModal");
const closeModalBtn = document.getElementById("closeModal");
const modalDetails = document.getElementById("modalDetails");
const selectedColorLabel = document.getElementById("selectedColorLabel");

const inputs = {
  general: document.getElementById("searchInput"),
  set: document.getElementById("filterSet"),
  name: document.getElementById("filterName"),
  feature: document.getElementById("filterFeature"),
};

let selectedColors = new Set();
let selectedType = "";
let selectedCost = "";
let selectedCounter = "";
let debounceTimeout;
let cardData = [];

async function loadCardsFromBackend() {
  message.classList.remove("hidden");
  message.textContent = "Loading cards from backend...";
  grid.classList.add("hidden");

  try {
    let page = 0;
    let totalPages = 1;
    const allCards = [];

    while (page < totalPages) {
      const response = await fetch(`${API_BASE_URL}/cards?page=${page}&size=1000`);
      if (!response.ok) {
        throw new Error(`Backend error: ${response.status}`);
      }

      const payload = await response.json();
      const pageCards = payload?._embedded?.cards || [];
      allCards.push(...pageCards);
      totalPages = payload?.page?.totalPages || 1;
      page += 1;
    }

    cardData = allCards.map((card) => ({
      cardNumber: card.number,
      cardName: card.name,
      cardType: card.type,
      cardSet: card.set,
      color: card.color,
      feature: card.feature,
      cost: card.cost,
      counter: card.counter,
      power: card.power,
      text: card.text,
      bucketImg: card.img,
    }));

    applyFilters();
  } catch (error) {
    console.error("Error loading cards from backend:", error);
    message.textContent = "Error loading cards from backend";
  }
}

toggleFiltersBtn.addEventListener("click", () => {
  filterSidebar.classList.toggle("sidebar-hidden");
});

const colorSlices = document.querySelectorAll(".color-slice");
colorSlices.forEach((slice) => {
  slice.addEventListener("click", () => {
    const color = slice.getAttribute("data-color");
    if (selectedColors.has(color)) {
      selectedColors.delete(color);
      slice.classList.remove("selected");
    } else {
      selectedColors.add(color);
      slice.classList.add("selected");
    }
    // Dim slices that are NOT selected (only when at least one is selected)
    colorSlices.forEach((s) => {
      const c = s.getAttribute("data-color");
      s.classList.toggle("dimmed", selectedColors.size > 0 && !selectedColors.has(c));
    });
    selectedColorLabel.textContent = selectedColors.size > 0
      ? `Selected: ${[...selectedColors].join(" · ")}`
      : "";
    triggerUpdate();
  });
});

const costBtns = document.querySelectorAll(".cost-btn");
costBtns.forEach((btn) => {
  btn.addEventListener("click", () => {
    const cost = btn.getAttribute("data-cost");
    if (selectedCost === cost) {
      selectedCost = "";
      btn.classList.remove("active");
    } else {
      selectedCost = cost;
      costBtns.forEach((b) => b.classList.remove("active"));
      btn.classList.add("active");
    }
    triggerUpdate();
  });
});

const counterBtns = document.querySelectorAll(".counter-btn");
counterBtns.forEach((btn) => {
  btn.addEventListener("click", () => {
    const counter = btn.getAttribute("data-counter");
    if (selectedCounter === counter) {
      selectedCounter = "";
      btn.classList.remove("active");
    } else {
      selectedCounter = counter;
      counterBtns.forEach((b) => b.classList.remove("active"));
      btn.classList.add("active");
    }
    triggerUpdate();
  });
});

const typeBtns = document.querySelectorAll(".type-btn");
typeBtns.forEach((btn) => {
  btn.addEventListener("click", () => {
    const type = btn.getAttribute("data-type");
    if (selectedType === type) {
      selectedType = "";
      btn.classList.remove("active");
    } else {
      selectedType = type;
      typeBtns.forEach((b) => b.classList.remove("active"));
      btn.classList.add("active");
    }
    triggerUpdate();
  });
});

Object.values(inputs).forEach((input) => {
  input.addEventListener("input", triggerUpdate);
});

clearFiltersBtn.addEventListener("click", () => {
  Object.values(inputs).forEach((i) => (i.value = ""));
  selectedColors.clear();
  selectedType = "";
  selectedCost = "";
  selectedCounter = "";
  colorSlices.forEach((s) => s.classList.remove("dimmed", "selected"));
  typeBtns.forEach((b) => b.classList.remove("active"));
  costBtns.forEach((b) => b.classList.remove("active"));
  counterBtns.forEach((b) => b.classList.remove("active"));
  selectedColorLabel.textContent = "";
  applyFilters();
});

function triggerUpdate() {
  clearTimeout(debounceTimeout);
  debounceTimeout = setTimeout(applyFilters, 300);
}

function applyFilters() {
  const general = inputs.general.value.trim().toLowerCase();
  const set = inputs.set.value.trim().toLowerCase();
  const name = inputs.name.value.trim().toLowerCase();
  const feature = inputs.feature.value.trim().toLowerCase();

  const isAnyFilterActive =
    general ||
    set ||
    name ||
    selectedType ||
    feature ||
    selectedColors.size > 0 ||
    selectedCost ||
    selectedCounter;

  let filtered = cardData;

  if (isAnyFilterActive) {
    filtered = cardData.filter((c) => {
    if (set && !c.cardSet?.toLowerCase().includes(set)) return false;
    if (name && !c.cardName?.toLowerCase().includes(name)) return false;
    if (selectedType && String(c.cardType ?? "").toUpperCase() !== selectedType) return false;
    if (feature && !c.feature?.toLowerCase().includes(feature)) return false;
    if (
      selectedColors.size > 0 &&
      ![...selectedColors].some((col) => c.color?.toLowerCase().includes(col.toLowerCase()))
    )
      return false;

    if (selectedCost) {
      const cardCostVal =
        c.cost === "-" || c.cost == null ? "0" : String(c.cost);
      if (cardCostVal !== selectedCost) return false;
    }

    if (selectedCounter && String(c.counter) !== selectedCounter) return false;

    if (general) {
      const fields = [
        c.cardNumber,
        c.cardName,
        c.cardType,
        c.cardSet,
        c.color,
        c.feature,
        c.cost,
      ];
      if (
        !fields.some((f) =>
          String(f ?? "")
            .toLowerCase()
            .includes(general),
        )
      )
        return false;
    }

    return true;
    });
  }

  if (filtered.length > 0) {
    filtered.sort((a, b) => (parseInt(a.cost) || 0) - (parseInt(b.cost) || 0));
    message.classList.add("hidden");
    grid.classList.remove("hidden");
    renderCards(filtered);
  } else {
    grid.innerHTML = "";
    grid.classList.add("hidden");
    message.classList.remove("hidden");
    message.textContent = "No cards found";
  }
}

function renderCards(cards) {
  grid.innerHTML = cards
    .map((card, idx) => {
      const imgHtml = card.bucketImg
        ? `<img src="${card.bucketImg}" alt="${card.cardName}" loading="lazy">`
        : `<div class="placeholder">${card.cardName || "No Image"}</div>`;

      return `
      <div class="card-item" data-idx="${idx}">
        ${imgHtml}
        <div class="card-info">
          <div class="card-number">${card.cardNumber || "Unknown #"}</div>
          <div class="card-name">${card.cardName || "Unknown Name"}</div>
          <div class="card-attr">Type: ${card.cardType || "N/A"}</div>
          <div class="card-attr">Color: ${card.color || "N/A"}</div>
        </div>
      </div>`;
    })
    .join("");

  grid.querySelectorAll(".card-item").forEach((item) => {
    item.addEventListener("click", () => {
      showModal(cards[parseInt(item.getAttribute("data-idx"))]);
    });
  });
}

function showModal(card) {
  const imgHtml = card.bucketImg
    ? `<img src="${card.bucketImg}" alt="${card.cardName}" class="modal-img">`
    : `<div class="placeholder modal-img">${card.cardName || "No Image"}</div>`;

  modalDetails.innerHTML = `
    <div class="modal-layout">
      <div class="modal-img-container">${imgHtml}</div>
      <div class="modal-text-container">
        <h2>${card.cardName || "Unknown Name"}</h2>
        <p><strong>Number:</strong> ${card.cardNumber || "N/A"}</p>
        <p><strong>Set:</strong> ${card.cardSet || "N/A"}</p>
        <p><strong>Type:</strong> ${card.cardType || "N/A"}</p>
        <p><strong>Color:</strong> ${card.color || "N/A"}</p>
        <p><strong>Cost:</strong> ${card.cost === "-" ? "0" : (card.cost ?? "N/A")}</p>
        <p><strong>Power:</strong> ${card.power || "N/A"}</p>
        <p><strong>Counter:</strong> ${card.counter ? card.counter > 0 ? "+" + card.counter : card.counter : "N/A"}</p>
        <p><strong>Traits:</strong> ${card.feature || "N/A"}</p>
        <p><strong>Effect:</strong> <span class="effect-content">${card.text?.trim() || "No effect"}</span></p>
      </div>
    </div>`;

  modal.classList.remove("hidden");
}

closeModalBtn.addEventListener("click", () => modal.classList.add("hidden"));

modal.querySelector(".modal-backdrop").addEventListener("click", () => {
  modal.classList.add("hidden");
});

loadCardsFromBackend();
