package com.example.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.SupplierInteractionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Supplier Interactions Timeline.
 * Append-only interaction log.
 */
@Dao
interface SupplierInteractionDao {

  @Query("SELECT * FROM supplier_interactions WHERE supplierId = :supplierId ORDER BY timestamp DESC")
  fun getInteractionsForSupplier(supplierId: String): Flow<List<SupplierInteractionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(interaction: SupplierInteractionEntity)

  @Query("SELECT COUNT(*) FROM supplier_interactions WHERE supplierId = :supplierId")
  suspend fun getInteractionCountForSupplier(supplierId: String): Int
}
