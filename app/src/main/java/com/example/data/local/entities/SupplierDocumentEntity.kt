package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
  tableName = "supplier_documents",
  foreignKeys = [
    ForeignKey(
      entity = SupplierEntity::class,
      parentColumns = ["id"],
      childColumns = ["supplierId"],
      onDelete = ForeignKey.RESTRICT
    )
  ],
  indices = [Index(value = ["supplierId"])]
)
data class SupplierDocumentEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val supplierId: String,
  val docType: String, // RC, NIF_NIS, REGULARITE_FISCALE, CASNOS_CNAS, RIB, CONTRACT, CATALOG, AUTRE
  val title: String,
  val referenceNumber: String = "",
  val issueDate: String = "",
  val expiryDate: String = "", // Used for expiry alert tracking
  val fileUri: String = "",
  val notes: String = "",

  // Versioning and Soft delete metadata
  val createdAt: Long = System.currentTimeMillis(),
  val createdBy: String = "Acheteur",
  val updatedAt: Long = System.currentTimeMillis(),
  val updatedBy: String = "Acheteur",
  val version: Int = 1,
  val isArchived: Boolean = false,
  val archiveReason: String? = null
)
