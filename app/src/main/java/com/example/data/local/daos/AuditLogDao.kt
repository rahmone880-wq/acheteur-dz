package com.example.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.AuditLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Immutable Audit Logs.
 * APPEND-ONLY: Inserts only, absolutely no updates or deletes permitted.
 */
@Dao
interface AuditLogDao {

  @Insert(onConflict = OnConflictStrategy.ABORT)
  suspend fun insert(log: AuditLogEntity)

  @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
  fun getAllLogs(): Flow<List<AuditLogEntity>>

  @Query("SELECT * FROM audit_logs WHERE entityType = :entityType AND entityId = :entityId ORDER BY timestamp DESC")
  fun getLogsForEntity(entityType: String, entityId: String): Flow<List<AuditLogEntity>>

  @Query("SELECT * FROM audit_logs WHERE entityType = :entityType ORDER BY timestamp DESC")
  fun getLogsByEntityType(entityType: String): Flow<List<AuditLogEntity>>
}
