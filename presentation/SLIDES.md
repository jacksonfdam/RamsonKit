# Presentation: Mobile Threat Modeling & Android 13+ Defense Architecture

---

## Slide 1: Title & Overview
- **Title:** Educational Security Demo: Threat Modeling & Android 13+ Defenses
- **Subtitle:** Analyzing Permission Hygeine, Scoped Storage, and Defense-in-Depth Architecture
- **Audience:** Academic / Security Engineering Seminar

---

## Slide 2: Executive Summary
- Understanding social engineering attack vectors (e.g., utility disguise).
- Evaluating permission risk profiles on modern Android platforms.
- Demonstrating zero-trust application isolation and active runtime defenses.

---

## Slide 3: The Social Engineering Threat Vector
- Utility apps ("QuickBoost", "Battery Saver") requesting excessive permissions.
- **Cognitive Trap:** Coupling legitimate-sounding explanations to invasive permission grants.
- User permission fatigue leading to blind authorization.

---

## Slide 4: Android Permission Evolution (API 21 -> API 33+)
- **Legacy Era (Android < 6):** Install-time all-or-nothing permissions.
- **Dynamic Era (Android 6-12):** Runtime permission prompts (`ACCESS_FINE_LOCATION`).
- **Modern Era (Android 13+):** Granular media permissions, explicit notification prompts (`POST_NOTIFICATIONS`), Scoped Storage enforcement.

---

## Slide 5: Permission Deep Dive: Location & Identity
- `ACCESS_FINE_LOCATION`: Exposes user geolocation history.
- Hardware Identifiers (IMEI, Serial): Blocked for non-system apps in modern SDKs (`READ_PHONE_STATE` restrictions).
- **Defense Principle:** Data Minimization.

---

## Slide 6: Permission Deep Dive: Storage Access
- Legacy `READ_EXTERNAL_STORAGE` exposed shared user media.
- **Android 13 Solution:** Photo Picker API eliminates app read access to unselected media files.
- `READ_MEDIA_IMAGES` / `READ_MEDIA_VIDEO` isolate media domains.

---

## Slide 7: STRIDE Threat Modeling for Mobile Apps
- **S**poofing: Package identity protection.
- **T**ampering: File integrity monitoring.
- **R**epudiation: Local audit trails.
- **I**nformation Disclosure: App sandboxing & encryption at rest.
- **D**enial of Service: System Overlay restrictions.
- **E**levation of Privilege: Strict runtime scope boundaries.

---

## Slide 8: Android System Sandboxing
- Linux UID isolation: Each Android app runs under a distinct Linux UID.
- Public Storage (`/storage/emulated/0`) vs. App Private Storage (`/data/data/<pkg>/files`).
- **Best Practice:** Keep all user data inside the app-private sandbox.

---

## Slide 9: Scoped Storage Deep Dive
- Android 10+ introduced **Scoped Storage**.
- Direct raw path access (`/storage/emulated/0/...`) restricted.
- Apps interact via System Selectors or `StorageAccessFramework`.

---

## Slide 10: Defensive Layer 1 - Screen Security (`FLAG_SECURE`)
- Prevents screen captures by third-party background applications.
- Blocks window preview leaks in the OS Recent Apps / Multitasking switcher.
- Essential for sensitive banking, note-taking, or photo vault applications.

---

## Slide 11: Defensive Layer 2 - Encryption at Rest
- `EncryptedSharedPreferences` backed by Android Keystore.
- AES-256 GCM authenticated encryption scheme.
- Cryptographic keys stored inside hardware-backed Keymaster / TEE (Trusted Execution Environment).

---

## Slide 12: Defensive Layer 3 - File Integrity Monitoring
- Utilizing Linux `FileObserver` (inotify kernel API).
- Real-time callbacks on `CREATE`, `MODIFY`, `DELETE` events within app-private directories.
- Immediate detection of unauthorized file access or corruption.

---

## Slide 13: Educational Awareness UI Architecture
- Purely in-memory state models (`StateFlow` / Jetpack Compose `remember`).
- Transparency screens explaining *why* permissions are requested.
- Interactive permission auditing card flow.

---

## Slide 14: Defense App Architecture ("Target Demo")
- Sandboxed file store (`context.filesDir/RansomKitDemo`).
- Isolated UI with toggleable defense switches.
- Live audit log viewer showing file access and defense status.

---

## Slide 15: Demonstrating Defense Mechanics
- **Scenario A:** Defense OFF vs. Scenario B: Defense ON.
- Toggling `FLAG_SECURE`: Observing Recent Apps blur/blackout behavior.
- Toggling `FileObserver`: Observing real-time change logs on file creation.

---

## Slide 16: Zero-Trust Mobile Design Principles
- Never trust external storage for application state.
- Assume runtime permissions can be audited or revoked at any time.
- Implement Defense-in-Depth rather than relying solely on OS controls.

---

## Slide 17: Incident Preparedness & Auditing
- Logging security-relevant events locally for user inspection.
- Exporting audit reports without sending data to remote endpoints.
- User visibility into app permission usage via Android Privacy Dashboard.

---

## Slide 18: Regulatory & Compliance Alignment
- GDPR / LGPD: Data minimization and explicit consent.
- OWASP MASVS (Mobile Application Security Verification Standard).
- MASVS-STORAGE: Ensuring secure data storage at rest.

---

## Slide 19: Future Android Platform Defenses
- Privacy Sandbox on Android (limiting cross-app tracking).
- Advanced API level enforcement for background execution.
- Automated Play Protect behavioral scanning.

---

## Slide 20: Summary & Key Takeaways
1. Design apps using app-private sandbox storage.
2. Use `FLAG_SECURE` for sensitive screens.
3. Encrypt sensitive preferences with Jetpack Security.
4. Educate users on permission hygiene and transparency.
