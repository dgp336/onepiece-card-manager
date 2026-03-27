# Instalación y Configuración - Cargador de Datos de Cartas

## Descripción

El backend incluye un sistema automático de cargue de datos que descarga información de cartas desde https://cdn.cardkaizoku.com/card_data.json e inserta los datos en la base de datos PostgreSQL.

## Características

✅ **Cargue Automático**: Se ejecuta automáticamente al iniciar la aplicación si la BD está vacía
✅ **Endpoints REST**: Permite controlar el cargue manualmente
✅ **Script Bash**: Herramienta CLI para facilitar el manejo de datos
✅ **Validación**: Valida que cada carta tenga al menos número y nombre
✅ **Manejo de Errores**: Logs detallados de cualquier problema

## Arquitectura

### Componentes Implementados

1. **CardDataLoaderService** (`service/CardDataLoaderService.java`)
   - Servicio principal que maneja descarga y procesamiento de datos
   - Métodos públicos:
     - `loadCardData()`: Descarga e inserta datos
     - `hasCards()`: Verifica si hay cartas en la BD
     - `clearAllCards()`: Elimina todas las cartas

2. **AppConfig** (`config/AppConfig.java`)
   - Configuración global de la aplicación
   - Define el bean de RestTemplate para peticiones HTTP

3. **OptcgApplication** (modificado)
   - Ahora incluye un CommandLineRunner
   - Ejecuta cargue automático al inicio si BD está vacía

4. **CardController** (modificado)
   - Tres nuevos endpoints REST:
     - `POST /cards/load-data`
     - `POST /cards/reload-data`
     - `DELETE /cards/clear-all`

### Estructura de Datos

El JSON remoto se espera que tenga una estructura como esta:

```json
[
  {
    "number": "ST01-001",
    "name": "Nombre de la Carta",
    "cost": "2",
    "attribute": "strength",
    "type": "Character",
    "power": "2000",
    "counter": "1000",
    "color": "Red",
    "feature": "Samurai",
    "text": "Descripción del efecto",
    "rarity": "C",
    "trigger": "draw",
    "block": "No",
    "set": "ST01",
    "img": "https://..."
  },
  ...
]
```

## Instalación

### Paso 1: Requisitos

```bash
# El backend requiere Java 21 y Maven
java -version  # Debe ser Java 21+
mvn -version   # Debe estar instalado
```

### Paso 2: Verificar Dependencias

Las dependencias necesarias ya están incluidas en `pom.xml`:

```xml
<!-- Spring Boot Web (incluye RestTemplate) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>

<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Base de datos -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Paso 3: Configuración de la Base de Datos

Asegúrate de que PostgreSQL está corriendo con las credenciales correctas en `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/db
spring.datasource.username=user
spring.datasource.password=pass
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=false
```

## Uso

### Iniciar la Aplicación

```bash
# Opción 1: Maven
cd backend/optcg
./mvnw spring-boot:run

# Opción 2: Con Docker Compose (desde raíz del proyecto)
cd scripts
docker-compose up -d
```

### Cargue Automático

Al iniciar el backend, el CommandLineRunner verificará:
1. Si hay cartas en la BD
2. Si está vacía, descargará e insertará todos los datos automáticamente
3. Si ya hay datos, saltará el cargue

Verás logs como estos:

```
INFO - La base de datos está vacía. Iniciando cargue de datos de cartas...
INFO - Descargando datos desde https://cdn.cardkaizoku.com/card_data.json
INFO - Se han cargado 5000 cartas exitosamente
```

### Cargue Manual - Opción 1: Script Bash

```bash
cd scripts

# Ver opciones disponibles
./load-card-data.sh help

# Cargar datos (solo si BD está vacía)
./load-card-data.sh load

# Recargar todos los datos
./load-card-data.sh reload

# Limpiar todos los datos
./load-card-data.sh clear
```

### Cargue Manual - Opción 2: cURL

```bash
# Cargar datos
curl -X POST http://localhost:8080/cards/load-data \
  -H "Content-Type: application/json"

