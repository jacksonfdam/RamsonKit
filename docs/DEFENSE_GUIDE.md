# Defensive Engineering Guide: Android 13+ Security Mechanisms

This guide details key defensive security controls for protecting Android applications and user data against unauthorized access and integrity threats.

---

## 1. Window Protection with `FLAG_SECURE`

To prevent screen capture, screen recording, and unauthorized preview screenshots in the Recent Apps switcher:

```kotlin
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity

class SecureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Prevent screen capture and recent apps preview leakage
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }
}
```

---

## 2. Secure Configuration & Credential Storage (`EncryptedSharedPreferences`)

Use Google's AndroidX Security library to encrypt key-value pairs at rest using AES-256 GCM backed by the Android Keystore.

```kotlin
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

fun getSecurePreferences(context: Context) {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_settings",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SKEY_KEY_GEN,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Write securely
    sharedPreferences.edit().putBoolean("is_defense_active", true).apply()
}
```

---

## 3. Sandboxed App Isolation (`context.filesDir`)

Always store sensitive user data, notes, and photos inside the application sandbox (`context.filesDir`) rather than public external storage (`/storage/emulated/0/`). Linux UID isolation guarantees that no other application can access these files without root privilege.

---

## 4. Real-time App-Private Integrity Monitoring (`FileObserver`)

Monitor the application's internal directory for unexpected file additions, modifications, or deletions:

```kotlin
import android.os.Build
import android.os.FileObserver
import java.io.File

class SandboxIntegrityObserver(
    private val sandboxDir: File,
    private val onEventDetected: (String, Int) -> Unit
) {
    private var observer: FileObserver? = null

    fun startMonitoring() {
        val mask = FileObserver.CREATE or FileObserver.MODIFY or FileObserver.DELETE
        
        observer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            object : FileObserver(sandboxDir, mask) {
                override fun onEvent(event: Int, path: String?) {
                    path?.let { onEventDetected(it, event) }
                }
            }
        } else {
            @Suppress("DEPRECATION")
            object : FileObserver(sandboxDir.absolutePath, mask) {
                override fun onEvent(event: Int, path: String?) {
                    path?.let { onEventDetected(it, event) }
                }
            }
        }
        
        observer?.startWatching()
    }

    fun stopMonitoring() {
        observer?.stopWatching()
    }
}
```
