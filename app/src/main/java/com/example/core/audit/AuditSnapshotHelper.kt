package com.example.core.audit

import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.PurchaseOrderEntity
import com.example.data.local.entities.PurchaseRequestEntity
import com.example.data.local.entities.ReceptionEntity
import com.example.data.local.entities.SupplierEntity
import com.example.data.local.entities.SupplierInvoiceEntity
import org.json.JSONObject

/**
 * Utility helper to convert entities to structured JSON snapshots (oldValueJson / newValueJson)
 * and generate human-readable delta diffs between the original (origine) and modified (modifié) states.
 */
object AuditSnapshotHelper {

  fun invoiceToJson(inv: SupplierInvoiceEntity): String {
    return JSONObject().apply {
      put("id", inv.id)
      put("invoiceNumber", inv.invoiceNumber)
      put("invoiceDate", inv.invoiceDate)
      put("dueDate", inv.dueDate)
      put("supplierId", inv.supplierId)
      put("supplierName", inv.supplierName)
      put("projectId", inv.projectId)
      put("projectName", inv.projectName)
      put("purchaseOrderId", inv.purchaseOrderId ?: "")
      put("bcNumber", inv.bcNumber)
      put("totalHtDzd", inv.totalHtDzd)
      put("totalTvaDzd", inv.totalTvaDzd)
      put("totalTtcDzd", inv.totalTtcDzd)
      put("paidAmountDzd", inv.paidAmountDzd)
      put("paymentStatus", inv.paymentStatus)
      put("matchingStatus", inv.matchingStatus)
      put("version", inv.version)
    }.toString(2)
  }

  fun purchaseOrderToJson(bc: PurchaseOrderEntity): String {
    return JSONObject().apply {
      put("id", bc.id)
      put("bcNumber", bc.bcNumber)
      put("supplierId", bc.supplierId)
      put("supplierName", bc.supplierName)
      put("projectId", bc.projectId)
      put("projectName", bc.projectName)
      put("orderDate", bc.orderDate)
      put("expectedDeliveryDate", bc.expectedDeliveryDate)
      put("deliverySiteAddress", bc.deliverySiteAddress)
      put("totalHtDzd", bc.totalHtDzd)
      put("totalTvaDzd", bc.totalTvaDzd)
      put("timbreFiscalDzd", bc.timbreFiscalDzd)
      put("totalTtcDzd", bc.totalTtcDzd)
      put("paymentTerms", bc.paymentTerms)
      put("status", bc.status)
      put("daNumber", bc.daNumber)
      put("version", bc.version)
    }.toString(2)
  }

  fun purchaseRequestToJson(da: PurchaseRequestEntity): String {
    return JSONObject().apply {
      put("id", da.id)
      put("daNumber", da.daNumber)
      put("projectId", da.projectId)
      put("projectName", da.projectName)
      put("requesterName", da.requesterName)
      put("department", da.department)
      put("requiredDate", da.requiredDate)
      put("priority", da.priority)
      put("status", da.status)
      put("justification", da.justification)
      put("totalEstimatedDzd", da.totalEstimatedDzd)
      put("version", da.version)
    }.toString(2)
  }

  fun receptionToJson(rec: ReceptionEntity): String {
    return JSONObject().apply {
      put("id", rec.id)
      put("brNumber", rec.brNumber)
      put("blNumber", rec.blNumber)
      put("supplierName", rec.supplierName)
      put("destinationName", rec.destinationName)
      put("receptionDate", rec.receptionDate)
      put("receivedBy", rec.receivedBy)
      put("status", rec.status)
      put("notes", rec.notes)
      put("version", rec.version)
    }.toString(2)
  }

  fun projectToJson(proj: ProjectEntity): String {
    return JSONObject().apply {
      put("id", proj.id)
      put("code", proj.code)
      put("name", proj.name)
      put("locationWilaya", proj.locationWilaya)
      put("totalBudgetDzd", proj.totalBudgetDzd)
      put("committedBudgetDzd", proj.committedBudgetDzd)
      put("invoicedBudgetDzd", proj.invoicedBudgetDzd)
      put("paidBudgetDzd", proj.paidBudgetDzd)
      put("status", proj.status)
      put("version", proj.version)
    }.toString(2)
  }

  fun supplierToJson(s: SupplierEntity): String {
    return JSONObject().apply {
      put("id", s.id)
      put("raisonSociale", s.raisonSociale)
      put("formeJuridique", s.formeJuridique)
      put("phone", s.phone)
      put("email", s.email)
      put("adresse", s.adresse)
      put("wilayaName", s.wilayaName)
      put("nif", s.nif)
      put("nis", s.nis)
      put("rcNumber", s.rcNumber)
      put("status", s.status)
      put("paymentTermsDays", s.paymentTermsDays)
      put("version", s.version)
    }.toString(2)
  }

