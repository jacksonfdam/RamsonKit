package com.demo.ransomkit.awareness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.ransomkit.common.model.PermissionExplanation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AwarenessAppScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AwarenessAppScreen() {
    var isSimulatedAlertVisible by remember { mutableStateOf(false) }

    val samplePermissions = remember {
        listOf(
            PermissionExplanation(
                title = "Internet Access",
                permissionName = "INTERNET",
                plausibleReason = "Cloud sync & battery optimization updates",
                securityRiskExplanation = "Allows exfiltration of collected data to remote C2 servers.",
                androidMitigation = "Standard permission, but monitored via Network Security Config & Privacy Dashboard."
            ),
            PermissionExplanation(
                title = "Fine Location",
                permissionName = "ACCESS_FINE_LOCATION",
                plausibleReason = "Locate nearby optimization servers",
                securityRiskExplanation = "Exposes user physical whereabouts and movement patterns.",
                androidMitigation = "Android 13+ enforces coarse location options and 'Only while in app' prompts."
            ),
            PermissionExplanation(
                title = "Post Notifications",
                permissionName = "POST_NOTIFICATIONS",
                plausibleReason = "Receive status alerts on RAM cleanup",
                securityRiskExplanation = "Used to display urgent deceptive messages or social engineering prompts.",
                androidMitigation = "Android 13+ requires explicit runtime grant for notifications."
            ),
            PermissionExplanation(
                title = "Usage Stats Access",
                permissionName = "PACKAGE_USAGE_STATS",
                plausibleReason = "Detect battery-draining background apps",
                securityRiskExplanation = "Monitors user behavior and target app launching frequency.",
                androidMitigation = "Requires user manual navigation to Special App Access settings."
            ),
            PermissionExplanation(
                title = "Receive Boot Completed",
                permissionName = "RECEIVE_BOOT_COMPLETED",
                plausibleReason = "Start background monitor on startup",
                securityRiskExplanation = "Ensures persistence across device reboots.",
                androidMitigation = "Android limits background execution start from boot receivers."
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security Awareness: Permission Risks") },
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
            Text(
                text = "Educational Threat Modeling Demo",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "This screen demonstrates how deceptive apps present legitimate-sounding justifications for high-risk permissions.",
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Button(
                onClick = { isSimulatedAlertVisible = !isSimulatedAlertVisible },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSimulatedAlertVisible) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(if (isSimulatedAlertVisible) "Hide Simulation Alert UI" else "Toggle Mock Threat UI Alert")
            }

            if (isSimulatedAlertVisible) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "[MOCK UI SIMULATION] Deceptive Screen Mockup",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "This is a non-destructive visual placeholder demonstrating how threat modeling analyzes UI overlays. No files are accessed or encrypted.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(samplePermissions) { item ->
                    PermissionCard(item)
                }
            }
        }
    }
}

@Composable
fun PermissionCard(item: PermissionExplanation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                SuggestionChip(
                    onClick = { },
                    label = { Text(item.permissionName, fontSize = 10.sp) }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Plausible Framing: ${item.plausibleReason}", fontSize = 13.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Security Risk: ${item.securityRiskExplanation}", fontSize = 13.sp, color = MaterialTheme.colorScheme.error)

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Android Defense: ${item.androidMitigation}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}
