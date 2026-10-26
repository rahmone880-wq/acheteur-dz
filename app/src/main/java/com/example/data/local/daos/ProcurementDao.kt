package com.example.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.PaymentEntity
import com.example.data.local.entities.PurchaseOrderEntity
import com.example.data.local.entities.PurchaseOrderLineEntity
import com.example.data.local.entities.PurchaseRequestEntity
import com.example.data.local.entities.ReceptionEntity
import com.example.data.local.entities.ReceptionLineEntity
import com.example.data.local.entities.SupplierInvoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProcurementDao {

  // --- Purchase Requests (DA) ---
  @Query("SELECT * FROM purchase_requests WHERE isArchived = 0 ORDER BY createdAt DESC")
  fun getActivePurchaseRequests(): Flow<List<PurchaseRequestEntity>>

  @Query("SELECT * FROM purchase_requests WHERE projectId = :projectId AND isArchived = 0 ORDER BY createdAt DESC")
  fun getPurchaseRequestsForProject(projectId: String): Flow<List<PurchaseRequestEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPurchaseRequest(da: PurchaseRequestEntity)

  @Query("UPDATE purchase_requests SET status = :status, updatedAt = :timestamp WHERE id = :id")
  suspend fun updatePurchaseRequestStatus(id: String, status: String, timestamp: Long)

  @Query("SELECT * FROM purchase_orders WHERE purchaseRequestId = :daId LIMIT 1")
  fun getPurchaseOrderForRequest(daId: String): Flow<PurchaseOrderEntity?>

  // --- Purchase Orders (BC) ---
  @Query("SELECT * FROM purchase_orders WHERE isArchived = 0 ORDER BY orderDate DESC")
  fun getActivePurchaseOrders(): Flow<List<PurchaseOrderEntity>>

  @Query("SELECT * FROM purchase_orders WHERE projectId = :projectId AND isArchived = 0 ORDER BY orderDate DESC")
  fun getPurchaseOrdersForProject(projectId: String): Flow<List<PurchaseOrderEntity>>

  @Query("SELECT * FROM purchase_orders WHERE supplierId = :supplierId AND isArchived = 0 ORDER BY orderDate DESC")
  fun getPurchaseOrdersForSupplier(supplierId: String): Flow<List<PurchaseOrderEntity>>

  @Query("SELECT * FROM purchase_orders WHERE id = :id LIMIT 1")
  fun getPurchaseOrderById(id: String): Flow<PurchaseOrderEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPurchaseOrder(bc: PurchaseOrderEntity)

  @Query("UPDATE purchase_orders SET status = :status, updatedAt = :timestamp WHERE id = :id")
  suspend fun updatePurchaseOrderStatus(id: String, status: String, timestamp: Long)

  // --- Purchase Order Lines (Articles commandés) ---
  @Query("SELECT * FROM purchase_order_lines WHERE purchaseOrderId = :orderId")
  fun getLinesForOrder(orderId: String): Flow<List<PurchaseOrderLineEntity>>

  @Query("""
    SELECT pol.* FROM purchase_order_lines pol
    INNER JOIN purchase_orders po ON pol.purchaseOrderId = po.id
    WHERE po.projectId = :projectId AND po.isArchived = 0
  """)
  fun getOrderLinesForProject(projectId: String): Flow<List<PurchaseOrderLineEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrderLines(lines: List<PurchaseOrderLineEntity>)

  // --- Receptions (Réception chantier / BL) ---
  @Query("SELECT * FROM receptions WHERE isArchived = 0 ORDER BY receptionDate DESC")
  fun getActiveReceptions(): Flow<List<ReceptionEntity>>

  @Query("SELECT * FROM receptions WHERE destinationId = :projectId AND isArchived = 0 ORDER BY receptionDate DESC")
  fun getReceptionsForProject(projectId: String): Flow<List<ReceptionEntity>>

  @Query("SELECT * FROM receptions WHERE supplierId = :supplierId AND isArchived = 0 ORDER BY receptionDate DESC")
  fun getReceptionsForSupplier(supplierId: String): Flow<List<ReceptionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReception(reception: ReceptionEntity)

  // --- Invoices & 3-Way Matching ---
  @Query("SELECT * FROM supplier_invoices WHERE isArchived = 0 ORDER BY invoiceDate DESC")
  fun getActiveInvoices(): Flow<List<SupplierInvoiceEntity>>

  @Query("SELECT * FROM supplier_invoices WHERE projectId = :projectId AND isArchived = 0 ORDER BY invoiceDate DESC")
  fun getInvoicesForProject(projectId: String): Flow<List<SupplierInvoiceEntity>>

  @Query("SELECT * FROM supplier_invoices WHERE supplierId = :supplierId AND isArchived = 0 ORDER BY invoiceDate DESC")
  fun getInvoicesForSupplier(supplierId: String): Flow<List<SupplierInvoiceEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertInvoice(invoice: SupplierInvoiceEntity)

  // --- Payments ---
  @Query("SELECT * FROM payments WHERE invoiceId = :invoiceId ORDER BY paymentDate DESC")
  fun getPaymentsForInvoice(invoiceId: String): Flow<List<PaymentEntity>>

  @Query("SELECT * FROM payments WHERE projectId = :projectId ORDER BY paymentDate DESC")
  fun getPaymentsForProject(projectId: String): Flow<List<PaymentEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPayment(payment: PaymentEntity)

  // Direct batch queries for data transfer & merge
  @Query("SELECT * FROM purchase_requests")
  suspend fun getAllPurchaseRequestsDirect(): List<PurchaseRequestEntity>

  @Query("SELECT * FROM purchase_orders")
  suspend fun getAllPurchaseOrdersDirect(): List<PurchaseOrderEntity>

  @Query("SELECT * FROM purchase_order_lines")
  suspend fun getAllPurchaseOrderLinesDirect(): List<PurchaseOrderLineEntity>

  @Query("SELECT * FROM receptions")
  suspend fun getAllReceptionsDirect(): List<ReceptionEntity>

  @Query("SELECT * FROM supplier_invoices")
  suspend fun getAllInvoicesDirect(): List<SupplierInvoiceEntity>

  @Query("SELECT * FROM purchase_requests WHERE daNumber = :daNumber LIMIT 1")
  suspend fun getPurchaseRequestByNumberDirect(daNumber: String): PurchaseRequestEntity?

  @Query("SELECT * FROM purchase_orders WHERE bcNumber = :bcNumber LIMIT 1")
  suspend fun getPurchaseOrderByNumberDirect(bcNumber: String): PurchaseOrderEntity?

  @Query("SELECT * FROM purchase_requests WHERE id = :id LIMIT 1")
  suspend fun getPurchaseRequestByIdDirect(id: String): PurchaseRequestEntity?

  @Query("SELECT * FROM purchase_orders WHERE id = :id LIMIT 1")
  suspend fun getPurchaseOrderByIdDirect(id: String): PurchaseOrderEntity?

  @Query("SELECT * FROM receptions WHERE id = :id LIMIT 1")
  suspend fun getReceptionByIdDirect(id: String): ReceptionEntity?

  @Query("SELECT * FROM supplier_invoices WHERE id = :id LIMIT 1")
  suspend fun getInvoiceByIdDirect(id: String): SupplierInvoiceEntity?
}
