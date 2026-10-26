package com.example.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.SupplierEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Suppliers.
 * STRICT POLICY: NO HARD DELETES. Only soft-archiving with reason.
 */
@Dao
interface SupplierDao {

  @Query("SELECT * FROM suppliers WHERE isArchived = 0 ORDER BY raisonSociale ASC")
  fun getActiveSuppliers(): Flow<List<SupplierEntity>>

  @Query("SELECT * FROM suppliers ORDER BY raisonSociale ASC")
  fun getAllSuppliersIncludingArchived(): Flow<List<SupplierEntity>>

  @Query("SELECT * FROM suppliers")
  suspend fun getAllSuppliersDirect(): List<SupplierEntity>

  @Query("SELECT * FROM suppliers WHERE nif = :nif LIMIT 1")
  suspend fun getSupplierByNifDirect(nif: String): SupplierEntity?

  @Query("SELECT * FROM suppliers WHERE id = :id LIMIT 1")
  fun getSupplierById(id: String): Flow<SupplierEntity?>

  @Query("SELECT * FROM suppliers WHERE id = :id LIMIT 1")
  suspend fun getSupplierByIdDirect(id: String): SupplierEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(supplier: SupplierEntity)

  @Update
  suspend fun update(supplier: SupplierEntity)

  @Query("UPDATE suppliers SET isArchived = 1, archiveReason = :reason, updatedAt = :timestamp, updatedBy = :user WHERE id = :id")
  suspend fun softArchive(id: String, reason: String, user: String, timestamp: Long)

  @Query("UPDATE suppliers SET isArchived = 0, archiveReason = NULL, updatedAt = :timestamp, updatedBy = :user WHERE id = :id")
  suspend fun restoreArchived(id: String, user: String, timestamp: Long)

  @Query("UPDATE suppliers SET status = :newStatus, statusReason = :reason, updatedAt = :timestamp, updatedBy = :user WHERE id = :id")
  suspend fun updateStatus(id: String, newStatus: String, reason: String, user: String, timestamp: Long)

  @Query("""
    UPDATE suppliers SET 
      qualityScore = :quality, 
      priceScore = :price, 
      deliveryScore = :delivery, 
      afterSalesScore = :afterSales, 
      reliabilityScore = :reliability, 
      overallRating = :overall,
      updatedAt = :timestamp,
      updatedBy = :user
    WHERE id = :id
  """)
  suspend fun updateScores(
    id: String,
    quality: Double,
    price: Double,
    delivery: Double,
    afterSales: Double,
    reliability: Double,
    overall: Double,
    user: String,
    timestamp: Long
  )

  @Query("SELECT COUNT(*) FROM suppliers WHERE isArchived = 0")
  fun getActiveSupplierCount(): Flow<Int>
}
