# Docker Compose - One Piece Card Manager

This `docker-compose.yml` file orchestrates the services required to run the full application:

- **PostgreSQL Database**: Backend database
- **Backend**: Spring Boot API on port 8080
- **Frontend**: Angular/Node.js application on port 3000

## Requirements

- Docker
- Docker Compose

## Usage

### Start all services

```bash
docker-compose up -d
```

This command:
1. Builds and starts the PostgreSQL database
2. Builds and runs the backend (Spring Boot)
3. Builds and runs the frontend

### View logs

```bash
# All services
docker-compose logs -f

# A specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgresql_database
```

### Stop services

```bash
docker-compose down
```

To also remove volumes:

```bash
docker-compose down -v
```

## Ports

- **Backend**: http://localhost:8080
- **Frontend**: http://localhost:3000
- **PostgreSQL**: localhost:5432

## Database Configuration

- User: `user`
- Password: `pass`
- Database: `db`

## Notes

- Services are connected through the `onepiece-network` network
- The frontend starts after the backend (defined dependency)
- The backend starts after the database (defined dependency)
- PostgreSQL volumes are removed when using `docker-compose down -v`

---

# Card Data Loading

The system includes automatic and manual functionality to load card data from https://cdn.cardkaizoku.com/card_data.json

## Automatic Loading

When the backend starts, if the database is empty, all card data is loaded automatically.

## Manual Loading

There are two ways to load data manually:

### Option 1: Use the bash script

```bash
# Load data (only if DB is empty)
./load-card-data.sh load

# Reload data (clears and loads again)
./load-card-data.sh reload

# Clear all data
./load-card-data.sh clear

# Show help
./load-card-data.sh help
```

### Option 2: Use curl directly

```bash
# Load data
curl -X POST http://localhost:8080/cards/load-data

# Reload data
curl -X POST http://localhost:8080/cards/reload-data

# Clear data
curl -X DELETE http://localhost:8080/cards/clear-all
```

## Endpoint Response

The response is a JSON object with the following format:

```json
{
  "success": true,
  "message": "Cards loaded successfully",
  "cardsLoaded": 5000
}
```

## Loading Notes

- The `/cards/load-data` endpoint only loads data if the DB is empty
- The `/cards/reload-data` endpoint clears first and then loads data
- Loading is idempotent: you can run it multiple times safely
- Loading time depends on internet speed and database performance
- Running the load once is recommended for optimal performance

