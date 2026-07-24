# RansomKit - Safe Android 13+ Security & Defense Demonstration Project

RansomKit is an educational, multi-module Android project designed for security research, threat modeling, and defensive engineering seminars. It demonstrates permission hygiene, Scoped Storage isolation, and runtime defense controls on modern Android devices (Android 13+ / API 33+).

---

## ⚠️ Important Safety Notice

This project contains **NO malicious code, NO real file encryption algorithms, NO background screen-locking receivers, and NO remote data exfiltration mechanisms**. 

All visual alerts are in-memory UI toggles, and all file operations are strictly confined to the application's isolated private sandbox (`context.filesDir`).

---

## Repository Structure

- **`docs/`**: Comprehensive security documentation.
  - [`THREAT_MODEL.md`](./docs/THREAT_MODEL.md): STRIDE threat classification & permission risk analysis.
  - [`DEFENSE_GUIDE.md`](./docs/DEFENSE_GUIDE.md): Implementation guide for Android defensive APIs (`FLAG_SECURE`, `EncryptedSharedPreferences`, `FileObserver`).
- **`presentation/`**: Seminar presentation material.
  - [`SLIDES.md`](./presentation/SLIDES.md): 20-slide markdown presentation on Android zero-trust architecture.
- **`common/`**: Shared data models (`PermissionExplanation`, `AuditLogEntry`, `SandboxedFileItem`).
- **`attacker_awareness/`**: Educational app demonstrating permission transparency and threat modeling UI analysis ("QuickBoost Demo").
- **`defender/`**: Target application with interactive, toggleable security controls ("Defender Vault").

---

## Defensive Technologies Demonstrated

1. **Window Protection (`FLAG_SECURE`)**: Prevents screen capture, recording, and Recent Apps task switcher leakage.
2. **Key-Value Encryption (`EncryptedSharedPreferences`)**: Authenticated AES-256 GCM encryption backed by hardware Keymaster / Android Keystore.
3. **App-Private Sandboxing (`context.filesDir`)**: Linux UID-level file system isolation enforcing zero-trust access boundaries.
4. **Sandboxed File Integrity Monitoring (`FileObserver`)**: Kernel-level `inotify` file watcher detecting real-time file creation, modification, and deletion events.

---

## Prerequisites & Building

- Android Studio Quail 2 (2026.1.2 Patch 1) or later
- JDK 17
- Android SDK Platform 37 and Build Tools 36.0.0+
- Gradle Wrapper 9.6.1 and Android Gradle Plugin 9.2.0
- Target SDK: 37 (Android 17); minimum SDK: 26 (Android 8.0)

To build all modules via Gradle CLI:
```bash
./gradlew build
```

## Current Toolchain & Libraries

Version pins are managed in `gradle/libs.versions.toml`. The project uses stable releases of AndroidX Core 1.19.0, Lifecycle 2.11.0, Activity Compose 1.13.0, Security Crypto 1.1.0, and the Compose BOM 2026.06.00. AGP 9 provides built-in Kotlin support, so the project no longer applies the legacy Kotlin Android plugin; the Kotlin Compose plugin 2.3.21 supplies the Compose compiler.
