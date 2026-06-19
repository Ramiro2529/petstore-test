# petstore-test

API REST desarrollada en Spring Boot que actúa como intermediario hacia la API externa [Petstore](https://petstore.swagger.io).

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/pet/{petId}` | Consulta una mascota por ID |
| POST | `/api/pet` | Registra una nueva mascota |

### GET `/api/pet/{petId}`
**Response:**
```json
{
  "id": 1,
  "name": "doggie",
  "status": "available"
}
```

### POST `/api/pet`
**Request:**
```json
{
  "id": 1,
  "name": "doggie",
  "status": "available"
}
```
**Response:**
```json
{
  "transactionId": "uuid-v4",
  "dateCreated": "2026-06-19T10:00:00Z",
  "name": "doggie",
  "status": "available"
}
```

## Tecnologías

- Java 17
- Spring Boot 3.5
- RestTemplate
- Bean Validation
- Jacoco (cobertura mínima 90%)

## Ejecutar

```bash
./gradlew bootRun
```

## Tests

```bash
./gradlew test
```