  fun diffInvoices(old: SupplierInvoiceEntity, new: SupplierInvoiceEntity): String {
    val diffs = mutableListOf<String>()
    if (old.invoiceNumber != new.invoiceNumber) diffs.add("N° Facture : ${old.invoiceNumber} → ${new.invoiceNumber}")
    if (old.totalTtcDzd != new.totalTtcDzd) diffs.add("Total TTC : ${old.totalTtcDzd} DA → ${new.totalTtcDzd} DA")
    if (old.totalHtDzd != new.totalHtDzd) diffs.add("Total HT : ${old.totalHtDzd} DA → ${new.totalHtDzd} DA")
    if (old.paymentStatus != new.paymentStatus) diffs.add("Paiement : ${old.paymentStatus} → ${new.paymentStatus}")
    if (old.matchingStatus != new.matchingStatus) diffs.add("Rapprochement : ${old.matchingStatus} → ${new.matchingStatus}")
    if (old.dueDate != new.dueDate) diffs.add("Échéance : ${old.dueDate} → ${new.dueDate}")
    if (old.paidAmountDzd != new.paidAmountDzd) diffs.add("Montant réglé : ${old.paidAmountDzd} DA → ${new.paidAmountDzd} DA")
    return if (diffs.isEmpty()) "Mise à jour des informations générales" else diffs.joinToString("; ")
  }

  fun diffPurchaseOrders(old: PurchaseOrderEntity, new: PurchaseOrderEntity): String {
    val diffs = mutableListOf<String>()
    if (old.status != new.status) diffs.add("Statut : ${old.status} → ${new.status}")
    if (old.paymentTerms != new.paymentTerms) diffs.add("Modalités : ${old.paymentTerms} → ${new.paymentTerms}")
    if (old.expectedDeliveryDate != new.expectedDeliveryDate) diffs.add("Livraison prévue : ${old.expectedDeliveryDate} → ${new.expectedDeliveryDate}")
    if (old.totalTtcDzd != new.totalTtcDzd) diffs.add("Total TTC : ${old.totalTtcDzd} DA → ${new.totalTtcDzd} DA")
    if (old.deliverySiteAddress != new.deliverySiteAddress) diffs.add("Adresse livraison mise à jour")
    return if (diffs.isEmpty()) "Mise à jour du bon de commande" else diffs.joinToString("; ")
  }

  fun diffPurchaseRequests(old: PurchaseRequestEntity, new: PurchaseRequestEntity): String {
    val diffs = mutableListOf<String>()
    if (old.status != new.status) diffs.add("Statut : ${old.status} → ${new.status}")
    if (old.priority != new.priority) diffs.add("Priorité : ${old.priority} → ${new.priority}")
    if (old.totalEstimatedDzd != new.totalEstimatedDzd) diffs.add("Montant estimé : ${old.totalEstimatedDzd} DA → ${new.totalEstimatedDzd} DA")
    if (old.requesterName != new.requesterName) diffs.add("Demandeur : ${old.requesterName} → ${new.requesterName}")
    if (old.justification != new.justification) diffs.add("Justification modifiée")
    return if (diffs.isEmpty()) "Mise à jour de la demande d'achat" else diffs.joinToString("; ")
  }

  fun diffReceptions(old: ReceptionEntity, new: ReceptionEntity): String {
    val diffs = mutableListOf<String>()
    if (old.blNumber != new.blNumber) diffs.add("BL N° : ${old.blNumber} → ${new.blNumber}")
    if (old.status != new.status) diffs.add("Statut : ${old.status} → ${new.status}")
    if (old.notes != new.notes) diffs.add("Notes révisées")
    return if (diffs.isEmpty()) "Mise à jour de la réception" else diffs.joinToString("; ")
  }

  /**
   * Parses JSON string into a list of key-value pairs for display.
   */
  fun parseJsonToMap(jsonStr: String?): Map<String, String> {
    if (jsonStr.isNullOrBlank()) return emptyMap()
    return try {
      val obj = JSONObject(jsonStr)
      val map = mutableMapOf<String, String>()
      val keys = obj.keys()
      while (keys.hasNext()) {
        val key = keys.next()
        val value = obj.opt(key)?.toString() ?: ""
        if (value.isNotBlank() && value != "null") {
          map[key] = value
        }
      }
      map
    } catch (_: Exception) {
      emptyMap()
    }
  }
}
