# LotteryGenerator

A modern, multi-module Android application demonstrating **MVI architecture** with **Jetpack Compose**. Generate lottery numbers for various lottery types with smooth animations, confetti effects, and comprehensive history tracking.

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.12.01-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange.svg)](https://developer.android.com)

---

## Features

- **Multiple Lottery Types**: Support for 8 predefined lottery games including Powerball, Mega Millions, Lotto 6/49, Cash4Life, and more
- **Custom Lottery Types**: Create your own lottery configurations with custom number ranges
- **Animated Number Generation**: Smooth bounce animations with staggered delays
- **Confetti Celebration**: Physics-based particle animation on number generation
- **History Tracking**: Full history with pagination, filtering, and detail views
- **Dark Mode Support**: Material3 theming with light and dark mode
- **Offline First**: All data stored locally with Room database
- **Clean Architecture**: Multi-module MVI architecture with clear separation of concerns

## Supported Lottery Types

| Lottery | Main Numbers | Bonus Ball |
|---------|--------------|------------|
| **Powerball** | 5 from 1-69 | 1 from 1-26 |
| **Mega Millions** | 5 from 1-70 | 1 from 1-25 |
| **Lotto 6/49** | 6 from 1-49 | None |
| **Cash4Life** | 5 from 1-60 | 1 from 1-4 |
| **Lucky for Life** | 5 from 1-48 | 1 from 1-18 |
| **Lotto America** | 5 from 1-52 | 1 from 1-10 |
| **Pick 3** | 3 digits 0-9 | None |
| **Pick 4** | 4 digits 0-9 | None |

---

## Architecture

LotteryGenerator follows the **MVI (Model-View-Intent)** architectural pattern with a **multi-module** structure organized by layer.

### MVI Pattern

```
┌─────────────────────────────────────────────────────────────┐
│                         UI Layer                            │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                   Composable Screen                  │   │
│  │  • Renders State                                     │   │
│  │  • Emits Intent on user action                       │   │
│  │  • Observes Effect for one-time events               │   │
│  └──────────────────────┬──────────────────────────────┘   │
│                         │                                   │
│                    Intent │ ▲ State/Effect                  │
│                         ▼ │                                 │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                    ViewModel                         │   │
│  │  • Processes Intent via handleIntent()               │   │
│  │  • Updates State via setState { copy(...) }          │   │
│  │  • Emits Effect via sendEffect()                     │   │
│  │  • Calls Use Cases for business logic                │   │
│  └──────────────────────┬──────────────────────────────┘   │
└─────────────────────────│───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                      Domain Layer                           │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                    Use Cases                         │   │
│  │  • Single responsibility business operations         │   │
│  │  • Returns Result<T> (Success/Error/Loading)         │   │
│  └──────────────────────┬──────────────────────────────┘   │
└─────────────────────────│───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                       Data Layer                            │
│  ┌─────────────────┐  ┌─────────────────┐  ┌────────────┐  │
│  │   Repository    │  │    Database     │  │    API     │  │
│  │  (Interfaces)   │  │     (Room)      │  │  (Stubbed) │  │
│  └─────────────────┘  └─────────────────┘  └────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### MVI Contract Pattern

Each feature defines a contract with three sealed components:

```kotlin
object FeatureContract {
    data class State(                    // Immutable UI state
        val isLoading: Boolean = false,
        val data: List<Item> = emptyList(),
        val error: String? = null
    )

    sealed class Intent {                // User actions
        object LoadData : Intent()
        data class SelectItem(val id: String) : Intent()
    }

    sealed class Effect {                // One-time side effects
        data class ShowToast(val message: String) : Effect()
        object NavigateBack : Effect()
    }
}
```

### Module Structure

```
LotteryGenerator/
├── app/                          # Application entry point
│   ├── di/                       # Koin module aggregation
│   ├── navigation/               # NavHost setup
│   └── ui/                       # MainActivity, MainScreen
│
├── core/                         # Shared modules
│   ├── core-common/              # BaseViewModel, Result, DispatcherProvider
│   ├── core-data/                # Repository implementations, API, DTOs
│   ├── core-database/            # Room database, DAOs, entities
│   ├── core-domain/              # Use cases, domain models, repository interfaces
│   ├── core-navigation/          # Route definitions, navigation items
│   └── core-ui/                  # Theme, components, animations, strings
│
└── feature/                      # Feature modules
    ├── feature-generator/        # Number generation screen
    ├── feature-history/          # History list & detail screens
    └── feature-settings/         # Settings & custom lottery types
```

### Module Dependencies

```
                              ┌─────────┐
                              │   app   │
                              └────┬────┘
                                   │
           ┌───────────────────────┼───────────────────────┐
           │                       │                       │
           ▼                       ▼                       ▼
  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
  │feature-generator│    │ feature-history │    │feature-settings │
  └────────┬────────┘    └────────┬────────┘    └────────┬────────┘
           │                       │                       │
           └───────────────────────┼───────────────────────┘
                                   │
                                   ▼
  ┌────────────────────────────────────────────────────────────────┐
  │                        Core Modules                            │
  │                                                                │
  │  ┌─────────────┐    ┌─────────────┐    ┌───────────────────┐  │
  │  │ core-domain │◄───│  core-data  │───►│  core-database    │  │
  │  └─────────────┘    └─────────────┘    └───────────────────┘  │
  │                                                                │
  │  ┌─────────────┐    ┌─────────────┐    ┌───────────────────┐  │
  │  │ core-common │    │   core-ui   │    │  core-navigation  │  │
  │  └─────────────┘    └─────────────┘    └───────────────────┘  │
  └────────────────────────────────────────────────────────────────┘

  Legend:
  • app           → All feature modules, core-navigation
  • feature-*     → core-domain, core-ui, core-common, core-navigation
  • core-data     → core-domain, core-database
  • core-domain   → core-common
```

### Repository Interface Segregation

```kotlin
// Focused interfaces following Interface Segregation Principle
interface NumberGeneratorRepository {
    suspend fun generateNumbers(lotteryType: LotteryType): GeneratedNumbers
}

interface LotteryConfigRepository {
    suspend fun getLotteryTypes(): List<LotteryType>
}

interface HistoryRepository {
    fun getHistory(): Flow<List<GeneratedNumbers>>
    fun getHistoryById(id: String): GeneratedNumbers?
    suspend fun deleteHistory(id: String)
    suspend fun clearAllHistory()
    fun getHistoryPaged(limit: Int, offset: Int): Flow<List<GeneratedNumbers>>
}

// Combined interface for convenience
interface LotteryRepository :
    NumberGeneratorRepository,
    LotteryConfigRepository,
    HistoryRepository
```

---

## Tech Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Language** | Kotlin | 2.0.21 |
| **UI Framework** | Jetpack Compose | BOM 2024.12.01 |
| **Design System** | Material3 | Latest |
| **Navigation** | Compose Navigation | 2.8.5 |
| **DI Framework** | Koin | 4.0.1 |
| **Database** | Room | 2.6.1 |
| **Preferences** | DataStore | 1.1.1 |
| **Async** | Coroutines | 1.9.0 |
| **Serialization** | Kotlinx Serialization | 1.7.3 |
| **Build Tool** | Gradle (Kotlin DSL) | 8.13 |
| **Android Plugin** | AGP | 8.11.1 |

### Testing Stack

| Library | Purpose |
|---------|---------|
| JUnit 4 | Test framework |
| Turbine 1.2.0 | Flow testing |
| MockK 1.13.13 | Mocking |
| Coroutines Test | Coroutine testing |
| Espresso 3.6.1 | UI testing |

---

## Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2.1) or newer
- JDK 11 or higher
- Android SDK 36
- Kotlin 2.0.21

### Build & Run

```bash
# Clone the repository
git clone https://github.com/yourusername/LotteryGenerator.git
cd LotteryGenerator

# Build debug APK
./gradlew assembleDebug

# Build and install on connected device/emulator
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

# Check dependencies for updates
./gradlew dependencyUpdates
```

---

## Testing

### Testing Philosophy

All tests follow the **Given-When-Then** (Arrange-Act-Assert) pattern:

```kotlin
@Test
fun `should generate numbers when generate intent is processed`() = runTest {
    // Given - Setup preconditions
    val lotteryType = LotteryType.POWERBALL
    coEvery { generateNumbersUseCase(lotteryType) } returns Result.Success(mockNumbers)

    // When - Execute the action being tested
    viewModel.processIntent(GeneratorIntent.GenerateNumbers)
    advanceUntilIdle()

    // Then - Assert expected outcomes
    viewModel.state.test {
        val state = awaitItem()
        assertEquals(mockNumbers, state.generatedNumbers)
        assertFalse(state.isLoading)
        cancelAndIgnoreRemainingEvents()
    }
}
```

### Test Coverage

| Module | Test File | Coverage |
|--------|-----------|----------|
| core-data | `StubbedLotteryApiTest` | Number generation, ranges, uniqueness, sorting |
| core-data | `DataMapperTest` | DTO/Entity conversion, safe parsing |
| core-data | `LotteryRepositoryTest` | Repository operations, pagination |
| core-domain | `GenerateNumbersUseCaseTest` | Use case business logic |
| feature-generator | `GeneratorViewModelTest` | 50+ test cases for all intents |
| feature-history | `HistoryListViewModelTest` | List operations, filtering |
| feature-settings | `SettingsViewModelTest` | Settings state management |

### Test Utilities

- `MainDispatcherRule` - JUnit rule for test dispatchers
- `TestDispatcherProvider` - Provides test dispatchers
- `FakeLotteryApi` - Controllable API for testing
- `FakeHistoryDao` - In-memory DAO for testing
- `FakeLotteryRepository` - Mock repository implementation

---

## Project Structure Details

### Key Files

| File | Purpose |
|------|---------|
| `core-common/.../BaseViewModel.kt` | Abstract MVI ViewModel base class |
| `core-common/.../Result.kt` | Result sealed class (Success/Error/Loading) |
| `core-data/.../StubbedLotteryApi.kt` | Simulated API with 800ms delay |
| `core-data/.../DataMapper.kt` | Centralized DTO/Entity mapping |
| `core-database/.../LottoDatabase.kt` | Room database configuration |
| `core-ui/.../theme/` | Material3 theme with sky blue primary |
| `core-ui/.../animation/ConfettiAnimation.kt` | Physics-based particle animation |
| `app/.../di/AppModule.kt` | Koin module aggregation |
| `app/.../navigation/LottoNavHost.kt` | Navigation graph |

### Navigation Routes

| Route | Screen |
|-------|--------|
| `generator` | Number generation (start destination) |
| `history` | History list |
| `history_detail/{entryId}` | History detail view |
| `settings` | Settings screen |
| `custom_lottery_types` | Custom type management |
| `add_lottery_type` | Create custom type |
| `edit_lottery_type/{typeId}` | Edit custom type |

---

## Dependency Management

Dependencies are centralized in `gradle/libs.versions.toml`:

```toml
[versions]
kotlin = "2.0.21"
compose-bom = "2024.12.01"
koin-bom = "4.0.1"
room = "2.6.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
# ... more libraries
```

To add a new dependency:
1. Add version to `[versions]` section
2. Add library to `[libraries]` section
3. Reference in `build.gradle.kts` using `libs.xxx` syntax

---

## Code Style

- **Kotlin Conventions**: Follow official Kotlin coding conventions
- **MVI Pattern**: Use Contract, ViewModel, Screen structure for features
- **Sealed Classes**: Use for Intent and Effect types
- **Data Classes**: Use for State (immutable)
- **Stateless UI**: Keep all state in ViewModel
- **String Resources**: Use `stringResource()` for all UI text
- **AndroidX Only**: No legacy Android Support libraries

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Commit Guidelines

- Use clear, descriptive commit messages
- Reference issue numbers when applicable
- Keep commits focused and atomic

---

## License

```
MIT License

Copyright (c) 2025 LotteryGenerator

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## Disclaimer

This application is for entertainment purposes only. Generated numbers are random and do not guarantee any lottery winnings. Please gamble responsibly and in accordance with your local laws and regulations.

---

## Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern Android UI toolkit
- [Koin](https://insert-koin.io/) - Pragmatic dependency injection
- [Room](https://developer.android.com/training/data-storage/room) - SQLite abstraction
- [Material3](https://m3.material.io/) - Design system
