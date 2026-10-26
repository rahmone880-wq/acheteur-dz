package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val database: AppDatabase) {

  private val productDao = database.productDao()
  private val auditDao = database.auditLogDao()

  fun getActiveProducts(): Flow<List<ProductEntity>> = productDao.getActiveProducts()

  fun getProductById(id: String): Flow<ProductEntity?> = productDao.getProductById(id)

  fun getProductsBySupplier(supplierId: String): Flow<List<ProductEntity>> =
    productDao.getProductsBySupplier(supplierId)

  suspend fun saveProduct(product: ProductEntity, currentUser: String = "Acheteur") {
    val now = System.currentTimeMillis()
    productDao.insert(
      product.copy(
        updatedAt = now,
        updatedBy = currentUser
      )
    )

    auditDao.insert(
      AuditLogEntity(
        entityType = "PRODUCT",
        entityId = product.id,
        action = "SAVE",
        who = currentUser,
        timestamp = now,
        description = "Article ${product.designationFr} (${product.reference}) enregistré. Fournisseur: ${product.defaultSupplierName}"
      )
    )
  }

  suspend fun softArchiveProduct(id: String, reason: String, currentUser: String = "Acheteur") {
    productDao.softArchive(id, reason, currentUser, System.currentTimeMillis())
  }
}