# Recargar datos
curl -X POST http://localhost:8080/cards/reload-data \
  -H "Content-Type: application/json"

# Limpiar datos
curl -X DELETE http://localhost:8080/cards/clear-all \
  -H "Content-Type: application/json"
```

### Cargue Manual - Opción 3: REST Client (VS Code)

Si tienes la extensión REST Client, crea un archivo `requests.http`:

```http
### Cargar datos
POST http://localhost:8080/cards/load-data
Content-Type: application/json

###Recargar datos
POST http://localhost:8080/cards/reload-data
Content-Type: application/json

### Limpiar datos
DELETE http://localhost:8080/cards/clear-all
Content-Type: application/json
```

## Respuestas de los Endpoints

### Exitosa

```json
{
  "success": true,
  "message": "Cartas cargadas exitosamente",
  "cardsLoaded": 5000
}
```

### Error

```json
{
  "success": false,
  "message": "Error al cargar las cartas: Connection timeout",
  "cardsLoaded": 0
}
```

## Logs

El sistema genera logs detallados. Para verlos:

```bash
# Con Docker Compose
docker-compose logs -f backend

# Con Maven directamente
# Los logs se mostrarán en la consola
```

Niveles de log:
- **INFO**: Operaciones normales (cargue iniciado, BD vacía, etc.)
- **WARN**: Situaciones inesperadas (cartas sin datos completos, BD ya tiene cartas)
- **ERROR**: Errores que impiden continuar (conexión fallida, error parsing JSON, etc.)

## Solución de Problemas

### "Connection timeout"
- Verifica que https://cdn.cardkaizoku.com esté accesible
- Rev isa tu conexión a internet
- El timeout está configurado a 30 segundos

### "La base de datos ya contiene cartas"
- Esto es normal. El cargue automático solo funciona si la BD está vacía
- Para recargar: `./load-card-data.sh reload` o `POST /cards/reload-data`

### "Cartas sin número o nombre"
- El sistema valida que cada carta tenga número y nombre
- Las cartas inválidas se saltan automáticamente
- Revisa los logs para ver cuáles se descartaron

### "Error parsing JSON"
- Verifica que la estructura del JSON remoto es correcta
- El sistema espera un array o un objeto con campo "cards"

## Performance

- Cargue típico: ~5000 cartas en 30-60 segundos
- Acceso a BD: ~10ms por consulta
- Requiere conexión a internet para descarga

## Implementación Técnica

### Flujo de Cargue

```
1. OptcgApplication.main()
   ↓
2. CommandLineRunner ejecuta
   ├─ Verifica if hasCards()
   │  ├─ Si false: loadCardData()
   │  │  ├─ RestTemplate GET JSON
   │  │  ├─ ObjectMapper parse JSON
   │  │  ├─ parseCardFromJson() para cada carta
   │  │  ├─ cardRepository.saveAll()
   │  │  └─ Log resultado
   │  └─ Si true: Log "BD ya contiene cartas"
   └─ Fin

3. Aplicación iniciada, endpoints disponibles
```

### Seguridad

- Los endpoints no tienen restrict iones adicionales (puedes agregar @Secured si lo necesitas)
- Las peticiones HTTP se hacen solo en startup o por request explícito
- La validación de datos es básica (número y nombre no nulos)

## Mantenimiento

Si en el futuro necesitas:

1. **Cambiar la URL remota**: Modifica `CARD_DATA_URL` en `CardDataLoaderService.java`

2. **Agregar más validaciones**: Expande el método `parseCardFromJson()`

3. **Cambiar el comportamiento del cargue automático**: Modifica el `CommandLineRunner` en `OptcgApplication.java`

4. **Agregar autenticación a los endpoints**: Agrega `@Secured` a los métodos del controlador

## Referencias

- Spring RestTemplate: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html
- Jackson JSON: https://github.com/FasterXML/jackson
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
