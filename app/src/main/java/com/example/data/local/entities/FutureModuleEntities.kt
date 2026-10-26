package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "projects")
data class ProjectEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val code: String, // e.g. PRJ-2026-ALG01
  val name: String,
  val client: String = "",
  val locationWilaya: String = "16",
  val locationCommune: String = "",
  val costCenter: String = "",
  val manager: String = "",
  val status: String = "ACTIF", // ACTIF, CLOTURE, SUSPENDU
  val totalBudgetDzd: Double = 0.0,
  val committedBudgetDzd: Double = 0.0,
  val invoicedBudgetDzd: Double = 0.0,
  val paidBudgetDzd: Double = 0.0,
  val createdAt: Long = System.currentTimeMillis(),
  val createdBy: String = "Acheteur",
  val updatedAt: Long = System.currentTimeMillis(),
  val updatedBy: String = "Acheteur",
  val version: Int = 1,
  val isArchived: Boolean = false,
  val archiveReason: String? = null
)

@Entity(tableName = "products")
data class ProductEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val reference: String,
  val designationFr: String,
  val designationAr: String = "",
  val category: String = "",
  val unit: String = "U", // U, Kg, Mètre, Litre, Forfait
  val tvaRate: Double = 19.0, // 19%, 9%, 0%
  val defaultSupplierId: String? = null,
  val defaultSupplierName: String = "",
  val lastPriceDzd: Double = 0.0,
  val minPriceDzd: Double = 0.0,
  val maxPriceDzd: Double = 0.0,
  val avgPriceDzd: Double = 0.0,
  val isStockable: Boolean = false, // Stock flag only used when Warehouse module is ON
  val createdAt: Long = System.currentTimeMillis(),
  val createdBy: String = "Acheteur",
  val updatedAt: Long = System.currentTimeMillis(),
  val updatedBy: String = "Acheteur",
  val version: Int = 1,
  val isArchived: Boolean = false,
  val archiveReason: String? = null
)

@Entity(tableName = "purchase_requests")
data class PurchaseRequestEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val daNumber: String, // e.g. DA-2026-0001
  val requesterName: String,
  val department: String = "Technique / Chantier",
  val projectId: String, // Target Project / Site
  val projectName: String = "",
  val priority: String = "NORMALE", // FAIBLE, NORMALE, URGENTE
  val requiredDate: String = "",
  val status: String = "VALIDEE", // BROUILLON, SOUMISE, VALIDEE, REJETEE, COMMANDEE, CLOTUREE
  val justification: String = "",
  val totalEstimatedDzd: Double = 0.0,
  val createdAt: Long = System.currentTimeMillis(),
  val createdBy: String = "Acheteur",
  val updatedAt: Long = System.currentTimeMillis(),
  val updatedBy: String = "Acheteur",
  val version: Int = 1,
  val isArchived: Boolean = false,
  val archiveReason: String? = null
)

@Entity(tableName = "purchase_orders")
data class PurchaseOrderEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val bcNumber: String, // e.g. BC-2026-0001
  val purchaseRequestId: String? = null,
  val daNumber: String = "", // Linked DA number (e.g. DA-2026-0001)
  val supplierId: String,
  val supplierName: String = "",
  val projectId: String, // Target site
  val projectName: String = "",
  val status: String = "CONFIRME", // BROUILLON, ENVOYE, CONFIRME, PARTIELLEMENT_RECU, RECU, CLOTURE, ANNULE
  val orderDate: String,
  val expectedDeliveryDate: String = "",
  val deliverySiteAddress: String = "",
  val totalHtDzd: Double = 0.0,
  val totalTvaDzd: Double = 0.0,
  val timbreFiscalDzd: Double = 0.0,
  val totalTtcDzd: Double = 0.0,
  val paymentTerms: String = "30 jours",
  val createdAt: Long = System.currentTimeMillis(),
  val createdBy: String = "Acheteur",
  val updatedAt: Long = System.currentTimeMillis(),
  val updatedBy: String = "Acheteur",
  val version: Int = 1,
  val isArchived: Boolean = false,
  val archiveReason: String? = null
)

