package com.example.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.StatusHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Status Transitions.
 * APPEND-ONLY: Logs all status changes with mandatory reason.
 */
@Dao
interface StatusHistoryDao {

  @Insert(onConflict = OnConflictStrategy.ABORT)
  suspend fun insert(history: StatusHistoryEntity)

  @Query("SELECT * FROM status_history WHERE entityType = :entityType AND entityId = :entityId ORDER BY timestamp DESC")
  fun getHistoryForEntity(entityType: String, entityId: String): Flow<List<StatusHistoryEntity>>
}
