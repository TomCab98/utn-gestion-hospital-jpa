# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/org/example` contains the application code. Key packages include `config`, `entidades`, `repositorios`, `servicio`, `ui`, and `utils`.
- `src/main/resources` holds configuration such as `META-INF/persistence.xml` and `logback.xml`.
- `data/` stores the local H2 database files (e.g., `hospidb.mv.db`) used by the persistence layer.
- No `src/test` directory is present yet; tests should be added under `src/test/java` when created.

## Build, Test, and Development Commands
- `./gradlew build` (or `gradlew.bat build` on Windows): compiles and assembles the project.
- `./gradlew run`: starts the CLI application (`org.example.Main`).
- `./gradlew test`: runs JUnit 5 tests (once added).

## Coding Style & Naming Conventions
- Indentation uses 2 spaces as in existing sources.
- Java packages are lowercase (`org.example.*`), classes are `PascalCase`, methods and fields are `camelCase`.
- Keep classes focused by package: entities in `entidades`, repositories in `repositorios`, and UI flow in `ui`.
- Use Lombok only where it is already used; avoid introducing new annotations without clear benefit.

## Testing Guidelines
- Testing framework: JUnit 5 (configured in `build.gradle`).
- Place tests in `src/test/java`, mirroring package names (e.g., `org.example.servicio`).
- Name tests with `*Test` or `*IT` suffixes. Prefer small unit tests before adding integration tests.

## Commit & Pull Request Guidelines
- Commit messages follow a Conventional Commits style such as `feat: ...` and `fix: ...`.
- Keep commits scoped and descriptive.
- Pull requests should include a short summary, how to test (`./gradlew test` or a manual flow), and any relevant screenshots or console output for CLI changes.

## Configuration & Data Notes
- JPA configuration lives in `src/main/resources/META-INF/persistence.xml` and points to the local H2 file database under `data/`.
- Be deliberate when changing database files in `data/`; they affect local state for other contributors.
