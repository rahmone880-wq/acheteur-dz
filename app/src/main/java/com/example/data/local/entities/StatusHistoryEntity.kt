package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
  tableName = "status_history",
  indices = [Index(value = ["entityType", "entityId"])]
)
data class StatusHistoryEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val entityType: String,
  val entityId: String,
  val fromStatus: String,
  val toStatus: String,
  val reason: String = "",
  val changedBy: String = "Acheteur",
  val timestamp: Long = System.currentTimeMillis()
)
