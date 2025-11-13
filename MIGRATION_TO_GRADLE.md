# Migration from Maven to Gradle

This document describes the complete migration from Maven to Gradle that was completed on October 1, 2025.

## 🎯 What Was Changed

### ✅ Gradle Build System Setup
- **Added Gradle Wrapper**: `gradlew` and `gradlew.bat` for consistent builds
- **Created build.gradle**: Complete dependency configuration and tasks
- **Created settings.gradle**: Project configuration
- **Created gradle.properties**: Build optimization settings

### ✅ Dependencies Migrated
All Maven dependencies successfully converted to Gradle:

**Spring Boot Starters:**
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-validation`
- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-clientEntity`

**Security & JWT:**
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (version 0.12.3)
- `spring-security-crypto`

**Database & Migration:**
- `postgresql` (runtime)
- `liquibase-core`
- Liquibase Gradle plugin (version 2.2.1)

**Other Libraries:**
- `jayway.jsonpath` (2.9.0)
- `pdfbox`, `pdfbox-io` (3.0.2)
- `lombok`

### ✅ Liquibase Integration
- **Gradle Plugin**: `org.liquibase.gradle` (version 2.2.1)
- **Custom Tasks**: Added `migrateDatabase` and `checkMigrationStatus`
- **Configuration**: Database connection settings in build.gradle
- **Migration**: All existing migrations V1, V3, V4 preserved

### ✅ Build Tasks Available

| Task | Description | Maven Equivalent |
|------|-------------|------------------|
| `./gradlew build` | Compile and package | `mvn package` |
| `./gradlew bootRun` | Run Spring Boot app | `mvn spring-boot:run` |
| `./gradlew test` | Run tests | `mvn test` |
| `./gradlew clean` | Clean build artifacts | `mvn clean` |
| `./gradlew migrateDatabase` | Run Liquibase migrations | `mvn liquibase:update` |
| `./gradlew checkMigrationStatus` | Check migration status | `mvn liquibase:status` |
| `./gradlew liquibaseStatus` | Alternative status check | `mvn liquibase:status` |
| `./gradlew update` | Run migrations | `mvn liquibase:update` |
| `./gradlew validateChangelog` | Validate changelog | `mvn liquibase:validate` |
| `./gradlew updateSQL` | Generate SQL (dry run) | `mvn liquibase:updateSQL` |

## 🚀 Quick Start with Gradle

### 1. Install Java 17+
```bash
# Verify Java version
java -version
```

### 2. Build Project
```bash
# Build the application
./gradlew build

# Run the application
./gradlew bootRun
```

### 3. Database Operations
```bash
# Check migration status
./gradlew checkMigrationStatus

# Run migrations
./gradlew migrateDatabase

# Alternative commands
./gradlew liquibaseStatus
./gradlew update
```

### 4. Development Workflow
```bash
# Clean and build
./gradlew clean build

# Run tests
./gradlew test

# Run with Docker Compose
docker-compose up -d
```

## 📁 New File Structure

```
ng-back/
├── build.gradle                 # Gradle build configuration
├── settings.gradle              # Project settings
├── gradle.properties           # Gradle properties
├── gradlew                     # Gradle wrapper (Unix/Mac)
├── gradlew.bat                # Gradle wrapper (Windows)
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar # Wrapper JAR
│       └── gradle-wrapper.properties
├── src/main/
│   ├── java/                  # Java source files
│   └── resources/             # Application resources
│       └── db/changelog/      # Liquibase migrations
└── docker-compose.yml        # Docker configuration
```

## ⚙️ Gradle Configuration Details

### Build Script Features
```gradle
// Java toolchain (Java 17)
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

// Dependency management
dependencyManagement {
    imports {
        mavenBom org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES
    }
}

// Liquibase configuration
liquibase {
    activities {
        main {
            changeLogFile 'src/main/resources/db/changelog/db.changelog-master.xml'
            url 'jdbc:postgresql://localhost:5432/pt_db'
            username 'spring'
            password 'spring'
            driver 'org.postgresql.Driver'
            classpath 'src/main/resources'
        }
    }
}
```

