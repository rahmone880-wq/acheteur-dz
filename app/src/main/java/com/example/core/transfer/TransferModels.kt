package com.example.core.transfer

import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.PurchaseOrderEntity
import com.example.data.local.entities.PurchaseOrderLineEntity
import com.example.data.local.entities.PurchaseRequestEntity
import com.example.data.local.entities.ReceptionEntity
import com.example.data.local.entities.SupplierBankAccountEntity
import com.example.data.local.entities.SupplierDocumentEntity
import com.example.data.local.entities.SupplierEntity
import com.example.data.local.entities.SupplierInvoiceEntity

/**
 * Scope of data to transfer between devices.
 */
enum class TransferScope(val labelFr: String, val labelAr: String) {
  FULL("Base Complète (Recommandé)", "كامل البيانات (موصى به)"),
  SUPPLIERS("Fournisseurs & Coordonnées", "الموردين وجهات الاتصال"),
  PROJECTS("Chantiers & Budgets", "المشاريع والميزانيات"),
  PROCUREMENT("Achats (DA, BC, Réceptions)", "المشتريات وسندات الطلب"),
  PRODUCTS("Catalogue Articles & Prix", "دليل المواد والأسعار")
}

/**
 * Data transfer container package designed for peer-to-peer export, share, and import.
 */
data class TransferPackage(
  val appName: String = "Acheteur DZ",
  val formatVersion: Int = 1,
  val timestamp: Long = System.currentTimeMillis(),
  val senderDevice: String = "Téléphone Acheteur DZ",
  val scope: String = TransferScope.FULL.name,
  val suppliers: List<SupplierEntity> = emptyList(),
  val bankAccounts: List<SupplierBankAccountEntity> = emptyList(),
  val documents: List<SupplierDocumentEntity> = emptyList(),
  val projects: List<ProjectEntity> = emptyList(),
  val products: List<ProductEntity> = emptyList(),
  val purchaseRequests: List<PurchaseRequestEntity> = emptyList(),
  val purchaseOrders: List<PurchaseOrderEntity> = emptyList(),
  val purchaseOrderLines: List<PurchaseOrderLineEntity> = emptyList(),
  val receptions: List<ReceptionEntity> = emptyList(),
  val invoices: List<SupplierInvoiceEntity> = emptyList()
) {
  val totalItemsCount: Int
    get() = suppliers.size + projects.size + products.size +
        purchaseRequests.size + purchaseOrders.size + receptions.size + invoices.size
}

/**
 * Report generated after executing a smart merge into the receiver's local database.
 */
data class MergeReport(
  val success: Boolean,
  val suppliersAdded: Int = 0,
  val suppliersUpdated: Int = 0,
  val projectsAdded: Int = 0,
  val projectsUpdated: Int = 0,
  val productsAdded: Int = 0,
  val productsUpdated: Int = 0,
  val daAdded: Int = 0,
  val daUpdated: Int = 0,
  val bcAdded: Int = 0,
  val bcUpdated: Int = 0,
  val receptionsAdded: Int = 0,
  val invoicesAdded: Int = 0,
  val message: String = ""
) {
  val totalMerged: Int
    get() = suppliersAdded + suppliersUpdated + projectsAdded + projectsUpdated +
        productsAdded + productsUpdated + daAdded + daUpdated +
        bcAdded + bcUpdated + receptionsAdded + invoicesAdded
}