@Entity(tableName = "purchase_order_lines")
data class PurchaseOrderLineEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val purchaseOrderId: String,
  val productId: String? = null,
  val productReference: String = "",
  val designation: String,
  val unit: String = "U",
  val quantity: Double,
  val unitPriceHt: Double,
  val discountPercent: Double = 0.0,
  val tvaRate: Double = 19.0,
  val totalHt: Double,
  val totalTtc: Double,
  val receivedQuantity: Double = 0.0
)

@Entity(tableName = "receptions")
data class ReceptionEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val blNumber: String, // Bon de Livraison supplier number
  val brNumber: String, // Internal Bon de Réception number BR-2026-0001
  val purchaseOrderId: String,
  val bcNumber: String = "",
  val supplierId: String,
  val supplierName: String = "",
  val destinationType: String = "PROJET", // PROJET (default) or MAGASIN (when module is ON)
  val destinationId: String, // Project ID or Warehouse ID
  val destinationName: String = "",
  val receptionDate: String,
  val receivedBy: String = "Acheteur",
  val status: String = "CONFORME", // CONFORME, AVEC_RESERVES, REJETE
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val createdBy: String = "Acheteur",
  val updatedAt: Long = System.currentTimeMillis(),
  val updatedBy: String = "Acheteur",
  val version: Int = 1,
  val isArchived: Boolean = false,
  val archiveReason: String? = null
)

@Entity(tableName = "reception_lines")
data class ReceptionLineEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val receptionId: String,
  val purchaseOrderLineId: String? = null,
  val productId: String? = null,
  val designation: String,
  val orderedQuantity: Double,
  val receivedQuantity: Double,
  val rejectedQuantity: Double = 0.0,
  val rejectionReason: String = ""
)

@Entity(tableName = "supplier_invoices")
data class SupplierInvoiceEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val invoiceNumber: String,
  val invoiceDate: String,
  val dueDate: String,
  val supplierId: String,
  val supplierName: String = "",
  val projectId: String = "",
  val projectName: String = "",
  val purchaseOrderId: String? = null,
  val bcNumber: String = "",
  val totalHtDzd: Double = 0.0,
  val totalTvaDzd: Double = 0.0,
  val totalTtcDzd: Double = 0.0,
  val paidAmountDzd: Double = 0.0,
  val paymentStatus: String = "NON_PAYEE", // NON_PAYEE, PARTIELLE, PAYEE
  val matchingStatus: String = "CONFORME", // 3-way matching: CONFORME, ECART_PRIX, ECART_QTE
  val createdAt: Long = System.currentTimeMillis(),
  val createdBy: String = "Acheteur",
  val updatedAt: Long = System.currentTimeMillis(),
  val updatedBy: String = "Acheteur",
  val version: Int = 1,
  val isArchived: Boolean = false,
  val archiveReason: String? = null
)

@Entity(tableName = "payments")
data class PaymentEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val invoiceId: String,
  val supplierId: String,
  val projectId: String,
  val amountDzd: Double,
  val paymentMethod: String = "VIREMENT", // VIREMENT, CHEQUE, ESPECES, TRAITE
  val referenceNumber: String = "",
  val paymentDate: String,
  val bankAccount: String = "BNA",
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val createdBy: String = "Acheteur"
)

@Entity(tableName = "warehouse_stocks")
data class WarehouseStockEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val warehouseName: String = "Magasin Principal",
  val productId: String,
  val currentQuantity: Double = 0.0,
  val minThreshold: Double = 5.0,
  val cumpDzd: Double = 0.0, // Coût Unitaire Moyen Pondéré
  val createdAt: Long = System.currentTimeMillis(),
  val createdBy: String = "Acheteur",
  val updatedAt: Long = System.currentTimeMillis(),
  val updatedBy: String = "Acheteur",
  val version: Int = 1,
  val isArchived: Boolean = false,
  val archiveReason: String? = null
)
