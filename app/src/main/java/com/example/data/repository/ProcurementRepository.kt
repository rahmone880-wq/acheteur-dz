package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.PurchaseOrderEntity
import com.example.data.local.entities.PurchaseOrderLineEntity
import com.example.data.local.entities.PurchaseRequestEntity
import com.example.data.local.entities.ReceptionEntity
import com.example.data.local.entities.SupplierInvoiceEntity
import kotlinx.coroutines.flow.Flow

import com.example.core.audit.AuditSnapshotHelper

class ProcurementRepository(private val database: AppDatabase) {

  private val procurementDao = database.procurementDao()
  private val projectDao = database.projectDao()
  private val auditDao = database.auditLogDao()

  // --- Purchase Requests (DA) ---
  fun getActivePurchaseRequests(): Flow<List<PurchaseRequestEntity>> =
    procurementDao.getActivePurchaseRequests()

  suspend fun savePurchaseRequest(da: PurchaseRequestEntity, currentUser: String = "Acheteur") {
    val existing = procurementDao.getPurchaseRequestByIdDirect(da.id)
    val now = System.currentTimeMillis()

    if (existing == null) {
      val toInsert = da.copy(
        createdAt = now,
        createdBy = currentUser,
        updatedAt = now,
        updatedBy = currentUser,
        version = 1
      )
      procurementDao.insertPurchaseRequest(toInsert)

      auditDao.insert(
        AuditLogEntity(
          entityType = "DA",
          entityId = toInsert.id,
          action = "CREATE",
          who = currentUser,
          timestamp = now,
          oldValueJson = null,
          newValueJson = AuditSnapshotHelper.purchaseRequestToJson(toInsert),
          description = "Création DA ${toInsert.daNumber} (${toInsert.projectName}) - Estimé: ${toInsert.totalEstimatedDzd} DA"
        )
      )
    } else {
      val nextVersion = existing.version + 1
      val updated = da.copy(
        version = nextVersion,
        updatedAt = now,
        updatedBy = currentUser
      )
      procurementDao.insertPurchaseRequest(updated)

      val diff = AuditSnapshotHelper.diffPurchaseRequests(existing, updated)
      auditDao.insert(
        AuditLogEntity(
          entityType = "DA",
          entityId = updated.id,
          action = "UPDATE",
          who = currentUser,
          timestamp = now,
          oldValueJson = AuditSnapshotHelper.purchaseRequestToJson(existing),
          newValueJson = AuditSnapshotHelper.purchaseRequestToJson(updated),
          description = "Modification v$nextVersion DA ${updated.daNumber} : $diff"
        )
      )
    }
  }

  // --- Purchase Orders (BC) ---
  fun getActivePurchaseOrders(): Flow<List<PurchaseOrderEntity>> =
    procurementDao.getActivePurchaseOrders()

  fun getLinesForOrder(orderId: String): Flow<List<PurchaseOrderLineEntity>> =
    procurementDao.getLinesForOrder(orderId)

  suspend fun savePurchaseOrder(
    bc: PurchaseOrderEntity,
    lines: List<PurchaseOrderLineEntity> = emptyList(),
    currentUser: String = "Acheteur"
  ) {
    val existing = procurementDao.getPurchaseOrderByIdDirect(bc.id)
    val now = System.currentTimeMillis()

    if (existing == null) {
      val toInsert = bc.copy(
        createdAt = now,
        createdBy = currentUser,
        updatedAt = now,
        updatedBy = currentUser,
        version = 1
      )
      procurementDao.insertPurchaseOrder(toInsert)
      if (lines.isNotEmpty()) {
        procurementDao.insertOrderLines(lines)
      }

      // Automatically update target project's committed budget!
      val project = projectDao.getProjectByIdDirect(toInsert.projectId)
      if (project != null) {
        val newCommitted = project.committedBudgetDzd + toInsert.totalTtcDzd
        projectDao.updateBudgetConsumptions(
          id = project.id,
          committed = newCommitted,
          invoiced = project.invoicedBudgetDzd,
          paid = project.paidBudgetDzd,
          timestamp = now
        )
      }

      // Automatically update linked DA status to COMMANDEE if present
      if (!toInsert.purchaseRequestId.isNullOrBlank()) {
        procurementDao.updatePurchaseRequestStatus(toInsert.purchaseRequestId, "COMMANDEE", now)
      }

      val daDescription = if (!toInsert.daNumber.isNullOrBlank()) " [Lié à DA : ${toInsert.daNumber}]" else ""
      auditDao.insert(
        AuditLogEntity(
          entityType = "BC",
          entityId = toInsert.id,
          action = "CREATE",
          who = currentUser,
          timestamp = now,
          oldValueJson = null,
          newValueJson = AuditSnapshotHelper.purchaseOrderToJson(toInsert),
          description = "Bon de Commande ${toInsert.bcNumber} émis au fournisseur ${toInsert.supplierName} - Total TTC: ${toInsert.totalTtcDzd} DA$daDescription"
        )
      )
    } else {
      val nextVersion = existing.version + 1
      val updated = bc.copy(
        version = nextVersion,
        updatedAt = now,
        updatedBy = currentUser
      )
      procurementDao.insertPurchaseOrder(updated)
      if (lines.isNotEmpty()) {
        procurementDao.insertOrderLines(lines)
      }

      // Adjust project committed budget difference
      val project = projectDao.getProjectByIdDirect(updated.projectId)
      if (project != null) {
        val budgetDiff = updated.totalTtcDzd - existing.totalTtcDzd
        val newCommitted = (project.committedBudgetDzd + budgetDiff).coerceAtLeast(0.0)
        projectDao.updateBudgetConsumptions(
          id = project.id,
          committed = newCommitted,
          invoiced = project.invoicedBudgetDzd,
          paid = project.paidBudgetDzd,
          timestamp = now
        )
      }

      val diff = AuditSnapshotHelper.diffPurchaseOrders(existing, updated)
      auditDao.insert(
        AuditLogEntity(
          entityType = "BC",
          entityId = updated.id,
          action = "UPDATE",
          who = currentUser,
          timestamp = now,
          oldValueJson = AuditSnapshotHelper.purchaseOrderToJson(existing),
          newValueJson = AuditSnapshotHelper.purchaseOrderToJson(updated),
          description = "Modification v$nextVersion BC ${updated.bcNumber} : $diff"
        )
      )
    }
  }

