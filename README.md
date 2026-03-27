# One Piece Card Explorer 🏴‍☠️

Application to explore, filter, and browse One Piece TCG cards with database persistence.

## 🚀 Main Features
- Card loading from a remote JSON source into the backend.
- Card persistence in PostgreSQL.
- Global search and combinable filters (set, name, type, color, cost, trait, and counter).
- Card detail view in a modal.
- REST API to handle data reload and cleanup.

## 🧱 Architecture (3 Containers)
The project runs with Docker Compose and is composed of **three services**:

1. **Database (`postgresql_database`)**
   - Image: `postgres:16`
   - Port: `5432`
   - Stores cards and related data.

2. **Backend (`onepiece-backend`)**
   - Spring Boot + JPA
   - Port: `8080`
   - Exposes the API (`/api`) and loads cards into the database.

3. **Frontend (`onepiece-frontend`)**
   - Vite + JavaScript
   - Port: `3000`
   - Consumes the backend API and renders the search/filter interface.

## 🛠️ Technology Stack
- **Frontend:** Vite, JavaScript, CSS.
- **Backend:** Java 21, Spring Boot, Spring Data JPA.
- **Database:** PostgreSQL 16.
- **Infrastructure:** Docker, Docker Compose.

## 📦 Project Structure
```text
onepiece-card-manager/
├── backend/optcg/           # API Spring Boot
├── frontend/optcg/          # Web application (Vite)
├── scripts/                 # Docker Compose and helper scripts
├── README.md                # Unified documentation
├── README-ESP.md            # Reference to README.md
└── README-ENG.md            # Reference to README.md
```

## ▶️ Run with Docker
From the project root:

```bash
cd scripts
sudo docker compose up -d --build
```

Available services:
- Frontend: http://localhost:3000
- Backend: http://localhost:8080/api
- Database: `localhost:5432`

## 🔄 Card Management
Useful endpoints:

- Reload cards: `POST http://localhost:8080/api/cards/reload-data`
- Clear cards: `DELETE http://localhost:8080/api/cards/clear-all`
- Fetch cards: `GET http://localhost:8080/api/cards`

## 📄 License
Project licensed under **MIT**. See [LICENSE.md](LICENSE.md).

---
Developed by David Granados Pérez (2026).
