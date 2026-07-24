# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this project is

RansomKit is an educational Android project for security seminars: it demonstrates *defensive* Android APIs and permission-risk awareness. `AGENTS.md` documents the coding conventions and PR expectations — read it too; this file covers architecture and the current (verified) build reality.

**Hard constraint from `AGENTS.md`, which overrides feature requests:** never add real encryption of user data, exfiltration, screen-locking/overlay persistence, or any file access outside `context.filesDir`. All "attack" surfaces are in-memory UI mockups. If asked to make the demo "more realistic," implement it as UI/logging only.

## Build

**JDK 17 is mandatory, not a suggestion.** The project uses Gradle 9.6.1 and Android Gradle Plugin 9.2.0, whose supported JDK is 17. Kotlin support is built into AGP 9; Compose uses the Kotlin Compose plugin 2.3.21.

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./gradlew :common:assembleDebug              # shared library — currently builds clean
./gradlew :defender:assembleDebug            # Defender Vault APK
./gradlew :attacker_awareness:assembleDebug  # QuickBoost awareness APK
```

A Gradle wrapper *is* committed now (`AGENTS.md` still claims otherwise); prefer `./gradlew` over a system `gradle`.

## Two known build blockers

Both app modules fail to assemble as of the initial commit. Fix these before trusting any "does it build" signal:

1. `defender/src/main/java/com/demo/ransomkit/defender/MainActivity.kt:107` uses `EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SKEY_KEY_GEN`, which does not exist in `androidx.security:security-crypto`. The real constant is `AES256_SIV`. The same wrong constant is copy-pasted into `docs/DEFENSE_GUIDE.md:49` — fix both, since the doc is the source the code was written from.
2. Both `AndroidManifest.xml` files reference `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`, but neither module has any `res/mipmap*` directory — resource linking fails. Either add launcher icons or drop the `android:icon`/`android:roundIcon` attributes.

## Testing

There are no test source sets at all — no `src/test/`, no `src/androidTest/`, and no test dependencies in any `build.gradle.kts` (the `testInstrumentationRunner` is declared but `androidx.test.*` artifacts are not). `./gradlew test` and `connectedAndroidTest` are no-ops today; adding a test also means adding the dependencies. `AGENTS.md` lists those commands aspirationally.

## Architecture

Three Gradle modules, all `compileSdk = 37` / `minSdk = 26` (and API 37 `targetSdk` for app modules), all under the `com.demo.ransomkit` package root. There is no DI, no ViewModel layer, no navigation library, and no repository layer — each app is a single `MainActivity` that owns its state directly. Keep it that way unless asked otherwise; the code is meant to be readable on a projector.

- **`common/`** (`com.demo.ransomkit.common`) — an Android *library* holding only data classes in `model/SecurityState.kt`: `PermissionExplanation`, `AuditLogEntry`, `SandboxedFileItem`. No Compose dependency. Any model shared by both apps belongs here rather than duplicated.
- **`defender/`** (`com.demo.ransomkit.defender`, "Defender Vault") — the module that actually exercises Android security APIs. `MainActivity` holds `fileObserver`, `auditLogs`, and `sandboxedFiles` as activity fields (`mutableStateListOf`) and passes them plus `onToggle*` lambdas down into the stateless `DefenderDashboardScreen`. The three toggles are the demo's substance: `FLAG_SECURE` via `window.setFlags`, `EncryptedSharedPreferences` with an AES-256-GCM `MasterKey`, and a `FileObserver` over `filesDir/RansomKitDemo`. Every toggle calls `logEvent(...)`, which prepends an `AuditLogEntry` — that audit trail is the visible teaching output, so new controls should log through it too. Sample files are seeded once in `onCreate` only if the demo dir does not exist.
- **`attacker_awareness/`** (`com.demo.ransomkit.awareness`, "QuickBoost") — purely declarative. A hardcoded `List<PermissionExplanation>` inside `remember` renders as `PermissionCard`s, plus one boolean that reveals a labeled `[MOCK UI SIMULATION]` card. It requests **no permissions** in its manifest; it only *describes* risky permissions. Do not add real permission requests to make the demo "authentic."

Note the toggle state pattern in `defender`: `DefenderDashboardScreen` keeps its own `remember { mutableStateOf(false) }` per switch while the activity performs the side effect. This means toggles reset on configuration change while the underlying window flag or watcher persists. If you touch the switches, that desync is the thing to fix.

## Docs are part of the deliverable

`docs/THREAT_MODEL.md` (STRIDE table, per-permission Android 13+ mitigations), `docs/DEFENSE_GUIDE.md` (copy-pasteable Kotlin for each control), and `presentation/SLIDES.md` are seminar material, not incidental README padding. When you change a security control in `defender/`, update the matching snippet in `DEFENSE_GUIDE.md` — the two are meant to stay in lockstep, and today they share a bug because of it.

## Repo hygiene gotchas

There is no `.gitignore`, so `build/`, `.gradle/`, and `gradlew`/`gradlew.bat` show as untracked noise. `local.properties` and `.gradle/config.properties` are committed and machine-specific — don't rely on their values, and avoid re-committing local churn in them.