  // --- Receptions (Réception chantier / BL) ---
  fun getActiveReceptions(): Flow<List<ReceptionEntity>> =
    procurementDao.getActiveReceptions()

  suspend fun saveReception(reception: ReceptionEntity, currentUser: String = "Acheteur") {
    val existing = procurementDao.getReceptionByIdDirect(reception.id)
    val now = System.currentTimeMillis()

    if (existing == null) {
      val toInsert = reception.copy(
        createdAt = now,
        createdBy = currentUser,
        updatedAt = now,
        updatedBy = currentUser,
        version = 1
      )
      procurementDao.insertReception(toInsert)

      // Update BC status
      if (toInsert.purchaseOrderId.isNotBlank()) {
        procurementDao.updatePurchaseOrderStatus(toInsert.purchaseOrderId, "RECU", now)
      }

      auditDao.insert(
        AuditLogEntity(
          entityType = "RECEPTION",
          entityId = toInsert.id,
          action = "RECEIVE",
          who = currentUser,
          timestamp = now,
          oldValueJson = null,
          newValueJson = AuditSnapshotHelper.receptionToJson(toInsert),
          description = "Réception chantier ${toInsert.brNumber} (BL: ${toInsert.blNumber}) enregistrée pour ${toInsert.destinationName}"
        )
      )
    } else {
      val nextVersion = existing.version + 1
      val updated = reception.copy(
        version = nextVersion,
        updatedAt = now,
        updatedBy = currentUser
      )
      procurementDao.insertReception(updated)

      val diff = AuditSnapshotHelper.diffReceptions(existing, updated)
      auditDao.insert(
        AuditLogEntity(
          entityType = "RECEPTION",
          entityId = updated.id,
          action = "UPDATE",
          who = currentUser,
          timestamp = now,
          oldValueJson = AuditSnapshotHelper.receptionToJson(existing),
          newValueJson = AuditSnapshotHelper.receptionToJson(updated),
          description = "Modification v$nextVersion Réception ${updated.brNumber} : $diff"
        )
      )
    }
  }

  // --- Invoices & 3-Way Matching ---
  fun getActiveInvoices(): Flow<List<SupplierInvoiceEntity>> =
    procurementDao.getActiveInvoices()

  suspend fun saveInvoice(invoice: SupplierInvoiceEntity, currentUser: String = "Acheteur") {
    val existing = procurementDao.getInvoiceByIdDirect(invoice.id)
    val now = System.currentTimeMillis()

    if (existing == null) {
      val toInsert = invoice.copy(
        createdAt = now,
        createdBy = currentUser,
        updatedAt = now,
        updatedBy = currentUser,
        version = 1
      )
      procurementDao.insertInvoice(toInsert)

      // Update project invoiced budget
      val project = projectDao.getProjectByIdDirect(toInsert.projectId)
      if (project != null) {
        val newInvoiced = project.invoicedBudgetDzd + toInsert.totalTtcDzd
        projectDao.updateBudgetConsumptions(
          id = project.id,
          committed = project.committedBudgetDzd,
          invoiced = newInvoiced,
          paid = project.paidBudgetDzd,
          timestamp = now
        )
      }

      auditDao.insert(
        AuditLogEntity(
          entityType = "INVOICE",
          entityId = toInsert.id,
          action = "CREATE",
          who = currentUser,
          timestamp = now,
          oldValueJson = null,
          newValueJson = AuditSnapshotHelper.invoiceToJson(toInsert),
          description = "Facture ${toInsert.invoiceNumber} enregistrée (${toInsert.supplierName}) - Total TTC: ${toInsert.totalTtcDzd} DA"
        )
      )
    } else {
      val nextVersion = existing.version + 1
      val updated = invoice.copy(
        version = nextVersion,
        updatedAt = now,
        updatedBy = currentUser
      )
      procurementDao.insertInvoice(updated)

      // Adjust project invoiced budget difference
      val project = projectDao.getProjectByIdDirect(updated.projectId)
      if (project != null) {
        val budgetDiff = updated.totalTtcDzd - existing.totalTtcDzd
        val newInvoiced = (project.invoicedBudgetDzd + budgetDiff).coerceAtLeast(0.0)
        projectDao.updateBudgetConsumptions(
          id = project.id,
          committed = project.committedBudgetDzd,
          invoiced = newInvoiced,
          paid = project.paidBudgetDzd,
          timestamp = now
        )
      }

      val diff = AuditSnapshotHelper.diffInvoices(existing, updated)
      auditDao.insert(
        AuditLogEntity(
          entityType = "INVOICE",
          entityId = updated.id,
          action = "UPDATE",
          who = currentUser,
          timestamp = now,
          oldValueJson = AuditSnapshotHelper.invoiceToJson(existing),
          newValueJson = AuditSnapshotHelper.invoiceToJson(updated),
          description = "Modification v$nextVersion Facture ${updated.invoiceNumber} : $diff"
        )
      )
    }
  }
}
