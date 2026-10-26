package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Immutable Audit Log pattern: records an append-only, tamper-evident chronological trace
 * of every critical operation (CREATE, UPDATE, ARCHIVE, RESTORE, STATUS_CHANGE) across all entities.
 *
 * Stores both delta snapshots (oldValueJson, newValueJson), the actor identity (who),
 * timestamp, and device fingerprint.
 */
@Entity(
  tableName = "audit_logs",
  indices = [Index(value = ["entityType", "entityId"]), Index(value = ["timestamp"])]
)
data class AuditLogEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val entityType: String, // SUPPLIER, PROJECT, DA, BC, RECEPTION, INVOICE, BANK_ACCOUNT, DOCUMENT
  val entityId: String,
  val action: String, // CREATE, UPDATE, ARCHIVE, RESTORE, STATUS_CHANGE
  val who: String = "Acheteur",
  val timestamp: Long = System.currentTimeMillis(),
  val oldValueJson: String? = null,
  val newValueJson: String? = null,
  val description: String = "",
  val device: String = "Android Device"
)
