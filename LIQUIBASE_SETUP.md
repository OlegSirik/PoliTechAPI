# Liquibase Database Migration Setup

## Overview
This project uses Liquibase for database migration management, providing controlled and versioned schema changes.

## What Was Implemented

### 1. Dependencies Added
- `liquibase-core` - Core Liquibase functionality integrated with Spring Boot
- `liquibase-maven-plugin` - Maven plugin for command-line migration management

### 2. Configuration Changes
- **application.yml**: Added Liquibase configuration
- **JPA Configuration**: Using `ddl-auto: validate` to prevent Hibernate from auto-generating schema
- **Liquibase Settings**:
  - `enabled: true` - Enables Liquibase on startup
  - `change-log: classpath:db/changelog/db.changelog-master.xml` - Master changelog location
  - `drop-first: false` - Prevents dropping schema on startup

### 3. Migration Structure
```
src/main/resources/
├── liquibase.properties                    # Liquibase Maven plugin configuration
└── db/
    └── changelog/
        ├── db.changelog-master.xml         # Master changelog file
        └── changes/
            ├── v1-initial-schema.xml       # Initial database schema
            └── v3-accountEntity-tables.xml       # Account management tables
```

### 4. Files Created
- **db.changelog-master.xml**: Master changelog that includes all changesets
- **v1-initial-schema.xml**: Initial database schema (tables, sequences, indexes)
- **v3-accountEntity-tables.xml**: Account management system tables
- **liquibase.properties**: Configuration for Maven plugin

### 5. Files Modified
- **pom.xml**: Added Liquibase dependencies and Maven plugin
- **application.yml**: Added Liquibase configuration

## Usage

### Running Migrations
Migrations run automatically when the Spring Boot application starts. Liquibase will:
1. Check if the `databasechangelog` table exists (creates it if not)
2. Compare executed changesets with changelog files
3. Execute any pending changesets in order

### Manual Migration Commands (Gradle)
```bash
# Check migration status
./gradlew checkMigrationStatus
# Alternatively: ./gradlew liquibaseStatus

# Run pending migrations
./gradlew migrateDatabase
# Alternatively: ./gradlew update

# Validate changelog
./gradlew validateChangelog

# Generate SQL for pending migrations (without executing)
./gradlew updateSQL

# Rollback last changeset
./gradlew liquibaseRollback -PliquibaseCommandValue=1

# Clear checksums (if needed after manual fixes)
./gradlew clearChecksums
```

### Creating New Migrations

#### Option 1: XML Format (Recommended for complex changes)
Create a new XML file in `src/main/resources/db/changelog/changes/`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
    http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.20.xsd">

    <changeSet id="4" author="your-name">
        <comment>Add audit columns to pt_products</comment>
        
        <addColumn tableName="pt_products">
            <column name="created_at" type="timestamp" defaultValueComputed="CURRENT_TIMESTAMP"/>
            <column name="updated_at" type="timestamp"/>
        </addColumn>
    </changeSet>

</databaseChangeLog>
```

Then add it to `db.changelog-master.xml`:
```xml
<include file="db/changelog/changes/v4-add-audit-columns.xml"/>
```

#### Option 2: SQL Format (For simple SQL scripts)
Create a new SQL file in `src/main/resources/db/changelog/changes/`:

```sql
--liquibase formatted sql

--changeset author-name:5
--comment: Add new index
CREATE INDEX pt_products_name_idx ON pt_products(name);
```

Then add it to `db.changelog-master.xml`:
```xml
<include file="db/changelog/changes/v5-add-indexes.sql"/>
```

## Benefits of Liquibase
- **Database Agnostic**: Works with PostgreSQL, MySQL, Oracle, etc.
- **Version Control**: Database changes are versioned and tracked
- **Team Collaboration**: Consistent schema across all environments
- **Rollback Support**: Built-in rollback capabilities
- **Flexible Formats**: Support for XML, YAML, JSON, and SQL
- **Spring Boot Integration**: Seamless integration with Spring Boot
- **Change Tracking**: `databasechangelog` table tracks all executed changesets

## Migration Best Practices

### General Guidelines
- Keep changesets small and focused on one logical change
- Never modify executed changesets (create new ones instead)
- Use meaningful changeset IDs and author names
- Add comments to explain complex changes
- Test migrations thoroughly before deployment

### XML Format Guidelines
- Use `<createTable>`, `<addColumn>`, etc. for database-agnostic changes
- Leverage built-in rollback for supported operations
- Use preconditions to ensure safe execution

### SQL Format Guidelines
- Start with `--liquibase formatted sql` comment
- Use `--changeset author:id` for each change
- Add `--rollback` comments for custom rollback logic
- Use `--comment` to document changes

### Rollback Strategy
```xml
<!-- Automatic rollback for supported operations -->
<changeSet id="6" author="dev">
    <addColumn tableName="pt_products">
        <column name="description" type="text"/>
    </addColumn>
    <!-- Liquibase automatically knows how to rollback -->
</changeSet>

<!-- Custom rollback for complex operations -->
<changeSet id="7" author="dev">
    <sql>
        UPDATE pt_products SET status = 'ACTIVE' WHERE status IS NULL;
    </sql>
    <rollback>
        UPDATE pt_products SET status = NULL WHERE status = 'ACTIVE';
    </rollback>
</changeSet>
```

## Troubleshooting

### Migration Fails
1. Check application logs for specific error messages
2. Verify database connectivity and credentials
3. Check if `databasechangelog` table exists
4. Validate changelog XML syntax
5. Use `mvn liquibase:validate` to check changelog

### Checksum Validation Failed
If you need to modify an executed changeset (not recommended):
```bash
mvn liquibase:clearCheckSums
```

### Starting Fresh (Development Only)
```bash
# Stop application
docker-compose down

# Remove database volume
docker volume rm ng-back_db_data

# Start fresh
docker-compose up -d
```

### Migration Already Executed
If Liquibase says a changeset was already executed:
- Check the `databasechangelog` table in your database
- Verify the changeset ID hasn't been used before
- Consider using a new changeset ID

## Differences from Flyway

| Feature | Flyway | Liquibase |
|---------|--------|-----------|
| Default Format | SQL | XML (also supports SQL, YAML, JSON) |
| Rollback | Manual (Pro feature) | Built-in |
| Database Agnostic | Limited | Excellent |
| Change Tracking | Version-based | Changeset-based |
| Preconditions | Limited | Extensive |
| Spring Boot Support | Excellent | Excellent |

## Environment-Specific Changes

For development-specific settings, update `application-dev.yml`:
```yaml
spring:
  liquibase:
    enabled: true
    drop-first: false  # Set to true to drop schema on startup (dev only!)
    contexts: dev      # Run only changesets tagged with "dev" context
```

## Additional Resources
- [Liquibase Documentation](https://docs.liquibase.com/)
- [Spring Boot Liquibase Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.liquibase)
- [Liquibase Best Practices](https://www.liquibase.org/get-started/best-practices)

