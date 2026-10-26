package com.example.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

  @Query("SELECT * FROM projects WHERE isArchived = 0 ORDER BY name ASC")
  fun getActiveProjects(): Flow<List<ProjectEntity>>

  @Query("SELECT * FROM projects")
  suspend fun getAllProjectsDirect(): List<ProjectEntity>

  @Query("SELECT * FROM projects WHERE code = :code LIMIT 1")
  suspend fun getProjectByCodeDirect(code: String): ProjectEntity?

  @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
  fun getProjectById(id: String): Flow<ProjectEntity?>

  @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
  suspend fun getProjectByIdDirect(id: String): ProjectEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(project: ProjectEntity)

  @Update
  suspend fun update(project: ProjectEntity)

  @Query("UPDATE projects SET isArchived = 1, archiveReason = :reason, updatedAt = :timestamp, updatedBy = :user WHERE id = :id")
  suspend fun softArchive(id: String, reason: String, user: String, timestamp: Long)

  @Query("""
    UPDATE projects SET
      committedBudgetDzd = :committed,
      invoicedBudgetDzd = :invoiced,
      paidBudgetDzd = :paid,
      updatedAt = :timestamp
    WHERE id = :id
  """)
  suspend fun updateBudgetConsumptions(id: String, committed: Double, invoiced: Double, paid: Double, timestamp: Long)
}
