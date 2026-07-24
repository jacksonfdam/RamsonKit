# Repository Guidelines

## Project Structure & Module Organization

RansomKit is a Kotlin/Jetpack Compose Android project targeting API 34 (minimum API 26). `common/` is an Android library for shared models under `common/src/main/java/com/demo/ransomkit/common/`. The two application modules are `defender/` (Defender Vault) and `attacker_awareness/` (permission-awareness demo); each keeps Kotlin sources in `src/main/java/` and resources/manifests in `src/main/res/` and `src/main/AndroidManifest.xml`. Keep cross-app models in `common`, not duplicated between apps. Security background belongs in `docs/`; seminar material belongs in `presentation/`.

## Build, Test, and Development Commands

Use JDK 17 and Android Studio Jellyfish (or Gradle 8.2+ with an installed Android SDK).

```bash
gradle build                         # compile and run all configured checks
gradle :defender:assembleDebug       # build the Defender debug APK
gradle :attacker_awareness:assembleDebug
gradle :common:build                 # build the shared library
gradle test                          # run local unit tests when present
gradle connectedAndroidTest          # run device/emulator instrumentation tests
```

There is currently no Gradle wrapper committed; use the system `gradle` command or import the root `settings.gradle.kts` into Android Studio. Prefer module-scoped commands while iterating.

## Coding Style & Naming Conventions

Write idiomatic Kotlin with four-space indentation, `PascalCase` classes/composables, `camelCase` functions and properties, and `UPPER_SNAKE_CASE` constants. Keep package names under `com.demo.ransomkit`. Compose screens use descriptive `*Screen` names; small reusable UI elements use purpose-based names such as `DefenseSwitchRow`. Follow the existing import ordering and let Android Studio format Kotlin before committing. Dependencies and versions are centralized in `gradle/libs.versions.toml`.

## Testing Guidelines

Place JVM tests in `<module>/src/test/` and instrumentation or Compose UI tests in `<module>/src/androidTest/`. Name test files after the subject (for example, `SecurityStateTest.kt`) and test methods for behavior, such as `toggleFileObserver_startsWatching()`. Add tests for new shared models and security-control behavior; manually verify UI changes on an API 26+ emulator when automated coverage is unavailable.

## Commit & Pull Request Guidelines

The repository has no committed history yet, so no established commit convention exists. Use concise, imperative messages with a module prefix, e.g. `defender: add secure-preferences status`. Keep each commit focused. Pull requests should explain the user-visible/security impact, list test commands and emulator checks performed, link related issues, and include screenshots for Compose UI changes. Never introduce real encryption, exfiltration, screen-locking, or access outside the app-private sandbox; this repository is strictly an educational defensive demonstration.
