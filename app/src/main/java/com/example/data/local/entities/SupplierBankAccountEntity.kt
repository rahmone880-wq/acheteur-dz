package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Supplier bank account holding Algerian banking credentials (RIB 20 chiffres, CCP, Banque, Agence).
 */
@Entity(
  tableName = "supplier_bank_accounts",
  foreignKeys = [
    ForeignKey(
      entity = SupplierEntity::class,
      parentColumns = ["id"],
      childColumns = ["supplierId"],
      onDelete = ForeignKey.RESTRICT
    )
  ],
  indices = [
    Index(value = ["supplierId"]),
    Index(value = ["rib"])
  ]
)
data class SupplierBankAccountEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val supplierId: String,
  val bankName: String,
  val agency: String = "",
  val rib: String, // 20 digits in Algeria
  val ccp: String = "",
  val iban: String = "",
  val holderName: String,
  val isPrimary: Boolean = false,

  // Versioning and Soft delete metadata
  val createdAt: Long = System.currentTimeMillis(),
  val createdBy: String = "Acheteur",
  val updatedAt: Long = System.currentTimeMillis(),
  val updatedBy: String = "Acheteur",
  val version: Int = 1,
  val isArchived: Boolean = false,
  val archiveReason: String? = null
)
