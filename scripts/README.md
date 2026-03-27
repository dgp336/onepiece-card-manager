# Docker Compose - One Piece Card Manager

Este archivo `docker-compose.yml` orquesta los servicios necesarios para ejecutar la aplicación completa:

- **PostgreSQL Database**: Base de datos del backend
- **Backend**: API Spring Boot en el puerto 8080
- **Frontend**: Aplicación Angular/Node.js en el puerto 3000

## Requisitos

- Docker
- Docker Compose

## Uso

### Iniciar todos los servicios

```bash
docker-compose up -d
```

Este comando:
1. Construye y inicia la base de datos PostgreSQL
2. Compila y ejecuta el backend (Spring Boot)
3. Compila y ejecuta el frontend

### Ver los logs

```bash
# Todos los servicios
docker-compose logs -f

# Un servicio específico
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgresql_database
```

### Detener los servicios

```bash
docker-compose down
```

Para eliminar también los volúmenes:

```bash
docker-compose down -v
```

## Puertos

- **Backend**: http://localhost:8080
- **Frontend**: http://localhost:3000
- **PostgreSQL**: localhost:5432

## Configuración de la Base de Datos

- Usuario: `user`
- Contraseña: `pass`
- Base de datos: `db`

## Notas

- Los servicios están conectados a través de la red `onepiece-network`
- El frontend se inicia después del backend (dependencia definida)
- El backend se inicia después de la base de datos (dependencia definida)
- Los volúmenes de PostgreSQL se pierde al usar `docker-compose down -v`

---

# Cargue de Datos de Cartas

El sistema incluye funcionalidad automática y manual para cargar datos de cartas desde https://cdn.cardkaizoku.com/card_data.json

## Cargue Automático

Al iniciar el backend, si la base de datos está vacía, se cargarán automáticamente todos los datos de cartas.

## Cargue Manual

Hay dos formas de cargar datos manualmente:

### Opción 1: Usar el script bash

```bash
# Cargar datos (solo si la BD está vacía)
./load-card-data.sh load

# Recargar datos (elimina y vuelve a cargar)
./load-card-data.sh reload

# Limpiar todos los datos
./load-card-data.sh clear

# Mostrar ayuda
./load-card-data.sh help
```

### Opción 2: Usar curl directamente

```bash
# Cargar datos
curl -X POST http://localhost:8080/cards/load-data

# Recargar datos
curl -X POST http://localhost:8080/cards/reload-data

# Limpiar datos
curl -X DELETE http://localhost:8080/cards/clear-all
```

## Respuesta de los Endpoints

La respuesta es un JSON con el siguiente formato:

```json
{
  "success": true,
  "message": "Cartas cargadas exitosamente",
  "cardsLoaded": 5000
}
```

## Notas del Cargue

- El endpoint `/cards/load-data` solo carga si la BD está vacía
- El endpoint `/cards/reload-data` limpia primero y luego carga
- El cargue es idempotente: puedes ejecutarlo varias veces sin problemas
- Los tiempos de cargue dependen de la velocidad de internet y la velocidad de la base de datos
- Se recomienda ejecutar el cargue una sola vez para optimizar el rendimiento

