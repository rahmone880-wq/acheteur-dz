package com.example.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

  @Query("SELECT * FROM products WHERE isArchived = 0 ORDER BY designationFr ASC")
  fun getActiveProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products")
  suspend fun getAllProductsDirect(): List<ProductEntity>

  @Query("SELECT * FROM products WHERE reference = :reference LIMIT 1")
  suspend fun getProductByReferenceDirect(reference: String): ProductEntity?

  @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
  fun getProductById(id: String): Flow<ProductEntity?>

  @Query("SELECT * FROM products WHERE defaultSupplierId = :supplierId AND isArchived = 0")
  fun getProductsBySupplier(supplierId: String): Flow<List<ProductEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(product: ProductEntity)

  @Update
  suspend fun update(product: ProductEntity)

  @Query("UPDATE products SET isArchived = 1, archiveReason = :reason, updatedAt = :timestamp, updatedBy = :user WHERE id = :id")
  suspend fun softArchive(id: String, reason: String, user: String, timestamp: Long)
}
