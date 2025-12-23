# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

LotteryGenerator is a multi-module Android application demonstrating MVI architecture with Jetpack Compose. It generates lottery numbers for various lottery types (Powerball, Mega Millions, Lotto 6/49) with animations and history tracking.

- **Package**: `com.aaltix.lotto`
- **Language**: Kotlin 2.0.21
- **Min SDK**: 24 (Android 7.0)
- **Target/Compile SDK**: 36 (Android 15)

## Architecture

- **MVI (Model-View-Intent)**: Unidirectional data flow with State, Intent, and Effect
- **Multi-Module**: 9 modules organized by layer (core, feature, app)
- **Dependency Injection**: Koin 4.0.1
- **UI**: Jetpack Compose with Material3
- **Interface Segregation**: Repository split into focused interfaces (NumberGeneratorRepository, LotteryConfigRepository, HistoryRepository)

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build and install on device/emulator
./gradlew installDebug

# Run all unit tests
./gradlew test

# Run specific module tests
./gradlew :feature:feature-generator:test
./gradlew :core:core-data:test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean build

# Check for dependency updates
./gradlew dependencyUpdates
```

## Module Structure

```
LotteryGenerator/
├── app/                          # Entry point, Koin setup, navigation host
├── core/
│   ├── core-common/              # BaseViewModel, Result, DispatcherProvider
│   ├── core-data/                # Repository, stubbed API, DTOs, DataMapper
│   ├── core-database/            # Room database, DAOs, entities
│   ├── core-domain/              # Use cases, domain models, mappers
│   ├── core-ui/                  # Theme, components, animations, string resources
│   └── core-navigation/          # Route definitions, bottom nav items
└── feature/
    ├── feature-generator/        # Main number generation screen
    ├── feature-history/          # History list & detail screens
    └── feature-settings/         # Lottery type preferences
```

## Key Files

### MVI Base Classes
- `core/core-common/.../BaseViewModel.kt` - Abstract ViewModel with State, Intent, Effect
- `feature/feature-generator/.../GeneratorContract.kt` - Example MVI contract

### Dependency Injection
- `app/.../di/AppModule.kt` - Combines all Koin modules
- Each module has its own `*Module.kt` file

### Navigation
- `app/.../navigation/LottoNavHost.kt` - Navigation graph
- `core/core-navigation/.../BottomNavItem.kt` - Bottom nav enum

### Data Layer
- `core/core-data/.../StubbedLotteryApi.kt` - Simulated API with 800ms delay
- `core/core-data/.../DataMapper.kt` - Centralized DTO/Entity mapping with safe parsing
- `core/core-data/.../repository/` - Segregated repository interfaces:
  - `NumberGeneratorRepository` - Number generation
  - `LotteryConfigRepository` - Lottery type configuration
  - `HistoryRepository` - History operations with pagination
  - `LotteryRepository` - Combined interface extending all three
- `core/core-database/.../LottoDatabase.kt` - Room database

### UI Resources
- `core/core-ui/src/main/res/values/strings.xml` - Centralized string resources for all feature modules
- `core/core-ui/.../theme/Color.kt` - Sky blue theme with light gray background

## Testing

Tests use JUnit4, Turbine (Flow testing), MockK, and TestCoroutineDispatcher.

### Testing Style: Given-When-Then

All tests follow the **Given-When-Then** (Arrange-Act-Assert) pattern for clarity and consistency:

```kotlin
@Test
fun `descriptive test name in backticks`() = runTest {
    // Given - Setup preconditions
    val expectedResult = ...
    coEvery { someUseCase() } returns Result.Success(data)

    // When - Execute the action being tested
    viewModel.processIntent(SomeIntent.DoAction)
    advanceUntilIdle()

    // Then - Assert expected outcomes
    viewModel.state.test {
        val state = awaitItem()
        assertEquals(expectedResult, state.data)
        cancelAndIgnoreRemainingEvents()
    }
}
```

### Test Utilities
- `core/core-common/src/test/.../MainDispatcherRule.kt` - JUnit rule for test dispatchers
- `core/core-common/src/test/.../TestDispatcherProvider.kt` - Test dispatcher provider

### Test Fakes
- `core/core-data/src/test/.../fake/FakeLotteryApi.kt` - Controllable API for testing
- `core/core-data/src/test/.../fake/FakeHistoryDao.kt` - In-memory DAO for testing

### Test Coverage
- `StubbedLotteryApiTest.kt` - Number generation logic (counts, ranges, uniqueness, sorting, randomness)
- `DataMapperTest.kt` - DTO/Entity conversion, safe parsing, round-trip tests
- `LotteryRepositoryTest.kt` - Repository operations, history, pagination

### Test Locations
- Unit tests: `src/test/java/` in each module
- Instrumented tests: `src/androidTest/java/` (app module)

## Tech Stack

- **Build**: Gradle 8.13 with Kotlin DSL, AGP 8.13.2
- **Compose BOM**: 2024.12.01
- **Room**: 2.6.1 for persistence
- **Navigation**: Compose Navigation 2.8.5
- **Coroutines**: 1.9.0
- **Testing**: JUnit4, Turbine 1.2.0, MockK 1.13.13
- **Java Target**: Java 11

## Dependency Management

Dependencies are managed via Gradle version catalog at `gradle/libs.versions.toml`. To add dependencies:
1. Add version to `[versions]` section
2. Add library to `[libraries]` section
3. Reference in build.gradle.kts using `libs.xxx` syntax

## Lottery Types Supported

| Type | Main Numbers | Bonus |
|------|-------------|-------|
| Powerball | 5 from 1-69 | 1 from 1-26 |
| Mega Millions | 5 from 1-70 | 1 from 1-25 |
| Lotto 6/49 | 6 from 1-49 | None |

## Code Style

- Follow Kotlin coding conventions
- Use MVI pattern for new features (Contract, ViewModel, Screen)
- Use sealed classes for Intent and Effect
- Use data class for State
- Keep UI stateless - all state in ViewModel
- Use `stringResource()` for all UI strings in Composables
- String resources centralized in `core-ui` module for multi-module access
- Always use AndroidX libraries instead of legacy Android Support libraries

## Design Patterns Applied

- **Interface Segregation**: Repository split into focused interfaces
- **Single Responsibility**: DataMapper handles all DTO/Entity conversions
- **Safe Parsing**: Number parsing uses `toIntOrNull()` with logging for invalid values
- **Pagination Support**: HistoryDao and HistoryRepository support paged queries
