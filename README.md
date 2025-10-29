# Insurance Policy Management System

A Spring Boot application for managing insurance policies with advanced number generation capabilities.

## Features

- **Policy Processing**: Complete policy lifecycle management with validation and calculation
- **Number Generator**: Flexible policy number generation with customizable masks
- **Product Management**: Insurance product and version management
- **LOB Management**: Line of Business configuration
- **File Management**: Document storage and retrieval

## Requirements

- Java 17+
- Gradle 8.7+ (wrapper included)
- PostgreSQL 13+

## Quick Start

### Using Docker (Recommended)

```bash
# Build and start the application with PostgreSQL
docker compose up -d --build

# The application will be available at http://localhost:8080
```

### Run Locally

1. Create database:
```bash
createdb pt_db
```

2. Configure credentials in `src/main/resources/application.yml` if different from defaults.

3. Build and run:
```bash
./gradlew bootRun
```

App listens on `http://localhost:8080`.

## Number Generator System

The number generator provides flexible policy number generation with support for various patterns and automatic date substitution.

### Features

- **Customizable Masks**: Use `{KEY}` format for flexible number generation
- **Date Integration**: Built-in support for `{YYYY}`, `{YY}`, `{MM}`
- **Sequential Numbers**: Any length `{X...}` patterns (e.g., `{X}`, `{XX}`, `{XXXX}`)
- **Reset Policies**: YEARLY, MONTHLY, or NEVER reset options
- **Thread-Safe**: Pessimistic locking for concurrent access

### Database Schema

```sql
CREATE TABLE pt_number_generators (
  id BIGSERIAL PRIMARY KEY,
  product_code VARCHAR(100) NOT NULL,
  mask VARCHAR(255) NOT NULL,
  reset_policy VARCHAR(20) NOT NULL,  -- YEARLY | MONTHLY | NEVER
  max_value INT NOT NULL DEFAULT 999999,
  last_reset DATE NOT NULL DEFAULT CURRENT_DATE,
  current_value INT NOT NULL DEFAULT 0
);
```

### Usage Examples

#### 1. Basic Setup
Create a number generator record:
```sql
INSERT INTO pt_number_generators (product_code, mask, reset_policy, max_value) 
VALUES ('NS-SRAVNIRU', '{YYYY}-{NS}-{XXXX}', 'YEARLY', 9999);
```

#### 2. API Usage

**Endpoint**: `POST /test/policy/nextnumber/{id}`

**Request Body**:
```json
{
  "NS": "INS",
  "PREFIX": "POL"
}
```

**Response**: `2025-INS-0001`

#### 3. Mask Patterns

| Pattern | Description | Example Output |
|---------|-------------|----------------|
| `{YYYY}` | Full year | `2025` |
| `{YY}` | Two-digit year | `25` |
| `{MM}` | Two-digit month | `09` |
| `{X}` | Single digit sequence | `1` |
| `{XX}` | Two-digit sequence | `01` |
| `{XXXX}` | Four-digit sequence | `0001` |
| `{CUSTOM}` | Custom key from request | Value from JSON |

#### 4. Reset Policies

- **YEARLY**: Resets sequence on January 1st
- **MONTHLY**: Resets sequence on the 1st of each month  
- **NEVER**: Never resets, continues incrementing

#### 5. Complete Examples

**Mask**: `{YYYY}-{PRODUCT}-{XXXX}`
**Request**: `{"PRODUCT": "NS"}`
**Output**: `2025-NS-0001`

**Mask**: `{YY}{MM}-{PREFIX}-{XXXX}`
**Request**: `{"PREFIX": "POL"}`
**Output**: `2509-POL-0001`

**Mask**: `{BRANCH}/{YYYY}/{XXXXX}`
**Request**: `{"BRANCH": "MOS"}`
**Output**: `MOS/2025/00001`

### API Endpoints

#### Number Generator
- `POST /test/policy/nextnumber/{id}` - Generate next policy number

#### Policy Processing
- `POST /test/policy/inner` - Process policy with validation and calculation

#### General API
- `GET /api/notes` – list notes
- `POST /api/notes` – create note
- `GET /api/notes/{id}` – get note
- `PUT /api/notes/{id}` – update note
- `DELETE /api/notes/{id}` – delete note

### Database Configuration

Default JDBC URL: `jdbc:postgresql://localhost:5432/pt_db`

Override via environment variables:
```bash
DB_HOST=localhost  # Database host (default: localhost)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pt_db
SPRING_DATASOURCE_USERNAME=spring
SPRING_DATASOURCE_PASSWORD=spring
```

### Database Migrations

This project uses **Liquibase** for database schema management. Migrations run automatically on application startup.

**Key Features:**
- Automatic schema versioning and tracking
- Built-in rollback support
- Database-agnostic change definitions
- Support for XML, YAML, JSON, and SQL formats

**Documentation:**
- See `LIQUIBASE_SETUP.md` for complete Liquibase documentation
- See `MIGRATION_FROM_FLYWAY.md` for migration history from Flyway

**Quick Commands:**
```bash
./gradlew bootRun                 # Start application (Flyway runs automatically)
./gradlew build                   # Build with migrations
```

### Docker Configuration

The application includes Docker support with:
- Multi-stage build for optimized image size
- PostgreSQL database integration
- Environment-based configuration

**Docker Hub Image**: `olegsirik/vsk-robot-zaytsev:03`

### Development

The project follows standard Spring Boot conventions:
- Domain entities in `ru.pt.domain`
- Services in `ru.pt.service`  
- REST controllers in `ru.pt.api`
- Repositories in `ru.pt.repository`

### Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request
