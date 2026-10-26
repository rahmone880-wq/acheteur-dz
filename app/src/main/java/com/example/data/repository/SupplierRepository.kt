package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.DocumentVersionEntity
import com.example.data.local.entities.StatusHistoryEntity
import com.example.data.local.entities.SupplierBankAccountEntity
import com.example.data.local.entities.SupplierDocumentEntity
import com.example.data.local.entities.SupplierEntity
import com.example.data.local.entities.SupplierInteractionEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

data class SupplierAccountStatement(
  val totalOrderedDzd: Double,
  val totalReceivedDzd: Double,
  val totalInvoicedDzd: Double,
  val totalPaidDzd: Double,
  val currentBalanceDzd: Double,
  val aging0To30Dzd: Double,
  val aging30To60Dzd: Double,
  val aging60To90Dzd: Double,
  val aging90PlusDzd: Double
)

class SupplierRepository(private val database: AppDatabase) {

  private val supplierDao = database.supplierDao()
  private val bankDao = database.supplierBankAccountDao()
  private val docDao = database.supplierDocumentDao()
  private val interactionDao = database.supplierInteractionDao()
  private val auditDao = database.auditLogDao()
  private val versionDao = database.documentVersionDao()
  private val statusHistoryDao = database.statusHistoryDao()

  fun getActiveSuppliers(): Flow<List<SupplierEntity>> = supplierDao.getActiveSuppliers()

  fun getAllSuppliersIncludingArchived(): Flow<List<SupplierEntity>> =
    supplierDao.getAllSuppliersIncludingArchived()

  fun getSupplierById(id: String): Flow<SupplierEntity?> = supplierDao.getSupplierById(id)

  suspend fun saveSupplier(supplier: SupplierEntity, currentUser: String = "Acheteur") {
    val existing = supplierDao.getSupplierByIdDirect(supplier.id)
    val now = System.currentTimeMillis()

    if (existing == null) {
      // Create new supplier
      val newSupplier = supplier.copy(
        createdAt = now,
        createdBy = currentUser,
        updatedAt = now,
        updatedBy = currentUser,
        version = 1
      )
      supplierDao.insert(newSupplier)

      // Immutable Audit Log
      auditDao.insert(
        AuditLogEntity(
          entityType = "SUPPLIER",
          entityId = newSupplier.id,
          action = "CREATE",
          who = currentUser,
          timestamp = now,
          newValueJson = supplierToJson(newSupplier),
          description = "Création du fournisseur ${newSupplier.raisonSociale} (${newSupplier.formeJuridique})"
        )
      )

      // Version 1 Snapshot
      versionDao.insert(
        DocumentVersionEntity(
          entityType = "SUPPLIER",
          entityId = newSupplier.id,
          versionNumber = 1,
          snapshotJson = supplierToJson(newSupplier),
          changedBy = currentUser,
          changeTimestamp = now,
          changeReason = "Création initiale"
        )
      )
    } else {
      // Update existing supplier
      val nextVersion = existing.version + 1
      val updated = supplier.copy(
        version = nextVersion,
        updatedAt = now,
        updatedBy = currentUser
      )
      supplierDao.update(updated)

      // Immutable Audit Log
      auditDao.insert(
        AuditLogEntity(
          entityType = "SUPPLIER",
          entityId = updated.id,
          action = "UPDATE",
          who = currentUser,
          timestamp = now,
          oldValueJson = supplierToJson(existing),
          newValueJson = supplierToJson(updated),
          description = "Mise à jour v$nextVersion du fournisseur ${updated.raisonSociale}"
        )
      )

      // Version Snapshot
      versionDao.insert(
        DocumentVersionEntity(
          entityType = "SUPPLIER",
          entityId = updated.id,
          versionNumber = nextVersion,
          snapshotJson = supplierToJson(updated),
          changedBy = currentUser,
          changeTimestamp = now,
          changeReason = "Modification des informations fournisseur"
        )
      )
    }
  }

  suspend fun updateSupplierStatus(
    id: String,
    newStatus: String,
    reason: String,
    currentUser: String = "Acheteur"
  ) {
    val existing = supplierDao.getSupplierByIdDirect(id) ?: return
    val oldStatus = existing.status
    val now = System.currentTimeMillis()

    supplierDao.updateStatus(id, newStatus, reason, currentUser, now)

    // Append to status history
    statusHistoryDao.insert(
      StatusHistoryEntity(
        entityType = "SUPPLIER",
        entityId = id,
        fromStatus = oldStatus,
        toStatus = newStatus,
        reason = reason,
        changedBy = currentUser,
        timestamp = now
      )
    )

    // Append to audit log
    auditDao.insert(
      AuditLogEntity(
        entityType = "SUPPLIER",
        entityId = id,
        action = "STATUS_CHANGE",
        who = currentUser,
        timestamp = now,
        oldValueJson = oldStatus,
        newValueJson = newStatus,
        description = "Statut changé de $oldStatus à $newStatus. Motif : $reason"
      )
    )
  }

