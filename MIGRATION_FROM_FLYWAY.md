# Migration from Flyway to Liquibase

This document describes the migration from Flyway to Liquibase that was completed on October 1, 2025.

## What Changed

### 1. Dependencies (pom.xml)
**Removed:**
- `org.flywaydb:flyway-core`
- `org.flywaydb:flyway-database-postgresql`
- `org.flywaydb:flyway-maven-plugin`

**Added:**
- `org.liquibase:liquibase-core`
- `org.liquibase:liquibase-maven-plugin`

### 2. Configuration Files
**Modified:**
- `src/main/resources/application.yml` - Changed from Flyway to Liquibase configuration
- `src/main/resources/application-dev.yml` - Updated for Liquibase

**Deleted:**
- `src/main/java/ru/pt/config/FlywayConfig.java` - No longer needed (Spring Boot auto-configuration handles Liquibase)
- `FLYWAY_SETUP.md` - Replaced with `LIQUIBASE_SETUP.md`

**Created:**
- `src/main/resources/liquibase.properties` - Maven plugin configuration
- `src/main/resources/db/changelog/db.changelog-master.xml` - Master changelog
- `src/main/resources/db/changelog/changes/v1-initial-schema.xml` - Converted from V1__Initial_schema.sql
- `src/main/resources/db/changelog/changes/v3-accountEntity-tables.xml` - Converted from V3__Create_account_tables.sql
- `LIQUIBASE_SETUP.md` - Complete Liquibase documentation

### 3. Migration Files Structure

**Before (Flyway):**
```
src/main/resources/db/migration/
├── V1__Initial_schema.sql
├── V2__Add_sample_migration.sql
└── V3__Create_account_tables.sql
```

**After (Liquibase):**
```
src/main/resources/db/changelog/
├── db.changelog-master.xml
└── changes/
    ├── v1-initial-schema.xml
    └── v3-accountEntity-tables.xml
```

## Benefits of This Migration

1. **Better Rollback Support**: Liquibase has built-in rollback capabilities
2. **Database Independence**: XML format makes it easier to support multiple databases
3. **More Flexibility**: Support for XML, YAML, JSON, and SQL formats
4. **Better Change Tracking**: Changeset-based tracking instead of version numbers
5. **Preconditions**: Can define conditions that must be met before executing changes
6. **Contexts and Labels**: Better control over which changes run in which environments

## How to Use

### First Time Setup (Fresh Database)
Simply start your application with Docker Compose:
```bash
docker-compose up -d
```

Liquibase will automatically:
1. Create the `databasechangelog` and `databasechangeloglock` tables
2. Execute all changesets in order
3. Mark them as executed

### Migrating Existing Flyway Database

If you have an existing database with Flyway migrations, you have two options:

#### Option 1: Clean Start (Development Only)
```bash
# Stop application
docker-compose down

# Remove database volume
docker volume rm ng-back_db_data

# Start fresh
docker-compose up -d
```

#### Option 2: Mark as Executed (Preserve Data)
If you need to keep existing data:

1. Connect to your PostgreSQL database
2. Drop the Flyway tracking table:
   ```sql
   DROP TABLE IF EXISTS flyway_schema_history;
   ```
3. Start your application - Liquibase will create its own tracking tables
4. Since all tables already exist, Liquibase changesets with `IF NOT EXISTS` will succeed without making changes
5. Liquibase will mark all changesets as executed

### Creating New Migrations

See `LIQUIBASE_SETUP.md` for detailed instructions on creating new migrations.

Quick example:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
    http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.20.xsd">

    <changeSet id="4" author="your-name">
        <comment>Add new column</comment>
        <addColumn tableName="pt_products">
            <column name="status" type="varchar(20)"/>
        </addColumn>
    </changeSet>
</databaseChangeLog>
```

Then add to `db.changelog-master.xml`:
```xml
<include file="db/changelog/changes/v4-add-status-column.xml"/>
```

## Verification

After migration, verify that:
1. Application starts without errors
2. All tables exist in the database
3. `databasechangelog` table is created and contains 2 entries (changeset id="1" and id="3")
4. `databasechangeloglock` table exists

## Rollback (If Needed)

If you need to rollback to Flyway:
1. Restore `pom.xml`, `application.yml`, and `application-dev.yml` from git
2. Restore Flyway migration files
3. Re-create `FlywayConfig.java`
4. In database, drop Liquibase tables: `DROP TABLE databasechangelog, databasechangeloglock;`
5. Rebuild and restart

## Support

For issues or questions:
- See `LIQUIBASE_SETUP.md` for detailed documentation
- Check [Liquibase Documentation](https://docs.liquibase.com/)
- Review application logs for error messages

