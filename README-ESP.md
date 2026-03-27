# One Piece Card Explorer 🏴‍☠️

Aplicación para explorar, filtrar y consultar cartas de One Piece TCG con persistencia en base de datos.

## 🚀 Características principales
- Carga de cartas desde fuente JSON remota al backend.
- Persistencia de cartas en PostgreSQL.
- Búsqueda global y filtros combinables (set, nombre, tipo, color, coste, rasgo y counter).
- Vista de detalle de carta en modal.
- API REST para gestionar recarga y limpieza de datos.

## 🧱 Arquitectura (3 contenedores)
El proyecto se ejecuta con Docker Compose y está compuesto por **tres servicios**:

1. **Base de datos (`postgresql_database`)**
   - Imagen: `postgres:16`
   - Puerto: `5432`
   - Guarda las cartas y datos relacionados.

2. **Backend (`onepiece-backend`)**
   - Spring Boot + JPA
   - Puerto: `8080`
   - Expone la API (`/api`) y realiza la carga de cartas en la base de datos.

3. **Frontend (`onepiece-frontend`)**
   - Vite + JavaScript
   - Puerto: `3000`
   - Consume la API del backend y renderiza la interfaz de búsqueda/filtros.

## 🛠️ Stack tecnológico
- **Frontend:** Vite, JavaScript, CSS.
- **Backend:** Java 21, Spring Boot, Spring Data JPA.
- **Base de datos:** PostgreSQL 16.
- **Infraestructura:** Docker, Docker Compose.

## 📦 Estructura del proyecto
```text
onepiece-card-manager/
├── backend/optcg/           # API Spring Boot
├── frontend/optcg/          # Aplicación web (Vite)
├── scripts/                 # Docker Compose y scripts auxiliares
├── README.md                # Documentación unificada
├── README-ESP.md            # Referencia a README.md
└── README-ENG.md            # Reference to README.md
```

## ▶️ Ejecución con Docker
Desde la raíz del proyecto:

```bash
cd scripts
sudo docker compose up -d --build
```

Servicios disponibles:
- Frontend: http://localhost:3000
- Backend: http://localhost:8080/api
- Base de datos: `localhost:5432`

## 🔄 Gestión de cartas
Endpoints útiles:

- Recargar cartas: `POST http://localhost:8080/api/cards/reload-data`
- Limpiar cartas: `DELETE http://localhost:8080/api/cards/clear-all`
- Consultar cartas: `GET http://localhost:8080/api/cards`

## 📄 Licencia
Proyecto bajo licencia **MIT**. Ver [LICENSE.md](LICENSE.md).

---
Desarrollado por David Granados Pérez (2026).
