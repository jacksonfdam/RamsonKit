package com.demo.ransomkit.common.model

import java.io.File

/**
 * Data class representing permission explanation cards in educational UI flows.
 */
data class PermissionExplanation(
    val title: String,
    val permissionName: String,
    val plausibleReason: String,
    val securityRiskExplanation: String,
    val androidMitigation: String
)

/**
 * Audit log entry for tracking defense events.
 */
data class AuditLogEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String,
    val details: String,
    val isProtected: Boolean
)

/**
 * File integrity model for sandboxed target files.
 */
data class SandboxedFileItem(
    val name: String,
    val path: String,
    val sizeBytes: Long,
    val isIntact: Boolean,
    val lastModified: Long
)
