package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Foundational Supplier entity incorporating mandatory Algerian fiscal fields,
 * performance scoring, commercial parameters, soft-deletion, and entity versioning.
 */
@Entity(
  tableName = "suppliers",
  indices = [
    Index(value = ["nif"]),
    Index(value = ["rcNumber"]),
    Index(value = ["status"]),
    Index(value = ["isArchived"])
  ]
)
data class SupplierEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val raisonSociale: String,
  val formeJuridique: String = "SARL",
  val activite: String = "",
  val adresse: String = "",
  val wilayaCode: String = "16",
  val wilayaName: String = "Alger",
  val commune: String = "",
  val phone: String = "",
  val email: String = "",
  val website: String = "",
  val contactPerson: String = "",

  // Fiscal & Legal (Algerian standard)
  val rcNumber: String = "",
  val rcDate: String = "",
  val nif: String = "",
  val nis: String = "",
  val articleImposition: String = "",
  val carteArtisanNin: String = "",
  val agrement: String = "",

  // Status: EN_COURS, AGREE, SUSPENDU, BLACKLISTE
  val status: String = "AGREE",
  val statusReason: String = "",

  // Scoring (1 to 5)
  val qualityScore: Double = 4.0,
  val priceScore: Double = 4.0,
  val deliveryScore: Double = 4.0,
  val afterSalesScore: Double = 4.0,
  val reliabilityScore: Double = 4.0,
  val overallRating: Double = 4.0,

  // Commercial terms
  val paymentTermsDays: Int = 30, // 30 jours, 60 jours, comptant
  val leadTimeDays: Int = 7,
  val deliveryZones: String = "Toutes wilayas",
  val categoriesTags: String = "Fournitures & Consommables",

  // Custom Fields (JSON)
  val customFieldsJson: String = "{}",

  // Immutability, Soft delete, and Versioning metadata
  val createdAt: Long = System.currentTimeMillis(),
  val createdBy: String = "Acheteur",
  val updatedAt: Long = System.currentTimeMillis(),
  val updatedBy: String = "Acheteur",
  val version: Int = 1,
  val isArchived: Boolean = false,
  val archiveReason: String? = null
)