### Performance Optimizations
```properties
# gradle.properties
org.gradle.jvmargs=-Xmx2048M -XX:MaxMetaspaceSize=512m
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true
```

## 🔧 IDE Integration

### IntelliJ IDEA
1. Open the project root directory (contains `build.gradle`)
2. IDEA will automatically detect Gradle project
3. Sync dependencies when prompted
4. Enable Gradle auto-import for future changes

### Eclipse with Buildship
1. Import existing project as Gradle project
2. Point to directory containing `build.gradle`
3. Run Gradle refresh/sync

### Visual Studio Code
1. Install Gradle Extension Pack
2. Open project folder
3. Gradle tasks will be available in Explorer panel

## 🐛 Troubleshooting

### Common Issues

**1. Build Fails - Dependencies Not Found**
```bash
# Clean and refresh dependencies
./gradlew clean build --refresh-dependencies
```

**2. Java Version Issues**
```bash
# Set JAVA_HOME correctly
export JAVA_HOME=/path/to/java17

# Or use specific Java version
./gradlew build -Dorg.gradle.java.home=/path/to/java17
```

**3. Gradle Wrapper Permissions (Unix/Mac)**
```bash
chmod +x gradlew
```

**4. Liquibase Connection Issues**
- Check database is running
- Verify connection settings in `build.gradle`
- Ensure PostgreSQL is accessible on localhost:5432

**5. Out of Memory Errors**
```bash
# Increase Gradle memory in gradle.properties
org.gradle.jvmargs=-Xmx4096M -XX:MaxMetaspaceSize=1024m
```

## 📊 Comparison: Maven vs Gradle

| Feature | Maven Command | Gradle Command |
|---------|---------------|----------------|
| Clean | `mvn clean` | `./gradlew clean` |
| Compile | `mvn compile` | `./gradlew compileJava` |
| Package | `mvn package` | `./gradlew build` |
| Test | `mvn test` | `./gradlew test` |
| Run | `mvn spring-boot:run` | `./gradlew bootRun` |
| Liquibase Status | `mvn liquibase:status` | `./gradlew checkMigrationStatus` |
| Liquibase Update | `mvn liquibase:update` | `./gradlew migrateDatabase` |

## 🎉 Benefits of Gradle Migration

### Performance
- **Faster Builds**: Incremental compilation and caching
- **Parallel Execution**: Multi-project builds run in parallel
- **Daemon Mode**: Persistent Gradle processes reduce startup time

### Flexibility
- **Groovy DSL**: More readable and flexible build scripts
- **Plugin Ecosystem**: Rich plugin ecosystem
- **Custom Tasks**: Easy to create custom build tasks

### Developer Experience
- **Better IDE Integration**: Superior IntelliJ IDEA and VS Code support
- **Rich Output**: More informative build output
- **Dependency Insight**: Better dependency resolution information

## 🔄 Migration Verification

To ensure the migration was successful:

1. **Build Verification:**
   ```bash
   ./gradlew clean build
   ```

2. **Test Verification:**
   ```bash
   ./gradlew test
   ```

3. **Run Verification:**
   ```bash
   ./gradlew bootRun
   ```

4. **Database Verification:**
   ```bash
   ./gradlew checkMigrationStatus
   ```

5. **Docker Verification:**
   ```bash
   docker-compose up -d --build
   ```

## 📚 Additional Resources

- [Gradle Documentation](https://docs.gradle.org/)
- [Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/)
- [Liquibase Gradle Plugin](https://docs.liquibase.com/tools-integrations/gradle/home.html)
- [Gradle Build Script Basics](https://docs.gradle.org/current/userguide/tutorial_using_tasks.html)

The migration to Gradle is complete and the build system is now more efficient and flexible! 🎉
