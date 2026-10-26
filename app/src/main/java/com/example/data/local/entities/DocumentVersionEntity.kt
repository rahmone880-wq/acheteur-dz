package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Entity Versioning pattern: maintains complete point-in-time state snapshots for every revision
 * of business documents and master records (version 1, 2, 3...).
 *
 * Allows full auditability, historical rollback, and discrepancy resolution by comparing snapshots.
 */
@Entity(
  tableName = "document_versions",
  indices = [Index(value = ["entityType", "entityId", "versionNumber"])]
)
data class DocumentVersionEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val entityType: String,
  val entityId: String,
  val versionNumber: Int,
  val snapshotJson: String,
  val changedBy: String = "Acheteur",
  val changeTimestamp: Long = System.currentTimeMillis(),
  val changeReason: String = ""
)
