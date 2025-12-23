# Repository Guidelines

## Project Structure & Module Organization
The project follows a modular Android layout. `app/` hosts the launcher module and global Compose navigation graph. Shared logic lives under `core/` (common utilities, persistence, domain rules, UI primitives, navigation scaffolding), while end-user screens are isolated inside `feature/feature-*` modules to keep generator, history, and settings flows independent. Unit tests sit in each module’s `src/test/java`, with instrumentation and Compose UI tests mirrored under `src/androidTest/java`.

## Build, Test, and Development Commands
- `./gradlew assembleDebug` — compile all modules and produce a debuggable APK.
- `./gradlew :app:installDebug` — install the debug build on a connected device or emulator.
- `./gradlew lint` — run Android/Kotlin lint plus Compose checks across every module.
- `./gradlew :core:core-domain:testDebugUnitTest` (or other module/task) — execute JVM unit tests in isolation when iterating on rules or randomization logic.

## Coding Style & Naming Conventions
Source files use Kotlin with 4-space indentation and trailing commas enabled for multi-line constructs. Compose components are PascalCase (`HistoryScreen`), functions and properties are lowerCamelCase, and Gradle modules follow `feature-*` / `core-*` prefixes defined in `settings.gradle.kts`. Keep state hoisted and favor immutable flows in `core-data`/`core-domain`. Gradle scripts stay in Kotlin DSL; use version-catalog aliases (`gradle/libs.versions.toml`) instead of raw coordinates.

## Testing Guidelines
JVM tests rely on JUnit 4; use descriptive method names such as `shouldGenerateNumbersInRange`. Compose UI tests and navigation flows belong in `androidTest` using `androidx.compose.ui.test.junit4`, Espresso, and the Compose testing manifest already wired in `app/build.gradle.kts`. Before opening a PR, run `./gradlew testDebugUnitTest` to cover all unit-testable modules and `./gradlew connectedDebugAndroidTest` (emulator required) for UI coverage. Aim to mock randomness boundaries in generator tests so snapshots remain deterministic.

## Commit & Pull Request Guidelines
The repo lacks published Git history, so default to short, imperative messages that scope the touched module (e.g., `feature-generator: add Powerball presets`). Each PR should describe the user-facing change, list affected modules, call out new Gradle tasks or permissions, and attach screenshots/GIFs for UI updates. Link tracking issues when available and note any skipped tests with justification so reviewers can verify them manually.

## Security & Configuration Tips
Keep API keys, analytics IDs, or remote config endpoints out of version control—place them in `local.properties` or an injected `BuildConfig` field referenced from `core-data`. Never commit the generated `firebase-debug.log` or emulator databases; instead, document reproducible steps for reviewers.***