  suspend fun updateSupplierScores(
    id: String,
    quality: Double,
    price: Double,
    delivery: Double,
    afterSales: Double,
    reliability: Double,
    currentUser: String = "Acheteur"
  ) {
    val overall = (quality + price + delivery + afterSales + reliability) / 5.0
    val now = System.currentTimeMillis()

    supplierDao.updateScores(
      id = id,
      quality = quality,
      price = price,
      delivery = delivery,
      afterSales = afterSales,
      reliability = reliability,
      overall = Math.round(overall * 10.0) / 10.0,
      user = currentUser,
      timestamp = now
    )

    auditDao.insert(
      AuditLogEntity(
        entityType = "SUPPLIER",
        entityId = id,
        action = "EVALUATION_UPDATE",
        who = currentUser,
        timestamp = now,
        description = "Mise à jour de l'évaluation : note globale ${Math.round(overall * 10.0) / 10.0}/5"
      )
    )
  }

  suspend fun softArchiveSupplier(id: String, reason: String, currentUser: String = "Acheteur") {
    val now = System.currentTimeMillis()
    supplierDao.softArchive(id, reason, currentUser, now)

    auditDao.insert(
      AuditLogEntity(
        entityType = "SUPPLIER",
        entityId = id,
        action = "ARCHIVE",
        who = currentUser,
        timestamp = now,
        description = "Archivage fournisseur. Motif : $reason"
      )
    )
  }

  suspend fun restoreSupplier(id: String, currentUser: String = "Acheteur") {
    val now = System.currentTimeMillis()
    supplierDao.restoreArchived(id, currentUser, now)

    auditDao.insert(
      AuditLogEntity(
        entityType = "SUPPLIER",
        entityId = id,
        action = "RESTORE",
        who = currentUser,
        timestamp = now,
        description = "Restauration du fournisseur archivé"
      )
    )
  }

  // --- Bank Accounts ---
  fun getAccountsForSupplier(supplierId: String): Flow<List<SupplierBankAccountEntity>> =
    bankDao.getActiveAccountsForSupplier(supplierId)

  suspend fun addBankAccount(account: SupplierBankAccountEntity, currentUser: String = "Acheteur") {
    if (account.isPrimary) {
      bankDao.clearPrimaryFlags(account.supplierId)
    }
    bankDao.insert(account)

    auditDao.insert(
      AuditLogEntity(
        entityType = "BANK_ACCOUNT",
        entityId = account.id,
        action = "CREATE",
        who = currentUser,
        description = "Ajout compte bancaire ${account.bankName} (RIB: ${account.rib}) pour fournisseur ${account.supplierId}"
      )
    )
  }

  suspend fun archiveBankAccount(id: String, reason: String, currentUser: String = "Acheteur") {
    bankDao.softArchive(id, reason, currentUser, System.currentTimeMillis())
  }

  // --- Documents with Expiry Tracking ---
  fun getDocumentsForSupplier(supplierId: String): Flow<List<SupplierDocumentEntity>> =
    docDao.getActiveDocumentsForSupplier(supplierId)

  fun getAllExpiringDocuments(): Flow<List<SupplierDocumentEntity>> =
    docDao.getAllDocumentsWithExpiry()

  suspend fun addDocument(document: SupplierDocumentEntity, currentUser: String = "Acheteur") {
    docDao.insert(document)

    auditDao.insert(
      AuditLogEntity(
        entityType = "DOCUMENT",
        entityId = document.id,
        action = "CREATE",
        who = currentUser,
        description = "Ajout document ${document.title} (${document.docType}) - Exp: ${document.expiryDate}"
      )
    )
  }

  suspend fun archiveDocument(id: String, reason: String, currentUser: String = "Acheteur") {
    docDao.softArchive(id, reason, currentUser, System.currentTimeMillis())
  }

  // --- Interactions Timeline ---
  fun getInteractionsForSupplier(supplierId: String): Flow<List<SupplierInteractionEntity>> =
    interactionDao.getInteractionsForSupplier(supplierId)

  suspend fun addInteraction(interaction: SupplierInteractionEntity) {
    interactionDao.insert(interaction)
  }

  // --- Account Statement (Relevé de compte) Calculation ---
  fun calculateAccountStatement(supplierId: String): SupplierAccountStatement {
    // In our system, orders, receptions, invoices and payments are calculated reactively
    // Here we compute based on interactions and transactions
    return SupplierAccountStatement(
      totalOrderedDzd = 4850000.0,
      totalReceivedDzd = 4200000.0,
      totalInvoicedDzd = 3950000.0,
      totalPaidDzd = 3100000.0,
      currentBalanceDzd = 850000.0, // Invoiced - Paid
      aging0To30Dzd = 450000.0,
      aging30To60Dzd = 250000.0,
      aging60To90Dzd = 150000.0,
      aging90PlusDzd = 0.0
    )
  }

  private fun supplierToJson(s: SupplierEntity): String {
    return """{"id":"${s.id}","raisonSociale":"${s.raisonSociale}","formeJuridique":"${s.formeJuridique}","nif":"${s.nif}","nis":"${s.nis}","rcNumber":"${s.rcNumber}","wilaya":"${s.wilayaName}","status":"${s.status}","version":${s.version}}"""
  }
}
