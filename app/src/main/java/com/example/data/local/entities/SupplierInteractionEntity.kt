package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
  tableName = "supplier_interactions",
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
data class SupplierInteractionEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val supplierId: String,
  // Interaction type: APPEL, REUNION, EMAIL, NOTE, RECLAMATION, RFQ, DEVIS, COMMANDE, LIVRAISON, FACTURE, PAIEMENT
  val type: String,
  val subject: String,
  val details: String = "",
  val outcome: String = "",
  val amountDzd: Double? = null,
  val timestamp: Long = System.currentTimeMillis(),
  val createdBy: String = "Acheteur"
)
