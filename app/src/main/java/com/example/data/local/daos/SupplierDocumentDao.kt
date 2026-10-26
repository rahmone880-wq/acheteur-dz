package com.example.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.SupplierDocumentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Supplier Documents.
 * STRICT POLICY: NO HARD DELETES. Only soft-archiving.
 */
@Dao
interface SupplierDocumentDao {

  @Query("SELECT * FROM supplier_documents WHERE supplierId = :supplierId AND isArchived = 0 ORDER BY expiryDate ASC, title ASC")
  fun getActiveDocumentsForSupplier(supplierId: String): Flow<List<SupplierDocumentEntity>>

  @Query("SELECT * FROM supplier_documents")
  suspend fun getAllDocumentsDirect(): List<SupplierDocumentEntity>

  @Query("SELECT * FROM supplier_documents WHERE id = :id LIMIT 1")
  suspend fun getDocumentById(id: String): SupplierDocumentEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(document: SupplierDocumentEntity)

  @Update
  suspend fun update(document: SupplierDocumentEntity)

  @Query("UPDATE supplier_documents SET isArchived = 1, archiveReason = :reason, updatedAt = :timestamp, updatedBy = :user WHERE id = :id")
  suspend fun softArchive(id: String, reason: String, user: String, timestamp: Long)

  @Query("SELECT * FROM supplier_documents WHERE isArchived = 0 AND expiryDate != '' ORDER BY expiryDate ASC")
  fun getAllDocumentsWithExpiry(): Flow<List<SupplierDocumentEntity>>
}
