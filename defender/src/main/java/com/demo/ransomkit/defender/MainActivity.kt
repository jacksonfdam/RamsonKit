package com.demo.ransomkit.defender

import android.os.Build
import android.os.Bundle
import android.os.FileObserver
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.demo.ransomkit.common.model.AuditLogEntry
import com.demo.ransomkit.common.model.SandboxedFileItem
import java.io.File

class MainActivity : ComponentActivity() {

    private var fileObserver: FileObserver? = null
    private val auditLogs = mutableStateListOf<AuditLogEntry>()
    private val sandboxedFiles = mutableStateListOf<SandboxedFileItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val demoDir = File(filesDir, "RansomKitDemo")
        if (!demoDir.exists()) {
            demoDir.mkdirs()
            initSampleSandboxedFiles(demoDir)
        }

        refreshFileList(demoDir)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DefenderDashboardScreen(
                        sandboxedFiles = sandboxedFiles,
                        auditLogs = auditLogs,
                        onToggleFlagSecure = { enabled -> toggleFlagSecure(enabled) },
                        onToggleEncryptedPrefs = { enabled -> toggleEncryptedPrefs(enabled) },
                        onToggleFileObserver = { enabled -> toggleFileObserver(demoDir, enabled) },
                        onCreateSampleFile = {
                            val newFile = File(demoDir, "note_${System.currentTimeMillis()}.txt")
                            newFile.writeText("Sample sandboxed note content.")
                            refreshFileList(demoDir)
                        }
                    )
                }
            }
        }
    }

    private fun initSampleSandboxedFiles(dir: File) {
        for (i in 1..5) {
            File(dir, "sample_photo_$i.jpg").writeText("Mock Image Data $i")
            File(dir, "personal_note_$i.txt").writeText("Protected personal note content $i")
        }
    }

    private fun refreshFileList(dir: File) {
        sandboxedFiles.clear()
        dir.listFiles()?.forEach { file ->
            sandboxedFiles.add(
                SandboxedFileItem(
                    name = file.name,
                    path = file.absolutePath,
                    sizeBytes = file.length(),
                    isIntact = true,
                    lastModified = file.lastModified()
                )
            )
        }
    }

    private fun toggleFlagSecure(enabled: Boolean) {
        if (enabled) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
            logEvent("FLAG_SECURE", "Enabled window protection against screen capture.", true)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            logEvent("FLAG_SECURE", "Disabled window protection.", false)
        }
    }

    private fun toggleEncryptedPrefs(enabled: Boolean) {
        try {
            val masterKey = MasterKey.Builder(this)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val prefs = EncryptedSharedPreferences.create(
                this,
                "secure_vault_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SKEY_KEY_GEN,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            prefs.edit().putBoolean("vault_encrypted_mode", enabled).apply()
            logEvent("EncryptedSharedPreferences", "Preferences saved with AES-256 GCM encryption: $enabled", true)
        } catch (e: Exception) {
            logEvent("EncryptedSharedPreferences", "Error configuring secure preferences: ${e.message}", false)
        }
    }

    private fun toggleFileObserver(dir: File, enabled: Boolean) {
        if (enabled) {
            val mask = FileObserver.CREATE or FileObserver.MODIFY or FileObserver.DELETE
            fileObserver = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                object : FileObserver(dir, mask) {
                    override fun onEvent(event: Int, path: String?) {
                        path?.let { logEvent("FileObserver", "Detected file system event ($event) on: $it", true) }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                object : FileObserver(dir.absolutePath, mask) {
                    override fun onEvent(event: Int, path: String?) {
                        path?.let { logEvent("FileObserver", "Detected file system event ($event) on: $it", true) }
                    }
                }
            }
            fileObserver?.startWatching()
            logEvent("FileObserver", "Started watching sandbox dir: ${dir.name}", true)
        } else {
            fileObserver?.stopWatching()
            fileObserver = null
            logEvent("FileObserver", "Stopped file watcher.", false)
        }
    }

    private fun logEvent(type: String, details: String, isProtected: Boolean) {
        auditLogs.add(0, AuditLogEntry(eventType = type, details = details, isProtected = isProtected))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefenderDashboardScreen(
    sandboxedFiles: List<SandboxedFileItem>,
    auditLogs: List<AuditLogEntry>,
    onToggleFlagSecure: (Boolean) -> Unit,
    onToggleEncryptedPrefs: (Boolean) -> Unit,
    onToggleFileObserver: (Boolean) -> Unit,
    onCreateSampleFile: () -> Unit
) {
    var flagSecureActive by remember { mutableStateOf(false) }
    var encryptedPrefsActive by remember { mutableStateOf(false) }
    var fileObserverActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Defender Vault (Android 13+ Controls)") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = "Defensive Security Controls", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Defense Switches
            DefenseSwitchRow("Enable FLAG_SECURE", flagSecureActive) {
                flagSecureActive = it
                onToggleFlagSecure(it)
            }

            DefenseSwitchRow("Enable EncryptedSharedPreferences", encryptedPrefsActive) {
                encryptedPrefsActive = it
                onToggleEncryptedPrefs(it)
            }

            DefenseSwitchRow("Enable FileObserver Sandbox Monitor", fileObserverActive) {
                fileObserverActive = it
                onToggleFileObserver(it)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "App-Private Sandbox Files (${sandboxedFiles.size})", fontWeight = FontWeight.Bold)
                Button(onClick = onCreateSampleFile) {
                    Text("Add File")
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    items(sandboxedFiles) { file ->
                        Text(
                            text = "📄 ${file.name} (${file.sizeBytes} B)",
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Real-time Defense Audit Trail", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseSurface)
            ) {
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    items(auditLogs) { log ->
                        Text(
                            text = "[${log.eventType}] ${log.details}",
                            fontSize = 11.sp,
                            color = if (log.isProtected) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DefenseSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
