package com.example.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.DocumentVersionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Document Version Revisions.
 * APPEND-ONLY: Stores immutable revision snapshots.
 */
@Dao
interface DocumentVersionDao {

  @Insert(onConflict = OnConflictStrategy.ABORT)
  suspend fun insert(version: DocumentVersionEntity)

  @Query("SELECT * FROM document_versions WHERE entityType = :entityType AND entityId = :entityId ORDER BY versionNumber DESC")
  fun getVersionsForEntity(entityType: String, entityId: String): Flow<List<DocumentVersionEntity>>

  @Query("SELECT * FROM document_versions WHERE entityType = :entityType AND entityId = :entityId AND versionNumber = :versionNumber LIMIT 1")
  suspend fun getSpecificVersion(entityType: String, entityId: String, versionNumber: Int): DocumentVersionEntity?
}
