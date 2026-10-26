package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class SettingsEntity(
  @PrimaryKey val id: String = "default_settings",
  // Feature Flag: Warehouse / Magasin module (DISABLED by default)
  val isWarehouseModuleEnabled: Boolean = false,
  // Feature Flag: Contracts module
  val isContractsModuleEnabled: Boolean = true,
  // Company Profile (Algerian standard)
  val companyName: String = "Startup Algérie SPA",
  val companyAddress: String = "Centre d'Affaires El Qods, Chéraga, Alger",
  val companyWilaya: String = "16 - Alger",
  val companyPhone: String = "+213 23 12 34 56",
  val companyEmail: String = "achats@startupdz.com",
  val companyRc: String = "16/00-1234567B22",
  val companyNif: String = "002216001234567",
  val companyNis: String = "002216001234567",
  val companyAi: String = "16012345678",
  val companyRib: String = "001 00123 0123456789 45",
  val companyBank: String = "BNA - Agence Chéraga",
  // User info / Profil Acheteur & Compte Google
  val currentUserName: String = "Abdelkrim Benali",
  val currentUserRole: String = "Responsable des Achats & Approvisionnements",
  val currentUserEmail: String = "abdobigboss30@gmail.com",
  val currentUserPhone: String = "+213 555 12 34 56",
  val isGoogleLinked: Boolean = true,
  val googleDisplayName: String = "Abdelkrim Acheteur DZ",
  val gmailSignature: String = "Envoyé depuis Acheteur DZ - Gestion des Approvisionnements BTP",
  val lastSyncTimestamp: Long = 0L,
  val defaultTvaPercent: Double = 19.0
)
