# Repository Guidelines

## Project Structure & Module Organization

RansomKit is a Kotlin/Jetpack Compose Android project targeting API 37 (minimum API 26). `common/` is an Android library for shared models under `common/src/main/java/com/demo/ransomkit/common/`. The two application modules are `defender/` (Defender Vault) and `attacker_awareness/` (permission-awareness demo); each keeps Kotlin sources in `src/main/java/` and resources/manifests in `src/main/res/` and `src/main/AndroidManifest.xml`. Keep cross-app models in `common`, not duplicated between apps. Security background belongs in `docs/`; seminar material belongs in `presentation/`.

## Build, Test, and Development Commands

Use JDK 17, Android Studio Quail 2 (2026.1.2 Patch 1) or later, and Android SDK Platform 37 with Build Tools 36.0.0+.

```bash
./gradlew build                         # compile and run all configured checks
./gradlew :defender:assembleDebug       # build the Defender debug APK
./gradlew :attacker_awareness:assembleDebug
./gradlew :common:build                 # build the shared library
./gradlew test                          # run local unit tests when present
./gradlew connectedAndroidTest          # run device/emulator instrumentation tests
```

Use the committed Gradle Wrapper (9.6.1) rather than a system Gradle installation; import the root `settings.gradle.kts` into Android Studio. Prefer module-scoped commands while iterating.

## Coding Style & Naming Conventions

Write idiomatic Kotlin with four-space indentation, `PascalCase` classes/composables, `camelCase` functions and properties, and `UPPER_SNAKE_CASE` constants. Keep package names under `com.demo.ransomkit`. Compose screens use descriptive `*Screen` names; small reusable UI elements use purpose-based names such as `DefenseSwitchRow`. Follow the existing import ordering and let Android Studio format Kotlin before committing. Dependencies and versions are centralized in `gradle/libs.versions.toml`: AGP 9.2.0, Kotlin Compose 2.3.21, Core 1.19.0, Lifecycle 2.11.0, Activity Compose 1.13.0, Security Crypto 1.1.0, and Compose BOM 2026.06.00. AGP 9 provides Kotlin support; do not reapply the legacy Kotlin Android plugin.

## Testing Guidelines

Place JVM tests in `<module>/src/test/` and instrumentation or Compose UI tests in `<module>/src/androidTest/`. Name test files after the subject (for example, `SecurityStateTest.kt`) and test methods for behavior, such as `toggleFileObserver_startsWatching()`. Add tests for new shared models and security-control behavior; manually verify UI changes on an API 26+ emulator when automated coverage is unavailable.

## Commit & Pull Request Guidelines

Use concise, imperative Conventional Commit-style messages with a scope where useful, e.g. `defender: add secure-preferences status` or `build: update Android toolchain`. Keep each commit focused. Pull requests should explain the user-visible/security impact, list test commands and emulator checks performed, link related issues, and include screenshots for Compose UI changes. Never introduce real encryption, exfiltration, screen-locking, or access outside the app-private sandbox; this repository is strictly an educational defensive demonstration.
